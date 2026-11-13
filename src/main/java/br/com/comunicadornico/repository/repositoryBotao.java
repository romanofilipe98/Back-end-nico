package br.com.comunicadornico.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import br.com.comunicadornico.entity.entityBotao;

public interface repositoryBotao extends JpaRepository<entityBotao, Long> {

    // cria o SELECT * FROM tb_botao WHERE nome (ou seja, faz a função de pesquisar pelo nome)
    Optional<entityBotao> findByLegenda(String legenda);

    List<entityBotao> findByLegendaContainingIgnoreCase(String legenda);

    @Query("""
        SELECT DISTINCT b
        FROM entityBotao b
        WHERE b.usuario IS NULL
           OR b.usuario.idUsuario = :idUsuario
        ORDER BY b.idBotao
        """)
    List<entityBotao> findBotoesDisponiveisParaUsuario(
            @Param("idUsuario") Long idUsuario
    );

    @Modifying
    @Transactional
    @Query(
            value = """
                DELETE FROM tb_registrodeatividade
                WHERE fk_botao = :idBotao
                """,
            nativeQuery = true
    )
    void excluirRegistrosDeAtividadeDoBotao(
            @Param("idBotao") Long idBotao
    );
}
