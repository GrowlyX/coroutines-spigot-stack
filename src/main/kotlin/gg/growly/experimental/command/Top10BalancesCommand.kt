package gg.growly.experimental.command

import com.github.shynixn.mccoroutine.bukkit.SuspendingCommandExecutor
import gg.growly.experimental.ExperimentalPlugin
import gg.growly.experimental.model.PlayerProfileService
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

/**
 * @author GrowlyX
 * @since 5/30/2023
 */
class Top10BalancesCommand(
    private val plugin: ExperimentalPlugin
) : SuspendingCommandExecutor
{
    override suspend fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean
    {
        sender.sendMessage("=== Top 10 balances ===")

        PlayerProfileService
            .aggregateTop10BalanceEntries()
            .forEachIndexed { index, result ->
                sender.sendMessage(
                    "${ChatColor.BOLD}#${index + 1}. ${ChatColor.RESET}${result.username}: ${result.value}"
                )
            }

        return true
    }
}
