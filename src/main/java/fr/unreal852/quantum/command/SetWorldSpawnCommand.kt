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

class SetWorldSpawnCommand : Command<ServerCommandSource> {
    override fun run(context: CommandContext<ServerCommandSource>): Int {
        if (context.source == null) {
            return 0
        }

        try {
            val world = context.source.world
            val player = context.source.player

            if (player == null || world !is ServerWorld) {
                return 0
            }

            val radius = CommandArgumentsUtils.getIntArgument(context, SPAWN_RADIUS_ARG, -1)

            if (radius >= 0)
                world.gameRules.get(GameRules.SPAWN_RADIUS).set(radius, context.source.server)

            world.setCustomSpawnPos(player.pos, player.yaw, player.pitch)

            context.source.sendMessage(Text.translatable("quantum.text.cmd.world.spawnset", world.registryKey.value.toString()))
            context.source.sendMessage(Text.translatable("quantum.text.cmd.world.spawnset.position",
                String.format("%.3f", player.x), String.format("%.3f", player.y), String.format("%.3f", player.z),
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