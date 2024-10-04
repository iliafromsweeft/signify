package com.example.demo.controller;

import com.example.demo.service.DocumentService;
import com.example.demo.dto.FilePosition;
import com.example.demo.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final DocumentService documentService;

    @PostMapping(value = "/upload", consumes = { "multipart/form-data" })
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        Optional<Long> savedFileId = fileService.saveFile(file);
        return ResponseEntity.ok("File uploaded successfully. ID: " + savedFileId.get());
    }

    @PostMapping("/sendDocuments")
    public void sendDocuments(
            @RequestBody List<FilePosition> filePositions,
            @RequestParam String documentName,
            @RequestParam String personId) {

        documentService.createDocumentWithFilesAndSetDetails(filePositions, documentName, personId);
    }
}
