package com.gsclimbing.x.database.service;

import com.gsclimbing.database.entity.PerformanceReportRepairElevator;
import com.gsclimbing.database.entity.Report;
import com.gsclimbing.database.repository.PerformanceReportRepairElevatorRepository;
import com.gsclimbing.database.repository.ReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para Performance Report Repair Elevator
 * Gere tanto a entidade Report (genérica) como PerformanceReportRepairElevator (específica)
 */
@Service
public class PerformanceRepairElevatorService {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceRepairElevatorService.class);
    private static final int REPORT_TYPE = 6; // Performance Report Repair Elevator

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private PerformanceReportRepairElevatorRepository performanceReportRepairElevatorRepository;

    /**
     * Criar novo relatório completo (Report + PerformanceReportRepairElevator)
     *
     * @param report Entidade Report com dados genéricos
     * @param specificData Dados específicos do PerformanceReportRepairElevator
     * @return Report criado com ID gerado
     */
    @Transactional
    public Report createComplete(Report report, PerformanceReportRepairElevatorSpecificData specificData) {
        logger.info("📝 Creating Performance Report Repair Elevator (COMPLETE)");

        // 1. Primeiro, guardar o Report (tabela pai)
        report.setTypeReport(REPORT_TYPE);
        Report savedReport = reportRepository.save(report);
        logger.info("✅ Report base created with ID: {}", savedReport.getReportId());

        // 2. Depois, criar e guardar PerformanceReportRepairElevator (tabela filha)
        PerformanceReportRepairElevator specificReport = new PerformanceReportRepairElevator();

        // Copiar dados básicos do Report
        specificReport.setReportId(savedReport.getReportId());
        specificReport.setUuid(savedReport.getUuid());
        specificReport.setCreateDate(savedReport.getCreateDate());
        specificReport.setModifiedDate(savedReport.getModifiedDate());
        specificReport.setSite(savedReport.getSite());
        specificReport.setWtgNumber(savedReport.getWtgNumber());
        specificReport.setWtgType(savedReport.getWtgType());
        specificReport.setYearConstruction(savedReport.getYearConstruction());
        specificReport.setProjectoId(savedReport.getProjectoId());
        specificReport.setTurbinaId(savedReport.getTurbinaId());
        specificReport.setTurbine(savedReport.getTurbine());
        specificReport.setTypeReport(savedReport.getTypeReport());
        specificReport.setReportType(savedReport.getReportType());
        specificReport.setLocked(savedReport.getLocked());
        specificReport.setPermission2Edit(savedReport.getPermission2Edit());
        specificReport.setInsertImagesChk(savedReport.getInsertImagesChk());

        // Adicionar dados específicos
        specificReport.setReportNumber(specificData.getReportNumber());
        specificReport.setInpectorsWorkers(specificData.getInpectorsWorkers());
        specificReport.setStatementOfwork(specificData.getStatementOfwork());
        specificReport.setWorkCompleted(specificData.getWorkCompleted());
        specificReport.setTurbineOperable(specificData.getTurbineOperable());
        specificReport.setPlaceDate(specificData.getPlaceDate());
        specificReport.setResponsibleTechnician(specificData.getResponsibleTechnician());
        specificReport.setPerformanceReport(specificData.getPerformanceReport());

        PerformanceReportRepairElevator savedSpecific = performanceReportRepairElevatorRepository.save(specificReport);
        logger.info("✅ Performance Report Repair Elevator specific data created with ID: {}", savedSpecific.getReportId());

        return savedReport;
    }

    /**
     * Criar novo relatório (método original - apenas Report)
     * DEPRECATED: Use createComplete() para criar relatórios completos
     */
    @Transactional
    @Deprecated
    public Report create(Report report) {
        logger.warn("⚠️ Using deprecated create() method. Consider using createComplete() instead.");
        logger.info("📝 Creating Performance Report Repair Elevator (INCOMPLETE - only Report table)");
        report.setTypeReport(REPORT_TYPE);
        Report saved = reportRepository.save(report);
        logger.info("✅ Report created with ID: {}", saved.getReportId());
        return saved;
    }

    /**
     * Atualizar relatório existente completo
     *
     * @param report Entidade Report com dados genéricos atualizados
     * @param specificData Dados específicos atualizados
     * @return Report atualizado
     */
    @Transactional
    public Report updateComplete(Report report, PerformanceReportRepairElevatorSpecificData specificData) {
        logger.info("📝 Updating Performance Report Repair Elevator ID: {} (COMPLETE)", report.getReportId());

        // 1. Atualizar Report (tabela pai)
        Report updatedReport = reportRepository.save(report);
        logger.info("✅ Report base updated");

        // 2. Atualizar PerformanceReportRepairElevator (tabela filha)
        PerformanceReportRepairElevator specificReport = performanceReportRepairElevatorRepository
                .findById(report.getReportId())
                .orElse(null);

        // Se não existir, criar novo (caso de migração de dados antigos)
        if (specificReport == null) {
            logger.warn("⚠️ Specific data not found for Report ID: {}. Creating new entry.", report.getReportId());
            specificReport = new PerformanceReportRepairElevator();

            // Copiar TODOS os campos obrigatórios do Report
            specificReport.setReportId(updatedReport.getReportId());
            specificReport.setUuid(updatedReport.getUuid());
            specificReport.setCreateDate(updatedReport.getCreateDate());
            specificReport.setSite(updatedReport.getSite());
            specificReport.setWtgNumber(updatedReport.getWtgNumber());
            specificReport.setWtgType(updatedReport.getWtgType());
            specificReport.setYearConstruction(updatedReport.getYearConstruction());
            specificReport.setProjectoId(updatedReport.getProjectoId());
            specificReport.setTurbinaId(updatedReport.getTurbinaId());
            specificReport.setTurbine(updatedReport.getTurbine());
            specificReport.setTypeReport(updatedReport.getTypeReport());
            specificReport.setReportType(updatedReport.getReportType());
            specificReport.setLocked(updatedReport.getLocked());
            specificReport.setPermission2Edit(updatedReport.getPermission2Edit());
            specificReport.setInsertImagesChk(updatedReport.getInsertImagesChk());
        }

        // Atualizar dados específicos
        specificReport.setReportNumber(specificData.getReportNumber());
        specificReport.setInpectorsWorkers(specificData.getInpectorsWorkers());
        specificReport.setStatementOfwork(specificData.getStatementOfwork());
        specificReport.setWorkCompleted(specificData.getWorkCompleted());
        specificReport.setTurbineOperable(specificData.getTurbineOperable());
        specificReport.setPlaceDate(specificData.getPlaceDate());
        specificReport.setResponsibleTechnician(specificData.getResponsibleTechnician());
        specificReport.setPerformanceReport(specificData.getPerformanceReport());
        specificReport.setModifiedDate(updatedReport.getModifiedDate());

        performanceReportRepairElevatorRepository.save(specificReport);
        logger.info("✅ Performance Report Repair Elevator specific data updated");

        return updatedReport;
    }

    /**
     * Atualizar relatório existente (método original - apenas Report)
     * DEPRECATED: Use updateComplete() para atualizar relatórios completos
     */
    @Transactional
    @Deprecated
    public Report update(Report report) {
        logger.warn("⚠️ Using deprecated update() method. Consider using updateComplete() instead.");
        logger.info("📝 Updating Performance Report Repair Elevator ID: {} (INCOMPLETE - only Report table)", report.getReportId());
        Report updated = reportRepository.save(report);
        logger.info("✅ Report updated");
        return updated;
    }

    /**
     * Obter relatório por ID
     */
    public Report getById(Integer id) {
        return reportRepository.findById(id)
                .filter(r -> r.getTypeReport() == REPORT_TYPE)
                .orElse(null);
    }

    /**
     * Obter relatório completo por ID (com dados específicos)
     */
    public PerformanceReportRepairElevator getCompleteById(Integer id) {
        return performanceReportRepairElevatorRepository.findById(id)
                .orElse(null);
    }

    /**
     * Obter relatório por UUID
     */
    public Report getByUuid(String uuid) {
        return reportRepository.findByUuid(uuid)
                .filter(r -> r.getTypeReport() == REPORT_TYPE)
                .orElse(null);
    }

    /**
     * Obter todos os relatórios de uma turbina
     */
    public List<Report> getByTurbineId(Integer turbineId) {
        return reportRepository.findByTurbinaId(turbineId).stream()
                .filter(r -> r.getTypeReport() == REPORT_TYPE)
                .collect(Collectors.toList());
    }

    /**
     * Obter todos os relatórios de um projeto
     */
    public List<Report> getByProjectId(Integer projectId) {
        return reportRepository.findByProjectoId(projectId).stream()
                .filter(r -> r.getTypeReport() == REPORT_TYPE)
                .collect(Collectors.toList());
    }

    /**
     * Eliminar relatório (elimina de ambas as tabelas devido ao cascade)
     */
    @Transactional
    public void delete(Integer id) {
        logger.info("🗑️ Deleting Performance Report Repair Elevator ID: {}", id);

        // Eliminar dados específicos primeiro
        if (performanceReportRepairElevatorRepository.existsById(id)) {
            performanceReportRepairElevatorRepository.deleteById(id);
            logger.info("✅ Specific data deleted");
        }

        // Depois eliminar o Report base
        reportRepository.deleteById(id);
        logger.info("✅ Report deleted");
    }

    /**
     * Classe auxiliar para transportar dados específicos do PerformanceReportRepairElevator
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
        public void setResponsibleTechnician(String responsibleTechnician) { this.responsibleTechnician = responsibleTechnician; }

        public String getPerformanceReport() { return performanceReport; }
        public void setPerformanceReport(String performanceReport) { this.performanceReport = performanceReport; }
    }
}