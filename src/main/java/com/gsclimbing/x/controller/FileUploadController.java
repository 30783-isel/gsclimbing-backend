package com.gsclimbing.x.controller;

import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.service.FileService;
import com.gsclimbing.ftp.FTPDownloadFiles;
import com.gsclimbing.ftp.FTPUploadFile;
import com.gsclimbing.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Controller para upload de fotos do mobile
 */
@CrossOrigin(origins = "*", methods = {
        RequestMethod.OPTIONS,
        RequestMethod.POST
})
@Transactional
@RestController
@RequestMapping(path = "/api/reports/files")
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    private FileService fileService;

    /**
     * Upload de foto para um relatório específico (identificado por UUID)
     *
     * Endpoint: POST /api/reports/files/upload/{reportUuid}
     *
     * @param reportUuid UUID do relatório
     * @param file Ficheiro da foto
     * @param fieldName Nome do campo no PDF (photoOne, photoTwo, etc) - OBRIGATÓRIO
     * @param description Descrição da foto (opcional)
     * @return Resposta com fileId e success
     */
    @PostMapping("/upload/{reportUuid}")
    public ResponseEntity<?> uploadPhoto(
            @PathVariable String reportUuid,
            @RequestParam("file") MultipartFile file,
            @RequestParam("fieldName") String fieldName,  // ✅ NOVO - OBRIGATÓRIO
            @RequestParam(value = "description", required = false) String description) {

        try {
            logger.info("📤 Uploading photo for report UUID: {}, fieldName: {}", reportUuid, fieldName);

            // Validar ficheiro
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("File is empty"));
            }

            // Validar UUID do relatório
            if (reportUuid == null || reportUuid.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Report UUID is required"));
            }

            // ✅ Validar fieldName
            if (fieldName == null || fieldName.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Field name is required (e.g., photoOne, photoTwo)"));
            }

            // Gerar hash único para a foto
            String fileHash = UUID.randomUUID().toString().replace("-", "");

            // Converter MultipartFile para File
            File tempFile = FileUtils.multipartToFile(file, fileHash);

            // Upload para FTP (método correto)
            boolean uploaded = FTPUploadFile.uploadFile2FTPServer(tempFile, fileHash);

            if (!uploaded) {
                logger.error("❌ Failed to upload file to FTP server");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(createErrorResponse("Failed to upload file to storage"));
            }

            // Criar registo na base de dados
            FileData fileData = new FileData();
            fileData.setHash(fileHash);

            // ✅ CRÍTICO: Guardar o nome do campo do PDF, NÃO o nome do ficheiro!
            fileData.setName(fieldName);  // photoOne, photoTwo, photoThree, etc

            // ✅ nameField é usado para o campo de descrição no PDF
            fileData.setNameField(fieldName + "Txt");  // photoOneTxt, photoTwoTxt, etc

            fileData.setMimeType(file.getContentType());
            fileData.setDescription(description != null ? description : "");
            fileData.setUuid(reportUuid); // Associar ao relatório
            fileData.setCreateDate(LocalDateTime.now());
            fileData.setModifiedDate(LocalDateTime.now());
            fileData.setSize(file.getSize());
            fileData.setInsertedOnFtpServer(uploaded);

            // ✅ GUARDAR NA BD PRIMEIRO para obter o fileId gerado
            fileService.createFile(fileData);

            // ✅ AGORA o fileId já foi gerado pelo @GeneratedValue
            Integer generatedFileId = fileData.getFileId();

            logger.info("✅ Photo uploaded successfully - FieldName: {}, Hash: {}, FileId: {}",
                    fieldName, fileHash, generatedFileId);

            // ✅ Retornar resposta com fileId NUMÉRICO
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("fileId", generatedFileId);  // ✅ Retorna ID numérico, não hash!
            response.put("hash", fileHash);  // Incluir hash também para referência
            response.put("fieldName", fieldName);  // ✅ Retornar o fieldName
            response.put("fileName", file.getOriginalFilename());
            response.put("message", "Photo uploaded successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("❌ Error uploading photo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error uploading photo: " + e.getMessage()));
        }
    }


    @GetMapping("/download/{hash}")
    public ResponseEntity<byte[]> downloadPhoto(@PathVariable String hash) {
        try {
            logger.info("📥 Downloading photo with hash: {}", hash);

            // Download do FTP
            byte[] photoBytes = FTPDownloadFiles.downloadFile2FTPServer(hash);

            if (photoBytes == null) {
                logger.error("❌ Photo not found with hash: {}", hash);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Determinar o tipo de conteúdo (mime type)
            FileData fileData = fileService.readFileByHash(hash);
            String contentType = "image/jpeg"; // default

            if (fileData != null && fileData.getMimeType() != null) {
                contentType = fileData.getMimeType();
            }

            logger.info("✅ Photo downloaded successfully, size: {} bytes", photoBytes.length);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .header(HttpHeaders.CACHE_CONTROL, "max-age=3600")
                    .body(photoBytes);

        } catch (Exception e) {
            logger.error("❌ Error downloading photo with hash: {}", hash, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Criar resposta de erro
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", message);
        return error;
    }
}