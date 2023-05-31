package gg.growly.experimental.model

import com.github.shynixn.mccoroutine.bukkit.asyncDispatcher
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import gg.growly.experimental.ExperimentalPlugin
import gg.growly.experimental.PluginDatabaseService
import kotlinx.coroutines.withContext
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.litote.kmongo.eq
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * @author GrowlyX
 * @since 5/30/2023
 */
object PlayerProfileService : Listener
{
    // internal for org.bukkit.Player extentions in player.extensions.kt
    internal val playerProfileCache =
        ConcurrentHashMap<UUID, PlayerProfile>(1024)

    private lateinit var plugin: ExperimentalPlugin

    fun configure(plugin: ExperimentalPlugin)
    {
        this.plugin = plugin
        plugin.server.pluginManager
            .registerSuspendingEvents(
                this, plugin
            )
    }

    @EventHandler
    suspend fun onPlayerPreLogin(event: AsyncPlayerPreLoginEvent)
    {
        val playerProfile = PluginDatabaseService
            .playerProfileCollection
            .findOne(
                PlayerProfile::uniqueId eq event.uniqueId
            )
            ?: PlayerProfile(event.uniqueId)

        playerProfileCache[event.uniqueId] = playerProfile
    }

    @EventHandler
    suspend fun onPlayerQuit(event: PlayerQuitEvent)
    {
        val playerProfile = playerProfileCache
            .remove(event.player.uniqueId)
            ?: return
        saveProfile(profile = playerProfile)
    }

    suspend fun saveProfile(profile: PlayerProfile)
    {
        withContext(plugin.asyncDispatcher) {
            PluginDatabaseService
                .playerProfileCollection
                .save(profile)
        }
    }
}
