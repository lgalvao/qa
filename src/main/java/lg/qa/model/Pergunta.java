package lg.qa.model;

import jakarta.persistence.*;
import java.util.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pergunta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String descricao;

    @OneToMany(mappedBy = "pergunta", cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = Alternativa.class)
    @Builder.Default
    private List<Alternativa> alternativas = new ArrayList<>();

    @ManyToOne
    private Categoria categoria;

    private String nivelDificuldade;
    private Date dataCriacao;
}
