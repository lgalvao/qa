package lg.qa.service;

import lg.qa.dto.EstatisticasDTO;
import lg.qa.model.Prova;
import lg.qa.repository.ProvaRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EstatisticasService {
    private final ProvaRepository provaRepository;

    public EstatisticasService(ProvaRepository provaRepository) {
        this.provaRepository = provaRepository;
    }

    public EstatisticasDTO obterEstatisticas() {
        List<Prova> todasProvas = provaRepository.findAll();
        
        // Filtra apenas provas que foram respondidas
        List<Prova> provasRespondidas = todasProvas.stream()
            .filter(p -> !p.getRespostasDadas().isEmpty())
            .toList();

        // Estatísticas gerais
        EstatisticasDTO.EstatisticasGeraisDTO gerais = calcularEstatisticasGerais(provasRespondidas);
        
        // Estatísticas por categoria
        List<EstatisticasDTO.EstatisticasCategoriaDTO> porCategoria = calcularEstatisticasPorCategoria(provasRespondidas);
        
        // Estatísticas por nível de dificuldade
        List<EstatisticasDTO.EstatisticasDificuldadeDTO> porDificuldade = calcularEstatisticasPorDificuldade(provasRespondidas);

        return new EstatisticasDTO(gerais, porCategoria, porDificuldade);
    }

    private EstatisticasDTO.EstatisticasGeraisDTO calcularEstatisticasGerais(List<Prova> provas) {
        if (provas.isEmpty()) {
            return new EstatisticasDTO.EstatisticasGeraisDTO(0, 0, 0, 0);
        }

        int totalProvas = provas.size();
        double totalAcertos = provas.stream().mapToInt(Prova::getAcertos).sum();
        double totalErros = provas.stream().mapToInt(Prova::getErros).sum();
        
        double mediaAcertos = totalAcertos / totalProvas;
        double mediaErros = totalErros / totalProvas;
        double taxaAcertoGeral = (totalAcertos / (totalAcertos + totalErros)) * 100;

        return new EstatisticasDTO.EstatisticasGeraisDTO(
            totalProvas,
            mediaAcertos,
            mediaErros,
            Double.isNaN(taxaAcertoGeral) ? 0 : taxaAcertoGeral
        );
    }

    private List<EstatisticasDTO.EstatisticasCategoriaDTO> calcularEstatisticasPorCategoria(List<Prova> provas) {
        // Agrupa as provas por categoria
        Map<String, List<Prova>> provasPorCategoria = provas.stream()
            .collect(Collectors.groupingBy(p -> p.getCategoria().getNome()));

        // Calcula estatísticas para cada categoria
        return provasPorCategoria.entrySet().stream()
            .map(entry -> {
                String categoria = entry.getKey();
                List<Prova> provasCategoria = entry.getValue();
                
                int totalProvas = provasCategoria.size();
                double totalAcertos = provasCategoria.stream().mapToInt(Prova::getAcertos).sum();
                double totalErros = provasCategoria.stream().mapToInt(Prova::getErros).sum();
                double totalRespostas = totalAcertos + totalErros;
                
                double mediaAcertos = totalProvas > 0 ? totalAcertos / totalProvas : 0;
                double mediaErros = totalProvas > 0 ? totalErros / totalProvas : 0;
                double taxaAcerto = totalRespostas > 0 ? (totalAcertos / totalRespostas) * 100 : 0;
                
                return new EstatisticasDTO.EstatisticasCategoriaDTO(
                    categoria, totalProvas, mediaAcertos, mediaErros, taxaAcerto
                );
            })
            .sorted(Comparator.comparing(EstatisticasDTO.EstatisticasCategoriaDTO::getTaxaAcerto).reversed())
            .collect(Collectors.toList());
    }

    private List<EstatisticasDTO.EstatisticasDificuldadeDTO> calcularEstatisticasPorDificuldade(List<Prova> provas) {
        // Agrupa as provas por nível de dificuldade
        Map<String, List<Prova>> provasPorDificuldade = provas.stream()
            .collect(Collectors.groupingBy(Prova::getNivelDificuldade));

        // Calcula estatísticas para cada nível de dificuldade
        return provasPorDificuldade.entrySet().stream()
            .map(entry -> {
                String dificuldade = entry.getKey();
                List<Prova> provasDificuldade = entry.getValue();
                
                int totalProvas = provasDificuldade.size();
                double totalAcertos = provasDificuldade.stream().mapToInt(Prova::getAcertos).sum();
                double totalErros = provasDificuldade.stream().mapToInt(Prova::getErros).sum();
                double totalRespostas = totalAcertos + totalErros;
                
                double mediaAcertos = totalProvas > 0 ? totalAcertos / totalProvas : 0;
                double mediaErros = totalProvas > 0 ? totalErros / totalProvas : 0;
                double taxaAcerto = totalRespostas > 0 ? (totalAcertos / totalRespostas) * 100 : 0;
                
                return new EstatisticasDTO.EstatisticasDificuldadeDTO(
                    dificuldade, totalProvas, mediaAcertos, mediaErros, taxaAcerto
                );
            })
            .sorted(Comparator.comparing(EstatisticasDTO.EstatisticasDificuldadeDTO::getTaxaAcerto).reversed())
            .collect(Collectors.toList());
    }
}
