package br.com.comunicadornico.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.comunicadornico.dto.BotaoResumoDTO;
import br.com.comunicadornico.dto.TiraDeSentencaResponseDTO;
import br.com.comunicadornico.entity.entityBotao;
import br.com.comunicadornico.entity.entityTiraDeSentenca;
import br.com.comunicadornico.entity.entityUsuario;
import br.com.comunicadornico.repository.repositoryBotao;
import br.com.comunicadornico.repository.repositoryTiraDeSentenca;

@Service
public class serviceTiraDeSentenca {

    private static final int MAXIMO_BOTOES = 20;

    private final repositoryTiraDeSentenca repositoryTira;
    private final repositoryBotao repositoryBotao;
    private final serviceUsuario usuarioService;
    private final serviceBotao botaoService;
    private final serviceRegistroDeAtividade registroService;

    public serviceTiraDeSentenca(repositoryTiraDeSentenca repositoryTira,
                                 repositoryBotao repositoryBotao,
                                 serviceUsuario usuarioService,
                                 serviceBotao botaoService,
                                 serviceRegistroDeAtividade registroService) {
        this.repositoryTira = repositoryTira;
        this.repositoryBotao = repositoryBotao;
        this.usuarioService = usuarioService;
        this.botaoService = botaoService;
        this.registroService = registroService;
    }

    // READ - tira atual do usuário (cria uma vazia se ainda não existir; relação 1..1)
    @Transactional
    public TiraDeSentencaResponseDTO obter(Long idUsuario) {
        entityUsuario usuario = usuarioService.pesquisarUsuarioPorId(idUsuario);
        return montarResposta(obterOuCriar(usuario));
    }

    // adiciona um botão ao fim da frase e conta como uso
    @Transactional
    public TiraDeSentencaResponseDTO adicionarBotao(Long idUsuario, Long idBotao) {
        entityUsuario usuario = usuarioService.pesquisarUsuarioPorId(idUsuario);
        entityBotao botao = botaoService.pesquisarBotaoPorId(idBotao);

        if (botao.isOculto()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O botão está oculto e não pode ser usado na tira.");
        }

        entityTiraDeSentenca tira = obterOuCriar(usuario);
        if (tira.getSequenciaBotoes().size() >= MAXIMO_BOTOES) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A tira de sentença já atingiu o limite de " + MAXIMO_BOTOES + " botões.");
        }

        tira.getSequenciaBotoes().add(botao.getIdBotao());
        registroService.gravarRegistro(usuario, botao);
        return salvarEMontar(tira);
    }

    // remove o último botão da frase
    @Transactional
    public TiraDeSentencaResponseDTO removerUltimoBotao(Long idUsuario) {
        entityTiraDeSentenca tira = obterOuCriar(usuarioService.pesquisarUsuarioPorId(idUsuario));
        if (tira.getSequenciaBotoes().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A tira de sentença já está vazia.");
        }
        tira.getSequenciaBotoes().remove(tira.getSequenciaBotoes().size() - 1);
        return salvarEMontar(tira);
    }

    // limpa a frase inteira
    @Transactional
    public void limpar(Long idUsuario) {
        entityTiraDeSentenca tira = obterOuCriar(usuarioService.pesquisarUsuarioPorId(idUsuario));
        tira.getSequenciaBotoes().clear();
        tira.setTextoFormatado("");
        repositoryTira.save(tira);
    }

    // ---------- métodos auxiliares ----------

    private entityTiraDeSentenca obterOuCriar(entityUsuario usuario) {
        return repositoryTira.findByUsuarioIdUsuario(usuario.getIdUsuario()).orElseGet(() -> {
            entityTiraDeSentenca nova = new entityTiraDeSentenca();
            nova.setUsuario(usuario);
            nova.setTextoFormatado("");
            return repositoryTira.save(nova);
        });
    }

    // recalcula o texto (junção das legendas, sem IA) e devolve o DTO
    private TiraDeSentencaResponseDTO salvarEMontar(entityTiraDeSentenca tira) {
        List<entityBotao> botoes = resolverBotoes(tira.getSequenciaBotoes());
        tira.setTextoFormatado(botoes.stream().map(entityBotao::getLegenda).collect(Collectors.joining(" ")));
        return montarResposta(repositoryTira.save(tira));
    }

    private TiraDeSentencaResponseDTO montarResposta(entityTiraDeSentenca tira) {
        List<BotaoResumoDTO> resumo = resolverBotoes(tira.getSequenciaBotoes()).stream()
                .map(b -> new BotaoResumoDTO(b.getIdBotao(), b.getLegenda(), b.getFoto(), b.getCorFundo()))
                .toList();
        return new TiraDeSentencaResponseDTO(
                tira.getIdTiraDeSentenca(),
                tira.getUsuario().getIdUsuario(),
                List.copyOf(tira.getSequenciaBotoes()),
                tira.getTextoFormatado(),
                resumo);
    }

    // busca os botões em uma consulta só e preserva a ordem (e repetições) da frase
    private List<entityBotao> resolverBotoes(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        Map<Long, entityBotao> porId = repositoryBotao.findAllById(ids).stream()
                .collect(Collectors.toMap(entityBotao::getIdBotao, Function.identity()));
        return ids.stream().map(porId::get).filter(Objects::nonNull).toList();
    }
}