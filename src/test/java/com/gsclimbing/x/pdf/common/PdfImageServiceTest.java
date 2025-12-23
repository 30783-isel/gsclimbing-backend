package com.gsclimbing.x.pdf.common;

import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.ftp.FTPDownloadFiles;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDPushButton;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para PdfImageService
 */
@ExtendWith(MockitoExtension.class)
class PdfImageServiceTest {

    @InjectMocks
    private PdfImageService pdfImageService;

    private FileData mockFileData;
    private BufferedImage mockImage;
    private byte[] mockImageBytes;

    @BeforeEach
    void setUp() throws IOException {
        // Criar FileData mock
        mockFileData = new FileData();
        mockFileData.setHash("test-hash-123");
        mockFileData.setName("test-image");
        mockFileData.setMimeType("JPG");

        // Criar imagem mock
        mockImage = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);

        // Criar bytes de imagem válidos
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        ImageIO.write(mockImage, "jpg", baos);
        mockImageBytes = baos.toByteArray();
    }

    @Test
    void testLoadImageFromFtp_Success() throws IOException {
        // Arrange
        try (MockedStatic<FTPDownloadFiles> ftpMock = mockStatic(FTPDownloadFiles.class)) {
            ftpMock.when(() -> FTPDownloadFiles.downloadFile2FTPServer(anyString()))
                   .thenReturn(mockImageBytes);

            // Act
            BufferedImage result = pdfImageService.loadImageFromFtp(mockFileData);

            // Assert
            assertNotNull(result);
            assertEquals(800, result.getWidth());
            assertEquals(600, result.getHeight());
        }
    }

    @Test
    void testLoadImageFromFtp_NullFileData() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            pdfImageService.loadImageFromFtp(null);
        });
    }

    @Test
    void testLoadImageFromFtp_EmptyBytes() {
        // Arrange
        try (MockedStatic<FTPDownloadFiles> ftpMock = mockStatic(FTPDownloadFiles.class)) {
            ftpMock.when(() -> FTPDownloadFiles.downloadFile2FTPServer(anyString()))
                   .thenReturn(new byte[0]);

            // Act & Assert
            assertThrows(IOException.class, () -> {
                pdfImageService.loadImageFromFtp(mockFileData);
            });
        }
    }

    @Test
    void testValidateImageDimensions_Valid() {
        // Arrange
        BufferedImage image = new BufferedImage(200, 150, BufferedImage.TYPE_INT_RGB);

        // Act
        boolean result = pdfImageService.validateImageDimensions(image, 100, 100);

        // Assert
        assertTrue(result);
    }

    @Test
    void testValidateImageDimensions_TooSmall() {
        // Arrange
        BufferedImage image = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);

        // Act
        boolean result = pdfImageService.validateImageDimensions(image, 100, 100);

        // Assert
        assertFalse(result);
    }

    @Test
    void testValidateImageDimensions_NullImage() {
        // Act
        boolean result = pdfImageService.validateImageDimensions(null, 100, 100);

        // Assert
        assertFalse(result);
    }

    @Test
    void testResizeImage_LargerImage() {
        // Arrange
        BufferedImage largeImage = new BufferedImage(2000, 1500, BufferedImage.TYPE_INT_RGB);

        // Act
        BufferedImage result = pdfImageService.resizeImage(largeImage, 1000, 1000);

        // Assert
        assertNotNull(result);
        assertTrue(result.getWidth() <= 1000);
        assertTrue(result.getHeight() <= 1000);
    }

    @Test
    void testResizeImage_SmallerImage() {
        // Arrange
        BufferedImage smallImage = new BufferedImage(500, 400, BufferedImage.TYPE_INT_RGB);

        // Act
        BufferedImage result = pdfImageService.resizeImage(smallImage, 1000, 1000);

        // Assert
        assertNotNull(result);
        assertEquals(500, result.getWidth());
        assertEquals(400, result.getHeight());
    }

    @Test
    void testResizeImage_NullImage() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            pdfImageService.resizeImage(null, 1000, 1000);
        });
    }

    @Test
    void testInsertImageInPushButton_Success() throws IOException {
        // Arrange
        PDDocument mockDocument = mock(PDDocument.class);
        PDPushButton mockButton = mock(PDPushButton.class);
        
        when(mockButton.getWidgets()).thenReturn(java.util.Collections.emptyList());

        // Act
        pdfImageService.insertImageInPushButton(mockDocument, mockButton, mockImage, "test-field");

        // Assert
        // Verificar que não lançou exceção
        verify(mockButton, times(1)).getWidgets();
    }

    @Test
    void testInsertImageInPushButton_NullDocument() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            pdfImageService.insertImageInPushButton(null, mock(PDPushButton.class), mockImage, "test");
        });
    }

    @Test
    void testInsertImageInPushButton_NullButton() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            pdfImageService.insertImageInPushButton(mock(PDDocument.class), null, mockImage, "test");
        });
    }

    @Test
    void testInsertImageInPushButton_NullImage() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            pdfImageService.insertImageInPushButton(
                mock(PDDocument.class), 
                mock(PDPushButton.class), 
                null, 
                "test"
            );
        });
    }
}
