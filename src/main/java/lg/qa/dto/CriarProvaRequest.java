package lg.qa.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CriarProvaRequest {
    @NotNull(message = "A categoria é obrigatória")
    private Long categoriaId;
    
    private String nivelDificuldade;
    
    private int quantidadePerguntas = 10;
}
