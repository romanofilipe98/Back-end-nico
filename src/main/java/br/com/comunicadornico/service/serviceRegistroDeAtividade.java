package br.com.comunicadornico.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.comunicadornico.dto.BotaoAcionadoDTO;
import br.com.comunicadornico.dto.RegistroAtividadeResponseDTO;
import br.com.comunicadornico.entity.entityBotao;
import br.com.comunicadornico.entity.entityRegistroDeAtividade;
import br.com.comunicadornico.entity.entityUsuario;
import br.com.comunicadornico.repository.repositoryRegistroDeAtividade;

@Service
public class serviceRegistroDeAtividade {

    private final repositoryRegistroDeAtividade repositoryRegistro;
    private final serviceUsuario usuarioService;
    private final serviceBotao botaoService;

    public serviceRegistroDeAtividade(repositoryRegistroDeAtividade repositoryRegistro,
                                      serviceUsuario usuarioService,
                                      serviceBotao botaoService) {
        this.repositoryRegistro = repositoryRegistro;
        this.usuarioService = usuarioService;
        this.botaoService = botaoService;
    }

    // GRAVAÇÃO: soma 1 clique no registro do dia (usuário + botão), criando-o se ainda não existir
    @Transactional
    public entityRegistroDeAtividade gravarRegistro(entityUsuario usuario, entityBotao botao) {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime inicioDoDia = agora.toLocalDate().atStartOfDay();
        LocalDateTime inicioDoDiaSeguinte = inicioDoDia.plusDays(1);

        entityRegistroDeAtividade registro = repositoryRegistro
                .buscarDoDia(usuario.getIdUsuario(), botao.getIdBotao(), inicioDoDia, inicioDoDiaSeguinte)
                .orElseGet(() -> {
                    entityRegistroDeAtividade novo = new entityRegistroDeAtividade();
                    novo.setUsuario(usuario);
                    novo.setBotao(botao);
                    novo.setQuantidadeCliques(0);
                    return novo;
                });

        registro.setQuantidadeCliques(registro.getQuantidadeCliques() + 1);
        registro.setDataHora(agora);
        return repositoryRegistro.save(registro);
    }

    // ACIONAR BOTÃO: valida existência e visibilidade, grava o registro e devolve o áudio a tocar
    @Transactional
    public BotaoAcionadoDTO acionarBotao(Long idUsuario, Long idBotao) {
        entityUsuario usuario = usuarioService.pesquisarUsuarioPorId(idUsuario);
        entityBotao botao = botaoService.pesquisarBotaoPorId(idBotao);

        if (botao.isOculto()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O botão está oculto e não pode ser acionado.");
        }

        entityRegistroDeAtividade registro = gravarRegistro(usuario, botao);

        return new BotaoAcionadoDTO(
                botao.getIdBotao(),
                botao.getLegenda(),
                botao.getFoto(),
                escolherAudio(botao, usuario),
                registro.getIdRegistro(),
                registro.getQuantidadeCliques());
    }

    // CONSULTA dos registros de um usuário
    @Transactional(readOnly = true)
    public List<RegistroAtividadeResponseDTO> listarPorUsuario(Long idUsuario) {
        usuarioService.pesquisarUsuarioPorId(idUsuario); // 404 se o usuário não existir
        return repositoryRegistro.findByUsuarioIdUsuarioOrderByDataHoraDesc(idUsuario)
                .stream().map(RegistroAtividadeResponseDTO::de).toList();
    }

    // áudio gravado pelo responsável tem prioridade; senão usa a voz escolhida (true = masculina)
    private String escolherAudio(entityBotao botao, entityUsuario usuario) {
        if (botao.getAudioPersonalizado() != null && !botao.getAudioPersonalizado().isBlank()) {
            return botao.getAudioPersonalizado();
        }
        return usuario.isVoz() ? botao.getAudioMas() : botao.getAudioFem();
    }
}   