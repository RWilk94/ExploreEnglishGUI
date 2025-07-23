package rwilk.exploreenglish.repository.ewa;

import org.springframework.data.jpa.repository.JpaRepository;
import rwilk.exploreenglish.model.entity.ewa.EwaExerciseItem;

import java.util.List;

public interface EwaExerciseItemRepository extends JpaRepository<EwaExerciseItem, Long> {
    List<EwaExerciseItem> findAllByIsVideoDownloadedOrIsVoiceDownloaded(boolean isVideoDownloaded, boolean isVoiceDownloaded);
    List<EwaExerciseItem> findAllByEwaExercise_Id(Long exerciseId);
}
