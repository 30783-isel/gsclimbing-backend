package com.gsclimbing.x.pdf.defectInspectionReport.common;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.io.File;
import java.util.List;

public class PhotoInserter {

    public static void insertPhotos(
            PdfStamper stamper,
            List<String> photos,
            List<String> descriptions) throws Exception {

        if (photos == null || photos.isEmpty()) {
            return;
        }

        PdfContentByte canvas =
                stamper.getOverContent(PhotoLayout.PAGE);

        float x = PhotoLayout.START_X;
        float y = PhotoLayout.START_Y;
        int col = 0;

        for (int i = 0; i < photos.size(); i++) {

            String path = photos.get(i);
            if (path == null || !new File(path).exists()) {
                continue; // trata ausência de foto
            }

            Image img = Image.getInstance(path);
            img.scaleToFit(
                    PhotoLayout.IMAGE_WIDTH,
                    PhotoLayout.IMAGE_HEIGHT
            );
            img.setAbsolutePosition(x, y);
            canvas.addImage(img);

            // legenda
            if (descriptions != null && i < descriptions.size()) {
                ColumnText.showTextAligned(
                        canvas,
                        Element.ALIGN_LEFT,
                        new Phrase(descriptions.get(i)),
                        x,
                        y - 15,
                        0
                );
            }

            col++;
            if (col == PhotoLayout.COLUMNS) {
                col = 0;
                x = PhotoLayout.START_X;
                y -= PhotoLayout.IMAGE_HEIGHT + PhotoLayout.V_GAP;
            } else {
                x += PhotoLayout.IMAGE_WIDTH + PhotoLayout.H_GAP;
            }
        }
    }
}

