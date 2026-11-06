package br.com.comunicadornico.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.comunicadornico.entity.entityRegistroDeAtividade;

public interface repositoryRegistroDeAtividade extends JpaRepository<entityRegistroDeAtividade, Long> {

    // histórico de um usuário, do mais recente para o mais antigo
    List<entityRegistroDeAtividade> findByUsuarioIdUsuarioOrderByDataHoraDesc(Long idUsuario);

    // registro agregado do dia para o par usuário + botão
    @Query("select r from entityRegistroDeAtividade r "
         + "where r.usuario.idUsuario = :idUsuario and r.botao.idBotao = :idBotao "
         + "and r.dataHora >= :inicio and r.dataHora < :fim")
    Optional<entityRegistroDeAtividade> buscarDoDia(@Param("idUsuario") Long idUsuario,
                                                    @Param("idBotao") Long idBotao,
                                                    @Param("inicio") LocalDateTime inicio,
                                                    @Param("fim") LocalDateTime fim);
}