package ru.otus.java.basic.http.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String rawRequest;
    private String uri;
    private HttpMethod method;
    private String version;
    private Map<String, String> parameters;
    private Map<String, String> headers;
    private String body;
    private static final Logger logger = LogManager.getLogger(HttpRequest.class.getName());

    public String getRoutingKey() {
        return method + " " + uri;
    }

    public String getUri() {
        return uri;
    }

    public String getVersion() {
        return version;
    }

    public String getBody() {
        return body;
    }

    public HttpRequest(String rawRequest) {
        this.rawRequest = rawRequest;
        this.parse();
    }

    private void parse() {
        if (rawRequest.isEmpty()) {
            logger.error("http request header is empty");
            throw new BadRequestException("http request header is empty");
        }
        int startIndex = rawRequest.indexOf(' ');
        int endIndex1 = rawRequest.indexOf(' ', startIndex + 1);
        int endIndex2 = rawRequest.indexOf("\r\n");
        uri = rawRequest.substring(startIndex + 1, endIndex1);
        method = HttpMethod.valueOf(rawRequest.substring(0, startIndex));
        version = rawRequest.substring(endIndex1 + 1, endIndex2);
        parameters = new HashMap<>();
        if (uri.contains("?")) {
            String[] elements = uri.split("[?]");
            this.uri = elements[0];
            String[] keysValues = elements[1].split("&");
            for (String o : keysValues) {
                String[] keyValue = o.split("=");
                this.parameters.put(keyValue[0], keyValue[1]);
            }
        }
        headers = new HashMap<>();
        startIndex = rawRequest.indexOf("\r\n") + 2;
        endIndex2 = rawRequest.indexOf("\r\n\r\n");
        String rawHeaders = rawRequest.substring(startIndex, endIndex2);
        String[] headersLines = rawHeaders.split("\r\n");
        for (String o : headersLines) {
            String[] keyValue = o.split(": ", 2);
            headers.put(keyValue[0], keyValue[1]);
        }
        if (method == HttpMethod.POST) {
            this.body = rawRequest.substring(
                    rawRequest.indexOf("\r\n\r\n") + 4
            );
        }
    }

    public boolean containsParameter(String key) {
        return parameters.containsKey(key);
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public void printInfo() {
        logger.info("uri: {}", uri);
        logger.info("http method: {}", method);
        logger.info("http version: {}", version);
        if (!(body == null)) {
            logger.info("body: {}", body);
        } else {
            logger.info("body: null");
        }
        if (!parameters.isEmpty()) {
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                logger.info("uri parameters: {}: {}", entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            logger.info("headers: {}: {}", entry.getKey(), entry.getValue());
        }
        logger.debug("rawRequest:\n{}", rawRequest);
    }
}
