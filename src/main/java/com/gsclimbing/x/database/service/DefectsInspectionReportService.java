package com.gsclimbing.x.database.service;

import com.gsclimbing.database.entity.DefectsInspectionReport;
import com.gsclimbing.database.repository.DefectsInspectionReportRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefectsInspectionReportService extends BaseReportService<DefectsInspectionReport> {

    private static final int REPORT_TYPE = 0;

    @Autowired
    private DefectsInspectionReportRepository defectsInspectionReportRepository;

    @Override
    protected int getReportType() {
        return REPORT_TYPE;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected JpaRepository<DefectsInspectionReport, Integer> getSpecificRepository() {
        return (JpaRepository<DefectsInspectionReport, Integer>) defectsInspectionReportRepository;
    }

    @Override
    protected String getReportName() {
        return "Defects Inspection Report";
    }

    // Métodos de compatibilidade mantidos...
    @Transactional
    public DefectsInspectionReport createDefectsInspectionReport(DefectsInspectionReport report) {
        logger.info("📝 Creating Defects Inspection Report via compatibility method");
        return create(report);
    }

    @Transactional
    public DefectsInspectionReport updateDefectsInspectionReport(
            Integer id,
            DefectsInspectionReport report) {
        logger.info("🔧 Updating Defects Inspection Report via compatibility method");
        return update(id, report);
    }

    public DefectsInspectionReport getDefectsInspectionReportById(Integer id) {
        logger.info("🔍 Fetching Defects Inspection Report via compatibility method");
        return getById(id).orElse(null);
    }

    @Transactional
    public void deleteDefectsInspectionReport(Integer id) {
        logger.info("🗑️ Deleting Defects Inspection Report via compatibility method");
        delete(id);
    }
}