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

package ro.luca1152.gravitybox.systems.editor

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ro.luca1152.gravitybox.components.editor.UndoRedoComponent
import ro.luca1152.gravitybox.components.editor.undoRedo

class UndoRedoSystem : IteratingSystem(Family.all(UndoRedoComponent::class.java).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        handleUndos(entity)
        handleRedos(entity)
    }

    private fun handleUndos(entity: Entity) {
        for (i in 0 until entity.undoRedo.levelsToUndo) {
            val commandToUndo = entity.undoRedo.commandsToUndo.pop()
            commandToUndo.unexecute()
            entity.undoRedo.commandsToRedo.push(commandToUndo)
            entity.undoRedo.levelsToUndo--
        }
    }

    private fun handleRedos(entity: Entity) {
        for (i in 0 until entity.undoRedo.levelsToRedo) {
            val commandToRedo = entity.undoRedo.commandsToRedo.pop()
            commandToRedo.execute()
            entity.undoRedo.commandsToUndo.push(commandToRedo)
            entity.undoRedo.levelsToRedo--
        }
    }
}