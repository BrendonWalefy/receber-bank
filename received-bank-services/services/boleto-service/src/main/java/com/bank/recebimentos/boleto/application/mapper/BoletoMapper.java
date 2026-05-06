package com.bank.recebimentos.boleto.application.mapper;

import com.bank.recebimentos.boleto.application.dto.CriarBoletoResponse;
import com.bank.recebimentos.boleto.domain.Boleto;
import org.springframework.stereotype.Component;

@Component
public class BoletoMapper {
    public CriarBoletoResponse toBoletoResponse(Boleto boleto) {
        return new CriarBoletoResponse(
            boleto.getBoletoId().getValue().toString(),
            boleto.getCodigoBarras().getValor(),
            boleto.getLinhaDigitavel().getFormatada(),
            boleto.getValor().getValor(),
            boleto.getVencimento(),
            boleto.getStatus().name()
        );
    }
}
