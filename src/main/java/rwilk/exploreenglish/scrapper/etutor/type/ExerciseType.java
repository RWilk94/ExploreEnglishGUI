package rwilk.exploreenglish.scrapper.etutor.type;

import lombok.Getter;

@Getter
public enum ExerciseType {

    TIP("tip"), // DONE
    PICTURES_WORDS_LIST("pictures_words_list"), // DONE
    SCREEN("screen"), // DONE
    PICTURES_LISTENING("pictures_listening"), // DONE
    PICTURES_CHOICE("pictures_choice"), // DONE
    EXERCISE("exercise"), // DONE
    MATCHING_PAIRS("matching_pairs"), // DONE
    DIALOGUE("dialogue"), // DONE
    COMIC_BOOK("comic_book"), // DONE
    GRAMMAR_NOTE("grammar_note"), // DONE
    READING("reading"), // DONE
    PICTURES_MASKED_WRITING("pictures_masked_writing"), // DONE
    SPEAKING("speaking"),
    GRAMMAR_LIST("grammar_list"), // DONE
    WRITING("writing"),
    VIDEO("video"),
    WORDS_LIST("words_list"), // DONE
    MATCHING_PAIRS_GRAMMAR("matching_pairs_grammar1"), // DONE
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
