package com.gsclimbing.x.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsclimbing.database.entity.Report;
import com.gsclimbing.database.entity.Turbine;
import com.gsclimbing.x.dto.MobileReportDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Adapter base para converter DTOs mobile em entidades Report
 * Centraliza lógica comum de conversão
 *
 * @param <T> Tipo específico do relatório
 */
public abstract class BaseReportAdapter<T extends Report> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Converte DTO mobile completo para entidade
     * Cada adapter implementa isto
     */
    public abstract T toEntity(MobileReportDTO.ReportCreateUpdateDTO dto, Turbine turbine) throws Exception;

    /**
     * Preenche campos base comuns a todos os relatórios
     */
    protected T populateBaseFields(T report, MobileReportDTO.ReportCreateUpdateDTO dto, Turbine turbine) throws Exception {

        JsonNode reportData = objectMapper.readTree(dto.getReportData());

        // UUID e timestamps
        report.setUuid(UUID.randomUUID().toString());
        report.setCreateDate(LocalDateTime.now());
        report.setModifiedDate(LocalDateTime.now());

        report.setTypeReport(dto.getReportType());

        // Campos base comuns
        report.setSite(getStringValue(reportData, "site"));
        report.setWtgNumber(getStringValue(reportData, "wtgNumber"));
        report.setWtgType(getStringValue(reportData, "wtgType"));
        report.setYearConstruction(getStringValue(reportData, "yearConstruction"));

        // IDs
        report.setTurbinaId(dto.getTurbineId() != null ? dto.getTurbineId().intValue() : null);
        report.setProjectoId(reportData.has("projectoId") ? reportData.get("projectoId").asInt() : null);
        report.setTurbine(turbine);

        // Estado inicial
        report.setStatus(Report.ReportStatus.DRAFT);
        report.setLocked("N");
        report.setOfflineCreated(dto.getOfflineCreated() != null ? dto.getOfflineCreated() : false);

        // Idioma
        report.setLanguage(dto.getLanguage() != null ? dto.getLanguage() : "EN");

        return report;
    }

    /**
     * Helpers para extrair valores do JSON
     */
    protected String getStringValue(JsonNode node, String fieldName) {
        return node.has(fieldName) && !node.get(fieldName).isNull()
                ? node.get(fieldName).asText()
                : null;
    }

    protected Integer getIntegerValue(JsonNode node, String fieldName) {
        return node.has(fieldName) && !node.get(fieldName).isNull()
                ? node.get(fieldName).asInt()
                : null;
    }

    protected Boolean getBooleanValue(JsonNode node, String fieldName) {
        return node.has(fieldName) && !node.get(fieldName).isNull()
                ? node.get(fieldName).asBoolean()
                : null;
    }
}