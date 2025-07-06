package lg.qa.service;

import lg.qa.dto.CriarProvaRequest;
import lg.qa.dto.ProvaResponse;
import lg.qa.model.Categoria;
import lg.qa.model.Pergunta;
import lg.qa.model.Prova;
import lg.qa.repository.CategoriaRepository;
import lg.qa.repository.PerguntaRepository;
import lg.qa.repository.ProvaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ProvaServiceTest {
    @Mock
    private ProvaRepository provaRepository;

    @Mock
    private PerguntaRepository perguntaRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProvaService provaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveCriarProvaComSucesso() {
        Categoria cat = new Categoria();
        cat.setId(1L);
        cat.setNome("Matemática");

        Pergunta p = new Pergunta();
        p.setId(10L);
        p.setTitulo("Quanto é 2+2?");
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(cat));
        when(perguntaRepository.buscarAleatoriasPorCategoriaENivel(1L, "Fácil", 1)).thenReturn(List.of(p));
        when(provaRepository.save(any(Prova.class))).thenAnswer(inv -> {
            Prova prova = inv.getArgument(0);
            prova.setId(100L);
            return prova;
        });

        CriarProvaRequest req = new CriarProvaRequest();
        req.setCategoriaId(1L);
        req.setNivelDificuldade("Fácil");
        req.setQuantidadePerguntas(1);
        ProvaResponse resp = provaService.criarProva(req);
        assertThat(resp).isNotNull();
        assertThat(resp.getId()).isEqualTo(100L);
        assertThat(resp.getCategoria()).isEqualTo("Matemática");
        assertThat(resp.getPerguntas()).hasSize(1);
    }

    @Test
    void deveLancarExcecaoSeCategoriaNaoExiste() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());
        
        CriarProvaRequest req = new CriarProvaRequest();
        req.setCategoriaId(1L);
        req.setNivelDificuldade("Fácil");
        req.setQuantidadePerguntas(1);
        
        assertThatThrownBy(() -> provaService.criarProva(req))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Categoria não encontrada");
    }

    @Test
    void deveLancarExcecaoSeNaoHaPerguntasDisponiveis() {
        Categoria cat = new Categoria();
        cat.setId(1L);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(cat));
        when(perguntaRepository.buscarAleatoriasPorCategoriaENivel(1L, "Fácil", 1)).thenReturn(Collections.emptyList());

        CriarProvaRequest req = new CriarProvaRequest();
        req.setCategoriaId(1L);
        req.setNivelDificuldade("Fácil");
        req.setQuantidadePerguntas(1);
        
        assertThatThrownBy(() -> provaService.criarProva(req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Não há perguntas disponíveis");
    }

    @Test
    void deveListarTodasProvas() {
        Prova prova = new Prova();
        prova.setId(1L);

        Categoria cat = new Categoria();
        cat.setNome("Matemática");
        prova.setCategoria(cat);
        prova.setNivelDificuldade("Fácil");

        Pergunta p = new Pergunta();
        p.setId(10L);
        p.setTitulo("Quanto é 2+2?");
        prova.setPerguntas(List.of(p));
        
        when(provaRepository.findAll()).thenReturn(List.of(prova));
        
        List<ProvaResponse> resp = provaService.listarTodas();
        assertThat(resp).hasSize(1);
        assertThat(resp.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void deveObterProvaPorId() {
        Prova prova = new Prova();
        prova.setId(1L);

        Categoria cat = new Categoria();
        cat.setNome("Matemática");
        prova.setCategoria(cat);
        prova.setNivelDificuldade("Fácil");

        Pergunta p = new Pergunta();
        p.setId(10L);
        p.setTitulo("Quanto é 2+2?");
        prova.setPerguntas(List.of(p));

        when(provaRepository.findById(1L)).thenReturn(Optional.of(prova));

        ProvaResponse resp = provaService.obterPorId(1L);
        assertThat(resp).isNotNull();
        assertThat(resp.getId()).isEqualTo(1L);
    }

    @Test
    void deveLancarExcecaoSeProvaNaoExiste() {
        when(provaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> provaService.obterPorId(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Prova não encontrada");
    }
} 