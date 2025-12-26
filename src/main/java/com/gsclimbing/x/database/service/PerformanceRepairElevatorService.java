package com.gsclimbing.x.database.service;

import com.gsclimbing.database.entity.PerformanceReportRepairElevator;
import com.gsclimbing.database.entity.Report;
import com.gsclimbing.database.repository.PerformanceReportRepairElevatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service para Performance Report Repair Elevator
 * Estende BaseReportService para reutilizar lógica comum
 */
@Service
public class PerformanceRepairElevatorService extends BaseReportService<PerformanceReportRepairElevator> {

    private static final int REPORT_TYPE = 6;

    @Autowired
    private PerformanceReportRepairElevatorRepository performanceReportRepairElevatorRepository;

    // ========================================
    // IMPLEMENTAÇÃO DOS MÉTODOS ABSTRATOS
    // ========================================

    @Override
    protected int getReportType() {
        return REPORT_TYPE;
    }

    @Override
    protected String getReportName() {
        return "Performance Report Repair Elevator";
    }

    @Override
    protected Optional<PerformanceReportRepairElevator> findById(Integer id) {
        return performanceReportRepairElevatorRepository.findById(id);
    }

    @Override
    protected PerformanceReportRepairElevator save(PerformanceReportRepairElevator entity) {
        return performanceReportRepairElevatorRepository.save(entity);
    }

    @Override
    protected void deleteById(Integer id) {
        performanceReportRepairElevatorRepository.deleteById(id);
    }

    @Override
    protected List<PerformanceReportRepairElevator> findAll() {
        List<PerformanceReportRepairElevator> list = new ArrayList<>();
        performanceReportRepairElevatorRepository.findAll().forEach(list::add);
        return list;
    }

    // ========================================
    // MÉTODOS ESPECÍFICOS (mantidos para compatibilidade)
    // ========================================

    /**
     * Criar relatório completo (mantido para compatibilidade com controller)
     * Agora apenas delega para o método create() da base
     */
    @Transactional
    public PerformanceReportRepairElevator createComplete(
            PerformanceReportRepairElevator report,
            PerformanceReportRepairElevatorSpecificData specificData) {

        logger.info("═══════════════════════════════════════════════════════");
        logger.info("📝 Creating Performance Report Repair Elevator (COMPLETE)");

        // ✅ DEFINIR typeReport MANUALMENTE antes de criar
        report.setTypeReport(REPORT_TYPE);

        // ✅ Preencher dados específicos
        populateSpecificData(report, specificData);

        // ✅ Chamar create() que vai salvar tudo
        PerformanceReportRepairElevator saved = create(report);

        logger.info("✅ Performance Report created with ID: {}", saved.getReportId());
        logger.info("═══════════════════════════════════════════════════════");

        return saved;
    }

    /**
     * Atualizar relatório completo (mantido para compatibilidade)
     */
    @Transactional
    public PerformanceReportRepairElevator updateComplete(
            Report report,
            PerformanceReportRepairElevatorSpecificData specificData) {

        logger.info("═══════════════════════════════════════════════════════");
        logger.info("🔧 Updating Performance Report Repair Elevator ID: {}", report.getReportId());

        // Buscar entidade específica
        PerformanceReportRepairElevator specificReport = getById(report.getReportId())
                .orElseThrow(() -> new RuntimeException(
                        "Performance Report Repair Elevator not found: " + report.getReportId()));

        // Atualizar campos base do Report
        copyBaseFields(report, specificReport);

        // Atualizar dados específicos
        populateSpecificData(specificReport, specificData);

        // Usar método base para atualizar
        PerformanceReportRepairElevator updated = update(report.getReportId(), specificReport);

        logger.info("✅ Performance Report updated successfully");
        logger.info("═══════════════════════════════════════════════════════");

        return updated;
    }

    /**
     * Obter relatório completo por ID (mantido para compatibilidade)
     */
    public PerformanceReportRepairElevator getCompleteById(Integer id) {
        logger.info("🔍 Fetching complete Performance Report by ID: {}", id);
        return getById(id).orElse(null);
    }

    // ========================================
    // MÉTODOS AUXILIARES PRIVADOS
    // ========================================

    /**
     * Preencher dados específicos do Performance Report
     */
    private void populateSpecificData(
            PerformanceReportRepairElevator report,
            PerformanceReportRepairElevatorSpecificData specificData) {

        report.setReportNumber(specificData.getReportNumber());
        report.setInpectorsWorkers(specificData.getInpectorsWorkers());
        report.setStatementOfwork(specificData.getStatementOfwork());
        report.setWorkCompleted(specificData.getWorkCompleted());
        report.setTurbineOperable(specificData.getTurbineOperable());
        report.setPlaceDate(specificData.getPlaceDate());
        report.setResponsibleTechnician(specificData.getResponsibleTechnician());
        report.setPerformanceReport(specificData.getPerformanceReport());
    }

    /**
     * Copiar campos base de Report para PerformanceReportRepairElevator
     */
    private void copyBaseFields(Report source, PerformanceReportRepairElevator target) {
        target.setSite(source.getSite());
        target.setWtgNumber(source.getWtgNumber());
        target.setWtgType(source.getWtgType());
        target.setYearConstruction(source.getYearConstruction());
        target.setModifiedDate(source.getModifiedDate());
        target.setLocked(source.getLocked());
        target.setPermission2Edit(source.getPermission2Edit());
        target.setInsertImagesChk(source.getInsertImagesChk());

        // Copiar campos adicionais
        target.setAdditionalField1Label(source.getAdditionalField1Label());
        target.setAdditionalField1Text(source.getAdditionalField1Text());
        target.setAdditionalField2Label(source.getAdditionalField2Label());
        target.setAdditionalField2Text(source.getAdditionalField2Text());
        target.setAdditionalField3Label(source.getAdditionalField3Label());
        target.setAdditionalField3Text(source.getAdditionalField3Text());
        target.setAdditionalField4Label(source.getAdditionalField4Label());
        target.setAdditionalField4Text(source.getAdditionalField4Text());
        target.setAdditionalField5Label(source.getAdditionalField5Label());
        target.setAdditionalField5Text(source.getAdditionalField5Text());
        target.setAdditionalField6Label(source.getAdditionalField6Label());
        target.setAdditionalField6Text(source.getAdditionalField6Text());
        target.setAdditionalField7Label(source.getAdditionalField7Label());
        target.setAdditionalField7Text(source.getAdditionalField7Text());
    }

    // ========================================
    // CLASSE AUXILIAR PARA DADOS ESPECÍFICOS
    // ========================================

    /**
     * Classe para transportar dados específicos do Performance Report
     */
    public static class PerformanceReportRepairElevatorSpecificData {
        private String reportNumber;
        private String inpectorsWorkers;
        private String statementOfwork;
        private String workCompleted;
        private String turbineOperable;
        private String placeDate;
        private String responsibleTechnician;
        private String performanceReport;

        // Getters e Setters
        public String getReportNumber() { return reportNumber; }
        public void setReportNumber(String reportNumber) { this.reportNumber = reportNumber; }

        public String getInpectorsWorkers() { return inpectorsWorkers; }
        public void setInpectorsWorkers(String inpectorsWorkers) { this.inpectorsWorkers = inpectorsWorkers; }

        public String getStatementOfwork() { return statementOfwork; }
        public void setStatementOfwork(String statementOfwork) { this.statementOfwork = statementOfwork; }

        public String getWorkCompleted() { return workCompleted; }
        public void setWorkCompleted(String workCompleted) { this.workCompleted = workCompleted; }

        public String getTurbineOperable() { return turbineOperable; }
        public void setTurbineOperable(String turbineOperable) { this.turbineOperable = turbineOperable; }

        public String getPlaceDate() { return placeDate; }
        public void setPlaceDate(String placeDate) { this.placeDate = placeDate; }

        public String getResponsibleTechnician() { return responsibleTechnician; }
        public void setResponsibleTechnician(String responsibleTechnician) {
            this.responsibleTechnician = responsibleTechnician;
        }

        public String getPerformanceReport() { return performanceReport; }
        public void setPerformanceReport(String performanceReport) {
            this.performanceReport = performanceReport;
        }
    }
}