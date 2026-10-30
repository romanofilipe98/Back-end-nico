package br.com.comunicadornico.entity;

// import
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "tb_usuario")
@Schema(description = "Entidade que representa um usuário cadastrado no sistema")
public class entityUsuario {

    // criando a chave primaria
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único do usuaário", example = "1")
    private Long idUsuario;

    // colunas
    @Column(name= "nome", nullable = false)
    @Schema(description = "Nome do usuário", example = "Juao de Magalhães")
    private String nome;

    @Column(name= "sexo")
    @Schema(description = "Sexo do usuário", example = "Masculino")
    private String sexo;

    @Column(name= "nivelAutismo")
    @Schema(description = "Nivel de autismo do usuário", example = "Nivel 3")
    private String nivelAutismo;

    @Column(name= "dataNascAut", nullable = false)
    @Schema(description = "Data de nascimento no padrão YYYY-MM-DD", example = "1990-05-15")
    private LocalDate dataNascAut;

    @Column(name= "voz")
    @Schema(description = "Tipo de voz do sintetizador true(Masculino) false(Feminino)", example = "true")
    private boolean voz;
    
    // criando os relacionamentos da tabela
    // relacioamento bidirecional de usuárioxadministrador (*..1)
    @ManyToOne
    @JsonIgnoreProperties("usuarios") // resolvendo erro de recursao
    @JoinColumn(name = "fkAdministrador") // criando a coluna que irá armazenar a chave estrangeira
    @Schema(hidden = true)
    private entityAdministrador administrador; // cria o objeto administrador que será armazenado

    //relacionamento bidirecional de usuário x registrodeatividade (1...*)
    @OneToMany(mappedBy = "usuario")
    @Schema(hidden = true)
    private List<entityRegistroDeAtividade> registros;

    //relaciomaneto bideracional de usuário x categoria (1..*)
    @OneToMany(mappedBy = "usuario")
    @Schema(hidden = true)
    private List<entityCategoria> categorias;

    // relaciomaneto bideracional de usuário x botão (1..*)
    @OneToMany(mappedBy = "usuario")
    @Schema(hidden = true)
    private List<entityBotao> botoes;

    //relacionamento usuario x tira de setença (1..1)
    @OneToOne(mappedBy = "usuario")
    @Schema(hidden = true)
    private entityTiraDeSentenca tiraDeSentenca;

    // Construtor padrão (exigido pelo JPA)
    public entityUsuario() {
    }
 
    // Construtor com todos os atributos (exceto o id, gerado pelo banco)
    public entityUsuario(String nome, String sexo, String nivelAutismo, LocalDate dataNascAut, boolean voz) {
        this.nome = nome;
        this.sexo = sexo;
        this.nivelAutismo = nivelAutismo;
        this.dataNascAut = dataNascAut;
        this.voz = voz;
    }
 
    public Long getIdUsuario() {
        return idUsuario;
    }
 
    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }
 
    public String getNome() {
        return nome;
    }
 
    public void setNome(String nome) {
        this.nome = nome;
    }
 
    public String getSexo() {
        return sexo;
    }
 
    public void setSexo(String sexo) {
        this.sexo = sexo;
    }
 
    public String getNivelAutismo() {
        return nivelAutismo;
    }
 
    public void setNivelAutismo(String nivelAutismo) {
        this.nivelAutismo = nivelAutismo;
    }
 
    public LocalDate getDataNascAut() {
        return dataNascAut;
    }
 
    public void setDataNascAut(LocalDate dataNascAut) {
        this.dataNascAut = dataNascAut;
    }
 
    public boolean isVoz() {
        return voz;
    }
 
    public void setVoz(boolean voz) {
        this.voz = voz;
    }

    // ADICIONADO: getters e setters dos relacionamentos
    public entityAdministrador getAdministrador() {
        return administrador;
    }

    public void setAdministrador(entityAdministrador administrador) {
        this.administrador = administrador;
    }

    public List<entityRegistroDeAtividade> getRegistros() {
        return registros;
    }

    public void setRegistros(List<entityRegistroDeAtividade> registros) {
        this.registros = registros;
    }

    public List<entityCategoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<entityCategoria> categorias) {
        this.categorias = categorias;
    }

    public List<entityBotao> getBotoes() {
        return botoes;
    }

    public void setBotoes(List<entityBotao> botoes) {
        this.botoes = botoes;
    }

    public entityTiraDeSentenca getTiraDeSentenca() {
        return tiraDeSentenca;
    }

    public void setTiraDeSentenca(entityTiraDeSentenca tiraDeSentenca) {
        this.tiraDeSentenca = tiraDeSentenca;
    }
}