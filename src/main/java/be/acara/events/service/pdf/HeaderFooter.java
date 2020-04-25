package be.acara.events.service.pdf;

import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.IOException;

public class HeaderFooter extends PdfPageEventHelper {

    private static final Logger logger = LogManager.getLogger(HeaderFooter.class);

    @Override
    public void onOpenDocument(PdfWriter writer, Document document) {
        writer.getDirectContent().createTemplate(30, 16);
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        addHeader(writer);
        addFooter(writer);
    }

    private void addHeader(PdfWriter writer) {
        PdfPTable header = new PdfPTable(1);
        try {
            header.setTotalWidth(527);
            header.setLockedWidth(true);

            // add image
            PdfPCell imageCell = new PdfPCell();
            final String imageString = "src/main/resources/images/acara-logo.jpg";
            Image logo = Image.getInstance(imageString);
            logo.scalePercent(5);
            imageCell.setBorder(Rectangle.BOTTOM);
            imageCell.setBorderColor(Color.LIGHT_GRAY);
            imageCell.addElement(logo);
            header.addCell(imageCell);

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
            footer.writeSelectedRows(0, -1, 34, 50, writer.getDirectContent());
        } catch (DocumentException e) {
            logger.error("Error setting the footer to the ticket");
        }
    }
}
