package lg.qa.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RespostaProvaResponse {
    private Long id;
    private String titulo;
    private String categoria;
    private String nivelDificuldade;
    private Integer totalPerguntas;
    private Integer acertos;
    private Integer erros;
    private LocalDateTime dataConclusao;
    private List<RespostaDTO> respostas;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class RespostaDTO {
        private Long perguntaId;
        private String perguntaTitulo;
        private Long alternativaId;
        private String alternativaTexto;
        private boolean correta;
    }
}
