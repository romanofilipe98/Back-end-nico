package br.com.comunicadornico.dto;

import java.util.List;

import br.com.comunicadornico.entity.entityCategoria;

public class CriarCategoriaUsuarioResponseDTO {

    private Long idCategoria;
    private String nome;
    private String foto;
    private int posicao;
    private Long idUsuario;
    private String nomeUsuario;
    private List<BotaoCategoriaOrdemDTO> botoes;

    public CriarCategoriaUsuarioResponseDTO() {
    }

    public CriarCategoriaUsuarioResponseDTO(
            Long idCategoria,
            String nome,
            String foto,
            int posicao,
            Long idUsuario,
            String nomeUsuario,
            List<BotaoCategoriaOrdemDTO> botoes) {

        this.idCategoria = idCategoria;
        this.nome = nome;
        this.foto = foto;
        this.posicao = posicao;
        this.idUsuario = idUsuario;
        this.nomeUsuario = nomeUsuario;
        this.botoes = botoes;
    }

    public static CriarCategoriaUsuarioResponseDTO de(
            entityCategoria categoria,
            List<BotaoCategoriaOrdemDTO> botoes) {

        return new CriarCategoriaUsuarioResponseDTO(
                categoria.getIdCategoria(),
                categoria.getNome(),
                categoria.getFotoCat(),
                categoria.getPosicaoCat(),
                categoria.getUsuario() != null
                        ? categoria.getUsuario()
                                .getIdUsuario()
                        : null,
                categoria.getUsuario() != null
                        ? categoria.getUsuario()
                                .getNome()
                        : null,
                botoes
        );
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(
            Long idCategoria) {

        this.idCategoria =
                idCategoria;
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

    public List<BotaoCategoriaOrdemDTO>
            getBotoes() {

        return botoes;
    }

    public void setBotoes(
            List<BotaoCategoriaOrdemDTO>
                    botoes) {

        this.botoes = botoes;
    }
}