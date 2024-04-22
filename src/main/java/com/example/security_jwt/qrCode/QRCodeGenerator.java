package com.example.security_jwt.qrCode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class QRCodeGenerator {
    private static final int QR_CODE_WIDTH = 300;
    private static final int QR_CODE_HEIGHT = 300;
    private static final String QR_CODE_IMAGE_FORMAT = "png";

    public static void generateQRCode(String qrCodeText, String filePath) throws Exception{
        Map<EncodeHintType, Object> hintMap = new HashMap<>();
        hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix bitMatrix = new MultiFormatWriter().encode(qrCodeText, BarcodeFormat.QR_CODE, QR_CODE_WIDTH, QR_CODE_HEIGHT, hintMap);
        BufferedImage image = new BufferedImage(QR_CODE_WIDTH, QR_CODE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        for(int i = 0; i < QR_CODE_WIDTH; i++){
            for(int j = 0; j < QR_CODE_HEIGHT; j++){
                image.setRGB(i, j , bitMatrix.get(i, j) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }

        File qrFile = new File(filePath);
        ImageIO.write(image, QR_CODE_IMAGE_FORMAT, qrFile);
    }
}
