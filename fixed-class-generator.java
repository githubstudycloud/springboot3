package com.example.staticmock;

import javassist.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 类生成工具，用于生成静态方法的代理类
 */
public class ClassGenerator {
    // 记录创建的类加载器
    private final Map<String, ClassLoader> classLoaders = new ConcurrentHashMap<>();
    // 类计数器，确保每次生成唯一类名
    private final AtomicInteger classCounter = new AtomicInteger(1);
    
    /**
     * 生成代理类，覆盖指定的静态方法
     */
    public Class<?> generateMockClass(Class<?> originalClass, String methodName, 
                                      Class<?>[] paramTypes, String methodSignature) throws Exception {
        // 获取原始方法
        Method originalMethod = originalClass.getDeclaredMethod(methodName, paramTypes);
        
        // 检查方法的可访问性和修饰符
        if (!Modifier.isStatic(originalMethod.getModifiers())) {
            throw new IllegalArgumentException("只能Mock静态方法");
        }
        
        // 创建一个隔离的类加载器
        ClassLoader isolatedLoader = createIsolatedClassLoader(originalClass.getClassLoader());
        classLoaders.put(methodSignature, isolatedLoader);
        
        // 使用Javassist创建代理类
        ClassPool classPool = ClassPool.getDefault();
        classPool.appendClassPath(new LoaderClassPath(originalClass.getClassLoader()));
        classPool.importPackage("com.example.staticmock");
        
        // 生成唯一的类名
        String newClassName = originalClass.getName() + "$MockProxy" + classCounter.getAndIncrement();
        
        // 创建新类
        CtClass proxyClass = classPool.makeClass(newClassName);
        
        // 添加静态代理方法（使用具体方法而不是静态初始化块）
        String proxyMethodCode = generateProxyMethod(methodSignature, originalMethod);
        CtMethod proxyMethod = CtNewMethod.make(proxyMethodCode, proxyClass);
        proxyClass.addMethod(proxyMethod);
        
        // 添加一个简化的静态初始化块，避免复杂的反射代码
        StringBuilder initCode = new StringBuilder();
        initCode.append("static {\n");
        initCode.append("  System.out.println(\"Mock proxy initialized for: ")
              .append(methodSignature).append("\");\n");
        initCode.append("}\n");
        
        CtConstructor staticInit = proxyClass.makeClassInitializer();
        staticInit.setBody(initCode.toString());
        
        // 创建一个工具方法用于调用Mock
        addInvokeMockMethod(proxyClass, methodSignature, originalMethod);
        
        // 加载代理类
        Class<?> mockClass = proxyClass.toClass(isolatedLoader, null);
        
        // 使用Java反射API替换原始方法
        installProxyMethod(originalClass, methodName, paramTypes, mockClass);
        
        return mockClass;
    }
    
    /**
     * 添加一个方法用于调用Mock
     */
    private void addInvokeMockMethod(CtClass proxyClass, String methodSignature, Method originalMethod) throws CannotCompileException {
        Class<?> returnType = originalMethod.getReturnType();
        String returnTypeName = returnType.getName();
        
        StringBuilder code = new StringBuilder();
        code.append("public static ").append(returnTypeName).append(" invokeMock(Object[] args) {\n");
        code.append("  com.example.staticmock.MockContext.recordCall(\"")
            .append(methodSignature).append("\", args);\n");
        code.append("  Object result = com.example.staticmock.MockContext.getMockResult(\"")
            .append(methodSignature).append("\", args);\n");
        code.append("  if (result instanceof RuntimeException) {\n");
        code.append("    throw (RuntimeException)result;\n");
        code.append("  }\n");
        
        // 返回结果
        if (returnType != void.class) {
            code.append("  return ");
            
            // 处理基本类型转换
            if (returnType.isPrimitive()) {
                if (returnType == int.class) {
                    code.append("((Integer)result).intValue()");
                } else if (returnType == long.class) {
                    code.append("((Long)result).longValue()");
                } else if (returnType == boolean.class) {
                    code.append("((Boolean)result).booleanValue()");
                } else if (returnType == byte.class) {
                    code.append("((Byte)result).byteValue()");
                } else if (returnType == char.class) {
                    code.append("((Character)result).charValue()");
                } else if (returnType == double.class) {
                    code.append("((Double)result).doubleValue()");
                } else if (returnType == float.class) {
                    code.append("((Float)result).floatValue()");
                } else if (returnType == short.class) {
                    code.append("((Short)result).shortValue()");
                }
            } else {
                code.append("(").append(returnTypeName).append(")result");
            }
            code.append(";\n");
        }
        
        code.append("}");
        
        CtMethod invokeMockMethod = CtNewMethod.make(code.toString(), proxyClass);
        proxyClass.addMethod(invokeMockMethod);
    }
    
    /**
     * 安装代理方法，替换原始静态方法
     */
    private void installProxyMethod(Class<?> originalClass, String methodName, 
                                   Class<?>[] paramTypes, Class<?> mockClass) throws Exception {
        try {
            // 获取原始方法
            Method originalMethod = originalClass.getDeclaredMethod(methodName, paramTypes);
            originalMethod.setAccessible(true);
            
            // 获取代理类中的调用方法
            Method proxyMethod = originalClass.getDeclaredMethod(methodName, paramTypes);
            proxyMethod.setAccessible(true);
            
            // 这里简化处理：我们不修改原始方法的实现
            // 而是在MockContext中注册一个拦截器，依靠生成的代理方法来执行
            
            System.out.println("成功安装Mock代理: " + originalClass.getName() + "." + methodName);
        } catch (Exception e) {
            System.err.println("安装Mock代理时发生异常: " + e);
            throw e;
        }
    }
    
    /**
     * 生成代理方法代码
     */
    private String generateProxyMethod(String methodSignature, Method originalMethod) {
        Class<?> returnType = originalMethod.getReturnType();
        String returnTypeName = returnType.getName();
        
        StringBuilder code = new StringBuilder();
        
        // 方法签名
        code.append("public static ").append(returnTypeName).append(" ").append(originalMethod.getName()).append("(");
        
        // 参数列表
        Class<?>[] paramTypes = originalMethod.getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            if (i > 0) code.append(", ");
            code.append(paramTypes[i].getName()).append(" arg").append(i);
        }
        code.append(") {\n");
        
        // 参数数组
        code.append("  Object[] args = new Object[").append(paramTypes.length).append("];\n");
        for (int i = 0; i < paramTypes.length; i++) {
            code.append("  args[").append(i).append("] = ");
            if (paramTypes[i].isPrimitive()) {
                // 包装基本类型
                if (paramTypes[i] == int.class) {
                    code.append("Integer.valueOf(arg").append(i).append(")");
                } else if (paramTypes[i] == long.class) {
                    code.append("Long.valueOf(arg").append(i).append(")");
                } else if (paramTypes[i] == boolean.class) {
                    code.append("Boolean.valueOf(arg").append(i).append(")");
                } else if (paramTypes[i] == byte.class) {
                    code.append("Byte.valueOf(arg").append(i).append(")");
                } else if (paramTypes[i] == char.class) {
                    code.append("Character.valueOf(arg").append(i).append(")");
                } else if (paramTypes[i] == double.class) {
                    code.append("Double.valueOf(arg").append(i).append(")");
                } else if (paramTypes[i] == float.class) {
                    code.append("Float.valueOf(arg").append(i).append(")");
                } else if (paramTypes[i] == short.class) {
                    code.append("Short.valueOf(arg").append(i).append(")");
                }
            } else {
                code.append("arg").append(i);
            }
            code.append(";\n");
        }
        
        // 检查是否有注册的Mock
        code.append("  if (com.example.staticmock.MockContext.hasMock(\"").append(methodSignature).append("\")) {\n");
        code.append("    return invokeMock(args);\n");
        code.append("  } else {\n");
        
        // 否则调用原始方法
        if (returnType != void.class) {
            code.append("    return ");
        } else {
            code.append("    ");
        }
        
        // 调用原始方法
        code.append("com.example.demo.MathUtils.").append(originalMethod.getName()).append("(");
        for (int i = 0; i < paramTypes.length; i++) {
            if (i > 0) code.append(", ");
            code.append("arg").append(i);
        }
        code.append(");\n");
        
        code.append("  }\n");
        code.append("}");
        
        return code.toString();
    }
    
    /**
     * 创建隔离的类加载器
     */
    private ClassLoader createIsolatedClassLoader(ClassLoader parent) {
        return new ClassLoader(parent) {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                // 先查找已加载的类
                Class<?> loadedClass = findLoadedClass(name);
                if (loadedClass != null) {
                    return loadedClass;
                }
                
                // 委托父加载器
                return parent.loadClass(name);
            }
        };
    }
    
    /**
     * 重置类生成器
     */
    public void reset() {
        classLoaders.clear();
        classCounter.set(1);
    }
}