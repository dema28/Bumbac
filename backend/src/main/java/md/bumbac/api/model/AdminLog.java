package md.bumbac.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "admin_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminLog {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long adminId;
    private String action;

    @Lob
    private String payload;

    private Instant ts;
}
