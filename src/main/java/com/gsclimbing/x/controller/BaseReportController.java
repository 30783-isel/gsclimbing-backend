package com.gsclimbing.x.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.entity.Report;
import com.gsclimbing.database.entity.Turbine;
import com.gsclimbing.database.service.FileService;
import com.gsclimbing.database.service.TurbineService;
import com.gsclimbing.x.adapter.BaseReportAdapter;
import com.gsclimbing.x.database.entity.ReportHistory;
import com.gsclimbing.x.database.service.BaseReportService;
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

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller base genérico para todos os tipos de relatórios
 * Centraliza lógica comum de CRUD e operações
 *
 * @param <T> Tipo específico do relatório (extends Report)
 */
public abstract class BaseReportController<T extends Report> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected TurbineService turbineService;

    @Autowired
    protected FileService fileService;

    @Autowired
    protected ReportHistoryService historyService;

    @Autowired
    protected ReportComparisonService comparisonService;

    protected final ObjectMapper objectMapper = new ObjectMapper();

    // ========================================
    // MÉTODOS ABSTRATOS (cada controller implementa)
    // ========================================

    /**
     * Retorna o service específico do tipo de relatório
     */
    protected abstract BaseReportService<T> getReportService();

    /**
     * Retorna o adapter específico do tipo de relatório
     */
    protected abstract BaseReportAdapter<T> getReportAdapter();

    /**
     * Retorna o nome do endpoint (ex: "defect-inspection")
     */
    protected abstract String getEndpointName();

    /**
     * Retorna o tipo numérico do relatório (0-7)
     */
    protected abstract int getReportType();

    // ========================================
    // CREATE - Criar novo relatório
    // ========================================

    @Transactional
    public ResponseEntity<?> create(MobileReportDTO.ReportCreateUpdateDTO dto) {
        try {
            logger.info("═══════════════════════════════════════════════════════");
            logger.info("📋 CREATE {} - START", getEndpointName());
            logger.info("═══════════════════════════════════════════════════════");

            // 1. Validar campos obrigatórios
            JsonNode reportData = objectMapper.readTree(dto.getReportData());
            String site = reportData.has("site") ? reportData.get("site").asText() : null;
            String wtgNumber = reportData.has("wtgNumber") ? reportData.get("wtgNumber").asText() : null;

            if (site == null || site.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(errorResponse("Site is required"));
            }
            if (wtgNumber == null || wtgNumber.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(errorResponse("WTG Number is required"));
            }

            // 2. Validar turbina
            Turbine turbine = turbineService.getTurbine(dto.getTurbineId().intValue());
            if (turbine == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(errorResponse("Turbine not found with ID: " + dto.getTurbineId()));
            }

            // 3. Verificar se já existe relatório para esta turbina (opcional - pode comentar)
            List<T> existingReports = getReportService().getByTurbineId(turbine.getId());
            if (!existingReports.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Já existe um relatório para esta turbina. " +
                        "Elimine o relatório existente antes de criar um novo.");
                errorResponse.put("existingReportId", existingReports.get(0).getReportId());
                errorResponse.put("code", "REPORT_ALREADY_EXISTS");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }

            // 4. Converter DTO para entidade usando adapter
            T report = getReportAdapter().toEntity(dto, turbine);

            // 5. Salvar relatório usando service
            T savedReport = getReportService().create(report);

            logger.info("✅ Report created successfully with ID: {}", savedReport.getReportId());

            // 6. Associar fotos (se existirem)
            int numberPictures = 0;
            if (dto.getPhotoIds() != null && !dto.getPhotoIds().isEmpty()) {
                numberPictures = associatePhotosToReport(
                        savedReport.getReportId(),
                        dto.getPhotoIds()
                );
            }

            // 7. Criar resposta
            Map<String, Object> response = createSuccessResponse(savedReport, numberPictures);

            logger.info("═══════════════════════════════════════════════════════");
            logger.info("✅ CREATE completed - Report ID: {}", savedReport.getReportId());
            logger.info("═══════════════════════════════════════════════════════");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            logger.error("❌ Error creating report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Error creating report: " + e.getMessage()));
        }
    }

    // ========================================
    // GET BY ID - Obter relatório por ID
    // ========================================

    public ResponseEntity<?> getById(Integer reportId) {
        try {
            logger.info("🔍 Fetching {} by ID: {}", getEndpointName(), reportId);

            Optional<T> reportOpt = getReportService().getById(reportId);

            if (!reportOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(errorResponse("Report not found with ID: " + reportId));
            }

            T report = reportOpt.get();

            // Converter para DTO de resposta
            Map<String, Object> response = convertToResponseDTO(report);

            logger.info("✅ Report found");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("❌ Error fetching report {}", reportId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Error fetching report: " + e.getMessage()));
        }
    }

    // ========================================
    // GET BY TURBINE - Obter relatórios de uma turbina
    // ========================================

    public ResponseEntity<?> getByTurbine(Integer turbineId) {
        try {
            logger.info("📖 Fetching {} reports for turbine: {}", getEndpointName(), turbineId);

            List<T> reports = getReportService().getByTurbineId(turbineId);

            // Converter para DTOs
            List<Map<String, Object>> reportDTOs = reports.stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());

            logger.info("✅ Found {} reports", reportDTOs.size());
            return ResponseEntity.ok(reportDTOs);

        } catch (Exception e) {
            logger.error("❌ Error fetching reports for turbine {}", turbineId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Error fetching reports: " + e.getMessage()));
        }
    }

    // ========================================
    // GET BY PROJECT - Obter relatórios de um projeto
    // ========================================

    public ResponseEntity<?> getByProject(Integer projectId) {
        try {
            logger.info("📖 Fetching {} reports for project: {}", getEndpointName(), projectId);

            List<T> reports = getReportService().getByProjectId(projectId);

            // Converter para DTOs
            List<Map<String, Object>> reportDTOs = reports.stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());

            logger.info("✅ Found {} reports", reportDTOs.size());
            return ResponseEntity.ok(reportDTOs);

        } catch (Exception e) {
            logger.error("❌ Error fetching reports for project {}", projectId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Error fetching reports: " + e.getMessage()));
        }
    }

    // ========================================
    // UPDATE - Atualizar relatório
    // ========================================

    @Transactional
    public ResponseEntity<?> update(
            Integer reportId,
            MobileReportDTO.ReportCreateUpdateDTO dto,
            Authentication authentication) {

        try {
            String username = authentication.getName();

            logger.info("═══════════════════════════════════════════════════════");
            logger.info("🔧 UPDATE {} - START", getEndpointName());
            logger.info("📝 Report ID: {}", reportId);
            logger.info("📝 User: {}", username);
            logger.info("═══════════════════════════════════════════════════════");

            // 1. Buscar relatório existente
            Optional<T> reportOpt = getReportService().getById(reportId);
            if (!reportOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(errorResponse("Report not found"));
            }

            T oldReport = reportOpt.get();

            // 2. Guardar valores antigos para histórico
            Map<String, String> oldValues = captureOldValues(oldReport);

            // 3. Atualizar entidade com dados do DTO
            updateReportFromDTO(oldReport, dto);

            // 4. Salvar usando service
            T updatedReport = getReportService().update(reportId, oldReport);

            // 5. Detectar e registar alterações
            List<FieldChange> fieldChanges = comparisonService.detectChanges(oldValues, updatedReport);

            if (!fieldChanges.isEmpty()) {
                logger.info("🔍 Detected {} field changes", fieldChanges.size());
                for (FieldChange change : fieldChanges) {
                    historyService.createFieldChangeEntry(
                            updatedReport,
                            username,
                            change.getFieldName(),
                            change.getOldValue(),
                            change.getNewValue()
                    );
                }
            }

            // 6. Gestão de fotos (adicionar/remover)
            handlePhotoChanges(reportId, dto, username, oldReport.getUuid());

            logger.info("✅ Report updated successfully");
            logger.info("═══════════════════════════════════════════════════════");

            return ResponseEntity.ok(successResponse("Report updated successfully",
                    "reportId", reportId,
                    "uuid", updatedReport.getUuid()));

        } catch (Exception e) {
            logger.error("❌ Error updating report {}", reportId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Error updating report: " + e.getMessage()));
        }
    }

    // ========================================
    // DELETE - Eliminar relatório
    // ========================================

    @Transactional
    public ResponseEntity<?> delete(Integer reportId, Authentication authentication) {
        try {
            String username = authentication.getName();

            logger.info("🗑️ Deleting {} ID: {} by user {}", getEndpointName(), reportId, username);

            // Verificar se existe
            Optional<T> reportOpt = getReportService().getById(reportId);
            if (!reportOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(errorResponse("Report not found"));
            }

            // Registar no histórico antes de eliminar
            historyService.logDelete(reportOpt.get(), username);

            // Eliminar
            getReportService().delete(reportId);

            logger.info("✅ Report deleted successfully");

            return ResponseEntity.ok(successResponse("Report deleted successfully",
                    "reportId", reportId));

        } catch (Exception e) {
            logger.error("❌ Error deleting report {}", reportId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Error deleting report: " + e.getMessage()));
        }
    }

    // ========================================
    // GET PHOTOS - Obter fotos de um relatório
    // ========================================

    public ResponseEntity<?> getPhotos(Integer reportId) {
        try {
            logger.info("📸 Fetching photos for report {}", reportId);

            // Buscar relatório
            Optional<T> reportOpt = getReportService().getById(reportId);
            if (!reportOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(errorResponse("Report not found"));
            }

            T report = reportOpt.get();

            // Buscar fotos pelo UUID
            List<FileData> photos = fileService.readFile(report.getUuid());

            // Filtrar apenas fotos ativas
            photos = photos.stream()
                    .filter(FileData::isActive)
                    .collect(Collectors.toList());

            // Converter para DTOs
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
                photoData.put("downloadUrl", "/api/reports/mobile/files/download/" + photo.getHash());

                photoList.add(photoData);
            }

            logger.info("✅ Returning {} photos", photoList.size());
            return ResponseEntity.ok(photoList);

        } catch (Exception e) {
            logger.error("❌ Error getting photos for report {}", reportId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Error getting photos: " + e.getMessage()));
        }
    }

    // ========================================
    // GET HISTORY - Obter histórico de alterações
    // ========================================

    public ResponseEntity<?> getHistory(Long reportId) {
        try {
            logger.info("📜 Getting history for report {}", reportId);

            List<ReportHistory> history = historyService.getReportHistory(reportId);

            List<MobileReportDTO.ReportHistoryDTO> historyDTOs = history.stream()
                    .map(h -> MobileReportDTO.ReportHistoryDTO.builder()
                            .id(h.getId())
                            .changedBy(h.getChangedBy())
                            .changedAt(h.getChangedAt())
                            .action(h.getAction().name())
                            .fieldName(h.getFieldName())
                            .oldValue(h.getOldValue())
                            .newValue(h.getNewValue())
                            .description(h.getDescription())
                            .build())
                    .collect(Collectors.toList());

            logger.info("✅ Returning {} history entries", historyDTOs.size());
            return ResponseEntity.ok(historyDTOs);

        } catch (Exception e) {
            logger.error("❌ Error getting history for report {}", reportId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Error getting history: " + e.getMessage()));
        }
    }

    // ========================================
    // MÉTODOS AUXILIARES PROTEGIDOS
    // ========================================

    /**
     * Associar fotos a um relatório
     */
    protected int associatePhotosToReport(Integer reportId, List<String> photoIds) {
        int count = 0;

        logger.info("📸 Associating {} photos to report {}", photoIds.size(), reportId);

        for (String fileId : photoIds) {
            try {
                FileData fileData = null;

                // Tentar buscar por ID numérico
                try {
                    Long numericId = Long.parseLong(fileId);
                    fileData = fileService.readFileData(numericId.intValue());
                } catch (NumberFormatException e) {
                    // Tentar buscar por hash
                    fileData = fileService.readFileByHash(fileId);
                }

                if (fileData != null) {
                    count++;
                    logger.info("   ✅ Photo {} associated", fileData.getFileId());
                }

            } catch (Exception e) {
                logger.error("   ❌ Error associating photo {}: {}", fileId, e.getMessage());
            }
        }

        logger.info("✅ Total {} photos associated", count);
        return count;
    }

    /**
     * Capturar valores antigos para comparação
     */
    protected Map<String, String> captureOldValues(T report) {
        Map<String, String> oldValues = new HashMap<>();
        oldValues.put("site", report.getSite());
        oldValues.put("wtgNumber", report.getWtgNumber());
        oldValues.put("wtgType", report.getWtgType());
        oldValues.put("yearConstruction", report.getYearConstruction());
        // Adicionar outros campos conforme necessário
        return oldValues;
    }

    /**
     * Atualizar relatório a partir do DTO (método genérico básico)
     * Subclasses podem sobrescrever para campos específicos
     */
    protected void updateReportFromDTO(T report, MobileReportDTO.ReportCreateUpdateDTO dto) throws Exception {
        JsonNode reportData = objectMapper.readTree(dto.getReportData());

        if (reportData.has("site")) {
            report.setSite(reportData.get("site").asText());
        }
        if (reportData.has("wtgNumber")) {
            report.setWtgNumber(reportData.get("wtgNumber").asText());
        }
        if (reportData.has("wtgType")) {
            report.setWtgType(reportData.get("wtgType").asText());
        }
        if (reportData.has("yearConstruction")) {
            report.setYearConstruction(reportData.get("yearConstruction").asText());
        }

        report.setModifiedDate(LocalDateTime.now());
    }

    /**
     * Gestão de alterações de fotos (adicionar/remover)
     */
    protected void handlePhotoChanges(
            Integer reportId,
            MobileReportDTO.ReportCreateUpdateDTO dto,
            String username,
            String reportUuid) {

        // Implementação básica - subclasses podem sobrescrever
        if (dto.getPhotoFileIds() != null && !dto.getPhotoFileIds().isEmpty()) {
            associatePhotosToReport(reportId, dto.getPhotoFileIds());
        }
    }

    /**
     * Converter entidade para DTO de resposta
     */
    protected Map<String, Object> convertToResponseDTO(T report) {
        Map<String, Object> response = new HashMap<>();
        response.put("reportId", report.getReportId());
        response.put("uuid", report.getUuid());
        response.put("site", report.getSite());
        response.put("wtgNumber", report.getWtgNumber());
        response.put("wtgType", report.getWtgType());
        response.put("yearConstruction", report.getYearConstruction());
        response.put("createDate", report.getCreateDate());
        response.put("modifiedDate", report.getModifiedDate());
        response.put("typeReport", report.getTypeReport());
        response.put("status", report.getStatus() != null ? report.getStatus().name() : null);
        return response;
    }

    /**
     * Criar resposta de sucesso para CREATE
     */
    protected Map<String, Object> createSuccessResponse(T report, int numberOfPhotos) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Report created successfully");
        response.put("reportId", report.getReportId());
        response.put("uuid", report.getUuid());
        response.put("numberOfPhotos", numberOfPhotos);
        return response;
    }

    /**
     * Criar resposta de sucesso genérica
     */
    protected Map<String, Object> successResponse(String message, Object... extraKeyValuePairs) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);

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
    protected Map<String, Object> errorResponse(String error) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", error);
        return response;
    }
}
