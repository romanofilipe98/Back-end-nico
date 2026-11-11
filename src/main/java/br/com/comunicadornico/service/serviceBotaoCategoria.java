package br.com.comunicadornico.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.comunicadornico.dto.BotaoCategoriaDTO;
import br.com.comunicadornico.entity.entityBotao;
import br.com.comunicadornico.entity.entityBotaoCategoria;
import br.com.comunicadornico.entity.entityCategoria;
import br.com.comunicadornico.repository.repositoryBotao;
import br.com.comunicadornico.repository.repositoryBotaoCategoria;
import br.com.comunicadornico.repository.repositoryCategoria;

@Service
public class serviceBotaoCategoria {

    private final repositoryBotaoCategoria repository;
    private final repositoryBotao repositoryBotao;
    private final repositoryCategoria repositoryCategoria;

    public serviceBotaoCategoria(
            repositoryBotaoCategoria repository,
            repositoryBotao repositoryBotao,
            repositoryCategoria repositoryCategoria) {

        this.repository = repository;
        this.repositoryBotao =
                repositoryBotao;
        this.repositoryCategoria =
                repositoryCategoria;
    }

    public entityBotaoCategoria associar(
            BotaoCategoriaDTO dados) {

        if (dados.getIdBotao() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O botão é obrigatório."
            );
        }

        if (dados.getIdCategoria() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A categoria é obrigatória."
            );
        }

        if (dados.getPosicao() < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A posição deve ser maior que zero."
            );
        }

        entityBotao botao =
                repositoryBotao
                        .findById(
                                dados.getIdBotao()
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Botão não encontrado."
                                )
                        );

        entityCategoria categoria =
                repositoryCategoria
                        .findById(
                                dados.getIdCategoria()
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Categoria não encontrada."
                                )
                        );

        entityBotaoCategoria relacao =
                repository
                        .findByBotaoIdBotaoAndCategoriaIdCategoria(
                                dados.getIdBotao(),
                                dados.getIdCategoria()
                        )
                        .orElseGet(
                                entityBotaoCategoria::new
                        );

        relacao.setBotao(botao);
        relacao.setCategoria(categoria);
        relacao.setPosicao(
                dados.getPosicao()
        );

        return repository.save(relacao);
    }

    public List<entityBotaoCategoria>
            listarPorCategoria(
                    Long idCategoria) {

        return repository
                .findByCategoriaIdCategoriaOrderByPosicaoAsc(
                        idCategoria
                );
    }

    public void excluir(Long idRelacao) {
        entityBotaoCategoria relacao =
                repository
                        .findById(idRelacao)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Relação não encontrada."
                                )
                        );

        repository.delete(relacao);
    }
}