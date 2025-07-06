package lg.qa.service;

import lg.qa.dto.ResponderProvaRequest;
import lg.qa.dto.RespostaProvaResponse;
import lg.qa.model.*;
import lg.qa.repository.AlternativaRepository;
import lg.qa.repository.PerguntaRepository;
import lg.qa.repository.ProvaRepository;
import lg.qa.repository.RespostaDadaRepository;
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

class RespostaProvaServiceTest {
    @Mock
    private ProvaRepository provaRepository;

    @Mock
    private PerguntaRepository perguntaRepository;

    @Mock
    private AlternativaRepository alternativaRepository;

    @Mock
    private RespostaDadaRepository respostaDadaRepository;

    @InjectMocks
    private RespostaProvaService respostaProvaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveResponderProvaComSucesso() {
        Categoria categoria = Categoria.builder()
                .id(1L)
                .nome("Matemática")
                .build();
        Pergunta pergunta = Pergunta.builder()
                .id(10L)
                .titulo("Quanto é 2+2?")
                .alternativas(new java.util.ArrayList<>())
                .build();
        Alternativa alternativa = Alternativa.builder()
                .id(100L)
                .texto("4")
                .correta(true)
                .pergunta(pergunta)
                .build();
        pergunta.setAlternativas(List.of(alternativa));
        Prova prova = Prova.builder()
                .id(1L)
                .categoria(categoria)
                .nivelDificuldade("Fácil")
                .perguntas(List.of(pergunta))
                .respostasDadas(new java.util.ArrayList<>())
                .acertos(0)
                .erros(0)
                .dataCriacao(java.time.LocalDateTime.now())
                .build();
        when(provaRepository.findById(1L)).thenReturn(Optional.of(prova));
        when(alternativaRepository.findById(100L)).thenReturn(Optional.of(alternativa));
        when(respostaDadaRepository.save(any(RespostaDada.class))).thenAnswer(inv -> {
            RespostaDada rd = inv.getArgument(0);
            prova.getRespostasDadas().add(rd);
            return rd;
        });

        when(provaRepository.save(any(Prova.class))).thenAnswer(inv -> inv.getArgument(0));
        ResponderProvaRequest.RespostaDTO respDTO = new ResponderProvaRequest.RespostaDTO();
        respDTO.setPerguntaId(10L);
        respDTO.setAlternativaId(100L);
        ResponderProvaRequest req = new ResponderProvaRequest();
        req.setProvaId(1L);
        req.setRespostas(List.of(respDTO));
        RespostaProvaResponse resp = respostaProvaService.responderProva(req);
        assertThat(resp).isNotNull();
        assertThat(resp.getAcertos()).isEqualTo(1);
    }

    @Test
    void deveLancarExcecaoSeProvaNaoExiste() {
        when(provaRepository.findById(1L)).thenReturn(Optional.empty());
        ResponderProvaRequest req = new ResponderProvaRequest();
        req.setProvaId(1L);
        assertThatThrownBy(() -> respostaProvaService.responderProva(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Prova não encontrada");
    }

    @Test
    void deveLancarExcecaoSeProvaJaRespondida() {
        Prova prova = new Prova();
        prova.setId(1L);
        when(provaRepository.findById(1L)).thenReturn(Optional.of(prova));
        when(respostaDadaRepository.findByProvaId(1L)).thenReturn(List.of(new RespostaDada()));
        ResponderProvaRequest req = new ResponderProvaRequest();
        req.setProvaId(1L);
        assertThatThrownBy(() -> respostaProvaService.responderProva(req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("já foi respondida");
    }

    @Test
    void deveLancarExcecaoSeAlternativaNaoExiste() {
        Prova prova = new Prova();
        prova.setId(1L);
        Pergunta pergunta = new Pergunta();
        pergunta.setId(10L);
        prova.setPerguntas(List.of(pergunta));
        prova.setRespostasDadas(Collections.emptyList());
        when(provaRepository.findById(1L)).thenReturn(Optional.of(prova));
        when(alternativaRepository.findById(100L)).thenReturn(Optional.empty());
        ResponderProvaRequest.RespostaDTO respDTO = new ResponderProvaRequest.RespostaDTO();
        respDTO.setPerguntaId(10L);
        respDTO.setAlternativaId(100L);
        ResponderProvaRequest req = new ResponderProvaRequest();
        req.setProvaId(1L);
        req.setRespostas(List.of(respDTO));
        assertThatThrownBy(() -> respostaProvaService.responderProva(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Alternativa não encontrada");
    }

    @Test
    void deveLancarExcecaoSeAlternativaNaoPertenceAPergunta() {
        Prova prova = new Prova();
        prova.setId(1L);
        Pergunta pergunta = new Pergunta();
        pergunta.setId(10L);
        Alternativa alternativa = new Alternativa();
        alternativa.setId(100L);
        Pergunta outraPergunta = new Pergunta();
        outraPergunta.setId(99L);
        alternativa.setPergunta(outraPergunta); // diferente
        prova.setPerguntas(List.of(pergunta));
        prova.setRespostasDadas(new java.util.ArrayList<>());
        when(provaRepository.findById(1L)).thenReturn(Optional.of(prova));
        when(alternativaRepository.findById(100L)).thenReturn(Optional.of(alternativa));
        ResponderProvaRequest.RespostaDTO respDTO = new ResponderProvaRequest.RespostaDTO();
        respDTO.setPerguntaId(10L);
        respDTO.setAlternativaId(100L);
        ResponderProvaRequest req = new ResponderProvaRequest();
        req.setProvaId(1L);
        req.setRespostas(List.of(respDTO));
        assertThatThrownBy(() -> respostaProvaService.responderProva(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A alternativa não pertence à pergunta");
    }

    @Test
    void deveObterResultadoComSucesso() {
        Prova prova = new Prova();
        prova.setId(1L);
        prova.setAcertos(1);
        prova.setErros(0);
        prova.setDataCriacao(java.time.LocalDateTime.now());
        prova.setRespostasDadas(new java.util.ArrayList<>());
  
        Categoria cat = new Categoria();
        cat.setNome("Matemática");
        prova.setCategoria(cat);
  
        Pergunta pergunta = new Pergunta();
        pergunta.setId(10L);
        pergunta.setTitulo("Quanto é 2+2?");
  
        Alternativa alternativa = new Alternativa();
        alternativa.setId(100L);
        alternativa.setTexto("4");
        alternativa.setCorreta(true);
        alternativa.setPergunta(pergunta);
  
        pergunta.setAlternativas(List.of(alternativa));
        prova.setPerguntas(List.of(pergunta));
  
        RespostaDada respostaDada = RespostaDada.builder()
                .prova(prova)
                .pergunta(pergunta)
                .alternativaEscolhida(alternativa)
                .correta(true)
                .build();
        prova.setRespostasDadas(List.of(respostaDada));
  
        when(provaRepository.findById(1L)).thenReturn(Optional.of(prova));
        when(respostaDadaRepository.findByProvaId(1L)).thenReturn(List.of(respostaDada));
  
        RespostaProvaResponse resp = respostaProvaService.obterResultado(1L);
        assertThat(resp).isNotNull();
        assertThat(resp.getAcertos()).isEqualTo(1);
    }

    @Test
    void deveLancarExcecaoSeProvaNaoRespondida() {
        Prova prova = new Prova();
        prova.setId(1L);
  
        when(provaRepository.findById(1L)).thenReturn(Optional.of(prova));
        when(respostaDadaRepository.findByProvaId(1L)).thenReturn(Collections.emptyList());
  
        assertThatThrownBy(() -> respostaProvaService.obterResultado(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ainda não foi respondida");
    }

    @Test
    void deveLancarExcecaoSeProvaNaoExisteAoObterResultado() {
        when(provaRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> respostaProvaService.obterResultado(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Prova não encontrada");
    }
} 