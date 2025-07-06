package lg.qa.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lg.qa.model.Categoria;
import lg.qa.model.Pergunta;
import lg.qa.model.Prova;
import lg.qa.repository.AlternativaRepository;
import lg.qa.repository.CategoriaRepository;
import lg.qa.repository.PerguntaRepository;
import lg.qa.repository.ProvaRepository;
import lg.qa.repository.RespostaRepository;
import lg.qa.repository.RespostaDadaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ProvaControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProvaRepository provaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private PerguntaRepository perguntaRepository;

    @Autowired
    private AlternativaRepository alternativaRepository;

    @Autowired
    private RespostaRepository respostaRepository;

    @Autowired
    private RespostaDadaRepository respostaDadaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        respostaDadaRepository.deleteAll(); respostaDadaRepository.flush();
        respostaRepository.deleteAll(); respostaRepository.flush();
        alternativaRepository.deleteAll(); alternativaRepository.flush();
        provaRepository.deleteAll(); provaRepository.flush();
        perguntaRepository.deleteAll(); perguntaRepository.flush();
        categoriaRepository.deleteAll(); categoriaRepository.flush();
    }

    @Test
    void deveListarProvasResumidas() throws Exception {
        Categoria cat = new Categoria();
        cat.setNome("Matemática");
        categoriaRepository.save(cat);

        Pergunta p = new Pergunta();
        p.setTitulo("Quanto é 2+2?");
        p.setCategoria(cat);
        p.setNivelDificuldade("Fácil");
        perguntaRepository.save(p);

        Prova prova = new Prova();
        prova.setCategoria(cat);
        prova.setNivelDificuldade("Fácil");
        prova.setPerguntas(List.of(p));
        prova.setAcertos(1);
        prova.setErros(0);
        provaRepository.save(prova);

        String json = mockMvc.perform(get("/api/provas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<?> provas = objectMapper.readValue(json, new TypeReference<List<?>>(){});
        assertThat(provas).isNotEmpty();
    }

    @Test
    void deveRetornar404AoBuscarProvaInexistente() throws Exception {
        mockMvc.perform(get("/api/provas/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornarErroAoCriarProvaComCategoriaInexistente() throws Exception {
        String body = "{" +
                "\"categoriaId\":9999," +
                "\"nivelDificuldade\":\"Fácil\"," +
                "\"quantidadePerguntas\":1" +
                "}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/provas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornarErroAoCriarProvaSemPerguntasDisponiveis() throws Exception {
        Categoria cat = new Categoria();
        cat.setNome("Física");
        categoriaRepository.save(cat);
        String body = "{" +
                "\"categoriaId\":" + cat.getId() + "," +
                "\"nivelDificuldade\":\"Avançado\"," +
                "\"quantidadePerguntas\":1" +
                "}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/provas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }
} 