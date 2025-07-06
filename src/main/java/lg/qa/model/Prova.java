package lg.qa.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Prova {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private Categoria categoria;
    
    private String nivelDificuldade;
    
    @ManyToMany
    @JoinTable(
        name = "prova_perguntas",
        joinColumns = @JoinColumn(name = "prova_id"),
        inverseJoinColumns = @JoinColumn(name = "pergunta_id")
    )
    @Builder.Default
    private List<Pergunta> perguntas = new ArrayList<>();
    
    @OneToMany(mappedBy = "prova", cascade = CascadeType.ALL,
            orphanRemoval = true, targetEntity = RespostaDada.class)
    @Builder.Default
    private List<RespostaDada> respostasDadas = new ArrayList<>();
    
    @Builder.Default
    private Integer acertos = 0;
    
    @Builder.Default
    private Integer erros = 0;
    
    @Builder.Default
    private LocalDateTime dataCriacao = LocalDateTime.now();
}
