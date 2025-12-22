package com.gsclimbing.x.controller;


import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.entity.Report;
import com.gsclimbing.x.dto.DefectInspectionReportDTO;
import com.gsclimbing.x.pdf.defectInspectionReport.DefectInspectionPdfService;
import com.gsclimbing.x.util.DefectInstectionReportUtils;
import com.lowagie.text.pdf.PdfReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*", methods = {
        RequestMethod.OPTIONS,
        RequestMethod.GET,
        RequestMethod.POST,
        RequestMethod.PUT,
        RequestMethod.DELETE
})
@Transactional
@RestController
@RequestMapping(path = "/api/reports/pdf")
public class PDFController {

    @Autowired
    private DefectInstectionReportUtils defectInstectionReportUtils;
    private static final Logger logger = LoggerFactory.getLogger(PDFController.class);
    @GetMapping("/generate-defect-inspection-report/{reportId}")
    public ResponseEntity<?> generateGefectInspectionReport(@PathVariable Integer reportId) {
        try {
            DefectInspectionReportDTO dto = defectInstectionReportUtils.getDefectInspectionReportDto(reportId);
            // Template via classpath
            ClassPathResource templateResource =
                    new ClassPathResource("templates/Defect Inspection Report.pdf");
            PdfReader reader;
            try (InputStream is = templateResource.getInputStream()) {
                reader = new PdfReader(is);
            }
            // Garantir que a pasta de output existe
            File outputDir = new File("output");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            try (FileOutputStream fos = new FileOutputStream(
                    "output/Defect Inspection Report-" + dto.getWtgNumber() + ".pdf")) {
                DefectInspectionPdfService pdfService = new DefectInspectionPdfService();
                pdfService.generate(dto, reader, fos);
            }
            logger.info("PDF gerado com sucesso em output/");
            return ResponseEntity.status(HttpStatus.CREATED).body("Defect Inspection Report Created");
        } catch (Exception e) {
            logger.error("❌ Error creating Defect Inspection Report PDF", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating Defect Inspection Report PDF: " + e.getMessage());
        }
    }

    public static DefectInspectionReportDTO buildSampleDto() {
        LocalDateTime now = LocalDateTime.now();
        DefectInspectionReportDTO dto = DefectInspectionReportDTO.builder()
                .site("Parque Eólico de Sines")
                .wtgNumber("WTG-07")
                .wtgType("Vestas V90")
                .yearConstruction("2018")
                .projectoId(101)
                .turbinaId(7)
                .userId("mobile-user-123")
                .reportType(0)
                // ---------- Estado ----------
                .status(Report.ReportStatus.DRAFT)
                .syncStatus(Report.SyncStatus.SYNCED)
                .offlineCreated(false)
                // ---------- Datas ----------
                .createdAt(now)
                .updatedAt(now)
                .submittedAt(now)
                .submittedBy("joao.silva")
                // ---------- Idioma ----------
                .language("EN")
                // ---------- Campos adicionais ----------
                .additionalField1(
                        DefectInspectionReportDTO.AdditionalFieldDTO.builder()
                                .label("Inspector")
                                .value("João Silva")
                                .build()
                )
                .additionalField2(
                        DefectInspectionReportDTO.AdditionalFieldDTO.builder()
                                .label("Empresa")
                                .value("GS Climbing")
                                .build()
                )
                .additionalField3(
                        DefectInspectionReportDTO.AdditionalFieldDTO.builder()
                                .label("Data da Inspeção")
                                .value(now.toLocalDate().toString())
                                .build()
                )
                .additionalField4(
                        DefectInspectionReportDTO.AdditionalFieldDTO.builder()
                                .label("Condições Meteorológicas")
                                .value("Vento moderado")
                                .build()
                )
                .additionalField5(
                        DefectInspectionReportDTO.AdditionalFieldDTO.builder()
                                .label("Acesso")
                                .value("Normal")
                                .build()
                )
                .additionalField6(
                        DefectInspectionReportDTO.AdditionalFieldDTO.builder()
                                .label("Urgência")
                                .value("Média")
                                .build()
                )
                .additionalField7(
                        DefectInspectionReportDTO.AdditionalFieldDTO.builder()
                                .label("Observações Gerais")
                                .value("Necessária intervenção nos próximos 30 dias")
                                .build()
                )

                .build();
        // ---------- Fotos ----------
        dto.setListaFileData(Arrays.asList(
                createFileData("foto1.jpg", "Corrosão na escada"),
                createFileData("foto2.jpg", "Desgaste no ponto de ancoragem"),
                createFileData("foto3.jpg", "Fixação danificada no patamar intermédio")
        ));
        dto.setPhotoFileIds(Arrays.asList(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
        ));
        return dto;
    }
    // ---------------------------------------
    // FileData mock (sem JPA)
    // ---------------------------------------
    private static FileData createFileData(String path, String description) {
        FileData f = new FileData();
        f.setUrl(path);
        f.setDescription(description);
        f.setCreateDate(LocalDateTime.now());
        f.setIsDeleted("N");
        f.setMimeType("image/jpeg");
        f.setName(path);
        return f;
    }
}