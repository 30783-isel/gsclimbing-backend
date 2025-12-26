package com.gsclimbing.x.database.service;

import com.gsclimbing.database.entity.Report;
import com.gsclimbing.database.repository.ReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service base genérico para todos os tipos de relatórios
 */
public abstract class BaseReportService<T extends Report> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected ReportRepository reportRepository;

    // ========================================
    // MÉTODOS ABSTRATOS
    // ========================================

    protected abstract int getReportType();

    /**
     * ✅ CORRIGIDO: Retorna o repository específico (sem type checking)
     */
    protected abstract JpaRepository<T, Integer> getSpecificRepository();

    protected abstract String getReportName();

    // ========================================
    // MÉTODOS COMUNS
    // ========================================

    @Transactional
    public T create(T report) {
        logger.info("📝 Creating {} - START", getReportName());

        report.setUuid(UUID.randomUUID().toString());
        report.setCreateDate(LocalDateTime.now());
        report.setModifiedDate(LocalDateTime.now());
        report.setTypeReport(getReportType());
        report.setStatus(Report.ReportStatus.DRAFT);
        report.setSyncStatus(Report.SyncStatus.SYNCED);
        report.setOfflineCreated(false);

        T saved = getSpecificRepository().save(report);

        logger.info("✅ {} created successfully with ID: {}", getReportName(), saved.getReportId());
        return saved;
    }

    @Transactional
    public T update(Integer id, T updatedReport) {
        logger.info("🔧 Updating {} ID: {}", getReportName(), id);

        T existing = getById(id)
                .orElseThrow(() -> new RuntimeException(getReportName() + " not found: " + id));

        updatedReport.setModifiedDate(LocalDateTime.now());
        updatedReport.setReportId(id);

        T saved = getSpecificRepository().save(updatedReport);

        logger.info("✅ {} updated successfully", getReportName());
        return saved;
    }

    public Optional<T> getById(Integer id) {
        logger.info("🔍 Fetching {} by ID: {}", getReportName(), id);
        return getSpecificRepository().findById(id);
    }

    public List<T> getByTurbineId(Integer turbineId) {
        logger.info("🔍 Fetching {} reports for turbine: {}", getReportName(), turbineId);

        List<Report> baseReports = reportRepository.findByTurbinaIdAndTypeReport(turbineId, getReportType());

        List<T> specificReports = baseReports.stream()
                .map(r -> getById(r.getReportId()).orElse(null))
                .filter(r -> r != null)
                .collect(Collectors.toList());

        logger.info("✅ Found {} {} reports", specificReports.size(), getReportName());
        return specificReports;
    }

    public List<T> getByProjectId(Integer projectId) {
        logger.info("🔍 Fetching {} reports for project: {}", getReportName(), projectId);

        List<Report> baseReports = reportRepository.findByProjectoIdAndTypeReport(projectId, getReportType());

        List<T> specificReports = baseReports.stream()
                .map(r -> getById(r.getReportId()).orElse(null))
                .filter(r -> r != null)
                .collect(Collectors.toList());

        logger.info("✅ Found {} {} reports", specificReports.size(), getReportName());
        return specificReports;
    }

    @Transactional
    public void delete(Integer id) {
        logger.info("🗑️ Deleting {} ID: {}", getReportName(), id);

        if (!getSpecificRepository().existsById(id)) {
            throw new RuntimeException(getReportName() + " not found: " + id);
        }

        getSpecificRepository().deleteById(id);

        logger.info("✅ {} deleted successfully", getReportName());
    }

    public boolean existsByTurbineId(Integer turbineId) {
        List<T> existing = getByTurbineId(turbineId);
        return !existing.isEmpty();
    }
}