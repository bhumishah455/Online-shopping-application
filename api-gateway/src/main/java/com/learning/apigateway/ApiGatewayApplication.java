/*
 * Distributed Tracing:
 *
 * Zipkin helps you see the complete journey of a request through your microservices,
 * So you can quickly find which service is slow or where the request failed.
 * Zipkin visually shows these traces and spans, making debugging microservices much easier.
 *
 * Trace ID remains the same throughout a single request flow.
 * Span ID changes for each operation/service call.
 *
 * With Circuit Breaker, additional spans may be created,
 * so Zipkin can show separate spans/operations for the request.
 */

package com.learning.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableEurekaClient
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
