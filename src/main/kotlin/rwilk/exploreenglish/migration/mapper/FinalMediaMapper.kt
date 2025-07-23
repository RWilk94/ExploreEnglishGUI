package rwilk.exploreenglish.migration.mapper

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import rwilk.exploreenglish.migration.entity.FinalMedia
import rwilk.exploreenglish.migration.entity.FinalMediaContent
import rwilk.exploreenglish.migration.model.LanguageVariantEnum
import rwilk.exploreenglish.migration.model.MediaTypeEnum
import rwilk.exploreenglish.migration.repository.FinalMediaContentRepository
import rwilk.exploreenglish.migration.repository.FinalMediaRepository
import rwilk.exploreenglish.model.LanguageEnum

@Component
class FinalMediaMapper(
    private val finalMediaRepository: FinalMediaRepository,
    private val finalMediaContentRepository: FinalMediaContentRepository,
) {
    private val logger = LoggerFactory.getLogger(FinalMediaMapper::class.java)

    fun mapAudio(primarySound: String?, secondarySound: String?): FinalMedia? {
        if (primarySound.isNullOrBlank() && secondarySound.isNullOrBlank()) {
            return null
        }

        val finalMedia = createAudio(
            primarySound = primarySound,
            secondarySound = secondarySound
        )

        val existingMedia = queryMediaContentByUrl(primarySound)
            ?: queryMediaContentByUrl(secondarySound)

        // add new media contents if they are not already present
        // ex some exercise items have only primary sound, some only secondary sound
        if (finalMedia != null && existingMedia != null && finalMedia.mediaContents.size != existingMedia.mediaContents.size) {
            val existingUrls = existingMedia.mediaContents.mapNotNull { it.url }
            finalMedia.mediaContents
                .filter { it.url != null && it.url !in existingUrls }
                .forEach { mediaContent ->
                    existingMedia.mediaContents.add(mediaContent)
                }
        }

        // set relationships for media contents and save the media
        val mediaToSave = existingMedia ?: finalMedia
        try {
            if (mediaToSave == null) {
                logger.warn("No media created for [$primarySound and $secondarySound]")
                return null
            } else {
                mediaToSave.mediaContents.forEach { mediaContent ->
                    mediaContent.media = mediaToSave
                }
                return finalMediaRepository.save(mediaToSave)
            }
        } catch (e: Exception) {
            logger.error("Failed to map audio for [$primarySound and $secondarySound]", e)
            return null
        }
    }

    fun mapImage(url: String?): FinalMedia? {
        val finalMedia = when (!url.isNullOrBlank()) {
            true -> finalMediaContentRepository.findByUrl(
                url
            )?.media

            else -> null
        }

        return finalMedia ?: createImage(url)?.let {
            it.mediaContents.forEach { mediaContent ->
                mediaContent.media = it
            }
            finalMediaRepository.save(it)
        }
    }

    fun mapVideo(url: String?): FinalMedia? {
        val finalMedia = when (!url.isNullOrBlank()) {
            true -> finalMediaContentRepository.findByUrl(
                url
            )?.media

            else -> null
        }

        return finalMedia ?: createVideo(url)?.let {
            it.mediaContents.forEach { mediaContent ->
                mediaContent.media = it
            }
            finalMediaRepository.save(it)
        }
    }

    private fun createAudio(primarySound: String?, secondarySound: String?): FinalMedia? {
        val finalMediaContentList = mutableListOf<FinalMediaContent>()

        if (!primarySound.isNullOrBlank()) {
            finalMediaContentList.add(
                FinalMediaContent(
                    id = null,
                    url = primarySound,
                    type = LanguageVariantEnum.BRITISH_ENGLISH.name,
                    language = LanguageEnum.ENGLISH.name
                )
            )
        }

        if (!secondarySound.isNullOrBlank()) {
            finalMediaContentList.add(
                FinalMediaContent(
                    id = null,
                    url = secondarySound,
                    type = LanguageVariantEnum.AMERICAN_ENGLISH.name,
                    language = LanguageEnum.ENGLISH.name
                )
            )
        }

        return FinalMedia(
            id = null,
            text = null,
            type = MediaTypeEnum.AUDIO.name,
            mediaContents = finalMediaContentList
        )
    }

    private fun createImage(url: String?): FinalMedia? {
        if (url.isNullOrBlank()) {
            return null
        }

        val mediaContent = FinalMediaContent(
            id = null,
            url = url,
            type = null,
            language = null
        )

        return FinalMedia(
            id = null,
            text = null,
            type = MediaTypeEnum.IMAGE.name,
            mediaContents = mutableListOf(mediaContent)
        )
    }

    private fun createVideo(url: String?): FinalMedia? {
        if (url.isNullOrBlank()) {
            return null
        }

        val mediaContent = FinalMediaContent(
            id = null,
            url = url,
            type = null,
            language = null
        )

        return FinalMedia(
            id = null,
            text = null,
            type = MediaTypeEnum.VIDEO.name,
            mediaContents = mutableListOf(mediaContent)
        )
    }

    private fun queryMediaContentByUrl(url: String?): FinalMedia? {
        return if (!url.isNullOrBlank()) {
            val media = finalMediaContentRepository.findByUrl(url)
            if (media != null) {
                return media.media
            }

            val likeMedia = finalMediaContentRepository.findByUrlLike('%' + url.substring(url.lastIndexOf('/') + 1))
            if (likeMedia.isNotEmpty()) {
                 return likeMedia.first().media
            }
            return null
        } else {
            null
        }
    }
}
