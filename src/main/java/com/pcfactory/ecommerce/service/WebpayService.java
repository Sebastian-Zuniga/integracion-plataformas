package com.pcfactory.ecommerce.service;

import com.pcfactory.ecommerce.config.WebpayProperties;
import com.pcfactory.ecommerce.dto.webpay.WebpayCommitResponse;
import com.pcfactory.ecommerce.dto.webpay.WebpayCreateRequest;
import com.pcfactory.ecommerce.dto.webpay.WebpayCreateResponse;
import com.pcfactory.ecommerce.model.Transaccion;
import com.pcfactory.ecommerce.repository.TransaccionRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class WebpayService {

    private final TransaccionRepository transaccionRepository;
    private final WebpayClient webpayClient;

    public WebpayService(TransaccionRepository transaccionRepository,
                         WebpayProperties properties,
                         WebClient.Builder webClientBuilder) {
        this.transaccionRepository = transaccionRepository;
        this.webpayClient = webClientBuilder
                .baseUrl(properties.getBaseUrl())
                .defaultHeader("Tbk-Api-Key-Id", properties.getCommerceCode())
                .defaultHeader("Tbk-Api-Key-Secret", properties.getApiKey())
                .build();
    }

    public WebpayCreateResponse iniciarPago(BigDecimal monto, String urlRetorno) {
        validarDatosCreacion(monto, urlRetorno);

        String buyOrder = "ORD-" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        String sessionId = "SESS-" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);

        Transaccion transaccion = new Transaccion();
        transaccion.setBuyOrder(buyOrder);
        transaccion.setSessionId(sessionId);
        transaccion.setAmount(monto);
        transaccion.setEstado("CREADO");
        transaccion.setFechaCreacion(LocalDateTime.now());
        transaccion.setFechaActualizacion(LocalDateTime.now());
        transaccionRepository.save(transaccion);

        WebpayCreateRequest request = new WebpayCreateRequest(buyOrder, sessionId, monto, urlRetorno);

        try {
            WebpayCreateResponse respuesta = webpayClient.post()
                    .uri("/rswebpaytransaction/api/webpay/v1.2/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(status -> status.isError(), response ->
                            response.bodyToMono(String.class)
                                    .map(body -> new ResponseStatusException(BAD_GATEWAY,
                                            "Webpay rechazó la creación de la transacción: " + body)))
                    .bodyToMono(WebpayCreateResponse.class)
                    .block();

            if (respuesta == null || respuesta.token() == null || respuesta.url() == null) {
                throw new ResponseStatusException(BAD_GATEWAY, "Webpay no retornó token y URL válidos");
            }

            transaccion.setTokenWebpay(respuesta.token());
            transaccion.setFechaActualizacion(LocalDateTime.now());
            transaccionRepository.save(transaccion);
            return respuesta;
        } catch (ResponseStatusException exception) {
            transaccion.setEstado("NO PAGADO");
            transaccion.setFechaActualizacion(LocalDateTime.now());
            transaccionRepository.save(transaccion);
            throw exception;
        } catch (Exception exception) {
            transaccion.setEstado("NO PAGADO");
            transaccion.setFechaActualizacion(LocalDateTime.now());
            transaccionRepository.save(transaccion);
            throw new ResponseStatusException(BAD_GATEWAY, "No fue posible comunicarse con Webpay", exception);
        }
    }

    public Transaccion finalizarPago(String token) {
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "El token de Webpay es obligatorio");
        }

        Transaccion transaccion = transaccionRepository.findByTokenWebpay(token)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "No existe una transacción para el token recibido"));

        try {
            WebpayCommitResponse respuesta = webpayClient.put()
                    .uri("/rswebpaytransaction/api/webpay/v1.2/transactions/{token}", token)
                    .retrieve()
                    .onStatus(status -> status.isError(), response ->
                            response.bodyToMono(String.class)
                                    .map(body -> new ResponseStatusException(BAD_GATEWAY,
                                            "Webpay rechazó la confirmación: " + body)))
                    .bodyToMono(WebpayCommitResponse.class)
                    .block();

            if (respuesta == null) {
                throw new ResponseStatusException(BAD_GATEWAY, "Webpay no retornó información de confirmación");
            }

            boolean pagado = Integer.valueOf(0).equals(respuesta.responseCode())
                    && "AUTHORIZED".equalsIgnoreCase(respuesta.status());

            transaccion.setEstado(pagado ? "PAGADO" : "NO PAGADO");
            transaccion.setResponseCode(respuesta.responseCode());
            transaccion.setAuthorizationCode(respuesta.authorizationCode());
            transaccion.setFechaActualizacion(LocalDateTime.now());
            return transaccionRepository.save(transaccion);
        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new ResponseStatusException(BAD_GATEWAY, "No fue posible confirmar el pago con Webpay", exception);
        }
    }

    private void validarDatosCreacion(BigDecimal monto, String urlRetorno) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "El monto debe ser mayor que cero");
        }
        if (urlRetorno == null || urlRetorno.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "La URL de retorno es obligatoria");
        }
        try {
            URI uri = URI.create(urlRetorno);
            if (!("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(BAD_REQUEST, "La URL de retorno no es válida");
        }
    }
}
