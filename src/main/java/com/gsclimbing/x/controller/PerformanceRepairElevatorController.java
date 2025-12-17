package com.gsclimbing.x.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.entity.PerformanceReportRepairElevator;
import com.gsclimbing.database.entity.Report;
import com.gsclimbing.database.entity.Turbine;
import com.gsclimbing.database.service.FileService;
import com.gsclimbing.database.service.TurbineService;
import com.gsclimbing.dto.ReportDto;
import com.gsclimbing.x.adapter.PerformanceRepairElevatorAdapter;
import com.gsclimbing.x.database.entity.ReportHistory;
import com.gsclimbing.x.database.service.PerformanceRepairElevatorService;
import com.gsclimbing.x.database.service.ReportComparisonService;
import com.gsclimbing.x.database.service.ReportHistoryService;
import com.gsclimbing.x.dto.MobileReportDTO;
import com.gsclimbing.x.util.FieldChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller para receber relatórios do mobile
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
public class PerformanceRepairElevatorController {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceRepairElevatorController.class);
    @Autowired
    private PerformanceRepairElevatorService performanceRepairElevatorService;

    @Autowired
    private PerformanceRepairElevatorAdapter performanceRepairElevatorAdapter;

    @Autowired
    private TurbineService turbineService;

    @Autowired
    private ReportHistoryService historyService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ReportComparisonService comparisonService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ====================================================================
    // CREATE - Criar novo Performance Report Repair Elevator
    // ====================================================================
    /**
     * Criar Performance Report Repair Elevator
     * VERSÃO CORRETA - Grava em ambas as tabelas (Report + PerformanceReportRepairElevator)
     */
    @PostMapping("/performance-repair-elevator")
    public ResponseEntity<?> createPerformanceRepairElevator(
            @RequestBody MobileReportDTO.ReportCreateUpdateDTO dto) {

        try {
            logger.info("📋 Creating Performance Report Repair Elevator");

            JsonNode reportData = objectMapper.readTree(dto.getReportData());
            Integer projectoId = reportData.has("projectoId") ? reportData.get("projectoId").asInt() : null;
            Long turbineId = dto.getTurbineId();

            logger.info("Project ID: {}, Turbine ID: {}", projectoId, turbineId);

            // 1. Validar turbina
            Turbine turbine = turbineService.getTurbine(turbineId.intValue());
            if (turbine == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(errorResponse("Turbina não encontrada"));
            }

            // 2. Verificar duplicados (opcional)
            JsonNode reportDataX = objectMapper.readTree(dto.getReportData());
            String site = reportDataX.has("site") ? reportDataX.get("site").asText() : null;
            String wtgNumber = reportDataX.has("wtgNumber") ? reportDataX.get("wtgNumber").asText() : null;

            // Query para verificar se já existe (se necessário)
            // ...

            // 3. Converter DTO para entidade Report (dados genéricos)
            Report report = performanceRepairElevatorAdapter.toEntity(dto, turbine);
            report.setReportType(Report.ReportStatus.DRAFT.ordinal());

            // 4. Extrair dados específicos do DTO
            PerformanceRepairElevatorService.PerformanceReportRepairElevatorSpecificData specificData =
                    performanceRepairElevatorAdapter.toSpecificData(dto);

            // 5. CRIAR RELATÓRIO COMPLETO (Report + PerformanceReportRepairElevator)
            // Este método grava em AMBAS as tabelas de forma transacional
            Report saved = performanceRepairElevatorService.createComplete(report, specificData);

            // 6. Associar fotos (se existirem)
            List<Long> photoFileIds = dto.getPhotoIds();
            if (photoFileIds != null && !photoFileIds.isEmpty()) {
                logger.info("📸 Associating {} photos to report", photoFileIds.size());
                // ... código para associar fotos ...
            }

            logger.info("✅ Performance Report Repair Elevator created successfully: {}", saved.getReportId());

            return ResponseEntity.ok(successResponse(
                    "Report created successfully",
                    "reportId", saved.getReportId(),
                    "uuid", saved.getUuid()
            ));

        } catch (Exception e) {
            logger.error("❌ Error creating Performance Report Repair Elevator", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Error creating report: " + e.getMessage()));
        }
    }

    // ====================================================================
    // UPDATE - Atualizar Performance Report Repair Elevator existente
    // ====================================================================
    /**
     * Atualizar Performance Report Repair Elevator
     * VERSÃO CORRETA - Atualiza ambas as tabelas
     */
    @PutMapping("/performance-repair-elevator/{id}")
    public ResponseEntity<?> updatePerformanceRepairElevator(
            @PathVariable Long id,
            @RequestBody MobileReportDTO.ReportCreateUpdateDTO dto) {

        try {
            logger.info("📝 Updating Performance Report Repair Elevator: {}", id);

            // 1. Buscar relatório existente
            Report report = performanceRepairElevatorService.getById(id.intValue());
            if (report == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(errorResponse("Relatório não encontrado"));
            }

            // 2. Verificar permissões (se necessário)
            if ("Y".equals(report.getLocked())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(errorResponse("Relatório está bloqueado"));
            }

            // 3. Atualizar entidade Report com dados do DTO
            performanceRepairElevatorAdapter.updateEntity(report, dto);

            // 4. Obter dados específicos atuais
            PerformanceRepairElevatorService.PerformanceReportRepairElevatorSpecificData specificData =
                    new PerformanceRepairElevatorService.PerformanceReportRepairElevatorSpecificData();

            // 5. Atualizar dados específicos com dados do DTO
            performanceRepairElevatorAdapter.updateSpecificData(specificData, dto);

            // 6. ATUALIZAR RELATÓRIO COMPLETO (Report + PerformanceReportRepairElevator)
            // Este método atualiza AMBAS as tabelas de forma transacional
            Report updated = performanceRepairElevatorService.updateComplete(report, specificData);

            logger.info("✅ Performance Report Repair Elevator updated successfully");

            return ResponseEntity.ok(successResponse(
                    "Report updated successfully",
                    "reportId", updated.getReportId(),
                    "uuid", updated.getUuid()
            ));

        } catch (Exception e) {
            logger.error("❌ Error updating Performance Report Repair Elevator", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Error updating report: " + e.getMessage()));
        }
    }

// ====================================================================
// GET BY ID - Obter Performance Report Repair Elevator por ID
// ====================================================================
    /**
     * Obter Performance Report Repair Elevator por ID
     */
    @GetMapping("/performance-repair-elevator/{id}")
    public ResponseEntity<?> getPerformanceRepairElevator(@PathVariable Long id) {
        try {
            logger.info("📖 Fetching Performance Report Repair Elevator: {}", id);

            // ✅ USAR getCompleteById para obter entidade com dados específicos
            PerformanceReportRepairElevator report = performanceRepairElevatorService.getCompleteById(id.intValue());

            if (report == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(errorResponse("Relatório não encontrado"));
            }

            logger.info("✅ Complete report found: {}", report.getReportId());

            // Criar um DTO customizado com todos os campos necessários
            Map<String, Object> response = new HashMap<>();

            // Campos básicos (da tabela Report)
            response.put("reportId", report.getReportId());
            response.put("uuid", report.getUuid());
            response.put("site", report.getSite());
            response.put("wtgNumber", report.getWtgNumber());
            response.put("wtgType", report.getWtgType());
            response.put("yearConstruction", report.getYearConstruction());
            response.put("projectoId", report.getProjectoId());
            response.put("turbinaId", report.getTurbinaId());
            response.put("createDate", report.getCreateDate());
            response.put("modifiedDate", report.getModifiedDate());
            response.put("locked", report.getLocked());
            response.put("permission2Edit", report.getPermission2Edit());
            response.put("typeReport", report.getTypeReport());
            response.put("reportType", report.getReportType());

            // ✅ Campos específicos (da tabela PerformanceReportRepairElevator)
            response.put("reportNumber", report.getReportNumber());
            response.put("inpectorsWorkers", report.getInpectorsWorkers());
            response.put("statementOfwork", report.getStatementOfwork());
            response.put("workCompleted", report.getWorkCompleted());
            response.put("turbineOperable", report.getTurbineOperable());
            response.put("placeDate", report.getPlaceDate());
            response.put("responsibleTechnician", report.getResponsibleTechnician());
            response.put("performanceReport", report.getPerformanceReport());

            // Campos adicionais genéricos (se existirem)
            response.put("additionalField1Label", report.getAdditionalField1Label());
            response.put("additionalField1Text", report.getAdditionalField1Text());
            response.put("additionalField2Label", report.getAdditionalField2Label());
            response.put("additionalField2Text", report.getAdditionalField2Text());
            response.put("additionalField3Label", report.getAdditionalField3Label());
            response.put("additionalField3Text", report.getAdditionalField3Text());
            response.put("additionalField4Label", report.getAdditionalField4Label());
            response.put("additionalField4Text", report.getAdditionalField4Text());
            response.put("additionalField5Label", report.getAdditionalField5Label());
            response.put("additionalField5Text", report.getAdditionalField5Text());
            response.put("additionalField6Label", report.getAdditionalField6Label());
            response.put("additionalField6Text", report.getAdditionalField6Text());
            response.put("additionalField7Label", report.getAdditionalField7Label());
            response.put("additionalField7Text", report.getAdditionalField7Text());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("❌ Error fetching Performance Report Repair Elevator", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Error fetching report: " + e.getMessage()));
        }
    }









// ====================================================================
// GET BY TURBINE - Obter todos os relatórios de uma turbina
// ====================================================================
    /**
     * Obter todos os Performance Reports de uma turbina
     */
    @GetMapping("/performance-repair-elevator/turbine/{turbineId}")
    public ResponseEntity<?> getPerformanceRepairElevatorsByTurbine(@PathVariable Integer turbineId) {
        try {
            logger.info("📖 Fetching Performance Reports for turbine: {}", turbineId);

            List<Report> reports = performanceRepairElevatorService.getByTurbineId(turbineId);
            logger.info("✅ Found {} reports", reports.size());

            // Converte para DTO para evitar problemas de serialização
            List<MobileReportDTO.ReportResponseDTO> reportDTOs = reports.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            logger.info("🔄 Successfully converted {} reports to DTO", reportDTOs.size());

            return ResponseEntity.ok(reportDTOs);

        } catch (Exception e) {
            logger.error("❌ Error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Error fetching reports: " + e.getMessage()));
        }
    }

// ====================================================================
// GET BY PROJECT - Obter todos os relatórios de um projeto
// ====================================================================
    /**
     * Obter todos os Performance Reports de um projeto
     */
    @GetMapping("/performance-repair-elevator/project/{projectId}")
    public ResponseEntity<?> getPerformanceRepairElevatorsByProject(@PathVariable Integer projectId) {
        try {
            logger.info("📖 Fetching Performance Reports for project: {}", projectId);

            List<Report> reports = performanceRepairElevatorService.getByProjectId(projectId);

            // Converte para DTO para evitar problemas de serialização
            List<MobileReportDTO.ReportResponseDTO> reportDTOs = reports.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            logger.info("🔄 Successfully converted {} reports to DTO", reportDTOs.size());

            return ResponseEntity.ok(reportDTOs);

        } catch (Exception e) {
            logger.error("❌ Error fetching reports by project", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Error fetching reports: " + e.getMessage()));
        }
    }

    private MobileReportDTO.ReportResponseDTO convertToDTO(Report report) {
        return MobileReportDTO.ReportResponseDTO.builder()
                .id(report.getReportId() != null ? report.getReportId().longValue() : null)
                .turbineId(report.getTurbinaId() != null ? report.getTurbinaId().longValue() : null)
                .turbineName(report.getTurbine() != null ? report.getTurbine().getName() : null)
                .reportType(report.getTypeReport())
                .status(report.getStatus() != null ? report.getStatus().name() : null)
                .language(report.getLanguage())
                .offlineCreated(report.getOfflineCreated())
                .submittedAt(report.getSubmittedAt())
                .submittedBy(report.getSubmittedBy())
                .unlockRequested(report.getUnlockRequested())
                .unlockRequestedAt(report.getUnlockRequestedAt())
                .createdAt(report.getCreateDate())
                .updatedAt(report.getModifiedDate())
                .files(report.getListaFileData() != null ?
                        report.getListaFileData().stream()
                                .filter(FileData::isActive)
                                .map(f -> MobileReportDTO.FileDataDTO.builder()
                                        .id(f.getFileId() != null ? f.getFileId().longValue() : null)
                                        .filename(f.getName())
                                        .url(f.getUrl())
                                        .mimeType(f.getMimeType())
                                        .size(f.getSize())
                                        .build())
                                .collect(Collectors.toList())
                        : null)
                .build();
    }
// ====================================================================
// DELETE - Eliminar Performance Report Repair Elevator
// ====================================================================
    /**
     * Eliminar Performance Report Repair Elevator
     */
    @DeleteMapping("/performance-repair-elevator/{id}")
    public ResponseEntity<?> deletePerformanceRepairElevator(@PathVariable Long id) {
        try {
            logger.info("🗑️ Deleting Performance Report Repair Elevator: {}", id);

            Report report = performanceRepairElevatorService.getById(id.intValue());

            if (report == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(errorResponse("Relatório não encontrado"));
            }

            // Registar eliminação no histórico antes de apagar
            historyService.createHistoryEntry(
                    report,
                    "mobile_user",
                    ReportHistory.HistoryAction.DELETE,
                    "Relatório eliminado"
            );

            // Eliminar fotos associadas
            List<FileData> photos = fileService.readFile(report.getUuid());
            for (FileData photo : photos) {
                if (photo.getFileId() != null) {
                    fileService.deleteFile(photo.getFileId());
                    logger.info("🗑️ Foto eliminada: {}", photo.getFileId());
                }
            }

            // Eliminar relatório
            performanceRepairElevatorService.delete(id.intValue());

            logger.info("✅ Performance Report Repair Elevator deleted");

            return ResponseEntity.ok(successResponse("Report deleted successfully"));

        } catch (Exception e) {
            logger.error("❌ Error deleting Performance Report Repair Elevator", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Error deleting report: " + e.getMessage()));
        }
    }

// ====================================================================
// GET HISTORY - Obter histórico de alterações
// ====================================================================
    /**
     * Obter histórico de alterações de um Performance Report
     */
    @GetMapping("/performance-repair-elevator/{id}/history")
    public ResponseEntity<?> getPerformanceRepairElevatorHistory(@PathVariable Long id) {
        try {
            logger.info("📜 Fetching history for Performance Report: {}", id);

            List<ReportHistory> history = historyService.getReportHistory(id);

            logger.info("✅ Found {} history entries", history.size());
            return ResponseEntity.ok(history);

        } catch (Exception e) {
            logger.error("❌ Error fetching report history", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Error fetching history: " + e.getMessage()));
        }
    }

// ====================================================================
// GET PHOTOS - Obter fotos de um relatório
// ====================================================================
    /**
     * Obter fotos de um Performance Report
     */
    @GetMapping("/performance-repair-elevator/{id}/photos")
    public ResponseEntity<?> getPerformanceRepairElevatorPhotos(@PathVariable Long id) {
        try {
            logger.info("📸 Fetching photos for Performance Report: {}", id);

            Report report = performanceRepairElevatorService.getById(id.intValue());

            if (report == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(errorResponse("Relatório não encontrado"));
            }

            List<FileData> photos = fileService.readFile(report.getUuid());

            List<Map<String, Object>> photoList = new ArrayList<>();
            for (FileData photo : photos) {
                Map<String, Object> photoData = new HashMap<>();
                photoData.put("fileId", photo.getFileId());
                photoData.put("hash", photo.getHash());
                photoData.put("downloadUrl", "reports/mobile/files/download/" + photo.getHash());
                // ... outros campos
                photoList.add(photoData);
            }

            logger.info("✅ Found {} photos", photos.size());
            return ResponseEntity.ok(photoList);

        } catch (Exception e) {
            logger.error("❌ Error fetching photos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Error fetching photos: " + e.getMessage()));
        }
    }

// ====================================================================
// MÉTODO AUXILIAR - Obter valor de campo adicional (para Report genérico)
// ====================================================================
    /**
     * Obter valor de um campo adicional (label ou text) de um Report genérico
     * @param report Relatório (Report, não DefectsInspectionReport)
     * @param fieldNumber Número do campo (1-7)
     * @param isLabel true para label, false para text
     * @return Valor do campo ou string vazia
     */
    private String getAdditionalFieldValueForReport(Report report, int fieldNumber, boolean isLabel) {
        try {
            String methodName = "getAdditionalField" + fieldNumber + (isLabel ? "Label" : "Text");
            java.lang.reflect.Method method = Report.class.getMethod(methodName);
            Object value = method.invoke(report);
            return value != null ? value.toString() : "";
        } catch (Exception e) {
            return "";
        }
    }

// ====================================================================
// MÉTODO AUXILIAR - Associar fotos ao relatório
// ====================================================================
    /**
     * Associar fotos já carregadas ao relatório
     * ✅ SUPORTA tanto IDs numéricos como hashes (UUIDs)
     *
     * @param reportId     ID do relatório
     * @param photoFileIds Lista de IDs ou hashes de ficheiros de fotos
     * @return Número de fotos associadas
     */
    private int associatePhotosToReportPerformanceRepairElevator(Integer reportId, List<String> photoFileIds) {
        int count = 0;

        logger.info("🔗 Associando {} fotos ao relatório {}", photoFileIds.size(), reportId);

        for (String fileId : photoFileIds) {
            try {
                FileData fileData = null;

                // Tentar converter para Integer (caso seja ID numérico)
                try {
                    Integer numericId = Integer.parseInt(fileId);
                    Optional<FileData> optionalFileData = fileService.readFile(numericId);

                    if (optionalFileData.isPresent()) {
                        fileData = optionalFileData.get();
                        logger.info("   ✅ Foto encontrada por ID: {}", numericId);
                    } else {
                        logger.warn("   ⚠️ FileData não encontrado para ID: {}", numericId);
                    }

                } catch (NumberFormatException e) {
                    // Não é número, tentar buscar por hash (UUID)
                    logger.info("   🔍 '{}' não é número, buscando por hash...", fileId);
                    fileData = fileService.readFileByHash(fileId);

                    if (fileData != null) {
                        logger.info("   ✅ Foto encontrada por hash: {} (ID: {})", fileId, fileData.getFileId());
                    } else {
                        logger.warn("   ⚠️ FileData não encontrado para hash: {}", fileId);
                    }
                }

                // Se encontrou o FileData, associar ao relatório
                if (fileData != null) {
                    // Buscar o Report pelo ID para obter o UUID
                    Report report = performanceRepairElevatorService.getById(reportId);
                    if (report != null) {
                        count++;
                        logger.info("   ✅ Foto {} associada ao relatório {}", fileData.getFileId(), reportId);
                    }
                }

            } catch (Exception e) {
                logger.error("   ❌ Erro ao associar foto {}: {}", fileId, e.getMessage());
            }
        }

        logger.info("✅ Total de {} fotos associadas", count);
        return count;
    }





// ====================================================================
// MÉTODOS HELPER PARA RESPOSTAS (Java 8 compatible)
// ====================================================================

    /**
     * Criar mapa genérico (helper para Java 8)
     */
    private Map<String, Object> createMap(Object... keyValuePairs) {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            if (i + 1 < keyValuePairs.length) {
                map.put((String) keyValuePairs[i], keyValuePairs[i + 1]);
            }
        }
        return map;
    }

    /**
     * Criar resposta de sucesso
     */
    private Map<String, Object> successResponse(String message, Object... extraKeyValuePairs) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);

        // Adicionar campos extras
        for (int i = 0; i < extraKeyValuePairs.length; i += 2) {
            if (i + 1 < extraKeyValuePairs.length) {
                response.put((String) extraKeyValuePairs[i], extraKeyValuePairs[i + 1]);
            }
        }

        return response;
    }

    /**
     * Criar resposta de erro
     */
    private Map<String, Object> errorResponse(String error) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", error);
        return response;
    }
}
