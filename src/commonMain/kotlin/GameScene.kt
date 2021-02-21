import com.soywiz.klock.TimeSpan
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.*
import kotlin.random.Random

class GameScene : Scene() {

    val mines: MutableList<Image> = mutableListOf()
    var score = 0
    val busyRows = mutableListOf<Int>()

    @ExperimentalCoroutinesApi
    private val hitChannel =  Channel<Int>()
    private val escapedChannel =  Channel<Int>()

    override suspend fun Container.sceneInit() {
            var loadingMine = false
            var codeInBaltic = 10
            val mainTree = resourcesVfs["seeTree.kTree"].readKTree(views)
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
                    if(codeInBaltic < 1) {

                    } else if (codeInBaltic == 1) {
                        codeLeftText.color = Colors.RED
                    }
                    if(codeInBaltic >= 0) {
                        codeLeftText.text = "Code left: $codeInBaltic"
                    }
                }
            }
            //start game
            var job  = launch {
                while (busyRows.size < 4) {
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
                val freePos = IntRange(1,9).toMutableList().minus(busyRows)
                create().apply {
                    val enemyPos = freePos[Random.nextInt(1, 9 - busyRows.size)]
                    stage.addEnemy(this, enemyPos, fishMargin)
                    onCollision({ mines.contains(it) }) {
                        if (alive) {
                            score++
                            GlobalScope.launch {
                                hitChannel.send(1)
                            }
                            fish.hit()
                            addUpdater {
                                if (y > stage.getEndY() + this.height) {
                                    stage.removeEnemy(this, enemyPos)
                                    GlobalScope.launch {
                                        addFishEnemy(stage)
                                    }
                                }
                            }
                        }
                    }
                    addUpdater {
                        if (x > stage.getEndX() + this.width) {
                            stage.removeEnemy(this, enemyPos)
                            GlobalScope.launch {
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
