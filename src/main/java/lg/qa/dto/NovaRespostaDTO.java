package lg.qa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class NovaRespostaDTO {
    @NotBlank(message = "O texto da resposta é obrigatório")
    private String texto;
    @NotNull(message = "A data é obrigatória")
    private LocalDateTime data;
} 