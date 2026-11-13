package br.com.comunicadornico.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.comunicadornico.entity.entityBotaoPersonalizacao;

@Repository
public interface repositoryBotaoPersonalizacao
        extends JpaRepository<
                entityBotaoPersonalizacao,
                Long> {

    Optional<entityBotaoPersonalizacao>
            findByUsuarioIdUsuarioAndBotaoIdBotao(
                    Long idUsuario,
                    Long idBotao
            );

    List<entityBotaoPersonalizacao>
            findByUsuarioIdUsuario(
                    Long idUsuario
            );

    List<entityBotaoPersonalizacao>
            findByBotaoIdBotao(
                    Long idBotao
            );

    boolean
            existsByUsuarioIdUsuarioAndBotaoIdBotao(
                    Long idUsuario,
                    Long idBotao
            );

    void
            deleteByUsuarioIdUsuarioAndBotaoIdBotao(
                    Long idUsuario,
                    Long idBotao
            );

    void deleteByBotaoIdBotao(
            Long idBotao
    );
}