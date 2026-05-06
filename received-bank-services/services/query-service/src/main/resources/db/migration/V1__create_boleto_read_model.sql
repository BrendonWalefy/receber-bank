CREATE TABLE boleto_read_model (
    boleto_id UUID PRIMARY KEY,
    beneficiario_cpf_cnpj VARCHAR(14) NOT NULL,
    beneficiario_nome VARCHAR(150) NOT NULL,
    pagador_cpf_cnpj VARCHAR(14) NOT NULL,
    pagador_nome VARCHAR(150) NOT NULL,
    valor NUMERIC(19, 2) NOT NULL,
    vencimento DATE NOT NULL,
    codigo_barras VARCHAR(47) NOT NULL,
    linha_digitavel VARCHAR(47) NOT NULL,
    status VARCHAR(30) NOT NULL,
    pago_em TIMESTAMP,
    notificacao_enviada_em TIMESTAMP,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_boleto_read_model_status ON boleto_read_model (status);
CREATE INDEX idx_boleto_read_model_vencimento ON boleto_read_model (vencimento);
