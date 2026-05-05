package com.foodexpress.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class QrGenerator {

    public static String generateQrBase64(String text, int width, int height) {

        if (text == null || text.trim().isEmpty()) {
            return null; // prevent invalid QR
        }

        try {
            QRCodeWriter writer = new QRCodeWriter();

            BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", outputStream);

            byte[] bytes = outputStream.toByteArray();

            // ✅ Return Base64 with prefix (IMPORTANT for JSP/HTML)
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}