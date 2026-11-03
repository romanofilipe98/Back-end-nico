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

import br.com.comunicadornico.entity.entityCategoria;
import br.com.comunicadornico.service.serviceCategoria;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/categorias")
@Tag(name="Categorias", description="Endpoints para gestão, cadastro e consulta de categorias")
public class controllerCategoria {

    // 1- Cria Categoria (post) - Rota Protegida
    @Autowired
    private serviceCategoria meuService;

    @PostMapping
    @SecurityRequirement(name="BearerAuth")
    @Operation(summary = "Criar nova Categoria", description = "Registra uma nova Categoria no banco de dados (Requer Token).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro na validação dos dados enviados")
    })
    public ResponseEntity<entityCategoria> criarCategoria(@RequestBody entityCategoria novaCategoria) {

        entityCategoria categoriaCriada = meuService.criarCategoria(novaCategoria);

        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaCriada);
    }

    // 2. Read Consultar todas as categorias - Rota Protegida
    @GetMapping
    @SecurityRequirement(name="BearerAuth")
    @Operation(summary = "Listar Todos as Categorias", description = "Retorna uma lista de todos as categorias cadastrados (Requer Token).")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<entityCategoria>> listarTodos() {

        List<entityCategoria> lista = meuService.listarCategorias();

        return ResponseEntity.status(HttpStatus.OK).body(lista);
    }

    // READ - Consultar categoria pelo nome - Rota Protegida
    @GetMapping("/buscar")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Buscar categorias por nome", description = "Pesquisa categorias que contenham o nome informado (Requer Token).")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    public ResponseEntity<List<entityCategoria>> buscarPorNome(@RequestParam String nome) {
        List<entityCategoria> categorias = meuService.pesquisarCategoria(nome);
        return ResponseEntity.status(HttpStatus.OK).body(categorias);
    }

    // READ - Consultar por ID
    @GetMapping("/{idCategoria}")
    @SecurityRequirement(name="BearerAuth")
    @Operation(summary = "Buscar categorias por ID", description = "Retorna os dados de uma categoria específica pelo seu ID (Requer Token).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrado")
    })
    public ResponseEntity<entityCategoria> buscarCategoriaPorId(@RequestParam Long idCategoria) {

        entityCategoria categoriaEncontrada = meuService.pesquisarCategoriaPorId(idCategoria);

        return ResponseEntity.ok(categoriaEncontrada);

    }
    
    //4. UPTADE - Atualizar um registro existente (PUT) - Rota Protegida
    @PutMapping("/{idCategoria}")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Atualizar categoria", description = "Atualiza os dados de uma categoria existente pelo ID (Requer Token).")
    @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso")
    public ResponseEntity<entityCategoria> atualizarCategoria(@PathVariable Long idCategoria, @RequestBody entityCategoria categoriaAtualizada) {
        entityCategoria categoria = meuService.atualizarCategoria(idCategoria, categoriaAtualizada);
        return ResponseEntity.status(HttpStatus.OK).body(categoria);
    }


    // 5. DELETE - Excluir um registro (DELETE)
    @DeleteMapping("/{idCategoria}")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Excluir Categoria", description = "Remove uma categoria do banco de dados pelo ID (Requer Token).")
    @ApiResponse(responseCode = "204", description = "Categoria excluída com sucesso")
    public ResponseEntity<Void> excluirCategoria(@PathVariable Long idCategoria) {
        meuService.excluirCategoria(idCategoria);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
