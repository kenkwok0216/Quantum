package fr.unreal852.quantum.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import fr.unreal852.quantum.Quantum
import fr.unreal852.quantum.world.QuantumWorldData
import fr.unreal852.quantum.state.QuantumStorage
import fr.unreal852.quantum.state.QuantumWorldStorage
import fr.unreal852.quantum.command.suggestion.WorldsDimensionSuggestionProvider
import fr.unreal852.quantum.utils.Extensions.teleportToWorld
import net.minecraft.command.argument.DimensionArgumentType
import net.minecraft.command.argument.IdentifierArgumentType
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.world.GameMode
import net.minecraft.nbt.NbtCompound

class TeleportToWorldCommand : Command<ServerCommandSource> {

    override fun run(context: CommandContext<ServerCommandSource>): Int {

        if (context.source == null) {
            return 0
        }

        try {
            val player = context.source.player ?: return 0
            val worldName = IdentifierArgumentType.getIdentifier(context, WORLD_IDENTIFIER_ARG)
            val world = context.source.server.getWorld(RegistryKey.of(RegistryKeys.WORLD, worldName))

            if (world == null) {
                context.source.sendError(Text.translatable("quantum.text.cmd.world.notexists.unspecified"))
                return 0
            }

            player.teleportToWorld(world) 


            //This can be remove and here for own use only
            // Determine the game mode based on the world name prefix
            val targetGameMode = if (worldName.toString().startsWith("minecraft:")) {
                GameMode.SURVIVAL // Default to Survival mode for Minecraft worlds
            } else {
                GameMode.ADVENTURE // Default to Adventure mode for non-Minecraft worlds
            } 

            player.changeGameMode(targetGameMode)

        } catch (e: Exception) {
            Quantum.LOGGER.error("An error occurred while teleporting the player.", e)
        }

        return 1
    }

    companion object {

        private const val WORLD_IDENTIFIER_ARG = "worldIdentifier"

        fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
            dispatcher.register(CommandManager.literal("qt")
                .then(CommandManager.literal("tp")
                    .requires { commandSource: ServerCommandSource -> commandSource.hasPermissionLevel(4) }
                    .then(
                        CommandManager.argument(WORLD_IDENTIFIER_ARG, DimensionArgumentType.dimension())
                            .suggests(WorldsDimensionSuggestionProvider())
                            .executes(TeleportToWorldCommand())
                    )))
        }
    }
}