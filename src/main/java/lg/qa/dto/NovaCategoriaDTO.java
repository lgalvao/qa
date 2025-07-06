package lg.qa.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NovaCategoriaDTO {
    @NotBlank(message = "O nome da categoria é obrigatório")
    private String nome;
} 