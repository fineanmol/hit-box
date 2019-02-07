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

package ro.luca1152.gravitybox.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.input.GestureDetector
import ro.luca1152.gravitybox.utils.GameCamera
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class ZoomingSystem(private val gameCamera: GameCamera = Injekt.get(),
                    private val inputMultiplexer: InputMultiplexer = Injekt.get()) : EntitySystem() {
    private lateinit var gestureDetector: GestureDetector
    private var currentZoom = 1f

    override fun addedToEngine(engine: Engine?) {
        gestureDetector = GestureDetector(object : GestureDetector.GestureAdapter() {
            override fun zoom(initialDistance: Float, distance: Float): Boolean {
                gameCamera.zoom = currentZoom * (initialDistance / distance)
                return false
            }

            override fun panStop(x: Float, y: Float, pointer: Int, button: Int): Boolean {
                currentZoom = gameCamera.zoom
                return true
            }
        })
        inputMultiplexer.addProcessor(gestureDetector)
    }

    override fun removedFromEngine(engine: Engine?) {
        inputMultiplexer.removeProcessor(gestureDetector)
    }
}