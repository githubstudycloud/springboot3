// ==================== 修改后的 MockClassLoader.java ====================
package com.example.staticmock;

import javassist.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 增强的自定义类加载器，兼容Java 21
 */
public class MockClassLoader extends ClassLoader {
    private final ClassPool classPool;
    private final Map<String, Class<?>> loadedClasses = new ConcurrentHashMap<>();
    private final Map<String, byte[]> modifiedBytecode = new ConcurrentHashMap<>();
    
    /**
     * 创建一个MockClassLoader
     * @param parent 父级类加载器
     */
    public MockClassLoader(ClassLoader parent) {
        super(parent);
        classPool = new ClassPool(true);
        classPool.appendClassPath(new LoaderClassPath(parent));
        try {
            // 导入必要的包
            classPool.importPackage("com.example.staticmock");
        } catch (Exception e) {
            throw new RuntimeException("初始化MockClassLoader失败", e);
        }
    }
    
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // 首先检查已加载的类
        Class<?> clazz = loadedClasses.get(name);
        if (clazz != null) {
            if (resolve) {
                resolveClass(clazz);
            }
            return clazz;
        }
        
        // 对于系统类和框架类，委托给父加载器
        if (shouldDelegateToParent(name)) {
            try {
                clazz = super.loadClass(name, resolve);
                loadedClasses.put(name, clazz);
                return clazz;
            } catch (ClassNotFoundException e) {
                // 继续尝试自己加载
            }
        }
        
        try {
            // 查找之前已经修改过的字节码
            byte[] bytecode = modifiedBytecode.get(name);
            
            if (bytecode == null) {
                // 还没有修改过，需要使用Javassist进行转换
                bytecode = transformClass(name);
                
                if (bytecode != null) {
                    modifiedBytecode.put(name, bytecode);
                }
            }
            
            if (bytecode != null) {
                // 定义类
                clazz = defineClass(name, bytecode, 0, bytecode.length, 
                                   getClass().getProtectionDomain());
                
                if (resolve) {
                    resolveClass(clazz);
                }
                
                loadedClasses.put(name, clazz);
                return clazz;
            }
        } catch (Exception e) {
            System.err.println("警告: 无法转换类 " + name + ": " + e.getMessage());
            // 出现异常时打印调试信息但不抛出，继续尝试父加载器
        }
        
        // 最后尝试父加载器
        return super.loadClass(name, resolve);
    }
    
    /**
     * 判断是否应该委托给父加载器
     */
    private boolean shouldDelegateToParent(String name) {
        // 系统类和框架类委托给父加载器
        return name.startsWith("java.") || 
               name.startsWith("javax.") || 
               name.startsWith("sun.") || 
               name.startsWith("com.sun.") ||
               name.startsWith("jdk.") ||
               name.startsWith("org.junit") ||
               name.startsWith("com.example.staticmock");
    }
    
    /**
     * 转换类的字节码
     */
    private byte[] transformClass(String className) {
        try {
            // 使用Javassist获取并修改类
            CtClass ctClass = classPool.get(className);
            
            // 如果类已被冻结，解冻它
            if (ctClass.isFrozen()) {
                ctClass.defrost();
            }
            
            // 检查是否有静态方法需要处理
            boolean modified = false;
            for (CtMethod method : ctClass.getDeclaredMethods()) {
                if (Modifier.isStatic(method.getModifiers()) && 
                    !Modifier.isNative(method.getModifiers()) && 
                    !Modifier.isAbstract(method.getModifiers())) {
                    // 处理静态方法
                    transformMethod(ctClass, method);
                    modified = true;
                }
            }
            
            // 只有实际修改了类才返回字节码
            if (modified) {
                byte[] bytecode = ctClass.toBytecode();
                ctClass.detach(); // 减少内存使用
                return bytecode;
            } else {
                ctClass.detach(); // 减少内存使用
                return null; // 表示没有修改
            }
        } catch (NotFoundException e) {
            // 类未找到，让父加载器处理
            return null;
        } catch (Exception e) {
            System.err.println("转换类 " + className + " 时发生错误: " + e);
            e.printStackTrace();
            return null; // 出错时返回null，让父加载器处理
        }
    }
    
    /**
     * 转换单个方法
     */
    private void transformMethod(CtClass ctClass, CtMethod method) throws Exception {
        // 提取方法信息，构建签名
        String methodName = method.getName();
        CtClass[] paramTypes = method.getParameterTypes();
        CtClass returnType = method.getReturnType();
        
        // 构建方法签名
        StringBuilder signatureBuilder = new StringBuilder();
        signatureBuilder.append(ctClass.getName()).append("#").append(methodName).append("(");
        for (int i = 0; i < paramTypes.length; i++) {
            if (i > 0) signatureBuilder.append(",");
            signatureBuilder.append(paramTypes[i].getName());
        }
        signatureBuilder.append(")");
        String methodSignature = signatureBuilder.toString();
        
        // 检查是否已经处理过（有$original后缀）
        try {
            ctClass.getDeclaredMethod(methodName + "$original", paramTypes);
            return; // 已经处理过，跳过
        } catch (NotFoundException e) {
            // 未处理过，继续
        }
        
        // 复制原始方法
        String originalMethodName = methodName + "$original";
        CtMethod originalMethod = CtNewMethod.copy(method, originalMethodName, ctClass, null);
        
        // 修改备份方法的修饰符为私有
        originalMethod.setModifiers(originalMethod.getModifiers() & ~Modifier.PUBLIC | Modifier.PRIVATE);
        ctClass.addMethod(originalMethod);
        
        // 重写原始方法的实现
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("{\n");
        
        // 构建方法签名（避免硬编码，在运行时计算）
        bodyBuilder.append("    String methodSignature = \"").append(methodSignature).append("\";\n");
        
        // 构建参数数组
        bodyBuilder.append("    Object[] args = new Object[").append(paramTypes.length).append("];\n");
        for (int i = 0; i < paramTypes.length; i++) {
            bodyBuilder.append("    args[").append(i).append("] = ");
            
            if (paramTypes[i].isPrimitive()) {
                // 包装基本类型
                if (paramTypes[i] == CtClass.intType) {
                    bodyBuilder.append("Integer.valueOf($").append(i + 1).append(")");
                } else if (paramTypes[i] == CtClass.longType) {
                    bodyBuilder.append("Long.valueOf($").append(i + 1).append(")");
                } else if (paramTypes[i] == CtClass.booleanType) {
                    bodyBuilder.append("Boolean.valueOf($").append(i + 1).append(")");
                } else if (paramTypes[i] == CtClass.byteType) {
                    bodyBuilder.append("Byte.valueOf($").append(i + 1).append(")");
                } else if (paramTypes[i] == CtClass.charType) {
                    bodyBuilder.append("Character.valueOf($").append(i + 1).append(")");
                } else if (paramTypes[i] == CtClass.doubleType) {
                    bodyBuilder.append("Double.valueOf($").append(i + 1).append(")");
                } else if (paramTypes[i] == CtClass.floatType) {
                    bodyBuilder.append("Float.valueOf($").append(i + 1).append(")");
                } else if (paramTypes[i] == CtClass.shortType) {
                    bodyBuilder.append("Short.valueOf($").append(i + 1).append(")");
                }
            } else {
                bodyBuilder.append("$").append(i + 1);
            }
            bodyBuilder.append(";\n");
        }
        
        // 记录方法调用
        bodyBuilder.append("    com.example.staticmock.MockContext.recordCall(methodSignature, args);\n");
        
        // 增加错误处理和恢复机制
        bodyBuilder.append("    try {\n");
        bodyBuilder.append("        // 检查是否有Mock定义\n");
        bodyBuilder.append("        if (com.example.staticmock.MockContext.hasMock(methodSignature)) {\n");
        bodyBuilder.append("            Object result = com.example.staticmock.MockContext.getMockResult(methodSignature, args);\n");
        
        // 处理返回值
        if (returnType != CtClass.voidType) {
            bodyBuilder.append("            return ");
            
            // 根据返回类型进行相应转换
            if (returnType.isPrimitive()) {
                if (returnType == CtClass.booleanType) {
                    bodyBuilder.append("((Boolean)result).booleanValue()");
                } else if (returnType == CtClass.byteType) {
                    bodyBuilder.append("((Byte)result).byteValue()");
                } else if (returnType == CtClass.charType) {
                    bodyBuilder.append("((Character)result).charValue()");
                } else if (returnType == CtClass.shortType) {
                    bodyBuilder.append("((Short)result).shortValue()");
                } else if (returnType == CtClass.intType) {
                    bodyBuilder.append("((Integer)result).intValue()");
                } else if (returnType == CtClass.longType) {
                    bodyBuilder.append("((Long)result).longValue()");
                } else if (returnType == CtClass.floatType) {
                    bodyBuilder.append("((Float)result).floatValue()");
                } else if (returnType == CtClass.doubleType) {
                    bodyBuilder.append("((Double)result).doubleValue()");
                }
            } else {
                bodyBuilder.append("(").append(returnType.getName()).append(")result");
            }
            bodyBuilder.append(";\n");
        } else {
            bodyBuilder.append("            return;\n");
        }
        
        bodyBuilder.append("        }\n");
        bodyBuilder.append("    } catch (Exception e) {\n");
        bodyBuilder.append("        System.err.println(\"执行Mock时发生异常: \" + e.getMessage());\n");
        bodyBuilder.append("        e.printStackTrace();\n");
        bodyBuilder.append("        // 出错时继续执行原始方法\n");
        bodyBuilder.append("    }\n");
        
        // 调用原始方法
        if (returnType != CtClass.voidType) {
            bodyBuilder.append("    return ");
        }
        bodyBuilder.append("    ").append(originalMethodName).append("($$);\n");
        bodyBuilder.append("}");
        
        // 设置新的方法体
        method.setBody(bodyBuilder.toString());
    }
    
    /**
     * 清除所有缓存
     */
    public void clearCache() {
        modifiedBytecode.clear();
    }
}

// ==================== 修改后的 StaticMocker.java ====================
package com.example.staticmock;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 静态方法Mock的核心引擎类，使用自定义类加载器实现
 * 增强版：兼容Java 21
 */
public class StaticMocker {
    // 单例模式
    private static final StaticMocker INSTANCE = new StaticMocker();
    
    // 记录被Mock的类名
    private final Set<String> mockedClasses = new HashSet<>();
    
    // 自定义类加载器
    private final MockClassLoader mockClassLoader;
    
    private StaticMocker() {
        // 创建自定义类加载器
        mockClassLoader = new MockClassLoader(Thread.currentThread().getContextClassLoader());
    }
    
    public static StaticMocker getInstance() {
        return INSTANCE;
    }

    /**
     * 开始对特定类的静态方法进行Mock
     */
    public <T> WhenBuilder<T> when(Class<T> clazz, String methodName, Class<?>... paramTypes) {
        return new WhenBuilder<>(clazz, methodName, paramTypes);
    }
    
    /**
     * 重置所有Mock
     */
    public void resetAll() {
        MockContext.reset();
        mockedClasses.clear();
        mockClassLoader.clearCache();
    }

    /**
     * 验证静态方法被调用的次数
     */
    public void verify(Class<?> clazz, String methodName, int times, Class<?>... paramTypes) {
        String methodSignature = buildMethodSignature(clazz, methodName, paramTypes);
        int actualCalls = MockContext.getCallCount(methodSignature);
        
        if (actualCalls != times) {
            throw new AssertionError(
                String.format("方法 %s 预期被调用 %d 次，实际被调用 %d 次", 
                              methodSignature, times, actualCalls));
        }
    }
    
    /**
     * Mock流式API的构建器
     */
    public class WhenBuilder<T> {
        private final Class<T> clazz;
        private final String methodName;
        private final Class<?>[] paramTypes;
        
        private WhenBuilder(Class<T> clazz, String methodName, Class<?>[] paramTypes) {
            this.clazz = clazz;
            this.methodName = methodName;
            this.paramTypes = paramTypes;
        }
        
        /**
         * 设置返回值
         */
        public void thenReturn(Object returnValue) {
            String methodSignature = buildMethodSignature(clazz, methodName, paramTypes);
            
            // 注册Mock行为
            MockContext.registerMock(methodSignature, args -> returnValue);
            
            // 确保类被MockClassLoader加载
            try {
                ensureClassIsMocked(clazz.getName());
            } catch (Exception e) {
                throw new RuntimeException("无法创建Mock类: " + e.getMessage(), e);
            }
        }
        
        /**
         * 设置异常抛出
         */
        public void thenThrow(Throwable throwable) {
            String methodSignature = buildMethodSignature(clazz, methodName, paramTypes);
            
            // 注册抛出异常的Mock行为
            MockContext.registerMock(methodSignature, args -> {
                throw new RuntimeException(throwable);
            });
            
            // 确保类被MockClassLoader加载
            try {
                ensureClassIsMocked(clazz.getName());
            } catch (Exception e) {
                throw new RuntimeException("无法创建Mock类: " + e.getMessage(), e);
            }
        }
    }
    
    /**
     * 确保类被MockClassLoader加载
     */
    private void ensureClassIsMocked(String className) throws ClassNotFoundException {
        if (!mockedClasses.contains(className)) {
            // 标记为已Mock
            mockedClasses.add(className);
            
            // 保存当前上下文类加载器
            ClassLoader originalLoader = Thread.currentThread().getContextClassLoader();
            
            try {
                // 设置MockClassLoader为上下文类加载器
                Thread.currentThread().setContextClassLoader(mockClassLoader);
                
                // 通过MockClassLoader加载类，触发字节码转换
                Class<?> mockedClass = mockClassLoader.loadClass(className);
                
                System.out.println("已成功Mock类: " + className);
            } finally {
                // 恢复原始类加载器
                Thread.currentThread().setContextClassLoader(originalLoader);
            }
        }
    }
    
    /**
     * 构建方法签名
     */
    static String buildMethodSignature(Class<?> clazz, String methodName, Class<?>[] paramTypes) {
        StringBuilder signature = new StringBuilder();
        signature.append(clazz.getName()).append("#").append(methodName).append("(");
        
        for (int i = 0; i < paramTypes.length; i++) {
            if (i > 0) signature.append(",");
            signature.append(paramTypes[i].getName());
        }
        
        signature.append(")");
        return signature.toString();
    }
    
    /**
     * 获取MockClassLoader，用于测试
     */
    public MockClassLoader getMockClassLoader() {
        return mockClassLoader;
    }
    
    /**
     * 初始化测试环境
     * 在每个测试方法执行前调用，确保后续类加载正确
     */
    public void initTestEnvironment() {
        // 保存当前上下文类加载器
        ClassLoader originalLoader = Thread.currentThread().getContextClassLoader();
        // 设置为我们的MockClassLoader
        Thread.currentThread().setContextClassLoader(mockClassLoader);
    }
    
    /**
     * 还原测试环境
     * 测试方法执行后调用
     */
    public void restoreEnvironment(ClassLoader originalLoader) {
        // 恢复原始类加载器
        if (originalLoader != null) {
            Thread.currentThread().setContextClassLoader(originalLoader);
        }
    }
}

// ==================== 更新测试类 MathUtilsTest.java ====================
package com.example.demo;

import com.example.staticmock.StaticMocker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MathUtils测试类 - Java 21兼容版
 */
class MathUtilsTest {
    
    private final StaticMocker mocker = StaticMocker.getInstance();
    private ClassLoader originalClassLoader;
    
    @BeforeEach
    void setUp() {
        // 保存原始类加载器
        originalClassLoader = Thread.currentThread().getContextClassLoader();
        // 设置测试环境
        mocker.initTestEnvironment();
    }
    
    @AfterEach
    void tearDown() {
        // 每个测试结束后重置所有Mock
        mocker.resetAll();
        // 恢复原始环境
        mocker.restoreEnvironment(originalClassLoader);
    }
    
    // 测试方法保持不变
    @Test
    void testAddWithMock() {
        // 对静态方法add进行Mock
        mocker.when(MathUtils.class, "add", int.class, int.class)
              .thenReturn(100);
        
        // 验证Mock生效
        assertEquals(100, MathUtils.add(5, 10));
        
        // 验证方法被调用了1次
        mocker.verify(MathUtils.class, "add", 1, int.class, int.class);
    }
    
    @Test
    void testMultiplyWithMock() {
        // 对静态方法multiply进行Mock
        mocker.when(MathUtils.class, "multiply", int.class, int.class)
              .thenReturn(50);
        
        // 验证Mock生效
        assertEquals(50, MathUtils.multiply(5, 10));
        
        // 验证方法被调用了1次
        mocker.verify(MathUtils.class, "multiply", 1, int.class, int.class);
    }
    
    @Test
    void testDivideThrowsException() {
        // 对静态方法divide进行Mock，使其抛出异常
        mocker.when(MathUtils.class, "divide", int.class, int.class)
              .thenThrow(new ArithmeticException("Mock异常"));
        
        // 验证异常抛出
        assertThrows(ArithmeticException.class, () -> MathUtils.divide(10, 2));
    }
    
    @Test
    void testApplicationWithMockedMathUtils() {
        // 创建应用实例
        Application app = new Application();
        
        // Mock MathUtils.add 返回30
        mocker.when(MathUtils.class, "add", int.class, int.class)
              .thenReturn(30);
        
        // Mock MathUtils.multiply 返回100
        mocker.when(MathUtils.class, "multiply", int.class, int.class)
              .thenReturn(100);
        
        // 调用应用方法，其内部使用了被Mock的静态方法
        int result = app.calculate(10, 5);
        
        // 验证结果
        assertEquals(100, result);
        
        // 验证静态方法调用
        mocker.verify(MathUtils.class, "add", 1, int.class, int.class);
        mocker.verify(MathUtils.class, "multiply", 1, int.class, int.class);
    }
}
