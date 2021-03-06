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

package ro.luca1152.gravitybox.utils.ui.panes

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import ktx.inject.Context
import ro.luca1152.gravitybox.GameRules
import ro.luca1152.gravitybox.components.game.level
import ro.luca1152.gravitybox.events.EventQueue
import ro.luca1152.gravitybox.screens.PlayScreen
import ro.luca1152.gravitybox.systems.game.ShowNextLevelEvent
import ro.luca1152.gravitybox.utils.ui.label.DistanceFieldLabel
import ro.luca1152.gravitybox.utils.ui.popup.Pane

class RestartLevelConfirmationPane(context: Context) : Pane(context, 600f, 370f, context.inject()) {
    // Injected objects
    private val skin: Skin = context.inject()
    private val playScreen: PlayScreen = context.inject()
    private val gameRules: GameRules = context.inject()
    private val eventQueue: EventQueue = context.inject()

    private val text = DistanceFieldLabel(
        context,
        """
            Are you sure you want to restart
            the level?
            """.trimIndent(), skin, "regular", 36f, skin.getColor("text-gold")
    )
    private val restartButton = Button(skin, "long-button").apply {
        val buttonText = DistanceFieldLabel(
            context,
            "Restart",
            skin, "regular", 36f, Color.WHITE
        )
        add(buttonText)
        color.set(0 / 255f, 129 / 255f, 213 / 255f, 1f)
        addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                super.clicked(event, x, y)
                val levelEntity = playScreen.levelEntity
                if (!levelEntity.level.isRestarting) {
                    levelEntity.level.restartLevel = true
                    gameRules.RESTART_COUNT++
                }
                this@RestartLevelConfirmationPane.hide()
            }
        })
    }
    private val nextLevelButton = Button(skin, "long-button").apply {
        val buttonText = DistanceFieldLabel(
            context,
            "Next level",
            skin, "regular", 36f, Color.WHITE
        )
        add(buttonText)
        color.set(99 / 255f, 116 / 255f, 132 / 255f, 1f)
        addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                super.clicked(event, x, y)
                eventQueue.addScheduled(ShowNextLevelEvent())
                this@RestartLevelConfirmationPane.hide()
            }
        })
    }

    init {
        widget.run {
            add(text).padBottom(33f).expand().top().row()
            add(restartButton).width(492f).padBottom(32f).row()
            add(nextLevelButton).width(492f).row()
        }
    }
}