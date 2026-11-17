package br.com.comunicadornico.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.comunicadornico.dto.AdministradorPerfilDTO;
import br.com.comunicadornico.entity.entityAdministrador;
import br.com.comunicadornico.service.serviceAdministrador;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/administradores")
@Tag(name = "Administradores", description = "Endpoints para gestão, cadastro e consulta de administradores")
public class controllerAdministrador {

    @Autowired
    private serviceAdministrador meuService;

    // Autorização: um administrador só acessa/altera a PRÓPRIA conta
    private entityAdministrador exigirProprioAdministrador(Long id, Authentication authentication) {
        entityAdministrador alvo = meuService.pesquisarAdministradorPorId(id);
        if (authentication == null || !alvo.getEmail().equalsIgnoreCase(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você só pode acessar a sua própria conta.");
        }
        return alvo;
    }

    @PostMapping
    @Operation(summary = "Criar novo administrador", description = "Registra um administrador. A senha é criptografada (Rota pública).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Administrador criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro na validação dos dados enviados"),
            @ApiResponse(responseCode = "409", description = "E-mail ou telefone já cadastrado")
    })
    public ResponseEntity<entityAdministrador> criarAdministrador(
            @Valid @RequestBody entityAdministrador novoAdministrador) {
        return ResponseEntity.status(HttpStatus.CREATED).body(meuService.criarAdministrador(novoAdministrador));
    }

    @GetMapping
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Listar administradores", description = "Retorna apenas a conta do administrador autenticado (Requer Token).")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<entityAdministrador>> listarTodosAdministradores(Authentication authentication) {
        List<entityAdministrador> lista = meuService.listarAdministradores().stream()
                .filter(a -> a.getEmail().equalsIgnoreCase(authentication.getName()))
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/buscaradmnome")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Buscar por nome", description = "Pesquisa dentro da conta autenticada (Requer Token).")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    public ResponseEntity<List<entityAdministrador>> buscarPorNome(@RequestParam String nome,
            Authentication authentication) {
        List<entityAdministrador> lista = meuService.pesquisarAdministrador(nome).stream()
                .filter(a -> a.getEmail().equalsIgnoreCase(authentication.getName()))
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Buscar perfil do administrador autenticado", description = "Retorna somente os dados necessários para edição do perfil.")
    public ResponseEntity<AdministradorPerfilDTO> buscarPerfilAdministrador(
            Authentication authentication) {

        if (authentication == null
                || authentication.getName() == null
                || authentication.getName().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Administrador não autenticado.");
        }

        entityAdministrador administrador = meuService.pesquisarAdministradorPorEmail(
                authentication.getName());

        AdministradorPerfilDTO resposta = new AdministradorPerfilDTO(
                administrador.getIdAdministrador(),
                administrador.getNome(),
                administrador.getEmail(),
                administrador.getPin(),
                administrador.getTelefone(),
                administrador.getDataNasc(),
                administrador.getSexo());

        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{idAdministrador}")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Buscar por ID", description = "Retorna o administrador autenticado (Requer Token).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Administrador encontrado"),
            @ApiResponse(responseCode = "403", description = "Conta de outro administrador"),
            @ApiResponse(responseCode = "404", description = "Administrador não encontrado")
    })
    public ResponseEntity<entityAdministrador> buscarAdministradorPorId(@PathVariable Long idAdministrador,
            Authentication authentication) {
        return ResponseEntity.ok(exigirProprioAdministrador(idAdministrador, authentication));
    }

    @PutMapping("/{idAdministrador}")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Atualizar administrador", description = "Atualiza a própria conta e retorna somente os dados do perfil.")
    @ApiResponse(responseCode = "200", description = "Administrador atualizado com sucesso")
    public ResponseEntity<AdministradorPerfilDTO> atualizarAdministrador(
            @PathVariable Long idAdministrador,
            @RequestBody entityAdministrador administradorAtualizado,
            Authentication authentication) {

        exigirProprioAdministrador(
                idAdministrador,
                authentication);

        entityAdministrador administrador = meuService.atualizarAdministrador(
                idAdministrador,
                administradorAtualizado);

        AdministradorPerfilDTO resposta = new AdministradorPerfilDTO(
                administrador.getIdAdministrador(),
                administrador.getNome(),
                administrador.getEmail(),
                administrador.getPin(),
                administrador.getTelefone(),
                administrador.getDataNasc(),
                administrador.getSexo());

        return ResponseEntity.ok(resposta);
    }

    @DeleteMapping("/{idAdministrador}")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Excluir administrador", description = "Remove a própria conta (Requer Token).")
    @ApiResponse(responseCode = "204", description = "Administrador excluído com sucesso")
    public ResponseEntity<Void> excluirAdministrador(@PathVariable Long idAdministrador,
            Authentication authentication) {
        exigirProprioAdministrador(idAdministrador, authentication);
        meuService.excluirAdministrador(idAdministrador);
        return ResponseEntity.noContent().build();
    }
}