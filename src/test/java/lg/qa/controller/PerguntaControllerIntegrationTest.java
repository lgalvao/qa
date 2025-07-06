package lg.qa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lg.qa.dto.NovaPerguntaDTO;
import lg.qa.dto.NovaPerguntaDTO.NovaAlternativaDTO;
import lg.qa.model.Categoria;
import lg.qa.repository.AlternativaRepository;
import lg.qa.repository.CategoriaRepository;
import lg.qa.repository.PerguntaRepository;
import lg.qa.repository.RespostaRepository;
import lg.qa.repository.RespostaDadaRepository;
import lg.qa.repository.ProvaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class PerguntaControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PerguntaRepository perguntaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private AlternativaRepository alternativaRepository;

    @Autowired
    private RespostaRepository respostaRepository;

    @Autowired
    private RespostaDadaRepository respostaDadaRepository;

    @Autowired
    private ProvaRepository provaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Categoria categoria;

    @BeforeEach
    void setup() {
        respostaDadaRepository.deleteAll(); respostaDadaRepository.flush();
        respostaRepository.deleteAll(); respostaRepository.flush();
        alternativaRepository.deleteAll(); alternativaRepository.flush();
        provaRepository.deleteAll(); provaRepository.flush();
        perguntaRepository.deleteAll(); perguntaRepository.flush();
        categoriaRepository.deleteAll(); categoriaRepository.flush();
        categoria = new Categoria();
        categoria.setNome("Matemática");
        categoria = categoriaRepository.save(categoria);
    }

    @Test
    void deveCriarPerguntaComSucesso() throws Exception {
        NovaPerguntaDTO dto = new NovaPerguntaDTO();
        dto.setTitulo("Quanto é 2+2?");
        dto.setDescricao("Soma simples");
        dto.setCategoriaId(categoria.getId());
        dto.setNivelDificuldade("Fácil");

        NovaAlternativaDTO a1 = new NovaAlternativaDTO();
        a1.setTexto("3"); a1.setCorreta(false);

        NovaAlternativaDTO a2 = new NovaAlternativaDTO();
        a2.setTexto("4"); a2.setCorreta(true);
        dto.setAlternativas(List.of(a1, a2));
        mockMvc.perform(post("/perguntas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Quanto é 2+2?"));
    }

    @Test
    void deveValidarCamposObrigatorios() throws Exception {
        NovaPerguntaDTO dto = new NovaPerguntaDTO();

        // Não preenche nada
        mockMvc.perform(post("/perguntas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").exists());
    }
} 