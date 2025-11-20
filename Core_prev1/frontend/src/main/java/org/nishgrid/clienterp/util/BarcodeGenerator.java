package org.nishgrid.clienterp.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import java.awt.image.BufferedImage;

public class BarcodeGenerator {


    public static Image generateBarcodeImage(String text, int width, int height) {
        try {
            Code128Writer writer = new Code128Writer();
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.CODE_128, width, height);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);


            return SwingFXUtils.toFXImage(bufferedImage, null);

        } catch (Exception e) {
            System.err.println("Error generating barcode: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}