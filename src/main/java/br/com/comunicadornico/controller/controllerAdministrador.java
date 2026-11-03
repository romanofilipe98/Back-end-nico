package br.com.comunicadornico.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.comunicadornico.entity.entityAdministrador;
import br.com.comunicadornico.service.serviceAdministrador;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/administradores")
@Tag(name = "Administradores", description = "Endpoints para gestão, cadastro e consulta de administradores")
public class controllerAdministrador {

    @Autowired
    private serviceAdministrador meuService;

    // 1- Criar Administrador (POST) - Rota Pública
    @PostMapping
    @Operation(summary = "Criar novo administrador", description = "Registra um administrador no banco de dados. A senha será criptografada automaticamente (Rota pública).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Administrador criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro na validação dos dados enviados")
    })
    public ResponseEntity<entityAdministrador> criarAdministrador(@RequestBody entityAdministrador novoAdministrador) {
        entityAdministrador administradorCriado = meuService.criarAdministrador(novoAdministrador);
        return ResponseEntity.status(HttpStatus.CREATED).body(administradorCriado);
    }

    // 2. READ Consultar todos os administradores - Rota Protegida
    @GetMapping
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Listar todos", description = "Retorna uma lista de todos os administradores cadastrados (Requer Token).")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<entityAdministrador>> listarTodosAdministradores() {
        List<entityAdministrador> lista = meuService.listarAdministradores();
        return ResponseEntity.status(HttpStatus.OK).body(lista);
    }

    // 3. READ - Consultar administrador pelo nome - Rota Protegida
    @GetMapping("/buscaradmnome")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Buscar por nome", description = "Pesquisa administradores que contenham o nome informado (Requer Token).")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    public ResponseEntity<List<entityAdministrador>> buscarPorNome(@RequestParam String nome) {
        List<entityAdministrador> administradores = meuService.pesquisarAdministrador(nome);
        return ResponseEntity.status(HttpStatus.OK).body(administradores);
    }

    // 4. READ - Buscar por ID - Rota Protegida
    @GetMapping("/{idAdministrador}")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Buscar por ID", description = "Retorna os dados de um administrador específico pelo seu ID (Requer Token).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Administrador encontrado"),
        @ApiResponse(responseCode = "404", description = "Administrador não encontrado")
    })
    public ResponseEntity<entityAdministrador> buscarAdministradorPorId(@PathVariable Long idAdministrador) {
        entityAdministrador administradorEncontrado = meuService.pesquisarAdministradorPorId(idAdministrador);
        return ResponseEntity.ok(administradorEncontrado);
    }

    // 5. UPDATE - Atualizar um registro existente (PUT) - Rota Protegida
    @PutMapping("/{idAdministrador}")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Atualizar administrador", description = "Atualiza os dados de um administrador existente pelo ID (Requer Token).")
    @ApiResponse(responseCode = "200", description = "Administrador atualizado com sucesso")
    public ResponseEntity<entityAdministrador> atualizarAdministrador(@PathVariable Long idAdministrador, @RequestBody entityAdministrador administradorAtualizado) {
        entityAdministrador administrador = meuService.atualizarAdministrador(idAdministrador, administradorAtualizado);
        return ResponseEntity.status(HttpStatus.OK).body(administrador);
    }

    // 6. DELETE - Excluir um registro (DELETE) - Rota Protegida
    @DeleteMapping("/{idAdministrador}")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Excluir administrador", description = "Remove um administrador do banco de dados pelo ID (Requer Token).")
    @ApiResponse(responseCode = "204", description = "Administrador excluído com sucesso")
    public ResponseEntity<Void> excluirAdministrador(@PathVariable Long idAdministrador) {
        meuService.excluirAdministrador(idAdministrador);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}