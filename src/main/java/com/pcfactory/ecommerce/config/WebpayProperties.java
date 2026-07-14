package com.pcfactory.ecommerce.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "webpay")
public class WebpayProperties {

    private String baseUrl;
    private String commerceCode;
    private String apiKey;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getCommerceCode() {
        return commerceCode;
    }

    public void setCommerceCode(String commerceCode) {
        this.commerceCode = commerceCode;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
