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
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    /**
     * Apenas pequenos ajustes de tipo são necessários no Controller
     * O método createPerformanceRepairElevator já está quase correto!
     */

    @PostMapping("/performance-repair-elevator")
    public ResponseEntity<?> createPerformanceRepairElevator(
            @RequestBody MobileReportDTO.ReportCreateUpdateDTO dto) {

        try {
            logger.info("═══════════════════════════════════════════════════════");
            logger.info("📋 CREATE called - START");
            logger.info("📋 Thread: {}", Thread.currentThread().getName());
            logger.info("═══════════════════════════════════════════════════════");
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

            // 2. ✅ O adapter agora retorna PerformanceReportRepairElevator diretamente!
            PerformanceReportRepairElevator report = performanceRepairElevatorAdapter.toEntity(dto, turbine);
            report.setReportType(Report.ReportStatus.DRAFT.ordinal());

            // 3. Extrair dados específicos do DTO
            PerformanceRepairElevatorService.PerformanceReportRepairElevatorSpecificData specificData =
                    performanceRepairElevatorAdapter.toSpecificData(dto);

            // 4. ✅ CRIAR RELATÓRIO (agora só cria UMA vez!)
            Report saved = performanceRepairElevatorService.createComplete(report, specificData);

            // 5. Associar fotos (se existirem) - este código já está correto
            List<Long> photoFileIds = dto.getPhotoIds();
            if (photoFileIds != null && !photoFileIds.isEmpty()) {
                logger.info("📸 Associating {} photos to report", photoFileIds.size());
                // ... código de associação de fotos existente ...
            }

            logger.info("✅ Performance Report Repair Elevator created successfully: {}", saved.getReportId());
            logger.info("═══════════════════════════════════════════════════════");
            logger.info("✅ CREATE completed - Report ID: {}", saved.getReportId());
            logger.info("═══════════════════════════════════════════════════════");

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

    @PutMapping("/performance-repair-elevator/{id}")
    public ResponseEntity<?> updatePerformanceRepairElevator(
            @PathVariable Long id,
            @RequestBody MobileReportDTO.ReportCreateUpdateDTO dto,
            Authentication authentication) {

        try {
            // ✅ OBTER USERNAME DO UTILIZADOR LOGADO
            String username = authentication.getName();

            logger.info("═══════════════════════════════════════════════════════");
            logger.info("📝 UPDATE called - START");
            logger.info("📝 Report ID: {}", id);
            logger.info("📝 User: {}", username);
            logger.info("═══════════════════════════════════════════════════════");

            // 1. ✅ Buscar relatório específico existente
            PerformanceReportRepairElevator oldReport = performanceRepairElevatorService.getCompleteById(id.intValue());
            if (oldReport == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(errorResponse("Relatório não encontrado"));
            }

            // ✅ GUARDAR VALORES ANTIGOS ANTES DE ATUALIZAR
            String oldSite = oldReport.getSite();
            String oldWtgNumber = oldReport.getWtgNumber();
            String oldWtgType = oldReport.getWtgType();
            String oldYearConstruction = oldReport.getYearConstruction();
            String oldReportNumber = oldReport.getReportNumber();
            String oldInspectorsWorkers = oldReport.getInpectorsWorkers();
            String oldStatementOfWork = oldReport.getStatementOfwork();
            String oldWorkCompleted = oldReport.getWorkCompleted();
            String oldTurbineOperable = oldReport.getTurbineOperable();
            String oldPlaceDate = oldReport.getPlaceDate();
            String oldResponsibleTechnician = oldReport.getResponsibleTechnician();
            String oldPerformanceReport = oldReport.getPerformanceReport();

            // Guardar campos adicionais antigos
            Map<String, String> oldAdditionalFields = new HashMap<>();
            for (int i = 1; i <= 3; i++) {
                oldAdditionalFields.put("additionalField" + i + "Label", getAdditionalFieldValue(oldReport, i, true));
                oldAdditionalFields.put("additionalField" + i + "Text", getAdditionalFieldValue(oldReport, i, false));
            }

            // ✅ GUARDAR FOTOS ANTIGAS (excluindo as recém-carregadas)
            List<FileData> allPhotosFileData = fileService.readFile(oldReport.getUuid());
            LocalDateTime twoSecondsAgo = LocalDateTime.now().minusSeconds(2);

            List<Long> oldPhotoIds = allPhotosFileData.stream()
                    .filter(fd -> fd != null && fd.getFileId() != null)
                    .filter(FileData::isActive)
                    .filter(fd -> fd.getCreateDate() != null && fd.getCreateDate().isBefore(twoSecondsAgo))
                    .map(FileData::getFileId)
                    .map(Integer::longValue)
                    .collect(Collectors.toList());

            logger.info("📸 FOTOS ANTIGAS (>2s): {} fotos encontradas", oldPhotoIds.size());
            oldPhotoIds.forEach(photoId -> logger.info("   - Foto antiga ID: {}", photoId));

            // 2. ✅ Atualizar entidade
            performanceRepairElevatorAdapter.updateEntity(oldReport, dto);

            // 3. Obter dados específicos do DTO
            PerformanceRepairElevatorService.PerformanceReportRepairElevatorSpecificData specificData =
                    new PerformanceRepairElevatorService.PerformanceReportRepairElevatorSpecificData();

            // 4. Atualizar dados específicos
            performanceRepairElevatorAdapter.updateSpecificData(specificData, dto);

            // 5. ✅ ATUALIZAR RELATÓRIO COMPLETO
            Report updated = performanceRepairElevatorService.updateComplete(oldReport, specificData);

            // ✅ OBTER VALORES NOVOS APÓS ATUALIZAÇÃO
            PerformanceReportRepairElevator newReport = performanceRepairElevatorService.getCompleteById(id.intValue());

            // ✅ 6. DETECTAR E REGISTAR ALTERAÇÕES NOS CAMPOS
            logger.info("🔍 Detecting field changes...");
            List<FieldChange> fieldChanges = new ArrayList<>();

            // Comparar campos básicos
            if (!Objects.equals(oldSite, newReport.getSite())) {
                fieldChanges.add(new FieldChange("site", oldSite, newReport.getSite()));
            }
            if (!Objects.equals(oldWtgNumber, newReport.getWtgNumber())) {
                fieldChanges.add(new FieldChange("wtgNumber", oldWtgNumber, newReport.getWtgNumber()));
            }
            if (!Objects.equals(oldWtgType, newReport.getWtgType())) {
                fieldChanges.add(new FieldChange("wtgType", oldWtgType, newReport.getWtgType()));
            }
            if (!Objects.equals(oldYearConstruction, newReport.getYearConstruction())) {
                fieldChanges.add(new FieldChange("yearConstruction", oldYearConstruction, newReport.getYearConstruction()));
            }

            // Comparar campos específicos
            if (!Objects.equals(oldReportNumber, newReport.getReportNumber())) {
                fieldChanges.add(new FieldChange("reportNumber", oldReportNumber, newReport.getReportNumber()));
            }
            if (!Objects.equals(oldInspectorsWorkers, newReport.getInpectorsWorkers())) {
                fieldChanges.add(new FieldChange("inspectorsWorkers", oldInspectorsWorkers, newReport.getInpectorsWorkers()));
            }
            if (!Objects.equals(oldStatementOfWork, newReport.getStatementOfwork())) {
                fieldChanges.add(new FieldChange("statementOfWork", oldStatementOfWork, newReport.getStatementOfwork()));
            }
            if (!Objects.equals(oldWorkCompleted, newReport.getWorkCompleted())) {
                fieldChanges.add(new FieldChange("workCompleted", oldWorkCompleted, newReport.getWorkCompleted()));
            }
            if (!Objects.equals(oldTurbineOperable, newReport.getTurbineOperable())) {
                fieldChanges.add(new FieldChange("turbineOperable", oldTurbineOperable, newReport.getTurbineOperable()));
            }
            if (!Objects.equals(oldPlaceDate, newReport.getPlaceDate())) {
                fieldChanges.add(new FieldChange("placeDate", oldPlaceDate, newReport.getPlaceDate()));
            }
            if (!Objects.equals(oldResponsibleTechnician, newReport.getResponsibleTechnician())) {
                fieldChanges.add(new FieldChange("responsibleTechnician", oldResponsibleTechnician, newReport.getResponsibleTechnician()));
            }
            if (!Objects.equals(oldPerformanceReport, newReport.getPerformanceReport())) {
                fieldChanges.add(new FieldChange("performanceReport", oldPerformanceReport, newReport.getPerformanceReport()));
            }

            // Comparar campos adicionais
            for (int i = 1; i <= 3; i++) {
                String labelKey = "additionalField" + i + "Label";
                String textKey = "additionalField" + i + "Text";

                String oldLabel = oldAdditionalFields.get(labelKey);
                String newLabel = getAdditionalFieldValue(newReport, i, true);
                if (!Objects.equals(oldLabel, newLabel)) {
                    fieldChanges.add(new FieldChange(labelKey, oldLabel, newLabel));
                }

                String oldText = oldAdditionalFields.get(textKey);
                String newText = getAdditionalFieldValue(newReport, i, false);
                if (!Objects.equals(oldText, newText)) {
                    fieldChanges.add(new FieldChange(textKey, oldText, newText));
                }
            }

            logger.info("📊 Detected {} field changes", fieldChanges.size());

            // ✅ REGISTAR ALTERAÇÕES NO HISTÓRICO
            for (FieldChange fieldChange : fieldChanges) {
                historyService.createFieldChangeEntry(
                        updated,
                        username,
                        fieldChange.getFieldName(),
                        fieldChange.getOldValue(),
                        fieldChange.getNewValue()
                );
                logger.info("✏️ Field '{}' changed: '{}' -> '{}'",
                        fieldChange.getFieldName(),
                        fieldChange.getOldValue(),
                        fieldChange.getNewValue());
            }

            // ✅ 7. PROCESSAR ALTERAÇÕES DE FOTOS
            logger.info("🔍 ========== COMPARAÇÃO DE FOTOS ==========");

            if (dto.getPhotoIds() != null && !dto.getPhotoIds().isEmpty()) {
                logger.info("📸 DTO photoIds: {}", dto.getPhotoIds());

                // Converter IDs do DTO para Long
                List<Long> newPhotoIds = dto.getPhotoIds();

                logger.info("📸 FOTOS NOVAS: {} fotos", newPhotoIds.size());
                newPhotoIds.forEach(photoId -> logger.info("   - Foto nova ID: {}", photoId));

                logger.info("🔍 Comparando fotos antigas vs novas...");
                logger.info("   Antigas (>2s): {}", oldPhotoIds);
                logger.info("   Novas: {}", newPhotoIds);

                // Se não havia fotos antigas, são as PRIMEIRAS fotos → NÃO registar no histórico
                if (oldPhotoIds.isEmpty() && !newPhotoIds.isEmpty()) {
                    logger.info("ℹ️ Primeiras fotos do relatório - não registar no histórico");
                } else {
                    // Comparar fotos APENAS se já existiam fotos antigas
                    List<FieldChange> photoChanges = comparisonService.comparePhotos(oldPhotoIds, newPhotoIds);

                    logger.info("📊 Resultado da comparação: {} alterações de fotos", photoChanges.size());

                    // Registar e processar remoções
                    for (FieldChange photoChange : photoChanges) {
                        historyService.createFieldChangeEntry(
                                updated,
                                username,
                                photoChange.getFieldName(),
                                photoChange.getOldValue(),
                                photoChange.getNewValue()
                        );
                        logger.info("📸 {} | OLD: '{}' | NEW: '{}'",
                                photoChange.getFieldName(),
                                photoChange.getOldValue(),
                                photoChange.getNewValue());

                        // Se foi removida, apagar fisicamente
                        if ("photo_removed".equals(photoChange.getFieldName())) {
                            try {
                                String oldValue = photoChange.getOldValue();
                                if (oldValue != null && oldValue.startsWith("Foto ID: ")) {
                                    String photoIdStr = oldValue.replace("Foto ID: ", "").split("\\|")[0].trim();
                                    Integer photoId = Integer.parseInt(photoIdStr);

                                    logger.info("🗑️ Apagando foto removida: ID {}", photoId);
                                    fileService.deleteFile(photoId);
                                    logger.info("✅ Foto {} marcada como removida", photoId);
                                }
                            } catch (Exception e) {
                                logger.error("❌ Erro ao apagar foto: {}", photoChange.getOldValue(), e);
                            }
                        }
                    }
                }
            } else {
                logger.info("ℹ️ Nenhum photoId fornecido no DTO");
            }

            logger.info("✅ Performance Report Repair Elevator updated successfully");
            logger.info("═══════════════════════════════════════════════════════");
            logger.info("✅ UPDATE completed - Report ID: {} - {} field changes", id, fieldChanges.size());
            logger.info("═══════════════════════════════════════════════════════");

            return ResponseEntity.ok(successResponse(
                    "Report updated successfully",
                    "reportId", updated.getReportId(),
                    "uuid", updated.getUuid(),
                    "changes", fieldChanges.size()
            ));

        } catch (Exception e) {
            logger.error("❌ Error updating Performance Report Repair Elevator", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Error updating report: " + e.getMessage()));
        }
    }

    /**
     * ✅ MÉTODO AUXILIAR: Obter valor de campo adicional
     */
    private String getAdditionalFieldValue(PerformanceReportRepairElevator report, int fieldNumber, boolean isLabel) {
        switch (fieldNumber) {
            case 1:
                return isLabel ? report.getAdditionalField1Label() : report.getAdditionalField1Text();
            case 2:
                return isLabel ? report.getAdditionalField2Label() : report.getAdditionalField2Text();
            case 3:
                return isLabel ? report.getAdditionalField3Label() : report.getAdditionalField3Text();
            default:
                return null;
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
    /**
     * GET /api/reports/mobile/performance-repair-elevator/{id}/history
     * Obter histórico de alterações de um Performance Report Repair Elevator
     */
    @GetMapping("/performance-repair-elevator/{id}/history")
    public ResponseEntity<?> getPerformanceRepairElevatorHistory(@PathVariable Long id) {
        try {
            logger.info("📜 Getting history for Performance Report: {}", id);

            List<ReportHistory> history = historyService.getReportHistory(id);

            // ✅ CONVERTER para DTO (igual ao Defect Inspection)
            List<MobileReportDTO.ReportHistoryDTO> historyDTOs = history.stream()
                    .map(h -> {
                        String oldValue = h.getOldValue();
                        String newValue = h.getNewValue();

                        // ✅ Se é alteração de foto E ainda não tem hash, adicionar
                        if ("photo_added".equals(h.getFieldName()) || "photo_removed".equals(h.getFieldName())) {
                            boolean oldValueHasHash = oldValue != null && oldValue.contains("|");
                            boolean newValueHasHash = newValue != null && newValue.contains("|");

                            if (!oldValueHasHash && oldValue != null && oldValue.startsWith("Foto ID: ")) {
                                oldValue = addHashToPhotoValue(oldValue);
                            }

                            if (!newValueHasHash && newValue != null && newValue.startsWith("Foto ID: ")) {
                                newValue = addHashToPhotoValue(newValue);
                            }
                        }

                        return MobileReportDTO.ReportHistoryDTO.builder()
                                .id(h.getId())
                                .changedBy(h.getChangedBy())
                                .changedAt(h.getChangedAt())
                                .action(h.getAction().name())
                                .fieldName(h.getFieldName())
                                .oldValue(oldValue)
                                .newValue(newValue)
                                .description(h.getDescription())
                                .build();
                    })
                    .collect(Collectors.toList());

            logger.info("✅ Returning {} history entries", historyDTOs.size());
            return ResponseEntity.ok(historyDTOs);

        } catch (Exception e) {
            logger.error("❌ Error getting history for Performance Report {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao obter histórico: " + e.getMessage());
        }
    }

    /**
     * ✅ MÉTODO AUXILIAR: Adicionar hash às fotos
     * (Copiar do DefectInspectionReportsController)
     */
    private String addHashToPhotoValue(String photoValue) {
        try {
            // Extrair ID da foto: "Foto ID: 123" -> 123
            String idStr = photoValue.replace("Foto ID: ", "").trim();
            Integer photoId = Integer.parseInt(idStr);

            // Buscar foto no banco de dados
            Optional<FileData> photo = fileService.readFile(photoId);
            if (photo.isPresent() && photo.get().getHash() != null) {
                // Adicionar hash: "Foto ID: 123|abc123..."
                return photoValue + "|" + photo.get().getHash();
            }
        } catch (Exception e) {
            logger.warn("⚠️ Não foi possível adicionar hash à foto: {}", photoValue, e);
        }
        return photoValue;
    }

// ====================================================================
// GET PHOTOS - Obter fotos de um relatório
// ====================================================================
    /**
     * Obter fotos de um Performance Report Repair Elevator
     *
     * Endpoint: GET /api/reports/mobile/performance-repair-elevator/{id}/photos
     *
     * @param id ID do relatório
     * @return Lista de fotos com URLs para download
     */
    @GetMapping("/performance-repair-elevator/{id}/photos")
    public ResponseEntity<?> getPerformanceRepairElevatorPhotos(@PathVariable Long id) {
        try {
            logger.info("📸 Fetching photos for Performance Report: {}", id);

            // 1. Buscar relatório
            Report report = performanceRepairElevatorService.getById(id.intValue());

            if (report == null) {
                logger.error("❌ Report not found with ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(errorResponse("Report not found with ID: " + id));
            }

            // 2. Buscar fotos ativas usando o UUID do relatório
            List<FileData> photos = fileService.readActiveFilesByUuid(report.getUuid());

            if (photos == null || photos.isEmpty()) {
                logger.info("ℹ️ No active photos found for report {}", id);
                return ResponseEntity.ok(new ArrayList<>());
            }

            // 3. Converter para DTOs com URLs de download
            List<Map<String, Object>> photoList = new ArrayList<>();

            for (FileData photo : photos) {
                Map<String, Object> photoData = new HashMap<>();
                photoData.put("fileId", photo.getFileId());
                photoData.put("hash", photo.getHash());
                photoData.put("name", photo.getName());
                photoData.put("description", photo.getDescription());
                photoData.put("mimeType", photo.getMimeType());
                photoData.put("size", photo.getSize());
                photoData.put("createDate", photo.getCreateDate());

                // URL para download da foto
                // O frontend usa este caminho para fazer download via /api/reports/mobile/files/download/{hash}
                photoData.put("downloadUrl", "/api/reports/mobile/files/download/" + photo.getHash());

                photoList.add(photoData);
            }

            logger.info("✅ Returning {} active photos for report {}", photoList.size(), id);
            return ResponseEntity.ok(photoList);

        } catch (Exception e) {
            logger.error("❌ Error getting photos for report {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Error getting photos: " + e.getMessage()));
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
