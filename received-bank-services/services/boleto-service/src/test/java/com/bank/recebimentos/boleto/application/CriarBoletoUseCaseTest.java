package com.bank.recebimentos.boleto.application;

import com.bank.recebimentos.boleto.application.dto.CriarBoletoCommand;
import com.bank.recebimentos.boleto.application.port.BoletoEventPublisher;
import com.bank.recebimentos.boleto.domain.Boleto;
import com.bank.recebimentos.boleto.domain.BoletoId;
import com.bank.recebimentos.boleto.domain.BoletoRepository;
import com.bank.recebimentos.boleto.domain.events.BoletoGeradoEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CriarBoletoUseCaseTest {
    @Test
    void deveSalvarBoletoEPublicarEvento() {
        InMemoryBoletoRepository repository = new InMemoryBoletoRepository();
        CapturingBoletoEventPublisher publisher = new CapturingBoletoEventPublisher();
        CriarBoletoUseCase useCase = new CriarBoletoUseCase(repository, publisher);

        String boletoId = useCase.executar(new CriarBoletoCommand(
            "12345678901",
            "Empresa Beneficiaria",
            "341",
            "10987654321",
            "Cliente Pagador",
            "Rua Teste, 100",
            "01001000",
            new BigDecimal("99.90"),
            LocalDate.now().plusDays(3),
            "Compra online"
        ));

        assertTrue(repository.buscarPorId(new BoletoId(java.util.UUID.fromString(boletoId))).isPresent());
        assertEquals(1, publisher.eventos.size());
        assertEquals(boletoId, publisher.eventos.get(0).getBoletoId());
    }

    private static class InMemoryBoletoRepository implements BoletoRepository {
        private final Map<BoletoId, Boleto> boletos = new HashMap<>();

        @Override
        public void salvar(Boleto boleto) {
            boletos.put(boleto.getBoletoId(), boleto);
        }

        @Override
        public Optional<Boleto> buscarPorId(BoletoId boletoId) {
            return Optional.ofNullable(boletos.get(boletoId));
        }

        @Override
        public Optional<Boleto> buscarPorCodigoBarras(String codigoBarras) {
            return boletos.values().stream()
                .filter(boleto -> boleto.getCodigoBarras().getValor().equals(codigoBarras))
                .findFirst();
        }

        @Override
        public Optional<Boleto> buscarPorLinhaDigitavel(String linhaDigitavel) {
            return boletos.values().stream()
                .filter(boleto -> boleto.getLinhaDigitavel().getValor().equals(linhaDigitavel))
                .findFirst();
        }
    }

    private static class CapturingBoletoEventPublisher implements BoletoEventPublisher {
        private final List<BoletoGeradoEvent> eventos = new ArrayList<>();

        @Override
        public void publicarBoletoGerado(BoletoGeradoEvent evento) {
            eventos.add(evento);
        }
    }
}
