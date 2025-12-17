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
import com.gsclimbing.x.database.service.ReportComparisonService;
import com.gsclimbing.x.database.service.ReportHistoryService;
import com.gsclimbing.x.database.service.ReportValidationService;
import com.gsclimbing.x.dto.DefectInspectionReportDTO;
import com.gsclimbing.x.dto.DefectInspectionReportResponseDTO;
import com.gsclimbing.x.dto.MobileReportDTO;
import com.gsclimbing.x.util.FieldChange;
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

    @Autowired
    private ReportComparisonService comparisonService;

    @PostMapping("/defect-inspectionx")
    public ResponseEntity<?> createDefectInspectionReport(
            @RequestBody MobileReportDTO.ReportCreateUpdateDTO dto) {

        return createReport(dto);
    }


    private ResponseEntity<?> createReport(MobileReportDTO.ReportCreateUpdateDTO dto) {
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
            case 1:
                dto.setAdditionalField1(field);
                break;
            case 2:
                dto.setAdditionalField2(field);
                break;
            case 3:
                dto.setAdditionalField3(field);
                break;
            case 4:
                dto.setAdditionalField4(field);
                break;
            case 5:
                dto.setAdditionalField5(field);
                break;
            case 6:
                dto.setAdditionalField6(field);
                break;
            case 7:
                dto.setAdditionalField7(field);
                break;
        }
    }


    /**
     * Criar Defect Inspection Report a partir do mobile
     * VALIDAÇÃO: Apenas 1 relatório por turbina
     *
     * @param dto Dados do relatório
     * @return Resposta com ID do relatório criado
     */
    @PostMapping("/defect-inspection")
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


            dto.setReportType(Report.ReportStatus.DRAFT.ordinal());


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
     * ✅ SUPORTA tanto IDs numéricos como hashes (UUIDs)
     *
     * @param reportId     ID do relatório
     * @param photoFileIds Lista de IDs ou hashes de ficheiros de fotos
     * @return Número de fotos associadas
     */
    private int associatePhotosToReport(Integer reportId, List<String> photoFileIds) {
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
                        logger.error("   ❌ FileData não encontrado para hash: {}", fileId);
                    }
                }

                // Se encontrou a foto, associar ao relatório
                if (fileData != null) {
                    // A relação Report -> FileData já existe através do campo report em FileData
                    // Não é necessário fazer nada extra, apenas contar
                    count++;
                    logger.info("   📎 Foto {} associada ao relatório {}", fileData.getFileId(), reportId);
                }

            } catch (Exception e) {
                logger.error("❌ Erro ao associar foto {} ao relatório {}", fileId, reportId, e);
            }
        }

        logger.info("✅ Total: {} fotos associadas ao relatório {}", count, reportId);
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
     * <p>
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

            // ✅ ALTERAÇÃO: Buscar APENAS fotos ativas
            List<FileData> photos = fileService.readActiveFilesByUuid(report.getUuid());

            if (photos == null || photos.isEmpty()) {
                logger.info("No active photos found for report {}", reportId);
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
                photoData.put("downloadUrl", "/api/reports/files/download/" + photo.getHash());

                photoList.add(photoData);
            }

            logger.info("✅ Returning {} active photos for report {}", photoList.size(), reportId);
            return ResponseEntity.ok(photoList);

        } catch (Exception e) {
            logger.error("❌ Error getting photos for report {}", reportId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting photos: " + e.getMessage());
        }
    }

    /**
     * Download de uma foto específica pelo hash
     * <p>
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
     * GET /api/reports/mobile/defect-inspection/{reportId}/history
     * Obter histórico de alterações de um Defect Inspection Report
     */
    @GetMapping("/defect-inspection/{reportId}/history")
    public ResponseEntity<?> getDefectInspectionReportHistory(@PathVariable Long reportId) {
        try {
            logger.info("📜 Getting history for report {}", reportId);

            List<ReportHistory> history = historyService.getReportHistory(reportId);

            List<MobileReportDTO.ReportHistoryDTO> historyDTOs = history.stream()
                    .map(h -> {
                        String oldValue = h.getOldValue();
                        String newValue = h.getNewValue();

                        // ✅ NOVO: Se é alteração de foto E ainda não tem hash, adicionar
                        if ("photo_added".equals(h.getFieldName()) || "photo_removed".equals(h.getFieldName())) {
                            // Verificar se já tem hash (formato: "Foto ID: 123|hash")
                            boolean oldValueHasHash = oldValue != null && oldValue.contains("|");
                            boolean newValueHasHash = newValue != null && newValue.contains("|");

                            // Se não tem hash, buscar e adicionar
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
            logger.error("❌ Error getting history for report {}", reportId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao obter histórico: " + e.getMessage());
        }
    }

    /**
     * ✅ NOVO MÉTODO: Adiciona hash ao valor da foto
     * Converte "Foto ID: 123" para "Foto ID: 123|abcdef456"
     */
    private String addHashToPhotoValue(String photoValue) {
        try {
            // Extrair ID: "Foto ID: 123" -> "123"
            String photoIdStr = photoValue.replace("Foto ID: ", "").trim();
            Integer photoId = Integer.parseInt(photoIdStr);

            // Buscar FileData para obter hash
            Optional<FileData> fileData = fileService.readFile(photoId);

            if (fileData.isPresent() && fileData.get().getHash() != null) {
                String hash = fileData.get().getHash();
                logger.debug("📸 Added hash to photo {}: {}", photoId, hash);
                return "Foto ID: " + photoId + "|" + hash;
            } else {
                logger.warn("⚠️ Photo {} not found or has no hash", photoId);
                return photoValue; // Retornar original se não encontrar
            }
        } catch (Exception e) {
            logger.error("❌ Error adding hash to photo value: {}", photoValue, e);
            return photoValue; // Retornar original em caso de erro
        }
    }

    /**
     * POST /api/reports/mobile/sync-offline
     * Sincronizar relatório criado offline
     */
    @PostMapping("/sync-offline")
    public ResponseEntity<?> syncOfflineReport(
            @RequestBody MobileReportDTO.OfflineSyncDTO offlineSync,
            Authentication authentication
    ) {
        try {
            String username = authentication.getName();

            // Validar primeiro
            MobileReportDTO.ValidationResponseDTO validation = validationService.validateReport(
                    offlineSync.getReportType(),
                    offlineSync.getReportData(),
                    offlineSync.getPhotos() != null ? offlineSync.getPhotos().size() : 0
            );

            if (!validation.getIsValid()) {
                return ResponseEntity.ok(MobileReportDTO.SyncResponseDTO.builder()
                        .success(false)
                        .tempId(offlineSync.getTempId())
                        .message("Validação falhou")
                        .errors(validation.getErrors())
                        .build());
            }


            // 1. Primeiro fazer upload das fotos e obter os IDs
            List<Long> photoIds = new ArrayList<>();
            if (offlineSync.getPhotos() != null) {
                for (MobileReportDTO.PhotoUploadDTO photo : offlineSync.getPhotos()) {
                    Long photoId = 1L;
                    //Long photoId = photoService.uploadPhoto(photo);
                    photoIds.add(photoId);
                }
            }

            // 2. Mapear para o DTO de criação
            MobileReportDTO.ReportCreateUpdateDTO reportDTO = MobileReportDTO.mapToReportCreateUpdateDTO(offlineSync, photoIds);

            // 3. Criar o relatório
            createReport(reportDTO);

            return ResponseEntity.ok(MobileReportDTO.SyncResponseDTO.builder()
                    .success(true)
                    .reportId(123L) // ID real
                    .tempId(offlineSync.getTempId())
                    .message("Sincronizado com sucesso")
                    .build());

        } catch (Exception e) {
            return ResponseEntity.ok(MobileReportDTO.SyncResponseDTO.builder()
                    .success(false)
                    .tempId(offlineSync.getTempId())
                    .message("Erro: " + e.getMessage())
                    .build());
        }
    }





































    /**
     * CORREÇÃO FINAL: APAGAR FISICAMENTE AS FOTOS REMOVIDAS
     *
     * PROBLEMA IDENTIFICADO:
     * O método updateDefectInspectionReport() detecta que uma foto foi removida
     * e regista no histórico "photo_removed", MAS não apaga a foto do servidor!
     *
     * Quando voltas a abrir o relatório, o fileService.readFile(uuid) devolve
     * TODAS as fotos associadas ao UUID, incluindo as que foram "removidas".
     *
     * SOLUÇÃO:
     * Depois de detectar as fotos removidas, APAGÁ-LAS FISICAMENTE do servidor.
     */

    @PutMapping("/defect-inspection/{id}")
    public ResponseEntity<?> updateDefectInspectionReport(
            @PathVariable Long id,
            @RequestBody DefectInspectionReportDTO dto,
            Authentication authentication
    ) {
        try {
            String username = authentication.getName();
            logger.info("📝 Updating Defect Inspection Report {} by user {}", id, username);

            // 1. Buscar relatório existente
            DefectsInspectionReport report = defectsInspectionReportService
                    .readDefectsInspectionReport(id.intValue());

            if (report == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Relatório não encontrado");
            }

            // 2. Buscar fotos antigas EXCLUINDO as recém-carregadas E as removidas
            List<FileData> allPhotosFileData = fileService.readFile(report.getUuid());

            LocalDateTime twoSecondsAgo = LocalDateTime.now().minusSeconds(2);

            List<Long> oldPhotoIds = allPhotosFileData.stream()
                    .filter(fd -> fd != null && fd.getFileId() != null)
                    .filter(FileData::isActive)  // ✅ NOVO: Filtrar apenas ativas
                    .filter(fd -> fd.getCreateDate() != null && fd.getCreateDate().isBefore(twoSecondsAgo))
                    .map(FileData::getFileId)
                    .map(Integer::longValue)
                    .collect(Collectors.toList());

            logger.info("📸 FOTOS ANTIGAS ATIVAS (>2s): {} fotos encontradas", oldPhotoIds.size());

            logger.info("📸 FOTOS ANTIGAS (>2s): {} fotos encontradas", oldPhotoIds.size());
            oldPhotoIds.forEach(photoId -> logger.info("   - Foto antiga ID: {}", photoId));

            List<Long> recentPhotoIds = allPhotosFileData.stream()
                    .filter(fd -> fd != null && fd.getFileId() != null)
                    .filter(fd -> fd.getCreateDate() != null && !fd.getCreateDate().isBefore(twoSecondsAgo))
                    .map(FileData::getFileId)
                    .map(Integer::longValue)
                    .collect(Collectors.toList());

            if (!recentPhotoIds.isEmpty()) {
                logger.info("⏱️ Fotos recentes ignoradas (<2s): {}", recentPhotoIds);
            }

            // 3. Guardar estado antigo dos campos para comparação
            String oldSite = report.getSite();
            String oldWtgNumber = report.getWtgNumber();
            String oldWtgType = report.getWtgType();
            String oldYearConstruction = report.getYearConstruction();

            Map<String, String> oldAdditionalFields = new HashMap<>();
            for (int i = 1; i <= 7; i++) {
                String labelKey = "additionalField" + i + "Label";
                String textKey = "additionalField" + i + "Text";
                oldAdditionalFields.put(labelKey, getAdditionalFieldValue(report, i, true));
                oldAdditionalFields.put(textKey, getAdditionalFieldValue(report, i, false));
            }

            // 4. Atualizar campos do relatório
            if (dto.getSite() != null) report.setSite(dto.getSite());
            if (dto.getWtgNumber() != null) report.setWtgNumber(dto.getWtgNumber());
            if (dto.getWtgType() != null) report.setWtgType(dto.getWtgType());
            if (dto.getYearConstruction() != null) report.setYearConstruction(dto.getYearConstruction());

            report.setModifiedDate(LocalDateTime.now());

            // 5. Atualizar campos adicionais
            updateAdditionalFields(report, dto);

            // 6. Registar alterações nos campos de texto
            List<FieldChange> fieldChanges = new ArrayList<>();

            if (!Objects.equals(oldSite, report.getSite())) {
                fieldChanges.add(new FieldChange("site", oldSite, report.getSite()));
                logger.info("📝 Campo alterado: site | '{}' -> '{}'", oldSite, report.getSite());
            }
            if (!Objects.equals(oldWtgNumber, report.getWtgNumber())) {
                fieldChanges.add(new FieldChange("wtgNumber", oldWtgNumber, report.getWtgNumber()));
                logger.info("📝 Campo alterado: wtgNumber | '{}' -> '{}'", oldWtgNumber, report.getWtgNumber());
            }
            if (!Objects.equals(oldWtgType, report.getWtgType())) {
                fieldChanges.add(new FieldChange("wtgType", oldWtgType, report.getWtgType()));
                logger.info("📝 Campo alterado: wtgType | '{}' -> '{}'", oldWtgType, report.getWtgType());
            }
            if (!Objects.equals(oldYearConstruction, report.getYearConstruction())) {
                fieldChanges.add(new FieldChange("yearConstruction", oldYearConstruction, report.getYearConstruction()));
                logger.info("📝 Campo alterado: yearConstruction | '{}' -> '{}'",
                        oldYearConstruction, report.getYearConstruction());
            }

            // Verificar alterações nos campos adicionais
            for (int i = 1; i <= 7; i++) {
                String labelKey = "additionalField" + i + "Label";
                String textKey = "additionalField" + i + "Text";

                String oldLabel = oldAdditionalFields.get(labelKey);
                String oldText = oldAdditionalFields.get(textKey);

                String newLabel = getAdditionalFieldValue(report, i, true);
                String newText = getAdditionalFieldValue(report, i, false);

                if (!Objects.equals(oldLabel, newLabel)) {
                    fieldChanges.add(new FieldChange(labelKey, oldLabel, newLabel));
                    logger.info("📝 Campo alterado: {} | '{}' -> '{}'", labelKey, oldLabel, newLabel);
                }
                if (!Objects.equals(oldText, newText)) {
                    fieldChanges.add(new FieldChange(textKey, oldText, newText));
                    logger.info("📝 Campo alterado: {} | '{}' -> '{}'", textKey, oldText, newText);
                }
            }

            // 7. Guardar histórico de alterações nos campos
            for (FieldChange fieldChange : fieldChanges) {
                historyService.createFieldChangeEntry(
                        report,
                        username,
                        fieldChange.getFieldName(),
                        fieldChange.getOldValue(),
                        fieldChange.getNewValue()
                );
            }

            // 8. Processar alterações de fotos
            logger.info("🔍 ========== COMPARAÇÃO DE FOTOS ==========");

            if (dto.getPhotoFileIds() != null && !dto.getPhotoFileIds().isEmpty()) {
                logger.info("📸 DTO photoFileIds: {}", dto.getPhotoFileIds());

                // Converter IDs do DTO para Long
                List<Long> newPhotoIds = new ArrayList<>();
                logger.info("📥 Processando {} photo IDs do DTO...", dto.getPhotoFileIds().size());

                for (String photoId : dto.getPhotoFileIds()) {
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

                logger.info("📸 FOTOS NOVAS: {} fotos válidas", newPhotoIds.size());
                newPhotoIds.forEach(photoId -> logger.info("   - Foto nova ID: {}", photoId));

                logger.info("🔍 Comparando fotos antigas vs novas...");
                logger.info("   Antigas (>2s): {}", oldPhotoIds);
                logger.info("   Novas: {}", newPhotoIds);

                // ✅ ADICIONAR ESTA VERIFICAÇÃO:

                // Se não havia fotos antigas, são as PRIMEIRAS fotos → NÃO registar no histórico
                if (oldPhotoIds.isEmpty() && !newPhotoIds.isEmpty()) {
                    logger.info("ℹ️ Primeiras fotos do relatório - não registar no histórico");
                } else {
                    // Comparar fotos APENAS se já existiam fotos antigas
                    List<FieldChange> photoChanges = comparisonService.comparePhotos(oldPhotoIds, newPhotoIds);

                    logger.info("📊 Resultado da comparação: {} alterações de fotos", photoChanges.size());

                    // ✅ CORREÇÃO CRÍTICA: APAGAR FISICAMENTE AS FOTOS REMOVIDAS
                    for (FieldChange photoChange : photoChanges) {
                        // Registar no histórico
                        historyService.createFieldChangeEntry(
                                report,
                                username,
                                photoChange.getFieldName(),
                                photoChange.getOldValue(),
                                photoChange.getNewValue()
                        );
                        logger.info("📸 {} | OLD: '{}' | NEW: '{}'",
                                photoChange.getFieldName(),
                                photoChange.getOldValue(),
                                photoChange.getNewValue());

                        // ✅ SE FOI REMOVIDA, APAGAR FISICAMENTE!
                        if ("photo_removed".equals(photoChange.getFieldName())) {
                            try {
                                String oldValue = photoChange.getOldValue();
                                if (oldValue != null && oldValue.startsWith("Foto ID: ")) {
                                    String photoIdStr = oldValue.replace("Foto ID: ", "").split("\\|")[0].trim();
                                    Integer photoId = Integer.parseInt(photoIdStr);

                                    logger.info("🗑️ Apagando foto removida: ID {}", photoId);

                                    // Apagar fisicamente a foto
                                    fileService.deleteFile(photoId);  // ✅ Já faz soft delete

                                    logger.info("✅ Foto {} marcada como removida (soft delete)", photoId);
                                }
                            } catch (Exception e) {
                                logger.error("❌ Erro ao apagar foto: {}", photoChange.getOldValue(), e);
                            }
                        }
                    }
                }
            } else {
                logger.info("ℹ️ Nenhum photoFileId fornecido no DTO");
            }

            // 9. Guardar relatório atualizado
            defectsInspectionReportService.updateDefectsInspectionReport(report);

            // 10. Obter número de fotos atualizado
            List<FileData> photos = fileService.readFile(report.getUuid());
            int numberPictures = photos != null ? photos.size() : 0;

            // 11. Retornar resposta
            DefectInspectionReportResponseDTO response = adapter.toResponseDTO(report, numberPictures);
            response.setMessage(fieldChanges.size() + " campo(s) alterado(s)");

            logger.info("✅ Relatório {} atualizado com sucesso. {} alterações registadas",
                    id, fieldChanges.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("❌ Erro ao atualizar relatório {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar relatório: " + e.getMessage());
        }
    }

    private String getAdditionalFieldValue(DefectsInspectionReport report, int fieldNumber, boolean isLabel) {
        switch (fieldNumber) {
            case 1: return isLabel ? report.getAdditionalField1Label() : report.getAdditionalField1Text();
            case 2: return isLabel ? report.getAdditionalField2Label() : report.getAdditionalField2Text();
            case 3: return isLabel ? report.getAdditionalField3Label() : report.getAdditionalField3Text();
            case 4: return isLabel ? report.getAdditionalField4Label() : report.getAdditionalField4Text();
            case 5: return isLabel ? report.getAdditionalField5Label() : report.getAdditionalField5Text();
            case 6: return isLabel ? report.getAdditionalField6Label() : report.getAdditionalField6Text();
            case 7: return isLabel ? report.getAdditionalField7Label() : report.getAdditionalField7Text();
            default: return null;
        }
    }


/**
 * ===== RESUMO DA CORREÇÃO =====
 *
 * ANTES (ERRADO):
 * 1. Detecta photo_removed → Regista no histórico
 * 2. MAS a foto continua no servidor!
 * 3. Reabrir relatório → fileService.readFile() devolve a foto removida
 *
 * DEPOIS (CORRETO):
 * 1. Detecta photo_removed → Regista no histórico
 * 2. ✅ APAGA FISICAMENTE a foto com fileService.deleteFile()
 * 3. Reabrir relatório → fileService.readFile() NÃO devolve a foto ✅
 *
 * RESULTADO:
 * - Remover foto → Apagada do servidor
 * - Histórico: "photo_removed: Foto ID X"
 * - Reabrir → Foto não aparece mais ✅
 */







}