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
import br.com.comunicadornico.dto.EditarCategoriaUsuarioDTO;
import br.com.comunicadornico.dto.EditarCategoriaUsuarioResponseDTO;
import br.com.comunicadornico.entity.entityAdministrador;
import br.com.comunicadornico.entity.entityBotao;
import br.com.comunicadornico.entity.entityBotaoCategoria;
import br.com.comunicadornico.entity.entityCategoria;
import br.com.comunicadornico.entity.entityCategoriaPersonalizacao;
import br.com.comunicadornico.entity.entityUsuario;
import br.com.comunicadornico.repository.repositoryBotao;
import br.com.comunicadornico.repository.repositoryBotaoCategoria;
import br.com.comunicadornico.repository.repositoryCategoria;
import br.com.comunicadornico.repository.repositoryCategoriaPersonalizacao;
import br.com.comunicadornico.service.ServiceCloudinaryStorage.ResultadoUpload;

@Service
public class serviceEditarCategoriaUsuario {

    private final repositoryCategoria
            repositoryCategoria;

    private final repositoryBotao
            repositoryBotao;

    private final repositoryBotaoCategoria
            repositoryBotaoCategoria;

    private final repositoryCategoriaPersonalizacao
            repositoryPersonalizacao;

    private final serviceUsuario
            usuarioService;

    private final ServiceCloudinaryStorage
            cloudinaryStorage;

    public serviceEditarCategoriaUsuario(
            repositoryCategoria repositoryCategoria,
            repositoryBotao repositoryBotao,
            repositoryBotaoCategoria
                    repositoryBotaoCategoria,
            repositoryCategoriaPersonalizacao
                    repositoryPersonalizacao,
            serviceUsuario usuarioService,
            ServiceCloudinaryStorage
                    cloudinaryStorage) {

        this.repositoryCategoria =
                repositoryCategoria;

        this.repositoryBotao =
                repositoryBotao;

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
    public EditarCategoriaUsuarioResponseDTO editar(
            Long idCategoria,
            EditarCategoriaUsuarioDTO dados,
            MultipartFile foto,
            String emailAdministrador) {

        validarDados(dados);

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

        entityCategoria categoriaOriginal =
                repositoryCategoria
                        .findById(idCategoria)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Categoria não encontrada: "
                                                + idCategoria
                                )
                        );

        validarCategoriaDisponivel(
                categoriaOriginal,
                usuario
        );

        List<BotaoValidado> botoesValidados =
                validarBotoes(
                        dados.getBotoes(),
                        usuario
                );

        boolean categoriaGlobal =
                categoriaOriginal.getUsuario()
                        == null
                        && categoriaOriginal
                                .getAdministrador()
                                == null;

        if (categoriaGlobal) {
            return criarCopiaExclusiva(
                    categoriaOriginal,
                    usuario,
                    administrador,
                    dados,
                    botoesValidados,
                    foto
            );
        }

        return atualizarCategoriaExclusiva(
                categoriaOriginal,
                usuario,
                dados,
                botoesValidados,
                foto
        );
    }

    private EditarCategoriaUsuarioResponseDTO
            criarCopiaExclusiva(
                    entityCategoria categoriaOriginal,
                    entityUsuario usuario,
                    entityAdministrador administrador,
                    EditarCategoriaUsuarioDTO dados,
                    List<BotaoValidado> botoesValidados,
                    MultipartFile foto) {

        ResultadoUpload novaFoto =
                null;

        try {
            entityCategoria copia =
                    new entityCategoria();

            copia.setNome(
                    dados.getNome().trim()
            );

            copia.setUsuario(
                    usuario
            );

            copia.setAdministrador(
                    administrador
            );

            /*
             * A cópia ocupa inicialmente a mesma
             * posição visual da categoria padrão.
             *
             * A categoria padrão será ocultada
             * somente para este usuário.
             */
            copia.setPosicaoCat(
                    categoriaOriginal
                            .getPosicaoCat()
            );

            /*
             * A cópia pode usar a URL da imagem
             * padrão, porém não recebe o Public ID.
             *
             * Assim, excluir a cópia não remove
             * a imagem usada pela categoria global.
             */
            copia.setFotoCat(
                    categoriaOriginal
                            .getFotoCat()
            );

            copia.setFotoCatPublicId(
                    null
            );

            entityCategoria copiaSalva =
                    repositoryCategoria
                            .saveAndFlush(
                                    copia
                            );

            if (arquivoFoiEnviado(foto)) {
                novaFoto =
                        cloudinaryStorage
                                .uploadImagem(
                                        foto,
                                        caminhoImagem(
                                                usuario,
                                                copiaSalva
                                        )
                                );

                copiaSalva.setFotoCat(
                        novaFoto.url()
                );

                copiaSalva
                        .setFotoCatPublicId(
                                novaFoto.publicId()
                        );
            }

            copiaSalva =
                    repositoryCategoria
                            .saveAndFlush(
                                    copiaSalva
                            );

            substituirRelacoes(
                    copiaSalva,
                    botoesValidados
            );

            ocultarCategoriaGlobalParaUsuario(
                    categoriaOriginal,
                    usuario
            );

            return montarResposta(
                    copiaSalva,
                    usuario,
                    botoesValidados,
                    true,
                    true
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

    private EditarCategoriaUsuarioResponseDTO
            atualizarCategoriaExclusiva(
                    entityCategoria categoria,
                    entityUsuario usuario,
                    EditarCategoriaUsuarioDTO dados,
                    List<BotaoValidado> botoesValidados,
                    MultipartFile foto) {

        ResultadoUpload novaFoto =
                null;

        String fotoPublicIdAntigo =
                categoria
                        .getFotoCatPublicId();

        try {
            categoria.setNome(
                    dados.getNome().trim()
            );

            if (arquivoFoiEnviado(foto)) {
                novaFoto =
                        cloudinaryStorage
                                .uploadImagem(
                                        foto,
                                        caminhoImagem(
                                                usuario,
                                                categoria
                                        )
                                );

                categoria.setFotoCat(
                        novaFoto.url()
                );

                categoria
                        .setFotoCatPublicId(
                                novaFoto.publicId()
                        );
            }

            entityCategoria categoriaSalva =
                    repositoryCategoria
                            .saveAndFlush(
                                    categoria
                            );

            substituirRelacoes(
                    categoriaSalva,
                    botoesValidados
            );

            /*
             * Categorias exclusivas não precisam
             * de personalização separada.
             *
             * A visibilidade está representada na
             * resposta conforme o formulário.
             */
            if (novaFoto != null) {
                excluirImagemSemInterromper(
                        fotoPublicIdAntigo
                );
            }

            return montarResposta(
                    categoriaSalva,
                    usuario,
                    botoesValidados,
                    true,
                    true
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

    private void substituirRelacoes(
            entityCategoria categoria,
            List<BotaoValidado> botoesValidados) {

        List<entityBotaoCategoria>
                relacoesAnteriores =
                repositoryBotaoCategoria
                        .findByCategoriaIdCategoriaOrderByPosicaoAsc(
                                categoria
                                        .getIdCategoria()
                        );

        if (!relacoesAnteriores.isEmpty()) {
            repositoryBotaoCategoria
                    .deleteAll(
                            relacoesAnteriores
                    );

            repositoryBotaoCategoria
                    .flush();
        }

        List<entityBotaoCategoria>
                novasRelacoes =
                new ArrayList<>();

        for (
                BotaoValidado botaoValidado
                : botoesValidados
        ) {
            entityBotaoCategoria relacao =
                    new entityBotaoCategoria();

            relacao.setCategoria(
                    categoria
            );

            relacao.setBotao(
                    botaoValidado.botao()
            );

            relacao.setPosicao(
                    botaoValidado.posicao()
            );

            novasRelacoes.add(
                    relacao
            );
        }

        repositoryBotaoCategoria
                .saveAll(
                        novasRelacoes
                );

        repositoryBotaoCategoria
                .flush();
    }

    private void ocultarCategoriaGlobalParaUsuario(
            entityCategoria categoriaGlobal,
            entityUsuario usuario) {

        entityCategoriaPersonalizacao
                personalizacao =
                repositoryPersonalizacao
                        .findByUsuarioIdUsuarioAndCategoriaIdCategoria(
                                usuario.getIdUsuario(),
                                categoriaGlobal
                                        .getIdCategoria()
                        )
                        .orElseGet(() -> {
                            entityCategoriaPersonalizacao nova =
                                    new entityCategoriaPersonalizacao();

                            nova.setUsuario(
                                    usuario
                            );

                            nova.setCategoria(
                                    categoriaGlobal
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
    }

    private List<BotaoValidado> validarBotoes(
            List<BotaoCategoriaOrdemDTO> botoes,
            entityUsuario usuario) {

        if (botoes == null
                || botoes.isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Selecione pelo menos um botão para a categoria."
            );
        }

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

            validarBotaoDisponivel(
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

        for (
                int posicaoEsperada = 1;
                posicaoEsperada
                        <= resultado.size();
                posicaoEsperada++
        ) {
            if (!posicoesEncontradas
                    .contains(
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

    private void validarDados(
            EditarCategoriaUsuarioDTO dados) {

        if (dados == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Os dados da categoria são obrigatórios."
            );
        }

        if (dados.getIdUsuario() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O usuário é obrigatório."
            );
        }

        if (dados.getNome() == null
                || dados.getNome()
                        .isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O nome da categoria é obrigatório."
            );
        }

        if (dados.getNome()
                .trim()
                .length() > 100) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O nome da categoria deve ter no máximo 100 caracteres."
            );
        }

        if (dados.getBotoes() == null
                || dados.getBotoes()
                        .isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Selecione pelo menos um botão para a categoria."
            );
        }
    }

    private void validarCategoriaDisponivel(
            entityCategoria categoria,
            entityUsuario usuario) {

        if (categoria.getUsuario() == null) {
            return;
        }

        if (!categoria
                .getUsuario()
                .getIdUsuario()
                .equals(
                        usuario.getIdUsuario()
                )) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Esta categoria não está disponível para o usuário."
            );
        }
    }

    private void validarBotaoDisponivel(
            entityBotao botao,
            entityUsuario usuario) {

        if (botao.getUsuario() == null) {
            return;
        }

        if (!botao
                .getUsuario()
                .getIdUsuario()
                .equals(
                        usuario.getIdUsuario()
                )) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "O botão \""
                            + botao.getLegenda()
                            + "\" não está disponível para o usuário."
            );
        }
    }

    private EditarCategoriaUsuarioResponseDTO
            montarResposta(
                    entityCategoria categoria,
                    entityUsuario usuario,
                    List<BotaoValidado> botoesValidados,
                    boolean personalizada,
                    boolean exclusivaUsuario) {

        List<BotaoCategoriaOrdemDTO>
                botoesResposta =
                botoesValidados
                        .stream()
                        .map(
                                this::converterParaDTO
                        )
                        .toList();

        return new EditarCategoriaUsuarioResponseDTO(
                categoria.getIdCategoria(),
                usuario.getIdUsuario(),
                usuario.getNome(),
                categoria.getNome(),
                categoria.getFotoCat(),
                categoria.getPosicaoCat(),
                false,
                personalizada,
                exclusivaUsuario,
                botoesResposta
        );
    }

    private BotaoCategoriaOrdemDTO converterParaDTO(
            BotaoValidado botaoValidado) {

        BotaoCategoriaOrdemDTO dto =
                new BotaoCategoriaOrdemDTO();

        dto.setIdBotao(
                botaoValidado
                        .botao()
                        .getIdBotao()
        );

        dto.setPosicao(
                botaoValidado.posicao()
        );

        return dto;
    }

    private String caminhoImagem(
            entityUsuario usuario,
            entityCategoria categoria) {

        return "nico/usuarios/"
                + usuario.getIdUsuario()
                + "/categorias/"
                + categoria.getIdCategoria()
                + "/imagem";
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
                    .excluirImagem(
                            publicId
                    );
        } catch (RuntimeException exception) {
            System.err.println(
                    "Não foi possível excluir a imagem "
                            + publicId
                            + ": "
                            + exception.getMessage()
            );
        }
    }

    private record BotaoValidado(
            entityBotao botao,
            int posicao) {
    }
}