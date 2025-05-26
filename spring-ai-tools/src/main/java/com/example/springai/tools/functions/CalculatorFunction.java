package com.example.springai.tools.functions;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.function.Function;

/**
 * Function to perform calculations
 */
public class CalculatorFunction implements Function<CalculatorFunction.Request, CalculatorFunction.Response> {

    @Override
    public Response apply(Request request) {
        try {
            double result = switch (request.operation().toLowerCase()) {
                case "add", "+" -> request.a() + request.b();
                case "subtract", "-" -> request.a() - request.b();
                case "multiply", "*" -> request.a() * request.b();
                case "divide", "/" -> {
                    if (request.b() == 0) {
                        throw new ArithmeticException("Division by zero");
                    }
                    yield request.a() / request.b();
                }
                case "power", "^" -> Math.pow(request.a(), request.b());
                case "sqrt" -> Math.sqrt(request.a());
                case "sin" -> Math.sin(Math.toRadians(request.a()));
                case "cos" -> Math.cos(Math.toRadians(request.a()));
                case "tan" -> Math.tan(Math.toRadians(request.a()));
                case "log" -> Math.log10(request.a());
                case "ln" -> Math.log(request.a());
                default -> throw new IllegalArgumentException("Unknown operation: " + request.operation());
            };
            
            return new Response(true, result, null, formatExpression(request));
        } catch (Exception e) {
            return new Response(false, null, e.getMessage(), formatExpression(request));
        }
    }
    
    private String formatExpression(Request request) {
        if (request.b() != null) {
            return String.format("%f %s %f", request.a(), request.operation(), request.b());
        } else {
            return String.format("%s(%f)", request.operation(), request.a());
        }
    }

    public record Request(
        @JsonProperty("operation") String operation,
        @JsonProperty("a") double a,
        @JsonProperty("b") Double b
    ) {}

    public record Response(
        @JsonProperty("success") boolean success,
        @JsonProperty("result") Double result,
        @JsonProperty("error") String error,
        @JsonProperty("expression") String expression
    ) {}
}
