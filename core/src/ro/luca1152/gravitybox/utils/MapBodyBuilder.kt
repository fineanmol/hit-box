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

package ro.luca1152.gravitybox.utils

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import ro.luca1152.gravitybox.PPM
import ro.luca1152.gravitybox.components.MapComponent
import ro.luca1152.gravitybox.components.physics
import ro.luca1152.gravitybox.entities.EntityFactory
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

object MapBodyBuilder {
    private val engine: PooledEngine = Injekt.get()

    fun buildPlayer(
        tiledMap: TiledMap,
        existingPlayerEntity: Entity? = null,
        world: World = Injekt.get()
    ): Entity {
        val bodyDef = BodyDef().apply {
            type = BodyDef.BodyType.DynamicBody
        }
        val fixtureDef = FixtureDef().apply {
            shape = MapBodyBuilder.getRectangle(tiledMap.layers.get("Player").objects[0] as RectangleMapObject)
            density = 1.15f
            friction = 2f
            filter.categoryBits = EntityCategory.PLAYER.bits
            filter.maskBits = EntityCategory.OBSTACLE.bits
        }
        val body = world.createBody(bodyDef).apply {
            if (existingPlayerEntity != null) {
                world.destroyBody(existingPlayerEntity.physics.body)
                existingPlayerEntity.physics.body = this
                userData = existingPlayerEntity
            } else
                userData = EntityFactory.createPlayer(this)

            createFixture(fixtureDef)
        }
        fixtureDef.shape.dispose()
        return body.userData as Entity
    }

    fun buildFinish(
        tiledMap: TiledMap,
        existingFinishEntity: Entity? = null,
        world: World = Injekt.get()
    ): Entity {
        val bodyDef = BodyDef().apply {
            type = BodyDef.BodyType.DynamicBody
        }
        val fixtureDef = FixtureDef().apply {
            shape = MapBodyBuilder.getRectangle(tiledMap.layers.get("Finish").objects.get(0) as RectangleMapObject)
            density = 100f
            filter.categoryBits = EntityCategory.FINISH.bits
            filter.maskBits = EntityCategory.NONE.bits
        }
        val body = world.createBody(bodyDef).apply {
            if (existingFinishEntity != null) {
                world.destroyBody(existingFinishEntity.physics.body)
                existingFinishEntity.physics.body = this
                userData = existingFinishEntity
            } else
                userData = EntityFactory.createFinish(this)
            gravityScale = 0f
            createFixture(fixtureDef)
        }
        fixtureDef.shape.dispose()
        return body.userData as Entity
    }

    fun buildPlatforms(
        tiledMap: TiledMap,
        world: World = Injekt.get()
    ) {
        fun buildPlatformsOfType(platformType: String) {
            tiledMap.layers.get(platformType).objects.forEach { mapObject ->
                val bodyDef = BodyDef().apply {
                    type = BodyDef.BodyType.StaticBody
                }
                val platformShape = getRectangle(mapObject as RectangleMapObject)
                world.createBody(bodyDef).apply {
                    userData = EntityFactory.createPlatform(mapObject, platformType == "Dynamic", this)
                    createFixture(platformShape, 1f)
                }
                platformShape.dispose()
            }
        }
        buildPlatformsOfType("Static")
        buildPlatformsOfType("Dynamic")
    }

    fun buildPoints(
        map: MapComponent,
        world: World = Injekt.get()
    ) {
        map.tiledMap.layers.get("Points")?.objects?.forEach { mapObject ->
            // Increase the number points of the map
            map.totalPointsNumber++

            // Create the body
            val bodyDef = BodyDef().apply {
                type = BodyDef.BodyType.DynamicBody
            }
            val fixtureDef = FixtureDef().apply {
                shape = MapBodyBuilder.getRectangle(mapObject as RectangleMapObject)
                density = 100f
                filter.categoryBits = EntityCategory.POINT.bits
                filter.maskBits = EntityCategory.NONE.bits
            }
            world.createBody(bodyDef).apply {
                userData = EntityFactory.createPoint(this)
                gravityScale = 0f
                createFixture(fixtureDef)
            }
            fixtureDef.shape.dispose()
        }
    }

    private fun getRectangle(rectangleObject: RectangleMapObject): PolygonShape {
        val rectangle = rectangleObject.rectangle
        val size = Vector2((rectangle.x + rectangle.width * 0.5f) / PPM, (rectangle.y + rectangle.height * 0.5f) / PPM)
        return PolygonShape().apply {
            setAsBox(
                rectangle.width * 0.5f / PPM,
                rectangle.height * 0.5f / PPM,
                size,
                0.0f
            )
        }
    }
}