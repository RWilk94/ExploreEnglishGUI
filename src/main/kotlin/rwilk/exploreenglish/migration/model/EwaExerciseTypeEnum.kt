package rwilk.exploreenglish.migration.model

enum class EwaExerciseTypeEnum(val type: String) {
    EXPLAIN("explain"),
    CHOOSE_BY_VIDEO("chooseByVideo"),
    CHOOSE_BY_IMAGE("chooseByImage"),
    COMPOSE_PHRASE_BY_VIDEO("composePhraseByVideo"),
    COMPOSE_WORD("composeWord"),
    CHOOSE_ANSWER_BY_TEXT("chooseAnswerByText"),
    DIALOG("dialog"),
    SPEECH_EXERCISE("speechExercise"),
    COMPOSE_PHRASE_BY_TEXT("composePhraseByText"),
    EXPLAIN_WORD("explainWord");
}
