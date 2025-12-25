package fr.unreal852.quantum.state

import fr.unreal852.quantum.Quantum
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryWrapper.WrapperLookup
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import net.minecraft.world.PersistentState
import net.minecraft.world.PersistentStateType
import net.minecraft.datafixer.DataFixTypes
import com.mojang.serialization.Codec

class QuantumWorldStorage : PersistentState() {

    lateinit var worldSpawnPos: Vec3d
        private set
    var worldSpawnAngle = Vec2f(0.0f, 0.0f) // X = yaw, Y = pitch
        private set

    fun setWorldSpawn(worldSpawn: Vec3d, worldSpawnYaw: Float, worldSpawnPitch: Float) {
        this.worldSpawnPos = worldSpawn
        this.worldSpawnAngle = Vec2f(worldSpawnYaw, worldSpawnPitch)
        markDirty()
    }

    fun writeNbt(nbt: NbtCompound, registryLookup: WrapperLookup): NbtCompound {
        // write spawn pos
        nbt.putDouble(SPAWN_POS_X_KEY, worldSpawnPos.x)
        nbt.putDouble(SPAWN_POS_Y_KEY, worldSpawnPos.y)
        nbt.putDouble(SPAWN_POS_Z_KEY, worldSpawnPos.z)

        // write spawn angle
        nbt.putFloat(SPAWN_POS_YAW_KEY, worldSpawnAngle.x)
        nbt.putFloat(SPAWN_POS_PITCH_KEY, worldSpawnAngle.y)
        return nbt
    }

    companion object {
        const val STORAGE_ID = "${Quantum.MOD_ID}_world"
        private const val SPAWN_POS_X_KEY = "spawnposx"
        private const val SPAWN_POS_Y_KEY = "spawnposy"
        private const val SPAWN_POS_Z_KEY = "spawnposz"
        private const val SPAWN_POS_YAW_KEY = "spawnposyaw"
        private const val SPAWN_POS_PITCH_KEY = "spawnpospitch"

        // Use PersistentStateType instead of Type
        val TYPE: PersistentStateType<QuantumWorldStorage> =
            PersistentStateType(
                STORAGE_ID,
                { QuantumWorldStorage() },
                { Codec.unit(QuantumWorldStorage()) },
                DataFixTypes.LEVEL
            )

    fun getWorldState(world: ServerWorld): QuantumWorldStorage {
        val worldState = world.persistentStateManager.getOrCreate(TYPE)
        if (!worldState::worldSpawnPos.isInitialized) {
            // Use the static END_SPAWN_POS constant
            val spawnBlockPos: BlockPos = ServerWorld.END_SPAWN_POS
            worldState.worldSpawnPos = Vec3d(
                spawnBlockPos.x.toDouble(),
                spawnBlockPos.y.toDouble(),
                spawnBlockPos.z.toDouble()
            )
        }
        worldState.markDirty()
        return worldState
    }


        fun fromNbt(nbt: NbtCompound, registryLookup: WrapperLookup): QuantumWorldStorage {
            val worldState = QuantumWorldStorage()

            // unwrap Optionals
            val spawnPosX = nbt.getDouble(SPAWN_POS_X_KEY).orElse(0.0)
            val spawnPosY = nbt.getDouble(SPAWN_POS_Y_KEY).orElse(64.0)
            val spawnPosZ = nbt.getDouble(SPAWN_POS_Z_KEY).orElse(0.0)

            val spawnPosYaw = nbt.getFloat(SPAWN_POS_YAW_KEY).orElse(0f)
            val spawnPosPitch = nbt.getFloat(SPAWN_POS_PITCH_KEY).orElse(0f)

            worldState.worldSpawnPos = Vec3d(spawnPosX, spawnPosY, spawnPosZ)
            worldState.worldSpawnAngle = Vec2f(spawnPosYaw, spawnPosPitch)
            return worldState
        }
    }
}