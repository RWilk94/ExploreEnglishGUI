package rwilk.exploreenglish.repository.langeek;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rwilk.exploreenglish.model.entity.langeek.LangeekPromoBook;

import java.util.List;

public interface LangeekPromoBookRepository extends JpaRepository<LangeekPromoBook, Long> {
    @Query(nativeQuery = true, value = """
                select 
                    ll.id, 
                    ll.name,
                    ll.price,
                    (select lc.price from langeek_courses lc where lc.id = ll.course_id) as course_price,            
                    count(le.id) as words_count, 
                    (
                            select count(le2.id) as exercises_count
                            from langeek_lessons ll2
                            join langeek_exercises le2 on le2.lesson_id = ll2.id
                            where ll2.id = ll.id
                    ) as exercises_count,
                    ll.course_id as course_id,
                    (select lc.name from langeek_courses lc where lc.id = ll.course_id) as course_name
                from langeek_lessons ll
                join langeek_exercises le on le.lesson_id = ll.id
                join langeek_exercise_words lew on lew.exercise_id = le.id
                join langeek_words lw on lw.id = lew.word_id
                group by ll.id, ll.name
            """)
    List<LangeekPromoBook> findAllPromoBooks();

}
