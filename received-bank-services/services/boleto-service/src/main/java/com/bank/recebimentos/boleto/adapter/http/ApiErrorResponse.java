package com.bank.recebimentos.boleto.adapter.http;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(Instant timestamp, int status, String message, List<String> errors) {
}
