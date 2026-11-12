package br.com.comunicadornico.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.comunicadornico.dto.BotaoCategoriaOrdemDTO;
import br.com.comunicadornico.dto.CategoriaResolvidaDTO;
import br.com.comunicadornico.entity.entityCategoria;
import br.com.comunicadornico.entity.entityCategoriaPersonalizacao;
import br.com.comunicadornico.repository.repositoryCategoria;
import br.com.comunicadornico.repository.repositoryCategoriaPersonalizacao;

@Service
public class ServiceCategoriaResolvida {

    private final repositoryCategoria
            repositoryCategoria;

    private final repositoryCategoriaPersonalizacao
            repositoryPersonalizacao;

    private final serviceUsuario
            usuarioService;

    public ServiceCategoriaResolvida(
            repositoryCategoria repositoryCategoria,
            repositoryCategoriaPersonalizacao
                    repositoryPersonalizacao,
            serviceUsuario usuarioService) {

        this.repositoryCategoria =
                repositoryCategoria;

        this.repositoryPersonalizacao =
                repositoryPersonalizacao;

        this.usuarioService =
                usuarioService;
    }

    @Transactional(readOnly = true)
    public List<CategoriaResolvidaDTO>
            listarCategoriasResolvidas(
                    Long idUsuario,
                    String emailAdministrador) {

        usuarioService
                .validarUsuarioDoAdministrador(
                        idUsuario,
                        emailAdministrador
                );

        List<entityCategoria> categorias =
                repositoryCategoria
                        .findCategoriasDisponiveisParaUsuario(
                                idUsuario
                        );

        Map<Long, entityCategoriaPersonalizacao>
                personalizacaoPorCategoria =
                repositoryPersonalizacao
                        .findByUsuarioIdUsuario(
                                idUsuario
                        )
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        personalizacao ->
                                                personalizacao
                                                        .getCategoria()
                                                        .getIdCategoria(),
                                        Function.identity()
                                )
                        );

        return categorias
                .stream()
                .map(categoria ->
                        resolverCategoria(
                                categoria,
                                personalizacaoPorCategoria
                                        .get(
                                                categoria
                                                        .getIdCategoria()
                                        ),
                                idUsuario
                        )
                )
                .sorted(
                        (
                                primeira,
                                segunda
                        ) -> Integer.compare(
                                primeira.posicao(),
                                segunda.posicao()
                        )
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResolvidaDTO
            pesquisarCategoriaResolvida(
                    Long idUsuario,
                    Long idCategoria,
                    String emailAdministrador) {

        usuarioService
                .validarUsuarioDoAdministrador(
                        idUsuario,
                        emailAdministrador
                );

        entityCategoria categoria =
                repositoryCategoria
                        .findById(idCategoria)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Categoria não encontrada: "
                                                + idCategoria
                                )
                        );

        validarDisponibilidadeDaCategoria(
                categoria,
                idUsuario
        );

        entityCategoriaPersonalizacao
                personalizacao =
                repositoryPersonalizacao
                        .findByUsuarioIdUsuarioAndCategoriaIdCategoria(
                                idUsuario,
                                idCategoria
                        )
                        .orElse(null);

        return resolverCategoria(
                categoria,
                personalizacao,
                idUsuario
        );
    }

    private CategoriaResolvidaDTO
            resolverCategoria(
                    entityCategoria categoria,
                    entityCategoriaPersonalizacao
                            personalizacao,
                    Long idUsuario) {

        String nomeResolvido =
                resolverTexto(
                        personalizacao != null
                                ? personalizacao
                                        .getNome()
                                : null,
                        categoria.getNome()
                );

        String fotoResolvida =
                resolverTexto(
                        personalizacao != null
                                ? personalizacao
                                        .getFoto()
                                : null,
                        categoria.getFotoCat()
                );

        boolean ocultoResolvido =
                personalizacao != null
                        && personalizacao
                                .getOculto()
                                != null
                        ? personalizacao
                                .getOculto()
                        : false;

        boolean personalizada =
                possuiPersonalizacao(
                        personalizacao
                );

        boolean exclusivaUsuario =
                categoria.getUsuario() != null
                        && categoria
                                .getUsuario()
                                .getIdUsuario()
                                .equals(idUsuario);

        List<BotaoCategoriaOrdemDTO>
                botoesOrdenados =
                resolverBotoesOrdenados(
                        categoria
                );

        return new CategoriaResolvidaDTO(
                categoria.getIdCategoria(),
                nomeResolvido,
                fotoResolvida,
                categoria.getPosicaoCat(),
                ocultoResolvido,
                personalizada,
                exclusivaUsuario,
                botoesOrdenados
        );
    }

    private List<BotaoCategoriaOrdemDTO>
            resolverBotoesOrdenados(
                    entityCategoria categoria) {

        if (categoria.getBotaoCategorias()
                == null) {

            return List.of();
        }

        return categoria
                .getBotaoCategorias()
                .stream()
                .filter(relacao ->
                        relacao.getBotao()
                                != null
                )
                .sorted(
                        (
                                primeira,
                                segunda
                        ) -> Integer.compare(
                                primeira.getPosicao(),
                                segunda.getPosicao()
                        )
                )
                .map(relacao -> {
                    BotaoCategoriaOrdemDTO dto =
                            new BotaoCategoriaOrdemDTO();

                    dto.setIdBotao(
                            relacao
                                    .getBotao()
                                    .getIdBotao()
                    );

                    dto.setPosicao(
                            relacao.getPosicao()
                    );

                    return dto;
                })
                .toList();
    }

    private boolean possuiPersonalizacao(
            entityCategoriaPersonalizacao
                    personalizacao) {

        if (personalizacao == null) {
            return false;
        }

        return possuiTexto(
                personalizacao.getNome()
        )
                || possuiTexto(
                        personalizacao.getFoto()
                )
                || personalizacao
                        .getOculto()
                        != null;
    }

    private String resolverTexto(
            String valorPersonalizado,
            String valorPadrao) {

        if (possuiTexto(
                valorPersonalizado
        )) {
            return valorPersonalizado;
        }

        return valorPadrao;
    }

    private void validarDisponibilidadeDaCategoria(
            entityCategoria categoria,
            Long idUsuario) {

        if (categoria.getUsuario() == null) {
            return;
        }

        if (!categoria
                .getUsuario()
                .getIdUsuario()
                .equals(idUsuario)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Esta categoria não pertence ao comunicador do usuário."
            );
        }
    }

    private boolean possuiTexto(
            String valor) {

        return valor != null
                && !valor.isBlank();
    }
}