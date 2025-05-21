# Fixing Static Method Mocking Issues

**Created:** 5/21/2025 15:45  
**Updated:** 5/21/2025 15:46  
**Exported:** 5/21/2025 15:46  

## Prompt:
> File: paste.txt

现在报错static{附近格式不对，请重新审视代码并完整输出，解决不能mock的问题



## Response:
```plaintext
Thought process

我注意到用户提供了一个基于Javassist的静态方法Mock框架的代码，并表示在static{}附近有格式错误。让我仔细检查这段代码，特别是ClassGenerator.java中生成静态初始化块的部分。

首先，我来查看`generateStaticInitializer`方法，这是生成静态初始化块代码的方法：

`​`​`java
private String generateStaticInitializer(String className, String methodName, Class<?>[] paramTypes) {
    StringBuilder code = new StringBuilder();
    code.append("static {\n");
    code.append("  try {\n");
    
    // 创建参数类型数组
    code.append("    Class[] paramTypes = new Class[").append(paramTypes.length).append("];\n");
    for (int i = 0; i < paramTypes.length; i++) {
        code.append("    paramTypes[").append(i).append("] = ");
        if (paramTypes[i].isPrimitive()) {
            // 处理基本类型
            if (paramTypes[i] == int.class) {
                code.append("int.class;\n");
            } else if (paramTypes[i] == long.class) {
                code.append("long.class;\n");
            } else if (paramTypes[i] == boolean.class) {
                code.append("boolean.class;\n");
            } else if (paramTypes[i] == byte.class) {
                code.append("byte.class;\n");
            } else if (paramTypes[i] == char.class) {
                code.append("char.class;\n");
            } else if (paramTypes[i] == double.class) {
                code.append("double.class;\n");
            } else if (paramTypes[i] == float.class) {
                code.append("float.class;\n");
            } else if (paramTypes[i] == short.class) {
                code.append("short.class;\n");
            } else {
                code.append("void.class;\n");
            }
        } else {
            // 引用类型
            code.append("Class.forName(\"").append(paramTypes[i].getName()).append("\");\n");
        }
    }
    
    // 使用Java反射API替换原始方法
    code.append("    java.lang.reflect.Method originalMethod = ");
    code.append("Class.forName(\"").append(className).append("\")");
    code.append(".getDeclaredMethod(\"").append(methodName).append("\", paramTypes);\n");
    
    // 使方法可访问
    code.append("    originalMethod.setAccessible(true);\n");
    
    // 获取方法处理器
    code.append("    java.lang.reflect.Field handlerField = ");
    code.append("Class.forName(\"java.lang.reflect.Method\")");
    code.append(".getDeclaredField(\"methodAccessor\");\n");
    code.append("    handlerField.setAccessible(true);\n");
    
    // 设置方法处理器为我们的代理
    code.append("    Object originalHandler = handlerField.get(originalMethod);\n");
    code.append("    if (originalHandler != null) {\n");
    code.append("      System.out.println(\"已成功Mock静态方法: ").append(className)
        .append(".").append(methodName).append("\");\n");
    code.append("    }\n");
    
    code.append("  } catch (Exception e) {\n");
    code.append("    System.err.println(\"Mock静态方法时发生异常: \" + e);\n");
    code.append("    e.printStackTrace();\n");
    code.append("  }\n");
    code.append("}");
    
    return code.toString();
}
`​`​`

问题在于，这段代码只是打印了一条消息，但实际上并没有修改或替换原始方法的实现。静态初始化块中没有实际将原始方法的执行重定向到我们的代理方法。

我需要修改这段代码，使其能够真正地将原始静态方法的调用重定向到我们的代理方法。核心问题是缺少了将原始方法的实现替换为我们的代理方法的代码。

此外，由于Java的安全机制，直接通过反射修改Method对象的内部实现是困难的。我们需要一种更可靠的方式来拦截原始方法的调用。

我想到的一种解决方案是使用Javassist来直接修改类的字节码，将原始方法的实现替换为对我们代理方法的调用。

让我重新设计这个静态初始化块的生成逻辑，并修复格式错误：

1. 不使用静态初始化块的方式，而是使用Javassist直接修改原始方法的字节码
2. 将原始方法的实现改为调用我们的代理方法
3. 确保格式正确，没有语法错误

我将修改ClassGenerator.java中的实现，使用更直接的Javassist字节码操作来实现方法替换。
```

我仔细检查了代码，发现在`ClassGenerator.java`中的静态初始化块生成方法有问题。这段代码只是打印成功消息，但实际上并没有真正替换原始静态方法的实现，所以无法实现Mock功能。

我将重新设计整个`ClassGenerator.java`文件，使用更可靠的字节码修改方式来解决这个问题：

```java
package com.example.staticmock;

import javassist.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 类生成工具，用于生成静态方法的代理类和修改原始类
 */
public class ClassGenerator {
    // 类计数器，确保每次生成唯一类名
    private final AtomicInteger classCounter = new AtomicInteger(1);
    // 记录已修改的类
    private final Map<String, Boolean> modifiedClasses = new ConcurrentHashMap<>();
    // 记录代理类
    private final Map<String, Class<?>> proxyClasses = new ConcurrentHashMap<>();
    
    /**
     * 生成代理类，并修改原始类的静态方法实现
     */
    public Class<?> generateMockClass(Class<?> originalClass, String methodName, 
                                      Class<?>[] paramTypes, String methodSignature) throws Exception {
        // 如果已经处理过这个方法，直接返回已有的代理类
        if (proxyClasses.containsKey(methodSignature)) {
            return proxyClasses.get(methodSignature);
        }
        
        // 获取类池
        ClassPool classPool = ClassPool.getDefault();
        classPool.importPackage("com.example.staticmock");
        
        // 生成唯一的代理类名
        String proxyClassName = originalClass.getName() + "$MockProxy" + classCounter.getAndIncrement();
        
        // 获取原始类
        CtClass originalCtClass = classPool.get(originalClass.getName());
        
        // 确保类未被冻结
        if (originalCtClass.isFrozen()) {
            originalCtClass.defrost();
        }
        
        // 查找原始方法
        CtMethod originalMethod = findMethod(originalCtClass, methodName, paramTypes);
        if (originalMethod == null) {
            throw new NoSuchMethodException("方法未找到: " + methodName);
        }
        
        // 创建代理类
        CtClass proxyClass = classPool.makeClass(proxyClassName);
        
        // 创建代理方法
        CtMethod proxyMethod = createProxyMethod(proxyClass, originalMethod, methodSignature);
        proxyClass.addMethod(proxyMethod);
        
        // 修改原始方法的实现，使其调用代理方法
        modifyOriginalMethod(originalCtClass, originalMethod, proxyClassName, proxyMethod.getName());
        
        // 加载代理类
        Class<?> loadedProxyClass = proxyClass.toClass(originalClass.getClassLoader(), null);
        proxyClasses.put(methodSignature, loadedProxyClass);
        
        System.out.println("已成功Mock静态方法: " + originalClass.getName() + "." + methodName);
        
        return loadedProxyClass;
    }
    
    /**
     * 查找指定的方法
     */
    private CtMethod findMethod(CtClass ctClass, String methodName, Class<?>[] paramTypes) {
        try {
            CtClass[] ctParamTypes = new CtClass[paramTypes.length];
            ClassPool classPool = ctClass.getClassPool();
            
            for (int i = 0; i < paramTypes.length; i++) {
                if (paramTypes[i].isPrimitive()) {
                    // 处理基本类型
                    if (paramTypes[i] == int.class) {
                        ctParamTypes[i] = CtClass.intType;
                    } else if (paramTypes[i] == long.class) {
                        ctParamTypes[i] = CtClass.longType;
                    } else if (paramTypes[i] == boolean.class) {
                        ctParamTypes[i] = CtClass.booleanType;
                    } else if (paramTypes[i] == byte.class) {
                        ctParamTypes[i] = CtClass.byteType;
                    } else if (paramTypes[i] == char.class) {
                        ctParamTypes[i] = CtClass.charType;
                    } else if (paramTypes[i] == double.class) {
                        ctParamTypes[i] = CtClass.doubleType;
                    } else if (paramTypes[i] == float.class) {
                        ctParamTypes[i] = CtClass.floatType;
                    } else if (paramTypes[i] == short.class) {
                        ctParamTypes[i] = CtClass.shortType;
                    } else {
                        ctParamTypes[i] = CtClass.voidType;
                    }
                } else {
                    // 引用类型
                    ctParamTypes[i] = classPool.get(paramTypes[i].getName());
                }
            }
            
            return ctClass.getDeclaredMethod(methodName, ctParamTypes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 创建代理方法
     */
    private CtMethod createProxyMethod(CtClass proxyClass, CtMethod originalMethod, String methodSignature) throws Exception {
        // 获取返回类型
        CtClass returnType = originalMethod.getReturnType();
        
        // 构建代理方法签名
        String proxyMethodName = originalMethod.getName() + "_proxy";
        CtClass[] paramTypes = originalMethod.getParameterTypes();
        
        // 创建新方法
        CtMethod proxyMethod = new CtMethod(returnType, proxyMethodName, paramTypes, proxyClass);
        proxyMethod.setModifiers(Modifier.PUBLIC | Modifier.STATIC);
        
        // 构建方法体
        StringBuilder body = new StringBuilder();
        body.append("{\n");
        
        // 构建参数数组
        body.append("  Object[] args = new Object[").append(paramTypes.length).append("];\n");
        for (int i = 0; i < paramTypes.length; i++) {
            body.append("  args[").append(i).append("] = ");
            if (paramTypes[i].isPrimitive()) {
                // 包装基本类型
                if (paramTypes[i] == CtClass.intType) {
                    body.append("new Integer($").append(i + 1).append(")");
                } else if (paramTypes[i] == CtClass.longType) {
                    body.append("new Long($").append(i + 1).append(")");
                } else if (paramTypes[i] == CtClass.booleanType) {
                    body.append("new Boolean($").append(i + 1).append(")");
                } else if (paramTypes[i] == CtClass.byteType) {
                    body.append("new Byte($").append(i + 1).append(")");
                } else if (paramTypes[i] == CtClass.charType) {
                    body.append("new Character($").append(i + 1).append(")");
                } else if (paramTypes[i] == CtClass.doubleType) {
                    body.append("new Double($").append(i + 1).append(")");
                } else if (paramTypes[i] == CtClass.floatType) {
                    body.append("new Float($").append(i + 1).append(")");
                } else if (paramTypes[i] == CtClass.shortType) {
                    body.append("new Short($").append(i + 1).append(")");
                }
            } else {
                body.append("$").append(i + 1);
            }
            body.append(";\n");
        }
        
        // 记录方法调用
        body.append("  com.example.staticmock.MockContext.recordCall(\"")
            .append(methodSignature).append("\", args);\n");
        
        // 获取Mock结果
        body.append("  Object result = com.example.staticmock.MockContext.getMockResult(\"")
            .append(methodSignature).append("\", args);\n");
        
        // 处理异常
        body.append("  if (result instanceof RuntimeException) {\n");
        body.append("    throw (RuntimeException)result;\n");
        body.append("  }\n");
        
        // 返回结果
        if (returnType != CtClass.voidType) {
            body.append("  return ");
            
            // 处理基本类型转换
            if (returnType.isPrimitive()) {
                if (returnType == CtClass.intType) {
                    body.append("((Integer)result).intValue()");
                } else if (returnType == CtClass.longType) {
                    body.append("((Long)result).longValue()");
                } else if (returnType == CtClass.booleanType) {
                    body.append("((Boolean)result).booleanValue()");
                } else if (returnType == CtClass.byteType) {
                    body.append("((Byte)result).byteValue()");
                } else if (returnType == CtClass.charType) {
                    body.append("((Character)result).charValue()");
                } else if (returnType == CtClass.doubleType) {
                    body.append("((Double)result).doubleValue()");
                } else if (returnType == CtClass.floatType) {
                    body.append("((Float)result).floatValue()");
                } else if (returnType == CtClass.shortType) {
                    body.append("((Short)result).shortValue()");
                }
            } else {
                body.append("(").append(returnType.getName()).append(")result");
            }
            body.append(";\n");
        }
        
        body.append("}");
        
        // 设置方法体
        proxyMethod.setBody(body.toString());
        
        return proxyMethod;
    }
    
    /**
     * 修改原始方法实现
     */
    private void modifyOriginalMethod(CtClass originalClass, CtMethod originalMethod, 
                                     String proxyClassName, String proxyMethodName) throws Exception {
        // 构建调用代理方法的代码
        StringBuilder body = new StringBuilder();
        body.append("{\n");
        
        // 调用代理方法
        if (originalMethod.getReturnType() != CtClass.voidType) {
            body.append("  return ");
        } else {
            body.append("  ");
        }
        
        body.append(proxyClassName).append(".").append(proxyMethodName).append("(");
        
        // 传递参数
        CtClass[] paramTypes = originalMethod.getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            if (i > 0) body.append(", ");
            body.append("$").append(i + 1);
        }
        body.append(");\n");
        
        body.append("}");
        
        // 替换方法体
        originalMethod.setBody(body.toString());
    }
    
    /**
     * 重置类生成器
     */
    public void reset() {
        modifiedClasses.clear();
        proxyClasses.clear();
        classCounter.set(1);
    }
}
```

这个新的实现有以下关键改进：

1. 直接修改原始方法的实现，让它调用我们的代理方法，而不是通过静态初始化块尝试修改
2. 使用Javassist的API来处理方法体的替换，确保格式正确
3. 去除了隔离类加载器的复杂性，简化了设计
4. 添加了对已冻结类的检查和解冻处理
5. 重写了代理方法的创建，使用Javassist的类型系统而非Java的反射API

这种实现方式更直接，通过修改类的字节码来实现静态方法的拦截，不依赖Java反射API的复杂操作，应该能解决Mock无法生效的问题。

要完整使用这个框架，其他文件（StaticMocker.java, MockContext.java等）可以保持不变，只需要替换ClassGenerator.java文件。

如果你在使用过程中遇到任何问题，请告诉我，我可以进一步修改或提供其他解决方案。





---
Powered by [Claude Exporter](https://www.claudexporter.com)