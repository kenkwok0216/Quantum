package fr.unreal852.quantum.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import fr.unreal852.quantum.Quantum
import fr.unreal852.quantum.command.suggestion.DifficultySuggestionProvider
import fr.unreal852.quantum.command.suggestion.WorldsDimensionSuggestionProvider
import fr.unreal852.quantum.utils.CommandArgumentsUtils.getEnumArgument
import fr.unreal852.quantum.utils.CommandArgumentsUtils.getIdentifierArgument
import fr.unreal852.quantum.utils.CommandArgumentsUtils.getSeedArgument
import fr.unreal852.quantum.world.QuantumWorldData
import net.minecraft.command.argument.DimensionArgumentType
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.world.Difficulty
import net.minecraft.world.World
import net.minecraft.world.dimension.DimensionTypes
import xyz.nucleoid.fantasy.RuntimeWorldConfig
import kotlin.random.Random
import net.minecraft.world.GameMode
import net.minecraft.world.GameRules

class CreateWorldCommand : Command<ServerCommandSource> {
    override fun run(context: CommandContext<ServerCommandSource>): Int {

        if (context.source == null)
            return 0

        try {

            val server = context.source.server
            val worldName = StringArgumentType.getString(context, WORLD_NAME_ARG).lowercase()
            val worldIdentifier = Identifier.of(Quantum.MOD_ID, worldName)

            if (Quantum.worldExists(worldIdentifier)) {
                context.source.sendError(Text.translatable("quantum.text.cmd.world.exists", worldName))
                return 0
            }

            
            val worldDifficulty = getEnumArgument(context, WORLD_DIFFICULTY_ARG, Difficulty::class.java, server.saveProperties.difficulty)
            val dimensionIdentifier = getIdentifierArgument(context, WORLD_DIMENSION_ARG, DimensionTypes.OVERWORLD_ID)
            val serverWorld = server.getWorld(RegistryKey.of(RegistryKeys.WORLD, dimensionIdentifier)) ?: server.getWorld(World.OVERWORLD)!!
            val worldSeed = getSeedArgument(context, WORLD_SEED_ARG, Random.nextLong())

            val worldConfig = RuntimeWorldConfig()
                .setDifficulty(worldDifficulty)
                .setDimensionType(serverWorld.dimensionEntry)
                .setGenerator(serverWorld.chunkManager.chunkGenerator)
                .setSeed(worldSeed)
                .setShouldTickTime(true)
                .setGameRule(GameRules.DO_FIRE_TICK, false)
                .setGameRule(GameRules.DO_WEATHER_CYCLE, false)
                .setGameRule(GameRules.DO_MOB_GRIEFING, false)

            val quantumWorldData = QuantumWorldData(worldIdentifier, dimensionIdentifier, worldConfig)

            Quantum.getOrCreateWorld(server, quantumWorldData, true)

            context.source.sendMessage(Text.translatable("quantum.text.cmd.world.created", worldName))
        } catch (e: Exception) {
            Quantum.LOGGER.error("An error occurred while creating the world.", e)
        }

        return 1
    }

    companion object {

        private const val WORLD_NAME_ARG = "worldName"
        private const val WORLD_DIFFICULTY_ARG = "worldDifficulty"
        private const val WORLD_DIMENSION_ARG = "worldDifficulty"
        private const val WORLD_SEED_ARG = "worldSeed"

        fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
            dispatcher.register(CommandManager.literal("qt")
                .then(CommandManager.literal("createworld")
                    .requires { commandSource: ServerCommandSource -> commandSource.hasPermissionLevel(4) }
                    .then(
                        CommandManager.argument(WORLD_NAME_ARG, StringArgumentType.string())
                            .executes(CreateWorldCommand())
                            .then(
                                CommandManager.argument(WORLD_DIFFICULTY_ARG, StringArgumentType.string())
                                    .suggests(DifficultySuggestionProvider())
                                    .executes(CreateWorldCommand())
                                    .then(
                                        CommandManager.argument(WORLD_DIMENSION_ARG, DimensionArgumentType.dimension())
                                            .suggests(WorldsDimensionSuggestionProvider())
                                            .executes(CreateWorldCommand())
                                            .then(
                                                CommandManager.argument(WORLD_SEED_ARG, StringArgumentType.string())
                                                    .executes(CreateWorldCommand())
                                            )
                                    )
                            )
                    )
                )
            )
        }
    }
}

