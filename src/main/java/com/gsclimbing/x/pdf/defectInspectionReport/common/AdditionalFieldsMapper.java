package com.gsclimbing.x.pdf.defectInspectionReport.common;

import com.gsclimbing.x.dto.DefectInspectionReportDTO;
import com.lowagie.text.pdf.AcroFields;

import java.util.List;
import java.util.stream.IntStream;

public class AdditionalFieldsMapper {

    private static final int MAX_FIELDS = 7;

    public static void mapAdditionalFields(
            AcroFields fields,
            List<DefectInspectionReportDTO.AdditionalFieldDTO> additionalFields) throws Exception {

        if (additionalFields == null || additionalFields.isEmpty()) {
            return;
        }

        IntStream.range(0, Math.min(additionalFields.size(), MAX_FIELDS))
                .forEach(i -> {
                    DefectInspectionReportDTO.AdditionalFieldDTO field = additionalFields.get(i);
                    if (field == null) return;

                    try {
                        int index = i + 1;

                        if (field.getLabel() != null) {
                            fields.setField(
                                    "additionalField" + index + "Label",
                                    field.getLabel()
                            );
                        }

                        if (field.getValue() != null) {
                            fields.setField(
                                    "additionalField" + index + "Text",
                                    field.getValue()
                            );
                        }

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
