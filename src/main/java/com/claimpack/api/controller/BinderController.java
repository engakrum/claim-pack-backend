package com.claimpack.api.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class BinderController {

    @PostMapping("/upload")
    public ResponseEntity<byte[]> uploadFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "category", defaultValue = "General Dispute") String category,
            @RequestParam(value = "summary", defaultValue = "") String summary
    ) {
        try {
            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);

            document.open();
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.BLACK);
            Paragraph title = new Paragraph("Evidence Binder: " + category, titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            if (!summary.isEmpty()) {
                document.add(new Paragraph("Summary of Events:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
                document.add(new Paragraph(summary));
                document.add(new Paragraph("\n"));
            }

            document.add(new Paragraph("Exhibit List:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
            for (int i = 0; i < files.length; i++) {
                document.add(new Paragraph((i + 1) + ". " + files[i].getOriginalFilename() + " (" + (files[i].getSize() / 1024) + " KB)"));
            }
            document.add(new Paragraph("\n------------------------------------------------\n\n"));

            for (MultipartFile file : files) {
                if (file.getContentType() != null && file.getContentType().startsWith("image")) {
                    try {
                        document.add(new Paragraph("Exhibit: " + file.getOriginalFilename()));
                        Image img = Image.getInstance(file.getBytes());
                        float scaler = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin()) / img.getWidth()) * 100;
                        img.scalePercent(scaler);
                        document.add(img);
                        document.newPage();
                    } catch (Exception e) {}
                }
            }
            document.close();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ClaimPack_Binder.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(out.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}