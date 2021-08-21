import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.UIButton
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.Text
import com.soywiz.korge.view.get
import com.soywiz.korge.view.ktree.readKTree
import com.soywiz.korio.file.std.resourcesVfs

class EndScene(private val result: Int) : Scene() {
    override suspend fun Container.sceneInit() {
        val mainTree = resourcesVfs["endTree.kTree"].readKTree(views)
        addChild(mainTree)
        (mainTree["resultText"].first as Text).text = "Enemies killed: $result"
        (mainTree["restartButton"].first as UIButton).onClick {
            sceneContainer.changeTo<StartScene>()
        }

    }
}
