package br.com.comunicadornico.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private final ServiceBotaoResolvido botaoResolvidoService;

    public serviceRegistroDeAtividade(
            repositoryRegistroDeAtividade repositoryRegistro,
            serviceUsuario usuarioService,
            serviceBotao botaoService,
            ServiceBotaoResolvido botaoResolvidoService) {

        this.repositoryRegistro = repositoryRegistro;
        this.usuarioService = usuarioService;
        this.botaoService = botaoService;
        this.botaoResolvidoService = botaoResolvidoService;

    }

    @Transactional
    public entityRegistroDeAtividade gravarRegistro(
            entityUsuario usuario,
            entityBotao botao) {

        entityRegistroDeAtividade registro
                = new entityRegistroDeAtividade();

        registro.setUsuario(usuario);
        registro.setBotao(botao);
        registro.setQuantidadeCliques(1);

        registro.setDataHora(
                LocalDateTime.now(
                        ZoneId.of("America/Sao_Paulo")
                )
        );

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

        entityRegistroDeAtividade registro
                = gravarRegistro(usuario, botao);

        var botaoResolvido
                = botaoResolvidoService
                        .pesquisarBotaoResolvidoParaUso(
                                idUsuario,
                                idBotao
                        );

        return new BotaoAcionadoDTO(
                botao.getIdBotao(),
                botao.getLegenda(),
                botaoResolvido.foto(),
                botaoResolvido.audioResolvido(),
                registro.getIdRegistro(),
                registro.getQuantidadeCliques()
        );
    }

    // CONSULTA dos registros de um usuário
    @Transactional(readOnly = true)
    public List<RegistroAtividadeResponseDTO> listarPorUsuario(Long idUsuario) {
        usuarioService.pesquisarUsuarioPorId(idUsuario); // 404 se o usuário não existir
        return repositoryRegistro.findByUsuarioIdUsuarioOrderByDataHoraDesc(idUsuario)
                .stream().map(RegistroAtividadeResponseDTO::de).toList();
    }
}
