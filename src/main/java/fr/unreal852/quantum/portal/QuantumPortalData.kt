package fr.unreal852.quantum.portal

import fr.unreal852.quantum.utils.Extensions.getIdentifier
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier

class QuantumPortalData(
    destId: Identifier,
    portalBlockId: Identifier,
    portalIgniteItemId: Identifier,
    color: Int
) {
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
            val destinationId = nbt.getIdentifier(DESTINATION_KEY) ?: Identifier.of("minecraft:overworld")
            val blockId = nbt.getIdentifier(BLOCK_KEY) ?: Identifier.of("minecraft:nether_portal")
            val igniteId = nbt.getIdentifier(IGNITE_KEY) ?: Identifier.of("minecraft:flint_and_steel")

            // Handle optional color value
            val color = nbt.getInt(COLOR_KEY).orElse(0) // Default to 0 if not present

            return QuantumPortalData(destinationId, blockId, igniteId, color)
        }
    }
}