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

package ro.luca1152.gravitybox.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Pool.Poolable
import ktx.actors.minus
import ktx.actors.plus
import ro.luca1152.gravitybox.components.utils.ComponentResolver
import ro.luca1152.gravitybox.pixelsToMeters
import ro.luca1152.gravitybox.utils.kotlin.GameStage
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

/**
 * Contains the rendering data for an entity, such as the position or texture.
 * Is an Image (from Scene2D) in order to be able to use Actions.
 */
class ImageComponent(private val stage: GameStage = Injekt.get()) : Component, Poolable {
    var img: Image = Image()

    var width: Float
        get() = img.width
        set(value) {
            img.width = value
        }
    var height: Float
        get() = img.height
        set(value) {
            img.height = value
        }

    /** The X position of the Image's center. */
    var x: Float
        get() {
            if (width == 0f)
                error { "The width can't be 0." }
            return img.x + width / 2f
        }
        set(value) {
            if (width == 0f)
                error { "The width can't be 0." }
            img.x = value - width / 2f
        }

    /** The Y position of the Image's center. */
    var y: Float
        get() {
            if (height == 0f)
                error { "The height can't be 0." }
            return img.y + height / 2f
        }
        set(value) {
            if (height == 0f)
                error { "The height can't be 0." }
            img.y = value - height / 2f
        }

    var color: Color
        get() = img.color
        set(value) {
            img.color = value
        }

    fun set(texture: Texture, x: Float, y: Float, width: Float = 0f, height: Float = 0f) {
        img.run {
            drawable = TextureRegionDrawable(TextureRegion(texture))
            when (width == 0f && height == 0f) {
                true -> setSize(texture.width.pixelsToMeters, texture.height.pixelsToMeters)
                false -> setSize(width, height)
            }
            setOrigin(this.width / 2f, this.height / 2f)
        }

        // ImageComponent.setX() should be used, and not img.setPosition()
        setPosition(x, y)

        stage + img
    }

    fun set(texture: Texture, position: Vector2) = set(texture, position.x, position.y)

    fun setPosition(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    override fun reset() {
        stage - img
        img.run {
            color = Color.WHITE
            rotation = 0f
            scaleX = 1f; img.scaleY = 1f
            actions.forEach {
                removeAction(it)
            }
        }
    }

    companion object : ComponentResolver<ImageComponent>(ImageComponent::class.java)
}

val Entity.image: ImageComponent
    get() = ImageComponent[this]