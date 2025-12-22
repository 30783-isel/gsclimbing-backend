package com.gsclimbing.x.pdf.defectInspectionReport;

import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.x.dto.DefectInspectionReportDTO;
import com.gsclimbing.x.pdf.defectInspectionReport.common.PhotoInserter;


import com.lowagie.text.pdf.*;

import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefectInspectionPdfService {

    private static final int MAX_PHOTOS = 8;

    public void generate(
            DefectInspectionReportDTO dto,
            PdfReader reader,
            OutputStream outputStream) throws Exception {

        // Criar PdfStamper com reader já existente
        PdfStamper stamper = new PdfStamper(reader, outputStream);

        // Preencher campos do PDF
        AcroFields fields = stamper.getAcroFields();
        DefectInspectionPdfMapper.mapFields(fields, dto);

        // Inserir fotos
        PhotoInserter.insertPhotos(
                stamper,
                mapPhotoPaths(dto.getListaFileData()),
                mapDescriptions(dto.getListaFileData())
        );

        // Flatten final do formulário
        stamper.setFormFlattening(true);

        // Fechar recursos
        stamper.close();
        reader.close();
    }

    public static List<String> mapPhotoPaths(List<FileData> files) {
        return files.stream()
                .filter(FileData::isActive)
                .filter(f -> f.getUrl() != null && !f.getUrl().isEmpty())
                .sorted(Comparator.comparing(FileData::getCreateDate))
                .limit(MAX_PHOTOS)
                .map(FileData::getUrl)
                .collect(Collectors.toList());
    }

    public static List<String> mapDescriptions(List<FileData> files) {
        return files.stream()
                .filter(FileData::isActive)
                .sorted(Comparator.comparing(FileData::getCreateDate))
                .limit(MAX_PHOTOS)
                .map(f -> f.getDescription() != null ? f.getDescription() : "")
                .collect(Collectors.toList());
    }
}
