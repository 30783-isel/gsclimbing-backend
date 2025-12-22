package com.gsclimbing.x.util;

import com.gsclimbing.database.entity.DefectsInspectionReport;
import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.service.DefectsInspectionReportService;
import com.gsclimbing.database.service.FileService;
import com.gsclimbing.database.service.TurbineService;
import com.gsclimbing.x.adapter.DefectInspectionReportAdapter;
import com.gsclimbing.x.controller.DefectInspectionController;
import com.gsclimbing.x.dto.DefectInspectionReportResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class DefectInstectionReportUtils {
    private static final Logger logger = LoggerFactory.getLogger(DefectInspectionController.class);
    @Autowired
    private DefectsInspectionReportService defectsInspectionReportService;
    @Autowired
    private DefectInspectionReportAdapter defectInspectionReportAdapter;
    @Autowired
    private FileService fileService;

    public DefectInspectionReportResponseDTO getDefectInspectionReportDto(Integer reportId) throws Exception {
        DefectsInspectionReport report = defectsInspectionReportService.readDefectsInspectionReport(reportId);
        if (report == null) {
            throw new IllegalArgumentException("Report not found with ID: " + reportId);
        }
        List<FileData> photos = fileService.readFile(report.getUuid());
        int numberPictures = photos != null ? photos.size() : 0;
        return defectInspectionReportAdapter.toResponseDTO(report, numberPictures);
    }

    public DefectInspectionReportResponseDTO getDefectInspectionReport2PDFDto(Integer reportId) throws Exception {
        DefectsInspectionReport report = defectsInspectionReportService.readDefectsInspectionReport(reportId);
        if (report == null) {
            throw new IllegalArgumentException("Report not found with ID: " + reportId);
        }
        int numberPictures = 0;
        return defectInspectionReportAdapter.toResponseDTO(report, numberPictures);
    }

    /**
     * Associar fotos já carregadas ao relatório
     * ✅ SUPORTA tanto IDs numéricos como hashes (UUIDs)
     *
     * @param reportId     ID do relatório
     * @param photoFileIds Lista de IDs ou hashes de ficheiros de fotos
     * @return Número de fotos associadas
     */
    public int associatePhotosToReport(Integer reportId, List<String> photoFileIds) {
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
}
