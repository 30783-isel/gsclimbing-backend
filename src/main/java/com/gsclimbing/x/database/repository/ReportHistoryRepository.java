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
 * ✅ VERSÃO FINAL: Queries nativas + tipos corretos
 */
@Repository
public interface ReportHistoryRepository extends JpaRepository<ReportHistory, Long> {

    /**
     * Encontrar histórico de um relatório específico
     * Ordenado do mais recente para o mais antigo
     */
    List<ReportHistory> findByReportOrderByChangedAtDesc(Report report);

    /**
     * ✅ Encontrar histórico por ID do relatório (query nativa)
     * Evita problema de tipo entre Long (API) e Integer (Report.id)
     */
    @Query(value = "SELECT * FROM report_history WHERE report_id = :reportId ORDER BY changed_at DESC",
            nativeQuery = true)
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
     * ✅ Contar quantas alterações foram feitas num relatório (query nativa)
     */
    @Query(value = "SELECT COUNT(*) FROM report_history WHERE report_id = :reportId",
            nativeQuery = true)
    Long countByReportId(@Param("reportId") Long reportId);

    /**
     * ✅ Obter última alteração de um relatório (query nativa)
     * IMPORTANTE: Devolve List porque o Service faz .get(0)
     * Limita a 1 resultado para performance
     */
    @Query(value = "SELECT * FROM report_history WHERE report_id = :reportId ORDER BY changed_at DESC LIMIT 1",
            nativeQuery = true)
    List<ReportHistory> findLatestByReportId(@Param("reportId") Long reportId);
}