package com.bank.recebimentos.boleto.domain;

import java.util.Objects;
import java.util.UUID;

public final class BoletoId {
    private final UUID value;

    public BoletoId(UUID value) {
        this.value = Objects.requireNonNull(value, "BoletoId cannot be null");
    }

    public static BoletoId generate() {
        return new BoletoId(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoletoId boletoId = (BoletoId) o;
        return Objects.equals(value, boletoId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
