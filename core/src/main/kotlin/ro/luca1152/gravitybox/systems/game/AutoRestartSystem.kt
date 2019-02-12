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

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.signals.Signal
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import ro.luca1152.gravitybox.components.PhysicsComponent
import ro.luca1152.gravitybox.components.PlayerComponent
import ro.luca1152.gravitybox.components.physics
import ro.luca1152.gravitybox.events.GameEvent
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

/** Restarts the level when the player is off-screen. */
class AutoRestartSystem(private val gameEventSignal: Signal<GameEvent> = Injekt.get()) : IteratingSystem(Family.all(PlayerComponent::class.java, PhysicsComponent::class.java).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (playerIsUnderMap(entity.physics.body.worldCenter))
            gameEventSignal.dispatch(GameEvent.LEVEL_RESTART)
    }

    private fun playerIsUnderMap(playerPosition: Vector2) = playerPosition.y < -10f
}