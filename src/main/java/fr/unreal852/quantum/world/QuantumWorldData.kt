package fr.unreal852.quantum.world

import fr.unreal852.quantum.utils.Extensions.getIdentifier
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import net.minecraft.world.Difficulty
import net.minecraft.world.GameMode
import net.minecraft.world.GameRules
import xyz.nucleoid.fantasy.RuntimeWorldConfig
import com.mojang.brigadier.context.CommandContext

class QuantumWorldData(worldId: Identifier, dimensionId: Identifier, runtimeWorldConfig: RuntimeWorldConfig) {

    var worldId: Identifier = worldId
        private set
    var dimensionId: Identifier = dimensionId
        private set
    var runtimeWorldConfig: RuntimeWorldConfig = runtimeWorldConfig
        private set

    fun writeToNbt(nbt: NbtCompound) {
        nbt.putString(WORLD_KEY, worldId.toString())
        nbt.putString(DIM_KEY, dimensionId.toString())
        nbt.putLong(SEED_KEY, runtimeWorldConfig.seed)
        nbt.putInt(DIFFICULTY_KEY, runtimeWorldConfig.difficulty.id)
        nbt.putBoolean(SHOULD_TICK_KEY, runtimeWorldConfig.shouldTickTime())
    }

    companion object {

        private const val WORLD_KEY = "worldId"
        private const val DIM_KEY = "dimensionId"
        private const val SEED_KEY = "seed"
        private const val DIFFICULTY_KEY = "difficulty"
        private const val SHOULD_TICK_KEY = "tick"

        fun fromNbt(nbt: NbtCompound): QuantumWorldData {

            val worldId = nbt.getIdentifier(WORLD_KEY)
            val dimensionId = nbt.getIdentifier(DIM_KEY)
            val seed = nbt.getLong(SEED_KEY)
            val difficulty = Difficulty.byId(nbt.getInt(DIFFICULTY_KEY))
            val shouldTick = nbt.getBoolean(SHOULD_TICK_KEY)


            if(worldId.toString().startsWith("minecraft:")){
                return QuantumWorldData(
                    worldId,
                    dimensionId,
                    RuntimeWorldConfig()
                        .setSeed(seed)
                        .setDifficulty(difficulty)
                        .setShouldTickTime(shouldTick)
                )
            } else {
                return QuantumWorldData(
                    worldId,
                    dimensionId,
                    RuntimeWorldConfig()
                        .setSeed(seed)
                        .setDifficulty(difficulty)
                        .setShouldTickTime(shouldTick)
                        .setGameRule(GameRules.DO_FIRE_TICK, false)
                        .setGameRule(GameRules.DO_WEATHER_CYCLE, false)
                        .setGameRule(GameRules.KEEP_INVENTORY, true)
                        .setGameRule(GameRules.DO_MOB_GRIEFING, false)
                )
            }


        }
    }
}
