package br.com.comunicadornico.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "tb_categoria_personalizacao",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_usuario_categoria_personalizacao",
                        columnNames = {
                                "fkUsuario",
                                "fkCategoria"
                        }
                )
        }
)
@Schema(
        description = "Personalização de uma categoria para um usuário"
)
public class entityCategoriaPersonalizacao {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long idCategoriaPersonalizacao;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "fkUsuario",
            nullable = false
    )
    @Schema(hidden = true)
    private entityUsuario usuario;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "fkCategoria",
            nullable = false
    )
    @Schema(hidden = true)
    private entityCategoria categoria;

    @Column(
            name = "nome",
            length = 255
    )
    @Schema(
            description = "Nome personalizado da categoria"
    )
    private String nome;

    @Column(
            name = "foto",
            length = 2048
    )
    @Schema(
            description = "URL da foto personalizada da categoria"
    )
    private String foto;

    @Column(
            name = "fotoPublicId",
            length = 500
    )
    @Schema(hidden = true)
    private String fotoPublicId;

    @Column(
            name = "oculto"
    )
    @Schema(
            description = "Define se a categoria está oculta para o usuário"
    )
    private Boolean oculto;

    public entityCategoriaPersonalizacao() {
    }

    public entityCategoriaPersonalizacao(
            entityUsuario usuario,
            entityCategoria categoria,
            String foto,
            String fotoPublicId) {

        this.usuario = usuario;
        this.categoria = categoria;
        this.foto = foto;
        this.fotoPublicId = fotoPublicId;
    }

    public Long getIdCategoriaPersonalizacao() {
        return idCategoriaPersonalizacao;
    }

    public void setIdCategoriaPersonalizacao(
            Long idCategoriaPersonalizacao) {

        this.idCategoriaPersonalizacao =
                idCategoriaPersonalizacao;
    }

    public entityUsuario getUsuario() {
        return usuario;
    }

    public void setUsuario(
            entityUsuario usuario) {

        this.usuario = usuario;
    }

    public entityCategoria getCategoria() {
        return categoria;
    }

    public void setCategoria(
            entityCategoria categoria) {

        this.categoria = categoria;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(
            String nome) {

        this.nome = nome;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(
            String foto) {

        this.foto = foto;
    }

    public String getFotoPublicId() {
        return fotoPublicId;
    }

    public void setFotoPublicId(
            String fotoPublicId) {

        this.fotoPublicId =
                fotoPublicId;
    }

    public Boolean getOculto() {
        return oculto;
    }

    public void setOculto(
            Boolean oculto) {

        this.oculto = oculto;
    }
}