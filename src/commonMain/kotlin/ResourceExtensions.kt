import com.soywiz.korau.sound.Sound
import com.soywiz.korau.sound.readSound
import com.soywiz.korio.file.VfsFile

suspend fun VfsFile.readSoundIfExists(): Sound? =
    if (exists()) {
        readSound()
    } else {
        null
    }