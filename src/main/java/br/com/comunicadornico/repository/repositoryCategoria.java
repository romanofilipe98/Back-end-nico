package br.com.comunicadornico.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.comunicadornico.entity.entityCategoria;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface repositoryCategoria extends JpaRepository<entityCategoria, Long> {

    // cria o SELECT * FROM tb_categoria WHERE nome (ou seja, faz a função de pesquisar pelo nome)
    Optional<entityCategoria> findByNome(String nome);

    List<entityCategoria> findByNomeContainingIgnoreCase(String nome);

    @Query("""
        SELECT DISTINCT c
        FROM entityCategoria c
        WHERE c.usuario IS NULL
           OR c.usuario.idUsuario = :idUsuario
        ORDER BY c.posicaoCat, c.idCategoria
        """)
    List<entityCategoria> findCategoriasDisponiveisParaUsuario(
            @Param("idUsuario") Long idUsuario
    );
}
