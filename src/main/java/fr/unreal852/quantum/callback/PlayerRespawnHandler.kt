package fr.unreal852.quantum.callback

import fr.unreal852.quantum.state.QuantumWorldStorage
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents.AfterRespawn
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
import net.minecraft.network.packet.s2c.play.PositionFlag
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.Vec3d
import net.minecraft.entity.player.PlayerPosition

class PlayerRespawnHandler : AfterRespawn {

    // A Minecraft bug ignore the world spawn angle, this fix it for now
    // https://bugs.mojang.com/browse/MC-200092

    override fun afterRespawn(oldPlayer: ServerPlayerEntity, newPlayer: ServerPlayerEntity, alive: Boolean) {

        if (oldPlayer.spawnPointPosition != null) { // Ignore respawn if the player has a bed
            return
        }        

        val worldState = QuantumWorldStorage.getWorldState(newPlayer.serverWorld)

        val currentPosition = Vec3d(worldState.worldSpawnPos.x, worldState.worldSpawnPos.y, worldState.worldSpawnPos.z)

        val playerPosition = PlayerPosition(currentPosition, Vec3d.ZERO, worldState.worldSpawnAngle.x, worldState.worldSpawnAngle.y)
        // id #1
        val playerPositionPacket = PlayerPositionLookS2CPacket(
            0, playerPosition, PositionFlag.ROT)

        newPlayer.networkHandler.sendPacket(playerPositionPacket)
    }
}