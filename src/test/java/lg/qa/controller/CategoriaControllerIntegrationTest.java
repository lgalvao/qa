package lg.qa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lg.qa.dto.NovaCategoriaDTO;
import lg.qa.repository.AlternativaRepository;
import lg.qa.repository.CategoriaRepository;
import lg.qa.repository.RespostaRepository;
import lg.qa.repository.RespostaDadaRepository;
import lg.qa.repository.ProvaRepository;
import lg.qa.repository.PerguntaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.annotation.DirtiesContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class CategoriaControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

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
    private PerguntaRepository perguntaRepository;

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
    void deveCriarCategoriaComSucesso() throws Exception {
        NovaCategoriaDTO dto = new NovaCategoriaDTO();
        dto.setNome("Matemática");
        mockMvc.perform(post("/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Matemática"));
    }

    @Test
    void deveValidarNomeObrigatorio() throws Exception {
        NovaCategoriaDTO dto = new NovaCategoriaDTO();
        dto.setNome("");
        mockMvc.perform(post("/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").exists());
    }
} 