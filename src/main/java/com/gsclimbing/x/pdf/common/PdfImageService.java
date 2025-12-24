package com.gsclimbing.x.pdf.common;

import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.ftp.FTPDownloadFiles;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.pdmodel.interactive.form.PDPushButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Serviço responsável pela gestão de imagens em PDFs.
 * Fornece funcionalidades para carregar, processar e inserir imagens em campos PDF.
 */
@Service
public class PdfImageService {

    private static final Logger logger = LoggerFactory.getLogger(PdfImageService.class);
    private static final String DEFAULT_IMAGE_FORMAT = "jpg";
    private static final int DEFAULT_IMAGE_QUALITY = 85;

    /**
     * Carrega uma imagem do servidor FTP e converte para BufferedImage
     *
     * @param fileData Dados do ficheiro a carregar
     * @return BufferedImage da imagem carregada
     * @throws IOException se ocorrer erro ao carregar ou processar a imagem
     */
    public BufferedImage loadImageFromFtp(FileData fileData) throws IOException {
        if (fileData == null) {
            throw new IllegalArgumentException("FileData cannot be null");
        }

        logger.debug("Loading image from FTP: hash={}, name={}", fileData.getHash(), fileData.getName());

        byte[] imageBytes = FTPDownloadFiles.downloadFile2FTPServer(fileData.getHash());
        
        if (imageBytes == null || imageBytes.length == 0) {
            throw new IOException("Failed to download image or empty file: " + fileData.getHash());
        }

        try (InputStream imageStream = new ByteArrayInputStream(imageBytes)) {
            BufferedImage image = ImageIO.read(imageStream);
            
            if (image == null) {
                throw new IOException("Failed to decode image: " + fileData.getName());
            }
            
            logger.debug("Image loaded successfully: {}x{}", image.getWidth(), image.getHeight());
            return image;
        }
    }

    /**
     * Insere uma imagem num campo PDF do tipo PushButton
     *
     * @param pdfDocument Documento PDF onde inserir a imagem
     * @param pushButton Campo do tipo PushButton onde inserir a imagem
     * @param image Imagem a inserir
     * @param imageName Nome da imagem (para logging)
     * @throws IOException se ocorrer erro ao inserir a imagem
     */
    public void insertImageInPushButton(PDDocument pdfDocument, PDPushButton pushButton, 
                                       BufferedImage image, String imageName) throws IOException {
        
        if (pdfDocument == null || pushButton == null || image == null) {
            throw new IllegalArgumentException("PDF document, push button, and image cannot be null");
        }

        logger.debug("Inserting image '{}' into push button field", imageName);

        List<PDAnnotationWidget> widgets = pushButton.getWidgets();
        
        if (widgets == null || widgets.isEmpty()) {
            logger.warn("No widgets found for push button field: {}", imageName);
            return;
        }

        PDAnnotationWidget widget = widgets.get(0);
        
        // Converter BufferedImage para bytes
        byte[] imageBytes = convertImageToBytes(image);
        
        // Criar PDImageXObject
        PDImageXObject pdImage = PDImageXObject.createFromByteArray(pdfDocument, imageBytes, imageName);
        
        // Calcular proporções
        float imageAspectRatio = (float) pdImage.getHeight() / (float) pdImage.getWidth();
        PDRectangle buttonRect = getWidgetRectangle(widget);
        
        // Criar appearance stream
        createImageAppearanceStream(pdfDocument, widget, pdImage, buttonRect, imageAspectRatio);
        
        logger.debug("Image inserted successfully: {}", imageName);
    }

    /**
     * Converte BufferedImage para array de bytes
     */
    private byte[] convertImageToBytes(BufferedImage image) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            if (!ImageIO.write(image, DEFAULT_IMAGE_FORMAT, baos)) {
                throw new IOException("Failed to write image to byte array");
            }
            return baos.toByteArray();
        }
    }

    /**
     * Obtém o retângulo do widget
     */
    private PDRectangle getWidgetRectangle(PDAnnotationWidget widget) {
        PDRectangle rect = widget.getRectangle();
        if (rect == null) {
            logger.warn("Widget has no rectangle, using default");
            return new PDRectangle(0, 0, 100, 100);
        }
        return rect;
    }

    /**
     * Cria o appearance stream para a imagem no widget
     */
    private void createImageAppearanceStream(PDDocument pdfDocument, PDAnnotationWidget widget,
                                             PDImageXObject pdImage, PDRectangle buttonRect,
                                             float imageAspectRatio) throws IOException {

        PDAppearanceStream appearanceStream = new PDAppearanceStream(pdfDocument);

        // ✅ CRÍTICO: Adicionar a imagem aos resources!
        PDResources resources = new PDResources();
        COSName imageName = resources.add(pdImage);
        appearanceStream.setResources(resources);
        appearanceStream.setBBox(buttonRect);

        try (PDPageContentStream contentStream = new PDPageContentStream(
                pdfDocument, appearanceStream)) {

            // Calcular dimensões mantendo proporção
            float buttonWidth = buttonRect.getWidth();
            float buttonHeight = buttonRect.getHeight();
            float buttonAspectRatio = buttonHeight / buttonWidth;

            float drawWidth, drawHeight;
            float xOffset = 0, yOffset = 0;

            if (imageAspectRatio > buttonAspectRatio) {
                // Imagem mais alta que o botão - ajustar pela altura
                drawHeight = buttonHeight;
                drawWidth = drawHeight / imageAspectRatio;
                xOffset = (buttonWidth - drawWidth) / 2;
            } else {
                // Imagem mais larga que o botão - ajustar pela largura
                drawWidth = buttonWidth;
                drawHeight = drawWidth * imageAspectRatio;
                yOffset = (buttonHeight - drawHeight) / 2;
            }

            // Desenhar imagem
            contentStream.drawImage(pdImage, xOffset, yOffset, drawWidth, drawHeight);
        }

        // Definir appearance
        PDAppearanceDictionary appearanceDictionary = widget.getAppearance();
        if (appearanceDictionary == null) {
            appearanceDictionary = new PDAppearanceDictionary();
            widget.setAppearance(appearanceDictionary);
        }
        appearanceDictionary.setNormalAppearance(appearanceStream);

        // Atualizar appearance state
        COSDictionary cosWidget = widget.getCOSObject();
        cosWidget.setItem(COSName.AS, COSName.N);

        // Configurar appearance characteristics
        updateAppearanceCharacteristics(widget, pdImage);
    }

    /**
     * Atualiza as características de aparência do widget
     */
    private void updateAppearanceCharacteristics(PDAnnotationWidget widget, PDImageXObject pdImage) {
        COSDictionary cosWidget = widget.getCOSObject();
        COSDictionary mk = (COSDictionary) cosWidget.getDictionaryObject(COSName.MK);
        
        if (mk == null) {
            mk = new COSDictionary();
            cosWidget.setItem(COSName.MK, mk);
        }

        COSArray i = new COSArray();
        i.add(pdImage.getCOSObject());
        mk.setItem(COSName.I, i);
    }

    /**
     * Valida se a imagem tem dimensões válidas
     */
    public boolean validateImageDimensions(BufferedImage image, int minWidth, int minHeight) {
        if (image == null) {
            return false;
        }
        return image.getWidth() >= minWidth && image.getHeight() >= minHeight;
    }

    /**
     * Redimensiona imagem mantendo proporção
     */
    public BufferedImage resizeImage(BufferedImage original, int maxWidth, int maxHeight) {
        if (original == null) {
            throw new IllegalArgumentException("Original image cannot be null");
        }

        int originalWidth = original.getWidth();
        int originalHeight = original.getHeight();

        // Se já está dentro dos limites, retornar original
        if (originalWidth <= maxWidth && originalHeight <= maxHeight) {
            return original;
        }

        float aspectRatio = (float) originalHeight / (float) originalWidth;
        int newWidth, newHeight;

        if (originalWidth > originalHeight) {
            newWidth = maxWidth;
            newHeight = (int) (maxWidth * aspectRatio);
        } else {
            newHeight = maxHeight;
            newWidth = (int) (maxHeight / aspectRatio);
        }

        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        resized.createGraphics().drawImage(
            original.getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH),
            0, 0, null
        );

        return resized;
    }
}
