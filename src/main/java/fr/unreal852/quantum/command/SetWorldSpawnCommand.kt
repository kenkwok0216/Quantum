package fr.unreal852.quantum.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import fr.unreal852.quantum.Quantum
import fr.unreal852.quantum.utils.CommandArgumentsUtils
import fr.unreal852.quantum.utils.Extensions.setCustomSpawnPos
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.world.GameRules
import net.minecraft.util.math.Vec3d

class SetWorldSpawnCommand : Command<ServerCommandSource> {
    override fun run(context: CommandContext<ServerCommandSource>): Int {
        val player = context.source.player ?: return 0  // Ensure the player is valid
        val world = player.getEntityWorld() as? ServerWorld ?: return 0  // Ensure the world is ServerWorld

        try {
            val radius = CommandArgumentsUtils.getIntArgument(context, SPAWN_RADIUS_ARG, -1)

            // Set spawn radius if it is valid
            if (radius >= 0) {
                world.gameRules.get(GameRules.SPAWN_RADIUS).set(radius, context.source.server)
            }

            // Set the custom spawn position using player's position
            val playerPosition: Vec3d = player.getSyncedPos()

            world.setCustomSpawnPos(playerPosition, player.yaw, player.pitch)

            context.source.sendMessage(Text.translatable("quantum.text.cmd.world.spawnset", world.registryKey.value.toString()))
            context.source.sendMessage(Text.translatable("quantum.text.cmd.world.spawnset.position",
                String.format("%.3f", playerPosition.x), String.format("%.3f", playerPosition.y), String.format("%.3f", playerPosition.z),
                String.format("%.3f", player.yaw), String.format("%.3f", player.pitch)))

        } catch (e: Exception) {
            Quantum.LOGGER.error("An error occurred while setting the world spawn.", e)
        }

        return 1
    }

    companion object {
        private const val SPAWN_RADIUS_ARG = "spawnRadius"

        fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
            dispatcher.register(CommandManager.literal("qt")
                .then(CommandManager.literal("setSpawn")
                    .requires { commandSource: ServerCommandSource -> commandSource.hasPermissionLevel(4) }
                    .then(
                        CommandManager.argument(SPAWN_RADIUS_ARG, IntegerArgumentType.integer(0))
                            .executes(SetWorldSpawnCommand())
                    )
                )
            )
        }
    }
}