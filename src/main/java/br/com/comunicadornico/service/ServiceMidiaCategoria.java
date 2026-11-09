package br.com.comunicadornico.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import br.com.comunicadornico.dto.CategoriaResolvidaDTO;
import br.com.comunicadornico.entity.entityCategoria;
import br.com.comunicadornico.entity.entityCategoriaPersonalizacao;
import br.com.comunicadornico.entity.entityUsuario;
import br.com.comunicadornico.repository.repositoryCategoria;
import br.com.comunicadornico.repository.repositoryCategoriaPersonalizacao;
import br.com.comunicadornico.service.ServiceCloudinaryStorage.ResultadoUpload;

@Service
public class ServiceMidiaCategoria {

    private final repositoryCategoria repositoryCategoria;

    private final repositoryCategoriaPersonalizacao repositoryPersonalizacao;

    private final ServiceCloudinaryStorage cloudinaryStorage;

    private final serviceUsuario usuarioService;

    private final ServiceCategoriaResolvida categoriaResolvidaService;

    public ServiceMidiaCategoria(
            repositoryCategoria repositoryCategoria,
            repositoryCategoriaPersonalizacao repositoryPersonalizacao,
            ServiceCloudinaryStorage cloudinaryStorage,
            serviceUsuario usuarioService,
            ServiceCategoriaResolvida categoriaResolvidaService) {

        this.repositoryCategoria = repositoryCategoria;
        this.repositoryPersonalizacao
                = repositoryPersonalizacao;
        this.cloudinaryStorage = cloudinaryStorage;
        this.usuarioService = usuarioService;
        this.categoriaResolvidaService
                = categoriaResolvidaService;
    }

    @Transactional
    public entityCategoria atualizarFotoPadrao(
            Long idCategoria,
            MultipartFile foto) {

        if (!arquivoFoiEnviado(foto)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A foto da categoria é obrigatória."
            );
        }

        entityCategoria categoria
                = buscarCategoria(idCategoria);

        String publicIdAntigo
                = categoria.getFotoCatPublicId();

        ResultadoUpload novaFoto = null;

        try {
            novaFoto
                    = cloudinaryStorage.uploadImagem(
                            foto,
                            "nico/categorias/padrao/"
                            + idCategoria
                            + "/imagem"
                    );

            categoria.setFotoCat(
                    novaFoto.url()
            );

            categoria.setFotoCatPublicId(
                    novaFoto.publicId()
            );

            entityCategoria categoriaSalva
                    = repositoryCategoria
                            .saveAndFlush(categoria);

            excluirImagemSemInterromper(
                    publicIdAntigo
            );

            return categoriaSalva;

        } catch (RuntimeException exception) {

            if (novaFoto != null) {
                excluirImagemSemInterromper(
                        novaFoto.publicId()
                );
            }

            throw exception;
        }
    }

    @Transactional
    public CategoriaResolvidaDTO personalizarCategoria(
            Long idUsuario,
            Long idCategoria,
            MultipartFile foto,
            String emailAdministrador) {

        if (!arquivoFoiEnviado(foto)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A foto personalizada da categoria é obrigatória."
            );
        }

        entityUsuario usuario
                = usuarioService
                        .validarUsuarioDoAdministrador(
                                idUsuario,
                                emailAdministrador
                        );

        entityCategoria categoria
                = buscarCategoria(idCategoria);

        validarCategoriaDisponivelParaUsuario(
                categoria,
                idUsuario
        );

        entityCategoriaPersonalizacao personalizacao
                = repositoryPersonalizacao
                        .findByUsuarioIdUsuarioAndCategoriaIdCategoria(
                                idUsuario,
                                idCategoria
                        )
                        .orElseGet(() -> {
                            entityCategoriaPersonalizacao nova
                                    = new entityCategoriaPersonalizacao();

                            nova.setUsuario(usuario);
                            nova.setCategoria(categoria);

                            return nova;
                        });

        String publicIdAntigo
                = personalizacao.getFotoPublicId();

        ResultadoUpload novaFoto = null;

        try {
            novaFoto
                    = cloudinaryStorage.uploadImagem(
                            foto,
                            "nico/usuarios/"
                            + idUsuario
                            + "/categorias/"
                            + idCategoria
                            + "/imagem"
                    );

            personalizacao.setFoto(
                    novaFoto.url()
            );

            personalizacao.setFotoPublicId(
                    novaFoto.publicId()
            );

            repositoryPersonalizacao
                    .saveAndFlush(personalizacao);

            excluirImagemSemInterromper(
                    publicIdAntigo
            );

            return categoriaResolvidaService
                    .pesquisarCategoriaResolvida(
                            idUsuario,
                            idCategoria,
                            emailAdministrador
                    );

        } catch (RuntimeException exception) {

            if (novaFoto != null) {
                excluirImagemSemInterromper(
                        novaFoto.publicId()
                );
            }

            throw exception;
        }
    }

    private entityCategoria buscarCategoria(
            Long idCategoria) {

        return repositoryCategoria
                .findById(idCategoria)
                .orElseThrow(()
                        -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Categoria não encontrada: "
                                + idCategoria
                        )
                );
    }

    private void validarCategoriaDisponivelParaUsuario(
            entityCategoria categoria,
            Long idUsuario) {

        /*
         * Categoria sem usuário é uma categoria padrão,
         * disponível para todos os comunicadores.
         */
        if (categoria.getUsuario() == null) {
            return;
        }

        /*
         * Categoria vinculada diretamente a um usuário
         * só pode ser utilizada naquele comunicador.
         */
        if (!categoria.getUsuario()
                .getIdUsuario()
                .equals(idUsuario)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Esta categoria não está disponível "
                    + "para o comunicador do usuário."
            );
        }
    }

    private boolean arquivoFoiEnviado(
            MultipartFile arquivo) {

        return arquivo != null
                && !arquivo.isEmpty();
    }

    private void excluirImagemSemInterromper(
            String publicId) {

        if (publicId == null
                || publicId.isBlank()) {
            return;
        }

        try {
            cloudinaryStorage
                    .excluirImagem(publicId);

        } catch (RuntimeException exception) {

            System.err.println(
                    "Não foi possível excluir a imagem "
                    + publicId
                    + ": "
                    + exception.getMessage()
            );
        }
    }

    @Transactional
    public CategoriaResolvidaDTO removerPersonalizacaoCategoria(
            Long idUsuario,
            Long idCategoria,
            String emailAdministrador) {

        usuarioService.validarUsuarioDoAdministrador(
                idUsuario,
                emailAdministrador
        );

        entityCategoria categoria
                = buscarCategoria(idCategoria);

        validarCategoriaDisponivelParaUsuario(
                categoria,
                idUsuario
        );

        entityCategoriaPersonalizacao personalizacao
                = repositoryPersonalizacao
                        .findByUsuarioIdUsuarioAndCategoriaIdCategoria(
                                idUsuario,
                                idCategoria
                        )
                        .orElseThrow(()
                                -> new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "A categoria não possui personalização "
                                        + "para este comunicador."
                                )
                        );

        String fotoPublicId
                = personalizacao.getFotoPublicId();

        repositoryPersonalizacao.delete(personalizacao);
        repositoryPersonalizacao.flush();

        excluirImagemSemInterromper(fotoPublicId);

        return categoriaResolvidaService
                .pesquisarCategoriaResolvida(
                        idUsuario,
                        idCategoria,
                        emailAdministrador
                );
    }
}
