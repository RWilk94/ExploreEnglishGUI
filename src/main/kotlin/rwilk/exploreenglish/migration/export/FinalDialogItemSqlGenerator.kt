package rwilk.exploreenglish.migration.export

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import rwilk.exploreenglish.export.generator.SqlGeneratorAbstract
import rwilk.exploreenglish.migration.entity.FinalDialogItem

@Component
class FinalDialogItemSqlGenerator : SqlGeneratorAbstract<FinalDialogItem>() {

    private val logger = LoggerFactory.getLogger(FinalDialogItemSqlGenerator::class.java)
    private val TAG = "DIALOG_ITEMS"

    override fun generateSql(source: List<FinalDialogItem>, directoryAlias: String) {
        logger.info(LOG_PREFIX, directoryAlias, TAG)

        val chunks = source.chunked(CHUNK_SIZE)
        val sql = StringBuilder()

        for (chunk in chunks) {
            sql.append("INSERT INTO `dialog_items` (`id`, `type`, `dialog_foreign`, `dialog_native`, `media_face_image_id`, `media_comic_image_id`, `sound_seek_second`, `media_audio_id`, `media_video_id`, `exercise_id`) VALUES ")

            for (item in chunk) {
                sql.append("\n")
                    .append("(")
                    .append(item.id ?: "NULL") // COLUMN_ID
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(item.type)) // COLUMN TYPE
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(item.dialogForeign)) // COLUMN DIALOG_FOREIGN
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(QUOTE_SIGN)
                    .append(replaceApostrophe(item.dialogNative)) // COLUMN DIALOG_NATIVE
                    .append(QUOTE_SIGN)
                    .append(PARAM_SEPARATOR)
                    .append(item.faceImage?.id ?: "NULL") // MEDIA_FACE_IMAGE_ID
                    .append(PARAM_SEPARATOR)
                    .append(item.comicImage?.id ?: "NULL") // MEDIA_COMIC_IMAGE_ID
                    .append(PARAM_SEPARATOR)
                    .append(item.soundSeekSecond ?: "NULL") // SOUND_SEEK_SECOND
                    .append(PARAM_SEPARATOR)
                    .append(item.audio?.id ?: "NULL") // MEDIA_AUDIO_ID
                    .append(PARAM_SEPARATOR)
                    .append(item.video?.id ?: "NULL") // MEDIA_VIDEO_ID
                    .append(PARAM_SEPARATOR)
                    .append(item.exercise?.id ?: "NULL") // EXERCISE_ID
                    .append(getEndLineCharacter(chunk, item))
            }
        }
        exportFile(sql, "$directoryAlias/${TAG.lowercase()}.txt", TAG)
    }
}
