package br.com.comunicadornico.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.comunicadornico.dto.ExcluirRegistroUsuarioResponseDTO;
import br.com.comunicadornico.entity.entityCategoria;
import br.com.comunicadornico.entity.entityCategoriaPersonalizacao;
import br.com.comunicadornico.entity.entityUsuario;
import br.com.comunicadornico.repository.repositoryBotaoCategoria;
import br.com.comunicadornico.repository.repositoryCategoria;
import br.com.comunicadornico.repository.repositoryCategoriaPersonalizacao;

@Service
public class serviceExcluirCategoriaUsuario {

    private final repositoryCategoria
            repositoryCategoria;

    private final repositoryBotaoCategoria
            repositoryBotaoCategoria;

    private final repositoryCategoriaPersonalizacao
            repositoryPersonalizacao;

    private final serviceUsuario
            usuarioService;

    private final ServiceCloudinaryStorage
            cloudinaryStorage;

    public serviceExcluirCategoriaUsuario(
            repositoryCategoria repositoryCategoria,
            repositoryBotaoCategoria
                    repositoryBotaoCategoria,
            repositoryCategoriaPersonalizacao
                    repositoryPersonalizacao,
            serviceUsuario usuarioService,
            ServiceCloudinaryStorage
                    cloudinaryStorage) {

        this.repositoryCategoria =
                repositoryCategoria;

        this.repositoryBotaoCategoria =
                repositoryBotaoCategoria;

        this.repositoryPersonalizacao =
                repositoryPersonalizacao;

        this.usuarioService =
                usuarioService;

        this.cloudinaryStorage =
                cloudinaryStorage;
    }

    @Transactional
    public ExcluirRegistroUsuarioResponseDTO excluir(
            Long idCategoria,
            Long idUsuario,
            String emailAdministrador) {

        entityUsuario usuario =
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

        boolean categoriaPadrao =
                categoria.getUsuario() == null
                        && categoria
                                .getAdministrador()
                                == null;

        if (categoriaPadrao) {
            return ocultarCategoriaPadrao(
                    categoria,
                    usuario
            );
        }

        validarPropriedade(
                categoria,
                usuario
        );

        return excluirCategoriaPersonalizada(
                categoria
        );
    }

    private ExcluirRegistroUsuarioResponseDTO
            ocultarCategoriaPadrao(
                    entityCategoria categoria,
                    entityUsuario usuario) {

        entityCategoriaPersonalizacao
                personalizacao =
                repositoryPersonalizacao
                        .findByUsuarioIdUsuarioAndCategoriaIdCategoria(
                                usuario.getIdUsuario(),
                                categoria.getIdCategoria()
                        )
                        .orElseGet(() -> {
                            entityCategoriaPersonalizacao nova =
                                    new entityCategoriaPersonalizacao();

                            nova.setUsuario(
                                    usuario
                            );

                            nova.setCategoria(
                                    categoria
                            );

                            return nova;
                        });

        personalizacao.setOculto(
                true
        );

        repositoryPersonalizacao
                .saveAndFlush(
                        personalizacao
                );

        return new ExcluirRegistroUsuarioResponseDTO(
                categoria.getIdCategoria(),
                "CATEGORIA",
                "OCULTADA",
                "Categoria padrão removida do comunicador do usuário."
        );
    }

    private ExcluirRegistroUsuarioResponseDTO
            excluirCategoriaPersonalizada(
                    entityCategoria categoria) {

        Long idCategoria =
                categoria.getIdCategoria();

        String fotoPublicId =
                categoria
                        .getFotoCatPublicId();

        repositoryBotaoCategoria
                .deleteByCategoriaIdCategoria(
                        idCategoria
                );

        repositoryPersonalizacao
                .deleteByCategoriaIdCategoria(
                        idCategoria
                );

        repositoryCategoria.delete(
                categoria
        );

        repositoryCategoria.flush();

        excluirImagem(
                fotoPublicId
        );

        return new ExcluirRegistroUsuarioResponseDTO(
                idCategoria,
                "CATEGORIA",
                "EXCLUIDA",
                "Categoria personalizada excluída permanentemente."
        );
    }

    private void validarPropriedade(
            entityCategoria categoria,
            entityUsuario usuario) {

        if (categoria.getUsuario() == null
                || !categoria
                        .getUsuario()
                        .getIdUsuario()
                        .equals(
                                usuario.getIdUsuario()
                        )) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Esta categoria não pertence ao usuário selecionado."
            );
        }
    }

    private void excluirImagem(
            String publicId) {

        if (publicId == null
                || publicId.isBlank()) {

            return;
        }

        try {
            cloudinaryStorage
                    .excluirImagem(
                            publicId
                    );
        } catch (RuntimeException erro) {
            System.err.println(
                    "Não foi possível excluir a imagem "
                            + publicId
                            + ": "
                            + erro.getMessage()
            );
        }
    }
}