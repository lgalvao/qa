package lg.qa.controller;

import jakarta.validation.Valid;
import lg.qa.dto.CriarProvaRequest;
import lg.qa.dto.ProvaResponse;
import lg.qa.model.Prova;
import lg.qa.repository.ProvaRepository;
import lg.qa.service.ProvaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/provas")
@RequiredArgsConstructor
public class ProvaController {
    private final ProvaService provaService;
    private final ProvaRepository provaRepository;

    @PostMapping
    public ResponseEntity<ProvaResponse> criarProva(@Valid @RequestBody CriarProvaRequest request) {
        ProvaResponse prova = provaService.criarProva(request);
        return ResponseEntity
            .created(URI.create("/api/provas/" + prova.getId()))
            .body(prova);
    }

    @GetMapping
    public List<ProvaResumoDTO> listarProvas() {
        return provaRepository.findAll().stream().map(ProvaResumoDTO::from).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ProvaResponse obterProva(@PathVariable Long id) {
        return provaService.obterPorId(id);
    }

    public record ProvaResumoDTO(Long id, String categoria, String nivelDificuldade, Integer totalPerguntas, Integer acertos, Integer erros) {
        public static ProvaResumoDTO from(Prova p) {
            return new ProvaResumoDTO(
                p.getId(),
                p.getCategoria() != null ? p.getCategoria().getNome() : null,
                p.getNivelDificuldade(),
                p.getPerguntas() != null ? p.getPerguntas().size() : 0,
                p.getAcertos(),
                p.getErros()
            );
        }
    }
}
