package com.example.demo.service;

import com.example.demo.dto.FilePosition;
import com.example.demo.dto.UserDTO;
import com.example.demo.repository.FileRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final FileRepository fileRepository;
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;

    public void createDocumentWithFilesAndSetDetails(List<FilePosition> filePositions, String documentName, String personId) {
        UserDTO user = userRepository.getUserByPersonId(personId);
        if (user == null) {
            throw new RuntimeException("User not found with personId: " + personId);
        }

        Long documentId = createDocumentWithFiles(filePositions, documentName);

        setDocumentDetails(documentId, filePositions, user);
    }

    private Long createDocumentWithFiles(List<FilePosition> filePositions, String documentName) {
        List<Map<String, String>> filesData = new ArrayList<>();

        for (FilePosition filePosition : filePositions) {
            Optional<com.example.demo.entity.FileEntity> fileEntityOptional = fileRepository.findById(filePosition.getFileId());
            if (fileEntityOptional.isPresent()) {
                com.example.demo.entity.FileEntity fileEntity = fileEntityOptional.get();
                try {
                    Path filePath = Path.of(fileEntity.getPath());
                    byte[] fileContent = Files.readAllBytes(filePath);
                    String encodedFile = Base64.getEncoder().encodeToString(fileContent);

                    Map<String, String> fileData = new HashMap<>();
                    fileData.put("fileName", fileEntity.getName());
                    fileData.put("extension", "pdf");
                    fileData.put("fileBase64", encodedFile);

                    filesData.add(fileData);
                } catch (Exception e) {
                    throw new RuntimeException("Error reading file: " + fileEntity.getPath(), e);
                }
            } else {
                throw new RuntimeException("File not found with ID: " + filePosition.getFileId());
            }
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("documentName", documentName);
        requestBody.put("files", filesData);

        // Create the HttpEntity without headers, as they are handled by the interceptor
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody);

        // API call to create the document
        String apiEndpoint = "https://portal.signifyapp.com/integration-api/v2/create-document-with-multiple";
        ResponseEntity<Map> response = restTemplate.exchange(apiEndpoint, HttpMethod.POST, requestEntity, Map.class);

        // Extract documentId from the response
        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null && responseBody.containsKey("documentId")) {
            return Long.parseLong(responseBody.get("documentId").toString());
        } else {
            throw new RuntimeException("Failed to retrieve document ID from response");
        }
    }

    private void setDocumentDetails(Long documentId, List<FilePosition> filePositions, UserDTO user) {
        Map<String, Object> recipient = new HashMap<>();
        recipient.put("name", user.getName());
        recipient.put("email", user.getEmail());
        recipient.put("mobileNumber", user.getPhoneNumber());
        recipient.put("role", "SIGNER");
        recipient.put("recipientContactType", "EMAIL");
        recipient.put("possibleSignatureTypes", Arrays.asList("SMS_OTP"));
        recipient.put("qualifiedSignatureType", "GEO_ID_CARD");
        recipient.put("restrictedPermissions", Arrays.asList("DOWNLOAD_DOCUMENT", "ALLOW_COMMENT"));

        List<Map<String, Object>> fields = new ArrayList<>();
        for (int i = 0; i < filePositions.size(); i++) {
            FilePosition filePosition = filePositions.get(i);

            Map<String, Object> field = new HashMap<>();
            field.put("fileNumber", i + 1);
            field.put("type", "SIGNATURE");
            field.put("page", 1);
            field.put("left", filePosition.getLeft());
            field.put("top", filePosition.getTop());
            field.put("width", 200);
            field.put("height", 60);
            field.put("value", null);
            field.put("required", true);
            field.put("placeholder", null);
            fields.add(field);
        }

        recipient.put("fields", fields);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("documentId", documentId);
        requestBody.put("workflowType", "SERIAL");
        requestBody.put("recipients", Collections.singletonList(recipient));

        Map<String, Object> additionalDetails = new HashMap<>();
        additionalDetails.put("autoDeleteDate", "2024-07-23T10:52:25.060Z");
        requestBody.put("additionalDetails", additionalDetails);
        requestBody.put("shareAutomatically", false);

        // Create the HttpEntity without headers, as they are handled by the interceptor
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody);

        // API call to set the document details
        String apiEndpoint = "https://portal.signifyapp.com/integration-api/v2/set-document-details";
        restTemplate.exchange(apiEndpoint, HttpMethod.POST, requestEntity, String.class);
    }
}
