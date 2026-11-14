package br.com.comunicadornico.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import br.com.comunicadornico.dto.EditarBotaoUsuarioDTO;
import br.com.comunicadornico.dto.EditarBotaoUsuarioResponseDTO;
import br.com.comunicadornico.entity.entityAdministrador;
import br.com.comunicadornico.entity.entityBotao;
import br.com.comunicadornico.entity.entityBotaoCategoria;
import br.com.comunicadornico.entity.entityBotaoPersonalizacao;
import br.com.comunicadornico.entity.entityCategoria;
import br.com.comunicadornico.entity.entityUsuario;
import br.com.comunicadornico.repository.repositoryBotao;
import br.com.comunicadornico.repository.repositoryBotaoCategoria;
import br.com.comunicadornico.repository.repositoryBotaoPersonalizacao;
import br.com.comunicadornico.repository.repositoryCategoria;
import br.com.comunicadornico.service.ServiceCloudinaryStorage.ResultadoUpload;

@Service
public class serviceEditarBotaoUsuario {

        private final repositoryBotao repositoryBotao;

        private final repositoryCategoria repositoryCategoria;

        private final repositoryBotaoCategoria repositoryBotaoCategoria;

        private final repositoryBotaoPersonalizacao repositoryPersonalizacao;

        private final serviceUsuario usuarioService;

        private final ServiceCloudinaryStorage cloudinaryStorage;

        public serviceEditarBotaoUsuario(
                        repositoryBotao repositoryBotao,
                        repositoryCategoria repositoryCategoria,
                        repositoryBotaoCategoria repositoryBotaoCategoria,
                        repositoryBotaoPersonalizacao repositoryPersonalizacao,
                        serviceUsuario usuarioService,
                        ServiceCloudinaryStorage cloudinaryStorage) {

                this.repositoryBotao = repositoryBotao;

                this.repositoryCategoria = repositoryCategoria;

                this.repositoryBotaoCategoria = repositoryBotaoCategoria;

                this.repositoryPersonalizacao = repositoryPersonalizacao;

                this.usuarioService = usuarioService;

                this.cloudinaryStorage = cloudinaryStorage;
        }

        @Transactional
        public EditarBotaoUsuarioResponseDTO editar(
                        Long idBotao,
                        EditarBotaoUsuarioDTO dados,
                        MultipartFile foto,
                        MultipartFile audioPersonalizado,
                        String emailAdministrador) {

                validarDados(
                                dados);

                entityUsuario usuario = usuarioService
                                .validarUsuarioDoAdministrador(
                                                dados.getIdUsuario(),
                                                emailAdministrador);

                entityAdministrador administrador = usuario.getAdministrador();

                if (administrador == null) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "O usuário não possui administrador vinculado.");
                }

                entityBotao botaoOriginal = repositoryBotao
                                .findById(idBotao)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Botão não encontrado: "
                                                                + idBotao));

                validarBotaoDisponivel(
                                botaoOriginal,
                                usuario);

                entityCategoria categoria = repositoryCategoria
                                .findById(
                                                dados.getIdCategoria())
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Categoria não encontrada: "
                                                                + dados.getIdCategoria()));

                validarCategoriaDisponivel(
                                categoria,
                                usuario,
                                administrador);

                boolean botaoGlobal = botaoOriginal.getUsuario() == null
                                && botaoOriginal
                                                .getAdministrador() == null;

                if (botaoGlobal) {
                        return criarCopiaExclusiva(
                                        botaoOriginal,
                                        categoria,
                                        usuario,
                                        administrador,
                                        dados,
                                        foto,
                                        audioPersonalizado);
                }

                return atualizarBotaoExclusivo(
                                botaoOriginal,
                                categoria,
                                usuario,
                                dados,
                                foto,
                                audioPersonalizado);
        }

        private EditarBotaoUsuarioResponseDTO criarCopiaExclusiva(
                        entityBotao botaoOriginal,
                        entityCategoria categoria,
                        entityUsuario usuario,
                        entityAdministrador administrador,
                        EditarBotaoUsuarioDTO dados,
                        MultipartFile foto,
                        MultipartFile audioPersonalizado) {

                ResultadoUpload novaFoto = null;

                ResultadoUpload novoAudio = null;

                try {
                        entityBotao copia = new entityBotao();

                        preencherCampos(
                                        copia,
                                        dados);
                        copia.setOculto(
                                        false);

                        copia.setUsuario(
                                        usuario);

                        copia.setAdministrador(
                                        administrador);

                        copia.setAudioFem(
                                        null);

                        copia.setAudioFemPublicId(
                                        null);

                        copia.setAudioMas(
                                        null);

                        copia.setAudioMasPublicId(
                                        null);

                        copiarMidiasDoBotaoPadrao(
                                        botaoOriginal,
                                        copia);

                        entityBotao copiaSalva = repositoryBotao
                                        .saveAndFlush(
                                                        copia);

                        if (arquivoFoiEnviado(foto)) {
                                novaFoto = cloudinaryStorage
                                                .uploadImagem(
                                                                foto,
                                                                caminhoImagem(
                                                                                usuario,
                                                                                copiaSalva));

                                copiaSalva.setFoto(
                                                novaFoto.url());

                                copiaSalva.setFotoPublicId(
                                                novaFoto.publicId());
                        }

                        if (arquivoFoiEnviado(
                                        audioPersonalizado)) {
                                novoAudio = cloudinaryStorage
                                                .uploadAudio(
                                                                audioPersonalizado,
                                                                caminhoAudio(
                                                                                usuario,
                                                                                copiaSalva));

                                copiaSalva
                                                .setAudioPersonalizado(
                                                                novoAudio.url());

                                copiaSalva
                                                .setAudioPersonalizadoPublicId(
                                                                novoAudio.publicId());
                        }

                        copiaSalva = repositoryBotao
                                        .saveAndFlush(
                                                        copiaSalva);

                        associarCategoriaUnica(
                                        copiaSalva,
                                        categoria);

                        ocultarBotaoGlobalParaUsuario(
                                        botaoOriginal,
                                        usuario);

                        return montarResposta(
                                        copiaSalva,
                                        categoria,
                                        usuario,
                                        true,
                                        true);
                } catch (RuntimeException exception) {
                        excluirNovosUploads(
                                        novaFoto,
                                        novoAudio);

                        throw exception;
                }
        }

        private EditarBotaoUsuarioResponseDTO atualizarBotaoExclusivo(
                        entityBotao botao,
                        entityCategoria categoria,
                        entityUsuario usuario,
                        EditarBotaoUsuarioDTO dados,
                        MultipartFile foto,
                        MultipartFile audioPersonalizado) {

                ResultadoUpload novaFoto = null;

                ResultadoUpload novoAudio = null;

                String fotoPublicIdAntigo = botao.getFotoPublicId();

                String audioPublicIdAntigo = botao
                                .getAudioPersonalizadoPublicId();

                try {
                        preencherCampos(
                                        botao,
                                        dados);

                        if (arquivoFoiEnviado(foto)) {
                                novaFoto = cloudinaryStorage
                                                .uploadImagem(
                                                                foto,
                                                                caminhoImagem(
                                                                                usuario,
                                                                                botao));

                                botao.setFoto(
                                                novaFoto.url());

                                botao.setFotoPublicId(
                                                novaFoto.publicId());
                        }

                        if (arquivoFoiEnviado(
                                        audioPersonalizado)) {
                                novoAudio = cloudinaryStorage
                                                .uploadAudio(
                                                                audioPersonalizado,
                                                                caminhoAudio(
                                                                                usuario,
                                                                                botao));

                                botao.setAudioPersonalizado(
                                                novoAudio.url());

                                botao
                                                .setAudioPersonalizadoPublicId(
                                                                novoAudio.publicId());
                        }

                        entityBotao botaoSalvo = repositoryBotao
                                        .saveAndFlush(
                                                        botao);

                        associarCategoriaUnica(
                                        botaoSalvo,
                                        categoria);

                        excluirMidiasAntigas(
                                        novaFoto,
                                        fotoPublicIdAntigo,
                                        novoAudio,
                                        audioPublicIdAntigo);

                        return montarResposta(
                                        botaoSalvo,
                                        categoria,
                                        usuario,
                                        true,
                                        true);
                } catch (RuntimeException exception) {
                        excluirNovosUploads(
                                        novaFoto,
                                        novoAudio);

                        throw exception;
                }
        }

        private void preencherCampos(
                        entityBotao botao,
                        EditarBotaoUsuarioDTO dados) {

                botao.setLegenda(
                                dados.getLegenda().trim());

                botao.setCorFundo(
                                dados.getCorFundo().trim());

                botao.setCorFundoLegenda(
                                dados.getCorFundoLegenda()
                                                .trim());

                botao.setCorLegenda(
                                dados.getCorLegenda()
                                                .trim());

                botao.setExibirLegenda(
                                dados.isExibirLegenda());

                botao.setTamanhoBotao(
                                dados.getTamanhoBotao());

                botao.setOculto(
                                dados.isOculto());
        }

        private void copiarMidiasDoBotaoPadrao(
                        entityBotao origem,
                        entityBotao destino) {

                destino.setFoto(
                                origem.getFoto());

                /*
                 * A cópia pode usar a mesma URL da imagem
                 * padrão, mas não recebe o Public ID.
                 *
                 * Desse modo, excluir a cópia não remove
                 * o arquivo usado pelo botão global.
                 */
                destino.setFotoPublicId(
                                null);

                destino.setAudioPersonalizado(
                                origem.getAudioPersonalizado());

                destino
                                .setAudioPersonalizadoPublicId(
                                                null);
        }

        private void associarCategoriaUnica(
                        entityBotao botao,
                        entityCategoria categoria) {

                List<entityBotaoCategoria> relacoesAtuais = botao.getBotaoCategorias() != null
                                ? new ArrayList<>(
                                                botao.getBotaoCategorias())
                                : new ArrayList<>();

                if (!relacoesAtuais.isEmpty()) {
                        repositoryBotaoCategoria
                                        .deleteAll(
                                                        relacoesAtuais);

                        repositoryBotaoCategoria
                                        .flush();
                }

                int maiorPosicao = repositoryBotaoCategoria
                                .buscarMaiorPosicaoDaCategoria(
                                                categoria
                                                                .getIdCategoria());

                entityBotaoCategoria relacao = new entityBotaoCategoria();

                relacao.setBotao(
                                botao);

                relacao.setCategoria(
                                categoria);

                relacao.setPosicao(
                                maiorPosicao + 1);

                repositoryBotaoCategoria
                                .saveAndFlush(
                                                relacao);
        }

        private void ocultarBotaoGlobalParaUsuario(
                        entityBotao botaoGlobal,
                        entityUsuario usuario) {

                entityBotaoPersonalizacao personalizacao = repositoryPersonalizacao
                                .findByUsuarioIdUsuarioAndBotaoIdBotao(
                                                usuario.getIdUsuario(),
                                                botaoGlobal.getIdBotao())
                                .orElseGet(() -> {
                                        entityBotaoPersonalizacao nova = new entityBotaoPersonalizacao();

                                        nova.setUsuario(
                                                        usuario);

                                        nova.setBotao(
                                                        botaoGlobal);

                                        return nova;
                                });

                personalizacao.setOculto(
                                true);

                repositoryPersonalizacao
                                .saveAndFlush(
                                                personalizacao);
        }

        private void validarDados(
                        EditarBotaoUsuarioDTO dados) {

                if (dados == null) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Os dados do botão são obrigatórios.");
                }

                if (dados.getIdUsuario() == null) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "O usuário é obrigatório.");
                }

                if (dados.getIdCategoria() == null) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "A categoria é obrigatória.");
                }

                if (dados.getLegenda() == null
                                || dados.getLegenda()
                                                .isBlank()) {

                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "A legenda do botão é obrigatória.");
                }

                validarCor(
                                dados.getCorFundo(),
                                "A cor de fundo do botão");

                validarCor(
                                dados.getCorFundoLegenda(),
                                "A cor de fundo da legenda");

                validarCor(
                                dados.getCorLegenda(),
                                "A cor do texto da legenda");

                if (dados.getTamanhoBotao() < 1) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "O tamanho do botão deve ser maior que zero.");
                }
        }

        private void validarCor(
                        String cor,
                        String nomeCampo) {

                if (cor == null
                                || !cor.matches(
                                                "^#[0-9A-Fa-f]{6}$")) {

                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        nomeCampo
                                                        + " deve estar no formato hexadecimal.");
                }
        }

        private void validarBotaoDisponivel(
                        entityBotao botao,
                        entityUsuario usuario) {

                if (botao.getUsuario() == null) {
                        return;
                }

                if (!botao
                                .getUsuario()
                                .getIdUsuario()
                                .equals(
                                                usuario.getIdUsuario())) {

                        throw new ResponseStatusException(
                                        HttpStatus.FORBIDDEN,
                                        "Este botão não está disponível para o usuário.");
                }
        }

        private void validarCategoriaDisponivel(
                        entityCategoria categoria,
                        entityUsuario usuario,
                        entityAdministrador administrador) {

                boolean categoriaGlobal = categoria.getUsuario() == null
                                && categoria
                                                .getAdministrador() == null;

                if (categoriaGlobal) {
                        return;
                }

                boolean categoriaDoUsuario = categoria.getUsuario() != null
                                && categoria
                                                .getUsuario()
                                                .getIdUsuario()
                                                .equals(
                                                                usuario.getIdUsuario());

                if (categoriaDoUsuario) {
                        return;
                }

                boolean categoriaDoAdministrador = categoria.getAdministrador() != null
                                && categoria
                                                .getAdministrador()
                                                .getIdAdministrador()
                                                .equals(
                                                                administrador
                                                                                .getIdAdministrador());

                if (categoriaDoAdministrador) {
                        return;
                }

                throw new ResponseStatusException(
                                HttpStatus.FORBIDDEN,
                                "A categoria selecionada não está disponível para o usuário.");
        }

        private EditarBotaoUsuarioResponseDTO montarResposta(
                        entityBotao botao,
                        entityCategoria categoria,
                        entityUsuario usuario,
                        boolean personalizado,
                        boolean exclusivoUsuario) {

                return new EditarBotaoUsuarioResponseDTO(
                                botao.getIdBotao(),
                                usuario.getIdUsuario(),
                                usuario.getNome(),
                                categoria.getIdCategoria(),
                                categoria.getNome(),
                                botao.getLegenda(),
                                botao.getCorFundo(),
                                botao.getCorFundoLegenda(),
                                botao.getCorLegenda(),
                                botao.isExibirLegenda(),
                                botao.getTamanhoBotao(),
                                botao.isOculto(),
                                botao.getFoto(),
                                botao.getAudioPersonalizado(),
                                personalizado,
                                exclusivoUsuario);
        }

        private String caminhoImagem(
                        entityUsuario usuario,
                        entityBotao botao) {

                return "nico/usuarios/"
                                + usuario.getIdUsuario()
                                + "/botoes/"
                                + botao.getIdBotao()
                                + "/imagem";
        }

        private String caminhoAudio(
                        entityUsuario usuario,
                        entityBotao botao) {

                return "nico/usuarios/"
                                + usuario.getIdUsuario()
                                + "/botoes/"
                                + botao.getIdBotao()
                                + "/audio";
        }

        private boolean arquivoFoiEnviado(
                        MultipartFile arquivo) {

                return arquivo != null
                                && !arquivo.isEmpty();
        }

        private void excluirMidiasAntigas(
                        ResultadoUpload novaFoto,
                        String fotoPublicIdAntigo,
                        ResultadoUpload novoAudio,
                        String audioPublicIdAntigo) {

                if (novaFoto != null) {
                        excluirImagemSemInterromper(
                                        fotoPublicIdAntigo);
                }

                if (novoAudio != null) {
                        excluirAudioSemInterromper(
                                        audioPublicIdAntigo);
                }
        }

        private void excluirNovosUploads(
                        ResultadoUpload novaFoto,
                        ResultadoUpload novoAudio) {

                if (novaFoto != null) {
                        excluirImagemSemInterromper(
                                        novaFoto.publicId());
                }

                if (novoAudio != null) {
                        excluirAudioSemInterromper(
                                        novoAudio.publicId());
                }
        }

        private void excluirImagemSemInterromper(
                        String publicId) {

                if (publicId == null
                                || publicId.isBlank()) {

                        return;
                }

                try {
                        cloudinaryStorage
                                        .excluirImagem(
                                                        publicId);
                } catch (RuntimeException exception) {
                        System.err.println(
                                        "Não foi possível excluir a imagem "
                                                        + publicId
                                                        + ": "
                                                        + exception.getMessage());
                }
        }

        private void excluirAudioSemInterromper(
                        String publicId) {

                if (publicId == null
                                || publicId.isBlank()) {

                        return;
                }

                try {
                        cloudinaryStorage
                                        .excluirAudio(
                                                        publicId);
                } catch (RuntimeException exception) {
                        System.err.println(
                                        "Não foi possível excluir o áudio "
                                                        + publicId
                                                        + ": "
                                                        + exception.getMessage());
                }
        }
}