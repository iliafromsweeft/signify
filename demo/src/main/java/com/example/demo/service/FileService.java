package com.example.demo.service;

import com.example.demo.entity.FileEntity;
import com.example.demo.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public Optional<Long> saveFile(MultipartFile file) {
        try {
            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null || originalFileName.isEmpty()) {
                throw new IllegalArgumentException("Invalid file name");
            }

            String extension = getFileExtension(originalFileName);
            String fileNameWithoutExtension = originalFileName.substring(0, originalFileName.lastIndexOf('.'));

            Path filePath = Paths.get(uploadDir, originalFileName);
            if (!Files.exists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());
            }

            Files.write(filePath, file.getBytes());

            FileEntity fileEntity = new FileEntity();
            fileEntity.setName(fileNameWithoutExtension);
            fileEntity.setExtension(extension);
            fileEntity.setPath(filePath.toString());

            FileEntity savedEntity = fileRepository.save(fileEntity);
            return Optional.of(savedEntity.getId());

        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }


    private String getFileExtension(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }
}
