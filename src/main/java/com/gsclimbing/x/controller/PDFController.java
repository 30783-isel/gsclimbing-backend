package com.gsclimbing.x.controller;

import com.gsclimbing.database.entity.DefectsInspectionReport;
import com.gsclimbing.database.entity.Report;
import com.gsclimbing.database.service.DefectsInspectionReportService;
import com.gsclimbing.reports.populater.DefectsInspectionPopulater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para geração e download de PDFs de Defect Inspection Reports
 *
 * Endpoints:
 * - GET /api/pdf/defect-inspection/{id}/download - Download do PDF
 */
@CrossOrigin(origins = "*", methods = {
        RequestMethod.GET,
        RequestMethod.OPTIONS
})
@RestController
@RequestMapping(path = "/api/pdf")
@Transactional(readOnly = true)
public class PDFController {

    private static final Logger logger = LoggerFactory.getLogger(PDFController.class);

    @Autowired
    private DefectsInspectionReportService defectsInspectionReportService;

    @Autowired
    private DefectsInspectionPopulater defectsInspectionPopulater;

    /**
     * Download de PDF de Defect Inspection Report por ID
     *
     * Endpoint: GET /api/pdf/defect-inspection/{id}/download
     *
     * Exemplo: http://localhost:8080/api/pdf/defect-inspection/123/download
     *
     * @param id ID do relatório
     * @return PDF bytes com headers apropriados para download
     */
    @GetMapping("/defect-inspection/{id}/download")
    public ResponseEntity<byte[]> downloadDefectInspectionPdf(@PathVariable Integer id) {

        logger.info("📄 Downloading Defect Inspection Report PDF: id={}", id);

        try {
            // 1. Buscar relatório
            DefectsInspectionReport report = defectsInspectionReportService
                    .readDefectsInspectionReport(id);

            if (report == null) {
                logger.error("❌ Report not found: id={}", id);
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(null);
            }

            logger.debug("Report found: site={}, wtgNumber={}",
                    report.getSite(), report.getWtgNumber());

            // 2. Gerar PDF usando o populator refatorizado
            byte[] pdfBytes = defectsInspectionPopulater.generatePDF(report);

            if (pdfBytes == null || pdfBytes.length == 0) {
                logger.error("❌ Failed to generate PDF: empty result");
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(null);
            }

            logger.info("✅ PDF generated successfully: size={} bytes", pdfBytes.length);

            // 3. Preparar nome do ficheiro
            String filename = buildPdfFilename(report);

            // 4. Retornar com headers apropriados
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + filename + "\"")
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                    .body(pdfBytes);

        } catch (Exception e) {
            logger.error("❌ Error generating PDF for report id={}", id, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    /**
     * Preview de PDF (abre no browser em vez de fazer download)
     *
     * Endpoint: GET /api/pdf/defect-inspection/{id}/preview
     *
     * @param id ID do relatório
     * @return PDF bytes com header inline (para preview)
     */
    @GetMapping("/defect-inspection/{id}/preview")
    public ResponseEntity<byte[]> previewDefectInspectionPdf(@PathVariable Integer id) {

        logger.info("👁️ Previewing Defect Inspection Report PDF: id={}", id);

        try {
            DefectsInspectionReport report = defectsInspectionReportService
                    .readDefectsInspectionReport(id);

            if (report == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            byte[] pdfBytes = defectsInspectionPopulater.generatePDF(report);

            if (pdfBytes == null || pdfBytes.length == 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }

            String filename = buildPdfFilename(report);

            // DIFERENÇA: inline em vez de attachment (abre no browser)
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + filename + "\"")
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                    .body(pdfBytes);

        } catch (Exception e) {
            logger.error("❌ Error previewing PDF for report id={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Obter informações sobre o PDF sem fazer download
     *
     * Endpoint: GET /api/pdf/defect-inspection/{id}/info
     *
     * @param id ID do relatório
     * @return Informações do PDF em JSON
     */
    @GetMapping("/defect-inspection/{id}/info")
    public ResponseEntity<?> getDefectInspectionPdfInfo(@PathVariable Integer id) {

        logger.info("ℹ️ Getting PDF info for Defect Inspection Report: id={}", id);

        try {
            DefectsInspectionReport report = defectsInspectionReportService
                    .readDefectsInspectionReport(id);

            if (report == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Report not found with id: " + id));
            }

            // Gerar PDF para obter tamanho
            byte[] pdfBytes = defectsInspectionPopulater.generatePDF(report);

            if (pdfBytes == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ErrorResponse("Failed to generate PDF"));
            }

            PdfInfoResponse info = new PdfInfoResponse();
            info.setReportId(id);
            info.setFilename(buildPdfFilename(report));
            info.setSizeBytes(pdfBytes.length);
            info.setSizeKB(pdfBytes.length / 1024.0);
            info.setSizeMB(pdfBytes.length / (1024.0 * 1024.0));
            info.setSite(report.getSite());
            info.setWtgNumber(report.getWtgNumber());
            info.setWtgType(report.getWtgType());
            info.setDownloadUrl("/api/pdf/defect-inspection/" + id + "/download");
            info.setPreviewUrl("/api/pdf/defect-inspection/" + id + "/preview");

            return ResponseEntity.ok(info);

        } catch (Exception e) {
            logger.error("❌ Error getting PDF info for report id={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    // ============================================================================
    // Helper Methods
    // ============================================================================

    /**
     * Constrói nome do ficheiro PDF baseado nos dados do relatório
     *
     * Formato: DefectInspectionReport_SITE_WTG123_20241223.pdf
     */
    private String buildPdfFilename(DefectsInspectionReport report) {
        StringBuilder filename = new StringBuilder("DefectInspectionReport");

        if (report.getSite() != null && !report.getSite().trim().isEmpty()) {
            filename.append("_").append(sanitizeForFilename(report.getSite()));
        }

        if (report.getWtgNumber() != null && !report.getWtgNumber().trim().isEmpty()) {
            filename.append("_WTG").append(sanitizeForFilename(report.getWtgNumber()));
        }

        // Adicionar timestamp
        filename.append("_").append(java.time.LocalDate.now().toString().replace("-", ""));

        filename.append(".pdf");

        return filename.toString();
    }

    /**
     * Remove caracteres inválidos do nome do ficheiro
     */
    private String sanitizeForFilename(String input) {
        if (input == null) return "";
        // Remover caracteres que não são permitidos em nomes de ficheiros
        return input.replaceAll("[^a-zA-Z0-9.-]", "_")
                .replaceAll("_{2,}", "_"); // Substituir múltiplos underscores por um
    }

    // ============================================================================
    // DTOs
    // ============================================================================

    /**
     * DTO para resposta de informações do PDF
     */
    public static class PdfInfoResponse {
        private Integer reportId;
        private String filename;
        private long sizeBytes;
        private double sizeKB;
        private double sizeMB;
        private String site;
        private String wtgNumber;
        private String wtgType;
        private String downloadUrl;
        private String previewUrl;

        // Getters e Setters
        public Integer getReportId() { return reportId; }
        public void setReportId(Integer reportId) { this.reportId = reportId; }

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

        public String getDownloadUrl() { return downloadUrl; }
        public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }

        public String getPreviewUrl() { return previewUrl; }
        public void setPreviewUrl(String previewUrl) { this.previewUrl = previewUrl; }
    }

    /**
     * DTO para respostas de erro
     */
    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
}