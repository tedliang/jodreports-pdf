//
// JOOReports - The Open Source Java/OpenOffice Report Engine
// Copyright (C) 2004-2006 - Mirko Nasato <mirko@artofsolving.com>
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

    private final DocumentTemplate odtOutputDocumentTemplate;

    private final PdfOptions pdfOptions;

    public PdfOutputDocumentTemplate(DocumentTemplate odtOutputDocumentTemplate) {
        this(odtOutputDocumentTemplate, PdfOptions.create());
    }

    public PdfOutputDocumentTemplate(DocumentTemplate odtOutputDocumentTemplate, PdfOptions pdfOptions) {
        this.odtOutputDocumentTemplate = odtOutputDocumentTemplate;
        this.pdfOptions = pdfOptions;
    }

    public void setXmlEntries(String[] xmlEntries) {
        this.odtOutputDocumentTemplate.setXmlEntries(xmlEntries);
    }

    public void setContentWrapper(ContentWrapper contentWrapper) {
        odtOutputDocumentTemplate.setContentWrapper(contentWrapper);
    }

    public void setOpenDocumentSettings(Map openDocumentSettings) {
        odtOutputDocumentTemplate.setOpenDocumentSettings(openDocumentSettings);
    }

    public Map getConfigurations() {
        return odtOutputDocumentTemplate.getConfigurations();
    }

    public PdfOptions getPdfOptions() {
        return pdfOptions;
    }

    /**
     * Merge the data model into this template and create the output document.
     *
     * @param model
     * @param output
     * @throws IOException
     * @throws DocumentTemplateException
     */
    public void createDocument(Object model, OutputStream output) throws IOException, DocumentTemplateException {

        try (final OutputStreamToInputStream<OdfTextDocument> os2is = new OutputStreamToInputStream<OdfTextDocument>() {
            @Override
            protected OdfTextDocument doRead(final InputStream istream) throws Exception {
                OdfTextDocument document = OdfTextDocument.loadDocument(istream);
                return document;
            }
        }) {
            odtOutputDocumentTemplate.createDocument(model, os2is);
            PdfConverter.getInstance().convert(os2is.getResult(), output, pdfOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}