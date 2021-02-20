import com.soywiz.klock.TimeSpan
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korma.geom.Angle

class Enemy(private val enemySpriteMap: Bitmap) {

    private lateinit var enemySprite: Sprite
    var alive = true

    fun create(): View  {
        val spriteWidth = 54
        val spriteHeight = 49
        val enemyAnimation = SpriteAnimation (
            spriteMap = enemySpriteMap,
            spriteWidth = spriteWidth,
            spriteHeight = spriteHeight,
            columns = 4
        )
        return Sprite(enemyAnimation).also {
            enemySprite = it
        }
    }

    fun swim() {
        enemySprite.playAnimationLooped(spriteDisplayTime = TimeSpan(200.0))
        enemySprite.addUpdater {
            if(alive) {
                x += 2
            } else {
                y += 2
            }
        }
    }

    fun hit() {
        if(alive) {
            alive = false
            enemySprite.stopAnimation()
            enemySprite.rotation(Angle(180.0))
        }
    }
}