import com.soywiz.korge.*
import com.soywiz.korge.scene.Module
import com.soywiz.korge.scene.Scene
import com.soywiz.korim.color.Colors
import com.soywiz.korinject.AsyncInjector
import com.soywiz.korma.geom.SizeInt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.reflect.KClass


@ExperimentalCoroutinesApi
suspend fun main() = Korge(Korge.Config(module = ConfigModule))

object ConfigModule: Module() {
    override val bgcolor = Colors["#2b2b2b"]
    override val size = SizeInt(1024, 768)
    override val mainScene : KClass<out Scene> = StartScene::class

    override suspend fun AsyncInjector.configure(){
        mapPrototype {
            StartScene()
        }
        mapPrototype {
            GameScene()
        }
        mapPrototype {
            CreditsScene()
        }
        mapPrototype {
            StoryScene()
        }
        mapPrototype {
            EndScene(get())
        }
    }
}


