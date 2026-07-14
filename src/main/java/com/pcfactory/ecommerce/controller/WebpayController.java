package com.pcfactory.ecommerce.controller;

import com.pcfactory.ecommerce.dto.webpay.WebpayCreateResponse;
import com.pcfactory.ecommerce.model.Transaccion;
import com.pcfactory.ecommerce.service.WebpayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/webpay")
public class WebpayController {

    private final WebpayService webpayService;

    public WebpayController(WebpayService webpayService) {
        this.webpayService = webpayService;
    }

    /**
     * Endpoint solicitado por la pauta: crea una transacción en Webpay Plus,
     * persiste el registro como CREADO y retorna el token y la URL reales.
     */
    @GetMapping("/crear")
    public ResponseEntity<WebpayCreateResponse> crearTransaccion(
            @RequestParam BigDecimal monto,
            @RequestParam String urlRetorno) {
        return ResponseEntity.ok(webpayService.iniciarPago(monto, urlRetorno));
    }

    /**
     * Webpay envía token_ws a la URL de retorno. El backend confirma el pago
     * directamente con Transbank y actualiza el estado a PAGADO o NO PAGADO.
     */
    @PostMapping("/confirmar")
    public ResponseEntity<Transaccion> confirmarTransaccion(
            @RequestParam(name = "token_ws", required = false) String tokenWs,
            @RequestParam(name = "token", required = false) String tokenAlternativo) {

        String token = tokenWs != null && !tokenWs.isBlank() ? tokenWs : tokenAlternativo;
        return ResponseEntity.ok(webpayService.finalizarPago(token));
    }
}
