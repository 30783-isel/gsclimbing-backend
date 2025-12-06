package com.gsclimbing.x.database.repository;

import com.gsclimbing.database.entity.Report;
import com.gsclimbing.x.database.entity.ReportHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório para ReportHistory
 */
@Repository
public interface ReportHistoryRepository extends JpaRepository<ReportHistory, Long> {

    /**
     * Encontrar histórico de um relatório específico
     * Ordenado do mais recente para o mais antigo
     */
    List<ReportHistory> findByReportOrderByChangedAtDesc(Report report);

    /**
     * Encontrar histórico por ID do relatório
     */
    @Query("SELECT h FROM ReportHistory h WHERE h.report.id = :reportId ORDER BY h.changedAt DESC")
    List<ReportHistory> findByReportIdOrderByChangedAtDesc(@Param("reportId") Long reportId);

    /**
     * Encontrar histórico por utilizador
     */
    List<ReportHistory> findByChangedByOrderByChangedAtDesc(String changedBy);

    /**
     * Encontrar histórico por ação
     */
    List<ReportHistory> findByActionOrderByChangedAtDesc(ReportHistory.HistoryAction action);

    /**
     * Contar quantas alterações foram feitas num relatório
     */
    @Query("SELECT COUNT(h) FROM ReportHistory h WHERE h.report.id = :reportId")
    Long countByReportId(@Param("reportId") Long reportId);

    /**
     * Obter última alteração de um relatório
     */
    @Query("SELECT h FROM ReportHistory h WHERE h.report.id = :reportId ORDER BY h.changedAt DESC")
    List<ReportHistory> findLatestByReportId(@Param("reportId") Long reportId);
}
