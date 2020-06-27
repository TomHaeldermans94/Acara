package be.acara.events.service.pdf;

import be.acara.events.domain.Event;
import be.acara.events.domain.Order;
import be.acara.events.domain.User;
import be.acara.events.exceptions.PdfException;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class PdfServiceImpl implements PdfService {
    
    private final QRCodeService qrCodeService;
    
    @Autowired
    public PdfServiceImpl(QRCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] createTicketPdf(List<Order> orderList) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (Document document = new Document(PageSize.A4, 36, 36, 85, 36)) {
            setHeaderAndFooter(document, baos);
            document.open();
            for (Order order : orderList) {
                String code = String.valueOf(order.hashCode());
                setTitleOfPDF(document, order.getEvent());
                if (order.getEvent().getImage().length != 0) {
                    setPictureToPdf(document, order.getEvent().getImage());
                }
                setTableWith2cellsAndSpacing(document, "Amount of tickets: ", String.valueOf(order.getAmountOfTickets()), 25, false);
                setTableWith2cellsAndSpacing(document, "Date: ", order.getEvent().getEventDate().toString().substring(0, 10), 25, false);
                setTableWith2cellsAndSpacing(document, "Price: ", String.format("€ %s", order.getEvent().getPrice().toString()), 10, false);
                setTableWith2cellsAndSpacing(document, "Description: ", order.getEvent().getDescription(), 10, false);
                
                User user = order.getUser();
                setTableWith2cellsAndSpacing(document, "Name: ", user.getFirstName() + " " + user.getLastName(), 10, false);
                setTableWith2cellsAndSpacing(document, "Email: ", user.getEmail(), 10, false);
                
                setPictureToPdf(document, qrCodeService.getQRCodeImage(code, 100, 100));
                setTableWithOneCellAndSpacing(document, code, 0, false);
                document.newPage();
            }
        } catch (Exception e) {
            throw new PdfException("PDF exception", "error when creating the pdf file of the ticket");
        }
        return baos.toByteArray();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] createTicketPdf(Order order) {
        return createTicketPdf(Collections.singletonList(order));
    }
    
    /**
     * private method to set the title to the pdf document
     *
     * @param document HTML document for adding all kinds of text elements
     * @param event    event of which a ticket has to be generated
     */
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
    
    /**
     * private method to set the font to bold
     *
     * @return font the bold font
     */
    private Font setBoldFont() {
        Font font = new Font();
        font.setStyle(Font.BOLD);
        font.setSize(16);
        return font;
    }
    
    /**
     * private method to set the header and footer to the document
     *
     * @param document HTML document for adding all kinds of text elements
     * @param baos     the outputstream to which the pdfWriter can write
     */
    private void setHeaderAndFooter(Document document, ByteArrayOutputStream baos) {
        PdfWriter pdfWriter = PdfWriter.getInstance(document, baos);
        HeaderFooter pageEvent = new HeaderFooter();
        pdfWriter.setPageEvent(pageEvent);
    }
    
    /**
     * private method to set the picture of the event to the pdf ticket
     *
     * @param document   HTML document for adding all kinds of text elements
     * @param imageToSet image to set to the document
     * @throws IOException exception is generated if there is an error with getting the image
     */
    private void setPictureToPdf(Document document, byte[] imageToSet) throws IOException {
        if (imageToSet != null) {
            PdfPTable table = new PdfPTable(1);
            Image image = Image.getInstance(imageToSet);
            table.getDefaultCell().setFixedHeight(180);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
            table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            table.setSpacingBefore(20);
            table.addCell(image);
            document.add(table);
        }
    }
    
    /**
     * private method to add a table with 2 cells to the document
     *
     * @param document  HTML document for adding all kinds of text elements
     * @param cellText1 text for cell 1
     * @param cellText2 text for cell 2
     * @param spacing   the spacing between this table and the next one
     * @param border    boolean to know if a border is needed or not
     */
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
    
    /**
     * private method to add a table with 1 cell to the document
     *
     * @param document HTML document for adding all kinds of text elements
     * @param cellText text for cell
     * @param spacing  the spacing between this table and the next one
     * @param border   boolean to know if a border is needed or not
     */
    private void setTableWithOneCellAndSpacing(Document document, String cellText, int spacing, boolean border) {
        PdfPTable table = new PdfPTable(1);
        table.setSpacingBefore(spacing);
        PdfPCell cell = new PdfPCell(new Paragraph(cellText));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        if (!border) {
            cell.setBorder(Rectangle.NO_BORDER);
        }
        table.addCell(cell);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        document.add(table);
    }
}
