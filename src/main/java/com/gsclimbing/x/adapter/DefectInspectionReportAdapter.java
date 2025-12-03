package com.gsclimbing.x.adapter;

import com.gsclimbing.database.entity.DefectsInspectionReport;
import com.gsclimbing.database.entity.Turbine;
import com.gsclimbing.x.dto.DefectInspectionReportDTO;
import com.gsclimbing.x.dto.DefectInspectionReportResponseDTO;
import com.gsclimbing.x.dto.DefectInspectionReportResponseDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Adapter para converter DTOs em entidades e vice-versa
 */
@Component
public class DefectInspectionReportAdapter {

    /**
     * Converte DTO do mobile para entidade DefectsInspectionReport
     *
     * @param dto Dados vindos do mobile
     * @param turbine Turbina associada
     * @return Entidade pronta para salvar
     */
    public DefectsInspectionReport toEntity(DefectInspectionReportDTO dto, Turbine turbine) {
        DefectsInspectionReport report = new DefectsInspectionReport();

        // Gerar UUID único
        report.setUuid(UUID.randomUUID().toString());

        // Datas
        LocalDateTime now = LocalDateTime.now();
        report.setCreateDate(now);
        report.setModifiedDate(now);

        // Informações básicas do relatório
        report.setSite(dto.getSite());
        report.setWtgNumber(dto.getWtgNumber());
        report.setWtgType(dto.getWtgType());
        report.setYearConstruction(dto.getYearConstruction());

        // IDs
        report.setProjectoId(dto.getProjectoId());
        report.setTurbinaId(dto.getTurbinaId());
        report.setTurbine(turbine);

        // Tipo de relatório (0 = Defect Inspection Report)
        report.setTypeReport(0);

        // Flags de controlo
        report.setLocked("N"); // Não bloqueado por padrão
        report.setPermission2Edit("Y"); // Pode editar por padrão
        report.setInsertImagesChk("Y"); // Tem imagens

        // Campos adicionais
        if (dto.getAdditionalField1() != null) {
            report.setAdditionalField1Label(dto.getAdditionalField1().getLabel());
            report.setAdditionalField1Text(dto.getAdditionalField1().getValue());
        }

        if (dto.getAdditionalField2() != null) {
            report.setAdditionalField2Label(dto.getAdditionalField2().getLabel());
            report.setAdditionalField2Text(dto.getAdditionalField2().getValue());
        }

        if (dto.getAdditionalField3() != null) {
            report.setAdditionalField3Label(dto.getAdditionalField3().getLabel());
            report.setAdditionalField3Text(dto.getAdditionalField3().getValue());
        }

        if (dto.getAdditionalField4() != null) {
            report.setAdditionalField4Label(dto.getAdditionalField4().getLabel());
            report.setAdditionalField4Text(dto.getAdditionalField4().getValue());
        }

        if (dto.getAdditionalField5() != null) {
            report.setAdditionalField5Label(dto.getAdditionalField5().getLabel());
            report.setAdditionalField5Text(dto.getAdditionalField5().getValue());
        }

        if (dto.getAdditionalField6() != null) {
            report.setAdditionalField6Label(dto.getAdditionalField6().getLabel());
            report.setAdditionalField6Text(dto.getAdditionalField6().getValue());
        }

        if (dto.getAdditionalField7() != null) {
            report.setAdditionalField7Label(dto.getAdditionalField7().getLabel());
            report.setAdditionalField7Text(dto.getAdditionalField7().getValue());
        }

        return report;
    }

    /**
     * Converte entidade para DTO de resposta
     *
     * @param report Entidade salva
     * @param numberPictures Número de fotos associadas
     * @return DTO de resposta para o mobile
     */
    public DefectInspectionReportResponseDTO toResponseDTO(
            DefectsInspectionReport report,
            Integer numberPictures) {

        return DefectInspectionReportResponseDTO.builder()
                .reportId(report.getReportId())
                .uuid(report.getUuid())
                .createDate(report.getCreateDate())
                .site(report.getSite())
                .wtgNumber(report.getWtgNumber())
                .wtgType(report.getWtgType())
                .yearConstruction(report.getYearConstruction())
                .projectoId(report.getProjectoId())
                .turbinaId(report.getTurbinaId())
                .numberPictures(numberPictures)
                .success(true)
                .message("Report created successfully")
                .build();
    }
}