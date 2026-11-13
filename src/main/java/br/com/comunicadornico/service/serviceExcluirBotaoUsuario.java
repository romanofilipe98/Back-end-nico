package br.com.comunicadornico.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.comunicadornico.dto.ExcluirRegistroUsuarioResponseDTO;
import br.com.comunicadornico.entity.entityBotao;
import br.com.comunicadornico.entity.entityBotaoPersonalizacao;
import br.com.comunicadornico.entity.entityUsuario;
import br.com.comunicadornico.repository.repositoryBotao;
import br.com.comunicadornico.repository.repositoryBotaoCategoria;
import br.com.comunicadornico.repository.repositoryBotaoPersonalizacao;

@Service
public class serviceExcluirBotaoUsuario {

    private final repositoryBotao repositoryBotao;

    private final repositoryBotaoCategoria repositoryBotaoCategoria;

    private final repositoryBotaoPersonalizacao repositoryPersonalizacao;

    private final serviceUsuario usuarioService;

    private final ServiceCloudinaryStorage cloudinaryStorage;

    public serviceExcluirBotaoUsuario(
            repositoryBotao repositoryBotao,
            repositoryBotaoCategoria repositoryBotaoCategoria,
            repositoryBotaoPersonalizacao repositoryPersonalizacao,
            serviceUsuario usuarioService,
            ServiceCloudinaryStorage cloudinaryStorage) {

        this.repositoryBotao
                = repositoryBotao;

        this.repositoryBotaoCategoria
                = repositoryBotaoCategoria;

        this.repositoryPersonalizacao
                = repositoryPersonalizacao;

        this.usuarioService
                = usuarioService;

        this.cloudinaryStorage
                = cloudinaryStorage;
    }

    @Transactional
    public ExcluirRegistroUsuarioResponseDTO excluir(
            Long idBotao,
            Long idUsuario,
            String emailAdministrador) {

        entityUsuario usuario
                = usuarioService
                        .validarUsuarioDoAdministrador(
                                idUsuario,
                                emailAdministrador
                        );

        entityBotao botao
                = repositoryBotao
                        .findById(idBotao)
                        .orElseThrow(()
                                -> new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Botão não encontrado: "
                                        + idBotao
                                )
                        );

        boolean botaoPadrao
                = botao.getUsuario() == null
                && botao.getAdministrador()
                == null;

        if (botaoPadrao) {
            return ocultarBotaoPadrao(
                    botao,
                    usuario
            );
        }

        validarPropriedade(
                botao,
                usuario
        );

        return excluirBotaoPersonalizado(
                botao
        );
    }

    private ExcluirRegistroUsuarioResponseDTO
            ocultarBotaoPadrao(
                    entityBotao botao,
                    entityUsuario usuario) {

        entityBotaoPersonalizacao personalizacao
                = repositoryPersonalizacao
                        .findByUsuarioIdUsuarioAndBotaoIdBotao(
                                usuario.getIdUsuario(),
                                botao.getIdBotao()
                        )
                        .orElseGet(() -> {
                            entityBotaoPersonalizacao nova
                                    = new entityBotaoPersonalizacao();

                            nova.setUsuario(
                                    usuario
                            );

                            nova.setBotao(
                                    botao
                            );

                            return nova;
                        });

        personalizacao.setOculto(
                true
        );

        repositoryPersonalizacao.save(
                personalizacao
        );

        return new ExcluirRegistroUsuarioResponseDTO(
                botao.getIdBotao(),
                "BOTAO",
                "OCULTADO",
                "Botão padrão removido do comunicador do usuário."
        );
    }

    private ExcluirRegistroUsuarioResponseDTO
            excluirBotaoPersonalizado(
                    entityBotao botao) {

        Long idBotao
                = botao.getIdBotao();

        String fotoPublicId
                = botao.getFotoPublicId();

        String audioPublicId
                = botao
                        .getAudioPersonalizadoPublicId();

        repositoryBotaoCategoria
                .deleteByBotaoIdBotao(
                        idBotao
                );

        repositoryPersonalizacao
                .deleteByBotaoIdBotao(
                        idBotao
                );

        repositoryBotao
                .excluirRegistrosDeAtividadeDoBotao(
                        idBotao
                );

        repositoryBotao.delete(
                botao
        );

        repositoryBotao.flush();

        excluirImagem(
                fotoPublicId
        );

        excluirAudio(
                audioPublicId
        );

        return new ExcluirRegistroUsuarioResponseDTO(
                idBotao,
                "BOTAO",
                "EXCLUIDO",
                "Botão personalizado excluído permanentemente."
        );
    }

    private void validarPropriedade(
            entityBotao botao,
            entityUsuario usuario) {

        if (botao.getUsuario() == null
                || !botao
                        .getUsuario()
                        .getIdUsuario()
                        .equals(
                                usuario.getIdUsuario()
                        )) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Este botão não pertence ao usuário selecionado."
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

    private void excluirAudio(
            String publicId) {

        if (publicId == null
                || publicId.isBlank()) {

            return;
        }

        try {
            cloudinaryStorage
                    .excluirAudio(
                            publicId
                    );
        } catch (RuntimeException erro) {
            System.err.println(
                    "Não foi possível excluir o áudio "
                    + publicId
                    + ": "
                    + erro.getMessage()
            );
        }
    }
}
