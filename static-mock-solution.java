/**
 * 完整框架源代码目录结构
 *
 * javassist-static-mock/
 * ├── pom.xml
 * ├── src/
 *     ├── main/
 *     │   └── java/
 *     │       └── com/
 *     │           └── example/
 *     │               ├── staticmock/
 *     │               │   ├── StaticMocker.java        (核心Mock引擎)
 *     │               │   ├── MockClassLoader.java     (自定义类加载器 - 新增)
 *     │               │   ├── MockContext.java         (状态管理)
 *     │               │   └── MockClassFactory.java    (代理类构建器 - 替换ClassGenerator)
 *     │               └── demo/
 *     │                   ├── MathUtils.java           (被测试类)
 *     │                   └── Application.java         (使用示例)
 *     └── test/
 *         └── java/
 *             └── com/
 *                 └── example/
 *                     └── demo/
 *                         └── MathUtilsTest.java       (测试类)
 */

// ==================== 修改后的 pom.xml ====================
/**
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>javassist-static-mock</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <javassist.version>3.30.2-GA</javassist.version>
        <junit.version>5.10.0</junit.version>
        <asm.version>9.5</asm.version>
    </properties>

    <dependencies>
        <!-- Javassist -->
        <dependency>
            <groupId>org.javassist</groupId>
            <artifactId>javassist</artifactId>
            <version>${javassist.version}</version>
        </dependency>
        
        <!-- ASM - 新增 -->
        <dependency>
            <groupId>org.ow2.asm</groupId>
            <artifactId>asm</artifactId>
            <version>${asm.version}</version>
        </dependency>
        <dependency>
            <groupId>org.ow2.asm</groupId>
            <artifactId>asm-commons</artifactId>
            <version>${asm.version}</version>
        </dependency>
        
        <!-- JUnit 5 -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-engine</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.1.2</version>
            </plugin>
        </plugins>
    </build>
</project>
*/

// ==================== StaticMocker.java ====================
package com.example.staticmock;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 静态方法Mock的核心引擎类，使用自定义类加载器实现
 */
public class StaticMocker {
    // 单例模式
    private static final StaticMocker INSTANCE = new StaticMocker();
    
    // 记录被Mock的类名
    private final Set<String> mockedClasses = new HashSet<>();
    
    // 自定义类加载器
    private final MockClassLoader mockClassLoader;
    
    private StaticMocker() {
        // 创建自定义类加载器，委托给当前类加载器
        mockClassLoader = new MockClassLoader(StaticMocker.class.getClassLoader());
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
            
            // 通过MockClassLoader加载类，触发字节码转换
            Class<?> mockedClass = mockClassLoader.loadClass(className);
            
            // 确保类已被正确加载
            Thread.currentThread().setContextClassLoader(mockClassLoader);
            
            System.out.println("已成功Mock类: " + className);
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
     * 用于测试的获取MockClassLoader方法
     */
    MockClassLoader getMockClassLoader() {
        return mockClassLoader;
    }
}

// ==================== MockContext.java (保持不变) ====================
package com.example.staticmock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Mock上下文，管理Mock定义和调用记录
 */
public class MockContext {
    // Mock定义：方法签名 -> 结果处理函数
    private static final Map<String, Function<Object[], Object>> mockDefinitions = 
            new ConcurrentHashMap<>();
    
    // 调用记录：方法签名 -> 调用参数列表
    private static final Map<String, List<Object[]>> methodCalls = 
            new ConcurrentHashMap<>();
    
    /**
     * 注册Mock行为
     */
    public static void registerMock(String methodSignature, Function<Object[], Object> handler) {
        mockDefinitions.put(methodSignature, handler);
    }
    
    /**
     * 检查是否有Mock定义
     */
    public static boolean hasMock(String methodSignature) {
        return mockDefinitions.containsKey(methodSignature);
    }
    
    /**
     * 获取Mock结果
     */
    public static Object getMockResult(String methodSignature, Object... args) {
        Function<Object[], Object> handler = mockDefinitions.get(methodSignature);
        if (handler == null) {
            return null;
        }
        
        try {
            return handler.apply(args);
        } catch (RuntimeException e) {
            if (e.getCause() != null) {
                throw new RuntimeException(e.getCause());
            }
            throw e;
        }
    }
    
    /**
     * 记录方法调用
     */
    public static void recordCall(String methodSignature, Object... args) {
        methodCalls.computeIfAbsent(methodSignature, k -> new ArrayList<>())
                 .add(args);
    }
    
    /**
     * 获取调用次数
     */
    public static int getCallCount(String methodSignature) {
        List<Object[]> calls = methodCalls.get(methodSignature);
        return calls == null ? 0 : calls.size();
    }
    
    /**
     * 重置所有Mock定义和调用记录
     */
    public static void reset() {
        mockDefinitions.clear();
        methodCalls.clear();
    }
}

// ==================== MockClassLoader.java (新增文件) ====================
package com.example.staticmock;

import javassist.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义类加载器，用于拦截和修改类的加载
 */
public class MockClassLoader extends ClassLoader {
    private final ClassPool classPool;
    private final Map<String, Class<?>> loadedClasses = new HashMap<>();
    
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
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        // 检查是否已经加载
        Class<?> loadedClass = loadedClasses.get(name);
        if (loadedClass != null) {
            return loadedClass;
        }
        
        // 如果是静态Mock框架自身的类，或是Java核心类，委托给父加载器
        if (name.startsWith("com.example.staticmock") || 
            name.startsWith("java.") || 
            name.startsWith("sun.") ||
            name.startsWith("javax.") ||
            name.startsWith("org.junit")) {
            return super.loadClass(name);
        }
        
        try {
            // 使用Javassist获取类并修改
            CtClass ctClass = classPool.get(name);
            
            // 检查是否已经被冻结
            if (ctClass.isFrozen()) {
                ctClass.defrost();
            }
            
            // 修改所有静态方法，添加Mock逻辑
            transformMethods(ctClass);
            
            // 获取修改后的字节码
            byte[] classBytes = ctClass.toBytecode();
            
            // 使用自定义类加载器加载
            Class<?> clazz = defineClass(name, classBytes, 0, classBytes.length);
            loadedClasses.put(name, clazz);
            
            return clazz;
        } catch (NotFoundException | CannotCompileException | IOException e) {
            // 如果修改失败，回退到正常加载
            System.err.println("警告: 无法转换类 " + name + ": " + e.getMessage());
            return super.loadClass(name);
        }
    }
    
    /**
     * 修改类的静态方法，添加Mock拦截逻辑
     */
    private void transformMethods(CtClass ctClass) throws CannotCompileException {
        for (CtMethod method : ctClass.getDeclaredMethods()) {
            // 只处理静态方法
            if (Modifier.isStatic(method.getModifiers())) {
                // 插入Mock逻辑
                transformMethod(ctClass, method);
            }
        }
    }
    
    /**
     * 转换单个方法，添加Mock拦截逻辑
     */
    private void transformMethod(CtClass ctClass, CtMethod method) throws CannotCompileException {
        // 不修改本地方法或抽象方法
        if (Modifier.isNative(method.getModifiers()) || Modifier.isAbstract(method.getModifiers())) {
            return;
        }
        
        // 备份原始方法
        String originalMethodName = method.getName() + "$original";
        CtMethod originalMethod = null;
        
        try {
            // 复制原始方法
            originalMethod = CtNewMethod.copy(method, originalMethodName, ctClass, null);
            originalMethod.setModifiers(originalMethod.getModifiers() & ~Modifier.PUBLIC | Modifier.PRIVATE);
            ctClass.addMethod(originalMethod);
            
            // 重写原始方法，添加Mock逻辑
            transformMethodBody(method, originalMethodName);
            
        } catch (Exception e) {
            System.err.println("警告: 无法转换方法 " + method.getLongName() + ": " + e.getMessage());
        }
    }
    
    /**
     * 转换方法体，添加Mock拦截逻辑
     */
    private void transformMethodBody(CtMethod method, String originalMethodName) throws CannotCompileException {
        StringBuilder body = new StringBuilder();
        body.append("{\n");
        
        // 构建方法签名
        body.append("    String methodSignature = \"").append(method.getDeclaringClass().getName())
            .append("#").append(method.getName()).append("(");
        
        try {
            CtClass[] paramTypes = method.getParameterTypes();
            for (int i = 0; i < paramTypes.length; i++) {
                if (i > 0) body.append(",");
                body.append(paramTypes[i].getName());
            }
        } catch (NotFoundException e) {
            throw new CannotCompileException(e);
        }
        
        body.append(")\";\n");
        
        // 构建参数数组
        body.append("    Object[] args = new Object[$args.length];\n");
        body.append("    for (int i = 0; i < $args.length; i++) {\n");
        body.append("        args[i] = ($args[i] == null) ? null : $args[i];\n");
        body.append("    }\n");
        
        // 记录方法调用
        body.append("    com.example.staticmock.MockContext.recordCall(methodSignature, args);\n");
        
        // 检查是否有Mock定义
        body.append("    if (com.example.staticmock.MockContext.hasMock(methodSignature)) {\n");
        body.append("        Object result = com.example.staticmock.MockContext.getMockResult(methodSignature, args);\n");
        
        // 处理异常情况
        body.append("        if (result instanceof RuntimeException) {\n");
        body.append("            throw (RuntimeException)result;\n");
        body.append("        }\n");
        
        // 返回Mock结果
        CtClass returnType = null;
        try {
            returnType = method.getReturnType();
        } catch (NotFoundException e) {
            throw new CannotCompileException(e);
        }
        
        if (returnType != CtClass.voidType) {
            body.append("        return ");
            
            // 根据返回类型进行相应转换
            if (returnType.isPrimitive()) {
                if (returnType == CtClass.booleanType) {
                    body.append("((Boolean)result).booleanValue()");
                } else if (returnType == CtClass.byteType) {
                    body.append("((Byte)result).byteValue()");
                } else if (returnType == CtClass.charType) {
                    body.append("((Character)result).charValue()");
                } else if (returnType == CtClass.shortType) {
                    body.append("((Short)result).shortValue()");
                } else if (returnType == CtClass.intType) {
                    body.append("((Integer)result).intValue()");
                } else if (returnType == CtClass.longType) {
                    body.append("((Long)result).longValue()");
                } else if (returnType == CtClass.floatType) {
                    body.append("((Float)result).floatValue()");
                } else if (returnType == CtClass.doubleType) {
                    body.append("((Double)result).doubleValue()");
                }
            } else {
                body.append("(").append(returnType.getName()).append(")result");
            }
            body.append(";\n");
        }
        
        body.append("    } else {\n");
        // 调用原始方法
        if (returnType != CtClass.voidType) {
            body.append("        return ");
        } else {
            body.append("        ");
        }
        body.append(originalMethodName).append("($$);\n");
        body.append("    }\n");
        
        body.append("}");
        
        // 设置新的方法体
        method.setBody(body.toString());
    }
}

// ==================== MathUtils.java (保持不变) ====================
package com.example.demo;

/**
 * 数学工具类，包含静态方法
 */
public class MathUtils {
    
    /**
     * 加法运算
     */
    public static int add(int a, int b) {
        return a + b;
    }
    
    /**
     * 乘法运算
     */
    public static int multiply(int a, int b) {
        return a * b;
    }
    
    /**
     * 除法运算
     */
    public static int divide(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("除数不能为零");
        }
        return a / b;
    }
}

// ==================== Application.java (保持不变) ====================
package com.example.demo;

/**
 * 简单应用类，使用MathUtils
 */
public class Application {
    
    /**
     * 计算公式: (a + b) * 2
     */
    public int calculate(int a, int b) {
        int sum = MathUtils.add(a, b);
        return MathUtils.multiply(sum, 2);
    }
    
    public static void main(String[] args) {
        Application app = new Application();
        System.out.println("计算结果: " + app.calculate(10, 5));
    }
}

// ==================== MathUtilsTest.java (部分更新) ====================
package com.example.demo;

import com.example.staticmock.StaticMocker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MathUtils测试类
 */
class MathUtilsTest {
    
    private final StaticMocker mocker = StaticMocker.getInstance();
    
    @BeforeEach
    void setUp() {
        // 确保测试类使用MockClassLoader
        Thread.currentThread().setContextClassLoader(mocker.getMockClassLoader());
    }
    
    @AfterEach
    void tearDown() {
        // 每个测试结束后重置所有Mock
        mocker.resetAll();
    }
    
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
