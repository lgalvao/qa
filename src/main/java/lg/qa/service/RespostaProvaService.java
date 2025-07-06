package lg.qa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import lg.qa.dto.ResponderProvaRequest;
import lg.qa.dto.RespostaProvaResponse;
import lg.qa.model.*;
import lg.qa.repository.AlternativaRepository;
import lg.qa.repository.ProvaRepository;
import lg.qa.repository.RespostaDadaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RespostaProvaService {
    private final ProvaRepository provaRepository;
    private final AlternativaRepository alternativaRepository;
    private final RespostaDadaRepository respostaDadaRepository;

    @Transactional
    public RespostaProvaResponse responderProva(ResponderProvaRequest request) {
        // Busca a prova
        Prova prova = provaRepository.findById(request.getProvaId())
            .orElseThrow(() -> new IllegalArgumentException("Prova não encontrada"));

        // Verifica se a prova já foi respondida consultando o banco
        if (!respostaDadaRepository.findByProvaId(prova.getId()).isEmpty()) {
            throw new IllegalStateException("Esta prova já foi respondida");
        }

        // Mapeia as respostas por ID da pergunta para acesso rápido
        Map<Long, ResponderProvaRequest.RespostaDTO> respostasPorPergunta = request.getRespostas().stream()
            .collect(Collectors.toMap(ResponderProvaRequest.RespostaDTO::getPerguntaId, r -> r));

        // Para cada pergunta da prova, processa a resposta
        for (Pergunta pergunta : prova.getPerguntas()) {
            ResponderProvaRequest.RespostaDTO resposta = respostasPorPergunta.get(pergunta.getId());
            
            if (resposta != null) {
                // Encontra a alternativa selecionada
                Alternativa alternativa = alternativaRepository.findById(resposta.getAlternativaId())
                    .orElseThrow(() -> new IllegalArgumentException("Alternativa não encontrada"));

                // Verifica se a alternativa pertence à pergunta
                if (!alternativa.getPergunta().getId().equals(pergunta.getId())) {
                    throw new IllegalArgumentException("A alternativa não pertence à pergunta informada");
                }

                // Cria e salva a resposta
                RespostaDada respostaDada = RespostaDada.builder()
                    .prova(prova)
                    .pergunta(pergunta)
                    .alternativaEscolhida(alternativa)
                    .correta(alternativa.isCorreta())
                    .build();

                respostaDadaRepository.save(respostaDada);
                
                // Atualiza contadores de acertos/erros
                if (alternativa.isCorreta()) {
                    prova.setAcertos(prova.getAcertos() + 1);
                } else {
                    prova.setErros(prova.getErros() + 1);
                }
            }
        }

        // Atualiza a prova com os resultados
        prova = provaRepository.save(prova);

        // Monta a resposta
        return toRespostaProvaResponse(prova);
    }

    private RespostaProvaResponse toRespostaProvaResponse(Prova prova) {
        // Garante que as alternativas de cada pergunta estão carregadas
        if (prova.getPerguntas() != null) {
            for (Pergunta pergunta : prova.getPerguntas()) {
                if (pergunta.getAlternativas() != null) {
                    //noinspection ResultOfMethodCallIgnored
                    pergunta.getAlternativas().size(); // força o carregamento
                }
            }
        }
        List<RespostaProvaResponse.RespostaDTO> respostas = prova.getRespostasDadas().stream()
            .map(rd -> new RespostaProvaResponse.RespostaDTO(
                rd.getPergunta().getId(),
                rd.getPergunta().getTitulo(),
                rd.getAlternativaEscolhida().getId(),
                rd.getAlternativaEscolhida().getTexto(),
                rd.isCorreta()
            ))
            .collect(Collectors.toList());

        return new RespostaProvaResponse(
            prova.getId(),
            "Prova de " + prova.getCategoria().getNome(),
            prova.getCategoria().getNome(),
            prova.getNivelDificuldade(),
            prova.getPerguntas().size(),
            prova.getAcertos(),
            prova.getErros(),
            LocalDateTime.now(),
            respostas
        );
    }
    
    /**
     * Obtém o resultado de uma prova já respondida.
     * @param provaId ID da prova
     * @return Resposta com o resultado da prova
     * @throws IllegalArgumentException se a prova não for encontrada
     * @throws IllegalStateException se a prova ainda não tiver sido respondida
     */
    @Transactional(readOnly = true)
    public RespostaProvaResponse obterResultado(Long provaId) {
        // Busca a prova pelo método padrão
        Prova prova = provaRepository.findById(provaId)
            .orElseThrow(() -> new IllegalArgumentException("Prova não encontrada"));
        
        // Força o carregamento das respostas dadas e suas relações
        List<RespostaDada> respostas = respostaDadaRepository.findByProvaId(provaId);
        
        // Verifica se a prova já foi respondida
        if (respostas.isEmpty()) {
            throw new IllegalStateException("Esta prova ainda não foi respondida");
        }
        
        // Atualiza a lista de respostas da prova para garantir que esteja sincronizada
        prova.setRespostasDadas(respostas);
        
        // Retorna a resposta formatada
        return toRespostaProvaResponse(prova);
    }
}
