package com.example.demo.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.example.demo.service.AuthenticationService;

@Component
@RequiredArgsConstructor
public class OpenApiClientInterceptor implements RequestInterceptor {

    private final AuthenticationService authenticationService;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        if (isOpenApiRequest(requestTemplate)) {

            if (!requestTemplate.url().contains("/authenticate")) {
                String token = authenticationService.authenticate();
                requestTemplate.header("Authorization", "Bearer " + token);

            }

            requestTemplate.header("X-Language", "en");
            requestTemplate.header("Content-Type", "application/json");
        }
    }

    // Check if the request is for OpenAPI endpoints
    private boolean isOpenApiRequest(RequestTemplate requestTemplate) {
        String requestUrl = requestTemplate.url();
        return requestUrl.startsWith("https://portal.signifyapp.com/integration-api/");
    }
}
