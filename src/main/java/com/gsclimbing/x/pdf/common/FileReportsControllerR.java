package com.gsclimbing.x.pdf.common;

import com.gsclimbing.commons.enums.ReportEnum;
import com.gsclimbing.database.entity.Project;
import com.gsclimbing.database.entity.Report;
import com.gsclimbing.database.entity.Turbine;
import com.gsclimbing.database.service.*;
import com.gsclimbing.reports.populater.*;
import com.gsclimbing.zip.ZipUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Controller refatorizado para geração e download de relatórios PDF.
 *
 * Melhorias:
 * - Eliminação de código duplicado
 * - Logging detalhado
 * - Tratamento robusto de erros
 * - Uso de Strategy Pattern para diferentes tipos de relatórios
 * - Gestão automática de recursos
 */
@CrossOrigin(origins = "*", methods = {
        RequestMethod.OPTIONS,
        RequestMethod.GET,
        RequestMethod.POST,
        RequestMethod.PUT,
        RequestMethod.DELETE
})
@Transactional
@RestController
@RequestMapping(path = "/api/reports/files")
public class FileReportsControllerR {

    private static final Logger logger = LoggerFactory.getLogger(FileReportsControllerR.class);

    // ============================================================================
    // Services
    // ============================================================================

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TurbineService turbineService;

    // Report Services
    @Autowired
    private DefectsInspectionReportService defectsInspectionReportService;

    @Autowired
    private ExaminationTransformerService examinationTransformerService;

    @Autowired
    private Medidas6KvService medidas6KvService;

    @Autowired
    private Medidas690V400VService medidas690V400VService;

    @Autowired
    private MeasurementsMwSwitchgearService measurementsMwSwitchgearService;

    @Autowired
    private OnboardCraneInspectionReportService onboardCraneInspectionReportService;

    @Autowired
    private PerformanceReportRepairElevatorService performanceReportRepairElevatorService;

    @Autowired
    private StatutoryInspectionReportReportService statutoryInspectionReportReportService;

    // Populators
    @Autowired
    private DefectsInspectionPopulater defectsInspectionPopulater;

    @Autowired
    private ExaminationTransformerPopulater examinationTransformerPopulater;

    @Autowired
    private Medidas6KvPopulater medidas6KvPopulater;

    @Autowired
    private Medidas690V400VPopulater medidas690V400VPopulater;

    @Autowired
    private MeasurementsMwSwitchgearPopulater measurementsMwSwitchgearPopulater;

    @Autowired
    private OnboardCraneInspectionReportElevatorPopulater onboardCraneInspectionReportElevatorPopulater;

    @Autowired
    private PerformanceReportRepairElevatorPopulater performanceReportRepairElevatorPopulater;

    @Autowired
    private StatutoryInspectionReportElevatorPopulater statutoryInspectionReportElevatorPopulater;

    @Autowired
    private SimplePdfTemplateService simplePdfTemplateService;

    // ============================================================================
    // Public Endpoints
    // ============================================================================

    /**
     * Download de um único relatório em PDF
     *
     * @param typeReport Tipo do relatório (enum ordinal)
     * @param id ID do relatório
     * @return PDF bytes com headers apropriados
     */
    @RequestMapping("/download_pdfR/{typeReport}/{id}")
    public ResponseEntity<byte[]> downloadSingleReport(
            @PathVariable Integer typeReport,
            @PathVariable Integer id) {

        logger.info("📄 Downloading single report: type={}, id={}", typeReport, id);

        try {
            ReportEnum reportEnum = ReportEnum.values()[typeReport];
            String filename = reportEnum.getLabel() + ".pdf";

            byte[] pdfBytes = generatePdfForReport(reportEnum, id);

            if (pdfBytes == null || pdfBytes.length == 0) {
                logger.error("❌ Failed to generate PDF: empty result");
                return ResponseEntity.notFound().build();
            }

            logger.info("✅ PDF generated successfully: size={} bytes", pdfBytes.length);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + filename + "\"")
                    .body(pdfBytes);

        } catch (Exception e) {
            logger.error("❌ Error downloading report: type={}, id={}", typeReport, id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Download de todos os relatórios de um projeto como ZIP
     * Cada turbina fica numa pasta separada dentro do ZIP
     *
     * @param projectId ID do projeto
     * @return ZIP file com todos os relatórios
     */
    @RequestMapping("/download_zipR/{projectId}")
    public ResponseEntity<byte[]> downloadProjectReportsZip(@PathVariable int projectId) {

        logger.info("📦 Creating ZIP for project: id={}", projectId);

        try {
            Project project = projectService.getProject(projectId);
            if (project == null) {
                logger.error("❌ Project not found: id={}", projectId);
                return ResponseEntity.notFound().build();
            }

            byte[] zipBytes = createProjectZip(project);

            if (zipBytes == null || zipBytes.length == 0) {
                logger.error("❌ Failed to create ZIP: empty result");
                return ResponseEntity.noContent().build();
            }

            String filename = project.getName() + ".zip";
            logger.info("✅ ZIP created successfully: size={} bytes", zipBytes.length);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + filename + "\"")
                    .body(zipBytes);

        } catch (Exception e) {
            logger.error("❌ Error creating ZIP for project: id={}", projectId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Download de templates limpos (sem dados) de todos os tipos de relatório
     *
     * @param projectId ID do projeto (para nome do ZIP e dados básicos)
     * @return ZIP file com templates limpos
     */
    @RequestMapping("/download_clean_zipR/{projectId}")
    public ResponseEntity<byte[]> downloadCleanTemplatesZip(@PathVariable int projectId) {

        logger.info("📦 Creating clean templates ZIP for project: id={}", projectId);

        try {
            Project project = projectService.getProject(projectId);
            if (project == null) {
                logger.error("❌ Project not found: id={}", projectId);
                return ResponseEntity.notFound().build();
            }

            byte[] zipBytes = createCleanTemplatesZip(project);

            if (zipBytes == null || zipBytes.length == 0) {
                logger.error("❌ Failed to create clean templates ZIP");
                return ResponseEntity.noContent().build();
            }

            String filename = project.getName() + ".zip";
            logger.info("✅ Clean templates ZIP created: size={} bytes", zipBytes.length);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + filename + "\"")
                    .body(zipBytes);

        } catch (Exception e) {
            logger.error("❌ Error creating clean templates ZIP: projectId={}", projectId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ============================================================================
    // Private Helper Methods - ZIP Creation
    // ============================================================================

    /**
     * Cria ZIP com todos os relatórios do projeto
     * Organiza relatórios por turbina em pastas separadas
     */
    private byte[] createProjectZip(Project project) throws IOException {
        Path tempDir = null;

        try {
            tempDir = Files.createTempDirectory("project-reports-");
            logger.debug("Created temp directory: {}", tempDir);

            List<Turbine> turbines = turbineService.getTurbinesByProject(project);
            logger.info("Processing {} turbines", turbines.size());

            int totalReports = 0;

            for (Turbine turbine : turbines) {
                Path turbineDir = Files.createTempDirectory(
                        tempDir,
                        sanitizeFilename(turbine.getName()) + "-"
                );

                int reportCount = processReportsForTurbine(turbine, turbineDir);
                totalReports += reportCount;

                logger.debug("Turbine '{}': {} reports generated", turbine.getName(), reportCount);
            }

            logger.info("Total reports generated: {}", totalReports);

            if (totalReports == 0) {
                logger.warn("No reports generated for project: {}", project.getName());
                return new byte[0];
            }

            // Criar ZIP
            String zipPath = ZipUtils.ZipDirectory(tempDir.toString(), project.getName());
            return FileUtils.readFileToByteArray(new File(zipPath));

        } finally {
            cleanupTempDirectory(tempDir);
        }
    }

    /**
     * Processa todos os relatórios de uma turbina
     * @return número de relatórios gerados
     */
    private int processReportsForTurbine(Turbine turbine, Path outputDir) {
        int count = 0;

        for (Report report : turbine.getListReports()) {
            try {
                ReportEnum reportType = ReportEnum.values()[report.getTypeReport()];
                String filename = reportType.getLabel() + ".pdf";

                byte[] pdfBytes = generatePdfForReport(reportType, report.getReportId());

                if (pdfBytes != null && pdfBytes.length > 0) {
                    File outputFile = new File(outputDir.toString(), filename);

                    try (OutputStream outStream = new FileOutputStream(outputFile)) {
                        outStream.write(pdfBytes);
                        count++;
                    }
                } else {
                    logger.warn("Empty PDF generated for report: id={}, type={}",
                            report.getReportId(), reportType);
                }

            } catch (Exception e) {
                logger.error("Failed to generate report: id={}", report.getReportId(), e);
                // Continua com próximo relatório
            }
        }

        return count;
    }

    /**
     * Cria ZIP com templates limpos (apenas site, number, type preenchidos)
     */
    private byte[] createCleanTemplatesZip(Project project) throws IOException {
        Path tempDir = null;

        try {
            tempDir = Files.createTempDirectory("clean-templates-");
            logger.debug("Created temp directory for clean templates: {}", tempDir);

            // Determinar quais tipos de relatórios existem no projeto
            Set<ReportEnum> reportTypes = determineReportTypes(project);
            logger.info("Report types to generate: {}", reportTypes);

            int generatedCount = 0;

            for (ReportEnum reportType : reportTypes) {
                try {
                    byte[] pdfBytes = simplePdfTemplateService.populateBasicFields(
                            reportType,
                            project.getSite(),
                            project.getNumber(),
                            project.getType()
                    );

                    if (pdfBytes != null && pdfBytes.length > 0) {
                        String filename = reportType.getLabel() + ".pdf";
                        File outputFile = new File(tempDir.toString(), filename);

                        try (OutputStream outStream = new FileOutputStream(outputFile)) {
                            outStream.write(pdfBytes);
                            generatedCount++;
                        }
                    }

                } catch (Exception e) {
                    logger.error("Failed to generate clean template: type={}", reportType, e);
                }
            }

            logger.info("Generated {} clean templates", generatedCount);

            if (generatedCount == 0) {
                return new byte[0];
            }

            String zipPath = ZipUtils.ZipDirectory(tempDir.toString(), project.getName());
            return FileUtils.readFileToByteArray(new File(zipPath));

        } finally {
            cleanupTempDirectory(tempDir);
        }
    }

    /**
     * Determina quais tipos de relatórios existem no projeto
     */
    private Set<ReportEnum> determineReportTypes(Project project) {
        Set<ReportEnum> types = new HashSet<>();

        List<Turbine> turbines = turbineService.getTurbinesByProject(project);

        for (Turbine turbine : turbines) {
            if (turbine.isDefectsInspectionReport())
                types.add(ReportEnum.DIR);
            if (turbine.isOnboardCraneInspectionReport())
                types.add(ReportEnum.OCIR);
            if (turbine.isPerformanceReportRepairElevator())
                types.add(ReportEnum.PRRE);
            if (turbine.isStatutoryInspectionReport())
                types.add(ReportEnum.SIR);
            if (turbine.isMeasurementsMwSwitchgear())
                types.add(ReportEnum.MMSSC);
            if (turbine.isExaminationTransformer())
                types.add(ReportEnum.ET);
            if (turbine.isMeasurements6KV())
                types.add(ReportEnum.M6KV);
            if (turbine.isMeasurements690V400V())
                types.add(ReportEnum.M690V400V);
        }

        return types;
    }

    // ============================================================================
    // Private Helper Methods - PDF Generation
    // ============================================================================

    /**
     * Gera PDF para um relatório específico usando o populator apropriado
     * Usa Strategy Pattern baseado no tipo de relatório
     */
    private byte[] generatePdfForReport(ReportEnum reportType, Integer reportId) {
        logger.debug("Generating PDF: type={}, id={}", reportType, reportId);

        try {
            switch (reportType) {
                case DIR:
                    Report dirReport = defectsInspectionReportService.readDefectsInspectionReport(reportId);
                    return defectsInspectionPopulater.generatePDF(dirReport);

                case ET:
                    Report etReport = examinationTransformerService.readExaminationTransformer(reportId);
                    return examinationTransformerPopulater.generatePDF(etReport);

                case MMSSC:
                    Report mmsscReport = measurementsMwSwitchgearService.readMeasurementsMwSwitchgear(reportId);
                    return measurementsMwSwitchgearPopulater.generatePDF(mmsscReport);

                case M6KV:
                    Report m6kvReport = medidas6KvService.readMedidas6Kv(reportId);
                    return medidas6KvPopulater.generatePDF(m6kvReport);

                case M690V400V:
                    Report m690Report = medidas690V400VService.readMedidas690V400V(reportId);
                    return medidas690V400VPopulater.generatePDF(m690Report);

                case OCIR:
                    Report ocirReport = onboardCraneInspectionReportService.readOnboardCraneInspectionReport(reportId);
                    return onboardCraneInspectionReportElevatorPopulater.generatePDF(ocirReport);

                case PRRE:
                    Report prreReport = performanceReportRepairElevatorService.readPerformanceReportRepairElevator(reportId);
                    return performanceReportRepairElevatorPopulater.generatePDF(prreReport);

                case SIR:
                    Report sirReport = statutoryInspectionReportReportService.readStatutoryInspectionReport(reportId);
                    return statutoryInspectionReportElevatorPopulater.generatePDF(sirReport);

                default:
                    logger.error("Unknown report type: {}", reportType);
                    return null;
            }

        } catch (Exception e) {
            logger.error("Error generating PDF: type={}, id={}", reportType, reportId, e);
            return null;
        }
    }

    // ============================================================================
    // Utility Methods
    // ============================================================================

    /**
     * Sanitiza nome de ficheiro removendo caracteres inválidos
     */
    private String sanitizeFilename(String filename) {
        if (filename == null) return "unknown";
        return filename.replaceAll("[^a-zA-Z0-9.-]", "_");
    }

    /**
     * Limpa diretório temporário de forma segura
     */
    private void cleanupTempDirectory(Path tempDir) {
        if (tempDir != null) {
            try {
                FileUtils.deleteDirectory(tempDir.toFile());
                logger.debug("Cleaned up temp directory: {}", tempDir);
            } catch (IOException e) {
                logger.warn("Failed to cleanup temp directory: {}", tempDir, e);
            }
        }
    }
}
