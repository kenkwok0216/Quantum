@file:Suppress("unused")

package fr.unreal852.quantum.utils

import fr.unreal852.quantum.Quantum
import fr.unreal852.quantum.state.QuantumWorldStorage
import fr.unreal852.quantum.world.QuantumWorldData
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.server.MinecraftServer
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import net.minecraft.world.TeleportTarget
import xyz.nucleoid.fantasy.RuntimeWorldConfig
import net.minecraft.network.packet.s2c.play.PositionFlag
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.GameMode

object Extensions {

    
    fun NbtCompound.getIdentifier(key: String): Identifier {
        return Identifier.of(this.getString(key))
    }
    fun PlayerEntity.teleportToWorld(targetWorld: ServerWorld) {
        val worldState = QuantumWorldStorage.getWorldState(targetWorld)
        
        val teleportTarget = TeleportTarget(
            targetWorld, worldState.worldSpawnPos,
            Vec3d.ZERO,worldState.worldSpawnAngle.x, 
            worldState.worldSpawnAngle.y, TeleportTarget.NO_OP
            )
        
        this.teleportTo(teleportTarget)


    }

    fun PlayerEntity.teleportTo(targetWorld: ServerWorld, pos: Vec3d) {
        val teleportTarget = TeleportTarget(targetWorld, pos, Vec3d.ZERO, 0f, 0f, TeleportTarget.NO_OP)
        this.teleportTo(teleportTarget)
    }

    fun PlayerEntity.teleportTo(targetWorld: ServerWorld, pos: Vec3d, yaw: Float, pitch: Float) {
        val teleportTarget = TeleportTarget(targetWorld, pos, Vec3d.ZERO, yaw, pitch, TeleportTarget.NO_OP)
        this.teleportTo(teleportTarget)
    }

    fun ServerWorld.setCustomSpawnPos(pos: Vec3d, yaw: Float, pitch: Float) {
        val worldState = QuantumWorldStorage.getWorldState(this)
        worldState.setWorldSpawn(pos, yaw, pitch)
    }

    fun RuntimeWorldConfig.setDimensionAndGenerator(server: MinecraftServer, worldData: QuantumWorldData): RuntimeWorldConfig {
        val worldRegKey = RegistryKey.of(RegistryKeys.WORLD, worldData.dimensionId)
        var dimensionWorld = server.getWorld(worldRegKey)

        if (dimensionWorld == null) {
            Quantum.LOGGER.error("Failed to retrieve dimension ${worldData.dimensionId}. Defaulting to minecraft:overworld")
            dimensionWorld = server.overworld
        }

        worldData.runtimeWorldConfig.setDimensionType(dimensionWorld!!.dimensionEntry)
            .setGenerator(dimensionWorld.chunkManager.chunkGenerator)
        return this
    }

    fun BlockState.isIn(vararg tags: TagKey<Block>): Boolean {
        for (tag in tags) {
            if (this.isIn(tag))
                return true
        }
        return false
    }
}