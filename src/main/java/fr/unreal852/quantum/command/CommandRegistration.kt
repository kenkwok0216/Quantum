package fr.unreal852.quantum.command

import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.server.command.CommandManager.RegistrationEnvironment
import net.minecraft.server.command.ServerCommandSource

object CommandRegistration {
    fun registerCommands() {
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource>,
                                                                                 registryAccess: CommandRegistryAccess,
                                                                                 environment: RegistrationEnvironment ->
            if (!environment.integrated && !environment.dedicated) return@CommandRegistrationCallback

            CreateWorldCommand.register(dispatcher)
            DeleteWorldCommand.register(dispatcher)
            TeleportToWorldCommand.register(dispatcher)
            SetWorldSpawnCommand.register(dispatcher)
            SetTeleportSignCommand.register(dispatcher)
            //CreatePortalCommand.register(dispatcher)
            //DeletePortalCommand.register(dispatcher)
        })
    }
}
