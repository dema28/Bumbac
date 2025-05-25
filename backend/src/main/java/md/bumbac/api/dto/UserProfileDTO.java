package md.bumbac.api.dto;

import lombok.Data;
import lombok.AllArgsConstructor;   // ← импорт

@Data
@AllArgsConstructor               // ← добавляем
public class UserProfileDTO {
    private String email;
    private String fullName;
    private boolean emailVerified;
}
