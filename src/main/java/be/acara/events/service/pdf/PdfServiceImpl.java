package be.acara.events.service.pdf;

import be.acara.events.domain.CreateOrder;
import be.acara.events.domain.CreateOrderList;
import be.acara.events.domain.Event;
import be.acara.events.domain.User;
import be.acara.events.exceptions.PdfException;
import be.acara.events.service.EventService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class PdfServiceImpl implements PdfService {

    private final EventService eventService;

    @Autowired
    public PdfServiceImpl(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public byte[] createTicketPdf(CreateOrderList createOrderList, User user) {
        Map<Event, Integer> orders = listsToMap(getEventListFromOrderList(createOrderList), getAmountOfTicketsFromOrderList(createOrderList));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (Document document = new Document(PageSize.A4, 36, 36, 85, 36)) {
            setHeaderAndFooter(document, baos);
            document.open();
            for (Map.Entry<Event, Integer> entry : orders.entrySet()) {
                setTitleOfPDF(document, entry.getKey());
                setTableWith2cellsAndSpacing(document, "Amount of tickets: ", entry.getValue().toString(), 25, false);
                setTableWith2cellsAndSpacing(document, "Date: ", entry.getKey().getEventDate().toString().substring(0, 10), 30, false);
                setTableWith2cellsAndSpacing(document, "Price: ", String.format("€ %s", entry.getKey().getPrice().toString()), 15, false);
                setTableWith2cellsAndSpacing(document, "Description: ", entry.getKey().getDescription(), 15, false);
                setTableWith2cellsAndSpacing(document, "Name: ", user.getFirstName() + " " + user.getLastName(), 15, false);
                setTableWith2cellsAndSpacing(document, "Email: ", user.getEmail(), 15, false);
                if (entry.getKey().getImage().length != 0) {
                    setEventPictureToPdf(document, entry.getKey());
                }
                setTableWith2cellsAndSpacing(document, "Unique code: ", UUID.randomUUID().toString(), 25, true);
                document.newPage();
            }
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

    private List<Event> getEventListFromOrderList(CreateOrderList createOrderList) {
        return createOrderList
                .getOrders()
                .stream()
                .map(createOrder -> eventService.findById(createOrder.getEventId()))
                .collect(Collectors.toList());
    }

    private List<Integer> getAmountOfTicketsFromOrderList(CreateOrderList createOrderList) {
        return createOrderList
                .getOrders()
                .stream()
                .map(CreateOrder::getAmountOfTickets)
                .collect(Collectors.toList());
    }

    private <K, V> Map<K, V> listsToMap(List<K> keys, List<V> values) {
        return IntStream.range(0, keys.size()).boxed()
                .collect(Collectors.toMap(keys::get, values::get));
    }
}
