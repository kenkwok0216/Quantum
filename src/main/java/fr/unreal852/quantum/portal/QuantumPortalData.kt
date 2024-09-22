package fr.unreal852.quantum.portal

import fr.unreal852.quantum.utils.Extensions.getIdentifier
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier


class QuantumPortalData(destId: Identifier, portalBlockId: Identifier, portalIgniteItemId: Identifier, color: Int) {

    var destinationId: Identifier = destId
        private set

    var portalBlockId: Identifier = portalBlockId
        private set

    var portalIgniteItemId: Identifier = portalIgniteItemId
        private set

    var portalColor: Int = color
        private set

    fun writeToNbt(nbt: NbtCompound) {
        nbt.putString(DESTINATION_KEY, destinationId.toString())
        nbt.putString(BLOCK_KEY, portalBlockId.toString())
        nbt.putString(IGNITE_KEY, portalIgniteItemId.toString())
        nbt.putInt(COLOR_KEY, portalColor)
    }

    companion object {

        private const val DESTINATION_KEY = "destinationId"
        private const val BLOCK_KEY = "blockId"
        private const val IGNITE_KEY = "igniteId"
        private const val COLOR_KEY = "color"

        fun fromNbt(nbt: NbtCompound): QuantumPortalData {

            val destinationId = nbt.getIdentifier(DESTINATION_KEY)
            val blockId = nbt.getIdentifier(BLOCK_KEY)
            val igniteId = nbt.getIdentifier(IGNITE_KEY)
            val color = nbt.getInt(COLOR_KEY)

            val quantumPortalData = QuantumPortalData(
                destinationId,
                blockId,
                igniteId,
                color
            )
            return quantumPortalData
        }
    }
}
