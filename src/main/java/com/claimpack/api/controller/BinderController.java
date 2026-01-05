package com.claimpack.api.controller;

import com.claimpack.api.service.BinderEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class BinderController {

    @Autowired
    private BinderEngineService binderService;

    @PostMapping("/upload")
    public ResponseEntity<byte[]> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        try {
            System.out.println("🤖 Starting PDF generation for " + files.length + " files...");
            
            // 1. Call the Engine to make the PDF
            byte[] pdfBytes = binderService.createPdf(files);

            System.out.println("✅ PDF Created! Size: " + pdfBytes.length + " bytes.");

            // 2. Send the PDF back to the browser
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=evidence_binder.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}