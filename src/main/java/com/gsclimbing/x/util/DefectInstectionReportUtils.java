package com.gsclimbing.x.util;

import com.gsclimbing.database.entity.DefectsInspectionReport;
import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.service.DefectsInspectionReportService;
import com.gsclimbing.database.service.FileService;
import com.gsclimbing.database.service.TurbineService;
import com.gsclimbing.x.adapter.DefectInspectionReportAdapter;
import com.gsclimbing.x.dto.DefectInspectionReportResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefectInstectionReportUtils {

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
}
