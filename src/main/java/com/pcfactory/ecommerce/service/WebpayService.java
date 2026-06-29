package com.pcfactory.ecommerce.service;

import com.pcfactory.ecommerce.model.Transaccion;
import com.pcfactory.ecommerce.repository.TransaccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class WebpayService {

    @Autowired
    private TransaccionRepository transaccionRepository;

    private final WebClient webClient;

    // Configuración básica apuntando al ambiente de certificación de Transbank
    public WebpayService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://webpay3gint.transbank.cl").build();
    }

    public Map<String, Object> iniciarPago(BigDecimal monto, String urlRetorno) {
        // 1. Generar identificadores únicos de la transacción
        String buyOrder = "ORD-" + UUID.randomUUID().toString().substring(0, 8);
        String sessionId = "SESS-" + UUID.randomUUID().toString().substring(0, 8);

        // 2. Persistir inicialmente la transacción en estado CREADO
        Transaccion transaccion = new Transaccion(buyOrder, sessionId, monto, "CREADO", LocalDateTime.now());
        transaccionRepository.save(transaccion);

        // 3. Preparar el cuerpo para la API de Webpay
        Map<String, Object> body = new HashMap<>();
        body.put("buy_order", buyOrder);
        body.put("session_id", sessionId);
        body.put("amount", monto);
        body.put("return_url", urlRetorno);

        try {
            // 4. Llamar al servicio externo usando WebClient (POST /rswebpaytransaction/api/webpay/v1.2/transactions)
            // Se agregan las cabeceras obligatorias del comercio de integración de Transbank
            Map<String, Object> responseWebpay = this.webClient.post()
                    .uri("/rswebpaytransaction/api/webpay/v1.2/transactions")
                    .header("Tbk-Api-Key-Id", "597055555532")
                    .header("Tbk-Api-Key-Secret", "5729914e021bb4d043ca6dd22e66e9d9574af35e1850d4d7519b15cd9d894fee")
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block(); // Bloqueante de manera simple para ajustarse a controladores secuenciales estándar

            if (responseWebpay != null && responseWebpay.containsKey("token")) {
                String token = (String) responseWebpay.get("token");
                String url = (String) responseWebpay.get("url");

                // Actualizar el registro con el token recibido
                transaccion.setTokenWebpay(token);
                transaccionRepository.save(transaccion);

                Map<String, Object> resultado = new HashMap<>();
                resultado.put("token", token);
                resultado.put("url", url);
                return resultado;
            }
        } catch (Exception e) {
            // Si la llamada falla, dejamos constancia de que no se pudo procesar el pago externo
            transaccion.setEstado("NO PAGADO");
            transaccionRepository.save(transaccion);
        }

        return null;
    }

    public Transaccion finalizarPago(String token, String status) {
        // Buscar la transacción asociada al token recibido de Webpay
        return transaccionRepository.findByTokenWebpay(token)
                .map(transaccion -> {
                    if ("AUTHORIZED".equalsIgnoreCase(status) || "APPROVED".equalsIgnoreCase(status)) {
                        transaccion.setEstado("PAGADO");
                    } else {
                        transaccion.setEstado("NO PAGADO");
                    }
                    return transaccionRepository.save(transaccion);
                }).orElse(null);
    }
}