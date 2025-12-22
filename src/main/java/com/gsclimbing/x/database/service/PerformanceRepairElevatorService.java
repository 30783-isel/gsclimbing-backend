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
 * ✅ CORRIGIDO: Service para Performance Report Repair Elevator
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
     * ✅ CORRIGIDO: Criar novo relatório completo (Report + PerformanceReportRepairElevator)
     *
     * MUDANÇA PRINCIPAL: Recebe PerformanceReportRepairElevator e salva UMA ÚNICA VEZ
     *
     * @param report PerformanceReportRepairElevator já criado pelo adapter
     * @param specificData Dados específicos do PerformanceReportRepairElevator
     * @return Report criado com ID gerado
     */
    @Transactional
    public Report createComplete(PerformanceReportRepairElevator report, PerformanceReportRepairElevatorSpecificData specificData) {
        logger.info("═══════════════════════════════════════════════════════");
        logger.info("🔧 createComplete() called");
        logger.info("🔧 Thread: {}", Thread.currentThread().getName());
        logger.info("📝 Creating Performance Report Repair Elevator (COMPLETE)");

        // ✅ CORREÇÃO: Não criar nova instância!
        // O report já vem como PerformanceReportRepairElevator do adapter

        // 1. Preencher dados específicos diretamente no objeto recebido
        report.setReportNumber(specificData.getReportNumber());
        report.setInpectorsWorkers(specificData.getInpectorsWorkers());
        report.setStatementOfwork(specificData.getStatementOfwork());
        report.setWorkCompleted(specificData.getWorkCompleted());
        report.setTurbineOperable(specificData.getTurbineOperable());
        report.setPlaceDate(specificData.getPlaceDate());
        report.setResponsibleTechnician(specificData.getResponsibleTechnician());
        report.setPerformanceReport(specificData.getPerformanceReport());

        // 2. Definir tipo de relatório
        report.setTypeReport(REPORT_TYPE);

        // 3. ✅ SALVAR UMA ÚNICA VEZ
        // Com JOINED inheritance, isto cria registos em AMBAS as tabelas (report e performance_report_repair_elevator)
        logger.info("💾 About to save PerformanceReportRepairElevator to database...");
        PerformanceReportRepairElevator savedReport = performanceReportRepairElevatorRepository.save(report);

        logger.info("✅ Report base created with ID: {}", savedReport.getReportId());
        logger.info("✅ Performance Report Repair Elevator specific data created with SAME ID: {}", savedReport.getReportId());
        logger.info("═══════════════════════════════════════════════════════");

        return savedReport;
    }

    /**
     * ✅ CORRIGIDO: Atualizar relatório existente completo
     *
     * @param report Entidade Report com dados genéricos atualizados
     * @param specificData Dados específicos atualizados
     * @return Report atualizado
     */
    @Transactional
    public Report updateComplete(Report report, PerformanceReportRepairElevatorSpecificData specificData) {
        logger.info("═══════════════════════════════════════════════════════");
        logger.info("🔧 updateComplete() called for Report ID: {}", report.getReportId());
        logger.info("🔧 Thread: {}", Thread.currentThread().getName());
        logger.info("📝 Updating Performance Report Repair Elevator ID: {} (COMPLETE)", report.getReportId());

        // 1. Buscar a entidade específica existente
        logger.info("🔍 Looking for specific data with ID: {}", report.getReportId());
        PerformanceReportRepairElevator specificReport = performanceReportRepairElevatorRepository
                .findById(report.getReportId())
                .orElse(null);

        if (specificReport == null) {
            logger.error("❌ ERRO CRÍTICO: Specific data not found for Report ID: {}", report.getReportId());
            throw new RuntimeException("PerformanceReportRepairElevator not found for ID: " + report.getReportId());
        }

        logger.info("✅ Found existing PerformanceReportRepairElevator");

        // 2. Atualizar campos básicos (do Report pai)
        specificReport.setSite(report.getSite());
        specificReport.setWtgNumber(report.getWtgNumber());
        specificReport.setWtgType(report.getWtgType());
        specificReport.setYearConstruction(report.getYearConstruction());
        specificReport.setModifiedDate(report.getModifiedDate());
        specificReport.setLocked(report.getLocked());
        specificReport.setPermission2Edit(report.getPermission2Edit());
        specificReport.setInsertImagesChk(report.getInsertImagesChk());

        // Copiar campos adicionais
        specificReport.setAdditionalField1Label(report.getAdditionalField1Label());
        specificReport.setAdditionalField1Text(report.getAdditionalField1Text());
        specificReport.setAdditionalField2Label(report.getAdditionalField2Label());
        specificReport.setAdditionalField2Text(report.getAdditionalField2Text());
        specificReport.setAdditionalField3Label(report.getAdditionalField3Label());
        specificReport.setAdditionalField3Text(report.getAdditionalField3Text());
        specificReport.setAdditionalField4Label(report.getAdditionalField4Label());
        specificReport.setAdditionalField4Text(report.getAdditionalField4Text());
        specificReport.setAdditionalField5Label(report.getAdditionalField5Label());
        specificReport.setAdditionalField5Text(report.getAdditionalField5Text());
        specificReport.setAdditionalField6Label(report.getAdditionalField6Label());
        specificReport.setAdditionalField6Text(report.getAdditionalField6Text());
        specificReport.setAdditionalField7Label(report.getAdditionalField7Label());
        specificReport.setAdditionalField7Text(report.getAdditionalField7Text());

        // 3. Atualizar dados específicos
        specificReport.setReportNumber(specificData.getReportNumber());
        specificReport.setInpectorsWorkers(specificData.getInpectorsWorkers());
        specificReport.setStatementOfwork(specificData.getStatementOfwork());
        specificReport.setWorkCompleted(specificData.getWorkCompleted());
        specificReport.setTurbineOperable(specificData.getTurbineOperable());
        specificReport.setPlaceDate(specificData.getPlaceDate());
        specificReport.setResponsibleTechnician(specificData.getResponsibleTechnician());
        specificReport.setPerformanceReport(specificData.getPerformanceReport());

        // 4. Salvar
        logger.info("💾 About to save/update PerformanceReportRepairElevator...");
        PerformanceReportRepairElevator updated = performanceReportRepairElevatorRepository.save(specificReport);
        logger.info("✅ Performance Report Repair Elevator updated successfully");
        logger.info("═══════════════════════════════════════════════════════");

        return updated;
    }

    /**
     * Obter relatório por ID (retorna Report base)
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

        try {
            // ✅ CORREÇÃO: Eliminar diretamente pela tabela específica
            // Com JOINED inheritance, isto elimina automaticamente da tabela pai (report) também

            // Verificar se existe
            if (!performanceReportRepairElevatorRepository.existsById(id)) {
                logger.error("❌ Performance Report Repair Elevator não encontrado: {}", id);
                throw new RuntimeException("Performance Report Repair Elevator not found with ID: " + id);
            }

            // Eliminar (com cascade, elimina também da tabela report)
            performanceReportRepairElevatorRepository.deleteById(id);
            logger.info("✅ Performance Report Repair Elevator deleted successfully");

        } catch (Exception e) {
            logger.error("❌ Error deleting Performance Report Repair Elevator: {}", id, e);
            throw new RuntimeException("Error deleting report: " + e.getMessage(), e);
        }
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
        public String getReportNumber() {
            return reportNumber;
        }

        public void setReportNumber(String reportNumber) {
            this.reportNumber = reportNumber;
        }

        public String getInpectorsWorkers() {
            return inpectorsWorkers;
        }

        public void setInpectorsWorkers(String inpectorsWorkers) {
            this.inpectorsWorkers = inpectorsWorkers;
        }

        public String getStatementOfwork() {
            return statementOfwork;
        }

        public void setStatementOfwork(String statementOfwork) {
            this.statementOfwork = statementOfwork;
        }

        public String getWorkCompleted() {
            return workCompleted;
        }

        public void setWorkCompleted(String workCompleted) {
            this.workCompleted = workCompleted;
        }

        public String getTurbineOperable() {
            return turbineOperable;
        }

        public void setTurbineOperable(String turbineOperable) {
            this.turbineOperable = turbineOperable;
        }

        public String getPlaceDate() {
            return placeDate;
        }

        public void setPlaceDate(String placeDate) {
            this.placeDate = placeDate;
        }

        public String getResponsibleTechnician() {
            return responsibleTechnician;
        }

        public void setResponsibleTechnician(String responsibleTechnician) {
            this.responsibleTechnician = responsibleTechnician;
        }

        public String getPerformanceReport() {
            return performanceReport;
        }

        public void setPerformanceReport(String performanceReport) {
            this.performanceReport = performanceReport;
        }
    }
}