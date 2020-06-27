package be.acara.events.service.pdf;

import be.acara.events.exceptions.QRException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class QRCodeServiceImpl implements QRCodeService {
    /**
     * This method takes the width and height of the QR Code,
     * and returns a randomly generated QR Code in the form of a byte array.
     */
    @Override
    public byte[] getQRCodeImage(String code, int width, int height) {
        byte[] qrCode;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(code, BarcodeFormat.QR_CODE, width, height);
            ByteArrayOutputStream qrOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", qrOutputStream);
            qrCode = qrOutputStream.toByteArray();
        }
        catch (WriterException | IOException e) {
            throw new QRException("QR exception", "something went wrong when formatting the QR-code");
        }
        return qrCode;
    }
}
