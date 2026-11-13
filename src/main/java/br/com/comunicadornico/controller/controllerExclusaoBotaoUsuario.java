package br.com.comunicadornico.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.comunicadornico.dto.ExcluirRegistroUsuarioResponseDTO;
import br.com.comunicadornico.service.serviceExcluirBotaoUsuario;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(
        "/api/botoes/usuario"
)
@Tag(
        name = "Exclusão de botões",
        description = "Ocultação de botões padrão e exclusão de botões personalizados"
)
public class controllerExclusaoBotaoUsuario {

    private final serviceExcluirBotaoUsuario
            serviceExcluirBotaoUsuario;

    public controllerExclusaoBotaoUsuario(
            serviceExcluirBotaoUsuario
                    serviceExcluirBotaoUsuario) {

        this.serviceExcluirBotaoUsuario =
                serviceExcluirBotaoUsuario;
    }

    @DeleteMapping(
            value = "/{idBotao}",
            produces =
                    MediaType.APPLICATION_JSON_VALUE
    )
    @SecurityRequirement(
            name = "BearerAuth"
    )
    @Operation(
            summary = "Remover botão do usuário",
            description = """
                    Se o botão for padrão, oculta apenas para
                    o usuário selecionado.

                    Se o botão for personalizado e pertencer
                    ao usuário, remove definitivamente o botão,
                    as relações e as mídias próprias.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description =
                            "Botão ocultado ou excluído com sucesso."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description =
                            "Parâmetros inválidos."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description =
                            "Administrador não autenticado."
            ),
            @ApiResponse(
                    responseCode = "403",
                    description =
                            "O botão não pertence ao usuário selecionado."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description =
                            "Usuário ou botão não encontrado."
            )
    })
    public ResponseEntity<
            ExcluirRegistroUsuarioResponseDTO>
            excluirBotaoUsuario(

                    @PathVariable
                    Long idBotao,

                    @RequestParam
                    Long idUsuario,

                    Principal principal) {

        validarAutenticacao(
                principal
        );

        if (idUsuario == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O usuário é obrigatório."
            );
        }

        ExcluirRegistroUsuarioResponseDTO
                resposta =
                serviceExcluirBotaoUsuario
                        .excluir(
                                idBotao,
                                idUsuario,
                                principal.getName()
                        );

        return ResponseEntity.ok(
                resposta
        );
    }

    private void validarAutenticacao(
            Principal principal) {

        if (principal == null
                || principal.getName() == null
                || principal.getName()
                        .isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Administrador não autenticado."
            );
        }
    }
}