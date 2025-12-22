package com.gsclimbing.x.pdf.defectInspectionReport;

import com.gsclimbing.x.dto.DefectInspectionReportDTO;
import com.lowagie.text.pdf.AcroFields;

public class DefectInspectionPdfMapper {

    public static void mapFields(AcroFields fields, DefectInspectionReportDTO dto)
            throws Exception {

        fields.setField("site", dto.getSite());
        fields.setField("wtgNumber", dto.getWtgNumber());
        fields.setField("wtgType", dto.getWtgType());
        fields.setField("yearConstruction", dto.getYearConstruction());

        fields.setField("additionalField1Label", dto.getAdditionalField1().getLabel());
        fields.setField("additionalField1Text", dto.getAdditionalField1().getValue());

        fields.setField("additionalField2Label", dto.getAdditionalField2().getLabel());
        fields.setField("additionalField2Text", dto.getAdditionalField2().getValue());

        fields.setField("additionalField2Label", dto.getAdditionalField2().getLabel());
        fields.setField("additionalField2Text", dto.getAdditionalField2().getValue());

        fields.setField("additionalField3Label", dto.getAdditionalField3().getLabel());
        fields.setField("additionalField3Text", dto.getAdditionalField3().getValue());

        fields.setField("additionalField4Label", dto.getAdditionalField4().getLabel());
        fields.setField("additionalField4Text", dto.getAdditionalField4().getValue());

        fields.setField("additionalField5Label", dto.getAdditionalField5().getLabel());
        fields.setField("additionalField5Text", dto.getAdditionalField5().getValue());

        fields.setField("additionalField6Label", dto.getAdditionalField6().getLabel());
        fields.setField("additionalField6Text", dto.getAdditionalField6().getValue());

        fields.setField("additionalField7Label", dto.getAdditionalField7().getLabel());
        fields.setField("additionalField7Text", dto.getAdditionalField7().getValue());

        for (int i = 0; i < dto.getListaFileData().size(); i++) {
            fields.setField("description" + (i + 1),
                    dto.getListaFileData().get(i).getDescription());
        }

        // checkbox imagens
        fields.setField("insertImagesChk",
                dto.getListaFileData().isEmpty() ? "Off" : "Yes");
    }
}
