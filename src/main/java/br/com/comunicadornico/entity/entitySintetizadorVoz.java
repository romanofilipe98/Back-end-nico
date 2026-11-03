package br.com.comunicadornico.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "tb_sintetizadorvoz")
@Schema(description = "Entidade que representa o sintetizador de voz do sistema")

public class entitySintetizadorVoz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único do sinteizador de voz", example = "1")
    private Long idSintetizador;
    
 // construtor padrao (exigido pelo JPA)
    public entitySintetizadorVoz() {
    }
 
    public Long getIdSintetizador() {
        return idSintetizador;
    }
 
    public void setIdSintetizador(Long idSintetizador) {
        this.idSintetizador = idSintetizador;
    }
}

