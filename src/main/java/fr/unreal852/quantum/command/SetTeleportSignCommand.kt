package fr.unreal852.quantum.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import fr.unreal852.quantum.Quantum
import fr.unreal852.quantum.command.suggestion.WorldsDimensionSuggestionProvider
import net.minecraft.block.HangingSignBlock
import net.minecraft.block.SignBlock
import net.minecraft.block.WallSignBlock
import net.minecraft.block.entity.SignBlockEntity
import net.minecraft.block.entity.SignText
import net.minecraft.command.argument.DimensionArgumentType
import net.minecraft.command.argument.IdentifierArgumentType
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.world.RaycastContext

class SetTeleportSignCommand : Command<ServerCommandSource> {
    override fun run(context: CommandContext<ServerCommandSource>): Int {
        val player = context.source.player ?: return 0
        val world = player.getEntityWorld() as? ServerWorld ?: return 0 // Use getEntityWorld instead

        val rayContext = RaycastContext(
            player.eyePos,
            player.eyePos.add(player.rotationVecClient.multiply(200.0)),
            RaycastContext.ShapeType.OUTLINE,
            RaycastContext.FluidHandling.NONE,
            player
        )

        val hitResult = world.raycast(rayContext)
        val blockState = world.getBlockState(hitResult.blockPos)

        if (blockState.block !is SignBlock && blockState.block !is WallSignBlock && blockState.block !is HangingSignBlock) {
            context.source.sendError(Text.translatable("quantum.text.cmd.sign.lookat"))
            return 0
        }

        val worldIdentifier = IdentifierArgumentType.getIdentifier(context, WORLD_IDENTIFIER_ARG)
        val serverWorld = context.source.server.getWorld(RegistryKey.of(RegistryKeys.WORLD, worldIdentifier))
            ?: run {
                context.source.sendError(Text.translatable("quantum.text.cmd.world.notexists", worldIdentifier.toString()))
                return 0
            }

        val signEntity = world.getBlockEntity(hitResult.blockPos) as? SignBlockEntity ?: return 0

        // Use a lambda for changeText
        if (!signEntity.changeText({
                SignText()
                    .withMessage(0, Text.literal("Teleport"))
                    .withMessage(1, Text.literal(worldIdentifier.namespace))
                    .withMessage(2, Text.literal(worldIdentifier.path))
            }, false)) {
            context.source.sendError(Text.translatable("quantum.text.cmd.sign.failed"))
            return 0
        }

        context.source.sendMessage(Text.translatable("quantum.text.cmd.sign.success", worldIdentifier.toString()))
        return 1
    }

    companion object {
        private const val WORLD_IDENTIFIER_ARG = "worldIdentifier"

        fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
            dispatcher.register(CommandManager.literal("qt")
                .then(
                    CommandManager.literal("setdestination")
                        .requires { it.hasPermissionLevel(4) }  // Ensure only OP players can use this
                        .then(
                            CommandManager.argument(WORLD_IDENTIFIER_ARG, DimensionArgumentType.dimension())
                                .suggests(WorldsDimensionSuggestionProvider())
                                .executes { commandContext -> SetTeleportSignCommand().run(commandContext) }  // Use lambda
                        )
                )
            )
        }
    }
}