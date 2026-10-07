package com.wsg.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hiring")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class HiringController {

    @Autowired
    private HiringRepository repository;

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @PostMapping(value = "/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> applyForJob(
            @RequestParam(value = "fullName", required = false, defaultValue = "N/A") String fullName,
            @RequestParam(value = "phoneNumber", required = false, defaultValue = "N/A") String phoneNumber,
            @RequestParam(value = "email", required = false, defaultValue = "N/A") String email,
            @RequestParam(value = "age", required = false, defaultValue = "0") Integer age,
            @RequestParam(value = "jobPost", required = false, defaultValue = "General Security") String jobPost,
            @RequestParam(value = "qualification", required = false, defaultValue = "N/A") String qualification,
            @RequestParam(value = "height", required = false, defaultValue = "N/A") String height,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            @RequestParam(value = "resume", required = false) MultipartFile resume) {

        try {
            File uploadFolder = new File(UPLOAD_DIR);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            String photoName = "";
            if (photo != null && !photo.isEmpty()) {
                photoName = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
                Path photoPath = Paths.get(UPLOAD_DIR + photoName);
                Files.copy(photo.getInputStream(), photoPath, StandardCopyOption.REPLACE_EXISTING);
            }

            String resumeName = "";
            if (resume != null && !resume.isEmpty()) {
                resumeName = System.currentTimeMillis() + "_" + resume.getOriginalFilename();
                Path resumePath = Paths.get(UPLOAD_DIR + resumeName);
                Files.copy(resume.getInputStream(), resumePath, StandardCopyOption.REPLACE_EXISTING);
            }

            JobApplication applicant = new JobApplication();
            applicant.setFullName(fullName);
            applicant.setPhoneNumber(phoneNumber);
            applicant.setEmail(email);
            applicant.setAge(age);
            applicant.setJobPost(jobPost);
            applicant.setQualification(qualification);
            applicant.setHeight(height);
            applicant.setPhotoUrl(photoName);
            applicant.setResumeUrl(resumeName);

            repository.save(applicant);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Application submitted successfully!");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to upload files: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/all")
    public List<JobApplication> getAllApplications() {
        return repository.findAll();
    }
}