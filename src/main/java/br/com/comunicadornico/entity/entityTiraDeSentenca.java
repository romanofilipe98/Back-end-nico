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

    // criar chave primaria
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único da tira de sentença", example = "1")
    private Long idTiraDeSentenca;

    // variaveis
    // CORRIGIDO: o banco não guarda uma lista numa coluna simples.
    // @ElementCollection cria uma tabela auxiliar com os ids dos botões
    // e @OrderColumn guarda a posição de cada um, mantendo a ordem da frase.
    @ElementCollection
    @CollectionTable(name = "tb_tiradesentenca_sequencia", joinColumns = @JoinColumn(name = "fkTiraDeSentenca"))
    @OrderColumn(name = "posicao")
    @Column(name= "idBotao")
    @Schema(description = "Lista ordenada com os IDs dos botões que formam a frase", example = "[1, 5, 12]")
    private List<Integer> sequenciaBotoes = new ArrayList<>();

    @Column(name= "textoFormatado")
    @Schema(description = "Texto completo gerado pela junção das legendas dos botões", example = "Eu quero beber água")
    private String textoFormatado;

    // a sugestão por IA não será usada nesta etapa.
    // @Transient faz o JPA ignorar este atributo (não vira coluna no banco),
    // mas ele continua existindo na classe, como no diagrama.
    @Transient
    @Schema(description = "Lista temporária com IDs de botões sugeridos para a próxima palavra (não é salvo no banco)", example = "[3, 8]")
    private List<Integer> sugestao = new ArrayList<>();

    // criando relacionamento das tabelas
    // relação tira x usuario (1..1)
    @OneToOne
    @JoinColumn(name = "fkUsuario")
    @Schema(hidden = true)
    private entityUsuario usuario;

    // relação tira x botão
    @OneToMany(mappedBy = "tiraDeSentenca")
    @Schema(hidden = true)
    private List<entityBotao> botoes;

    // construtor padrão (exigido pelo JPA)
    public entityTiraDeSentenca() {
        this.sequenciaBotoes = new ArrayList<>();
        this.sugestao = new ArrayList<>();
    }
 
    // construtor com todos os atributos (exceto o id, gerado pelo banco)
    public entityTiraDeSentenca(List<Integer> sequenciaBotoes, String textoFormatado, List<Integer> sugestao) {
        this.sequenciaBotoes = sequenciaBotoes;
        this.textoFormatado = textoFormatado;
        this.sugestao = sugestao;
    }
 
    public Long getIdTiraDeSentenca() {
        return idTiraDeSentenca;
    }
 
    public void setIdTiraDeSentenca(Long idTiraDeSentenca) {
        this.idTiraDeSentenca = idTiraDeSentenca;
    }
 
    public List<Integer> getSequenciaBotoes() {
        return sequenciaBotoes;
    }
 
    public void setSequenciaBotoes(List<Integer> sequenciaBotoes) {
        this.sequenciaBotoes = sequenciaBotoes;
    }
 
    public String getTextoFormatado() {
        return textoFormatado;
    }
 
    public void setTextoFormatado(String textoFormatado) {
        this.textoFormatado = textoFormatado;
    }
 
    public List<Integer> getSugestao() {
        return sugestao;
    }
 
    public void setSugestao(List<Integer> sugestao) {
        this.sugestao = sugestao;
    }

    // ADICIONADO: getters e setters dos relacionamentos
    public entityUsuario getUsuario() {
        return usuario;
    }

    public void setUsuario(entityUsuario usuario) {
        this.usuario = usuario;
    }

    public List<entityBotao> getBotoes() {
        return botoes;
    }

    public void setBotoes(List<entityBotao> botoes) {
        this.botoes = botoes;
    }
}