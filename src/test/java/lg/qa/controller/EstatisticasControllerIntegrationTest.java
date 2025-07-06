package lg.qa.controller;

import lg.qa.dto.EstatisticasDTO;
import lg.qa.model.*;
import lg.qa.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class EstatisticasControllerIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProvaRepository provaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private PerguntaRepository perguntaRepository;

    @Autowired
    private AlternativaRepository alternativaRepository;

    @Autowired
    private RespostaDadaRepository respostaDadaRepository;

    private String baseUrl;

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + port + "/api/estatisticas";
        respostaDadaRepository.deleteAll(); respostaDadaRepository.flush();
        alternativaRepository.deleteAll(); alternativaRepository.flush();
        provaRepository.deleteAll(); provaRepository.flush();
        perguntaRepository.deleteAll(); perguntaRepository.flush();
        categoriaRepository.deleteAll(); categoriaRepository.flush();
    }

    @Test
    void quandoNaoHaProvas_retornaEstatisticasZeradas() {
        ResponseEntity<EstatisticasDTO> response = restTemplate.getForEntity(
                baseUrl, EstatisticasDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        EstatisticasDTO estatisticas = response.getBody();
        assertThat(estatisticas).isNotNull();
        assertThat(estatisticas.getGerais().getTotalProvasRealizadas()).isZero();
    }

    @Test
    void quandoExistemProvas_retornaEstatisticasCorretas() {
        // Cria dados básicos
        Categoria categoria = criarCategoria("Matemática");
        Pergunta pergunta = criarPerguntaComAlternativas("Quanto é 2+2?", "Fácil", categoria);
        Prova prova = criarProvaRespondida(categoria, pergunta);
        provaRepository.save(prova);

        // Chama o endpoint
        ResponseEntity<EstatisticasDTO> response = restTemplate.getForEntity(
                baseUrl, EstatisticasDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getGerais().getTotalProvasRealizadas()).isEqualTo(1);
    }

    private Categoria criarCategoria(String nome) {
        Categoria categoria = new Categoria();
        categoria.setNome(nome);
        return categoriaRepository.save(categoria);
    }

    private Pergunta criarPerguntaComAlternativas(String titulo, String nivelDificuldade, Categoria categoria) {
        Pergunta pergunta = new Pergunta();
        pergunta.setTitulo(titulo);
        pergunta.setNivelDificuldade(nivelDificuldade);
        pergunta.setCategoria(categoria);
        pergunta = perguntaRepository.save(pergunta);

        // Cria alternativas
        Alternativa alternativaCorreta = new Alternativa();
        alternativaCorreta.setTexto("4");
        alternativaCorreta.setCorreta(true);
        alternativaCorreta.setPergunta(pergunta);

        Alternativa alternativaIncorreta = new Alternativa();
        alternativaIncorreta.setTexto("3");
        alternativaIncorreta.setCorreta(false);
        alternativaIncorreta.setPergunta(pergunta);

        pergunta.setAlternativas(List.of(alternativaCorreta, alternativaIncorreta));
        alternativaRepository.saveAll(pergunta.getAlternativas());

        return pergunta;
    }

    private Prova criarProvaRespondida(Categoria categoria, Pergunta pergunta) {
        Prova prova = new Prova();
        prova.setNivelDificuldade("Fácil");
        prova.setCategoria(categoria);
        prova.setPerguntas(List.of(pergunta));
        prova.setAcertos(1);
        prova.setErros(0);
        prova = provaRepository.save(prova);

        // Cria resposta
        Alternativa alternativaCorreta = pergunta.getAlternativas().stream()
                .filter(Alternativa::isCorreta)
                .findFirst()
                .orElseThrow();

        RespostaDada resposta = new RespostaDada();
        resposta.setProva(prova);
        resposta.setPergunta(pergunta);
        resposta.setAlternativaEscolhida(alternativaCorreta);
        resposta.setCorreta(true);
        respostaDadaRepository.save(resposta);
        prova.getRespostasDadas().add(resposta);

        return prova;
    }
}
