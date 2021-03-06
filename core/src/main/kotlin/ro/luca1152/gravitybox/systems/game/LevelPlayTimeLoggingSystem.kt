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

package ro.luca1152.gravitybox.systems.game

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalSystem
import ktx.inject.Context
import ro.luca1152.gravitybox.GameRules
import ro.luca1152.gravitybox.components.game.LevelComponent
import ro.luca1152.gravitybox.components.game.level
import ro.luca1152.gravitybox.components.game.map
import ro.luca1152.gravitybox.utils.kotlin.getSingleton

class LevelPlayTimeLoggingSystem(context: Context) : IntervalSystem(1f) {
    // Injected objects
    private val gameRules: GameRules = context.inject()

    // Entities
    private lateinit var levelEntity: Entity

    override fun addedToEngine(engine: Engine) {
        levelEntity = engine.getSingleton<LevelComponent>()
    }

    override fun updateInterval() {
        if (!levelEntity.map.shouldBeLoggingLevelPlayTime) {
            return
        }
        gameRules.setGameLevelPlayTime(levelEntity.level.levelId, gameRules.getGameLevelPlayTime(levelEntity.level.levelId) + interval)
    }
}