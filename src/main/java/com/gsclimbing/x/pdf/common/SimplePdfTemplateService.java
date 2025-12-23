package com.gsclimbing.x.pdf.common;

import com.gsclimbing.commons.enums.ReportEnum;
import com.gsclimbing.ftp.FTPDownloadFiles;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Serviço para popular templates PDF com apenas campos básicos (site, number, type).
 * Usado para gerar templates limpos sem dados completos de relatórios.
 */
@Service
public class SimplePdfTemplateService {

    private static final Logger logger = LoggerFactory.getLogger(SimplePdfTemplateService.class);

    // Mapeamento de tipos de relatório para localização do template
    private static final Map<ReportEnum, TemplateInfo> TEMPLATE_LOCATIONS = new HashMap<>();

    static {
        TEMPLATE_LOCATIONS.put(ReportEnum.DIR,
                new TemplateInfo("/defectInspectionReport/", "Defect Inspection Report.pdf"));

        TEMPLATE_LOCATIONS.put(ReportEnum.OCIR,
                new TemplateInfo("/onboardCraneInspectionReport/", "Onboard crane Inspection Report.pdf"));

        TEMPLATE_LOCATIONS.put(ReportEnum.PRRE,
                new TemplateInfo("/performanceReportRepairElevator/", "Performance Report Repair Elevator.pdf"));

        TEMPLATE_LOCATIONS.put(ReportEnum.SIR,
                new TemplateInfo("/statutoryInspectionReport/", "Statutory Inspection Report.pdf"));

        TEMPLATE_LOCATIONS.put(ReportEnum.MMSSC,
                new TemplateInfo("/measurementsMVSwitchgearStatorCabinet/", "Measurements of MV Switchgear and Stator Cabinet.pdf"));

        TEMPLATE_LOCATIONS.put(ReportEnum.ET,
                new TemplateInfo("/examinationTransformer/", "Examination Transformer.pdf"));

        TEMPLATE_LOCATIONS.put(ReportEnum.M6KV,
                new TemplateInfo("/medidas6Kv/", "Medidas 6Kv.pdf"));

        TEMPLATE_LOCATIONS.put(ReportEnum.M690V400V,
                new TemplateInfo("/medidas690V400V/", "Medidas 690V400V.pdf"));
    }

    /**
     * Popula campos básicos num template PDF
     *
     * @param reportType Tipo do relatório
     * @param site Nome do site
     * @param wtgNumber Número da turbina (WTG)
     * @param wtgType Tipo da turbina
     * @return PDF bytes com campos básicos preenchidos
     */
    public byte[] populateBasicFields(ReportEnum reportType, String site,
                                      String wtgNumber, String wtgType) throws IOException {

        logger.debug("Populating basic fields: type={}, site={}, wtg={}",
                reportType, site, wtgNumber);

        TemplateInfo templateInfo = TEMPLATE_LOCATIONS.get(reportType);

        if (templateInfo == null) {
            throw new IllegalArgumentException("No template found for report type: " + reportType);
        }

        PDDocument pdfDocument = null;

        try {
            // Carregar template do FTP
            InputStream templateStream = FTPDownloadFiles.downloadPdfReportByTeamToTempFile(
                    templateInfo.folder,
                    templateInfo.filename
            );

            if (templateStream == null) {
                throw new IOException("Failed to download template: " + templateInfo.filename);
            }

            pdfDocument = PDDocument.load(templateStream);
            logger.debug("Template loaded: {} pages", pdfDocument.getNumberOfPages());

            // Popular apenas campos básicos
            setFieldIfExists(pdfDocument, "site", site);
            setFieldIfExists(pdfDocument, "wtgNumber", wtgNumber);
            setFieldIfExists(pdfDocument, "wtgType", wtgType);

            // Converter para bytes
            return convertToBytes(pdfDocument);

        } finally {
            if (pdfDocument != null) {
                try {
                    pdfDocument.close();
                } catch (IOException e) {
                    logger.warn("Error closing PDF document", e);
                }
            }
        }
    }

    /**
     * Define valor de um campo se ele existir no PDF
     */
    private void setFieldIfExists(PDDocument pdfDocument, String fieldName, String value) {
        if (value == null || value.trim().isEmpty()) {
            return;
        }

        try {
            PDDocumentCatalog catalog = pdfDocument.getDocumentCatalog();
            if (catalog == null) {
                logger.warn("Document catalog is null");
                return;
            }

            PDAcroForm acroForm = catalog.getAcroForm();
            if (acroForm == null) {
                logger.warn("AcroForm is null");
                return;
            }

            PDField field = acroForm.getField(fieldName);
            if (field != null) {
                field.setValue(value);
                logger.trace("Set field: {}={}", fieldName, value);
            } else {
                logger.trace("Field not found: {}", fieldName);
            }

        } catch (IOException e) {
            logger.warn("Error setting field: name={}, value={}", fieldName, value, e);
        }
    }

    /**
     * Converte PDDocument para array de bytes
     */
    private byte[] convertToBytes(PDDocument pdfDocument) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            pdfDocument.save(outputStream);

            try (InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray())) {
                byte[] pdfBytes = IOUtils.toByteArray(inputStream);
                logger.debug("PDF converted to bytes: size={}", pdfBytes.length);
                return pdfBytes;
            }
        }
    }

    /**
     * Informação sobre localização do template
     */
    private static class TemplateInfo {
        final String folder;
        final String filename;

        TemplateInfo(String folder, String filename) {
            this.folder = folder;
            this.filename = filename;
        }
    }
}