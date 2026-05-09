package bg.fmi.eventplatform.repository;

import bg.fmi.eventplatform.domain.PresentationMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PresentationMaterialRepository extends JpaRepository<PresentationMaterial, Long> {

    List<PresentationMaterial> findBySpeakerId(Long speakerId);

    List<PresentationMaterial> findByAgendaItemId(Long agendaItemId);
}