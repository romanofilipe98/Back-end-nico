package br.com.comunicadornico.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import br.com.comunicadornico.dto.CriarBotaoUsuarioResponseDTO;
import br.com.comunicadornico.dto.CriarBotaoUsuarioDTO;
import br.com.comunicadornico.entity.entityAdministrador;
import br.com.comunicadornico.entity.entityBotao;
import br.com.comunicadornico.entity.entityBotaoCategoria;
import br.com.comunicadornico.entity.entityCategoria;
import br.com.comunicadornico.entity.entityUsuario;
import br.com.comunicadornico.repository.repositoryBotao;
import br.com.comunicadornico.repository.repositoryBotaoCategoria;
import br.com.comunicadornico.repository.repositoryCategoria;
import org.springframework.web.multipart.MultipartFile;
import br.com.comunicadornico.service.ServiceCloudinaryStorage.ResultadoUpload;

@Service
public class serviceCriarBotaoUsuario {

    private final repositoryBotao repositoryBotao;
    private final repositoryCategoria repositoryCategoria;
    private final repositoryBotaoCategoria repositoryBotaoCategoria;
    private final serviceUsuario usuarioService;
    private final ServiceCloudinaryStorage cloudinaryStorage;

    public serviceCriarBotaoUsuario(
            repositoryBotao repositoryBotao,
            repositoryCategoria repositoryCategoria,
            repositoryBotaoCategoria repositoryBotaoCategoria,
            serviceUsuario usuarioService,
            ServiceCloudinaryStorage cloudinaryStorage) {

        this.repositoryBotao = repositoryBotao;
        this.repositoryCategoria
                = repositoryCategoria;

        this.repositoryBotaoCategoria
                = repositoryBotaoCategoria;

        this.usuarioService
                = usuarioService;

        this.cloudinaryStorage
                = cloudinaryStorage;
    }

    @Transactional
    public CriarBotaoUsuarioResponseDTO criar(
            CriarBotaoUsuarioDTO dados,
            MultipartFile foto,
            MultipartFile audioPersonalizado,
            String emailAdministrador) {

        validarDados(dados);

        if (foto == null || foto.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A foto do botão é obrigatória."
            );
        }

        if (audioPersonalizado == null
                || audioPersonalizado.isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O áudio personalizado é obrigatório."
            );
        }

        entityUsuario usuario
                = usuarioService
                        .validarUsuarioDoAdministrador(
                                dados.getIdUsuario(),
                                emailAdministrador
                        );

        entityAdministrador administrador
                = usuario.getAdministrador();

        if (administrador == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O usuário não possui administrador vinculado."
            );
        }

        entityCategoria categoria
                = repositoryCategoria
                        .findById(
                                dados.getIdCategoria()
                        )
                        .orElseThrow(()
                                -> new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Categoria não encontrada."
                                )
                        );

        validarCategoriaDisponivel(
                categoria,
                usuario,
                administrador
        );

        ResultadoUpload resultadoFoto = null;
        ResultadoUpload resultadoAudio = null;

        try {
            /*
         * O botão é criado primeiro para obter seu ID.
         * A transação ainda não foi confirmada.
             */
            entityBotao botao
                    = new entityBotao();

            botao.setLegenda(
                    dados.getLegenda().trim()
            );

            botao.setCorFundo(
                    dados.getCorFundo().trim()
            );

            botao.setTamanhoBotao(
                    dados.getTamanhoBotao()
            );

            botao.setOculto(false);
            botao.setUsuario(usuario);
            botao.setAdministrador(administrador);

            /*
         * Botões personalizados não possuem
         * áudio feminino e masculino padrão.
             */
            botao.setAudioFem(null);
            botao.setAudioFemPublicId(null);
            botao.setAudioMas(null);
            botao.setAudioMasPublicId(null);

            entityBotao botaoSalvo
                    = repositoryBotao.saveAndFlush(
                            botao
                    );

            Long idBotao
                    = botaoSalvo.getIdBotao();

            /*
         * Upload da imagem.
             */
            resultadoFoto
                    = cloudinaryStorage.uploadImagem(
                            foto,
                            "nico/usuarios/"
                            + usuario.getIdUsuario()
                            + "/botoes/"
                            + idBotao
                            + "/imagem"
                    );

            /*
         * Upload do áudio personalizado.
             */
            resultadoAudio
                    = cloudinaryStorage.uploadAudio(
                            audioPersonalizado,
                            "nico/usuarios/"
                            + usuario.getIdUsuario()
                            + "/botoes/"
                            + idBotao
                            + "/audio"
                    );

            botaoSalvo.setFoto(
                    resultadoFoto.url()
            );

            botaoSalvo.setFotoPublicId(
                    resultadoFoto.publicId()
            );

            botaoSalvo.setAudioPersonalizado(
                    resultadoAudio.url()
            );

            botaoSalvo.setAudioPersonalizadoPublicId(
                    resultadoAudio.publicId()
            );

            botaoSalvo
                    = repositoryBotao.saveAndFlush(
                            botaoSalvo
                    );

            /*
         * Associação com a categoria.
             */
            entityBotaoCategoria relacao
                    = new entityBotaoCategoria();

            relacao.setBotao(botaoSalvo);
            relacao.setCategoria(categoria);
            relacao.setPosicao(
                    dados.getPosicao()
            );

            entityBotaoCategoria relacaoSalva
                    = repositoryBotaoCategoria
                            .saveAndFlush(
                                    relacao
                            );

            return CriarBotaoUsuarioResponseDTO.de(
                    botaoSalvo,
                    categoria,
                    relacaoSalva.getPosicao()
            );
        } catch (RuntimeException exception) {
            /*
         * O banco será revertido pelo @Transactional.
         * Os arquivos no Cloudinary precisam ser
         * apagados manualmente.
             */
            if (resultadoFoto != null) {
                try {
                    cloudinaryStorage.excluirImagem(
                            resultadoFoto.publicId()
                    );
                } catch (RuntimeException erroExclusao) {
                    System.err.println(
                            "Não foi possível remover a imagem após falha: "
                            + erroExclusao.getMessage()
                    );
                }
            }

            if (resultadoAudio != null) {
                try {
                    cloudinaryStorage.excluirAudio(
                            resultadoAudio.publicId()
                    );
                } catch (RuntimeException erroExclusao) {
                    System.err.println(
                            "Não foi possível remover o áudio após falha: "
                            + erroExclusao.getMessage()
                    );
                }
            }

            throw exception;
        }
    }

    private void validarDados(
            CriarBotaoUsuarioDTO dados) {

        if (dados == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Os dados do botão são obrigatórios."
            );
        }

        if (dados.getLegenda() == null
                || dados.getLegenda()
                        .isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A legenda do botão é obrigatória."
            );
        }

        if (dados.getCorFundo() == null
                || dados.getCorFundo()
                        .isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A cor de fundo do botão é obrigatória."
            );
        }

        if (!dados.getCorFundo().matches(
                "^#[0-9A-Fa-f]{6}$"
        )) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A cor de fundo deve estar no formato hexadecimal, por exemplo #B3B4B7."
            );
        }

        if (dados.getIdUsuario() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O usuário é obrigatório."
            );
        }

        if (dados.getIdCategoria() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A categoria é obrigatória."
            );
        }

        if (dados.getTamanhoBotao() < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O tamanho do botão deve ser maior que zero."
            );
        }

        if (dados.getPosicao() < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A posição do botão deve ser maior que zero."
            );
        }
    }

    private void validarCategoriaDisponivel(
            entityCategoria categoria,
            entityUsuario usuario,
            entityAdministrador administrador) {

        /*
         * Categoria padrão:
         * sem usuário e sem administrador.
         */
        boolean categoriaGlobal
                = categoria.getUsuario() == null
                && categoria.getAdministrador() == null;

        if (categoriaGlobal) {
            return;
        }

        /*
         * Categoria exclusiva do mesmo usuário.
         */
        if (categoria.getUsuario() != null
                && categoria.getUsuario()
                        .getIdUsuario()
                        .equals(
                                usuario.getIdUsuario()
                        )) {

            return;
        }

        /*
         * Categoria pertencente ao mesmo
         * administrador autenticado.
         */
        if (categoria.getAdministrador() != null
                && categoria.getAdministrador()
                        .getIdAdministrador()
                        .equals(
                                administrador
                                        .getIdAdministrador()
                        )) {

            return;
        }

        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "A categoria selecionada não está disponível para este usuário."
        );
    }
}
