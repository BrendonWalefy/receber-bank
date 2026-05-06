package com.bank.recebimentos.query.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BoletoReadModelRepository extends JpaRepository<BoletoReadModel, UUID> {
    List<BoletoReadModel> findByStatus(String status);
}
