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

package ro.luca1152.gravitybox.utils.assets

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import ro.luca1152.gravitybox.utils.assets.loaders.MapPack
import ro.luca1152.gravitybox.utils.leaderboards.ShotsLeaderboard

object Assets {
    const val gameLeaderboardPath = "leaderboards/game-leaderboard.json"
    val gameLeaderboard = AssetDescriptor(gameLeaderboardPath, ShotsLeaderboard::class.java)
    val uiSkin = AssetDescriptor("skins/uiskin.json", Skin::class.java)
    val tileset = AssetDescriptor("graphics/tileset.atlas", TextureAtlas::class.java)
    val gameMaps = AssetDescriptor("maps/game/maps.json", MapPack::class.java)
}