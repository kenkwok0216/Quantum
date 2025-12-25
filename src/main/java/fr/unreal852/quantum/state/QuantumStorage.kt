package fr.unreal852.quantum.state

import fr.unreal852.quantum.Quantum
import fr.unreal852.quantum.portal.QuantumPortalData
import fr.unreal852.quantum.world.QuantumWorldData
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.server.MinecraftServer
import net.minecraft.world.PersistentState
import net.minecraft.world.PersistentStateType
import net.minecraft.datafixer.DataFixTypes
import com.mojang.serialization.Codec

class QuantumStorage : PersistentState() {

    private val worlds = mutableListOf<QuantumWorldData>()
    private val portals = mutableListOf<QuantumPortalData>()

    fun getWorlds(): List<QuantumWorldData> = worlds
    fun getPortals(): List<QuantumPortalData> = portals

    fun addWorld(worldData: QuantumWorldData) {
        worlds.add(worldData)
        markDirty()
    }

    fun addPortal(portalData: QuantumPortalData) {
        portals.add(portalData)
        markDirty()
    }

    // Direct removeWorld overload for Quantum.deleteWorld
    fun removeWorld(worldData: QuantumWorldData): Boolean {
        val removed = worlds.remove(worldData)
        if (removed) markDirty()
        return removed
    }

    // Predicate-based removal
    fun removeWorld(predicate: (QuantumWorldData) -> Boolean): Boolean {
        val world = worlds.find(predicate) ?: return false
        worlds.remove(world)
        markDirty()
        return true
    }

    fun removePortal(portalData: QuantumPortalData): Boolean {
        val removed = portals.remove(portalData)
        if (removed) markDirty()
        return removed
    }

    fun removePortal(predicate: (QuantumPortalData) -> Boolean): Boolean {
        val portal = portals.find(predicate) ?: return false
        portals.remove(portal)
        markDirty()
        return true
    }

    fun writeNbt(nbt: NbtCompound): NbtCompound {
        val worldsNbtList = NbtList()
        val portalsNbtList = NbtList()

        for (entry in worlds) {
            val entryNbt = NbtCompound()
            entry.writeToNbt(entryNbt)
            worldsNbtList.add(entryNbt)
        }

        for (entry in portals) {
            val entryNbt = NbtCompound()
            entry.writeToNbt(entryNbt)
            portalsNbtList.add(entryNbt)
        }

        nbt.put(WORLDS_KEY, worldsNbtList)
        nbt.put(PORTALS_KEY, portalsNbtList)
        return nbt
    }

    companion object {
        private const val STORAGE_ID = Quantum.MOD_ID
        private const val WORLDS_KEY = "worlds"
        private const val PORTALS_KEY = "portals"

        private val TYPE: PersistentStateType<QuantumStorage> =
            PersistentStateType(
                STORAGE_ID,
                { QuantumStorage() },
                { Codec.unit(QuantumStorage()) },
                DataFixTypes.LEVEL
            )

        fun getQuantumState(server: MinecraftServer): QuantumStorage {
            val stateManager = server.overworld.persistentStateManager
            return stateManager.getOrCreate(TYPE)
        }

        fun fromNbt(nbt: NbtCompound): QuantumStorage {
            val quantumStorage = QuantumStorage()

            val worldsNbtList = nbt.getList(WORLDS_KEY).orElse(NbtList())
            val portalsNbtList = nbt.getList(PORTALS_KEY).orElse(NbtList())

            for (i in 0 until worldsNbtList.size) {
                val entryNbt = worldsNbtList.getCompound(i).orElse(NbtCompound())
                quantumStorage.worlds.add(QuantumWorldData.fromNbt(entryNbt))
            }
            for (i in 0 until portalsNbtList.size) {
                val entryNbt = portalsNbtList.getCompound(i).orElse(NbtCompound())
                quantumStorage.portals.add(QuantumPortalData.fromNbt(entryNbt))
            }
            return quantumStorage
        }
    }
}