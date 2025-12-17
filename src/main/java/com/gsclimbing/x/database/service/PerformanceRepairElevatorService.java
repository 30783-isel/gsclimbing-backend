package com.gsclimbing.x.database.service;

import com.gsclimbing.database.entity.Report;
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
 * Reutiliza a mesma estrutura do DefectsInspectionReportService
 */
@Service
public class PerformanceRepairElevatorService {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceRepairElevatorService.class);
    private static final int REPORT_TYPE = 6; // Performance Report Repair Elevator

    @Autowired
    private ReportRepository reportRepository;

    /**
     * Criar novo relatório
     */
    @Transactional
    public Report create(Report report) {
        logger.info("📝 Creating Performance Report Repair Elevator");
        report.setTypeReport(REPORT_TYPE);
        Report saved = reportRepository.save(report);
        logger.info("✅ Report created with ID: {}", saved.getReportId());
        return saved;
    }

    /**
     * Atualizar relatório existente
     */
    @Transactional
    public Report update(Report report) {
        logger.info("📝 Updating Performance Report Repair Elevator ID: {}", report.getReportId());
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
     * Eliminar relatório
     */
    @Transactional
    public void delete(Integer id) {
        logger.info("🗑️ Deleting Performance Report Repair Elevator ID: {}", id);
        reportRepository.deleteById(id);
        logger.info("✅ Report deleted");
    }
}