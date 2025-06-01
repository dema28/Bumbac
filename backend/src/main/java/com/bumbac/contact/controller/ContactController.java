package com.bumbac.contact.controller;

import com.bumbac.contact.dto.ContactRequest;
import com.bumbac.contact.entity.ContactMessage;
import com.bumbac.contact.service.ContactService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<String> handle(@RequestBody ContactRequest request) {
        contactService.handleContact(request);
        return ResponseEntity.ok("Message received");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ContactMessage>> getAll() {
        return ResponseEntity.ok(contactService.getAllMessages());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/filter")
    public ResponseEntity<List<ContactMessage>> getFiltered(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to
    ) {
        return ResponseEntity.ok(contactService.getFilteredMessages(from, to));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/read")
    public ResponseEntity<String> readFile(@PathVariable Long id) {
        return ResponseEntity.ok(contactService.readMessageFile(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contactService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        Resource zip = contactService.exportMessagesToZip();
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + zip.getFilename());
        zip.getInputStream().transferTo(response.getOutputStream());
    }
}
