package com.uet.android.mouspad.Model.Ebook;

import android.content.Context;
import android.net.Uri;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PdfBook extends Book {
    private final Map<String,String> tocPoints = new LinkedHashMap<>();
    private PDFView mPdfView;
    private String mPdfUrl = "";

    PdfBook(Context context) {
        super(context);
    }

    public void setPdfView (PDFView pdfView){
        this.mPdfView = pdfView;
    }

    public void setPdfUrl(String url){
        this.mPdfUrl = url;
    }


    @Override
    protected void load() throws IOException {
    }

    @Override
    public Map<String, String> getToc() { return Collections.unmodifiableMap(tocPoints);
    }

    @Override
    protected BookMetadata getMetaData() throws IOException {
        return null;
    }

    @Override
    protected List<String> getSectionIds() {
        return null;
    }

    @Override
    protected Uri getUriForSectionID(String id) {
        return null;
    }

    @Override
    protected ReadPoint locateReadPoint(String section) {
        return null;
    }
}
