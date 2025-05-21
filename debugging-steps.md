# Debugging Javassist Static Method Mocking Issues

Follow these steps to isolate and fix issues with the Javassist-based static method mocking framework:

## 1. Add Detailed Logging

Add more detailed logging throughout the `ClassGenerator` class to see what's happening:

```java
// At the start of generateMockClass
System.out.println("Generating mock class for: " + methodSignature);

// Before creating static initializer
System.out.println("Creating static initializer block");

// After creating static initializer
System.out.println("Static initializer block created successfully");

// Inside generateStaticInitializer, log the generated code:
String initCode = code.toString();
System.out.println("Generated static initializer:");
System.out.println(initCode);
return initCode;
```

## 2. Test with Minimal Example

Create a minimal test case with a very simple class to mock:

```java
// SimpleClass.java
public class SimpleClass {
    public static int add(int a, int b) {
        return a + b;
    }
}

// SimpleTest.java
public class SimpleTest {
    public static void main(String[] args) {
        StaticMocker mocker = StaticMocker.getInstance();
        
        System.out.println("Original result: " + SimpleClass.add(1, 2));
        
        try {
            mocker.when(SimpleClass.class, "add", int.class, int.class)
                  .thenReturn(10);
                  
            System.out.println("Mocked result: " + SimpleClass.add(1, 2));
        } catch (Exception e) {
            System.err.println("Mock failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

## 3. Inspect Generated Classes

Add code to save the generated class bytecode to a file for inspection:

```java
private void saveClassToFile(CtClass ctClass) {
    try {
        String className = ctClass.getName();
        String fileName = className.replace('.', '/') + ".class";
        ctClass.writeFile("/tmp");
        System.out.println("Class written to: /tmp/" + fileName);
    } catch (Exception e) {
        System.err.println("Failed to write class to file: " + e.getMessage());
    }
}
```

Then call this method before `toClass()`:

```java
saveClassToFile(proxyClass);
Class<?> mockClass = proxyClass.toClass(isolatedLoader, null);
```

## 4. Step-by-Step Approach

If issues persist, try a more incremental approach:

1. Start with a very simple static initializer that does nothing:
   ```java
   static {
     System.out.println("Mock initialized");
   }
   ```

2. Once that works, add simple reflection code:
   ```java
   static {
     try {
       Class<?> cls = Class.forName("com.example.demo.MathUtils");
       System.out.println("Class loaded: " + cls.getName());
     } catch (Exception e) {
       e.printStackTrace();
     }
   }
   ```

3. Gradually add more functionality until you find the breaking point.

## 5. Alternative Method Replacement

Instead of trying to replace methods in a static initializer, try a simpler approach:

```java
// In StaticMocker class
public <T> void mockStaticMethod(Class<T> clazz, String methodName, Class<?>[] paramTypes) throws Exception {
    Method originalMethod = clazz.getDeclaredMethod(methodName, paramTypes);
    originalMethod.setAccessible(true);
    
    // Use direct Java reflection to change method behavior
    Field modifiersField = Method.class.getDeclaredField("modifiers");
    modifiersField.setAccessible(true);
    modifiersField.setInt(originalMethod, originalMethod.getModifiers() & ~Modifier.FINAL);
    
    // Store original method somewhere if needed for restore
    
    // ...rest of implementation
}
```

## 6. Use JVMTIAgent Alternative

If all else fails, consider using a more direct approach like instrumentation with a JVM attachment API:

```java
import com.sun.tools.attach.VirtualMachine;

// This must be in a separate process that attaches to your JVM
String pid = // get the process ID of your JVM
VirtualMachine vm = VirtualMachine.attach(pid);
try {
    vm.loadAgent("path/to/agent.jar", "");
} finally {
    vm.detach();
}
```

This approach relies on the `com.sun.tools.attach` API, which is part of the JDK tools.
