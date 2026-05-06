package com.bank.recebimentos.boleto.domain;

import java.util.Optional;

public interface BoletoRepository {
    void salvar(Boleto boleto);

    Optional<Boleto> buscarPorId(BoletoId boletoId);

    Optional<Boleto> buscarPorCodigoBarras(String codigoBarras);

    Optional<Boleto> buscarPorLinhaDigitavel(String linhaDigitavel);
}
