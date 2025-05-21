/**
 * ByteBuddy Alternative Approach
 * 
 * ByteBuddy is a more modern library than Javassist and provides a cleaner API
 * for manipulating classes. This is a simplified example of how you could use
 * ByteBuddy to mock static methods.
 */

// First, add ByteBuddy to your pom.xml
/*
<dependency>
    <groupId>net.bytebuddy</groupId>
    <artifactId>byte-buddy</artifactId>
    <version>1.14.9</version>
</dependency>
<dependency>
    <groupId>net.bytebuddy</groupId>
    <artifactId>byte-buddy-agent</artifactId>
    <version>1.14.9</version>
</dependency>
*/

// Then, create a simpler StaticMocker class:

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class StaticMocker {
    private static final StaticMocker INSTANCE = new StaticMocker();
    private final Map<String, Function<Object[], Object>> mockDefinitions = new ConcurrentHashMap<>();
    
    private StaticMocker() {}
    
    public static StaticMocker getInstance() {
        return INSTANCE;
    }
    
    public <T> WhenBuilder<T> when(Class<T> clazz, String methodName, Class<?>... paramTypes) {
        return new WhenBuilder<>(clazz, methodName, paramTypes);
    }
    
    public class WhenBuilder<T> {
        private final Class<T> clazz;
        private final String methodName;
        private final Class<?>[] paramTypes;
        
        private WhenBuilder(Class<T> clazz, String methodName, Class<?>[] paramTypes) {
            this.clazz = clazz;
            this.methodName = methodName;
            this.paramTypes = paramTypes;
        }
        
        public void thenReturn(Object returnValue) {
            String methodSignature = buildMethodSignature(clazz, methodName, paramTypes);
            mockDefinitions.put(methodSignature, args -> returnValue);
            
            try {
                // Create a subclass that overrides the static method
                new ByteBuddy()
                    .redefine(clazz)
                    .method(ElementMatchers.named(methodName))
                    .intercept(MethodDelegation.to(new MockInterceptor()))
                    .make()
                    .load(clazz.getClassLoader());
            } catch (Exception e) {
                throw new RuntimeException("Failed to create mock", e);
            }
        }
        
        // Other methods like thenThrow, etc.
    }
    
    // Interceptor that handles the method calls
    public class MockInterceptor {
        @net.bytebuddy.implementation.bind.annotation.RuntimeType
        public Object intercept(@Origin Method method, @AllArguments Object[] args) {
            String methodSignature = buildMethodSignature(
                method.getDeclaringClass(), method.getName(), method.getParameterTypes());
            
            Function<Object[], Object> mockBehavior = mockDefinitions.get(methodSignature);
            if (mockBehavior != null) {
                return mockBehavior.apply(args);
            }
            
            // Call original method if no mock is defined
            try {
                return method.invoke(null, args);
            } catch (Exception e) {
                throw new RuntimeException("Failed to call original method", e);
            }
        }
    }
    
    private String buildMethodSignature(Class<?> clazz, String methodName, Class<?>[] paramTypes) {
        StringBuilder signature = new StringBuilder();
        signature.append(clazz.getName()).append("#").append(methodName).append("(");
        
        for (int i = 0; i < paramTypes.length; i++) {
            if (i > 0) signature.append(",");
            signature.append(paramTypes[i].getName());
        }
        
        signature.append(")");
        return signature.toString();
    }
    
    public void resetAll() {
        mockDefinitions.clear();
    }
    
    // Verification methods can be added here
}