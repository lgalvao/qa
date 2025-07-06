package lg.qa.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.annotation.DirtiesContext;
import lg.qa.repository.ProvaRepository;
import lg.qa.repository.PerguntaRepository;
import lg.qa.repository.CategoriaRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import lg.qa.model.Categoria;
import lg.qa.model.Pergunta;
import lg.qa.model.Alternativa;
import lg.qa.model.Prova;
import lg.qa.repository.AlternativaRepository;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RespostaProvaControllerIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProvaRepository provaRepository;

    @Autowired
    private PerguntaRepository perguntaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private AlternativaRepository alternativaRepository;

    @org.junit.jupiter.api.BeforeEach
    void setup() {
        provaRepository.deleteAll(); provaRepository.flush();
        perguntaRepository.deleteAll(); perguntaRepository.flush();
        categoriaRepository.deleteAll(); categoriaRepository.flush();
    }

    @Test
    public void quandoProvaNaoExiste_entaoRetorna404() {
        String url = "http://localhost:" + port + "/api/provas/respostas/999/resultado";
        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, null, String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void quandoObterResultadoDeProvaNaoRespondida_entaoRetorna400() {
        Categoria cat = new Categoria();
        cat.setNome("Matemática");
        categoriaRepository.save(cat);
        
        Pergunta p = new Pergunta();
        p.setTitulo("Quanto é 2+2?");
        p.setCategoria(cat);
        perguntaRepository.save(p);
        
        Prova prova = new Prova();
        prova.setCategoria(cat);
        prova.setPerguntas(List.of(p));
        provaRepository.save(prova);
        String url = "http://localhost:" + port + "/api/provas/respostas/" + prova.getId() + "/resultado";
        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, null, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void quandoResponderProvaJaRespondida_entaoRetorna400() throws Exception {
        Categoria cat = new Categoria();
        cat.setNome("Matemática");
        categoriaRepository.save(cat);
        Pergunta p = new Pergunta();
        p.setTitulo("Quanto é 2+2?");
        p.setCategoria(cat);
        perguntaRepository.save(p);

        Alternativa alt = new Alternativa();
        alt.setTexto("4");
        alt.setCorreta(true);
        alt.setPergunta(p);
        alternativaRepository.save(alt);
        p.setAlternativas(List.of(alt));

        Prova prova = new Prova();
        prova.setCategoria(cat);
        prova.setPerguntas(List.of(p));
        provaRepository.save(prova);

        String url = "http://localhost:" + port + "/api/provas/respostas";
        String body = "{" +
                "\"provaId\":" + prova.getId() + "," +
                "\"respostas\":[{" +
                "\"perguntaId\":" + p.getId() + "," +
                "\"alternativaId\":" + alt.getId() + "}]}";

                HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        // Primeira resposta (deve ser sucesso)
        ResponseEntity<String> resp1 = restTemplate.postForEntity(url, entity, String.class);
        assertThat(resp1.getStatusCode().is2xxSuccessful()).isTrue();

        // Recarrega a prova do banco
        provaRepository.findById(prova.getId()).orElseThrow();

        // Segunda resposta (deve ser erro 400)
        ResponseEntity<String> resp2 = restTemplate.postForEntity(url, entity, String.class);
        assertThat(resp2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void quandoResponderProvaComSucesso_entaoRetornaResultado() throws Exception {
        Categoria cat = new Categoria();
        cat.setNome("Matemática");
        categoriaRepository.save(cat);

        Pergunta p = new Pergunta();
        p.setTitulo("Quanto é 2+2?");
        p.setCategoria(cat);
        perguntaRepository.save(p);

        Alternativa alt = new Alternativa();
        alt.setTexto("4");
        alt.setCorreta(true);
        alt.setPergunta(p);
        alternativaRepository.save(alt);
        p.setAlternativas(List.of(alt));

        Prova prova = new Prova();
        prova.setCategoria(cat);
        prova.setPerguntas(List.of(p));
        provaRepository.save(prova);

        String url = "http://localhost:" + port + "/api/provas/respostas";
        String body = "{" +
                "\"provaId\":" + prova.getId() + "," +
                "\"respostas\":[{" +
                "\"perguntaId\":" + p.getId() + "," +
                "\"alternativaId\":" + alt.getId() + "}]}";

                HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> resp = restTemplate.postForEntity(url, entity, String.class);

        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody()).contains("acertos");
    }
}
