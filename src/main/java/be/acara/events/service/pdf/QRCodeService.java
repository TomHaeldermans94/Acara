package be.acara.events.service.pdf;

import com.google.zxing.WriterException;

import java.io.IOException;

public interface QRCodeService {
    /**
     * This method returns a randomly generated QR Code in the form of a byte array.
     * @param code the code that is transformed into a qrCode
     * @param width the width of the image
     * @param height the height of the image
     * @return qrCodeImage
     */
    byte[] getQRCodeImage(String code, int width, int height) throws WriterException, IOException;
}
