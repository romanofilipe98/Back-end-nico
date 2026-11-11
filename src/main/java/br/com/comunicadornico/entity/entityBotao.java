package br.com.comunicadornico.entity;
//imports

import java.util.List; //import para usar a lista

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity  //informa o SpringBot que essa classe deve virar uma tabela real
@Table(name = "tb_botao") // Etiqueta de identificação
@Schema(description = "Entidade que representa um botão de comunicação do sistema")
public class entityBotao {

    @Id //criação da chave primaria id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ativa o autoincremento 
    @Schema(description = "ID único do botão", example = "1")
    private Long idBotao;

    // Colunas
    @Column(name = "legenda")
    @Schema(description = "Texto exibido abaixo do botão", example = "Quero água")
    private String legenda;

    @Column(name = "corFundo")
    @Schema(description = "Código hexadecimal ou nome da cor de fundo do botão", example = "#FFFFFF")
    private String corFundo;

    @Column(
            name = "cor_fundo_legenda",
            length = 7
    )
    @Schema(
            description = "Cor de fundo da tira de legenda",
            example = "#FFFFFF"
    )
    private String corFundoLegenda = "#FFFFFF";

    @Column(
            name = "cor_legenda",
            length = 7
    )
    @Schema(
            description = "Cor do texto da legenda",
            example = "#000000"
    )
    private String corLegenda = "#000000";

    @Column(
            name = "exibir_legenda",
            nullable = false
    )
    @Schema(
            description = "Indica se a legenda deve aparecer no botão",
            example = "true"
    )
    private boolean exibirLegenda = true;

    @Column(name = "foto")
    @Schema(description = "URL, caminho ou Base64 da imagem associada ao botão", example = "https://exemplo.com/agua.png")
    private String foto;

    @Column(name = "foto_public_id")
    @Schema(hidden = true)
    private String fotoPublicId;

    @Column(name = "audioPersonalizado")
    @Schema(description = "Caminho para o arquivo de áudio gravado pelo usuário", example = "/audios/personalizado_agua.mp3")
    private String audioPersonalizado;

    @Column(
            name = "audio_personalizado_public_id"
    )
    @Schema(
            description
            = "Public ID do áudio personalizado no Cloudinary"
    )
    private String audioPersonalizadoPublicId;

    @Column(name = "audioFem")
    @Schema(description = "Caminho para o arquivo de áudio gerado com voz feminina", example = "/audios/fem_agua.mp3")
    private String audioFem;

    @Column(name = "audio_fem_public_id")
    @Schema(hidden = true)
    private String audioFemPublicId;

    @Column(name = "audioMas")
    @Schema(description = "Caminho para o arquivo de áudio gerado com voz masculina", example = "/audios/masc_agua.mp3")
    private String audioMas;

    @Column(name = "audio_mas_public_id")
    @Schema(hidden = true)
    private String audioMasPublicId;

    @Column(name = "tamanhoBotao")
    @Schema(description = "Define a escala ou tamanho renderizado do botão", example = "2")
    private int tamanhoBotao;

    @Column(name = "oculto")
    @Schema(description = "Sinalizador se o botão está invisível para o usuário final", example = "false")
    private boolean oculto;

    // CORRIGIDO: a relação botão x categoria agora usa a tabela associativa (entityBotaoCategoria)
    // em vez do @ManyToMany automático. O botão conhece suas ligações com as categorias.
    @OneToMany(mappedBy = "botao")
    @Schema(hidden = true)
    private List<entityBotaoCategoria> botaoCategorias;

    // criando relação da tabela botão com registro de atividade
    // isso é uma relação biderecional, ou seja, registro sabe a qual botão ela está associada
    @OneToMany(mappedBy = "botao") // botão é o nome da variavel criada na classe registro
    @Schema(hidden = true)
    private List<entityRegistroDeAtividade> registroDeAtividade;

    // relação de botão x administrador (*..1)
    @ManyToOne
    @JoinColumn(name = "fkAdministrador")
    @Schema(hidden = true)
    private entityAdministrador administrador;

    // relação de botão x usuario (*..1)
    @ManyToOne
    @JoinColumn(name = "fkUsuario")
    @Schema(hidden = true)
    private entityUsuario usuario;

    //relação de botão x tiradesetença (*..1)
    @ManyToOne
    @JoinColumn(name = "fkTiraDeSentenca")
    @Schema(hidden = true)
    private entityTiraDeSentenca tiraDeSentenca;

    // ADICIONADO: construtor padrao (exigido pelo JPA)
    public entityBotao() {
    }

    // construtor com todos os atributos (exceto o id, gerado pelo banco)
    public entityBotao(String legenda, String corFundo, String foto, String audioPersonalizado, String audioFem, String audioMas, int tamanhoBotao, boolean oculto, List<entityRegistroDeAtividade> registroDeAtividade) {
        this.legenda = legenda;
        this.corFundo = corFundo;
        this.foto = foto;
        this.audioPersonalizado = audioPersonalizado;
        this.audioFem = audioFem;
        this.audioMas = audioMas;
        this.tamanhoBotao = tamanhoBotao;
        this.oculto = oculto;
        this.registroDeAtividade = registroDeAtividade;
    }

    public Long getIdBotao() {
        return idBotao;
    }

    public void setIdBotao(Long idBotao) {
        this.idBotao = idBotao;
    }

    public String getLegenda() {
        return legenda;
    }

    public void setLegenda(String legenda) {
        this.legenda = legenda;
    }

    public String getCorFundo() {
        return corFundo;
    }

    public void setCorFundo(String corFundo) {
        this.corFundo = corFundo;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getAudioPersonalizado() {
        return audioPersonalizado;
    }

    public void setAudioPersonalizado(String audioPersonalizado) {
        this.audioPersonalizado = audioPersonalizado;
    }

    public String getAudioFem() {
        return audioFem;
    }

    public void setAudioFem(String audioFem) {
        this.audioFem = audioFem;
    }

    public String getAudioMas() {
        return audioMas;
    }

    public void setAudioMas(String audioMas) {
        this.audioMas = audioMas;
    }

    public int getTamanhoBotao() {
        return tamanhoBotao;
    }

    public void setTamanhoBotao(int tamanhoBotao) {
        this.tamanhoBotao = tamanhoBotao;
    }

    public boolean isOculto() {
        return oculto;
    }

    public void setOculto(boolean oculto) {
        this.oculto = oculto;
    }

    public List<entityBotaoCategoria> getBotaoCategorias() {
        return botaoCategorias;
    }

    public void setBotaoCategorias(List<entityBotaoCategoria> botaoCategorias) {
        this.botaoCategorias = botaoCategorias;
    }

    public List<entityRegistroDeAtividade> getRegistroDeAtividade() {
        return registroDeAtividade;
    }

    public void setRegistroDeAtividade(List<entityRegistroDeAtividade> registroDeAtividade) {
        this.registroDeAtividade = registroDeAtividade;
    }

    // ADICIONADO: getters e setters dos relacionamentos
    public entityAdministrador getAdministrador() {
        return administrador;
    }

    public void setAdministrador(entityAdministrador administrador) {
        this.administrador = administrador;
    }

    public entityUsuario getUsuario() {
        return usuario;
    }

    public void setUsuario(entityUsuario usuario) {
        this.usuario = usuario;
    }

    public entityTiraDeSentenca getTiraDeSentenca() {
        return tiraDeSentenca;
    }

    public void setTiraDeSentenca(entityTiraDeSentenca tiraDeSentenca) {
        this.tiraDeSentenca = tiraDeSentenca;
    }

    public String getFotoPublicId() {
        return fotoPublicId;
    }

    public void setFotoPublicId(String fotoPublicId) {
        this.fotoPublicId = fotoPublicId;
    }

    public String getAudioFemPublicId() {
        return audioFemPublicId;
    }

    public void setAudioFemPublicId(String audioFemPublicId) {
        this.audioFemPublicId = audioFemPublicId;
    }

    public String getAudioMasPublicId() {
        return audioMasPublicId;
    }

    public void setAudioMasPublicId(String audioMasPublicId) {
        this.audioMasPublicId = audioMasPublicId;
    }

    public String getAudioPersonalizadoPublicId() {
        return audioPersonalizadoPublicId;
    }

    public void setAudioPersonalizadoPublicId(
            String audioPersonalizadoPublicId) {

        this.audioPersonalizadoPublicId
                = audioPersonalizadoPublicId;
    }

    public String getCorFundoLegenda() {
        return corFundoLegenda;
    }

    public void setCorFundoLegenda(
            String corFundoLegenda) {

        this.corFundoLegenda
                = corFundoLegenda;
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

        this.exibirLegenda
                = exibirLegenda;
    }
}
