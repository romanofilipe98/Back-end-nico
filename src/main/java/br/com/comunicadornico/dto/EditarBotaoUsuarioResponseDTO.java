package br.com.comunicadornico.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Resposta da edição de um botão para um usuário"
)
public class EditarBotaoUsuarioResponseDTO {

    @Schema(
            description = "ID do botão editado",
            example = "41"
    )
    private Long idBotao;

    @Schema(
            description = "ID do usuário que recebe a edição",
            example = "1"
    )
    private Long idUsuario;

    @Schema(
            description = "Nome do usuário",
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
            example = "Pessoas"
    )
    private String nomeCategoria;

    @Schema(
            description = "Legenda final do botão",
            example = "Quero um abraço"
    )
    private String legenda;

    @Schema(
            description = "Cor de fundo final do botão",
            example = "#FFFFFF"
    )
    private String corFundo;

    @Schema(
            description = "Cor de fundo final da tira de legenda",
            example = "#B3B4B7"
    )
    private String corFundoLegenda;

    @Schema(
            description = "Cor final do texto da legenda",
            example = "#000000"
    )
    private String corLegenda;

    @Schema(
            description = "Indica se a legenda deve ser exibida",
            example = "true"
    )
    private boolean exibirLegenda;

    @Schema(
            description = "Tamanho final do botão",
            example = "1"
    )
    private int tamanhoBotao;

    @Schema(
            description = "Indica se o botão está oculto para o usuário",
            example = "false"
    )
    private boolean oculto;

    @Schema(
            description = "URL final da imagem do botão"
    )
    private String foto;

    @Schema(
            description = "URL final do áudio personalizado"
    )
    private String audioPersonalizado;

    @Schema(
            description = "Indica se existe personalização para o usuário",
            example = "true"
    )
    private boolean personalizado;

    @Schema(
            description = "Indica se o botão base pertence exclusivamente ao usuário",
            example = "false"
    )
    private boolean exclusivoUsuario;

    public EditarBotaoUsuarioResponseDTO() {
    }

    public EditarBotaoUsuarioResponseDTO(
            Long idBotao,
            Long idUsuario,
            String nomeUsuario,
            Long idCategoria,
            String nomeCategoria,
            String legenda,
            String corFundo,
            String corFundoLegenda,
            String corLegenda,
            boolean exibirLegenda,
            int tamanhoBotao,
            boolean oculto,
            String foto,
            String audioPersonalizado,
            boolean personalizado,
            boolean exclusivoUsuario) {

        this.idBotao = idBotao;
        this.idUsuario = idUsuario;
        this.nomeUsuario = nomeUsuario;
        this.idCategoria = idCategoria;
        this.nomeCategoria = nomeCategoria;
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
        this.personalizado =
                personalizado;
        this.exclusivoUsuario =
                exclusivoUsuario;
    }

    public Long getIdBotao() {
        return idBotao;
    }

    public void setIdBotao(
            Long idBotao) {

        this.idBotao = idBotao;
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

    public boolean isPersonalizado() {
        return personalizado;
    }

    public void setPersonalizado(
            boolean personalizado) {

        this.personalizado =
                personalizado;
    }

    public boolean isExclusivoUsuario() {
        return exclusivoUsuario;
    }

    public void setExclusivoUsuario(
            boolean exclusivoUsuario) {

        this.exclusivoUsuario =
                exclusivoUsuario;
    }
}