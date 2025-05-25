package md.bumbac.api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "colors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Color {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;   // «Бордовый»
    private String code;   // "#7A0022"
}
