package net.sf.jodreports.pdf;

import net.sf.jooreports.templates.DocumentTemplateException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TemplateTest extends AbstractTemplateTest {

    @Test
    public void testSimpleField() throws IOException, DocumentTemplateException {
        File templateFile = getTestFile("hello-template.odt");
        Map model = new HashMap();
        model.put("name", "Ted");
        String actual = processTemplate(templateFile, model);
        assertEquals("output content", "Hello Ted!", actual);
    }
}