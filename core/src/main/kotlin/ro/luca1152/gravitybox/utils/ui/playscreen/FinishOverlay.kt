/*
 * This file is part of Gravity Box.
 *
 * Gravity Box is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Gravity Box is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Gravity Box.  If not, see <https://www.gnu.org/licenses/>.
 */

package ro.luca1152.gravitybox.utils.ui.playscreen

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import ktx.inject.Context
import ro.luca1152.gravitybox.GameRules
import ro.luca1152.gravitybox.components.game.level
import ro.luca1152.gravitybox.components.game.map
import ro.luca1152.gravitybox.events.EventQueue
import ro.luca1152.gravitybox.screens.PlayScreen
import ro.luca1152.gravitybox.systems.game.ShowNextLevelEvent
import ro.luca1152.gravitybox.utils.assets.Assets
import ro.luca1152.gravitybox.utils.kotlin.UIViewport
import ro.luca1152.gravitybox.utils.kotlin.injectNullable
import ro.luca1152.gravitybox.utils.leaderboards.GameShotsLeaderboard
import ro.luca1152.gravitybox.utils.ui.Colors
import ro.luca1152.gravitybox.utils.ui.label.OutlineDistanceFieldLabel
import ro.luca1152.gravitybox.utils.ui.panes.RestartLevelConfirmationPane

class FinishOverlay(private val context: Context) {
    // Injected objects
    private val skin: Skin = context.inject()
    private val manager: AssetManager = context.inject()
    private val uiViewport: UIViewport = context.inject()
    private val eventQueue: EventQueue = context.inject()
    private val playScreen: PlayScreen = context.inject()
    private val gameRules: GameRules = context.inject()

    // Buttons
    private val framedRestartButton = FramedRestartButton(context)

    // Panes
    val restartLevelConfirmationPane = RestartLevelConfirmationPane(context)

    // Labels
    private val rankLabel = object : OutlineDistanceFieldLabel(
        context,
        "rank #x",
        skin, "semi-bold", 40f, Colors.gameColor
    ) {
        private val rank1Text = "rank #1"
        private var storedRank = 0

        init {
            color.a = 0f
        }

        override fun act(delta: Float) {
            super.act(delta)
            updateLabel()
        }

        private fun updateLabel() {
            val levelEntity = playScreen.levelEntity
            val rank = levelEntity.map.rank
            if (!levelEntity.level.isLevelFinished || levelEntity.level.forceUpdateMap) return
            if (rank != -1 && !levelEntity.map.isNewRecord) {
                if (storedRank != rank) {
                    storedRank = rank
                    setText("rank #${levelEntity.map.rank}")
                }
            } else {
                setText(rank1Text)
            }
            layout()
        }
    }
    private val rankPercentageLabel = object : OutlineDistanceFieldLabel(
        context,
        "(top x.y%)",
        skin, "regular", 30f, Colors.gameColor
    ) {
        private val newRecordText = "NEW RECORD!"
        private var storedRankPercentage = -1f

        init {
            color.a = 0f
        }

        override fun act(delta: Float) {
            super.act(delta)
            updateLabel()
        }

        private fun updateLabel() {
            val levelEntity = playScreen.levelEntity
            if (!levelEntity.level.isLevelFinished || levelEntity.level.forceUpdateMap) return

            if (levelEntity.map.isNewRecord) {
                setText(newRecordText)
            } else {
                val rankPercentage = levelEntity.map.rankPercentage
                if (storedRankPercentage != rankPercentage) {
                    storedRankPercentage = rankPercentage
                    val percentageAsString = "%.1f".format(levelEntity.map.rankPercentage)
                    setText("(top ${if (percentageAsString == "0.0") "0.1" else percentageAsString}%)")
                }
            }

            layout()
        }
    }
    private val guideLabel = OutlineDistanceFieldLabel(
        context,
        "Tap anywhere to proceed",
        skin, "regular", 37f, Colors.gameColor
    ).apply {
        color.a = 0f
    }

    // UI
    private val touchableTransparentImage = Image(manager.get(Assets.tileset).findRegion("pixel")).apply {
        width = uiViewport.worldWidth
        height = uiViewport.worldHeight
        touchable = Touchable.disabled
        color = Color.CLEAR
        addListener(object : ClickListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                eventQueue.addScheduled(ShowNextLevelEvent())
                return true
            }
        })
    }

    // Tables
    val rootTable = Table().apply {
        setFillParent(true)
        addActor(touchableTransparentImage)
        add(rankLabel).top().padTop(69f).row()
        add(rankPercentageLabel).top().row()
        add(guideLabel).top().padTop(30f).expand().row()
        add(framedRestartButton).expand().bottom().row()
    }

    fun update() {
        val levelEntity = playScreen.levelEntity
        if (!levelEntity.level.isLevelFinished || levelEntity.level.isRestarting) return

        // The leaderboard was not loaded yet, so the finish UI shouldn't be shown
        if (context.injectNullable<GameShotsLeaderboard>() == null) return

        // The finish UI was already shown
        if (framedRestartButton.isTouchable) return

        // Hide menu overlay
        if (playScreen.menuOverlay.bottomGrayStrip.y == 0f) {
            playScreen.menuOverlay.hideMenuOverlay()
        }

        // Fade out things
        val fadeOutDuration = .2f
        playScreen.run {
            skipLevelButton.run {
                touchable = Touchable.disabled
                addAction(Actions.fadeOut(fadeOutDuration))
            }
            noAdsButton.run {
                touchable = Touchable.disabled
                addAction(Actions.fadeOut(fadeOutDuration))
            }
            menuButton.run {
                touchable = Touchable.disabled
                addAction(Actions.fadeOut(fadeOutDuration))
            }
            leaderboardButton.run {
                touchable = Touchable.disabled
                addAction(Actions.fadeOut(fadeOutDuration))
            }
            restartButton.run {
                touchable = Touchable.disabled
                addAction(Actions.fadeOut(fadeOutDuration))
            }
            rankLabel.run {
                addAction(Actions.fadeOut(fadeOutDuration))
            }
        }

        // Fade in things
        val fadeInDuration = .2f
        framedRestartButton.run {
            touchable = Touchable.enabled
            addAction(
                Actions.sequence(
                    Actions.delay(fadeOutDuration),
                    Actions.fadeIn(fadeInDuration)
                )
            )
        }
        rankLabel.run {
            addAction(
                Actions.sequence(
                    Actions.delay(fadeOutDuration),
                    Actions.fadeIn(fadeInDuration)
                )
            )
        }
        rankPercentageLabel.run {
            addAction(
                Actions.sequence(
                    Actions.delay(fadeOutDuration),
                    Actions.fadeIn(fadeInDuration)
                )
            )
        }
        guideLabel.run {
            if (!gameRules.DID_SHOW_GUIDE_BETWEEN_LEVELS) {
                addAction(
                    Actions.sequence(
                        Actions.delay(fadeOutDuration),
                        Actions.fadeIn(fadeInDuration)
                    )
                )
            }
        }
        touchableTransparentImage.run {
            addAction(
                Actions.sequence(
                    Actions.delay(fadeOutDuration),
                    Actions.run {
                        touchableTransparentImage.touchable = Touchable.enabled
                    }
                )
            )
        }
    }

    fun hide() {
        // The finish UI was already hidden
        if (!framedRestartButton.isTouchable) return

        // Fade out things
        val fadeOutDuration = .2f
        framedRestartButton.run {
            touchable = Touchable.disabled
            addAction(Actions.fadeOut(fadeOutDuration))
        }
        rankLabel.run {
            addAction(Actions.fadeOut(fadeOutDuration))
        }
        rankPercentageLabel.run {
            addAction(Actions.fadeOut(fadeOutDuration))
        }
        guideLabel.run {
            addAction(Actions.fadeOut(fadeOutDuration))
        }
        touchableTransparentImage.touchable = Touchable.disabled

        // Fade in things
        val fadeInDuration = .2f
        playScreen.run {
            skipLevelButton.run {
                if (!((levelEntity.level.levelId != gameRules.HIGHEST_FINISHED_LEVEL + 1 && !levelEntity.level.isSkippingLevel &&
                            // On Android it takes a bit to update Preferences, thus the skip level button flashed a bit without this condition
                            levelEntity.level.levelId != gameRules.HIGHEST_FINISHED_LEVEL + 2))
                ) {
                    touchable = Touchable.enabled
                    clearActions()
                    addAction(
                        Actions.sequence(
                            Actions.delay(fadeOutDuration),
                            Actions.fadeIn(fadeInDuration)
                        )
                    )
                }
            }
            noAdsButton.run {
                touchable = Touchable.enabled
                addAction(Actions.sequence(Actions.delay(fadeOutDuration), Actions.fadeIn(fadeInDuration)))
            }
            menuButton.run {
                touchable = Touchable.enabled
                addAction(Actions.sequence(Actions.delay(fadeOutDuration), Actions.fadeIn(fadeInDuration)))
            }
            leaderboardButton.run {
                touchable = Touchable.enabled
                addAction(Actions.sequence(Actions.delay(fadeOutDuration), Actions.fadeIn(fadeInDuration)))
            }
            restartButton.run {
                touchable = Touchable.enabled
                addAction(Actions.sequence(Actions.delay(fadeOutDuration), Actions.fadeIn(fadeInDuration)))
            }
            rankLabel.run {
                color.a = 1f
                isVisible = false
            }
        }
    }
}