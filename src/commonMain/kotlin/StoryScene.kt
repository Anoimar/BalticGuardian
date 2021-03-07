import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.get
import com.soywiz.korge.view.ktree.readKTree
import com.soywiz.korio.file.std.resourcesVfs

class StoryScene : Scene() {
    override suspend fun Container.sceneInit() {
        val mainTree = resourcesVfs["storyTree.kTree"].readKTree(views)
        addChild(mainTree)
        (mainTree["okButton"].first).onClick {
            sceneContainer.changeTo<StartScene>()
        }
    }
}
