package com.gsclimbing.x.database.service;

import com.gsclimbing.database.entity.Report;
import com.gsclimbing.database.repository.ReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    // MÉTODOS ABSTRATOS - Delegam para repository específico
    // ========================================

    protected abstract int getReportType();
    protected abstract String getReportName();

    // Métodos de acesso ao repository específico
    protected abstract Optional<T> findById(Integer id);
    protected abstract T save(T entity);
    protected abstract void deleteById(Integer id);
    protected abstract List<T> findAll();

    // ========================================
    // MÉTODOS COMUNS
    // ========================================

    @Transactional
    public T create(T report) {
        logger.info("📝 Creating {} - START", getReportName());

        logger.info("🔍 TypeReport BEFORE create: {}", report.getReportType());

        report.setUuid(UUID.randomUUID().toString());
        report.setCreateDate(LocalDateTime.now());
        report.setModifiedDate(LocalDateTime.now());

        if (report.getReportType() == null) {
            logger.warn("⚠️ TypeReport is NULL! Setting it now to: {}", getReportType());
            report.setReportType(getReportType());
        }

        logger.info("🔍 TypeReport AFTER setting: {}", report.getReportType());

        // ✅ ADICIONAR ESTES LOGS
        logger.info("🔍 DEBUG CREATE: turbinaId={}, projectoId={}, typeReport={}",
                report.getTurbinaId(),
                report.getProjectoId(),
                report.getReportType());

        report.setStatus(Report.ReportStatus.DRAFT);
        report.setSyncStatus(Report.SyncStatus.SYNCED);
        report.setOfflineCreated(false);

        T saved = save(report);

        // ✅ ADICIONAR ESTE LOG
        logger.info("✅ SAVED TO DB: reportId={}, turbinaId={}, typeReport={}",
                saved.getReportId(),
                saved.getTurbinaId(),
                saved.getTypeReport());

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

        T saved = save(updatedReport);

        logger.info("✅ {} updated successfully", getReportName());
        return saved;
    }

    public Optional<T> getById(Integer id) {
        logger.info("🔍 Fetching {} by ID: {}", getReportName(), id);
        return findById(id);
    }

    public List<T> getByTurbineId(Integer turbineId) {
        logger.info("🔍 Fetching {} reports for turbine: {}", getReportName(), turbineId);

        List<Report> baseReports = reportRepository.findByTurbinaIdAndTypeReport(turbineId, getReportType());

        List<T> typedReports = baseReports.stream()
                .map(report -> {
                    Optional<T> typedReport = findById(report.getReportId());
                    return typedReport.orElse(null);
                })
                .filter(report -> report != null)
                .collect(Collectors.toList());

        logger.info("✅ Found {} {} reports for turbine {}", typedReports.size(), getReportName(), turbineId);
        return typedReports;
    }

    public List<T> getByProjectId(Integer projectId) {
        logger.info("🔍 Fetching {} reports for project: {}", getReportName(), projectId);

        List<Report> baseReports = reportRepository.findByProjectoIdAndTypeReport(projectId, getReportType());

        List<T> typedReports = baseReports.stream()
                .map(report -> {
                    Optional<T> typedReport = findById(report.getReportId());
                    return typedReport.orElse(null);
                })
                .filter(report -> report != null)
                .collect(Collectors.toList());

        logger.info("✅ Found {} {} reports", typedReports.size(), getReportName());
        return typedReports;
    }

    @Transactional
    public void delete(Integer id) {
        logger.info("🗑️ Deleting {} ID: {}", getReportName(), id);
        deleteById(id);
        logger.info("✅ {} deleted successfully", getReportName());
    }

    public List<T> getAll() {
        logger.info("🔍 Fetching all {}", getReportName());
        return findAll();
    }
}