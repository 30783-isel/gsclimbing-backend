package com.gsclimbing.x.pdf.common;

import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.entity.Report;
import com.gsclimbing.database.service.FileService;
import com.gsclimbing.ftp.FTPDownloadFiles;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDPushButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe base abstrata para populadores de relatórios PDF.
 * Fornece funcionalidades comuns para manipulação de PDFs e inserção de imagens.
 * 
 * @param <T> Tipo do relatório (deve estender Report)
 */
public abstract class AbstractPdfReportPopulator<T extends Report> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractPdfReportPopulator.class);
    
    @Autowired
    protected FileService fileService;
    
    @Autowired
    protected PdfImageService pdfImageService;

    protected PDDocument pdfDocument;

    /**
     * Gera o PDF completo do relatório
     *
     * @param report Relatório a ser processado
     * @return Array de bytes do PDF gerado
     */
    public byte[] generatePDF(T report) {
        logger.info("Starting PDF generation for report: uuid={}", report.getUuid());
        
        try {
            return populateAndGeneratePdf(report);
        } catch (IOException e) {
            logger.error("Error generating PDF for report: uuid={}", report.getUuid(), e);
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }

    /**
     * Método template para popular e gerar o PDF.
     * Define o fluxo principal do processo.
     */
    private byte[] populateAndGeneratePdf(T report) throws IOException {
        try {
            // 1. Carregar template PDF
            loadPdfTemplate();
            
            // 2. Popular campos de texto
            populateTextFields(report);
            
            // 3. Carregar e inserir imagens
            populateImageFields(report);
            
            // 4. Processar campos adicionais (se necessário)
            processAdditionalFields(report);
            
            // 5. Converter para bytes e retornar
            return convertPdfToBytes();
            
        } finally {
            // Garantir que o documento é fechado
            closePdfDocument();
        }
    }

    /**
     * Carrega o template PDF do servidor FTP
     */
    private void loadPdfTemplate() throws IOException {
        String folder = getPdfTemplateFolder();
        String filename = getPdfTemplateFilename();
        
        logger.debug("Loading PDF template: folder={}, filename={}", folder, filename);
        
        InputStream inputStream = FTPDownloadFiles.downloadPdfReportByTeamToTempFile(folder, filename);
        
        if (inputStream == null) {
            throw new IOException("Failed to download PDF template: " + folder + filename);
        }
        
        pdfDocument = PDDocument.load(inputStream);
        logger.debug("PDF template loaded: pages={}", pdfDocument.getNumberOfPages());
    }

    /**
     * Popular campos de imagem do relatório
     */
    private void populateImageFields(T report) throws IOException {
        logger.debug("Populating image fields for report: uuid={}", report.getUuid());
        
        List<FileData> imageFiles = loadReportImages(report);
        
        if (imageFiles.isEmpty()) {
            logger.info("No images found for report: uuid={}", report.getUuid());
            return;
        }
        
        logger.info("Found {} images to insert", imageFiles.size());
        
        int successCount = 0;
        int errorCount = 0;
        
        for (FileData fileData : imageFiles) {
            try {
                processAndInsertImage(fileData);
                successCount++;
            } catch (Exception e) {
                errorCount++;
                logger.error("Failed to insert image: name={}, hash={}", 
                    fileData.getName(), fileData.getHash(), e);
            }
        }
        
        logger.info("Image insertion complete: success={}, errors={}", successCount, errorCount);
    }

    /**
     * Processa e insere uma única imagem no PDF
     */
    private void processAndInsertImage(FileData fileData) throws IOException {
        // Carregar imagem do FTP
        BufferedImage image = pdfImageService.loadImageFromFtp(fileData);
        
        // Validar dimensões (opcional - pode ser sobrescrito)
        if (!validateImage(image, fileData)) {
            logger.warn("Image validation failed, skipping: name={}", fileData.getName());
            return;
        }
        
        // Redimensionar se necessário
        image = resizeImageIfNeeded(image);
        
        // Inserir no campo PDF
        String fieldName = (String) fileData.getName();
        insertImageInField(fieldName, image);
        
        // Inserir descrição no campo de texto associado
        if (fileData.getNameField() != null && fileData.getDescription() != null) {
            setTextField(fileData.getNameField(), fileData.getDescription());
        }
    }

    /**
     * Insere uma imagem num campo PDF
     */
    private void insertImageInField(String fieldName, BufferedImage image) throws IOException {
        PDField field = getField(fieldName);
        
        if (field == null) {
            logger.warn("Field not found: {}", fieldName);
            return;
        }
        
        if (!(field instanceof PDPushButton)) {
            logger.warn("Field is not a push button: {}", fieldName);
            return;
        }
        
        pdfImageService.insertImageInPushButton(pdfDocument, (PDPushButton) field, image, fieldName);
    }

    /**
     * Carrega as imagens associadas ao relatório
     */
    public List<FileData> loadReportImages(T report) {
        if (report == null || report.getUuid() == null) {
            return new ArrayList<>();
        }

        List<FileData> allFiles = fileService.readActiveFilesByUuid(report.getUuid());
        
        // Filtrar apenas imagens JPG (pode ser customizado)
        return allFiles.stream()
            .filter(file -> isImageFile(file))
            .collect(Collectors.toList());
    }

    /**
     * Verifica se um ficheiro é uma imagem
     */
    public boolean isImageFile(FileData fileData) {
        if (fileData == null || fileData.getMimeType() == null) {
            return false;
        }
        
        String mimeType = fileData.getMimeType().toUpperCase();
        return mimeType.equals("JPG") || mimeType.equals("JPEG") || 
               mimeType.equals("PNG") || mimeType.equals("IMAGE/JPEG") ||
               mimeType.equals("IMAGE/PNG");
    }

    /**
     * Define valor de um campo de texto no PDF
     */
    protected void setTextField(String fieldName, String value) throws IOException {
        if (fieldName == null || value == null) {
            return;
        }
        
        PDField field = getField(fieldName);
        
        if (field != null) {
            field.setValue(value);
            logger.trace("Set field: {}={}", fieldName, value);
        } else {
            logger.debug("Field not found: {}", fieldName);
        }
    }

    /**
     * Obtém um campo do formulário PDF
     */
    protected PDField getField(String fieldName) {
        if (pdfDocument == null) {
            logger.error("PDF document is null");
            return null;
        }
        
        PDDocumentCatalog catalog = pdfDocument.getDocumentCatalog();
        if (catalog == null) {
            logger.error("Document catalog is null");
            return null;
        }
        
        PDAcroForm acroForm = catalog.getAcroForm();
        if (acroForm == null) {
            logger.error("AcroForm is null");
            return null;
        }
        
        return acroForm.getField(fieldName);
    }

    /**
     * Converte o documento PDF para array de bytes
     */
    private byte[] convertPdfToBytes() throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            pdfDocument.save(outputStream);
            
            try (InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray())) {
                byte[] pdfBytes = IOUtils.toByteArray(inputStream);
                logger.info("PDF generated successfully: size={} bytes", pdfBytes.length);
                return pdfBytes;
            }
        }
    }

    /**
     * Fecha o documento PDF
     */
    private void closePdfDocument() {
        if (pdfDocument != null) {
            try {
                pdfDocument.close();
                logger.debug("PDF document closed");
            } catch (IOException e) {
                logger.warn("Error closing PDF document", e);
            }
        }
    }

    // ========================================================================
    // MÉTODOS ABSTRATOS - Devem ser implementados pelas subclasses
    // ========================================================================

    /**
     * Retorna a pasta onde está o template PDF
     */
    protected abstract String getPdfTemplateFolder();

    /**
     * Retorna o nome do ficheiro do template PDF
     */
    protected abstract String getPdfTemplateFilename();

    /**
     * Popular campos de texto específicos do relatório
     */
    protected abstract void populateTextFields(T report) throws IOException;

    // ========================================================================
    // MÉTODOS OPCIONAIS - Podem ser sobrescritos se necessário
    // ========================================================================

    /**
     * Processar campos adicionais (pode ser sobrescrito)
     */
    protected void processAdditionalFields(T report) throws IOException {
        // Implementação padrão vazia
        // Subclasses podem sobrescrever se necessário
    }

    /**
     * Validar imagem antes de inserir (pode ser sobrescrito)
     */
    protected boolean validateImage(BufferedImage image, FileData fileData) {
        // Validação básica
        return image != null && image.getWidth() > 0 && image.getHeight() > 0;
    }

    /**
     * Redimensionar imagem se necessário (pode ser sobrescrito)
     */
    protected BufferedImage resizeImageIfNeeded(BufferedImage image) {
        // Por padrão, não redimensiona
        // Subclasses podem implementar lógica de redimensionamento
        return image;
    }

    /**
     * Obter logger específico da subclasse
     */
    protected Logger getLogger() {
        return logger;
    }
}
