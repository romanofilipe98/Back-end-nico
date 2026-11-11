package br.com.comunicadornico.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.comunicadornico.dto.BotaoCategoriaDTO;
import br.com.comunicadornico.dto.BotaoCategoriaResponseDTO;
import br.com.comunicadornico.entity.entityBotaoCategoria;
import br.com.comunicadornico.service.serviceBotaoCategoria;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/botoes-categorias")
@Tag(
        name = "Botões e categorias",
        description = "Associação entre botões e categorias"
)
public class controllerBotaoCategoria {

    private final serviceBotaoCategoria service;

    public controllerBotaoCategoria(
            serviceBotaoCategoria service) {

        this.service = service;
    }

    @PostMapping
    @SecurityRequirement(
            name = "BearerAuth"
    )
    @Operation(
            summary = "Associar botão e categoria"
    )
    public ResponseEntity<BotaoCategoriaResponseDTO>
            associar(
                    @RequestBody BotaoCategoriaDTO dados) {

        entityBotaoCategoria relacao
                = service.associar(dados);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        BotaoCategoriaResponseDTO.de(
                                relacao
                        )
                );
    }

    @GetMapping(
            "/categoria/{idCategoria}"
    )
    @SecurityRequirement(
            name = "BearerAuth"
    )
    @Operation(
            summary = "Listar botões da categoria"
    )
    public ResponseEntity<
        List<BotaoCategoriaResponseDTO>>
            listarPorCategoria(
                    @PathVariable Long idCategoria) {

        List<BotaoCategoriaResponseDTO> resposta
                = service
                        .listarPorCategoria(
                                idCategoria
                        )
                        .stream()
                        .map(
                                BotaoCategoriaResponseDTO::de
                        )
                        .toList();

        return ResponseEntity.ok(
                resposta
        );
    }

    @DeleteMapping("/{idRelacao}")
    @SecurityRequirement(
            name = "BearerAuth"
    )
    @Operation(
            summary = "Remover botão da categoria"
    )
    public ResponseEntity<Void> excluir(
            @PathVariable Long idRelacao) {

        service.excluir(idRelacao);

        return ResponseEntity
                .noContent()
                .build();
    }
}
