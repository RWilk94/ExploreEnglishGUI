package rwilk.exploreenglish.migration.controller

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Controller
import rwilk.exploreenglish.export.ExportServiceV2
import rwilk.exploreenglish.migration.entity.*
import rwilk.exploreenglish.migration.export.*
import rwilk.exploreenglish.migration.repository.*

@Controller
class ExportController(
    private val finalCourseRepository: FinalCourseRepository,
    private val finalLessonRepository: FinalLessonRepository,
    private val finalExerciseRepository: FinalExerciseRepository,
    private val finalNoteRepository: FinalNoteRepository,
    private val finalNoteItemRepository: FinalNoteItemRepository,
    private val finalDialogItemRepository: FinalDialogItemRepository,
    private val finalExerciseQuestionRepository: FinalExerciseQuestionRepository,
    private val finalExerciseWordRepository: FinalExerciseWordRepository,

    private val finalCourseSqlGenerator: FinalCourseSqlGenerator,
    private val finalLessonSqlGenerator: FinalLessonSqlGenerator,
    private val finalExerciseSqlGenerator: FinalExerciseSqlGenerator,
    private val finalNoteSqlGenerator: FinalNoteSqlGenerator,
    private val finalNoteItemSqlGenerator: FinalNoteItemSqlGenerator,
    private val finalDefinitionSqlGenerator: FinalDefinitionSqlGenerator,
    private val finalDialogItemSqlGenerator: FinalDialogItemSqlGenerator,
    private val finalExerciseAnswerSqlGenerator: FinalExerciseAnswerSqlGenerator,
    private val finalExerciseQuestionSqlGenerator: FinalExerciseQuestionSqlGenerator,
    private val finalExerciseWordSqlGenerator: FinalExerciseWordSqlGenerator,
    private val finalMediaContentSqlGenerator: FinalMediaContentSqlGenerator,
    private val finalMediaSqlGenerator: FinalMediaSqlGenerator,
    private val finalWordSqlGenerator: FinalWordSqlGenerator
) : ExportServiceV2, CommandLineRunner {

    override fun run(vararg args: String?) {
        // export()
    }

    override fun export() {
        val courses = finalCourseRepository.findAll()
        finalCourseSqlGenerator.generateSql(courses, "final_courses")

        val finalMediaCourse = mutableSetOf<FinalMedia>()
        courses.map { it.image }.forEach { finalMediaCourse.add(it!!) }
        val finalMediaContentCourse = mutableSetOf<FinalMediaContent>()
        for (media in finalMediaCourse) {
            finalMediaContentCourse.addAll(media.mediaContents)
        }

        finalMediaSqlGenerator.generateSql(finalMediaCourse.toList(), "final_courses")
        finalMediaContentSqlGenerator.generateSql(finalMediaContentCourse.toList(), "final_courses")

        for (course in courses) {
            val directoryAlias = "final_courses/${course.id}"

            val lessons = finalLessonRepository.findAllByCourse_Id(course.id!!)
            finalLessonSqlGenerator.generateSql(lessons, directoryAlias)

            val finalExercises = lessons
                .map { finalExerciseRepository.findAllByLesson_Id(it.id!!) }
                .flatten()
            finalExerciseSqlGenerator.generateSql(finalExercises, directoryAlias)

            val finalNotes = mutableListOf<FinalNote>()
            val finalExerciseQuestions = mutableListOf<FinalExerciseQuestion>()
            val finalExerciseAnswers = mutableListOf<FinalExerciseAnswer>()
            val finalDialogItems = mutableListOf<FinalDialogItem>()
            val finalExerciseWords = mutableListOf<FinalExerciseWord>()

            for (exercise in finalExercises) {
                finalNotes.addAll(finalNoteRepository.findAllByExercise_Id(exercise.id!!))
                finalExerciseQuestions.addAll(finalExerciseQuestionRepository.findAllByExercise_Id(exercise.id))
                finalDialogItems.addAll(finalDialogItemRepository.findAllByExercise_Id(exercise.id))
                finalExerciseWords.addAll(finalExerciseWordRepository.findAllByExercise_Id(exercise.id))
            }
            finalExerciseQuestionSqlGenerator.generateSql(finalExerciseQuestions, directoryAlias)
            finalDialogItemSqlGenerator.generateSql(finalDialogItems, directoryAlias)
            finalExerciseWordSqlGenerator.generateSql(finalExerciseWords, directoryAlias)

            for (finalExerciseQuestion in finalExerciseQuestions) {
                finalExerciseAnswers.addAll(finalExerciseQuestion.answers)
            }
            finalExerciseAnswerSqlGenerator.generateSql(finalExerciseAnswers, directoryAlias)

            val finalNoteItems = mutableListOf<FinalNoteItem>()
            for (note in finalNotes) {
                finalNoteItems.addAll(finalNoteItemRepository.findAllByNote_Id(note.id!!))
            }
            finalNoteSqlGenerator.generateSql(finalNotes, directoryAlias)
            finalNoteItemSqlGenerator.generateSql(finalNoteItems, directoryAlias)

            val finalWords = mutableListOf<FinalWord>()
            for (exerciseWord in finalExerciseWords) {
                finalWords.add(exerciseWord.word!!)
            }
            val finalDefinitions = mutableListOf<FinalDefinition>()
            for (word in finalWords) {
                finalDefinitions.addAll(word.definitions)
            }
            finalWordSqlGenerator.generateSql(finalWords, directoryAlias)
            finalDefinitionSqlGenerator.generateSql(finalDefinitions, directoryAlias)

            val finalMedia = mutableSetOf<FinalMedia>()

            lessons.forEach { lesson ->
                lesson.image?.let { finalMedia.add(it) }
            }
            finalExercises.forEach { exercise ->
                exercise.image?.let { finalMedia.add(it) }
            }
            finalNotes.forEach { note ->
                note.audio?.let { finalMedia.add(it) }
            }
            finalExerciseQuestions.forEach { question ->
                question.audio?.let { finalMedia.add(it) }
                question.image?.let { finalMedia.add(it) }
                question.video?.let { finalMedia.add(it) }
            }
            finalExerciseAnswers.forEach { answer ->
                answer.audio?.let { finalMedia.add(it) }
                answer.image?.let { finalMedia.add(it) }
                answer.video?.let { finalMedia.add(it) }
            }
            finalDialogItems.forEach { dialogItem ->
                dialogItem.faceImage?.let { finalMedia.add(it) }
                dialogItem.comicImage?.let { finalMedia.add(it) }
                dialogItem.audio?.let { finalMedia.add(it) }
                dialogItem.video?.let { finalMedia.add(it) }
            }
            finalNoteItems.forEach { noteItem ->
                noteItem.audio?.let { finalMedia.add(it) }
                noteItem.image?.let { finalMedia.add(it) }
            }
            finalWords.forEach { word ->
                word.image?.let { finalMedia.add(it) }
            }
            finalDefinitions.forEach { definition ->
                definition.audio?.let { finalMedia.add(it) }
                definition.video?.let { finalMedia.add(it) }
            }

            val finalMediaContent = mutableSetOf<FinalMediaContent>()
            for (media in finalMedia) {
                finalMediaContent.addAll(media.mediaContents)
            }

            finalMediaSqlGenerator.generateSql(finalMedia.toList(), directoryAlias)
            finalMediaContentSqlGenerator.generateSql(finalMediaContent.toList(), directoryAlias)
        }
    }
}
