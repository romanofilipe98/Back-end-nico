package br.com.comunicadornico.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

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
}