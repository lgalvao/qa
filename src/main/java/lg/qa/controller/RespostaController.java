package lg.qa.controller;

import jakarta.validation.Valid;
import lg.qa.dto.NovaRespostaDTO;
import lg.qa.model.Alternativa;
import lg.qa.model.Resposta;
import lg.qa.repository.PerguntaRepository;
import lg.qa.repository.RespostaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/perguntas/{perguntaId}/respostas")
@RequiredArgsConstructor
public class RespostaController {
    private final RespostaRepository respostaRepository;
    private final PerguntaRepository perguntaRepository;

    @PostMapping
    public Resposta responder(@PathVariable Long perguntaId, @Valid @RequestBody NovaRespostaDTO dto) {
        return perguntaRepository.findById(perguntaId).map(pergunta -> {
            Resposta resposta = new Resposta();
            resposta.setPergunta(pergunta);
            resposta.setTexto(dto.getTexto());
            resposta.setData(dto.getData());
            resposta.setCorreta(
                pergunta.getAlternativas().stream()
                    .filter(a -> a.getTexto().equalsIgnoreCase(dto.getTexto()))
                    .findFirst().map(Alternativa::isCorreta).orElse(false)
            );
            return respostaRepository.save(resposta);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pergunta não encontrada"));
    }

    @GetMapping
    public List<RespostaResumoDTO> listarRespostas(@PathVariable Long perguntaId) {
        if (!perguntaRepository.existsById(perguntaId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pergunta não encontrada");
        }
        return respostaRepository.findAll().stream()
                .filter(r -> r.getPergunta() != null && r.getPergunta().getId().equals(perguntaId))
                .map(RespostaResumoDTO::from)
                .collect(Collectors.toList());
    }

    public record RespostaResumoDTO(Long id, String texto, boolean correta, java.time.LocalDateTime data) {
        public static RespostaResumoDTO from(Resposta r) {
            return new RespostaResumoDTO(r.getId(), r.getTexto(), r.isCorreta(), r.getData());
        }
    }
}
