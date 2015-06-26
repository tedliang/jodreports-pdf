package net.sf.jodreports.pdf;

import net.sf.jooreports.templates.Configuration;
import net.sf.jooreports.templates.DocumentTemplate;
import net.sf.jooreports.templates.DocumentTemplateException;
import net.sf.jooreports.templates.DocumentTemplateFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.junit.Before;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class AbstractTemplateTest {

    private DocumentTemplateFactory documentTemplateFactory;

    @Before
    public void setUp() throws Exception {
        documentTemplateFactory = new DocumentTemplateFactory();
        documentTemplateFactory.getFreemarkerConfiguration().setLocale(Locale.US);
    }

    protected File getTestFile(String fileName) {
        return new File("src/test/resources", fileName);
    }

    protected static File createTempFile(String extension) throws IOException {
        File tempFile = File.createTempFile("document", "." + extension);
        tempFile.deleteOnExit();
        return tempFile;
    }

    protected static void assertFileCreated(File file) {
        assertTrue("file created", file.isFile() && file.length() > 0);
    }

    protected String processTemplate(File templateFile, Map model) throws IOException, DocumentTemplateException {
        File openDocumentFile = createTempFile("pdf");
        DocumentTemplate template = new PdfOutputDocumentTemplate(documentTemplateFactory.getTemplate(templateFile));
        Map configurations = template.getConfigurations();
        configurations.put(Configuration.SETTING_PROCESS_JOOSCRIPT_ONLY, Boolean.FALSE);
        template.createDocument(model, new FileOutputStream(openDocumentFile));
        assertFileCreated(openDocumentFile);
        return extractTextContent(openDocumentFile);
    }

    protected String extractTextContent(File openDocumentFile) throws IOException {
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(PDDocument.load(openDocumentFile));
        return text.trim();
    }

}