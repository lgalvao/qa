package lg.qa.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponderProvaRequest {
    @NotNull(message = "O ID da prova é obrigatório")
    private Long provaId;
    
    @NotNull(message = "As respostas não podem ser nulas")
    private List<RespostaDTO> respostas;
    
    @Getter
    @Setter
    public static class RespostaDTO {
        @NotNull(message = "O ID da pergunta é obrigatório")
        private Long perguntaId;
        
        @NotNull(message = "O ID da alternativa é obrigatório")
        private Long alternativaId;
    }
}
