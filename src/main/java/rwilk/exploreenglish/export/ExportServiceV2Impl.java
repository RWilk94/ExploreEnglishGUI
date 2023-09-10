package rwilk.exploreenglish.export;


import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import rwilk.exploreenglish.exception.RequiredObjectNotFoundException;
import rwilk.exploreenglish.export.generator.EtutorCourseSqlGenerator;
import rwilk.exploreenglish.model.entity.etutor.EtutorCourse;
import rwilk.exploreenglish.repository.etutor.EtutorCourseRepository;

@Slf4j
@Service
public class ExportServiceV2Impl implements ExportServiceV2, CommandLineRunner {

  private final EtutorCourseRepository etutorCourseRepository;
  private final EtutorCourseSqlGenerator etutorCourseSqlGenerator;

  public ExportServiceV2Impl(final EtutorCourseRepository etutorCourseRepository,
                             final EtutorCourseSqlGenerator etutorCourseSqlGenerator) {
    this.etutorCourseRepository = etutorCourseRepository;
    this.etutorCourseSqlGenerator = etutorCourseSqlGenerator;
  }

  @Override
  public void run(final String... args) throws Exception {
    export();
  }

  @Override
  public void export() {
    final EtutorCourse etutorCourse = etutorCourseRepository.findById(3L)
      .orElseThrow(() -> new RequiredObjectNotFoundException("Can't find a course"));

    etutorCourseSqlGenerator.generateSql(List.of(etutorCourse));
  }
}
