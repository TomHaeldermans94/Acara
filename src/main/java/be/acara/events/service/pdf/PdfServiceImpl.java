package be.acara.events.service.pdf;

import be.acara.events.domain.Event;
import be.acara.events.domain.User;
import be.acara.events.exceptions.PdfException;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@Component
public class PdfServiceImpl implements PdfService{

    public byte[] createTicketPdf(Event event, User user) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (Document document = new Document(PageSize.A4, 36, 36, 85, 36)) {
            setHeaderAndFooter(document, baos);
            document.open();
            setTitleOfPDF(document, event);
            setTableWith2cellsAndSpacing(document, "Date: ", event.getEventDate().toString().substring(0, 10), 30, false);
            setTableWith2cellsAndSpacing(document, "Price: ", String.format("€ %s", event.getPrice().toString()), 15, false);
            setTableWith2cellsAndSpacing(document, "Description: ", event.getDescription(), 15, false);
            setTableWith2cellsAndSpacing(document, "Name: ", user.getFirstName() + " " + user.getLastName(), 15, false);
            setTableWith2cellsAndSpacing(document, "Email: ", user.getEmail(), 15, false);
            if (event.getImage().length != 0) {
                setEventPictureToPdf(document, event);
            }
            setTableWith2cellsAndSpacing(document, "Unique code: ", UUID.randomUUID().toString(), 25, true);
        } catch (Exception e) {
            throw new PdfException("PDF exception", "error when creating the pdf file of the ticket");
        }
        return baos.toByteArray();
    }

    private void setTitleOfPDF(Document document, Event event) {
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
        table.setSpacingBefore(2);
        try {
            document.add(table);
        } catch (DocumentException e) {
            throw new PdfException("PDF exception", "error when setting the title to the pdf file of the ticket");
        }
    }

    private Font setBoldFont() {
        Font font = new Font();
        font.setStyle(Font.BOLD);
        font.setSize(16);
        return font;
    }

    private void setHeaderAndFooter(Document document, ByteArrayOutputStream baos) {
        PdfWriter pdfWriter = PdfWriter.getInstance(document, baos);
        HeaderFooter pageEvent = new HeaderFooter();
        pdfWriter.setPageEvent(pageEvent);
    }

    private void setEventPictureToPdf(Document document, Event event) throws IOException {
        PdfPTable table = new PdfPTable(1);
        Image image = Image.getInstance(event.getImage());
        table.getDefaultCell().setFixedHeight(200);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        table.setSpacingBefore(25);
        table.addCell(image);
        document.add(table);
    }

    private void setTableWith2cellsAndSpacing(Document document, String cellText1, String cellText2, int spacing, boolean border) {
        PdfPTable table = new PdfPTable(2);
        table.setSpacingBefore(spacing);
        if (!border) {
            table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        }
        table.addCell(cellText1);
        table.addCell(cellText2);
        document.add(table);
    }
}