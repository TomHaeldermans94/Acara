package be.acara.events.service.pdf;

import be.acara.events.domain.Event;
import be.acara.events.domain.User;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class EventPdf {

    private static final Logger logger = LogManager.getLogger(EventPdf.class);

    public static byte[] createTicketPdf(Event event, User user) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (Document document = new Document(PageSize.A4, 36, 36, 85, 36)) {
            setHeaderAndFooter(document, baos);
            document.open();
            setTitleOfPDF(document, event,2);
            setTableWith2cellsAndSpacing(document,"Date: ", event.getEventDate().toString().substring(0,10),30, false);
            setTableWith2cellsAndSpacing(document,"Price: ", String.format("€ %s",event.getPrice().toString()),15, false);
            setTableWith2cellsAndSpacing(document,"Description: ", event.getDescription(),15, false);
            setTableWith2cellsAndSpacing(document,"Name: ", user.getFirstName() + " " + user.getLastName(),15, false);
            setTableWith2cellsAndSpacing(document,"Email: ", user.getEmail(),15, false);
            setEventPictureToPdf(document,event,25);
            setTableWith2cellsAndSpacing(document,"Unique code: ", UUID.randomUUID().toString(),25,true);
        } catch (Exception e) {
            logger.error("Error when creating the pdf");
        }
        return baos.toByteArray();
    }

    private static void setTitleOfPDF(Document document, Event event, int spacing) {
        PdfPTable table = new PdfPTable(2);
        Paragraph paragraph1 = new Paragraph(event.getName(), setBoldFont());
        Paragraph paragraph2 = new Paragraph(event.getCategory().toString(), setBoldFont());
        paragraph1.setAlignment(Element.ALIGN_CENTER);
        paragraph2.setAlignment(Element.ALIGN_CENTER);
        PdfPCell cell1 = new PdfPCell();
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.addElement(paragraph1);
        PdfPCell cell2 = new PdfPCell();
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.addElement(paragraph2);
        table.addCell(cell1);
        table.addCell(cell2);
        table.setSpacingBefore(spacing);
        try {
            document.add(table);
        } catch (DocumentException e) {
            logger.error("Error when setting title to pdf");
        }
    }

    private static Font setBoldFont() {
        Font font = new Font();
        font.setStyle(Font.BOLD);
        font.setSize(16);
        return font;
    }

    private static void setHeaderAndFooter(Document document, ByteArrayOutputStream baos) {
        PdfWriter pdfWriter = PdfWriter.getInstance(document, baos);
        HeaderFooter pageEvent = new HeaderFooter();
        pdfWriter.setPageEvent(pageEvent);
    }

    static void setEventPictureToPdf(Document document, Event event, int spacing) throws IOException {
        if (event != null && event.getImage() != null) {
            PdfPTable table = new PdfPTable(1);
            Image image = Image.getInstance(event.getImage());
            table.getDefaultCell().setFixedHeight(200);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
            table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            table.setSpacingBefore(spacing);
            table.addCell(image);
            document.add(table);
        }
    }

    private static void setTableWith2cellsAndSpacing(Document document, String cellText1, String cellText2, int spacing, boolean border) {
        PdfPTable table = new PdfPTable(2);
        table.setSpacingBefore(spacing);
        if(!border) {
            table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        }
        table.addCell(cellText1);
        table.addCell(cellText2);
        document.add(table);
    }
}
