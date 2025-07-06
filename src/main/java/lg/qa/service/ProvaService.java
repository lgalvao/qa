package lg.qa.service;

import lg.qa.dto.CriarProvaRequest;
import lg.qa.dto.ProvaResponse;
import lg.qa.model.Alternativa;
import lg.qa.model.Categoria;
import lg.qa.model.Pergunta;
import lg.qa.model.Prova;
import lg.qa.repository.CategoriaRepository;
import lg.qa.repository.PerguntaRepository;
import lg.qa.repository.ProvaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProvaService {

    private final ProvaRepository provaRepository;
    private final PerguntaRepository perguntaRepository;
    private final CategoriaRepository categoriaRepository;

    public ProvaService(ProvaRepository provaRepository, 
                       PerguntaRepository perguntaRepository,
                       CategoriaRepository categoriaRepository) {
        this.provaRepository = provaRepository;
        this.perguntaRepository = perguntaRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public ProvaResponse criarProva(CriarProvaRequest request) {
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"));

        // Busca perguntas aleatórias baseadas nos critérios
        List<Pergunta> perguntas = perguntaRepository.buscarAleatoriasPorCategoriaENivel(
            request.getCategoriaId(), 
            request.getNivelDificuldade(), 
            request.getQuantidadePerguntas()
        );

        if (perguntas.isEmpty()) {
            throw new IllegalStateException("Não há perguntas disponíveis para os critérios fornecidos");
        }

        // Cria e salva a prova
        Prova prova = new Prova();
        prova.setCategoria(categoria);
        prova.setNivelDificuldade(request.getNivelDificuldade());
        prova.setPerguntas(perguntas);
        
        Prova provaSalva = provaRepository.save(prova);
        
        return toProvaResponse(provaSalva);
    }

    public List<ProvaResponse> listarTodas() {
        return provaRepository.findAll().stream()
            .map(this::toProvaResponse)
            .collect(Collectors.toList());
    }

    public ProvaResponse obterPorId(Long id) {
        return provaRepository.findById(id)
            .map(this::toProvaResponse)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prova não encontrada"));
    }

    private ProvaResponse toProvaResponse(Prova prova) {
        List<ProvaResponse.PerguntaResumoDTO> perguntasDTO = prova.getPerguntas().stream()
            .map(p -> new ProvaResponse.PerguntaResumoDTO(
                p.getId(),
                p.getTitulo(),
                p.getAlternativas().stream()
                    .map(Alternativa::getTexto)
                    .collect(Collectors.toList())
            ))
            .collect(Collectors.toList());

        return new ProvaResponse(
            prova.getId(),
            "Prova de " + prova.getCategoria().getNome(),
            prova.getCategoria().getNome(),
            prova.getNivelDificuldade(),
            prova.getDataCriacao(),
            perguntasDTO
        );
    }
}
