package br.com.comunicadornico.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.comunicadornico.entity.entityCategoriaPersonalizacao;

@Repository
public interface repositoryCategoriaPersonalizacao
        extends JpaRepository<entityCategoriaPersonalizacao, Long> {

    Optional<entityCategoriaPersonalizacao>
            findByUsuarioIdUsuarioAndCategoriaIdCategoria(
                    Long idUsuario,
                    Long idCategoria
            );

    List<entityCategoriaPersonalizacao>
            findByUsuarioIdUsuario(Long idUsuario);

    boolean existsByUsuarioIdUsuarioAndCategoriaIdCategoria(
            Long idUsuario,
            Long idCategoria
    );

    void deleteByUsuarioIdUsuarioAndCategoriaIdCategoria(
            Long idUsuario,
            Long idCategoria
    );
}