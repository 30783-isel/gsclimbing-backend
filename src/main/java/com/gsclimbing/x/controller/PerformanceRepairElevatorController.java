package com.gsclimbing.x.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.gsclimbing.database.entity.PerformanceReportRepairElevator;
import com.gsclimbing.database.entity.Report;
import com.gsclimbing.database.entity.Turbine;
import com.gsclimbing.x.adapter.PerformanceRepairElevatorAdapter;
import com.gsclimbing.x.database.service.BaseReportService;
import com.gsclimbing.x.database.service.PerformanceRepairElevatorService;
import com.gsclimbing.x.database.service.PerformanceRepairElevatorService.PerformanceReportRepairElevatorSpecificData;
import com.gsclimbing.x.dto.MobileReportDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller para Performance Report Repair Elevator
 * Estende BaseReportController para reutilizar lógica comum
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
@RequestMapping(path = "/api/reports/mobile")
public class PerformanceRepairElevatorController extends BaseReportController<PerformanceReportRepairElevator> {

    @Autowired
    private PerformanceRepairElevatorService performanceRepairElevatorService;

    @Autowired
    private PerformanceRepairElevatorAdapter performanceRepairElevatorAdapter;

    // ========================================
    // IMPLEMENTAÇÃO DOS MÉTODOS ABSTRATOS
    // ========================================

    @Override
    protected BaseReportService<PerformanceReportRepairElevator> getReportService() {
        return performanceRepairElevatorService;
    }

    @Override
    protected PerformanceRepairElevatorAdapter getReportAdapter() {
        return performanceRepairElevatorAdapter;
    }

    @Override
    protected String getEndpointName() {
        return "performance-repair-elevator";
    }

    @Override
    protected int getReportType() {
        return 6; // Performance Report Repair Elevator
    }

    // ========================================
    // ENDPOINTS REST (delegam para BaseReportController)
    // ========================================

    /**
     * POST /api/reports/mobile/performance-repair-elevator
     * Criar novo Performance Report Repair Elevator
     */
    @PostMapping("/performance-repair-elevator")
    public ResponseEntity<?> createPerformanceRepairElevator(
            @RequestBody MobileReportDTO.ReportCreateUpdateDTO dto) {

        logger.info("📋 POST /performance-repair-elevator called");

        try {
            // Validar turbina
            Turbine turbine = turbineService.getTurbine(dto.getTurbineId().intValue());
            if (turbine == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(errorResponse("Turbine not found"));
            }

            // Converter DTO para entidade usando adapter
            PerformanceReportRepairElevator report = performanceRepairElevatorAdapter.toEntity(dto, turbine);

            // Extrair dados específicos
            PerformanceReportRepairElevatorSpecificData specificData =
                    performanceRepairElevatorAdapter.toSpecificData(dto);

            // Criar usando método específico do service
            PerformanceReportRepairElevator saved =
                    performanceRepairElevatorService.createComplete(report, specificData);

            // Associar fotos
            int numberPictures = 0;
            if (dto.getPhotoFileIds() != null && !dto.getPhotoFileIds().isEmpty()) {
                numberPictures = associatePhotosToReport(
                        saved.getReportId(),
                        dto.getPhotoFileIds()
                );
            }

            logger.info("✅ Performance Report created with ID: {}", saved.getReportId());

            return ResponseEntity.ok(successResponse(
                    "Report created successfully",
                    "reportId", saved.getReportId(),
                    "uuid", saved.getUuid(),
                    "numberOfPhotos", numberPictures
            ));

        } catch (Exception e) {
            logger.error("❌ Error creating Performance Report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Error creating report: " + e.getMessage()));
        }
    }

    /**
     * GET /api/reports/mobile/performance-repair-elevator/{id}
     * Obter Performance Report por ID
     */
    @GetMapping("/performance-repair-elevator/{id}")
    public ResponseEntity<?> getPerformanceRepairElevator(@PathVariable Integer id) {

        logger.info("📖 GET /performance-repair-elevator/{} called", id);

        try {
            // Usar método específico que retorna dados completos
            PerformanceReportRepairElevator report =
                    performanceRepairElevatorService.getCompleteById(id);

            if (report == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(errorResponse("Report not found"));
            }

            // Criar resposta com campos específicos
            Map<String, Object> response = convertToPerformanceDTO(report);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("❌ Error fetching Performance Report {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Error fetching report: " + e.getMessage()));
        }
    }

    /**
     * GET /api/reports/mobile/performance-repair-elevator/turbine/{turbineId}
     * Obter todos os Performance Reports de uma turbina
     */
    @GetMapping("/performance-repair-elevator/turbine/{turbineId}")
    public ResponseEntity<?> getPerformanceRepairElevatorsByTurbine(@PathVariable Integer turbineId) {

        logger.info("📖 GET /performance-repair-elevator/turbine/{} called", turbineId);
        return getByTurbine(turbineId); // Método da classe base
    }

    /**
     * GET /api/reports/mobile/performance-repair-elevator/project/{projectId}
     * Obter todos os Performance Reports de um projeto
     */
    @GetMapping("/performance-repair-elevator/project/{projectId}")
    public ResponseEntity<?> getPerformanceRepairElevatorsByProject(@PathVariable Integer projectId) {

        logger.info("📖 GET /performance-repair-elevator/project/{} called", projectId);
        return getByProject(projectId); // Método da classe base
    }

    /**
     * PUT /api/reports/mobile/performance-repair-elevator/{id}
     * Atualizar Performance Report
     */
    @PutMapping("/performance-repair-elevator/{id}")
    public ResponseEntity<?> updatePerformanceRepairElevator(
            @PathVariable Integer id,
            @RequestBody MobileReportDTO.ReportCreateUpdateDTO dto,
            Authentication authentication) {

        logger.info("🔧 PUT /performance-repair-elevator/{} called", id);

        try {
            String username = authentication.getName();

            // Buscar relatório existente
            PerformanceReportRepairElevator oldReport =
                    performanceRepairElevatorService.getCompleteById(id);

            if (oldReport == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(errorResponse("Report not found"));
            }

            // Guardar valores antigos
            Map<String, String> oldValues = capturePerformanceOldValues(oldReport);

            // Atualizar entidade
            performanceRepairElevatorAdapter.updateEntity(oldReport, dto);

            // Atualizar dados específicos
            PerformanceReportRepairElevatorSpecificData specificData =
                    new PerformanceReportRepairElevatorSpecificData();
            performanceRepairElevatorAdapter.updateSpecificData(specificData, dto);

            // Salvar
            PerformanceReportRepairElevator updated =
                    performanceRepairElevatorService.updateComplete(oldReport, specificData);

            // Detectar alterações
            detectAndLogChanges(oldValues, updated, username);

            logger.info("✅ Performance Report updated successfully");

            return ResponseEntity.ok(successResponse(
                    "Report updated successfully",
                    "reportId", id,
                    "uuid", updated.getUuid()
            ));

        } catch (Exception e) {
            logger.error("❌ Error updating Performance Report {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Error updating report: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/reports/mobile/performance-repair-elevator/{id}
     * Eliminar Performance Report
     */
    @DeleteMapping("/performance-repair-elevator/{id}")
    public ResponseEntity<?> deletePerformanceRepairElevator(
            @PathVariable Integer id,
            Authentication authentication) {

        logger.info("🗑️ DELETE /performance-repair-elevator/{} called", id);
        return delete(id, authentication); // Método da classe base
    }

    /**
     * GET /api/reports/mobile/performance-repair-elevator/{id}/photos
     * Obter fotos de um Performance Report
     */
    @GetMapping("/performance-repair-elevator/{id}/photos")
    public ResponseEntity<?> getPerformanceRepairElevatorPhotos(@PathVariable Integer id) {

        logger.info("📸 GET /performance-repair-elevator/{}/photos called", id);
        return getPhotos(id); // Método da classe base
    }

    /**
     * GET /api/reports/mobile/performance-repair-elevator/{id}/history
     * Obter histórico de alterações de um Performance Report
     */
    @GetMapping("/performance-repair-elevator/{id}/history")
    public ResponseEntity<?> getPerformanceRepairElevatorHistory(@PathVariable Long id) {

        logger.info("📜 GET /performance-repair-elevator/{}/history called", id);
        return getHistory(id); // Método da classe base
    }

    // ========================================
    // MÉTODOS AUXILIARES ESPECÍFICOS
    // ========================================

    /**
     * Converter Performance Report para DTO com campos específicos
     */
    private Map<String, Object> convertToPerformanceDTO(PerformanceReportRepairElevator report) {
        Map<String, Object> response = new HashMap<>();

        // Campos base
        response.put("reportId", report.getReportId());
        response.put("uuid", report.getUuid());
        response.put("site", report.getSite());
        response.put("wtgNumber", report.getWtgNumber());
        response.put("wtgType", report.getWtgType());
        response.put("yearConstruction", report.getYearConstruction());
        response.put("createDate", report.getCreateDate());
        response.put("modifiedDate", report.getModifiedDate());

        // Campos específicos de Performance Report
        response.put("reportNumber", report.getReportNumber());
        response.put("inpectorsWorkers", report.getInpectorsWorkers());
        response.put("statementOfwork", report.getStatementOfwork());
        response.put("workCompleted", report.getWorkCompleted());
        response.put("turbineOperable", report.getTurbineOperable());
        response.put("placeDate", report.getPlaceDate());
        response.put("responsibleTechnician", report.getResponsibleTechnician());
        response.put("performanceReport", report.getPerformanceReport());

        // Campos adicionais
        for (int i = 1; i <= 7; i++) {
            response.put("additionalField" + i + "Label", getAdditionalFieldLabel(report, i));
            response.put("additionalField" + i + "Text", getAdditionalFieldText(report, i));
        }

        return response;
    }

    /**
     * Capturar valores antigos específicos de Performance Report
     */
    private Map<String, String> capturePerformanceOldValues(PerformanceReportRepairElevator report) {
        Map<String, String> oldValues = captureOldValues(report); // Método base

        // Adicionar campos específicos
        oldValues.put("reportNumber", report.getReportNumber());
        oldValues.put("inpectorsWorkers", report.getInpectorsWorkers());
        oldValues.put("workCompleted", report.getWorkCompleted());
        oldValues.put("turbineOperable", report.getTurbineOperable());
        oldValues.put("performanceReport", report.getPerformanceReport());

        return oldValues;
    }

    /**
     * Detectar e registar alterações
     */
    private void detectAndLogChanges(
            Map<String, String> oldValues,
            PerformanceReportRepairElevator newReport,
            String username) {

        // Comparar campos base
        compareAndLog(oldValues, newReport, "site", newReport.getSite(), username);
        compareAndLog(oldValues, newReport, "wtgNumber", newReport.getWtgNumber(), username);
        compareAndLog(oldValues, newReport, "wtgType", newReport.getWtgType(), username);

        // Comparar campos específicos
        compareAndLog(oldValues, newReport, "reportNumber", newReport.getReportNumber(), username);
        compareAndLog(oldValues, newReport, "inpectorsWorkers", newReport.getInpectorsWorkers(), username);
        compareAndLog(oldValues, newReport, "workCompleted", newReport.getWorkCompleted(), username);
        compareAndLog(oldValues, newReport, "turbineOperable", newReport.getTurbineOperable(), username);
        compareAndLog(oldValues, newReport, "performanceReport", newReport.getPerformanceReport(), username);
    }

    /**
     * Comparar campo e registar se alterado
     */
    private void compareAndLog(
            Map<String, String> oldValues,
            Report report,
            String fieldName,
            String newValue,
            String username) {

        String oldValue = oldValues.get(fieldName);
        if (!java.util.Objects.equals(oldValue, newValue)) {
            historyService.createFieldChangeEntry(report, username, fieldName, oldValue, newValue);
        }
    }

    /**
     * Obter label de campo adicional
     */
    private String getAdditionalFieldLabel(Report report, int fieldNumber) {
        try {
            String methodName = "getAdditionalField" + fieldNumber + "Label";
            return (String) Report.class.getMethod(methodName).invoke(report);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Obter texto de campo adicional
     */
    private String getAdditionalFieldText(Report report, int fieldNumber) {
        try {
            String methodName = "getAdditionalField" + fieldNumber + "Text";
            return (String) Report.class.getMethod(methodName).invoke(report);
        } catch (Exception e) {
            return null;
        }
    }
}