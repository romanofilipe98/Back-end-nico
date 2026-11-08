package br.com.comunicadornico.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.comunicadornico.dto.TiraDeSentencaResponseDTO;
import br.com.comunicadornico.service.serviceTiraDeSentenca;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/usuarios/{idUsuario}/tira-de-sentenca")
@Tag(name = "Tira de Sentença", description = "Montagem da frase do usuário (sem IA nesta fase)")
public class controllerTiraDeSentenca {

    private final serviceTiraDeSentenca meuService;

    public controllerTiraDeSentenca(serviceTiraDeSentenca meuService) {
        this.meuService = meuService;
    }

    @GetMapping
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Consultar tira de sentença", description = "Retorna a frase atual do usuário; cria uma vazia se não existir (Requer Token).")
    @ApiResponse(responseCode = "200", description = "Tira retornada com sucesso")
    public ResponseEntity<TiraDeSentencaResponseDTO> obter(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(meuService.obter(idUsuario));
    }

    @PostMapping("/botoes/{idBotao}")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Adicionar botão à tira", description = "Insere o botão ao final da frase e registra o uso (Requer Token).")
    @ApiResponse(responseCode = "200", description = "Botão adicionado")
    public ResponseEntity<TiraDeSentencaResponseDTO> adicionarBotao(@PathVariable Long idUsuario, @PathVariable Long idBotao) {
        return ResponseEntity.ok(meuService.adicionarBotao(idUsuario, idBotao));
    }

    @DeleteMapping("/botoes/ultimo")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Remover último botão", description = "Remove o último botão da frase (Requer Token).")
    @ApiResponse(responseCode = "200", description = "Botão removido")
    public ResponseEntity<TiraDeSentencaResponseDTO> removerUltimo(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(meuService.removerUltimoBotao(idUsuario));
    }

    @DeleteMapping
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Limpar tira", description = "Remove todos os botões da frase (Requer Token).")
    @ApiResponse(responseCode = "204", description = "Tira limpa")
    public ResponseEntity<Void> limpar(@PathVariable Long idUsuario) {
        meuService.limpar(idUsuario);
        return ResponseEntity.noContent().build();
    }
}