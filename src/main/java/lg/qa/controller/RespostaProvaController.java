package lg.qa.controller;

import jakarta.validation.Valid;
import lg.qa.dto.ResponderProvaRequest;
import lg.qa.dto.RespostaProvaResponse;
import lg.qa.service.RespostaProvaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

@RestController
@RequestMapping("/api/provas/respostas")
@RequiredArgsConstructor
public class RespostaProvaController {
    private final RespostaProvaService respostaProvaService;

    @PostMapping
    public ResponseEntity<RespostaProvaResponse> responderProva(
            @Valid @RequestBody ResponderProvaRequest request) {
        try {
            RespostaProvaResponse resposta = respostaProvaService.responderProva(request);
            return ResponseEntity
                    .created(URI.create("/api/provas/" + resposta.getId() + "/resultado"))
                    .body(resposta);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping("/{provaId}/resultado")
    public ResponseEntity<RespostaProvaResponse> obterResultado(@PathVariable Long provaId) {
        try {
            RespostaProvaResponse resposta = respostaProvaService.obterResultado(provaId);
            return ResponseEntity.ok(resposta);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
