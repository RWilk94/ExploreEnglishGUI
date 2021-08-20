package rwilk.exploreenglish.model;

import rwilk.exploreenglish.model.entity.Lesson;

public interface LearnItem {

  Integer getPosition();

  Lesson getLesson();

  String getName();

  void setPosition(Integer position);

}
