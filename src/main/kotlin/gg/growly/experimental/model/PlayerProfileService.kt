package gg.growly.experimental.model

import com.github.shynixn.mccoroutine.bukkit.asyncDispatcher
import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import gg.growly.experimental.ExperimentalPlugin
import gg.growly.experimental.PluginDatabaseService
import kotlinx.coroutines.withContext
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.aggregate
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

    @Serializable
    data class Top10Result(
        val uniqueId: @Contextual UUID,
        val username: String,
        val value: Int
    )

    suspend fun aggregateTop10BalanceEntries() =
        withContext(plugin.asyncDispatcher) {
            PluginDatabaseService
                .playerProfileCollection
                .aggregate<Top10Result>(
                    sort(
                        descending(PlayerProfile::balance)
                    ),
                    limit(10),
                    project(
                        Top10Result::uniqueId from PlayerProfile::uniqueId,
                        Top10Result::username from PlayerProfile::username,
                        Top10Result::value from PlayerProfile::balance
                    )
                )
                .toList()
        }

    @EventHandler
    suspend fun onPlayerPreLogin(event: AsyncPlayerPreLoginEvent)
    {
        val playerProfile = PluginDatabaseService
            .playerProfileCollection
            .findOne(
                PlayerProfile::uniqueId eq event.uniqueId
            )
            ?: PlayerProfile(
                uniqueId = event.uniqueId,
                username = ""
            )

        playerProfileCache[event.uniqueId] = playerProfile
    }

    @EventHandler
    suspend fun onPlayerLogin(event: PlayerLoginEvent)
    {
        val profile = event.player.profileNonNull()
        val previousUsername = profile.username
        profile.username = event.player.name

        val usernameNoLongerMatches =
            previousUsername != profile.username

        if (usernameNoLongerMatches)
        {
            saveProfile(profile)
            plugin.logger.info("Pushing username update for ${profile.uniqueId}")
        }
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
