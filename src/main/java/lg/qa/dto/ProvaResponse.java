package lg.qa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class ProvaResponse {
    private Long id;
    private String titulo;
    private String categoria;
    private String nivelDificuldade;
    private LocalDateTime dataCriacao;
    private List<PerguntaResumoDTO> perguntas;

    @Data
    @AllArgsConstructor
    public static class PerguntaResumoDTO {
        private Long id;
        private String titulo;
        private List<String> alternativas;
    }
}
