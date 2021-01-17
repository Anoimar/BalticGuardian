import com.soywiz.klock.TimeSpan
import com.soywiz.korge.view.Sprite
import com.soywiz.korge.view.SpriteAnimation
import com.soywiz.korge.view.addUpdater
import com.soywiz.korim.bitmap.Bitmap

class Enemy(private val enemySpriteMap: Bitmap) {

    private lateinit var enemySprite: Sprite

    fun create(): Sprite  {
        val enemyAnimation = SpriteAnimation (
            spriteMap = enemySpriteMap,
            spriteWidth = 54,
            spriteHeight = 49,
            columns = 4
        )
        return Sprite(enemyAnimation).also {
            enemySprite = it
        }
    }

    fun swim() {
        enemySprite.playAnimationLooped(spriteDisplayTime = TimeSpan(200.0))
        enemySprite.addUpdater {
            x++
        }
    }
}