import com.soywiz.klock.TimeSpan
import com.soywiz.korau.sound.Sound
import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.time.delay
import com.soywiz.korge.view.*
import com.soywiz.korge.view.ktree.readKTree
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.*
import com.soywiz.korio.async.launch
import com.soywiz.korio.file.std.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.*
import kotlin.random.Random

class GameScene : Scene() {

    private val mines: MutableList<Image> = mutableListOf()
    var score = 0
    var codeInBaltic = 10
    private val busyRows = mutableListOf<Int>()

    @ExperimentalCoroutinesApi
    private val hitChannel = Channel<Int>()
    private val escapedChannel = Channel<Int>()
    private var hitSound: Sound? = null

    override suspend fun Container.sceneInit() {
        var loadingMine = false
        val mainTree = resourcesVfs["seeTree.kTree"].readKTree(views)
        hitSound = resourcesVfs["sounds/hit.wav"].readSoundIfExists()

        addChild(mainTree)
        with(mainTree["seeLevel"]) {
            seeTopY = first.y
        }
        val scoreText = mainTree["scoreText"].first as Text
        val codeLeftText = (mainTree["codeLeftText"].first as Text).also {
            it.text = "Code left: $codeInBaltic"
        }

        //create player base
        val mineBitmap = resourcesVfs["mine_big.png"].readBitmap()
        val base = mainTree["base"].first
        base.onClick {
            if (!loadingMine) {
                launch {
                    val mine = Mine(base.getCenterX(), base.getEndY(), mineBitmap)
                    mine.drop(mines).let {
                        stage?.addChild(
                            it
                        )
                    }
                    loadingMine = true
                    delay(TimeSpan(1000.0))
                    loadingMine = false
                }
            }
        }

        launch {
            hitChannel.consumeEach {
                scoreText.text = "Score: $score"
            }

        }
        //track number of escapees
        launch {
            escapedChannel.consumeEach {
                codeInBaltic--
                if (codeInBaltic < 1) {
                    delay(TimeSpan(1000.0))
                    codeLeftText.text = "Code left: Extinct!"
                    delay(TimeSpan(3000.0))
                    sceneContainer.changeTo<EndScene>(score)
                    cancel()
                } else if (codeInBaltic == 1) {
                    codeLeftText.color = Colors.RED
                }
                if (codeInBaltic >= 0) {
                    codeLeftText.text = "Code left: $codeInBaltic"
                }
            }
        }
        //start game
          stage?.launch {
            while (busyRows.size < 4 && codeInBaltic > 0) {
                stage?.let {
                    addFishEnemy(it)
                    delay(TimeSpan(3000.0))
                }
            }
        }
    }

    private fun Stage.addEnemy(enemy: View, enemyPos: Int, fishMargin: Double) {
        enemy.y = seeTopY + fishMargin + enemyPos * 64
        addChild(enemy)
        busyRows.add(enemyPos)
    }


    private fun Stage.removeEnemy(enemy: View, enemyPos: Int) {
        removeChild(enemy)
        busyRows.remove(enemyPos)
    }

    private suspend fun addFishEnemy(stage: Stage) {
        val enemySpriteMap = resourcesVfs["fish_big.png"].readBitmap()
        val fishMargin = 12.5
        with(Enemy(enemySpriteMap)) {
            val fish = this
            val freePos = IntRange(1, 9).toMutableList().minus(busyRows)
            create().apply {
                val enemyPos = freePos[Random.nextInt(1, 9 - busyRows.size)]
                stage.addEnemy(this, enemyPos, fishMargin)
                onCollision({ mines.contains(it) }) {
                    if (alive) {
                        score++
                        stage.launch {
                            hitSound?.play()
                            hitChannel.send(1)
                        }
                        fish.hit()
                        addUpdater {
                            if (y > stage.getEndY() + this.height) {
                                stage.removeEnemy(this, enemyPos)
                                stage.launch {
                                    addFishEnemy(stage)
                                }
                            }
                            if(codeInBaltic < 1) {
                                stage.launch {
                                    delay(TimeSpan(500.00))
                                    stage.removeChild(this)
                                }
                            }
                        }
                    }
                }
                addUpdater {
                    if (x > stage.getEndX() + this.width) {
                        stage.removeEnemy(this, enemyPos)
                        stage.launch {
                            escapedChannel.send(1)
                            addFishEnemy(stage)
                        }
                    }
                }
            }
            swim()
        }
    }
}
