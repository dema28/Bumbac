package md.bumbac.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

    private final String uploadDir = "uploads/";

    @PostMapping("/avatar")
    public ResponseEntity<String> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) return ResponseEntity.badRequest().body("Empty file");

        String filename = UUID.randomUUID() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
        Path savePath = Paths.get(uploadDir + filename);
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        file.transferTo(savePath);
        return ResponseEntity.status(HttpStatus.CREATED).body("/uploads/" + filename);
    }
}
