package lg.qa.controller;

import jakarta.validation.Valid;
import lg.qa.dto.NovaCategoriaDTO;
import lg.qa.model.Categoria;
import lg.qa.repository.CategoriaRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {
    private final CategoriaRepository categoriaRepository;

    public CategoriaController(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping
    public List<Categoria> listar() {
        return categoriaRepository.findAll();
    }

    @PostMapping
    public Categoria criar(@Valid @RequestBody NovaCategoriaDTO dto) {
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome da categoria é obrigatório");
        }
        Categoria categoria = new Categoria();
        categoria.setNome(dto.getNome());
        return categoriaRepository.save(categoria);
    }
}
