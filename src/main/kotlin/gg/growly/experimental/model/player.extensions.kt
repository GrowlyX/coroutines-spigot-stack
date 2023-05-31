package gg.growly.experimental.model

import org.bukkit.entity.Player
import java.util.UUID

/**
 * @author GrowlyX
 * @since 5/30/2023
 */
fun Player.profileNullable() = PlayerProfileService
    .playerProfileCache[uniqueId]

fun Player.profileNonNull() = uniqueId.profileNonNull()
fun UUID.profileNonNull() = PlayerProfileService
    .playerProfileCache[this]!!
