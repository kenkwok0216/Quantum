// package fr.unreal852.quantum.command

// import com.mojang.brigadier.Command
// import com.mojang.brigadier.CommandDispatcher
// import com.mojang.brigadier.context.CommandContext
// import fr.unreal852.quantum.Quantum
// import fr.unreal852.quantum.command.suggestion.BlocksSuggestionProvider
// import fr.unreal852.quantum.state.QuantumStorage
// import net.minecraft.command.argument.IdentifierArgumentType
// import net.minecraft.server.command.CommandManager
// import net.minecraft.server.command.ServerCommandSource
// import net.minecraft.text.Text

// class DeletePortalCommand : Command<ServerCommandSource> {
//     override fun run(context: CommandContext<ServerCommandSource>): Int {

//         if (context.source == null)
//             return 0

//         try {

//             val portalBlockId = IdentifierArgumentType.getIdentifier(context, PORTAL_BLOCK_ARG)
//             val quantumStorage = QuantumStorage.getQuantumState(context.source.server)
//             val portal = quantumStorage.getPortal { it.portalBlockId == portalBlockId }

//             if (portal == null) {
//                 context.source.sendError(Text.translatable("quantum.text.cmd.portal.notexists", portalBlockId.toString()))
//                 return 0
//             }

//             if (quantumStorage.removePortal(portal))
//                 context.source.sendMessage(Text.translatable("quantum.text.cmd.world.deleted"))

//         } catch (e: Exception) {
//             Quantum.LOGGER.error("An error occurred while deleting the portal.", e)
//         }

//         return 1
//     }

//     companion object {

//         private const val PORTAL_BLOCK_ARG = "portalFrameBlock"

//         fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
//             dispatcher.register(CommandManager.literal("qt")
//                 .then(CommandManager.literal("deleteportal")
//                     .requires { commandSource: ServerCommandSource -> commandSource.hasPermissionLevel(4) }
//                     .then(
//                         CommandManager.argument(PORTAL_BLOCK_ARG, IdentifierArgumentType.identifier())
//                             .suggests(BlocksSuggestionProvider())
//                             .executes(DeletePortalCommand())
//                     )
//                 )
//             )
//         }
//     }
// }

