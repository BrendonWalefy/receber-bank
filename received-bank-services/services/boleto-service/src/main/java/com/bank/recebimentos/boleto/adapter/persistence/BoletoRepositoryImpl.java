package com.bank.recebimentos.boleto.adapter.persistence;

import com.bank.recebimentos.boleto.domain.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class BoletoRepositoryImpl implements BoletoRepository {
    private final BoletoSpringDataRepository springDataRepository;

    public BoletoRepositoryImpl(BoletoSpringDataRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public void salvar(Boleto boleto) {
        BoletoJpaEntity entity = new BoletoJpaEntity();
        entity.setId(boleto.getBoletoId().getValue());
        entity.setCodigoBarras(boleto.getCodigoBarras().getValor());
        entity.setLinhaDigitavel(boleto.getLinhaDigitavel().getValor());
        entity.setBeneficiarioCpfCnpj(boleto.getBeneficiario().getCpfCnpj());
        entity.setBeneficiarioNome(boleto.getBeneficiario().getNome());
        entity.setBeneficiarioBanco(boleto.getBeneficiario().getBanco());
        entity.setPagadorCpfCnpj(boleto.getPagador().getCpfCnpj());
        entity.setPagadorNome(boleto.getPagador().getNome());
        entity.setPagadorEndereco(boleto.getPagador().getEndereco());
        entity.setPagadorCep(boleto.getPagador().getCep());
        entity.setValor(boleto.getValor().getValor());
        entity.setVencimento(boleto.getVencimento());
        entity.setStatus(boleto.getStatus().name());
        entity.setNumeroSequencial(boleto.getNumeroSequencial());
        entity.setDescricao(boleto.getDescricao());

        springDataRepository.save(entity);
    }

    @Override
    public Optional<Boleto> buscarPorId(BoletoId boletoId) {
        return springDataRepository.findById(boletoId.getValue())
            .map(this::toDomain);
    }

    @Override
    public Optional<Boleto> buscarPorCodigoBarras(String codigoBarras) {
        return springDataRepository.findByCodigoBarras(codigoBarras)
            .map(this::toDomain);
    }

    @Override
    public Optional<Boleto> buscarPorLinhaDigitavel(String linhaDigitavel) {
        return springDataRepository.findByLinhaDigitavel(linhaDigitavel)
            .map(this::toDomain);
    }

    private Boleto toDomain(BoletoJpaEntity entity) {
        BoletoId boletoId = new BoletoId(entity.getId());
        Beneficiario beneficiario = new Beneficiario(
            entity.getBeneficiarioCpfCnpj(),
            entity.getBeneficiarioNome(),
            entity.getBeneficiarioBanco()
        );
        Pagador pagador = new Pagador(
            entity.getPagadorCpfCnpj(),
            entity.getPagadorNome(),
            entity.getPagadorEndereco(),
            entity.getPagadorCep()
        );
        Moeda valor = Moeda.brl(entity.getValor());
        CodigoBarras codigoBarras = new CodigoBarras(entity.getCodigoBarras());
        LinhaDigitavel linhaDigitavel = new LinhaDigitavel(entity.getLinhaDigitavel());

        return Boleto.reconstituir(
            boletoId,
            beneficiario,
            pagador,
            valor,
            entity.getVencimento(),
            codigoBarras,
            linhaDigitavel,
            entity.getDescricao(),
            BoletoStatus.valueOf(entity.getStatus()),
            entity.getNumeroSequencial()
        );
    }
}
