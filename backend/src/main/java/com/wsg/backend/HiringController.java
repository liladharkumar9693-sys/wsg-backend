package com.wsg.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@RestController
@RequestMapping("/api/hiring")
@CrossOrigin(origins = "*")
public class HiringController {

    @Autowired
    private HiringRepository repository;

    // फाइल सेव करने के लिए फोल्डर पाथ
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @PostMapping("/apply")
    public String applyForJob(
            @RequestParam("fullName") String fullName,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("email") String email,
            @RequestParam("age") Integer age,
            @RequestParam("jobPost") String jobPost,
            @RequestParam("qualification") String qualification,
            @RequestParam("height") String height,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            @RequestParam(value = "resume", required = false) MultipartFile resume) throws IOException {

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
        return "Application submitted successfully!";
    }

    @GetMapping("/all")
    public List<JobApplication> getAllApplications() {
        return repository.findAll();
    }
}