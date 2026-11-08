package br.com.comunicadornico.entity;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "tb_tiradesentenca")
@Schema(description = "Entidade que representa a tira de sentença do comunicador (frase montada pelo usuário)")
public class entityTiraDeSentenca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único da tira de sentença", example = "1")
    private Long idTiraDeSentenca;

    // ALTERADO: List<Integer> -> List<Long>, pois idBotao em entityBotao é Long
    @ElementCollection
    @CollectionTable(name = "tb_tiradesentenca_sequencia", joinColumns = @JoinColumn(name = "fkTiraDeSentenca"))
    @OrderColumn(name = "posicao")
    @Column(name = "idBotao")
    @Schema(description = "Lista ordenada com os IDs dos botões que formam a frase", example = "[1, 5, 12]")
    private List<Long> sequenciaBotoes = new ArrayList<>();

    @Column(name = "textoFormatado")
    @Schema(description = "Texto completo gerado pela junção das legendas dos botões", example = "Eu quero beber água")
    private String textoFormatado;

    // sugestão por IA não é usada nesta etapa; @Transient = não vira coluna
    @Transient
    @Schema(description = "Lista temporária com IDs de botões sugeridos (não é salvo no banco)", example = "[3, 8]")
    private List<Long> sugestao = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "fkUsuario")
    @Schema(hidden = true)
    private entityUsuario usuario;

    @OneToMany(mappedBy = "tiraDeSentenca")
    @Schema(hidden = true)
    private List<entityBotao> botoes;

    public entityTiraDeSentenca() {
        this.sequenciaBotoes = new ArrayList<>();
        this.sugestao = new ArrayList<>();
    }

    public entityTiraDeSentenca(List<Long> sequenciaBotoes, String textoFormatado, List<Long> sugestao) {
        this.sequenciaBotoes = sequenciaBotoes;
        this.textoFormatado = textoFormatado;
        this.sugestao = sugestao;
    }

    public Long getIdTiraDeSentenca() { return idTiraDeSentenca; }
    public void setIdTiraDeSentenca(Long idTiraDeSentenca) { this.idTiraDeSentenca = idTiraDeSentenca; }

    public List<Long> getSequenciaBotoes() { return sequenciaBotoes; }
    public void setSequenciaBotoes(List<Long> sequenciaBotoes) { this.sequenciaBotoes = sequenciaBotoes; }

    public String getTextoFormatado() { return textoFormatado; }
    public void setTextoFormatado(String textoFormatado) { this.textoFormatado = textoFormatado; }

    public List<Long> getSugestao() { return sugestao; }
    public void setSugestao(List<Long> sugestao) { this.sugestao = sugestao; }

    public entityUsuario getUsuario() { return usuario; }
    public void setUsuario(entityUsuario usuario) { this.usuario = usuario; }

    public List<entityBotao> getBotoes() { return botoes; }
    public void setBotoes(List<entityBotao> botoes) { this.botoes = botoes; }
}