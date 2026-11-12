package br.com.comunicadornico.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.comunicadornico.dto.BotaoResolvidoDTO;
import br.com.comunicadornico.entity.entityBotao;
import br.com.comunicadornico.entity.entityBotaoPersonalizacao;
import br.com.comunicadornico.entity.entityUsuario;
import br.com.comunicadornico.repository.repositoryBotao;
import br.com.comunicadornico.repository.repositoryBotaoPersonalizacao;

@Service
public class ServiceBotaoResolvido {

    private final repositoryBotao repositoryBotao;

    private final repositoryBotaoPersonalizacao
            repositoryPersonalizacao;

    private final serviceUsuario usuarioService;

    public ServiceBotaoResolvido(
            repositoryBotao repositoryBotao,
            repositoryBotaoPersonalizacao
                    repositoryPersonalizacao,
            serviceUsuario usuarioService) {

        this.repositoryBotao =
                repositoryBotao;

        this.repositoryPersonalizacao =
                repositoryPersonalizacao;

        this.usuarioService =
                usuarioService;
    }

    @Transactional(readOnly = true)
    public List<BotaoResolvidoDTO>
            listarBotoesResolvidos(
                    Long idUsuario,
                    String emailAdministrador) {

        entityUsuario usuario =
                usuarioService
                        .validarUsuarioDoAdministrador(
                                idUsuario,
                                emailAdministrador
                        );

        List<entityBotao> botoes =
                repositoryBotao
                        .findBotoesDisponiveisParaUsuario(
                                idUsuario
                        );

        Map<Long, entityBotaoPersonalizacao>
                personalizacaoPorBotao =
                repositoryPersonalizacao
                        .findByUsuarioIdUsuario(
                                idUsuario
                        )
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        personalizacao ->
                                                personalizacao
                                                        .getBotao()
                                                        .getIdBotao(),
                                        Function.identity()
                                )
                        );

        return botoes
                .stream()
                .map(botao ->
                        resolverBotao(
                                botao,
                                usuario,
                                personalizacaoPorBotao
                                        .get(
                                                botao.getIdBotao()
                                        )
                        )
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public BotaoResolvidoDTO
            pesquisarBotaoResolvido(
                    Long idUsuario,
                    Long idBotao,
                    String emailAdministrador) {

        entityUsuario usuario =
                usuarioService
                        .validarUsuarioDoAdministrador(
                                idUsuario,
                                emailAdministrador
                        );

        entityBotao botao =
                repositoryBotao
                        .findById(idBotao)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Botão não encontrado: "
                                                + idBotao
                                )
                        );

        validarDisponibilidadeDoBotao(
                botao,
                idUsuario
        );

        entityBotaoPersonalizacao personalizacao =
                repositoryPersonalizacao
                        .findByUsuarioIdUsuarioAndBotaoIdBotao(
                                idUsuario,
                                idBotao
                        )
                        .orElse(null);

        return resolverBotao(
                botao,
                usuario,
                personalizacao
        );
    }

    @Transactional(readOnly = true)
    public BotaoResolvidoDTO
            pesquisarBotaoResolvidoParaUso(
                    Long idUsuario,
                    Long idBotao) {

        entityUsuario usuario =
                usuarioService
                        .pesquisarUsuarioPorId(
                                idUsuario
                        );

        entityBotao botao =
                repositoryBotao
                        .findById(idBotao)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Botão não encontrado: "
                                                + idBotao
                                )
                        );

        validarDisponibilidadeDoBotao(
                botao,
                idUsuario
        );

        entityBotaoPersonalizacao personalizacao =
                repositoryPersonalizacao
                        .findByUsuarioIdUsuarioAndBotaoIdBotao(
                                idUsuario,
                                idBotao
                        )
                        .orElse(null);

        return resolverBotao(
                botao,
                usuario,
                personalizacao
        );
    }

    private BotaoResolvidoDTO resolverBotao(
            entityBotao botao,
            entityUsuario usuario,
            entityBotaoPersonalizacao
                    personalizacao) {

        String legendaResolvida =
                resolverTexto(
                        personalizacao != null
                                ? personalizacao
                                        .getLegenda()
                                : null,
                        botao.getLegenda()
                );

        String corFundoResolvida =
                resolverTexto(
                        personalizacao != null
                                ? personalizacao
                                        .getCorFundo()
                                : null,
                        botao.getCorFundo()
                );

        String corFundoLegendaResolvida =
                resolverTexto(
                        personalizacao != null
                                ? personalizacao
                                        .getCorFundoLegenda()
                                : null,
                        botao.getCorFundoLegenda()
                );

        String corLegendaResolvida =
                resolverTexto(
                        personalizacao != null
                                ? personalizacao
                                        .getCorLegenda()
                                : null,
                        botao.getCorLegenda()
                );

        boolean exibirLegendaResolvida =
                personalizacao != null
                        && personalizacao
                                .getExibirLegenda()
                                != null
                        ? personalizacao
                                .getExibirLegenda()
                        : botao.isExibirLegenda();

        int tamanhoBotaoResolvido =
                personalizacao != null
                        && personalizacao
                                .getTamanhoBotao()
                                != null
                        ? personalizacao
                                .getTamanhoBotao()
                        : botao.getTamanhoBotao();

        boolean ocultoResolvido =
                personalizacao != null
                        && personalizacao
                                .getOculto()
                                != null
                        ? personalizacao
                                .getOculto()
                        : botao.isOculto();

        String fotoResolvida =
                resolverFoto(
                        botao,
                        personalizacao
                );

        String audioResolvido =
                resolverAudio(
                        botao,
                        usuario,
                        personalizacao
                );

        boolean personalizado =
                possuiPersonalizacao(
                        personalizacao
                );

        boolean exclusivoUsuario =
                botao.getUsuario() != null
                        && botao.getUsuario()
                                .getIdUsuario()
                                .equals(
                                        usuario.getIdUsuario()
                                );

        return new BotaoResolvidoDTO(
                botao.getIdBotao(),
                legendaResolvida,
                corFundoResolvida,
                corFundoLegendaResolvida,
                corLegendaResolvida,
                exibirLegendaResolvida,
                fotoResolvida,
                audioResolvido,
                tamanhoBotaoResolvido,
                ocultoResolvido,
                personalizado,
                exclusivoUsuario,
                resolverIdsCategorias(
                        botao
                )
        );
    }

    private List<Long> resolverIdsCategorias(
            entityBotao botao) {

        if (botao.getBotaoCategorias()
                == null) {

            return List.of();
        }

        return botao
                .getBotaoCategorias()
                .stream()
                .filter(relacao ->
                        relacao.getCategoria()
                                != null
                )
                .map(relacao ->
                        relacao.getCategoria()
                                .getIdCategoria()
                )
                .distinct()
                .toList();
    }

    private String resolverFoto(
            entityBotao botao,
            entityBotaoPersonalizacao
                    personalizacao) {

        if (personalizacao != null
                && possuiTexto(
                        personalizacao.getFoto()
                )) {

            return personalizacao.getFoto();
        }

        return botao.getFoto();
    }

    private String resolverAudio(
            entityBotao botao,
            entityUsuario usuario,
            entityBotaoPersonalizacao
                    personalizacao) {

        if (personalizacao != null
                && possuiTexto(
                        personalizacao
                                .getAudioPersonalizado()
                )) {

            return personalizacao
                    .getAudioPersonalizado();
        }

        if (possuiTexto(
                botao.getAudioPersonalizado()
        )) {
            return botao
                    .getAudioPersonalizado();
        }

        if (usuario.isVoz()) {
            return botao.getAudioMas();
        }

        return botao.getAudioFem();
    }

    private boolean possuiPersonalizacao(
            entityBotaoPersonalizacao
                    personalizacao) {

        if (personalizacao == null) {
            return false;
        }

        return possuiTexto(
                personalizacao.getLegenda()
        )
                || possuiTexto(
                        personalizacao
                                .getCorFundo()
                )
                || possuiTexto(
                        personalizacao
                                .getCorFundoLegenda()
                )
                || possuiTexto(
                        personalizacao
                                .getCorLegenda()
                )
                || personalizacao
                        .getExibirLegenda()
                        != null
                || personalizacao
                        .getTamanhoBotao()
                        != null
                || personalizacao
                        .getOculto()
                        != null
                || possuiTexto(
                        personalizacao.getFoto()
                )
                || possuiTexto(
                        personalizacao
                                .getAudioPersonalizado()
                );
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

    private boolean possuiTexto(
            String valor) {

        return valor != null
                && !valor.isBlank();
    }

    private void validarDisponibilidadeDoBotao(
            entityBotao botao,
            Long idUsuario) {

        if (botao.getUsuario() == null) {
            return;
        }

        if (!botao
                .getUsuario()
                .getIdUsuario()
                .equals(idUsuario)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Este botão não pertence ao comunicador do usuário."
            );
        }
    }
}