package com.bank.recebimentos.query.adapter.http;

import com.bank.recebimentos.query.adapter.persistence.BoletoReadModel;
import com.bank.recebimentos.query.adapter.persistence.BoletoReadModelRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/consultas/boletos")
public class BoletoQueryController {
    private final BoletoReadModelRepository repository;

    public BoletoQueryController(BoletoReadModelRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<BoletoReadModel> listar(@RequestParam(value = "status", required = false) String status) {
        if (status == null || status.isBlank()) {
            return repository.findAll();
        }
        return repository.findByStatus(status);
    }

    @GetMapping("/{boletoId}")
    public ResponseEntity<BoletoReadModel> obter(@PathVariable("boletoId") UUID boletoId) {
        return repository.findById(boletoId)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
