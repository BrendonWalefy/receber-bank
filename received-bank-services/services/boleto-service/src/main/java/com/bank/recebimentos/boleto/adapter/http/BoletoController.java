package com.bank.recebimentos.boleto.adapter.http;

import com.bank.recebimentos.boleto.application.CriarBoletoUseCase;
import com.bank.recebimentos.boleto.application.dto.CriarBoletoCommand;
import com.bank.recebimentos.boleto.application.dto.CriarBoletoResponse;
import com.bank.recebimentos.boleto.application.mapper.BoletoMapper;
import com.bank.recebimentos.boleto.domain.BoletoId;
import com.bank.recebimentos.boleto.domain.BoletoRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/boletos")
public class BoletoController {
    private final CriarBoletoUseCase criarBoletoUseCase;
    private final BoletoRepository boletoRepository;
    private final BoletoMapper boletoMapper;

    public BoletoController(CriarBoletoUseCase criarBoletoUseCase,
                            BoletoRepository boletoRepository,
                            BoletoMapper boletoMapper) {
        this.criarBoletoUseCase = criarBoletoUseCase;
        this.boletoRepository = boletoRepository;
        this.boletoMapper = boletoMapper;
    }

    @PostMapping
    public ResponseEntity<CriarBoletoResponse> criar(@Valid @RequestBody CriarBoletoRequest request) {
        CriarBoletoCommand comando = new CriarBoletoCommand(
            request.getBeneficiarioCpfCnpj(),
            request.getBeneficiarioNome(),
            request.getBeneficiarioBanco(),
            request.getPagadorCpfCnpj(),
            request.getPagadorNome(),
            request.getPagadorEndereco(),
            request.getPagadorCep(),
            request.getValor(),
            request.getVencimento(),
            request.getDescricao()
        );

        String boletoId = criarBoletoUseCase.executar(comando);
        var boleto = boletoRepository.buscarPorId(new BoletoId(UUID.fromString(boletoId)))
            .orElseThrow(() -> new BoletoNaoEncontradoException(boletoId));

        return ResponseEntity
            .created(URI.create("/boletos/" + boletoId))
            .body(boletoMapper.toBoletoResponse(boleto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CriarBoletoResponse> obter(@PathVariable("id") UUID id) {
        var boleto = boletoRepository.buscarPorId(new BoletoId(id))
            .orElseThrow(() -> new BoletoNaoEncontradoException(id.toString()));
        return ResponseEntity.status(HttpStatus.OK).body(boletoMapper.toBoletoResponse(boleto));
    }
}
