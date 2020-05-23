package be.acara.events.service.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HeaderFooterTest {
    private HeaderFooter headerFooter;
    private PdfWriter writer;
    private Document document;
    private ByteArrayOutputStream baos;
    
    @BeforeEach
    void setUp() {
        headerFooter = new HeaderFooter();
        document = new Document(PageSize.A4, 36, 36, 85, 36);
        baos = new ByteArrayOutputStream();
        writer = PdfWriter.getInstance(document, baos);
    }
    
    @Test
    void onOpenDocument() {
        document.open();
        PdfWriter spyWriter = spy(writer);
        headerFooter.onOpenDocument(spyWriter, document);
        verify(spyWriter, times(1)).getDirectContent();
    }
    
    @Test
    void onEndPage() {
        document.open();
        PdfWriter spyWriter = spy(writer);
        headerFooter.onEndPage(spyWriter, document);
        verify(spyWriter, times(2)).getDirectContent();
    }
}
