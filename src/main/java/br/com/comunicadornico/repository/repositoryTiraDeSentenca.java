package br.com.comunicadornico.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.comunicadornico.entity.entityTiraDeSentenca;

public interface repositoryTiraDeSentenca extends JpaRepository<entityTiraDeSentenca, Long> {

    // relação 1..1, cada usuário tem no máximo uma tira
    Optional<entityTiraDeSentenca> findByUsuarioIdUsuario(Long idUsuario);
}