package com.gsclimbing.x.database.service;

import com.gsclimbing.database.entity.DefectsInspectionReport;
import com.gsclimbing.database.repository.DefectsInspectionReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DefectsInspectionReportService extends BaseReportService<DefectsInspectionReport> {

    private static final int REPORT_TYPE = 0;

    @Autowired
    private DefectsInspectionReportRepository defectsInspectionReportRepository;

    // ========================================
    // IMPLEMENTAÇÃO DOS MÉTODOS ABSTRATOS
    // ========================================

    @Override
    protected int getReportType() {
        return REPORT_TYPE;
    }

    @Override
    protected String getReportName() {
        return "Defects Inspection Report";
    }

    @Override
    protected Optional<DefectsInspectionReport> findById(Integer id) {
        return defectsInspectionReportRepository.findById(id);
    }

    @Override
    protected DefectsInspectionReport save(DefectsInspectionReport entity) {
        return defectsInspectionReportRepository.save(entity);
    }

    @Override
    protected void deleteById(Integer id) {
        defectsInspectionReportRepository.deleteById(id);
    }

    @Override
    protected List<DefectsInspectionReport> findAll() {
        return (List<DefectsInspectionReport>)defectsInspectionReportRepository.findAll();
    }

    // ========================================
    // MÉTODOS DE COMPATIBILIDADE
    // ========================================

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

    public List<DefectsInspectionReport> readDefectsInspectionReportByTurbineId(Integer turbineId) {
        logger.info("🔍 Fetching reports by turbine ID via compatibility method");
        return getByTurbineId(turbineId);
    }
}