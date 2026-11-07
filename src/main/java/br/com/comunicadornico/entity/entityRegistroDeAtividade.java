package br.com.comunicadornico.entity;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_registrodeatividade")
@Schema(description = "Entidade que representa o registro de atividade do usuário")
public class entityRegistroDeAtividade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único do registro de atividade", example = "1")
    private Long idRegistro;

    // ALTERADO: LocalDate -> LocalDateTime, para guardar também a hora do último acionamento
    @Column(name = "dataHora")
    @Schema(description = "Data e hora do último acionamento", example = "2026-11-04T13:00:00")
    private LocalDateTime dataHora;

    @Column(name = "quantidadeCliques")
    @Schema(description = "Quantidade de vezes que o botão foi acionado", example = "2")
    private int quantidadeCliques;

    @ManyToOne
    @JoinColumn(name = "fkBotao")
    @Schema(hidden = true)
    private entityBotao botao;

    @ManyToOne
    @JoinColumn(name = "fkUsuario")
    @Schema(hidden = true)
    private entityUsuario usuario;

    public entityRegistroDeAtividade() {
    }

    public entityRegistroDeAtividade(LocalDateTime dataHora, int quantidadeCliques, entityUsuario usuario) {
        this.dataHora = dataHora;
        this.quantidadeCliques = quantidadeCliques;
        this.usuario = usuario;
    }

    public Long getIdRegistro() { return idRegistro; }
    public void setIdRegistro(Long idRegistro) { this.idRegistro = idRegistro; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public int getQuantidadeCliques() { return quantidadeCliques; }
    public void setQuantidadeCliques(int quantidadeCliques) { this.quantidadeCliques = quantidadeCliques; }

    public entityUsuario getUsuario() { return usuario; }
    public void setUsuario(entityUsuario usuario) { this.usuario = usuario; }

    public entityBotao getBotao() { return botao; }
    public void setBotao(entityBotao botao) { this.botao = botao; }
}