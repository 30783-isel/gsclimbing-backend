package com.gsclimbing.x.controller;

import com.gsclimbing.database.entity.DefectsInspectionReport;
import com.gsclimbing.x.database.service.DefectsInspectionReportService;
import com.gsclimbing.x.adapter.DefectInspectionReportAdapter;
import com.gsclimbing.x.database.service.BaseReportService;
import com.gsclimbing.x.dto.MobileReportDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para Defect Inspection Reports
 * Estende BaseReportController para reutilizar lógica comum
 */
@CrossOrigin(origins = "*", methods = {
        RequestMethod.OPTIONS,
        RequestMethod.GET,
        RequestMethod.POST,
        RequestMethod.PUT,
        RequestMethod.DELETE
})
@Transactional
@RestController
@RequestMapping(path = "/api/reports/mobile")
public class DefectInspectionController extends BaseReportController<DefectsInspectionReport> {

    @Autowired
    private DefectsInspectionReportService defectsInspectionReportService;

    @Autowired
    private DefectInspectionReportAdapter defectInspectionReportAdapter;

    // ========================================
    // IMPLEMENTAÇÃO DOS MÉTODOS ABSTRATOS
    // ========================================

    @Override
    protected BaseReportService<DefectsInspectionReport> getReportService() {
        return defectsInspectionReportService;
    }

    @Override
    protected DefectInspectionReportAdapter getReportAdapter() {
        return defectInspectionReportAdapter;
    }

    @Override
    protected String getEndpointName() {
        return "defect-inspection";
    }

    @Override
    protected int getReportType() {
        return 0; // Defect Inspection Report
    }

    // ========================================
    // ENDPOINTS REST (delegam para BaseReportController)
    // ========================================

    /**
     * POST /api/reports/mobile/defect-inspection
     * Criar novo Defect Inspection Report
     */
    @PostMapping("/defect-inspection")
    public ResponseEntity<?> createDefectInspectionReport(
            @RequestBody MobileReportDTO.ReportCreateUpdateDTO dto) {

        logger.info("📋 POST /defect-inspection called");
        return create(dto); // Método da classe base
    }

    /**
     * GET /api/reports/mobile/defect-inspection/{id}
     * Obter Defect Inspection Report por ID
     */
    @GetMapping("/defect-inspection/{id}")
    public ResponseEntity<?> getDefectInspectionReport(@PathVariable Integer id) {

        logger.info("📖 GET /defect-inspection/{} called", id);
        return getById(id); // Método da classe base
    }

    /**
     * GET /api/reports/mobile/defect-inspection/turbine/{turbineId}
     * Obter todos os Defect Inspection Reports de uma turbina
     */
    @GetMapping("/defect-inspection/turbine/{turbineId}")
    public ResponseEntity<?> getDefectInspectionReportsByTurbine(@PathVariable Integer turbineId) {

        logger.info("📖 GET /defect-inspection/turbine/{} called", turbineId);
        return getByTurbine(turbineId); // Método da classe base
    }

    /**
     * GET /api/reports/mobile/defect-inspection/project/{projectId}
     * Obter todos os Defect Inspection Reports de um projeto
     */
    @GetMapping("/defect-inspection/project/{projectId}")
    public ResponseEntity<?> getDefectInspectionReportsByProject(@PathVariable Integer projectId) {

        logger.info("📖 GET /defect-inspection/project/{} called", projectId);
        return getByProject(projectId); // Método da classe base
    }

    /**
     * PUT /api/reports/mobile/defect-inspection/{id}
     * Atualizar Defect Inspection Report
     */
    @PutMapping("/defect-inspection/{id}")
    public ResponseEntity<?> updateDefectInspectionReport(
            @PathVariable Integer id,
            @RequestBody MobileReportDTO.ReportCreateUpdateDTO dto,
            Authentication authentication) {

        logger.info("🔧 PUT /defect-inspection/{} called", id);
        return update(id, dto, authentication); // Método da classe base
    }

    /**
     * DELETE /api/reports/mobile/defect-inspection/{id}
     * Eliminar Defect Inspection Report
     */
    @DeleteMapping("/defect-inspection/{id}")
    public ResponseEntity<?> deleteDefectInspectionReport(
            @PathVariable Integer id,
            Authentication authentication) {

        logger.info("🗑️ DELETE /defect-inspection/{} called", id);
        return delete(id, authentication); // Método da classe base
    }

    /**
     * GET /api/reports/mobile/defect-inspection/{id}/photos
     * Obter fotos de um Defect Inspection Report
     */
    @GetMapping("/defect-inspection/{id}/photos")
    public ResponseEntity<?> getDefectInspectionReportPhotos(@PathVariable Integer id) {

        logger.info("📸 GET /defect-inspection/{}/photos called", id);
        return getPhotos(id); // Método da classe base
    }

    /**
     * GET /api/reports/mobile/defect-inspection/{id}/history
     * Obter histórico de alterações de um Defect Inspection Report
     */
    @GetMapping("/defect-inspection/{id}/history")
    public ResponseEntity<?> getDefectInspectionReportHistory(@PathVariable Long id) {

        logger.info("📜 GET /defect-inspection/{}/history called", id);
        return getHistory(id); // Método da classe base
    }

    // ========================================
    // MÉTODOS ESPECÍFICOS (se necessário)
    // ========================================

    // Se precisares de lógica específica para Defect Inspection,
    // adiciona aqui. Caso contrário, tudo vem da classe base!
}