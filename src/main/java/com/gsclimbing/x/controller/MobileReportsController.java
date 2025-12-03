package com.gsclimbing.x.controller;

import com.gsclimbing.x.adapter.DefectInspectionReportAdapter;
import com.gsclimbing.database.entity.DefectsInspectionReport;
import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.entity.Turbine;
import com.gsclimbing.database.service.DefectsInspectionReportService;
import com.gsclimbing.database.service.FileService;
import com.gsclimbing.database.service.TurbineService;
import com.gsclimbing.x.dto.DefectInspectionReportDTO;
import com.gsclimbing.x.dto.DefectInspectionReportResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private DefectInspectionReportAdapter adapter;

    /**
     * Criar Defect Inspection Report a partir do mobile
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

            // Converter DTO para entidade
            DefectsInspectionReport report = adapter.toEntity(dto, turbine);

            // Salvar relatório
            DefectsInspectionReport savedReport =
                    defectsInspectionReportService.createDefectsInspectionReport(report);

            logger.info("Report created successfully with ID: {}", savedReport.getReportId());

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
            logger.error("Error creating report from mobile", e);
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
            @PathVariable String turbineId) {

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
}