import com.soywiz.korge.box2d.registerBodyWithFixture
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.Bitmap
import org.jbox2d.dynamics.BodyType

class Mine(var positionX: Double, var positionY: Double, private val mineBitmap: Bitmap) {

    fun drop(mines: MutableList<Image>): Image {

        val mine = Image(mineBitmap)
            .size(48,48)
            .position(positionX,positionY)
            .registerBodyWithFixture(type = BodyType.DYNAMIC, density = 2, friction = 0.01)
        mines.add(mine)
        mine.addUpdater {
            if(this.y > seeBottomY) {
                mines.remove(this)
            }
        }
        return mine
    }

}