package lg.qa.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstatisticasDTO {
    private EstatisticasGeraisDTO gerais;
    private List<EstatisticasCategoriaDTO> porCategoria;
    private List<EstatisticasDificuldadeDTO> porDificuldade;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EstatisticasGeraisDTO {
        private int totalProvasRealizadas;
        private double mediaAcertos;
        private double mediaErros;
        private double taxaAcertoGeral;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EstatisticasCategoriaDTO {
        private String categoria;
        private int totalProvas;
        private double mediaAcertos;
        private double mediaErros;
        private double taxaAcerto;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EstatisticasDificuldadeDTO {
        private String nivelDificuldade;
        private int totalProvas;
        private double mediaAcertos;
        private double mediaErros;
        private double taxaAcerto;
    }
}
