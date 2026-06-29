package com.pcfactory.ecommerce.controller;

import com.pcfactory.ecommerce.model.Transaccion;
import com.pcfactory.ecommerce.service.WebpayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/webpay")
public class WebpayController {

    @Autowired
    private WebpayService webpayService;

    // 1. Servicio GET para crear una transacción y llamar a Webpay
    @GetMapping("/crear")
    public ResponseEntity<?> crearTransaccion(@RequestParam BigDecimal monto, @RequestParam String urlRetorno) {
        Map<String, Object> respuesta = webpayService.iniciarPago(monto, urlRetorno);
        if (respuesta != null) {
            return ResponseEntity.ok(respuesta);
        }
        return ResponseEntity.badRequest().body("No se pudo iniciar la transacción con Webpay.");
    }

    // 2. Endpoint POST donde se recibe el status de pago desde Webpay y se cambia el estado
    @PostMapping("/confirmar")
    public ResponseEntity<?> confirmarTransaccion(@RequestParam String token, @RequestParam String status) {
        Transaccion transaccionActualizada = webpayService.finalizarPago(token, status);
        if (transaccionActualizada != null) {
            return ResponseEntity.ok(transaccionActualizada);
        }
        return ResponseEntity.notFound().build();
    }
}