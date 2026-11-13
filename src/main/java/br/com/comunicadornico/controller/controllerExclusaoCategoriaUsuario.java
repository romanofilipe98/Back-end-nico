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
import br.com.comunicadornico.service.serviceExcluirCategoriaUsuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(
        "/api/categorias/usuario"
)
@Tag(
        name = "Exclusão de categorias",
        description = "Exclusão segura de categorias personalizadas"
)
public class controllerExclusaoCategoriaUsuario {

    private final serviceExcluirCategoriaUsuario
            serviceExcluirCategoriaUsuario;

    public controllerExclusaoCategoriaUsuario(
            serviceExcluirCategoriaUsuario
                    serviceExcluirCategoriaUsuario) {

        this.serviceExcluirCategoriaUsuario =
                serviceExcluirCategoriaUsuario;
    }

    @DeleteMapping(
            value = "/{idCategoria}",
            produces =
                    MediaType.APPLICATION_JSON_VALUE
    )
    @SecurityRequirement(
            name = "BearerAuth"
    )
    @Operation(
            summary = "Excluir categoria personalizada",
            description = """
                    Categorias padrão não podem ser excluídas.

                    Categorias personalizadas do usuário são
                    removidas definitivamente, junto com suas
                    relações e imagem própria.

                    Os botões contidos na categoria não são excluídos.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description =
                            "Categoria personalizada excluída com sucesso."
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
                            "Categoria padrão ou categoria de outro usuário."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description =
                            "Usuário ou categoria não encontrado."
            )
    })
    public ResponseEntity<
            ExcluirRegistroUsuarioResponseDTO>
            excluirCategoriaUsuario(

                    @PathVariable
                    Long idCategoria,

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
                serviceExcluirCategoriaUsuario
                        .excluir(
                                idCategoria,
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