package rwilk.exploreenglish.scrapper.etutor.type;

import lombok.Getter;

@Getter
public enum ExerciseType {

    TIP("tip"), // DONE
    PICTURES_WORDS_LIST("pictures_words_list"),
    SCREEN("screen"), // DONE
    PICTURES_LISTENING("pictures_listening"),
    PICTURES_CHOICE("pictures_choice"),
    EXERCISE("exercise"),
    MATCHING_PAIRS("matching_pairs"),
    DIALOGUE("dialogue"),
    COMIC_BOOK("comic_book"), // DONE
    GRAMMAR_NOTE("grammar_note"), // DONE
    READING("reading"),
    PICTURES_MASKED_WRITING("pictures_masked_writing"),
    SPEAKING("speaking"),
    GRAMMAR_LIST("grammar_list"),
    WRITING("writing"),
    VIDEO("video"),
    WORDS_LIST("words_list"), // can be scrapped by WordScrapper
    MATCHING_PAIRS_GRAMMAR("matching_pairs_grammar1"),
    SCREEN_CULTURAL("screen-cultural"), // DONE
    SCREEN_CULINARY("screen-culinary"), // DONE
    SCREEN_MUSIC("screen-music"); // DONE

    private final String value;

    ExerciseType(final String value) {
        this.value = value;
    }

    public static ExerciseType fromString(final String text) {
        for (final ExerciseType type : ExerciseType.values()) {
            if (type.getValue().equalsIgnoreCase(text)) {
                return type;
            }
        }
        for (final ExerciseType type : ExerciseType.values()) {
            if (type.toString().equalsIgnoreCase(text)) {
                return type;
            }
        }
        return null;
    }

}
