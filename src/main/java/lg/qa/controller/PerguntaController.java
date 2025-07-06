package lg.qa.controller;

import jakarta.validation.Valid;
import lg.qa.dto.NovaPerguntaDTO;
import lg.qa.model.Alternativa;
import lg.qa.model.Categoria;
import lg.qa.model.Pergunta;
import lg.qa.repository.CategoriaRepository;
import lg.qa.repository.PerguntaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/perguntas")
@RequiredArgsConstructor
public class PerguntaController {
    private final PerguntaRepository perguntaRepository;
    private final CategoriaRepository categoriaRepository;

    @GetMapping
    public List<Pergunta> listar() {
        return perguntaRepository.findAll();
    }

    @PostMapping
    public Pergunta criar(@Valid @RequestBody NovaPerguntaDTO dto) {
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));
        Pergunta pergunta = new Pergunta();
        pergunta.setTitulo(dto.getTitulo());
        pergunta.setDescricao(dto.getDescricao());
        pergunta.setCategoria(categoria);
        pergunta.setNivelDificuldade(dto.getNivelDificuldade());
        List<Alternativa> alternativas = dto.getAlternativas().stream().map(a -> {
            Alternativa alt = new Alternativa();
            alt.setTexto(a.getTexto());
            alt.setCorreta(a.getCorreta());
            alt.setPergunta(pergunta);
            return alt;
        }).collect(Collectors.toList());
        pergunta.setAlternativas(alternativas);
        return perguntaRepository.save(pergunta);
    }

    @GetMapping("/{id}")
    public Pergunta buscar(@PathVariable Long id) {
        return perguntaRepository.findById(id).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        perguntaRepository.deleteById(id);
    }
}
