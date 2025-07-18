package rwilk.exploreenglish.migration.mapper

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
    fun mapAudio(primarySound: String?, secondarySound: String?): FinalMedia? {
        val finalMedia = createAudio(
            primarySound = primarySound,
            secondarySound = secondarySound
        )

        val existingMedia = when {
            !primarySound.isNullOrBlank() -> finalMediaContentRepository.findByUrl(
                primarySound
            )?.media

            !secondarySound.isNullOrBlank() -> finalMediaContentRepository.findByUrl(
                secondarySound
            )?.media

            else -> null
        }

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
        var mediaToSave = existingMedia ?: finalMedia
        mediaToSave?.let { media ->
            media.mediaContents.forEach { mediaContent ->
                mediaContent.media = media
            }
            mediaToSave = finalMediaRepository.save(media)
        }
        return mediaToSave
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

    private fun createAudio(primarySound: String?, secondarySound: String?): FinalMedia? {
        if (primarySound.isNullOrBlank() && secondarySound.isNullOrBlank()) {
            return null
        }

        val finalMediaContentList = mutableListOf<FinalMediaContent>()

        primarySound?.let {
            finalMediaContentList.add(
                FinalMediaContent(
                    id = null,
                    url = it,
                    type = LanguageVariantEnum.BRITISH_ENGLISH.name,
                    language = LanguageEnum.ENGLISH.name
                )
            )
        }

        secondarySound?.let {
            finalMediaContentList.add(
                FinalMediaContent(
                    id = null,
                    url = it,
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
            type = MediaTypeEnum.AUDIO.name,
            mediaContents = mutableListOf(mediaContent)
        )
    }
}
