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

import br.com.comunicadornico.entity.entityUsuario;
import br.com.comunicadornico.service.serviceUsuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController //avisa o spring que a classe vai responder a requisitos da web
@RequestMapping("/api/usuarios") //define o endereço base Url
@Tag(name = "Usuários", description = "Endpoints para gestão, cadastro e consulta de Usuarios")
public class controllerUsuario {

    @Autowired
    private serviceUsuario meuService;

    // 1 - Metodo para criar um novo usuario (POST) - Rota Protegida
    @PostMapping //informa que o post dessa classe deve passar por aqui, é uma etiqueta de anotação
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Criar novo usuario", description = "Registra um novo Usuário no banco de dados (Requer Token).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro na validação dos dados enviados")
    }) 
    public ResponseEntity<entityUsuario> criarUsuario(@RequestBody entityUsuario novoUsuario) {
        entityUsuario usuarioCriado = meuService.criarUsuario(novoUsuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCriado);
    }

    // 2. Read Consultar todos os registros - Rota Protegida
    @GetMapping
    @SecurityRequirement(name="BearerAuth")
    @Operation(summary = "Listar todos os usuários", description = "Retorna uma lista de todos os usuários cadastrados (Requer Token).")
    @ApiResponse(responseCode = "200", description = "Usuários retornados com sucesso")
    public ResponseEntity<List<entityUsuario>> listarTodos() {
        List<entityUsuario> lista = meuService.listarUsuarios();

        return ResponseEntity.status(HttpStatus.OK).body(lista);
    }

    // READ - Consultar registros pelo nome - Rota Protegida
    @GetMapping("/buscar")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Buscar usuário por nome", description = "Pesquisa um usuário que contenham o nome informado (Requer Token).")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    public ResponseEntity<List<entityUsuario>> buscarPorNome(@RequestParam String nome) {
        List<entityUsuario> usuarios = meuService.pesquisarUsuario(nome);
        return ResponseEntity.status(HttpStatus.OK).body(usuarios);
    }

    // READ - Consultar por ID
    @GetMapping("/{idUsuario}")
    @SecurityRequirement(name="BearerAuth")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna os dados de um usuário específico pelo seu ID (Requer Token).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<entityUsuario> buscarUsuarioPorId(@PathVariable Long idUsuario) {

        entityUsuario usuarioEncontrado = meuService.pesquisarUsuarioPorId(idUsuario);

        return ResponseEntity.ok(usuarioEncontrado);

    }

    //4. UPDATE - Atualizar um registro existente (PUT)
    @PutMapping("/{idUsuario}")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente pelo ID (Requer Token).")
    @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso")
    public ResponseEntity<entityUsuario> atualizarUsuario(@PathVariable Long idUsuario, @RequestBody entityUsuario usuarioAtualizado) {
        entityUsuario usuario = meuService.atualizarUsuario(idUsuario, usuarioAtualizado);

        return ResponseEntity.status(HttpStatus.OK).body(usuario);
    }

    // 5. DELETE - Excluir um registro (DELETE)
    @DeleteMapping("/{idUsuario}")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Excluir usuário", description = "Remove um usuário do banco de dados pelo ID (Requer Token).")
    @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso")
    public ResponseEntity<Void> excluirUsuario(@PathVariable Long idUsuario) {

        meuService.excluirUsuario(idUsuario);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}