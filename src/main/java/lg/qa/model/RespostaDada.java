package lg.qa.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RespostaDada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "prova_id")
    private Prova prova;
    
    @ManyToOne
    @JoinColumn(name = "pergunta_id")
    private Pergunta pergunta;
    
    @ManyToOne
    @JoinColumn(name = "alternativa_id")
    private Alternativa alternativaEscolhida;
    
    private boolean correta;
}
