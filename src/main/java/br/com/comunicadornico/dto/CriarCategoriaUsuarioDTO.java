package br.com.comunicadornico.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class CriarCategoriaUsuarioDTO {

    @NotBlank(
            message =
                    "O nome da categoria é obrigatório."
    )
    private String nome;

    @NotNull(
            message =
                    "O usuário é obrigatório."
    )
    private Long idUsuario;

    @Valid
    @NotEmpty(
            message =
                    "Selecione pelo menos um botão para a categoria."
    )
    private List<BotaoCategoriaOrdemDTO> botoes =
            new ArrayList<>();

    public CriarCategoriaUsuarioDTO() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(
            String nome) {

        this.nome = nome;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(
            Long idUsuario) {

        this.idUsuario = idUsuario;
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