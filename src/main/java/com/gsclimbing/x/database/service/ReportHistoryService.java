package com.gsclimbing.x.database.service;

import com.gsclimbing.database.entity.Report;
import com.gsclimbing.x.database.entity.ReportHistory;
import com.gsclimbing.x.database.repository.ReportHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service para gerir histórico de alterações em relatórios
 */
@Service
public class ReportHistoryService {

    @Autowired
    private ReportHistoryRepository historyRepository;

    /**
     * Criar entrada de histórico
     */
    @Transactional
    public ReportHistory createHistoryEntry(
            Report report,
            String username,
            ReportHistory.HistoryAction action,
            String description
    ) {
        ReportHistory history = ReportHistory.builder()
                .report(report)
                .changedBy(username)
                .changedAt(LocalDateTime.now())
                .action(action)
                .description(description)
                .build();

        return historyRepository.save(history);
    }

    /**
     * Criar entrada de histórico com detalhes de campo alterado
     */
    @Transactional
    public ReportHistory createFieldChangeEntry(
            Report report,
            String username,
            String fieldName,
            String oldValue,
            String newValue
    ) {
        ReportHistory history = ReportHistory.builder()
                .report(report)
                .changedBy(username)
                .changedAt(LocalDateTime.now())
                .action(ReportHistory.HistoryAction.UPDATE)
                .fieldName(fieldName)
                .oldValue(oldValue)
                .newValue(newValue)
                .description("Campo '" + fieldName + "' alterado")
                .build();

        return historyRepository.save(history);
    }

    /**
     * Registar criação de relatório
     */
    @Transactional
    public void logCreate(Report report, String username) {
        createHistoryEntry(
                report,
                username,
                ReportHistory.HistoryAction.CREATE,
                "Relatório criado"
        );
    }

    /**
     * Registar submissão de relatório
     */
    @Transactional
    public void logSubmit(Report report, String username) {
        createHistoryEntry(
                report,
                username,
                ReportHistory.HistoryAction.SUBMIT,
                "Relatório submetido"
        );
    }

    /**
     * Registar pedido de desbloqueio
     */
    @Transactional
    public void logRequestUnlock(Report report, String username) {
        createHistoryEntry(
                report,
                username,
                ReportHistory.HistoryAction.REQUEST_UNLOCK,
                "Pedido de permissão para editar"
        );
    }

    /**
     * Registar desbloqueio por admin
     */
    @Transactional
    public void logUnlock(Report report, String adminUsername, String techUsername) {
        createHistoryEntry(
                report,
                adminUsername,
                ReportHistory.HistoryAction.UNLOCK,
                "Desbloqueado para edição por " + techUsername
        );
    }

    /**
     * Registar aprovação
     */
    @Transactional
    public void logApprove(Report report, String adminUsername) {
        createHistoryEntry(
                report,
                adminUsername,
                ReportHistory.HistoryAction.APPROVE,
                "Relatório aprovado"
        );
    }

    /**
     * Registar rejeição
     */
    @Transactional
    public void logReject(Report report, String adminUsername, String reason) {
        createHistoryEntry(
                report,
                adminUsername,
                ReportHistory.HistoryAction.REJECT,
                "Relatório rejeitado: " + reason
        );
    }

    /**
     * Registar eliminação de relatório
     */
    @Transactional
    public void logDelete(Report report, String username) {
        createHistoryEntry(
                report,
                username,
                ReportHistory.HistoryAction.DELETE,
                "Relatório eliminado"
        );
    }

    /**
     * Obter histórico de um relatório
     */
    public List<ReportHistory> getReportHistory(Long reportId) {
        return historyRepository.findByReportIdOrderByChangedAtDesc(reportId);
    }

    /**
     * Obter histórico de um relatório (por entidade)
     */
    public List<ReportHistory> getReportHistory(Report report) {
        return historyRepository.findByReportOrderByChangedAtDesc(report);
    }

    /**
     * Contar alterações de um relatório
     */
    public Long countChanges(Long reportId) {
        return historyRepository.countByReportId(reportId);
    }

    /**
     * Obter última alteração
     */
    public ReportHistory getLatestChange(Long reportId) {
        List<ReportHistory> history = historyRepository.findLatestByReportId(reportId);
        return history.isEmpty() ? null : history.get(0);
    }
}
