CREATE TABLE boletos (
    id UUID PRIMARY KEY,
    codigo_barras VARCHAR(47) NOT NULL UNIQUE,
    linha_digitavel VARCHAR(47) NOT NULL UNIQUE,
    beneficiario_cpf_cnpj VARCHAR(14) NOT NULL,
    beneficiario_nome VARCHAR(150) NOT NULL,
    beneficiario_banco VARCHAR(3) NOT NULL,
    pagador_cpf_cnpj VARCHAR(14) NOT NULL,
    pagador_nome VARCHAR(150) NOT NULL,
    pagador_endereco VARCHAR(255) NOT NULL,
    pagador_cep VARCHAR(8) NOT NULL,
    valor NUMERIC(19, 2) NOT NULL,
    vencimento DATE NOT NULL,
    status VARCHAR(30) NOT NULL,
    numero_sequencial VARCHAR(50),
    descricao VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_boletos_status ON boletos (status);
CREATE INDEX idx_boletos_vencimento ON boletos (vencimento);
