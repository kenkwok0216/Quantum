package fr.unreal852.quantum

import fr.unreal852.quantum.callback.PlayerRespawnHandler
import fr.unreal852.quantum.callback.PlayerUseSignHandler
import fr.unreal852.quantum.command.CommandRegistration
import fr.unreal852.quantum.state.QuantumStorage
import fr.unreal852.quantum.utils.Extensions.setDimensionAndGenerator
import fr.unreal852.quantum.world.QuantumWorld
import fr.unreal852.quantum.world.QuantumWorldData
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarted
import net.fabricmc.fabric.api.event.player.UseBlockCallback
//import net.kyrptonaught.customportalapi.api.CustomPortalBuilder
import net.minecraft.registry.Registries
import net.minecraft.server.MinecraftServer
import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import xyz.nucleoid.fantasy.Fantasy
import java.util.concurrent.ConcurrentHashMap

object Quantum : ModInitializer {

    const val MOD_ID = "quantum"
    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)
    private val WORLDS: MutableMap<Identifier, QuantumWorld> = ConcurrentHashMap()

    override fun onInitialize() {

        CommandRegistration.registerCommands()

        ServerLifecycleEvents.SERVER_STARTED.register(ServerStarted { server: MinecraftServer ->
            loadWorlds(server)
            //loadPortals(server)
        })

        UseBlockCallback.EVENT.register(PlayerUseSignHandler())
        ServerPlayerEvents.AFTER_RESPAWN.register(PlayerRespawnHandler())
    }

    fun worldExists(identifier: Identifier): Boolean {
        return WORLDS.containsKey(identifier)
    }

    fun getOrCreateWorld(server: MinecraftServer, worldData: QuantumWorldData, saveToDisk: Boolean): QuantumWorld? {
        if (WORLDS.containsKey(worldData.worldId))
            return WORLDS[worldData.worldId]

        val fantasy = Fantasy.get(server)
        val runtimeWorldConfig = worldData.runtimeWorldConfig.setDimensionAndGenerator(server, worldData) // Important, set dim and chunk generator
        val runtimeWorldHandle = fantasy.getOrOpenPersistentWorld(worldData.worldId, runtimeWorldConfig)

        val world = QuantumWorld(runtimeWorldHandle, worldData)
        WORLDS[worldData.worldId] = world

        if (saveToDisk)
            QuantumStorage.getQuantumState(server).addWorld(worldData)

        return world
    }

    fun deleteWorld(identifier: Identifier): Boolean {
        val world = WORLDS.getOrDefault(identifier, null) ?: return false
        val server = world.serverWorld.server
        val fantasy = Fantasy.get(world.serverWorld.server)

        if (!fantasy.tickDeleteWorld(world.serverWorld))
            return false

        val state = QuantumStorage.getQuantumState(server)
        state.removeWorld(world.worldData)
        WORLDS.remove(identifier)

        return true
    }

    private fun loadWorlds(server: MinecraftServer) {

        WORLDS.clear() // This is important otherwise it can lead to a world not being loaded if the client close and reopen a world

        val state = QuantumStorage.getQuantumState(server)

        for (world in state.getWorlds()) {
            getOrCreateWorld(server, world, false)
            LOGGER.info("Found world '{}', loading it.", world.worldId)
        }
    }

    // private fun loadPortals(server: MinecraftServer) {
    //     val state = QuantumStorage.getQuantumState(server)

    //     for (portal in state.getPortals()) {
    //         val itemPortal = Registries.ITEM.get(portal.portalIgniteItemId)
    //         LOGGER.info("Found portal '{}', loading it.", portal.destinationId)
    //         CustomPortalBuilder.beginPortal()
    //             .frameBlock(portal.portalBlockId)
    //             .lightWithItem(itemPortal)
    //             .destDimID(portal.destinationId)
    //             .tintColor(portal.portalColor)
    //             .registerPortal()
    //     }
    // }
}