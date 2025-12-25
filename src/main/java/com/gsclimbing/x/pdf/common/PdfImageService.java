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

    /**
     * Carrega uma imagem do servidor FTP e converte para BufferedImage
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
     * VERSÃO CORRIGIDA - baseada no código antigo que funciona
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

        // Obter posição do botão
        PDRectangle buttonRect = getWidgetRectangle(widget);
        float x = buttonRect.getLowerLeftX();
        float y = buttonRect.getLowerLeftY();

        // ⚠️ CRÍTICO: Usar a lógica do código antigo para calcular dimensões
        float imageWidth;
        float imageHeight;

        if (pdImage.getWidth() > pdImage.getHeight()) {
            // Imagem horizontal - ajustar pela largura
            float ratio = (float) pdImage.getWidth() / (float) pdImage.getHeight();
            imageWidth = buttonRect.getWidth();
            imageHeight = imageWidth / ratio;
        } else {
            // Imagem vertical - ajustar pela altura
            float ratio = (float) pdImage.getHeight() / (float) pdImage.getWidth();
            imageHeight = buttonRect.getHeight();
            imageWidth = imageHeight / ratio;
        }

        // ⚠️ CRÍTICO: Criar appearance stream SEM adicionar imagem aos resources
        // (o código antigo não faz isso e funciona!)
        PDAppearanceStream appearanceStream = new PDAppearanceStream(pdfDocument);
        appearanceStream.setResources(new PDResources());

        try (PDPageContentStream contentStream = new PDPageContentStream(pdfDocument, appearanceStream)) {
            contentStream.drawImage(pdImage, x, y, imageWidth, imageHeight);
        }

        // ⚠️ CRÍTICO: BBox usa as coordenadas x, y do botão
        appearanceStream.setBBox(new PDRectangle(x, y, buttonRect.getWidth(), buttonRect.getHeight()));

        // Definir appearance
        PDAppearanceDictionary appearanceDictionary = widget.getAppearance();
        if (appearanceDictionary == null) {
            appearanceDictionary = new PDAppearanceDictionary();
            widget.setAppearance(appearanceDictionary);
        }
        appearanceDictionary.setNormalAppearance(appearanceStream);

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