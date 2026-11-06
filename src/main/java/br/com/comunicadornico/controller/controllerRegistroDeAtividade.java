package br.com.comunicadornico.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.comunicadornico.dto.BotaoAcionadoDTO;
import br.com.comunicadornico.dto.RegistroAtividadeResponseDTO;
import br.com.comunicadornico.service.serviceRegistroDeAtividade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/usuarios/{idUsuario}")
@Tag(name = "Registros de Atividade", description = "Acionamento de botões e histórico de uso")
public class controllerRegistroDeAtividade {

    private final serviceRegistroDeAtividade meuService;

    public controllerRegistroDeAtividade(serviceRegistroDeAtividade meuService) {
        this.meuService = meuService;
    }

    @PostMapping("/botoes/{idBotao}/acionar")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Acionar botão", description = "Registra o uso do botão e devolve o áudio a ser reproduzido (Requer Token).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Botão acionado e uso registrado"),
        @ApiResponse(responseCode = "404", description = "Usuário ou botão não encontrado"),
        @ApiResponse(responseCode = "409", description = "Botão oculto")
    })
    public ResponseEntity<BotaoAcionadoDTO> acionarBotao(@PathVariable Long idUsuario, @PathVariable Long idBotao) {
        return ResponseEntity.ok(meuService.acionarBotao(idUsuario, idBotao));
    }

    @GetMapping("/registros")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Listar registros de atividade", description = "Retorna o histórico de uso do usuário, do mais recente ao mais antigo (Requer Token).")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<RegistroAtividadeResponseDTO>> listarRegistros(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(meuService.listarPorUsuario(idUsuario));
    }
}