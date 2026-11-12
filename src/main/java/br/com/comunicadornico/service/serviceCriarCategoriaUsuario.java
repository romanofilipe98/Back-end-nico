package br.com.comunicadornico.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import br.com.comunicadornico.dto.BotaoCategoriaOrdemDTO;
import br.com.comunicadornico.dto.CriarCategoriaUsuarioDTO;
import br.com.comunicadornico.dto.CriarCategoriaUsuarioResponseDTO;
import br.com.comunicadornico.entity.entityAdministrador;
import br.com.comunicadornico.entity.entityBotao;
import br.com.comunicadornico.entity.entityBotaoCategoria;
import br.com.comunicadornico.entity.entityCategoria;
import br.com.comunicadornico.entity.entityUsuario;
import br.com.comunicadornico.repository.repositoryBotao;
import br.com.comunicadornico.repository.repositoryBotaoCategoria;
import br.com.comunicadornico.repository.repositoryCategoria;
import br.com.comunicadornico.service.ServiceCloudinaryStorage.ResultadoUpload;

@Service
public class serviceCriarCategoriaUsuario {

    private final repositoryCategoria repositoryCategoria;
    private final repositoryBotao repositoryBotao;
    private final repositoryBotaoCategoria repositoryBotaoCategoria;
    private final serviceUsuario usuarioService;
    private final ServiceCloudinaryStorage cloudinaryStorage;

    public serviceCriarCategoriaUsuario(
            repositoryCategoria repositoryCategoria,
            repositoryBotao repositoryBotao,
            repositoryBotaoCategoria repositoryBotaoCategoria,
            serviceUsuario usuarioService,
            ServiceCloudinaryStorage cloudinaryStorage) {

        this.repositoryCategoria =
                repositoryCategoria;

        this.repositoryBotao =
                repositoryBotao;

        this.repositoryBotaoCategoria =
                repositoryBotaoCategoria;

        this.usuarioService =
                usuarioService;

        this.cloudinaryStorage =
                cloudinaryStorage;
    }

    @Transactional
    public CriarCategoriaUsuarioResponseDTO criar(
            CriarCategoriaUsuarioDTO dados,
            MultipartFile foto,
            String emailAdministrador) {

        validarDados(dados, foto);

        /*
         * Localiza o usuário e confirma que pertence
         * ao administrador autenticado pelo JWT.
         */
        entityUsuario usuario =
                usuarioService
                        .validarUsuarioDoAdministrador(
                                dados.getIdUsuario(),
                                emailAdministrador
                        );

        entityAdministrador administrador =
                usuario.getAdministrador();

        if (administrador == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O usuário não possui administrador vinculado."
            );
        }

        /*
         * Carrega e valida todos os botões antes de
         * criar a categoria ou enviar a imagem.
         */
        List<BotaoValidado> botoesValidados =
                validarBotoes(
                        dados.getBotoes(),
                        usuario
                );

        /*
         * A nova categoria é inserida depois das
         * categorias globais e das categorias
         * exclusivas do usuário.
         */
        int maiorPosicao =
                repositoryCategoria
                        .buscarMaiorPosicaoDisponivelParaUsuario(
                                usuario.getIdUsuario()
                        );

        int novaPosicao =
                maiorPosicao + 1;

        ResultadoUpload resultadoFoto =
                null;

        try {
            entityCategoria categoria =
                    new entityCategoria();

            categoria.setNome(
                    dados.getNome().trim()
            );

            categoria.setPosicaoCat(
                    novaPosicao
            );

            categoria.setUsuario(
                    usuario
            );

            categoria.setAdministrador(
                    administrador
            );

            /*
             * Primeiro salva para obter o ID que será
             * usado na pasta da imagem no Cloudinary.
             */
            entityCategoria categoriaSalva =
                    repositoryCategoria
                            .saveAndFlush(
                                    categoria
                            );

            resultadoFoto =
                    cloudinaryStorage
                            .uploadImagem(
                                    foto,
                                    "nico/usuarios/"
                                            + usuario.getIdUsuario()
                                            + "/categorias/"
                                            + categoriaSalva
                                                    .getIdCategoria()
                                            + "/imagem"
                            );

            categoriaSalva.setFotoCat(
                    resultadoFoto.url()
            );

            categoriaSalva
                    .setFotoCatPublicId(
                            resultadoFoto.publicId()
                    );

            categoriaSalva =
                    repositoryCategoria
                            .saveAndFlush(
                                    categoriaSalva
                            );

            /*
             * Cria as relações entre a categoria
             * e os botões na ordem recebida.
             */
            List<entityBotaoCategoria> relacoes =
                    new ArrayList<>();

            for (
                    BotaoValidado botaoValidado
                    : botoesValidados
            ) {
                entityBotaoCategoria relacao =
                        new entityBotaoCategoria();

                relacao.setCategoria(
                        categoriaSalva
                );

                relacao.setBotao(
                        botaoValidado.botao()
                );

                relacao.setPosicao(
                        botaoValidado.posicao()
                );

                relacoes.add(relacao);
            }

            repositoryBotaoCategoria
                    .saveAll(relacoes);

            repositoryBotaoCategoria
                    .flush();

            /*
             * Devolve a mesma ordem confirmada
             * pelo back-end.
             */
            List<BotaoCategoriaOrdemDTO>
                    botoesResposta =
                    botoesValidados
                            .stream()
                            .map(
                                    this::converterParaDTO
                            )
                            .toList();

            return CriarCategoriaUsuarioResponseDTO.de(
                    categoriaSalva,
                    botoesResposta
            );
        } catch (RuntimeException exception) {
            /*
             * O banco será revertido pelo
             * @Transactional.
             *
             * O Cloudinary é externo ao banco,
             * por isso a imagem precisa ser removida
             * manualmente quando ocorrer uma falha.
             */
            if (resultadoFoto != null) {
                try {
                    cloudinaryStorage
                            .excluirImagem(
                                    resultadoFoto.publicId()
                            );
                } catch (RuntimeException erroExclusao) {
                    System.err.println(
                            "Não foi possível remover a imagem "
                                    + "da categoria após a falha: "
                                    + erroExclusao.getMessage()
                    );
                }
            }

            throw exception;
        }
    }

    private void validarDados(
            CriarCategoriaUsuarioDTO dados,
            MultipartFile foto) {

        if (dados == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Os dados da categoria são obrigatórios."
            );
        }

        if (dados.getNome() == null
                || dados.getNome().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O nome da categoria é obrigatório."
            );
        }

        if (dados.getNome().trim().length() > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O nome da categoria deve ter no máximo 100 caracteres."
            );
        }

        if (dados.getIdUsuario() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O usuário é obrigatório."
            );
        }

        if (foto == null || foto.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A imagem da categoria é obrigatória."
            );
        }

        if (dados.getBotoes() == null
                || dados.getBotoes().isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Selecione pelo menos um botão para a categoria."
            );
        }
    }

    private List<BotaoValidado> validarBotoes(
            List<BotaoCategoriaOrdemDTO> botoes,
            entityUsuario usuario) {

        List<BotaoValidado> resultado =
                new ArrayList<>();

        Set<Long> idsEncontrados =
                new HashSet<>();

        Set<Integer> posicoesEncontradas =
                new HashSet<>();

        for (
                BotaoCategoriaOrdemDTO item
                : botoes
        ) {
            if (item == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "A lista contém um botão inválido."
                );
            }

            if (item.getIdBotao() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Todos os botões devem possuir um ID."
                );
            }

            if (item.getPosicao() < 1) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "A posição dos botões deve começar em 1."
                );
            }

            if (!idsEncontrados.add(
                    item.getIdBotao()
            )) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Um mesmo botão não pode aparecer duas vezes na categoria."
                );
            }

            if (!posicoesEncontradas.add(
                    item.getPosicao()
            )) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Dois botões não podem ocupar a mesma posição."
                );
            }

            entityBotao botao =
                    repositoryBotao
                            .findById(
                                    item.getIdBotao()
                            )
                            .orElseThrow(() ->
                                    new ResponseStatusException(
                                            HttpStatus.NOT_FOUND,
                                            "Botão não encontrado: "
                                                    + item.getIdBotao()
                                    )
                            );

            validarAcessoAoBotao(
                    botao,
                    usuario
            );

            resultado.add(
                    new BotaoValidado(
                            botao,
                            item.getPosicao()
                    )
            );
        }

        /*
         * A ordem precisa ser contínua:
         * 1, 2, 3, 4...
         *
         * Não aceita, por exemplo:
         * 1, 2, 5.
         */
        for (
                int posicaoEsperada = 1;
                posicaoEsperada <= resultado.size();
                posicaoEsperada++
        ) {
            if (!posicoesEncontradas.contains(
                    posicaoEsperada
            )) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "A ordem dos botões deve ser contínua, começando em 1."
                );
            }
        }

        resultado.sort(
                (
                        primeiro,
                        segundo
                ) -> Integer.compare(
                        primeiro.posicao(),
                        segundo.posicao()
                )
        );

        return resultado;
    }

    private void validarAcessoAoBotao(
            entityBotao botao,
            entityUsuario usuario) {

        /*
         * Botão padrão global:
         * não pertence a usuário nem administrador.
         */
        boolean botaoGlobal =
                botao.getUsuario() == null
                        && botao.getAdministrador()
                                == null;

        if (botaoGlobal) {
            return;
        }

        /*
         * Botão personalizado do próprio usuário.
         */
        boolean botaoDoUsuario =
                botao.getUsuario() != null
                        && botao.getUsuario()
                                .getIdUsuario()
                                .equals(
                                        usuario.getIdUsuario()
                                );

        if (botaoDoUsuario) {
            return;
        }

        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "O botão \""
                        + botao.getLegenda()
                        + "\" não está disponível para este usuário."
        );
    }

    private BotaoCategoriaOrdemDTO converterParaDTO(
            BotaoValidado botaoValidado) {

        BotaoCategoriaOrdemDTO dto =
                new BotaoCategoriaOrdemDTO();

        dto.setIdBotao(
                botaoValidado.botao()
                        .getIdBotao()
        );

        dto.setPosicao(
                botaoValidado.posicao()
        );

        return dto;
    }

    /*
     * Estrutura interna usada somente durante
     * a validação e criação das relações.
     */
    private record BotaoValidado(
            entityBotao botao,
            int posicao) {
    }
}