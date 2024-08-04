package fr.unreal852.quantum.state

import fr.unreal852.quantum.Quantum
import fr.unreal852.quantum.portal.QuantumPortalData
import fr.unreal852.quantum.world.QuantumWorldData
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.registry.RegistryWrapper.WrapperLookup
import net.minecraft.server.MinecraftServer
import net.minecraft.world.PersistentState

class QuantumStorage : PersistentState() {

    private val worlds: MutableList<QuantumWorldData> = ArrayList()
    private val portals: MutableList<QuantumPortalData> = ArrayList()

    fun getWorlds(): List<QuantumWorldData> {
        return worlds
    }

    fun getPortals(): List<QuantumPortalData> {
        return portals
    }

    fun addWorldData(quantumWorldData: QuantumWorldData) {
        worlds.add(quantumWorldData)
        markDirty()
    }

    fun addPortalData(quantumPortalData: QuantumPortalData) {
        portals.add(quantumPortalData)
        markDirty()
    }

    fun removeWorldData(quantumWorldData: QuantumWorldData) {
        worlds.remove(quantumWorldData)
        markDirty()
    }

    fun removePortalData(quantumPortalData: QuantumPortalData) {
        portals.remove(quantumPortalData)
        markDirty()
    }

    override fun writeNbt(nbt: NbtCompound, registryLookup: WrapperLookup): NbtCompound {
        val worldsNbtList = NbtList()
        val portalsNbt = NbtList()
        for (entry in worlds) {
            val entryNbt = NbtCompound()
            entry.writeToNbt(entryNbt)
            worldsNbtList.add(entryNbt)
        }
        for (entry in portals) {
            val entryNbt = NbtCompound()

        }
        nbt.put(WORLDS_KEY, worldsNbtList)
        return nbt
    }

    companion object {

        private const val STORAGE_ID = Quantum.MOD_ID
        private const val WORLDS_KEY = "worlds"
        private const val PORTALS_KEY = "portals"

        private val PersistentStateTypeLoader = Type(
            { QuantumStorage() },
            { nbt: NbtCompound, registryLookup: WrapperLookup -> fromNbt(nbt, registryLookup) },
            null
        )

        fun getQuantumState(server: MinecraftServer): QuantumStorage {
            val stateManager = server.overworld.persistentStateManager
            val quantumState = stateManager.getOrCreate(PersistentStateTypeLoader, STORAGE_ID)
            quantumState.markDirty()
            return quantumState
        }

        @Suppress("UNUSED_PARAMETER")
        private fun fromNbt(nbt: NbtCompound, registryLookup: WrapperLookup): QuantumStorage {
            val quantumStorage = QuantumStorage()
            val worldsNbtList = nbt.getList(WORLDS_KEY, 10) // 10 is the NbtCompound type
            val portalsNbt = nbt.getList(PORTALS_KEY, 10)
            for (i in worldsNbtList.indices) {
                val entryNbt = worldsNbtList.getCompound(i)
                quantumStorage.worlds.add(QuantumWorldData.fromNbt(entryNbt))
            }
            for (i in portalsNbt.indices) {
                val entryNbt = portalsNbt.getCompound(i)
                quantumStorage.portals.add(QuantumPortalData.fromNbt(entryNbt))
            }
            return quantumStorage
        }
    }
}
