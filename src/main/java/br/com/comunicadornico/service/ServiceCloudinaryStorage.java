package br.com.comunicadornico.service;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class ServiceCloudinaryStorage {

    private static final long LIMITE_IMAGEM = 5L * 1024 * 1024;
    private static final long LIMITE_AUDIO = 10L * 1024 * 1024;

    private static final Set<String> TIPOS_IMAGEM = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private static final Set<String> TIPOS_AUDIO = Set.of(
            "audio/mpeg",
            "audio/mp4",
            "audio/wav",
            "audio/x-wav",
            "audio/ogg",
            "audio/webm"
    );

    private final Cloudinary cloudinary;

    public ServiceCloudinaryStorage(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public ResultadoUpload uploadImagem(
            MultipartFile arquivo,
            String pasta) {

        validarArquivo(
                arquivo,
                TIPOS_IMAGEM,
                LIMITE_IMAGEM,
                "imagem"
        );

        return realizarUpload(
                arquivo,
                pasta,
                "image"
        );
    }

    public ResultadoUpload uploadAudio(
            MultipartFile arquivo,
            String pasta) {

        validarArquivo(
                arquivo,
                TIPOS_AUDIO,
                LIMITE_AUDIO,
                "áudio"
        );

        /*
         * O Cloudinary trata arquivos de áudio usando
         * resource_type = video.
         */
        return realizarUpload(
                arquivo,
                pasta,
                "video"
        );
    }

    private ResultadoUpload realizarUpload(
            MultipartFile arquivo,
            String pasta,
            String resourceType) {

        try {
            String publicId = UUID.randomUUID().toString();

            Map<?, ?> resultado = cloudinary.uploader().upload(
                    arquivo.getBytes(),
                    ObjectUtils.asMap(
                            "folder", pasta,
                            "public_id", publicId,
                            "resource_type", resourceType,
                            "overwrite", false
                    )
            );

            return new ResultadoUpload(
                    resultado.get("secure_url").toString(),
                    resultado.get("public_id").toString(),
                    resultado.get("resource_type").toString(),
                    resultado.get("format") != null
                    ? resultado.get("format").toString()
                    : null,
                    ((Number) resultado.get("bytes")).longValue()
            );

        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Não foi possível enviar o arquivo ao Cloudinary.",
                    exception
            );
        }
    }

    public void excluirImagem(String publicId) {
        excluir(publicId, "image");
    }

    public void excluirAudio(String publicId) {
        excluir(publicId, "video");
    }

    private void excluir(
            String publicId,
            String resourceType) {

        if (publicId == null || publicId.isBlank()) {
            return;
        }

        try {
            cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap(
                            "resource_type", resourceType,
                            "invalidate", true
                    )
            );

        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Não foi possível excluir o arquivo do Cloudinary.",
                    exception
            );
        }
    }

    private void validarArquivo(
            MultipartFile arquivo,
            Set<String> tiposPermitidos,
            long limite,
            String tipoArquivo) {

        if (arquivo == null || arquivo.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O arquivo de " + tipoArquivo + " é obrigatório."
            );
        }

        if (arquivo.getSize() > limite) {
            throw new ResponseStatusException(
                    HttpStatus.PAYLOAD_TOO_LARGE,
                    "O arquivo de " + tipoArquivo
                    + " excede o limite de "
                    + limite / 1024 / 1024
                    + " MB."
            );
        }

        String contentType = arquivo.getContentType();

        if (contentType == null
                || !tiposPermitidos.contains(contentType.toLowerCase())) {

            throw new ResponseStatusException(
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Formato de " + tipoArquivo
                    + " não permitido: "
                    + contentType
            );
        }
    }

    public record ResultadoUpload(
            String url,
            String publicId,
            String resourceType,
            String formato,
            long tamanho
            ) {

    }
}
