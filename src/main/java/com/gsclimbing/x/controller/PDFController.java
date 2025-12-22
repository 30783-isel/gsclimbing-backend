package com.gsclimbing.x.controller;


import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.entity.Report;
import com.gsclimbing.x.dto.DefectInspectionReportDTO;
import com.gsclimbing.x.pdf.defectInspectionReport.DefectInspectionPdfService;
import com.lowagie.text.pdf.PdfReader;
import org.springframework.core.io.ClassPathResource;
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

    @GetMapping("/generate-defect-inspection-report")
    public void generateGefectInspectionReport() {
        try {
            // DTO de exemplo
            DefectInspectionReportDTO dto = buildSampleDto();

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

            // Output stream para o PDF
            try (FileOutputStream fos = new FileOutputStream(
                    "output/report-" + dto.getWtgNumber() + ".pdf")) {

                // Aqui chamaria o serviço de geração, passando reader e fos
                DefectInspectionPdfService pdfService = new DefectInspectionPdfService();
                pdfService.generate(dto, reader, fos);

            }

            System.out.println("PDF gerado com sucesso em output/");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DefectInspectionReportDTO buildSampleDto() {
        LocalDateTime now = LocalDateTime.now();
        DefectInspectionReportDTO dto = DefectInspectionReportDTO.builder()
                // ---------- Dados base ----------
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

        // IDs das fotos (simulação mobile)
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
