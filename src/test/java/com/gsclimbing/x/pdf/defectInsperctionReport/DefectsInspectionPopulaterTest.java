package com.gsclimbing.x.pdf.defectInsperctionReport;

import com.gsclimbing.database.entity.DefectsInspectionReport;
import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.service.FileService;
import com.gsclimbing.ftp.FTPDownloadFiles;
import com.gsclimbing.reports.service.PdfImageService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para DefectsInspectionPopulater
 */
@ExtendWith(MockitoExtension.class)
class DefectsInspectionPopulaterTest {

    @Mock
    private FileService fileService;

    @Mock
    private PdfImageService pdfImageService;

    @InjectMocks
    private DefectsInspectionPopulater populator;

    private DefectsInspectionReport mockReport;
    private InputStream mockPdfStream;

    @BeforeEach
    void setUp() throws IOException {
        // Criar relatório mock
        mockReport = new DefectsInspectionReport();
        mockReport.setUuid("test-uuid-123");
        mockReport.setSite("Test Site");
        mockReport.setWtgNumber("WTG-001");
        mockReport.setWtgType("Type A");
        mockReport.setYearConstruction("2024");
        mockReport.setInsertImagesChk("Yes");
        mockReport.setAdditionalField1Label("Label 1");
        mockReport.setAdditionalField1Text("Text 1");

        // Criar PDF mock simples
        byte[] mockPdfBytes = createMockPdfBytes();
        mockPdfStream = new ByteArrayInputStream(mockPdfBytes);
    }

    @Test
    void testGeneratePDF_Success() throws IOException {
        // Arrange
        try (MockedStatic<FTPDownloadFiles> ftpMock = mockStatic(FTPDownloadFiles.class)) {
            ftpMock.when(() -> FTPDownloadFiles.downloadPdfReportByTeamToTempFile(
                anyString(), anyString()
            )).thenReturn(mockPdfStream);

            when(fileService.readFile(anyString())).thenReturn(new ArrayList<>());

            // Act
            byte[] result = populator.generatePDF(mockReport);

            // Assert
            assertNotNull(result);
            assertTrue(result.length > 0);
        }
    }

    @Test
    void testGeneratePDF_WithImages() throws IOException {
        // Arrange
        List<FileData> mockImages = createMockImageFiles();
        
        try (MockedStatic<FTPDownloadFiles> ftpMock = mockStatic(FTPDownloadFiles.class)) {
            ftpMock.when(() -> FTPDownloadFiles.downloadPdfReportByTeamToTempFile(
                anyString(), anyString()
            )).thenReturn(mockPdfStream);
            
            ftpMock.when(() -> FTPDownloadFiles.downloadFile2FTPServer(anyString()))
                   .thenReturn(createMockImageBytes());

            when(fileService.readFile(anyString())).thenReturn(mockImages);
            
            doNothing().when(pdfImageService).insertImageInPushButton(
                any(), any(), any(), anyString()
            );

            // Act
            byte[] result = populator.generatePDF(mockReport);

            // Assert
            assertNotNull(result);
            assertTrue(result.length > 0);
            verify(fileService, times(1)).readFile(anyString());
        }
    }

    @Test
    void testGeneratePDF_NullReport() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            populator.generatePDF(null);
        });
    }

    @Test
    void testGeneratePDF_TemplateNotFound() {
        // Arrange
        try (MockedStatic<FTPDownloadFiles> ftpMock = mockStatic(FTPDownloadFiles.class)) {
            ftpMock.when(() -> FTPDownloadFiles.downloadPdfReportByTeamToTempFile(
                anyString(), anyString()
            )).thenReturn(null);

            // Act & Assert
            assertThrows(RuntimeException.class, () -> {
                populator.generatePDF(mockReport);
            });
        }
    }

    @Test
    void testLoadReportImages_FiltersCorrectly() {
        // Arrange
        List<FileData> allFiles = Arrays.asList(
            createFileData("image1.jpg", "JPG"),
            createFileData("image2.png", "PNG"),
            createFileData("document.pdf", "PDF"),
            createFileData("image3.jpeg", "JPEG")
        );

        when(fileService.readFile(anyString())).thenReturn(allFiles);

        // Act
        List<FileData> result = populator.loadReportImages(mockReport);

        // Assert
        assertEquals(3, result.size()); // Apenas imagens (JPG, PNG, JPEG)
    }

    @Test
    void testLoadReportImages_EmptyList() {
        // Arrange
        when(fileService.readFile(anyString())).thenReturn(new ArrayList<>());

        // Act
        List<FileData> result = populator.loadReportImages(mockReport);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testIsImageFile_ValidFormats() {
        // Arrange & Act & Assert
        assertTrue(populator.isImageFile(createFileData("test", "JPG")));
        assertTrue(populator.isImageFile(createFileData("test", "JPEG")));
        assertTrue(populator.isImageFile(createFileData("test", "PNG")));
        assertTrue(populator.isImageFile(createFileData("test", "IMAGE/JPEG")));
        assertTrue(populator.isImageFile(createFileData("test", "IMAGE/PNG")));
    }

    @Test
    void testIsImageFile_InvalidFormats() {
        // Arrange & Act & Assert
        assertFalse(populator.isImageFile(createFileData("test", "PDF")));
        assertFalse(populator.isImageFile(createFileData("test", "DOC")));
        assertFalse(populator.isImageFile(createFileData("test", "TXT")));
        assertFalse(populator.isImageFile(createFileData("test", null)));
        assertFalse(populator.isImageFile(null));
    }

    // ====================================================================
    // Helper Methods
    // ====================================================================

    private byte[] createMockPdfBytes() throws IOException {
        // Criar um PDF mínimo válido
        PDDocument doc = new PDDocument();
        doc.addPage(new org.apache.pdfbox.pdmodel.PDPage());
        
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        doc.save(baos);
        doc.close();
        
        return baos.toByteArray();
    }

    private List<FileData> createMockImageFiles() {
        List<FileData> files = new ArrayList<>();
        
        FileData file1 = new FileData();
        file1.setHash("hash1");
        file1.setName("image1");
        file1.setMimeType("JPG");
        file1.setDescription("Description 1");
        files.add(file1);

        FileData file2 = new FileData();
        file2.setHash("hash2");
        file2.setName("image2");
        file2.setMimeType("JPG");
        file2.setDescription("Description 2");
        files.add(file2);

        return files;
    }

    private byte[] createMockImageBytes() throws IOException {
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(
            100, 100, java.awt.image.BufferedImage.TYPE_INT_RGB
        );
        
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        javax.imageio.ImageIO.write(img, "jpg", baos);
        
        return baos.toByteArray();
    }

    private FileData createFileData(String name, String mimeType) {
        FileData file = new FileData();
        file.setName(name);
        file.setMimeType(mimeType);
        file.setHash("hash-" + name);
        return file;
    }
}
