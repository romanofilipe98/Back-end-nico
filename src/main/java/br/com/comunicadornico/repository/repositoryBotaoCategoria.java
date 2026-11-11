package br.com.comunicadornico.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.comunicadornico.entity.entityBotaoCategoria;

public interface repositoryBotaoCategoria
        extends JpaRepository<entityBotaoCategoria, Long> {

    List<entityBotaoCategoria>
            findByCategoriaIdCategoriaOrderByPosicaoAsc(
                    Long idCategoria
            );

    Optional<entityBotaoCategoria>
            findByBotaoIdBotaoAndCategoriaIdCategoria(
                    Long idBotao,
                    Long idCategoria
            );

    boolean existsByBotaoIdBotaoAndCategoriaIdCategoria(
            Long idBotao,
            Long idCategoria
    );

    @Query("""
            SELECT COALESCE(MAX(relacao.posicao), 0)
            FROM entityBotaoCategoria relacao
            WHERE relacao.categoria.idCategoria = :idCategoria
            """)
    int buscarMaiorPosicaoDaCategoria(
            @Param("idCategoria")
            Long idCategoria
    );
}