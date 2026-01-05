package com.claimpack.api.service;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class BinderEngineService {

    public byte[] createPdf(MultipartFile[] files) throws IOException {
        // The "Pro" Utility that manages memory automatically
        PDFMergerUtility merger = new PDFMergerUtility();
        ByteArrayOutputStream finalOutputStream = new ByteArrayOutputStream();

        // We need to keep track of temp docs to close them later (to save memory)
        // But for this simple version, we will let Java handle the cleanup.
        
        for (MultipartFile file : files) {
            String filename = file.getOriginalFilename().toLowerCase();

            // 📄 CASE 1: It is already a PDF (Add it directly)
            if (filename.endsWith(".pdf")) {
                merger.addSource(file.getInputStream());
            } 
            // 🖼️ CASE 2: It is an Image (Convert to Temp PDF -> Add to Merger)
            else {
                ByteArrayOutputStream imagePdfStream = new ByteArrayOutputStream();
                try (PDDocument tempDoc = new PDDocument()) {
                    PDPage page = new PDPage(PDRectangle.LETTER);
                    tempDoc.addPage(page);

                    byte[] imageBytes = file.getBytes();
                    PDImageXObject pdImage = PDImageXObject.createFromByteArray(tempDoc, imageBytes, file.getOriginalFilename());

                    try (PDPageContentStream contentStream = new PDPageContentStream(tempDoc, page)) {
                        float pageWidth = page.getMediaBox().getWidth();
                        float pageHeight = page.getMediaBox().getHeight();
                        
                        // Scale to fit
                        float scale = Math.min(pageWidth / pdImage.getWidth(), pageHeight / pdImage.getHeight());
                        float imageWidth = pdImage.getWidth() * scale;
                        float imageHeight = pdImage.getHeight() * scale;

                        contentStream.drawImage(pdImage, (pageWidth - imageWidth) / 2, (pageHeight - imageHeight) / 2, imageWidth, imageHeight);
                    }
                    // Save this single image-page to a stream
                    tempDoc.save(imagePdfStream);
                }
                // Add that stream to the merger
                merger.addSource(new ByteArrayInputStream(imagePdfStream.toByteArray()));
            }
        }

        // Merge everything into the final result
        merger.setDestinationStream(finalOutputStream);
        merger.mergeDocuments(null);

        return finalOutputStream.toByteArray();
    }
}