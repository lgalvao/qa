package lg.qa.repository;

import lg.qa.model.Pergunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public interface PerguntaRepository extends JpaRepository<Pergunta, Long> {
    
    @Query("""
        SELECT p FROM Pergunta p
        WHERE (:categoriaId IS NULL OR p.categoria.id = :categoriaId)
        AND (:nivelDificuldade IS NULL OR p.nivelDificuldade = :nivelDificuldade)
        ORDER BY RANDOM()
    """)
    List<Pergunta> buscarAleatoriasPorCategoriaENivel(
        @Param("categoriaId") Long categoriaId,
        @Param("nivelDificuldade") String nivelDificuldade
    );
    
    default List<Pergunta> buscarAleatoriasPorCategoriaENivel(Long categoriaId, String nivelDificuldade, int limite) {
        List<Pergunta> resultados = buscarAleatoriasPorCategoriaENivel(categoriaId, nivelDificuldade);
        return resultados.stream().limit(limite).collect(Collectors.toList());
    }
}
