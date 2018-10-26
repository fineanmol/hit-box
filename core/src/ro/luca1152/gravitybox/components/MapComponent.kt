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
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.maps.tiled.TiledMap
import ktx.assets.getAsset
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class MapComponent(levelNumber: Int,
                   manager: AssetManager = Injekt.get()) : Component {
    val tiledMap: TiledMap = manager.getAsset("maps/map-$levelNumber.tmx")
    val width = tiledMap.properties.get("width") as Int
    val height = tiledMap.properties.get("height") as Int
    val hue = tiledMap.properties.get("hue") as Int

    companion object : ComponentResolver<MapComponent>(MapComponent::class.java)
}

val Entity.map: MapComponent
    get() = MapComponent[this]