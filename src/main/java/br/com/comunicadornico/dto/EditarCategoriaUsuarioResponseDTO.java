package br.com.comunicadornico.dto;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Resposta da edição de uma categoria para um usuário"
)
public class EditarCategoriaUsuarioResponseDTO {

    @Schema(
            description = "ID da categoria editada",
            example = "10"
    )
    private Long idCategoria;

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
            description = "Nome final da categoria",
            example = "Minha rotina atualizada"
    )
    private String nome;

    @Schema(
            description = "URL final da imagem da categoria"
    )
    private String foto;

    @Schema(
            description = "Posição da categoria no carrossel",
            example = "10"
    )
    private int posicao;

    @Schema(
            description = "Indica se a categoria está oculta para o usuário",
            example = "false"
    )
    private boolean oculto;

    @Schema(
            description = "Indica se existe personalização para o usuário",
            example = "true"
    )
    private boolean personalizada;

    @Schema(
            description = "Indica se a categoria base pertence exclusivamente ao usuário",
            example = "false"
    )
    private boolean exclusivaUsuario;

    @Schema(
            description = "Botões associados à categoria na ordem de exibição"
    )
    private List<BotaoCategoriaOrdemDTO> botoes =
            new ArrayList<>();

    public EditarCategoriaUsuarioResponseDTO() {
    }

    public EditarCategoriaUsuarioResponseDTO(
            Long idCategoria,
            Long idUsuario,
            String nomeUsuario,
            String nome,
            String foto,
            int posicao,
            boolean oculto,
            boolean personalizada,
            boolean exclusivaUsuario,
            List<BotaoCategoriaOrdemDTO> botoes) {

        this.idCategoria = idCategoria;
        this.idUsuario = idUsuario;
        this.nomeUsuario = nomeUsuario;
        this.nome = nome;
        this.foto = foto;
        this.posicao = posicao;
        this.oculto = oculto;
        this.personalizada = personalizada;
        this.exclusivaUsuario =
                exclusivaUsuario;

        this.botoes =
                botoes != null
                        ? new ArrayList<>(botoes)
                        : new ArrayList<>();
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(
            Long idCategoria) {

        this.idCategoria =
                idCategoria;
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

    public int getPosicao() {
        return posicao;
    }

    public void setPosicao(
            int posicao) {

        this.posicao = posicao;
    }

    public boolean isOculto() {
        return oculto;
    }

    public void setOculto(
            boolean oculto) {

        this.oculto = oculto;
    }

    public boolean isPersonalizada() {
        return personalizada;
    }

    public void setPersonalizada(
            boolean personalizada) {

        this.personalizada =
                personalizada;
    }

    public boolean isExclusivaUsuario() {
        return exclusivaUsuario;
    }

    public void setExclusivaUsuario(
            boolean exclusivaUsuario) {

        this.exclusivaUsuario =
                exclusivaUsuario;
    }

    public List<BotaoCategoriaOrdemDTO>
            getBotoes() {

        return botoes;
    }

    public void setBotoes(
            List<BotaoCategoriaOrdemDTO>
                    botoes) {

        this.botoes =
                botoes != null
                        ? new ArrayList<>(botoes)
                        : new ArrayList<>();
    }
}