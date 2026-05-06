package com.bank.recebimentos.boleto.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BoletoSpringDataRepository extends JpaRepository<BoletoJpaEntity, UUID> {
    Optional<BoletoJpaEntity> findByCodigoBarras(String codigoBarras);

    Optional<BoletoJpaEntity> findByLinhaDigitavel(String linhaDigitavel);
}
