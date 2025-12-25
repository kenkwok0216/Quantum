package fr.unreal852.quantum.callback

import fr.unreal852.quantum.state.QuantumWorldStorage
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents.AfterRespawn
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
import net.minecraft.network.packet.s2c.play.PositionFlag
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.Vec3d
import net.minecraft.entity.EntityPosition
import net.minecraft.server.world.ServerWorld

class PlayerRespawnHandler : AfterRespawn {

    override fun afterRespawn(oldPlayer: ServerPlayerEntity, newPlayer: ServerPlayerEntity, alive: Boolean) {
        // Check if the old player's respawn data is null
        if (oldPlayer.getRespawn() == null) {
            // Use getEntityWorld() instead to access the world
            val world = newPlayer.getEntityWorld() as? ServerWorld ?: return
            
            val worldState = QuantumWorldStorage.getWorldState(world)

            // Create the EntityPosition using spawn data
            val entityPos = EntityPosition(
                Vec3d(worldState.worldSpawnPos.x.toDouble(),
                      worldState.worldSpawnPos.y.toDouble(),
                      worldState.worldSpawnPos.z.toDouble()),
                Vec3d.ZERO,
                worldState.worldSpawnAngle.x.toFloat(), // yaw
                worldState.worldSpawnAngle.y.toFloat()  // pitch
            )

            val packet = PlayerPositionLookS2CPacket(
                newPlayer.id, // Using the new player ID
                entityPos,
                PositionFlag.ROT // Adjust based on necessary flags
            )

            // Send the packet to the new player
            newPlayer.networkHandler.sendPacket(packet)
        }
    }
}