package lg.qa.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resposta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pergunta_id")
    private Pergunta pergunta;
    
    private String texto;
    private boolean correta;
    
    @Builder.Default
    private LocalDateTime data = LocalDateTime.now();
}
