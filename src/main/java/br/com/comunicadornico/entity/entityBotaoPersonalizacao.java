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
        name = "tb_botao_personalizacao",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_usuario_botao_personalizacao",
                        columnNames = {
                                "fk_usuario",
                                "fk_botao"
                        }
                )
        }
)
@Schema(
        description = "Personalização de um botão do comunicador para um usuário"
)
public class entityBotaoPersonalizacao {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long idBotaoPersonalizacao;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "fk_usuario",
            nullable = false
    )
    @Schema(hidden = true)
    private entityUsuario usuario;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "fk_botao",
            nullable = false
    )
    @Schema(hidden = true)
    private entityBotao botao;

    @Column(
            name = "legenda",
            length = 255
    )
    @Schema(
            description = "Legenda personalizada do botão"
    )
    private String legenda;

    @Column(
            name = "cor_fundo",
            length = 7
    )
    @Schema(
            description = "Cor personalizada do fundo do botão",
            example = "#FFFFFF"
    )
    private String corFundo;

    @Column(
            name = "cor_fundo_legenda",
            length = 7
    )
    @Schema(
            description = "Cor personalizada do fundo da legenda",
            example = "#B3B4B7"
    )
    private String corFundoLegenda;

    @Column(
            name = "cor_legenda",
            length = 7
    )
    @Schema(
            description = "Cor personalizada do texto da legenda",
            example = "#000000"
    )
    private String corLegenda;

    @Column(
            name = "exibir_legenda"
    )
    @Schema(
            description = "Define se a legenda deve ser exibida"
    )
    private Boolean exibirLegenda;

    @Column(
            name = "tamanho_botao"
    )
    @Schema(
            description = "Tamanho personalizado do botão"
    )
    private Integer tamanhoBotao;

    @Column(
            name = "oculto"
    )
    @Schema(
            description = "Define se o botão está oculto para o usuário"
    )
    private Boolean oculto;

    @Column(
            name = "foto",
            length = 2048
    )
    @Schema(
            description = "URL da foto personalizada armazenada no Cloudinary"
    )
    private String foto;

    @Column(
            name = "foto_public_id",
            length = 500
    )
    @Schema(hidden = true)
    private String fotoPublicId;

    @Column(
            name = "audio_personalizado",
            length = 2048
    )
    @Schema(
            description = "URL do áudio personalizado armazenado no Cloudinary"
    )
    private String audioPersonalizado;

    @Column(
            name = "audio_public_id",
            length = 500
    )
    @Schema(hidden = true)
    private String audioPublicId;

    public entityBotaoPersonalizacao() {
    }

    public entityBotaoPersonalizacao(
            entityUsuario usuario,
            entityBotao botao,
            String foto,
            String fotoPublicId,
            String audioPersonalizado,
            String audioPublicId) {

        this.usuario = usuario;
        this.botao = botao;
        this.foto = foto;
        this.fotoPublicId = fotoPublicId;
        this.audioPersonalizado = audioPersonalizado;
        this.audioPublicId = audioPublicId;
    }

    public Long getIdBotaoPersonalizacao() {
        return idBotaoPersonalizacao;
    }

    public void setIdBotaoPersonalizacao(
            Long idBotaoPersonalizacao) {

        this.idBotaoPersonalizacao =
                idBotaoPersonalizacao;
    }

    public entityUsuario getUsuario() {
        return usuario;
    }

    public void setUsuario(
            entityUsuario usuario) {

        this.usuario = usuario;
    }

    public entityBotao getBotao() {
        return botao;
    }

    public void setBotao(
            entityBotao botao) {

        this.botao = botao;
    }

    public String getLegenda() {
        return legenda;
    }

    public void setLegenda(
            String legenda) {

        this.legenda = legenda;
    }

    public String getCorFundo() {
        return corFundo;
    }

    public void setCorFundo(
            String corFundo) {

        this.corFundo = corFundo;
    }

    public String getCorFundoLegenda() {
        return corFundoLegenda;
    }

    public void setCorFundoLegenda(
            String corFundoLegenda) {

        this.corFundoLegenda =
                corFundoLegenda;
    }

    public String getCorLegenda() {
        return corLegenda;
    }

    public void setCorLegenda(
            String corLegenda) {

        this.corLegenda = corLegenda;
    }

    public Boolean getExibirLegenda() {
        return exibirLegenda;
    }

    public void setExibirLegenda(
            Boolean exibirLegenda) {

        this.exibirLegenda =
                exibirLegenda;
    }

    public Integer getTamanhoBotao() {
        return tamanhoBotao;
    }

    public void setTamanhoBotao(
            Integer tamanhoBotao) {

        this.tamanhoBotao =
                tamanhoBotao;
    }

    public Boolean getOculto() {
        return oculto;
    }

    public void setOculto(
            Boolean oculto) {

        this.oculto = oculto;
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

    public String getAudioPersonalizado() {
        return audioPersonalizado;
    }

    public void setAudioPersonalizado(
            String audioPersonalizado) {

        this.audioPersonalizado =
                audioPersonalizado;
    }

    public String getAudioPublicId() {
        return audioPublicId;
    }

    public void setAudioPublicId(
            String audioPublicId) {

        this.audioPublicId =
                audioPublicId;
    }
}