package br.com.comunicadornico.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class EditarCategoriaUsuarioDTO {

    @NotNull(
            message = "O usuário é obrigatório."
    )
    private Long idUsuario;

    @NotBlank(
            message = "O nome da categoria é obrigatório."
    )
    private String nome;

    private boolean oculto;

    @Valid
    @NotEmpty(
            message = "Selecione pelo menos um botão para a categoria."
    )
    private List<BotaoCategoriaOrdemDTO> botoes =
            new ArrayList<>();

    public EditarCategoriaUsuarioDTO() {
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(
            Long idUsuario) {

        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(
            String nome) {

        this.nome = nome;
    }

    public boolean isOculto() {
        return oculto;
    }

    public void setOculto(
            boolean oculto) {

        this.oculto = oculto;
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
                        ? botoes
                        : new ArrayList<>();
    }
}