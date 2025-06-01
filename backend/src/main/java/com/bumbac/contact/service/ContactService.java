package com.bumbac.contact.service;

import com.bumbac.contact.dto.ContactRequest;
import com.bumbac.contact.entity.ContactMessage;
import com.bumbac.contact.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final String storagePath = "storage/messages"; // можно вынести в application.properties или .env

    public void handleContact(ContactRequest request) {
        String fileName = "contact_" + UUID.randomUUID() + ".md";
        String filePath = storagePath + "/" + fileName;

        File directory = new File(storagePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("## Contact Message\n\n");
            writer.write("**Name:** " + request.getName() + "\n");
            writer.write("**Email:** " + request.getEmail() + "\n");
            writer.write("**Subject:** " + request.getSubject() + "\n\n");
            writer.write("**Message:**\n\n" + request.getMessage() + "\n");
        } catch (IOException e) {
            throw new RuntimeException("Unable to write contact message", e);
        }

        ContactMessage msg = ContactMessage.builder()
                .name(request.getName())
                .email(request.getEmail())
                .subject(request.getSubject())
                .filePath(filePath)
                .build();

        contactRepository.save(msg);
    }

    public List<ContactMessage> getAllMessages() {
        return contactRepository.findAll();
    }

    public String readMessageFile(Long id) {
        ContactMessage msg = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        Path path = Paths.get(msg.getFilePath());

        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException("Cannot read message file", e);
        }
    }

    public List<ContactMessage> getFilteredMessages(String from, String to) {
        List<ContactMessage> all = contactRepository.findAll();
        if (from == null && to == null) return all;

        LocalDate fromDate = (from != null) ? LocalDate.parse(from) : LocalDate.MIN;
        LocalDate toDate = (to != null) ? LocalDate.parse(to) : LocalDate.MAX;

        return all.stream()
                .filter(msg -> {
                    LocalDate created = msg.getCreatedAt().toLocalDate();
                    return !created.isBefore(fromDate) && !created.isAfter(toDate);
                })
                .toList();
    }

    public void deleteMessage(Long id) {
        ContactMessage msg = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        try {
            Files.deleteIfExists(Paths.get(msg.getFilePath()));
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при удалении файла", e);
        }

        contactRepository.delete(msg);
    }

    public Resource exportMessagesToZip() {
        String folderPath = storagePath;
        String zipPath = "storage/messages_export_" + UUID.randomUUID() + ".zip";

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath))) {
            Files.walk(Paths.get(folderPath))
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try (InputStream fis = Files.newInputStream(file)) {
                            ZipEntry entry = new ZipEntry(file.getFileName().toString());
                            zos.putNextEntry(entry);
                            fis.transferTo(zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            throw new RuntimeException("Ошибка при упаковке файла: " + file.getFileName(), e);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании архива", e);
        }

        return new FileSystemResource(zipPath);
    }
}
