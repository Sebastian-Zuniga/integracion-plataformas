package com.pcfactory.ecommerce.dto.webpay;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record WebpayCreateRequest(
        @JsonProperty("buy_order") String buyOrder,
        @JsonProperty("session_id") String sessionId,
        BigDecimal amount,
        @JsonProperty("return_url") String returnUrl
) {
}
