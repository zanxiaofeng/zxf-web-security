package com.zxf.example.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

@Slf4j
public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        if (log.isDebugEnabled()) {
            logRequest(request, body, log::debug);
        }
        //BufferingClientHttpResponseWrapper response = new BufferingClientHttpResponseWrapper(execution.execute(request, body));
        ClientHttpResponse response;
        try {
            response = execution.execute(request, body);
            Assert.isTrue(response.getClass().getName().equals("org.springframework.http.client.BufferingClientHttpResponseWrapper"), "Not BufferingClientHttpResponseWrapper");
        } catch (Exception ex) {
            log.error("Exception when seng request", ex);
            logRequest(request, body, log::error);
            throw ex;
        }
        if (log.isDebugEnabled()) {
            logResponse(response, log::debug);
        }
        if (!response.getStatusCode().is2xxSuccessful()) {
            logRequest(request, body, log::error);
            logResponse(response, log::error);
        }
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body, Consumer<String> logger) {
        logger.accept("=================================================Request begin=================================================");
        logger.accept("URI             : " + request.getURI());
        logger.accept("Methed          : " + request.getMethod());
        logger.accept("Headers         : " + request.getHeaders());
        logger.accept("Request Body    : " + new String(body, StandardCharsets.UTF_8));
        logger.accept("=================================================Request end=================================================");
    }

    private void logResponse(ClientHttpResponse response, Consumer<String> logger) throws IOException {
        logger.accept("=================================================Response begin=================================================");
        logger.accept("Status     : " + response.getStatusCode());
        logger.accept("Headers         : " + response.getHeaders());
        logger.accept("Response Body   : " + toString(response));
        logger.accept("=================================================Response end=================================================");
    }

    private String toString(ClientHttpResponse response) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
            String inputStr;
            StringBuilder builder = new StringBuilder();
            while ((inputStr = bufferedReader.readLine()) != null) {
                builder.append(inputStr);
            }
            return builder.toString();
        }
    }
}