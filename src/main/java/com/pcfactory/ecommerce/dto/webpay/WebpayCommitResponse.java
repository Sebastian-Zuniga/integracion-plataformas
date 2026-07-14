package com.pcfactory.ecommerce.dto.webpay;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record WebpayCommitResponse(
        @JsonProperty("buy_order") String buyOrder,
        @JsonProperty("session_id") String sessionId,
        BigDecimal amount,
        String status,
        @JsonProperty("response_code") Integer responseCode,
        @JsonProperty("authorization_code") String authorizationCode,
        @JsonProperty("payment_type_code") String paymentTypeCode,
        @JsonProperty("installments_number") Integer installmentsNumber
) {
}
