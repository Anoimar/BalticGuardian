import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import com.soywiz.korge.view.ktree.readKTree
import com.soywiz.korio.file.std.resourcesVfs

class StartScene: Scene(){

    override suspend fun Container.sceneInit() {
        val mainTree = resourcesVfs["startTree.kTree"].readKTree(views)
        addChild(mainTree)
        mainTree["startText"].first.onClick {
            sceneContainer.changeTo<GameScene>()
        }
        mainTree["creditsText"].first.onClick {
            sceneContainer.changeTo<CreditsScene>()
        }
    }
}