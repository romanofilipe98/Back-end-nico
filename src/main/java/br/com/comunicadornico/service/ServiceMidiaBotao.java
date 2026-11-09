package br.com.comunicadornico.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import br.com.comunicadornico.entity.entityBotao;
import br.com.comunicadornico.repository.repositoryBotao;
import br.com.comunicadornico.service.ServiceCloudinaryStorage.ResultadoUpload;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import br.com.comunicadornico.dto.BotaoResolvidoDTO;
import br.com.comunicadornico.entity.entityBotaoPersonalizacao;
import br.com.comunicadornico.entity.entityUsuario;
import br.com.comunicadornico.repository.repositoryBotaoPersonalizacao;

@Service
public class ServiceMidiaBotao {

    private final repositoryBotao repositoryBotao;

    private final ServiceCloudinaryStorage cloudinaryStorage;

    private final repositoryBotaoPersonalizacao repositoryPersonalizacao;

    private final serviceUsuario usuarioService;

    private final ServiceBotaoResolvido botaoResolvidoService;

    public ServiceMidiaBotao(
            repositoryBotao repositoryBotao,
            ServiceCloudinaryStorage cloudinaryStorage,
            repositoryBotaoPersonalizacao repositoryPersonalizacao,
            serviceUsuario usuarioService,
            ServiceBotaoResolvido botaoResolvidoService) {

        this.repositoryBotao = repositoryBotao;
        this.cloudinaryStorage = cloudinaryStorage;
        this.repositoryPersonalizacao
                = repositoryPersonalizacao;
        this.usuarioService = usuarioService;
        this.botaoResolvidoService
                = botaoResolvidoService;
    }

    @Transactional
    public entityBotao atualizarMidiaPadrao(
            Long idBotao,
            MultipartFile foto,
            MultipartFile audioFem,
            MultipartFile audioMas) {

        entityBotao botao = repositoryBotao
                .findById(idBotao)
                .orElseThrow(()
                        -> new org.springframework.web.server.ResponseStatusException(
                                org.springframework.http.HttpStatus.NOT_FOUND,
                                "Botão não encontrado: "
                                + idBotao
                        )
                );

        ResultadoUpload novaFoto = null;
        ResultadoUpload novoAudioFem = null;
        ResultadoUpload novoAudioMas = null;

        String fotoPublicIdAntigo
                = botao.getFotoPublicId();

        String audioFemPublicIdAntigo
                = botao.getAudioFemPublicId();

        String audioMasPublicIdAntigo
                = botao.getAudioMasPublicId();

        try {
            if (arquivoFoiEnviado(foto)) {
                novaFoto
                        = cloudinaryStorage.uploadImagem(
                                foto,
                                "nico/botoes/padrao/"
                                + idBotao
                                + "/imagem"
                        );

                botao.setFoto(novaFoto.url());
                botao.setFotoPublicId(
                        novaFoto.publicId()
                );
            }

            if (arquivoFoiEnviado(audioFem)) {
                novoAudioFem
                        = cloudinaryStorage.uploadAudio(
                                audioFem,
                                "nico/botoes/padrao/"
                                + idBotao
                                + "/audio-feminino"
                        );

                botao.setAudioFem(
                        novoAudioFem.url()
                );

                botao.setAudioFemPublicId(
                        novoAudioFem.publicId()
                );
            }

            if (arquivoFoiEnviado(audioMas)) {
                novoAudioMas
                        = cloudinaryStorage.uploadAudio(
                                audioMas,
                                "nico/botoes/padrao/"
                                + idBotao
                                + "/audio-masculino"
                        );

                botao.setAudioMas(
                        novoAudioMas.url()
                );

                botao.setAudioMasPublicId(
                        novoAudioMas.publicId()
                );
            }

            entityBotao botaoSalvo
                    = repositoryBotao.save(botao);

            excluirMidiasAntigas(
                    novaFoto,
                    fotoPublicIdAntigo,
                    novoAudioFem,
                    audioFemPublicIdAntigo,
                    novoAudioMas,
                    audioMasPublicIdAntigo
            );

            return botaoSalvo;

        } catch (RuntimeException exception) {

            excluirNovosUploads(
                    novaFoto,
                    novoAudioFem,
                    novoAudioMas
            );

            throw exception;
        }
    }

    private void excluirMidiasAntigas(
            ResultadoUpload novaFoto,
            String fotoPublicIdAntigo,
            ResultadoUpload novoAudioFem,
            String audioFemPublicIdAntigo,
            ResultadoUpload novoAudioMas,
            String audioMasPublicIdAntigo) {

        if (novaFoto != null) {
            excluirImagemSemInterromper(
                    fotoPublicIdAntigo
            );
        }

        if (novoAudioFem != null) {
            excluirAudioSemInterromper(
                    audioFemPublicIdAntigo
            );
        }

        if (novoAudioMas != null) {
            excluirAudioSemInterromper(
                    audioMasPublicIdAntigo
            );
        }
    }

    private void excluirNovosUploads(
            ResultadoUpload novaFoto,
            ResultadoUpload novoAudioFem,
            ResultadoUpload novoAudioMas) {

        if (novaFoto != null) {
            excluirImagemSemInterromper(
                    novaFoto.publicId()
            );
        }

        if (novoAudioFem != null) {
            excluirAudioSemInterromper(
                    novoAudioFem.publicId()
            );
        }

        if (novoAudioMas != null) {
            excluirAudioSemInterromper(
                    novoAudioMas.publicId()
            );
        }
    }

    private void excluirImagemSemInterromper(
            String publicId) {

        try {
            cloudinaryStorage.excluirImagem(publicId);
        } catch (RuntimeException exception) {
            System.err.println(
                    "Não foi possível excluir a imagem "
                    + publicId
                    + ": "
                    + exception.getMessage()
            );
        }
    }

    private void excluirAudioSemInterromper(
            String publicId) {

        try {
            cloudinaryStorage.excluirAudio(publicId);
        } catch (RuntimeException exception) {
            System.err.println(
                    "Não foi possível excluir o áudio "
                    + publicId
                    + ": "
                    + exception.getMessage()
            );
        }
    }

    private boolean arquivoFoiEnviado(
            MultipartFile arquivo) {

        return arquivo != null
                && !arquivo.isEmpty();
    }

    @Transactional
    public BotaoResolvidoDTO personalizarBotao(
            Long idUsuario,
            Long idBotao,
            MultipartFile foto,
            MultipartFile audioPersonalizado,
            String emailAdministrador) {

        entityUsuario usuario
                = usuarioService.validarUsuarioDoAdministrador(
                        idUsuario,
                        emailAdministrador
                );

        entityBotao botao = repositoryBotao
                .findById(idBotao)
                .orElseThrow(()
                        -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Botão não encontrado: "
                                + idBotao
                        )
                );

        validarBotaoDisponivelParaUsuario(
                botao,
                idUsuario
        );

        entityBotaoPersonalizacao personalizacao
                = repositoryPersonalizacao
                        .findByUsuarioIdUsuarioAndBotaoIdBotao(
                                idUsuario,
                                idBotao
                        )
                        .orElseGet(() -> {
                            entityBotaoPersonalizacao nova
                                    = new entityBotaoPersonalizacao();

                            nova.setUsuario(usuario);
                            nova.setBotao(botao);

                            return nova;
                        });

        ResultadoUpload novaFoto = null;
        ResultadoUpload novoAudio = null;

        String fotoPublicIdAntigo
                = personalizacao.getFotoPublicId();

        String audioPublicIdAntigo
                = personalizacao.getAudioPublicId();

        try {
            if (arquivoFoiEnviado(foto)) {
                novaFoto
                        = cloudinaryStorage.uploadImagem(
                                foto,
                                "nico/usuarios/"
                                + idUsuario
                                + "/botoes/"
                                + idBotao
                                + "/imagem"
                        );

                personalizacao.setFoto(
                        novaFoto.url()
                );

                personalizacao.setFotoPublicId(
                        novaFoto.publicId()
                );
            }

            if (arquivoFoiEnviado(
                    audioPersonalizado)) {

                novoAudio
                        = cloudinaryStorage.uploadAudio(
                                audioPersonalizado,
                                "nico/usuarios/"
                                + idUsuario
                                + "/botoes/"
                                + idBotao
                                + "/audio"
                        );

                personalizacao.setAudioPersonalizado(
                        novoAudio.url()
                );

                personalizacao.setAudioPublicId(
                        novoAudio.publicId()
                );
            }

            repositoryPersonalizacao.saveAndFlush(
                    personalizacao
            );

            if (novaFoto != null) {
                excluirImagemSemInterromper(
                        fotoPublicIdAntigo
                );
            }

            if (novoAudio != null) {
                excluirAudioSemInterromper(
                        audioPublicIdAntigo
                );
            }

            return botaoResolvidoService
                    .pesquisarBotaoResolvido(
                            idUsuario,
                            idBotao,
                            emailAdministrador
                    );

        } catch (RuntimeException exception) {

            if (novaFoto != null) {
                excluirImagemSemInterromper(
                        novaFoto.publicId()
                );
            }

            if (novoAudio != null) {
                excluirAudioSemInterromper(
                        novoAudio.publicId()
                );
            }

            throw exception;
        }
    }

    private void validarBotaoDisponivelParaUsuario(
            entityBotao botao,
            Long idUsuario) {

        /*
     * Botão sem usuário é um botão padrão,
     * disponível para todos os comunicadores.
         */
        if (botao.getUsuario() == null) {
            return;
        }

        /*
     * Botões vinculados diretamente a um usuário
     * só podem ser usados naquele comunicador.
         */
        if (!botao.getUsuario()
                .getIdUsuario()
                .equals(idUsuario)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Este botão não está disponível "
                    + "para o comunicador do usuário."
            );
        }
    }

    @Transactional
    public BotaoResolvidoDTO removerPersonalizacaoBotao(
            Long idUsuario,
            Long idBotao,
            String emailAdministrador) {

        /*
     * Confirma que o usuário pertence ao administrador
     * autenticado antes de qualquer remoção.
         */
        usuarioService.validarUsuarioDoAdministrador(
                idUsuario,
                emailAdministrador
        );

        entityBotao botao = repositoryBotao
                .findById(idBotao)
                .orElseThrow(()
                        -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Botão não encontrado: " + idBotao
                        )
                );

        validarBotaoDisponivelParaUsuario(
                botao,
                idUsuario
        );

        entityBotaoPersonalizacao personalizacao
                = repositoryPersonalizacao
                        .findByUsuarioIdUsuarioAndBotaoIdBotao(
                                idUsuario,
                                idBotao
                        )
                        .orElseThrow(()
                                -> new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "O botão não possui personalização "
                                        + "para este comunicador."
                                )
                        );

        String fotoPublicId
                = personalizacao.getFotoPublicId();

        String audioPublicId
                = personalizacao.getAudioPublicId();

        repositoryPersonalizacao.delete(personalizacao);
        repositoryPersonalizacao.flush();

        excluirImagemSemInterromper(fotoPublicId);
        excluirAudioSemInterromper(audioPublicId);
        return botaoResolvidoService
                .pesquisarBotaoResolvido(
                        idUsuario,
                        idBotao,
                        emailAdministrador
                );
    }
}
