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
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.MathUtils
import ro.luca1152.gravitybox.utils.kotlin.GameCamera
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class ZoomingSystem(private val gameCamera: GameCamera = Injekt.get(),
                    private val inputMultiplexer: InputMultiplexer = Injekt.get()) : EntitySystem() {
    companion object {
        private const val DEFAULT_ZOOM = .75f
        private const val MIN_ZOOM = .3f // The maximum you can zoom in
        private const val MAX_ZOOM = 1.5f // The maximum you can zoom out
    }

    private lateinit var gestureDetector: GestureDetector
    private var currentZoom = DEFAULT_ZOOM

    override fun addedToEngine(engine: Engine?) {
        gameCamera.zoom = DEFAULT_ZOOM

        // Add gesture listener for zooming
        gestureDetector = GestureDetector(object : GestureDetector.GestureAdapter() {
            override fun zoom(initialDistance: Float, distance: Float): Boolean {
                // If a finger was lifted then zooming should stop and the currentZoom should be updated, since it is
                // updated only when zooming stops, and you can't zoom with only one finger.
                if (!Gdx.input.isTouched(1) || (!Gdx.input.isTouched(0) && Gdx.input.isTouched(1))) {
                    currentZoom = gameCamera.zoom
                    return true
                }

                // Apply the actual zoom
                gameCamera.zoom = currentZoom * (initialDistance / distance)

                // Keep the zoom within bounds
                gameCamera.zoom = MathUtils.clamp(gameCamera.zoom, MIN_ZOOM, MAX_ZOOM)

                // If true, the zooming gesture gets worse, meaning that there would occasionally be
                // a sudden pan after you stop zooming, so I just return false.
                return false
            }

            override fun panStop(x: Float, y: Float, pointer: Int, button: Int): Boolean {
                // Update the currentZoom here because if it was updated in zoom() you would zoom exponentially.
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