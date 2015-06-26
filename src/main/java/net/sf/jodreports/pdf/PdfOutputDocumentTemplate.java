//
// JOOReports - The Open Source Java/OpenOffice Report PDF output extension
// Copyright (C) 2004-2015 - Ted Liang <tedliang@gmail.com>
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
// http://www.gnu.org/copyleft/lesser.html
//
package net.sf.jodreports.pdf;

import com.gc.iotools.stream.os.OutputStreamToInputStream;
import net.sf.jooreports.templates.DocumentTemplate;
import net.sf.jooreports.templates.DocumentTemplateException;
import org.odftoolkit.odfdom.converter.pdf.PdfConverter;
import org.odftoolkit.odfdom.converter.pdf.PdfOptions;
import org.odftoolkit.odfdom.doc.OdfTextDocument;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class PdfOutputDocumentTemplate implements DocumentTemplate {

    private final DocumentTemplate decoratedTemplate;

    private final PdfOptions pdfOptions;

    public PdfOutputDocumentTemplate(DocumentTemplate decoratedTemplate) {
        this(decoratedTemplate, PdfOptions.create());
    }

    public PdfOutputDocumentTemplate(DocumentTemplate decoratedTemplate, PdfOptions pdfOptions) {
        this.decoratedTemplate = decoratedTemplate;
        this.pdfOptions = pdfOptions;
    }

    @Override
    public void setXmlEntries(String[] xmlEntries) {
        this.decoratedTemplate.setXmlEntries(xmlEntries);
    }

    @Override
    public void setContentWrapper(ContentWrapper contentWrapper) {
        decoratedTemplate.setContentWrapper(contentWrapper);
    }

    @Override
    public void setOpenDocumentSettings(Map openDocumentSettings) {
        decoratedTemplate.setOpenDocumentSettings(openDocumentSettings);
    }

    @Override
    public Map getConfigurations() {
        return decoratedTemplate.getConfigurations();
    }

    public PdfOptions getPdfOptions() {
        return pdfOptions;
    }

    @Override
    public void createDocument(Object model, OutputStream output) throws IOException, DocumentTemplateException {
        try (final OutputStream pdf = output;
             final OutputStreamToInputStream<OdfTextDocument> pipe = new OutputStreamToInputStream<OdfTextDocument>() {
            @Override
            protected OdfTextDocument doRead(final InputStream odt) throws Exception {
                return OdfTextDocument.loadDocument(odt);
            }
        }) {
            decoratedTemplate.createDocument(model, pipe);
            PdfConverter.getInstance().convert(pipe.getResult(), pdf, pdfOptions);
        }
        catch (IOException | DocumentTemplateException ex) {
            throw ex;
        }
        catch (Exception e) {
            throw new DocumentTemplateException(e);
        }
    }

}