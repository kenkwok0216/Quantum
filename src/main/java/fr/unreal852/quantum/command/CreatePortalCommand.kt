// package fr.unreal852.quantum.command

// import com.mojang.brigadier.Command
// import com.mojang.brigadier.CommandDispatcher
// import com.mojang.brigadier.context.CommandContext
// import fr.unreal852.quantum.Quantum
// import fr.unreal852.quantum.command.suggestion.BlocksSuggestionProvider
// import fr.unreal852.quantum.command.suggestion.ItemsSuggestionProvider
// import fr.unreal852.quantum.command.suggestion.WorldsDimensionSuggestionProvider
// import fr.unreal852.quantum.portal.QuantumPortalData
// import fr.unreal852.quantum.state.QuantumStorage
// import net.kyrptonaught.customportalapi.api.CustomPortalBuilder
// import net.minecraft.command.argument.DimensionArgumentType
// import net.minecraft.command.argument.IdentifierArgumentType
// import net.minecraft.registry.Registries
// import net.minecraft.server.command.CommandManager
// import net.minecraft.server.command.ServerCommandSource
// import net.minecraft.text.Text

// class CreatePortalCommand : Command<ServerCommandSource> {
//     override fun run(context: CommandContext<ServerCommandSource>): Int {

//         if (context.source == null)
//             return 0

//         try {

//             val quantumStorage = QuantumStorage.getQuantumState(context.source.server)
//             val portalBlockId = IdentifierArgumentType.getIdentifier(context, PORTAL_BLOCK_ARG)

//             if (quantumStorage.getPortal { it.portalBlockId == portalBlockId } != null) {
//                 context.source.sendError(Text.translatable("quantum.text.cmd.portal.exists", portalBlockId.toString()))
//                 return 0
//             }

//             val destinationId = IdentifierArgumentType.getIdentifier(context, PORTAL_DESTINATION_ARG)
//             val portalItemId = IdentifierArgumentType.getIdentifier(context, PORTAL_ITEM_ARG)

//             val portalBlock = Registries.BLOCK.get(portalBlockId)
//             val portalItem = Registries.ITEM.get(portalItemId)

//             val portalLink = CustomPortalBuilder.beginPortal()
//                 .frameBlock(portalBlock)
//                 .lightWithItem(portalItem)
//                 .destDimID(destinationId)
//                 .tintColor(240, 142, 25)
//                 .registerPortal()

//             context.source.sendMessage(Text.translatable("quantum.text.cmd.portal.created"))

//             quantumStorage.addPortal(QuantumPortalData(portalLink.dimID, portalLink.block, portalItemId, portalLink.colorID))

//         } catch (e: Exception) {
//             Quantum.LOGGER.error("An error occurred while creating the world.", e)
//         }

//         return 1
//     }

//     companion object {

//         private const val PORTAL_BLOCK_ARG = "portalFrameBlock"
//         private const val PORTAL_DESTINATION_ARG = "destinationWorld"
//         private const val PORTAL_ITEM_ARG = "portalItem"

//         fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
//             dispatcher.register(CommandManager.literal("qt")
//                 .then(CommandManager.literal("createportal")
//                     .requires { commandSource: ServerCommandSource -> commandSource.hasPermissionLevel(4) }
//                     .then(
//                         CommandManager.argument(PORTAL_BLOCK_ARG, IdentifierArgumentType.identifier())
//                             .suggests(BlocksSuggestionProvider())
//                             .then(
//                                 CommandManager.argument(PORTAL_ITEM_ARG, IdentifierArgumentType.identifier())
//                                     .suggests(ItemsSuggestionProvider())
//                                     .then(
//                                         CommandManager.argument(PORTAL_DESTINATION_ARG, DimensionArgumentType.dimension())
//                                             .suggests(WorldsDimensionSuggestionProvider())
//                                             .executes(CreatePortalCommand())
//                                     )
//                             )

//                     )
//                 )
//             )
//         }
//     }
// }

