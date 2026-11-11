package br.com.comunicadornico.dto;

import br.com.comunicadornico.entity.entityBotao;
import br.com.comunicadornico.entity.entityCategoria;
import br.com.comunicadornico.entity.entityUsuario;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description =
                "Resposta do cadastro de um botão exclusivo para um usuário"
)
public class CriarBotaoUsuarioResponseDTO {

    @Schema(
            description = "ID do botão criado",
            example = "81"
    )
    private Long idBotao;

    @Schema(
            description = "Legenda exibida no botão",
            example = "Quero desenhar"
    )
    private String legenda;

    @Schema(
            description = "Cor de fundo do botão",
            example = "#FFFFFF"
    )
    private String corFundo;

    @Schema(
            description = "Cor de fundo da tira de legenda",
            example = "#B3B4B7"
    )
    private String corFundoLegenda;

    @Schema(
            description = "Cor do texto da legenda",
            example = "#000000"
    )
    private String corLegenda;

    @Schema(
            description = "Indica se a legenda aparece no botão",
            example = "true"
    )
    private boolean exibirLegenda;

    @Schema(
            description = "Tamanho configurado para o botão",
            example = "1"
    )
    private int tamanhoBotao;

    @Schema(
            description = "Indica se o botão está oculto",
            example = "false"
    )
    private boolean oculto;

    @Schema(
            description = "URL da imagem do botão"
    )
    private String foto;

    @Schema(
            description = "URL do áudio personalizado"
    )
    private String audioPersonalizado;

    @Schema(
            description = "ID do usuário que pode acessar o botão",
            example = "1"
    )
    private Long idUsuario;

    @Schema(
            description = "Nome do usuário vinculado ao botão",
            example = "Usuário Teste"
    )
    private String nomeUsuario;

    @Schema(
            description = "ID da categoria associada",
            example = "2"
    )
    private Long idCategoria;

    @Schema(
            description = "Nome da categoria associada",
            example = "Higiene"
    )
    private String nomeCategoria;

    @Schema(
            description = "Posição calculada automaticamente dentro da categoria",
            example = "10"
    )
    private int posicao;

    public CriarBotaoUsuarioResponseDTO() {
    }

    public CriarBotaoUsuarioResponseDTO(
            Long idBotao,
            String legenda,
            String corFundo,
            String corFundoLegenda,
            String corLegenda,
            boolean exibirLegenda,
            int tamanhoBotao,
            boolean oculto,
            String foto,
            String audioPersonalizado,
            Long idUsuario,
            String nomeUsuario,
            Long idCategoria,
            String nomeCategoria,
            int posicao) {

        this.idBotao = idBotao;
        this.legenda = legenda;
        this.corFundo = corFundo;
        this.corFundoLegenda =
                corFundoLegenda;
        this.corLegenda = corLegenda;
        this.exibirLegenda =
                exibirLegenda;
        this.tamanhoBotao =
                tamanhoBotao;
        this.oculto = oculto;
        this.foto = foto;
        this.audioPersonalizado =
                audioPersonalizado;
        this.idUsuario = idUsuario;
        this.nomeUsuario =
                nomeUsuario;
        this.idCategoria =
                idCategoria;
        this.nomeCategoria =
                nomeCategoria;
        this.posicao = posicao;
    }

    public static CriarBotaoUsuarioResponseDTO de(
            entityBotao botao,
            entityCategoria categoria,
            int posicao) {

        entityUsuario usuario =
                botao.getUsuario();

        return new CriarBotaoUsuarioResponseDTO(
                botao.getIdBotao(),
                botao.getLegenda(),
                botao.getCorFundo(),
                botao.getCorFundoLegenda(),
                botao.getCorLegenda(),
                botao.isExibirLegenda(),
                botao.getTamanhoBotao(),
                botao.isOculto(),
                botao.getFoto(),
                botao.getAudioPersonalizado(),
                usuario != null
                        ? usuario.getIdUsuario()
                        : null,
                usuario != null
                        ? usuario.getNome()
                        : null,
                categoria != null
                        ? categoria.getIdCategoria()
                        : null,
                categoria != null
                        ? categoria.getNome()
                        : null,
                posicao
        );
    }

    public Long getIdBotao() {
        return idBotao;
    }

    public void setIdBotao(
            Long idBotao) {

        this.idBotao = idBotao;
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

        this.corLegenda =
                corLegenda;
    }

    public boolean isExibirLegenda() {
        return exibirLegenda;
    }

    public void setExibirLegenda(
            boolean exibirLegenda) {

        this.exibirLegenda =
                exibirLegenda;
    }

    public int getTamanhoBotao() {
        return tamanhoBotao;
    }

    public void setTamanhoBotao(
            int tamanhoBotao) {

        this.tamanhoBotao =
                tamanhoBotao;
    }

    public boolean isOculto() {
        return oculto;
    }

    public void setOculto(
            boolean oculto) {

        this.oculto = oculto;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(
            String foto) {

        this.foto = foto;
    }

    public String getAudioPersonalizado() {
        return audioPersonalizado;
    }

    public void setAudioPersonalizado(
            String audioPersonalizado) {

        this.audioPersonalizado =
                audioPersonalizado;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(
            Long idUsuario) {

        this.idUsuario = idUsuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(
            String nomeUsuario) {

        this.nomeUsuario =
                nomeUsuario;
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(
            Long idCategoria) {

        this.idCategoria =
                idCategoria;
    }

    public String getNomeCategoria() {
        return nomeCategoria;
    }

    public void setNomeCategoria(
            String nomeCategoria) {

        this.nomeCategoria =
                nomeCategoria;
    }

    public int getPosicao() {
        return posicao;
    }

    public void setPosicao(
            int posicao) {

        this.posicao = posicao;
    }
}