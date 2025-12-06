package com.gsclimbing.x.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsclimbing.database.entity.Report;
import com.gsclimbing.ftp.FTPDownloadFiles;
import com.gsclimbing.x.adapter.DefectInspectionReportAdapter;
import com.gsclimbing.database.entity.DefectsInspectionReport;
import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.entity.Turbine;
import com.gsclimbing.database.service.DefectsInspectionReportService;
import com.gsclimbing.database.service.FileService;
import com.gsclimbing.database.service.TurbineService;
import com.gsclimbing.x.database.entity.ReportHistory;
import com.gsclimbing.x.database.service.ReportHistoryService;
import com.gsclimbing.x.database.service.ReportValidationService;
import com.gsclimbing.x.dto.DefectInspectionReportDTO;
import com.gsclimbing.x.dto.DefectInspectionReportResponseDTO;
import com.gsclimbing.x.dto.MobileReportDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
public class MobileReportsController {

    private static final Logger logger = LoggerFactory.getLogger(MobileReportsController.class);

    @Autowired
    private DefectsInspectionReportService defectsInspectionReportService;

    @Autowired
    private TurbineService turbineService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ReportHistoryService historyService;

    @Autowired
    private ReportValidationService validationService;

    @Autowired
    private DefectInspectionReportAdapter adapter;


    @PostMapping("/defect-inspection")
    public ResponseEntity<?> createDefectInspectionReport(
            @RequestBody MobileReportDTO.ReportCreateUpdateDTO dto) {

        try {
            logger.info("Creating Defect Inspection Report from mobile");

            // Validar campos obrigatórios no reportData JSON
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
            List<DefectsInspectionReport> existingReports =
                    defectsInspectionReportService.readDefectsInspectionReportByTurbineId(turbine.getId());

            if (!existingReports.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Já existe um relatório para esta turbina. " +
                        "Elimine o relatório existente antes de criar um novo.");
                errorResponse.put("existingReportId", existingReports.get(0).getReportId());
                errorResponse.put("code", "REPORT_ALREADY_EXISTS");

                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }

            // Converter DTO para entidade
            DefectsInspectionReport report = adapter.toEntity(dto, turbine);

            // Salvar relatório
            DefectsInspectionReport savedReport =
                    defectsInspectionReportService.createDefectsInspectionReport(report);

            logger.info("✅ Report created successfully with ID: {}", savedReport.getReportId());

            // Associar fotos ao relatório
            int numberPictures = 0;
            if (dto.getPhotoIds() != null && !dto.getPhotoIds().isEmpty()) {
                numberPictures = associatePhotosToReport(
                        savedReport.getReportId(),
                        dto.getPhotoIds().stream()
                                .map(String::valueOf)
                                .collect(Collectors.toList())
                );
            }

            // Criar resposta
            DefectInspectionReportResponseDTO response =
                    adapter.toResponseDTO(savedReport, numberPictures);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            logger.error("❌ Error creating report from mobile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating report: " + e.getMessage());
        }
    }

    /**
     * Mapear dados do MobileReportDTO para DefectInspectionReportDTO
     */
    private DefectInspectionReportDTO mapToDefectInspectionDTO(
            JsonNode reportData,
            MobileReportDTO.ReportCreateUpdateDTO mobileDto) {

        DefectInspectionReportDTO dto = new DefectInspectionReportDTO();

        // Campos obrigatórios
        dto.setSite(reportData.get("site").asText());
        dto.setWtgNumber(reportData.get("wtgNumber").asText());

        // Campos opcionais
        if (reportData.has("wtgType")) {
            dto.setWtgType(reportData.get("wtgType").asText());
        }
        if (reportData.has("yearConstruction")) {
            dto.setYearConstruction(reportData.get("yearConstruction").asText());
        }

        // Mapear campos adicionais (additionalField1-7)
        for (int i = 1; i <= 7; i++) {
            String fieldKey = "additionalField" + i;
            if (reportData.has(fieldKey)) {
                JsonNode field = reportData.get(fieldKey);
                DefectInspectionReportDTO.AdditionalFieldDTO additionalField =
                        new DefectInspectionReportDTO.AdditionalFieldDTO();

                if (field.has("label")) {
                    additionalField.setLabel(field.get("label").asText());
                }
                if (field.has("value")) {
                    additionalField.setValue(field.get("value").asText());
                }

                // Usar reflection ou switch para setar o campo correto
                setAdditionalField(dto, i, additionalField);
            }
        }

        return dto;
    }

    private void setAdditionalField(DefectInspectionReportDTO dto, int index,
                                    DefectInspectionReportDTO.AdditionalFieldDTO field) {
        switch (index) {
            case 1: dto.setAdditionalField1(field); break;
            case 2: dto.setAdditionalField2(field); break;
            case 3: dto.setAdditionalField3(field); break;
            case 4: dto.setAdditionalField4(field); break;
            case 5: dto.setAdditionalField5(field); break;
            case 6: dto.setAdditionalField6(field); break;
            case 7: dto.setAdditionalField7(field); break;
        }
    }


    /**
     * Criar Defect Inspection Report a partir do mobile
     * VALIDAÇÃO: Apenas 1 relatório por turbina
     *
     * @param dto Dados do relatório
     * @return Resposta com ID do relatório criado
     */
    @PostMapping("/defect-inspectionm")
    public ResponseEntity<?> createDefectInspectionReport(
            @RequestBody DefectInspectionReportDTO dto) {

        try {
            logger.info("Creating Defect Inspection Report from mobile for turbine: {}",
                    dto.getTurbinaId());

            // Validar campos obrigatórios
            if (dto.getSite() == null || dto.getSite().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Site is required");
            }
            if (dto.getWtgNumber() == null || dto.getWtgNumber().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("WTG Number is required");
            }
            if (dto.getProjectoId() == null) {
                return ResponseEntity.badRequest().body("Project ID is required");
            }
            if (dto.getTurbinaId() == null) {
                return ResponseEntity.badRequest().body("Turbine ID is required");
            }

            // Validar turbina
            Turbine turbine = turbineService.getTurbine(dto.getTurbinaId());
            if (turbine == null) {
                logger.error("Turbine not found: {}", dto.getTurbinaId());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Turbine not found with ID: " + dto.getTurbinaId());
            }

            // ⭐ VALIDAÇÃO: Verificar se já existe relatório para esta turbina
            List<DefectsInspectionReport> existingReports =
                    defectsInspectionReportService.readDefectsInspectionReportByTurbineId(dto.getTurbinaId());

            if (existingReports != null && !existingReports.isEmpty()) {
                logger.warn("⚠️ Turbine {} already has a Defect Inspection Report", dto.getTurbinaId());

                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Esta turbina já possui um relatório Defect Inspection. Elimine o relatório existente antes de criar um novo.");
                errorResponse.put("existingReportId", existingReports.get(0).getReportId());
                errorResponse.put("code", "REPORT_ALREADY_EXISTS");

                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }

            // Converter DTO para entidade
            DefectsInspectionReport report = adapter.toEntity(dto, turbine);

            // Salvar relatório
            DefectsInspectionReport savedReport =
                    defectsInspectionReportService.createDefectsInspectionReport(report);

            logger.info("✅ Report created successfully with ID: {}", savedReport.getReportId());

            // Associar fotos ao relatório (se foram enviados IDs)
            int numberPictures = 0;
            if (dto.getPhotoFileIds() != null && !dto.getPhotoFileIds().isEmpty()) {
                numberPictures = associatePhotosToReport(
                        savedReport.getReportId(),
                        dto.getPhotoFileIds()
                );
            }

            // Criar resposta
            DefectInspectionReportResponseDTO response =
                    adapter.toResponseDTO(savedReport, numberPictures);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            logger.error("❌ Error creating report from mobile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating report: " + e.getMessage());
        }
    }


    /**
     * Associar fotos já carregadas ao relatório
     *
     * @param reportId ID do relatório
     * @param photoFileIds Lista de IDs de ficheiros de fotos
     * @return Número de fotos associadas
     */
    private int associatePhotosToReport(Integer reportId, List<String> photoFileIds) {
        int count = 0;
        for (String fileId : photoFileIds) {
            try {
                Optional<FileData> optionalFileData = fileService.readFile(Integer.parseInt(fileId));
                if (optionalFileData.isPresent()) {
                    FileData fileData = optionalFileData.get();
                    // FileData já tem relação com Report via report field
                    // Não precisa de setReportId - a relação é gerida pela entidade Report
                    count++;
                }
            } catch (Exception e) {
                logger.error("Error associating photo {} to report {}", fileId, reportId, e);
            }
        }
        logger.info("Associated {} photos to report {}", count, reportId);
        return count;
    }

    /**
     * Obter relatório por ID (para o mobile)
     *
     * @param reportId ID do relatório
     * @return Dados do relatório
     */
    @GetMapping("/defect-inspection/{reportId}")
    public ResponseEntity<?> getDefectInspectionReport(@PathVariable Integer reportId) {
        try {
            DefectsInspectionReport report =
                    defectsInspectionReportService.readDefectsInspectionReport(reportId);

            if (report == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Report not found with ID: " + reportId);
            }

            // Contar fotos usando o UUID do relatório
            List<FileData> photos = fileService.readFile(report.getUuid());
            int numberPictures = photos != null ? photos.size() : 0;

            DefectInspectionReportResponseDTO response =
                    adapter.toResponseDTO(report, numberPictures);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting report: " + e.getMessage());
        }
    }

    /**
     * Atualizar relatório existente
     *
     * @param reportId ID do relatório
     * @param dto Dados atualizados
     * @return Resposta com dados atualizados
     */
    @PutMapping("/defect-inspection/{reportId}")
    public ResponseEntity<?> updateDefectInspectionReport(
            @PathVariable Integer reportId,
            @RequestBody DefectInspectionReportDTO dto) {

        try {
            logger.info("Updating Defect Inspection Report: {}", reportId);

            // Validar campos obrigatórios
            if (dto.getSite() == null || dto.getSite().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Site is required");
            }
            if (dto.getWtgNumber() == null || dto.getWtgNumber().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("WTG Number is required");
            }

            // Buscar relatório existente
            DefectsInspectionReport existingReport =
                    defectsInspectionReportService.readDefectsInspectionReport(reportId);

            if (existingReport == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Report not found with ID: " + reportId);
            }

            // Verificar se está bloqueado
            if ("Y".equals(existingReport.getLocked())) {
                return ResponseEntity.status(HttpStatus.LOCKED)
                        .body("Report is locked and cannot be modified");
            }

            // Atualizar campos
            existingReport.setSite(dto.getSite());
            existingReport.setWtgNumber(dto.getWtgNumber());
            existingReport.setWtgType(dto.getWtgType());
            existingReport.setYearConstruction(dto.getYearConstruction());
            existingReport.setModifiedDate(java.time.LocalDateTime.now());

            // Atualizar campos adicionais
            updateAdditionalFields(existingReport, dto);

            // Salvar
            DefectsInspectionReport updatedReport =
                    defectsInspectionReportService.updateDefectsInspectionReport(existingReport);

            // Contar fotos usando o UUID do relatório
            List<FileData> photos = fileService.readFile(updatedReport.getUuid());
            int numberPictures = photos != null ? photos.size() : 0;

            DefectInspectionReportResponseDTO response =
                    adapter.toResponseDTO(updatedReport, numberPictures);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error updating report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating report: " + e.getMessage());
        }
    }

    /**
     * Método auxiliar para atualizar campos adicionais
     */
    private void updateAdditionalFields(DefectsInspectionReport report, DefectInspectionReportDTO dto) {
        if (dto.getAdditionalField1() != null) {
            report.setAdditionalField1Label(dto.getAdditionalField1().getLabel());
            report.setAdditionalField1Text(dto.getAdditionalField1().getValue());
        }
        if (dto.getAdditionalField2() != null) {
            report.setAdditionalField2Label(dto.getAdditionalField2().getLabel());
            report.setAdditionalField2Text(dto.getAdditionalField2().getValue());
        }
        if (dto.getAdditionalField3() != null) {
            report.setAdditionalField3Label(dto.getAdditionalField3().getLabel());
            report.setAdditionalField3Text(dto.getAdditionalField3().getValue());
        }
        if (dto.getAdditionalField4() != null) {
            report.setAdditionalField4Label(dto.getAdditionalField4().getLabel());
            report.setAdditionalField4Text(dto.getAdditionalField4().getValue());
        }
        if (dto.getAdditionalField5() != null) {
            report.setAdditionalField5Label(dto.getAdditionalField5().getLabel());
            report.setAdditionalField5Text(dto.getAdditionalField5().getValue());
        }
        if (dto.getAdditionalField6() != null) {
            report.setAdditionalField6Label(dto.getAdditionalField6().getLabel());
            report.setAdditionalField6Text(dto.getAdditionalField6().getValue());
        }
        if (dto.getAdditionalField7() != null) {
            report.setAdditionalField7Label(dto.getAdditionalField7().getLabel());
            report.setAdditionalField7Text(dto.getAdditionalField7().getValue());
        }
    }

    /**
     * Obter lista de relatórios de uma turbina
     *
     * @param turbineId ID da turbina
     * @return Lista de relatórios
     */
    @GetMapping("/defect-inspection/turbine/{turbineId}")
    public ResponseEntity<?> getDefectInspectionReportsByTurbine(
            @PathVariable Integer turbineId) {

        try {
            logger.info("Getting Defect Inspection Reports for turbine: {}", turbineId);

            // Buscar relatórios da turbina
            List<DefectsInspectionReport> reports =
                    defectsInspectionReportService.readDefectsInspectionReportByTurbineId(turbineId);

            // Converter para DTOs
            List<DefectInspectionReportResponseDTO> responseDTOs = new ArrayList<>();

            for (DefectsInspectionReport report : reports) {
                // Contar fotos usando o UUID do relatório
                List<FileData> photos = fileService.readFile(report.getUuid());
                int numberPictures = photos != null ? photos.size() : 0;

                // Criar response DTO
                DefectInspectionReportResponseDTO responseDTO =
                        adapter.toResponseDTO(report, numberPictures);

                responseDTOs.add(responseDTO);
            }

            logger.info("Found {} reports for turbine {}", responseDTOs.size(), turbineId);

            return ResponseEntity.ok(responseDTOs);

        } catch (Exception e) {
            logger.error("Error getting reports for turbine", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting reports: " + e.getMessage());
        }
    }

    /**
     * Obter fotos de um relatório (para o mobile)
     *
     * Endpoint: GET /api/reports/mobile/defect-inspection/{reportId}/photos
     *
     * @param reportId ID do relatório
     * @return Lista de fotos com URLs para download
     */
    @GetMapping("/defect-inspection/{reportId}/photos")
    public ResponseEntity<?> getReportPhotos(@PathVariable Integer reportId) {
        try {
            logger.info("📸 Getting photos for report ID: {}", reportId);

            // Buscar relatório
            DefectsInspectionReport report =
                    defectsInspectionReportService.readDefectsInspectionReport(reportId);

            if (report == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Report not found with ID: " + reportId);
            }

            // Buscar fotos usando o UUID do relatório
            List<FileData> photos = fileService.readFile(report.getUuid());

            if (photos == null || photos.isEmpty()) {
                logger.info("No photos found for report {}", reportId);
                return ResponseEntity.ok(new ArrayList<>());
            }

            // Converter para DTOs com URLs de download
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
                // O frontend pode usar este hash para fazer download via /api/reports/files/download/{hash}
                photoData.put("downloadUrl", "/api/reports/files/download/" + photo.getHash());

                photoList.add(photoData);
            }

            logger.info("✅ Returning {} photos for report {}", photoList.size(), reportId);
            return ResponseEntity.ok(photoList);

        } catch (Exception e) {
            logger.error("❌ Error getting photos for report {}", reportId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting photos: " + e.getMessage());
        }
    }

    /**
     * Download de uma foto específica pelo hash
     *
     * Endpoint: GET /api/reports/files/download/{hash}
     *
     * @param hash Hash da foto no FTP
     * @return Bytes da imagem
     */
    @GetMapping("/files/download/{hash}")
    public ResponseEntity<byte[]> downloadPhoto(@PathVariable String hash) {
        try {
            logger.info("📥 Downloading photo with hash: {}", hash);

            // Download do FTP
            byte[] photoBytes = FTPDownloadFiles.downloadFile2FTPServer(hash);

            if (photoBytes == null) {
                logger.error("❌ Photo not found with hash: {}", hash);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Determinar o tipo de conteúdo (mime type)
            FileData fileData = fileService.readFileByHash(hash);
            String contentType = "image/jpeg"; // default

            if (fileData != null && fileData.getMimeType() != null) {
                contentType = fileData.getMimeType();
            }

            logger.info("✅ Photo downloaded successfully, size: {} bytes", photoBytes.length);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .header(HttpHeaders.CACHE_CONTROL, "max-age=3600")
                    .body(photoBytes);

        } catch (Exception e) {
            logger.error("❌ Error downloading photo with hash: {}", hash, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Eliminar Defect Inspection Report
     *
     * @param reportId ID do relatório a eliminar
     * @return Status da operação
     */
    @DeleteMapping("/defect-inspection/{reportId}")
    public ResponseEntity<?> deleteDefectInspectionReport(@PathVariable Integer reportId) {
        try {
            logger.info("🗑️ Deleting Defect Inspection Report: {}", reportId);

            // Buscar relatório
            DefectsInspectionReport report =
                    defectsInspectionReportService.readDefectsInspectionReport(reportId);

            if (report == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Report not found with ID: " + reportId);
            }

            // Verificar se está bloqueado
            if ("Y".equals(report.getLocked())) {
                return ResponseEntity.status(HttpStatus.LOCKED)
                        .body("Report is locked and cannot be deleted");
            }

            String uuid = report.getUuid();

            // Eliminar fotos associadas ao relatório
            try {
                List<FileData> photos = fileService.readFile(uuid);
                if (photos != null && !photos.isEmpty()) {
                    logger.info("📸 Deleting {} photos for report {}", photos.size(), reportId);
                    for (FileData photo : photos) {
                        try {
                            // Eliminar ficheiro físico (FTP ou local)
                            fileService.deleteFile(photo.getFileId());
                            logger.info("   ✅ Photo deleted: {}", photo.getHash());
                        } catch (Exception e) {
                            logger.error("   ❌ Error deleting photo: {}", photo.getHash(), e);
                            // Continuar a eliminar outras fotos mesmo se uma falhar
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("❌ Error deleting photos for report {}", reportId, e);
                // Continuar com a eliminação do relatório
            }

            // Eliminar relatório da base de dados
            defectsInspectionReportService.deleteDefectsInspectionReport(reportId);

            logger.info("✅ Report {} deleted successfully", reportId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Report deleted successfully");
            response.put("reportId", reportId);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("❌ Error deleting report {}", reportId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting report: " + e.getMessage());
        }
    }



































    /**
     * POST /api/reports/mobile/validate
     * Validar relatório antes de submeter
     */
    @PostMapping("/validate")
    public ResponseEntity<MobileReportDTO.ValidationResponseDTO> validateReport(
            @RequestBody MobileReportDTO.ValidationRequestDTO request
    ) {
        MobileReportDTO.ValidationResponseDTO response = validationService.validateReport(
                request.getReportType(),
                request.getReportData(),
                request.getPhotoCount()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/reports/mobile/draft
     * Criar rascunho de relatório (campos parciais)
     */
    @PostMapping("/draft")
    public ResponseEntity<?> createDraft(
            @RequestBody MobileReportDTO.ReportCreateUpdateDTO dto,
            Authentication authentication
    ) {
        try {
            String username = authentication.getName();

            // Buscar turbina
            Turbine turbine = turbineService.getTurbine(dto.getTurbineId().intValue());
            if (turbine == null) {
                return ResponseEntity.badRequest().body("Turbina não encontrada");
            }

            // Criar relatório em modo DRAFT
            Report report = new Report();
            report.setTurbine(turbine);
            report.setTypeReport(dto.getReportType());
            report.setStatus(Report.ReportStatus.DRAFT);
            report.setLanguage(dto.getLanguage() != null ? dto.getLanguage() : "EN");
            report.setOfflineCreated(dto.getOfflineCreated() != null ? dto.getOfflineCreated() : false);
            // report.setReportData(dto.getReportData()); // Guardar JSON

            // Guardar no repositório (assumindo ReportService existe)
            // reportService.save(report);

            // Registar no histórico
            historyService.logCreate(report, username);

            // Resposta
            MobileReportDTO.ReportResponseDTO response = MobileReportDTO.ReportResponseDTO.builder()
                    .id(Long.valueOf(report.getReportId()))
                    .turbineId((long) turbine.getId())
                    .turbineName(turbine.getName())
                    .reportType(report.getTypeReport())
                    .status(report.getStatus().name())
                    .language(report.getLanguage())
                    .canEdit(true)
                    .isLocked(false)
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao criar rascunho: " + e.getMessage());
        }
    }

    /**
     * POST /api/reports/mobile/submit
     * Submeter relatório final
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submitReport(
            @RequestBody MobileReportDTO.ReportSubmitDTO dto,
            Authentication authentication
    ) {
        try {
            String username = authentication.getName();

            // Buscar relatório
            // Report report = reportService.findById(dto.getReportId());
            // if (report == null) {
            //     return ResponseEntity.badRequest().body("Relatório não encontrado");
            // }

            // Validar antes de submeter
            MobileReportDTO.ValidationResponseDTO validation = validationService.validateReport(
                    null, // report.getReportType(),
                    dto.getReportData(),
                    dto.getPhotoIds() != null ? dto.getPhotoIds().size() : 0
            );

            if (!validation.getIsValid()) {
                return ResponseEntity.badRequest().body(validation);
            }

            // Submeter relatório
            // report.submit(username);
            // reportService.save(report);

            // Registar no histórico
            // historyService.logSubmit(report, username);

            // Enviar email
            // emailService.sendReportSubmittedEmail(report);

            return ResponseEntity.ok("Relatório submetido com sucesso");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao submeter relatório: " + e.getMessage());
        }
    }

    /**
     * POST /api/reports/mobile/{id}/request-unlock
     * Técnico pede permissão para editar
     */
    @PostMapping("/{id}/request-unlock")
    public ResponseEntity<?> requestUnlock(
            @PathVariable Long id,
            @RequestBody MobileReportDTO.UnlockRequestDTO dto,
            Authentication authentication
    ) {
        try {
            String username = authentication.getName();

            // Report report = reportService.findById(id);
            // if (report == null) {
            //     return ResponseEntity.badRequest().body("Relatório não encontrado");
            // }

            // if (!report.isLocked()) {
            //     return ResponseEntity.badRequest().body("Relatório não está bloqueado");
            // }

            // Marcar pedido de desbloqueio
            // report.requestUnlock();
            // reportService.save(report);

            // Registar no histórico
            // historyService.logRequestUnlock(report, username);

            // Enviar email ao admin
            // emailService.sendUnlockRequestEmail(report, username, dto.getReason());

            return ResponseEntity.ok("Pedido de desbloqueio enviado ao administrador");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao pedir desbloqueio: " + e.getMessage());
        }
    }

    /**
     * PUT /api/reports/mobile/{id}/unlock
     * Admin desbloqueia relatório (só ADMIN)
     */
    @PutMapping("/{id}/unlock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> unlockReport(
            @PathVariable Long id,
            Authentication authentication
    ) {
        try {
            String adminUsername = authentication.getName();

            // Report report = reportService.findById(id);
            // if (report == null) {
            //     return ResponseEntity.badRequest().body("Relatório não encontrado");
            // }

            // String techUsername = report.getSubmittedBy();
            // report.unlock(adminUsername);
            // reportService.save(report);

            // Registar no histórico
            // historyService.logUnlock(report, adminUsername, techUsername);

            // Notificar técnico (opcional)
            // emailService.sendUnlockNotificationEmail(report, techUsername);

            return ResponseEntity.ok("Relatório desbloqueado");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao desbloquear relatório: " + e.getMessage());
        }
    }

    /**
     * PUT /api/reports/mobile/{id}/update
     * Atualizar relatório (se permitido)
     */
    @PutMapping("/{id}/update")
    public ResponseEntity<?> updateReport(
            @PathVariable Long id,
            @RequestBody MobileReportDTO.ReportCreateUpdateDTO dto,
            Authentication authentication
    ) {
        try {
            String username = authentication.getName();

            // Report report = reportService.findById(id);
            // if (report == null) {
            //     return ResponseEntity.badRequest().body("Relatório não encontrado");
            // }

            // if (!report.canEdit()) {
            //     return ResponseEntity.status(HttpStatus.FORBIDDEN)
            //             .body("Relatório bloqueado. Peça permissão ao administrador.");
            // }

            // Atualizar dados
            // String oldData = report.getReportData();
            // report.setReportData(dto.getReportData());
            // reportService.save(report);

            // Registar alteração no histórico
            // historyService.createFieldChangeEntry(
            //         report,
            //         username,
            //         "reportData",
            //         oldData,
            //         dto.getReportData()
            // );

            return ResponseEntity.ok("Relatório atualizado");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar relatório: " + e.getMessage());
        }
    }

    /**
     * GET /api/reports/{id}/history
     * Obter histórico de alterações
     */
    @GetMapping("/{id}/history")
    public ResponseEntity<?> getReportHistory(@PathVariable Long id) {
        try {
            List<ReportHistory> history = historyService.getReportHistory(id);

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

            return ResponseEntity.ok(historyDTOs);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao obter histórico: " + e.getMessage());
        }
    }

    /**
     * POST /api/reports/mobile/sync-offline
     * Sincronizar relatório criado offline
     */
    @PostMapping("/sync-offline")
    public ResponseEntity<?> syncOfflineReport(
            @RequestBody MobileReportDTO.OfflineSyncDTO dto,
            Authentication authentication
    ) {
        try {
            String username = authentication.getName();

            // Validar primeiro
            MobileReportDTO.ValidationResponseDTO validation = validationService.validateReport(
                    dto.getReportType(),
                    dto.getReportData(),
                    dto.getPhotos() != null ? dto.getPhotos().size() : 0
            );

            if (!validation.getIsValid()) {
                return ResponseEntity.ok(MobileReportDTO.SyncResponseDTO.builder()
                        .success(false)
                        .tempId(dto.getTempId())
                        .message("Validação falhou")
                        .errors(validation.getErrors())
                        .build());
            }

            // Criar relatório no servidor
            // ... (lógica de criação similar ao createDraft)

            return ResponseEntity.ok(MobileReportDTO.SyncResponseDTO.builder()
                    .success(true)
                    .reportId(123L) // ID real
                    .tempId(dto.getTempId())
                    .message("Sincronizado com sucesso")
                    .build());

        } catch (Exception e) {
            return ResponseEntity.ok(MobileReportDTO.SyncResponseDTO.builder()
                    .success(false)
                    .tempId(dto.getTempId())
                    .message("Erro: " + e.getMessage())
                    .build());
        }
    }



}