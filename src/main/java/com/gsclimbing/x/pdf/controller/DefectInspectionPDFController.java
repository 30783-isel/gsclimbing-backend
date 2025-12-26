package com.gsclimbing.x.pdf.controller;

import com.gsclimbing.database.entity.DefectsInspectionReport;
import com.gsclimbing.x.database.service.DefectsInspectionReportService;
import com.gsclimbing.x.pdf.populaters.DefectsInspectionPopulaterR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller REST para geração de PDFs de Defect Inspection Reports
 * utilizando as classes da package com.gsclimbing.x.pdf
 *
 * Endpoints disponíveis:
 * - GET /api/pdf/defect-inspection/{id}/download - Download do PDF
 * - GET /api/pdf/defect-inspection/{id}/preview - Preview do PDF no browser
 * - GET /api/pdf/defect-inspection/{id}/info - Informações sobre o PDF
 *
 * @author gsclimbing-backend
 * @version 1.0
 */
@CrossOrigin(origins = "*", methods = {
        RequestMethod.GET,
        RequestMethod.POST,
        RequestMethod.OPTIONS
})
@RestController
@RequestMapping(path = "/api/pdf/defect-inspection")
@Transactional(readOnly = true)
public class DefectInspectionPDFController {

    private static final Logger logger = LoggerFactory.getLogger(DefectInspectionPDFController.class);

    private static final DateTimeFormatter FILENAME_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Autowired
    private DefectsInspectionReportService defectsInspectionReportService;

    @Autowired
    private DefectsInspectionPopulaterR defectsInspectionPopulater;

    /**
     * Download de PDF de Defect Inspection Report por ID
     *
     * Endpoint: GET /api/pdf/defect-inspection/{id}/download
     *
     * Exemplo:
     * - http://localhost:8080/api/pdf/defect-inspection/123/download
     *
     * @param id ID do relatório
     * @return PDF bytes com headers apropriados para download
     */
    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TECH')")
    public ResponseEntity<byte[]> downloadDefectInspectionPdf(@PathVariable Integer id) {

        logger.info("📄 [DOWNLOAD] Iniciando download de Defect Inspection Report PDF: id={}", id);

        try {
            // 1. Buscar relatório na base de dados
            DefectsInspectionReport report = defectsInspectionReportService
                    .getDefectsInspectionReportById(id);

            if (report == null) {
                logger.error("❌ [DOWNLOAD] Relatório não encontrado: id={}", id);
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(null);
            }

            logger.debug("✓ Relatório encontrado: site={}, wtgNumber={}, uuid={}",
                    report.getSite(), report.getWtgNumber(), report.getUuid());

            // 2. Gerar PDF usando o populator refatorizado
            byte[] pdfBytes = defectsInspectionPopulater.generatePDF(report);

            if (pdfBytes == null || pdfBytes.length == 0) {
                logger.error("❌ [DOWNLOAD] Falha ao gerar PDF: resultado vazio");
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(null);
            }

            logger.info("✅ [DOWNLOAD] PDF gerado com sucesso: size={} bytes ({} KB)",
                    pdfBytes.length, pdfBytes.length / 1024);

            // 3. Construir nome do ficheiro
            String filename = buildPdfFilename(report);
            logger.debug("Filename gerado: {}", filename);

            // 4. Retornar PDF com headers apropriados para download
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + filename + "\"")
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                    .header(HttpHeaders.PRAGMA, "no-cache")
                    .header(HttpHeaders.EXPIRES, "0")
                    .body(pdfBytes);

        } catch (Exception e) {
            logger.error("❌ [DOWNLOAD] Erro ao gerar PDF para relatório id={}", id, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    /**
     * Preview de PDF no browser (abre inline em vez de fazer download)
     *
     * Endpoint: GET /api/pdf/defect-inspection/{id}/preview
     *
     * Útil para visualizar o PDF directamente no browser antes de fazer download
     *
     * @param id ID do relatório
     * @return PDF bytes com header inline para preview
     */
    @GetMapping("/{id}/preview")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TECH')")
    public ResponseEntity<byte[]> previewDefectInspectionPdf(@PathVariable Integer id) {

        logger.info("👁️ [PREVIEW] Iniciando preview de Defect Inspection Report PDF: id={}", id);

        try {
            // Buscar relatório
            DefectsInspectionReport report = defectsInspectionReportService
                    .getDefectsInspectionReportById(id);

            if (report == null) {
                logger.error("❌ [PREVIEW] Relatório não encontrado: id={}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Gerar PDF
            byte[] pdfBytes = defectsInspectionPopulater.generatePDF(report);

            if (pdfBytes == null || pdfBytes.length == 0) {
                logger.error("❌ [PREVIEW] Falha ao gerar PDF");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }

            logger.info("✅ [PREVIEW] PDF gerado com sucesso: size={} bytes", pdfBytes.length);

            String filename = buildPdfFilename(report);

            // DIFERENÇA: "inline" em vez de "attachment" (abre no browser)
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + filename + "\"")
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                    .body(pdfBytes);

        } catch (Exception e) {
            logger.error("❌ [PREVIEW] Erro ao fazer preview do PDF para relatório id={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Obter informações sobre o PDF sem fazer download completo
     *
     * Endpoint: GET /api/pdf/defect-inspection/{id}/info
     *
     * Retorna metadata útil como tamanho do ficheiro, nome, URLs de download/preview, etc.
     * Útil para validações ou UI antes de fazer o download efectivo.
     *
     * @param id ID do relatório
     * @return JSON com informações do PDF
     */
    @GetMapping("/{id}/info")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TECH')")
    public ResponseEntity<?> getDefectInspectionPdfInfo(@PathVariable Integer id) {

        logger.info("ℹ️ [INFO] Obtendo informações do PDF para Defect Inspection Report: id={}", id);

        try {
            // Buscar relatório
            DefectsInspectionReport report = defectsInspectionReportService
                    .getDefectsInspectionReportById(id);

            if (report == null) {
                logger.error("❌ [INFO] Relatório não encontrado: id={}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Relatório não encontrado com id: " + id));
            }

            // Gerar PDF para obter tamanho (pode ser optimizado com cache)
            byte[] pdfBytes = defectsInspectionPopulater.generatePDF(report);

            if (pdfBytes == null) {
                logger.error("❌ [INFO] Falha ao gerar PDF");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ErrorResponse("Falha ao gerar PDF"));
            }

            // Construir resposta com informações
            PdfInfoResponse info = new PdfInfoResponse();
            info.setReportId(id);
            info.setUuid(report.getUuid());
            info.setFilename(buildPdfFilename(report));
            info.setSizeBytes(pdfBytes.length);
            info.setSizeKB(Math.round(pdfBytes.length / 1024.0 * 100.0) / 100.0);
            info.setSizeMB(Math.round(pdfBytes.length / (1024.0 * 1024.0) * 100.0) / 100.0);
            info.setSite(report.getSite());
            info.setWtgNumber(report.getWtgNumber());
            info.setWtgType(report.getWtgType());
            info.setYearConstruction(report.getYearConstruction());
            info.setDownloadUrl("/api/pdf/defect-inspection/" + id + "/download");
            info.setPreviewUrl("/api/pdf/defect-inspection/" + id + "/preview");
            info.setGeneratedAt(LocalDateTime.now().format(FILENAME_DATE_FORMAT));

            logger.info("✅ [INFO] Informações do PDF obtidas com sucesso");

            return ResponseEntity.ok(info);

        } catch (Exception e) {
            logger.error("❌ [INFO] Erro ao obter informações do PDF para relatório id={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno ao processar pedido"));
        }
    }

    /**
     * Verificar se um relatório existe e pode gerar PDF
     *
     * Endpoint: GET /api/pdf/defect-inspection/{id}/exists
     *
     * @param id ID do relatório
     * @return 200 OK se existe, 404 NOT FOUND se não existe
     */
    @GetMapping("/{id}/exists")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TECH')")
    public ResponseEntity<?> checkReportExists(@PathVariable Integer id) {

        logger.info("🔍 [EXISTS] Verificando existência do relatório: id={}", id);

        try {
            DefectsInspectionReport report = defectsInspectionReportService
                    .getDefectsInspectionReportById(id);

            if (report == null) {
                logger.info("❌ [EXISTS] Relatório não encontrado: id={}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ExistsResponse(false, "Relatório não encontrado"));
            }

            logger.info("✅ [EXISTS] Relatório encontrado: id={}", id);
            return ResponseEntity.ok(new ExistsResponse(true, "Relatório existe"));

        } catch (Exception e) {
            logger.error("❌ [EXISTS] Erro ao verificar relatório id={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ExistsResponse(false, "Erro ao verificar relatório"));
        }
    }

    // ============================================================================
    // Private Helper Methods
    // ============================================================================

    /**
     * Constrói o nome do ficheiro PDF de forma consistente
     *
     * Formato: DefectInspection_{Site}_{WTG}_{Timestamp}.pdf
     * Exemplo: DefectInspection_WindFarm_WTG-01_20240315_143052.pdf
     */
    private String buildPdfFilename(DefectsInspectionReport report) {
        StringBuilder filename = new StringBuilder("DefectInspection");

        // Adicionar Site (sanitizado)
        if (report.getSite() != null && !report.getSite().trim().isEmpty()) {
            filename.append("_").append(sanitizeFilename(report.getSite()));
        }

        // Adicionar WTG Number (sanitizado)
        if (report.getWtgNumber() != null && !report.getWtgNumber().trim().isEmpty()) {
            filename.append("_").append(sanitizeFilename(report.getWtgNumber()));
        }

        // Adicionar timestamp
        filename.append("_").append(LocalDateTime.now().format(FILENAME_DATE_FORMAT));

        filename.append(".pdf");

        return filename.toString();
    }

    /**
     * Sanitiza string para usar em nome de ficheiro
     * Remove caracteres especiais que podem causar problemas
     */
    private String sanitizeFilename(String input) {
        if (input == null) {
            return "";
        }

        // Replace espaços por underscores e remove caracteres especiais
        return input.trim()
                .replaceAll("\\s+", "_")
                .replaceAll("[^a-zA-Z0-9_-]", "")
                .substring(0, Math.min(input.length(), 50)); // Limitar tamanho
    }

    // ============================================================================
    // Response DTOs
    // ============================================================================

    /**
     * DTO para resposta de informações do PDF
     */
    public static class PdfInfoResponse {
        private Integer reportId;
        private String uuid;
        private String filename;
        private long sizeBytes;
        private double sizeKB;
        private double sizeMB;
        private String site;
        private String wtgNumber;
        private String wtgType;
        private String yearConstruction;
        private String downloadUrl;
        private String previewUrl;
        private String generatedAt;

        // Getters and Setters
        public Integer getReportId() { return reportId; }
        public void setReportId(Integer reportId) { this.reportId = reportId; }

        public String getUuid() { return uuid; }
        public void setUuid(String uuid) { this.uuid = uuid; }

        public String getFilename() { return filename; }
        public void setFilename(String filename) { this.filename = filename; }

        public long getSizeBytes() { return sizeBytes; }
        public void setSizeBytes(long sizeBytes) { this.sizeBytes = sizeBytes; }

        public double getSizeKB() { return sizeKB; }
        public void setSizeKB(double sizeKB) { this.sizeKB = sizeKB; }

        public double getSizeMB() { return sizeMB; }
        public void setSizeMB(double sizeMB) { this.sizeMB = sizeMB; }

        public String getSite() { return site; }
        public void setSite(String site) { this.site = site; }

        public String getWtgNumber() { return wtgNumber; }
        public void setWtgNumber(String wtgNumber) { this.wtgNumber = wtgNumber; }

        public String getWtgType() { return wtgType; }
        public void setWtgType(String wtgType) { this.wtgType = wtgType; }

        public String getYearConstruction() { return yearConstruction; }
        public void setYearConstruction(String yearConstruction) { this.yearConstruction = yearConstruction; }

        public String getDownloadUrl() { return downloadUrl; }
        public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }

        public String getPreviewUrl() { return previewUrl; }
        public void setPreviewUrl(String previewUrl) { this.previewUrl = previewUrl; }

        public String getGeneratedAt() { return generatedAt; }
        public void setGeneratedAt(String generatedAt) { this.generatedAt = generatedAt; }
    }

    /**
     * DTO para resposta de erros
     */
    public static class ErrorResponse {
        private String error;
        private String timestamp;

        public ErrorResponse(String error) {
            this.error = error;
            this.timestamp = LocalDateTime.now().toString();
        }

        public String getError() { return error; }
        public void setError(String error) { this.error = error; }

        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }

    /**
     * DTO para resposta de verificação de existência
     */
    public static class ExistsResponse {
        private boolean exists;
        private String message;

        public ExistsResponse(boolean exists, String message) {
            this.exists = exists;
            this.message = message;
        }

        public boolean isExists() { return exists; }
        public void setExists(boolean exists) { this.exists = exists; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}