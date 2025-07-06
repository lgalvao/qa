package lg.qa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class NovaPerguntaDTO {
    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    private String descricao;

    @NotNull(message = "A categoria é obrigatória")
    private Long categoriaId;

    @NotBlank(message = "O nível de dificuldade é obrigatório")
    private String nivelDificuldade;

    @NotNull(message = "As alternativas são obrigatórias")
    private List<NovaAlternativaDTO> alternativas;

    @Getter
    @Setter
    public static class NovaAlternativaDTO {
        @NotBlank(message = "O texto da alternativa é obrigatório")
        private String texto;
        @NotNull(message = "Informe se a alternativa é correta")
        private Boolean correta;
    }
} 