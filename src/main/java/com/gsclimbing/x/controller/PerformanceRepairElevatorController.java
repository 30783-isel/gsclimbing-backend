package com.gsclimbing.x.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsclimbing.database.entity.FileData;
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

// ====================================================================
// CREATE - Criar novo Performance Report Repair Elevator
// ====================================================================
    /**
     * Criar Performance Report Repair Elevator
     */
    @PostMapping("/performance-repair-elevator")
    public ResponseEntity<?> createPerformanceRepairElevator(
            @RequestBody MobileReportDTO.ReportCreateUpdateDTO dto) {

        try {
            logger.info("📝 Creating Performance Report Repair Elevator from mobile");

            // Validar campos obrigatórios
            ObjectMapper mapper = new ObjectMapper();
            JsonNode reportData = mapper.readTree(dto.getReportData());

            String site = reportData.has("site") ? reportData.get("site").asText() : null;
            String wtgNumber = reportData.has("wtgNumber") ? reportData.get("wtgNumber").asText() : null;

            if (site == null || site.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Site is required");
            }
            if (wtgNumber == null || wtgNumber.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("WTG Number is required");
            }

            // Buscar turbina
            Turbine turbine = turbineService.getTurbine(dto.getTurbineId().intValue());
            if (turbine == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Turbine not found with ID: " + dto.getTurbineId());
            }

            // Validar se já existe relatório para esta turbina
            List<Report> existingReports = performanceRepairElevatorService.getByTurbineId(turbine.getId());
            if (!existingReports.isEmpty()) {
                Map<String, Object> errorResponse = errorResponse("Já existe um relatório para esta turbina. Elimine o relatório existente antes de criar um novo.");
                errorResponse.put("existingReportId", existingReports.get(0).getReportId());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }

            // Criar relatório
            Report report = performanceRepairElevatorAdapter.toEntity(dto, turbine);
            report.setReportType(Report.ReportStatus.DRAFT.ordinal());
            Report saved = performanceRepairElevatorService.create(report);

            // Extrair userId do reportData ou usar padrão
            String userId = reportData.has("userId") ? reportData.get("userId").asText() : "mobile_user";

            // Registar criação no histórico
            historyService.logCreate(saved, userId);

            // Associar fotos
            List<Long> photoFileIds = dto.getPhotoIds();
            if (photoFileIds != null && !photoFileIds.isEmpty()) {
                logger.info("📸 Associating {} photos to report", photoFileIds.size());
                List<String> photoIdsAsString = new ArrayList<>();
                for (Long photoId : photoFileIds) {
                    photoIdsAsString.add(String.valueOf(photoId));
                }
                int photosAssociated = associatePhotosToReportPerformanceRepairElevator(saved.getReportId(), photoIdsAsString);
                logger.info("✅ {} photos associated", photosAssociated);
            }

            logger.info("✅ Performance Report Repair Elevator created: {}", saved.getReportId());

            return ResponseEntity.ok(successResponse("Report created successfully",
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

            // 2. Parse do reportData
            ObjectMapper mapper = new ObjectMapper();
            JsonNode reportData = mapper.readTree(dto.getReportData());

            // 3. Buscar fotos antigas ANTES de qualquer alteração (com delay de 2s)
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            List<FileData> oldPhotosFileData = fileService.readFile(report.getUuid());
            List<Long> oldPhotoIds = new ArrayList<>();
            for (FileData fd : oldPhotosFileData) {
                if (fd != null && fd.getFileId() != null) {
                    oldPhotoIds.add(fd.getFileId().longValue());
                }
            }

            logger.info("📸 FOTOS ANTIGAS (>2s): {} fotos encontradas", oldPhotoIds.size());
            for (Long photoId : oldPhotoIds) {
                logger.info("   - Foto antiga ID: {}", photoId);
            }

            // 4. Guardar estado antigo dos campos para comparação
            String oldSite = report.getSite();
            String oldWtgNumber = report.getWtgNumber();
            String oldWtgType = report.getWtgType();
            String oldYearConstruction = report.getYearConstruction();

            // Guardar campos adicionais antigos
            Map<String, String> oldAdditionalFields = new HashMap<>();
            for (int i = 1; i <= 7; i++) {
                String labelKey = "additionalField" + i + "Label";
                String textKey = "additionalField" + i + "Text";
                oldAdditionalFields.put(labelKey, getAdditionalFieldValueForReport(report, i, true));
                oldAdditionalFields.put(textKey, getAdditionalFieldValueForReport(report, i, false));
            }

            // 5. Atualizar relatório
            performanceRepairElevatorAdapter.updateEntity(report, dto);
            Report updated = performanceRepairElevatorService.update(report);

            // 6. Buscar fotos novas do DTO
            List<Long> photoFileIdsFromDto = dto.getPhotoIds();
            List<Long> newPhotoIds = new ArrayList<>();

            if (photoFileIdsFromDto != null && !photoFileIdsFromDto.isEmpty()) {
                logger.info("📥 Processando {} photo IDs do DTO...", photoFileIdsFromDto.size());

                for (Long photoIdLong : photoFileIdsFromDto) {
                    String photoId = String.valueOf(photoIdLong);
                    try {
                        Long numericId = Long.parseLong(photoId);
                        newPhotoIds.add(numericId);
                        logger.info("   ✅ Photo ID convertido: {}", numericId);
                    } catch (NumberFormatException e) {
                        logger.info("   🔍 '{}' não é número, buscando por hash...", photoId);
                        FileData fileData = fileService.readFileByHash(photoId);
                        if (fileData != null && fileData.getFileId() != null) {
                            newPhotoIds.add(fileData.getFileId().longValue());
                            logger.info("   ✅ Encontrado FileData para hash {}: ID {}",
                                    photoId, fileData.getFileId());
                        } else {
                            logger.error("   ❌ FileData não encontrado para: {}", photoId);
                        }
                    }
                }
            }

            logger.info("📸 FOTOS NOVAS: {} fotos", newPhotoIds.size());
            for (Long photoId : newPhotoIds) {
                logger.info("   - Foto nova ID: {}", photoId);
            }

            // 7. Comparar fotos para histórico (se não for primeira vez)
            List<FieldChange> photoChanges = new ArrayList<>();

            if (oldPhotoIds.isEmpty() && !newPhotoIds.isEmpty()) {
                logger.info("ℹ️ Primeiras fotos do relatório - não registar no histórico");
            } else {
                photoChanges = comparisonService.comparePhotos(oldPhotoIds, newPhotoIds);
                logger.info("📊 Resultado da comparação: {} alterações de fotos", photoChanges.size());
            }

            // 8. Comparar campos de texto
            List<FieldChange> fieldChanges = new ArrayList<>();

            if (!Objects.equals(oldSite, updated.getSite())) {
                fieldChanges.add(new FieldChange("site", oldSite, updated.getSite()));
                logger.info("📝 Campo alterado: site | '{}' -> '{}'", oldSite, updated.getSite());
            }
            if (!Objects.equals(oldWtgNumber, updated.getWtgNumber())) {
                fieldChanges.add(new FieldChange("wtgNumber", oldWtgNumber, updated.getWtgNumber()));
                logger.info("📝 Campo alterado: wtgNumber | '{}' -> '{}'", oldWtgNumber, updated.getWtgNumber());
            }
            if (!Objects.equals(oldWtgType, updated.getWtgType())) {
                fieldChanges.add(new FieldChange("wtgType", oldWtgType, updated.getWtgType()));
                logger.info("📝 Campo alterado: wtgType | '{}' -> '{}'", oldWtgType, updated.getWtgType());
            }
            if (!Objects.equals(oldYearConstruction, updated.getYearConstruction())) {
                fieldChanges.add(new FieldChange("yearConstruction", oldYearConstruction, updated.getYearConstruction()));
                logger.info("📝 Campo alterado: yearConstruction | '{}' -> '{}'",
                        oldYearConstruction, updated.getYearConstruction());
            }

            // Verificar alterações nos campos adicionais
            for (int i = 1; i <= 7; i++) {
                String labelKey = "additionalField" + i + "Label";
                String textKey = "additionalField" + i + "Text";

                String oldLabel = oldAdditionalFields.get(labelKey);
                String oldText = oldAdditionalFields.get(textKey);

                String newLabel = getAdditionalFieldValueForReport(updated, i, true);
                String newText = getAdditionalFieldValueForReport(updated, i, false);

                if (!Objects.equals(oldLabel, newLabel)) {
                    fieldChanges.add(new FieldChange(labelKey, oldLabel, newLabel));
                    logger.info("📝 Campo alterado: {} | '{}' -> '{}'", labelKey, oldLabel, newLabel);
                }
                if (!Objects.equals(oldText, newText)) {
                    fieldChanges.add(new FieldChange(textKey, oldText, newText));
                    logger.info("📝 Campo alterado: {} | '{}' -> '{}'", textKey, oldText, newText);
                }
            }

            // 9. Combinar todas as alterações
            fieldChanges.addAll(photoChanges);

            // 10. Registar no histórico usando createFieldChangeEntry
            if (!fieldChanges.isEmpty()) {
                String userId = reportData.has("userId") ? reportData.get("userId").asText() : "mobile_user";

                for (FieldChange fieldChange : fieldChanges) {
                    historyService.createFieldChangeEntry(
                            updated,
                            userId,
                            fieldChange.getFieldName(),
                            fieldChange.getOldValue(),
                            fieldChange.getNewValue()
                    );
                }

                logger.info("📜 {} alterações registadas no histórico", fieldChanges.size());
            } else {
                logger.info("ℹ️ Nenhuma alteração detetada");
            }

            // 11. Associar novas fotos
            if (photoFileIdsFromDto != null && !photoFileIdsFromDto.isEmpty()) {
                List<String> photoIdsAsString = new ArrayList<>();
                for (Long photoId : photoFileIdsFromDto) {
                    photoIdsAsString.add(String.valueOf(photoId));
                }
                int photosAssociated = associatePhotosToReportPerformanceRepairElevator(updated.getReportId(), photoIdsAsString);
                logger.info("✅ {} photos associated", photosAssociated);
            }

            logger.info("✅ Performance Report Repair Elevator updated");

            return ResponseEntity.ok(successResponse("Report updated successfully",
                    "reportId", updated.getReportId(),
                    "uuid", updated.getUuid(),
                    "changesCount", fieldChanges.size()
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

            Report report = performanceRepairElevatorService.getById(id.intValue());

            if (report == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(errorResponse("Relatório não encontrado"));
            }

            logger.info("✅ Report found: {}", report.getReportId());
            return ResponseEntity.ok(report);

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

            logger.info("✅ Found {} photos", photos.size());
            return ResponseEntity.ok(photos);

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
