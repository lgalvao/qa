package lg.qa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lg.qa.dto.NovaRespostaDTO;
import lg.qa.model.Alternativa;
import lg.qa.model.Categoria;
import lg.qa.model.Pergunta;
import lg.qa.repository.CategoriaRepository;
import lg.qa.repository.PerguntaRepository;
import lg.qa.repository.RespostaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RespostaControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private PerguntaRepository perguntaRepository;

    @Autowired
    private RespostaRepository respostaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private lg.qa.repository.ProvaRepository provaRepository;

    private Long perguntaId;

    @BeforeEach
    void setup() {
        // Limpa dados
        respostaRepository.deleteAll();
        respostaRepository.flush();
        provaRepository.deleteAll();
        provaRepository.flush();
        perguntaRepository.deleteAll();
        perguntaRepository.flush();
        categoriaRepository.deleteAll();
        categoriaRepository.flush();

        // Cria dados de teste
        Categoria cat = new Categoria();
        cat.setNome("Matemática");
        categoriaRepository.save(cat);

        Pergunta p = new Pergunta();
        p.setTitulo("Quanto é 2+2?");
        p.setCategoria(cat);
        p.setNivelDificuldade("Fácil");

        Alternativa a1 = new Alternativa();
        a1.setTexto("3");
        a1.setCorreta(false);
        a1.setPergunta(p);

        Alternativa a2 = new Alternativa();
        a2.setTexto("4");
        a2.setCorreta(true);
        a2.setPergunta(p);

        p.setAlternativas(Arrays.asList(a1, a2));
        p = perguntaRepository.saveAndFlush(p);
        perguntaRepository.flush();
        // Adiciona flush explícito nas alternativas
        // alternativaRepository.flush();
        perguntaId = p.getId();
    }

    @Test
    void deveMarcarRespostaComoCorreta() throws Exception {
        NovaRespostaDTO dto = new NovaRespostaDTO();
        dto.setTexto("4");
        dto.setData(LocalDateTime.now());
        mockMvc.perform(post("/perguntas/" + perguntaId + "/respostas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correta").value(true));
    }

    @Test
    void deveValidarCamposObrigatorios() throws Exception {
        NovaRespostaDTO dto = new NovaRespostaDTO();
        // Não preenche nada
        mockMvc.perform(post("/perguntas/" + perguntaId + "/respostas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").exists());
    }

    @Test
    void deveListarRespostasDeUmaPergunta() throws Exception {
        // Cria uma resposta
        NovaRespostaDTO dto = new NovaRespostaDTO();
        dto.setTexto("4");
        dto.setData(LocalDateTime.now());
        mockMvc.perform(post("/perguntas/" + perguntaId + "/respostas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        // Lista respostas
        String json = mockMvc.perform(get("/perguntas/" + perguntaId + "/respostas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        java.util.List<?> respostas = objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<java.util.List<?>>(){});
        org.assertj.core.api.Assertions.assertThat(respostas).isNotEmpty();
    }

    @Test
    void deveMarcarRespostaComoIncorreta() throws Exception {
        NovaRespostaDTO dto = new NovaRespostaDTO();
        dto.setTexto("3"); // alternativa incorreta
        dto.setData(LocalDateTime.now());
        mockMvc.perform(post("/perguntas/" + perguntaId + "/respostas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correta").value(false));
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaRespostas() throws Exception {
        String json = mockMvc.perform(get("/perguntas/" + perguntaId + "/respostas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        java.util.List<?> respostas = objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<java.util.List<?>>(){});
        org.assertj.core.api.Assertions.assertThat(respostas).isEmpty();
    }

    @Test
    void deveRetornar404ParaPerguntaInexistente() throws Exception {
        NovaRespostaDTO dto = new NovaRespostaDTO();
        dto.setTexto("4");
        dto.setData(LocalDateTime.now());
        mockMvc.perform(post("/perguntas/999999/respostas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/perguntas/999999/respostas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void devePermitirRespostasDuplicadas() throws Exception {
        NovaRespostaDTO dto = new NovaRespostaDTO();
        dto.setTexto("4");
        dto.setData(LocalDateTime.now());
        mockMvc.perform(post("/perguntas/" + perguntaId + "/respostas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
        mockMvc.perform(post("/perguntas/" + perguntaId + "/respostas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}