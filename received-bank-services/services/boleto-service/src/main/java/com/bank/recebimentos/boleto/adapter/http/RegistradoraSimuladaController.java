package com.bank.recebimentos.boleto.adapter.http;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/simulacoes/registradora/boletos")
public class RegistradoraSimuladaController {
    @PostMapping
    public ResponseEntity<RegistrarBoletoSimuladoResponse> registrar(
        @RequestBody RegistrarBoletoSimuladoRequest request
    ) {
        return ResponseEntity.accepted().body(new RegistrarBoletoSimuladoResponse(
            request.boletoId(),
            "REG-" + UUID.randomUUID(),
            "REGISTRADO",
            Instant.now()
        ));
    }
}
