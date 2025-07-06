package lg.qa.repository;

import lg.qa.model.RespostaDada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RespostaDadaRepository extends JpaRepository<RespostaDada, Long> {
    
    @Query("SELECT rd FROM RespostaDada rd " +
           "LEFT JOIN FETCH rd.prova p " +
           "LEFT JOIN FETCH rd.pergunta pe " +
           "LEFT JOIN FETCH rd.alternativaEscolhida a " +
           "WHERE p.id = :provaId")
    List<RespostaDada> findByProvaId(@Param("provaId") Long provaId);
}
