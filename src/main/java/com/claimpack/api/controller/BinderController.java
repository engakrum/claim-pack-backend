package com.claimpack.api.controller;

import com.claimpack.api.service.AiAnalysisService;
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
// 🚀 SECURITY UPDATE: ALLOW CLOUD ACCESS
@CrossOrigin(origins = "*") 
public class BinderController {

    @Autowired
    private BinderEngineService binderService;

    @Autowired
    private AiAnalysisService aiService;

    @PostMapping("/upload")
    public ResponseEntity<byte[]> uploadFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "category", defaultValue = "General Dispute") String category,
            @RequestParam(value = "summary", defaultValue = "") String summary
    ) {
        try {
            System.out.println("Generating Binder for: " + category);
            
            byte[] pdfBytes = binderService.createPdf(files, category, summary);
            
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