package be.acara.events.service.pdf;

import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.IOException;

public class HeaderFooter extends PdfPageEventHelper {

    private static final Logger logger = LogManager.getLogger(HeaderFooter.class);
    private Image total;

    @Override
    public void onOpenDocument(PdfWriter writer, Document document) {
        PdfTemplate t = writer.getDirectContent().createTemplate(30, 16);
        try {
            total = Image.getInstance(t);
        } catch (DocumentException e) {
            logger.error("Error setting header and footer to pdf");
        }
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        addHeader(writer);
        addFooter(writer);
    }

    private void addHeader(PdfWriter writer) {
        PdfPTable header = new PdfPTable(2);
        try {
            // set grey line of header
            header.setWidths(new int[]{5, 20});
            header.setTotalWidth(527);
            header.setLockedWidth(true);

            // add image
            PdfPCell imageCell = new PdfPCell();
            final String imageString = "src/main/resources/images/acara-logo.jpg";
            Image logo = Image.getInstance(imageString);
            imageCell.setBorder(Rectangle.BOTTOM);
            imageCell.setBorderColor(Color.LIGHT_GRAY);
            imageCell.addElement(logo);
            header.addCell(imageCell);

            // add text
            PdfPCell text = new PdfPCell();
            text.setPaddingBottom(15);
            text.setPaddingLeft(10);
            text.setBorder(Rectangle.BOTTOM);
            text.setBorderColor(Color.LIGHT_GRAY);
            text.setHorizontalAlignment(Element.ALIGN_RIGHT);
            text.addElement(new Phrase("ACARA", new Font(Font.HELVETICA, 12)));
            header.addCell(text);

            // write content
            header.writeSelectedRows(0, -1, 34, 803, writer.getDirectContent());
        } catch (DocumentException | IOException e) {
            logger.error("Error setting header to ticket");
        }
    }

    private void addFooter(PdfWriter writer) {
        PdfPTable footer = new PdfPTable(2);
        try {
            // set defaults for A4 size
            footer.setWidths(new int[]{20, 5});
            footer.setTotalWidth(527);
            footer.setLockedWidth(true);
            footer.getDefaultCell().setFixedHeight(40);
            footer.getDefaultCell().setBorder(Rectangle.TOP);
            footer.getDefaultCell().setBorderColor(Color.LIGHT_GRAY);

            // add copyright
            footer.addCell(new Phrase("ACARA", new Font(Font.HELVETICA, 12, Font.BOLD)));

            // add current page count
            footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            footer.addCell(new Phrase(String.format("Page %d", writer.getPageNumber()), new Font(Font.HELVETICA, 8)));

            // write page
            PdfContentByte canvas = writer.getDirectContent();
            footer.writeSelectedRows(0, -1, 34, 50, canvas);
        } catch (DocumentException e) {
            logger.error("Error with setting the footer to the ticket");
        }
    }
}
