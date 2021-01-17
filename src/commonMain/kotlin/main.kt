import com.soywiz.klock.TimeSpan
import com.soywiz.korge.*
import com.soywiz.korge.input.mouse
import com.soywiz.korge.view.*
import com.soywiz.korge.view.ktree.readKTree
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.*
import com.soywiz.korio.async.launchImmediately
import com.soywiz.korio.file.std.*

var seeTopY = 0.0
const val seeBottomY = 768
val cellSize = 32


suspend fun main() = Korge(width = 1024, height = 768, bgcolor = Colors["#2b2b2b"]) {

    val mainTree = resourcesVfs["seeTree.kTree"].readKTree(views)
    addChild(mainTree)
    with(mainTree["seeLevel"]) {
        seeTopY = first.y
    }

    val base = mainTree["base"].first
    base.mouse {
        click{

        }
    }
    addFishEnemy(this)

}

suspend fun addFishEnemy(stage: Stage) {
    val enemySpriteMap = resourcesVfs["fish_big.png"].readBitmap()
    val fishMargin = 12.5
    with(Enemy(enemySpriteMap)) {
        create().apply {
            y = seeTopY + fishMargin + 4 * 64
            stage.addChild(this)
        }
        swim()
    }
}


