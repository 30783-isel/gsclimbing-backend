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
 * Centraliza lógica comum de CRUD e operações
 *
 * @param <T> Tipo específico do relatório (extends Report)
 */
public abstract class BaseReportService<T extends Report> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected ReportRepository reportRepository;

    // ========================================
    // MÉTODOS ABSTRATOS (cada service implementa)
    // ========================================

    /**
     * Retorna o tipo numérico deste relatório (0-7)
     */
    protected abstract int getReportType();

    /**
     * Retorna o repository específico do tipo
     */
    protected abstract JpaRepository<T, Integer> getSpecificRepository();

    /**
     * Nome do relatório para logs
     */
    protected abstract String getReportName();

    // ========================================
    // MÉTODOS COMUNS (implementados aqui)
    // ========================================

    /**
     * Criar novo relatório
     */
    @Transactional
    public T create(T report) {
        logger.info("📝 Creating {} - START", getReportName());

        // Preencher campos base
        report.setUuid(UUID.randomUUID().toString());
        report.setCreateDate(LocalDateTime.now());
        report.setModifiedDate(LocalDateTime.now());
        report.setTypeReport(getReportType());
        report.setStatus(Report.ReportStatus.DRAFT);
        report.setSyncStatus(Report.SyncStatus.SYNCED);
        report.setOfflineCreated(false);

        // Salvar (com JOINED inheritance, salva em ambas as tabelas)
        T saved = getSpecificRepository().save(report);

        logger.info("✅ {} created successfully with ID: {}", getReportName(), saved.getReportId());
        return saved;
    }

    /**
     * Atualizar relatório existente
     */
    @Transactional
    public T update(Integer id, T updatedReport) {
        logger.info("🔧 Updating {} ID: {}", getReportName(), id);

        // Verificar se existe
        T existing = getById(id)
                .orElseThrow(() -> new RuntimeException(getReportName() + " not found: " + id));

        // Atualizar data de modificação
        updatedReport.setModifiedDate(LocalDateTime.now());
        updatedReport.setReportId(id); // Garantir que mantém o ID

        // Salvar
        T saved = getSpecificRepository().save(updatedReport);

        logger.info("✅ {} updated successfully", getReportName());
        return saved;
    }

    /**
     * Obter relatório por ID
     */
    public Optional<T> getById(Integer id) {
        logger.info("🔍 Fetching {} by ID: {}", getReportName(), id);
        return getSpecificRepository().findById(id);
    }

    /**
     * Obter todos os relatórios de uma turbina
     */
    public List<T> getByTurbineId(Integer turbineId) {
        logger.info("🔍 Fetching {} reports for turbine: {}", getReportName(), turbineId);

        // Buscar na tabela base Report filtrado por tipo e turbina
        List<Report> baseReports = reportRepository.findByTurbinaIdAndTypeReport(turbineId, getReportType());

        // Converter para tipo específico
        List<T> specificReports = baseReports.stream()
                .map(r -> getById(r.getReportId()).orElse(null))
                .filter(r -> r != null)
                .collect(Collectors.toList());

        logger.info("✅ Found {} {} reports", specificReports.size(), getReportName());
        return specificReports;
    }

    /**
     * Obter todos os relatórios de um projeto
     */
    public List<T> getByProjectId(Integer projectId) {
        logger.info("🔍 Fetching {} reports for project: {}", getReportName(), projectId);

        // Buscar na tabela base Report filtrado por tipo e projeto
        List<Report> baseReports = reportRepository.findByProjectoIdAndTypeReport(projectId, getReportType());

        // Converter para tipo específico
        List<T> specificReports = baseReports.stream()
                .map(r -> getById(r.getReportId()).orElse(null))
                .filter(r -> r != null)
                .collect(Collectors.toList());

        logger.info("✅ Found {} {} reports", specificReports.size(), getReportName());
        return specificReports;
    }

    /**
     * Eliminar relatório
     */
    @Transactional
    public void delete(Integer id) {
        logger.info("🗑️ Deleting {} ID: {}", getReportName(), id);

        if (!getSpecificRepository().existsById(id)) {
            throw new RuntimeException(getReportName() + " not found: " + id);
        }

        // Eliminar (com JOINED inheritance, elimina automaticamente da tabela pai)
        getSpecificRepository().deleteById(id);

        logger.info("✅ {} deleted successfully", getReportName());
    }

    /**
     * Verificar se já existe relatório para esta turbina (prevenir duplicados)
     */
    public boolean existsByTurbineId(Integer turbineId) {
        List<T> existing = getByTurbineId(turbineId);
        return !existing.isEmpty();
    }
}

