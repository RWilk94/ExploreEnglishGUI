package rwilk.exploreenglish.repository.langeek;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rwilk.exploreenglish.model.entity.langeek.LangeekWord;

import java.util.List;

@SuppressWarnings("SqlNoDataSourceInspection")
public interface LangeekWordRepository extends JpaRepository<LangeekWord, Long> {
    List<LangeekWord> findAllByHref(String href);

    @Query(value = """
            select lw.* from langeek_words lw
            join langeek_exercise_words ew on lw.id = ew.word_id
            where ew.exercise_id = :exerciseId 
                          and lw.href like :href 
                          and lw.native_translation = :nativeTranslation 
                          and lw.part_of_speech = :partOfSpeech 
                          and lw.image = :image
            """, nativeQuery = true)
    List<LangeekWord> findByExerciseIdAndHrefLike(Long exerciseId, String href, String nativeTranslation, String partOfSpeech, String image);

    @Query(value = """
            select lw.* from langeek_words lw
            join langeek_exercise_words ew on lw.id = ew.word_id
            where ew.exercise_id = :exerciseId 
                          and lw.href like :href 
                          and lw.native_translation = :nativeTranslation 
                          and lw.part_of_speech = :partOfSpeech 
                          and lw.image is null
            """, nativeQuery = true)
    List<LangeekWord> findByExerciseIdAndHrefLike(Long exerciseId, String href, String nativeTranslation, String partOfSpeech);
}
