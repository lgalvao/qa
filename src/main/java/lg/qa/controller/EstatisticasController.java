package lg.qa.controller;

import lg.qa.dto.EstatisticasDTO;
import lg.qa.service.EstatisticasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/estatisticas")
@RequiredArgsConstructor
public class EstatisticasController {
    private final EstatisticasService estatisticasService;

    @GetMapping
    public ResponseEntity<EstatisticasDTO> obterEstatisticas() {
        EstatisticasDTO estatisticas = estatisticasService.obterEstatisticas();
        return ResponseEntity.ok(estatisticas);
    }
    
    @GetMapping("/categorias")
    public ResponseEntity<List<EstatisticasDTO.EstatisticasCategoriaDTO>> obterEstatisticasPorCategoria() {
        EstatisticasDTO estatisticas = estatisticasService.obterEstatisticas();
        return ResponseEntity.ok(estatisticas.getPorCategoria());
    }
    
    @GetMapping("/dificuldade")
    public ResponseEntity<List<EstatisticasDTO.EstatisticasDificuldadeDTO>> obterEstatisticasPorDificuldade() {
        EstatisticasDTO estatisticas = estatisticasService.obterEstatisticas();
        return ResponseEntity.ok(estatisticas.getPorDificuldade());
    }
}
