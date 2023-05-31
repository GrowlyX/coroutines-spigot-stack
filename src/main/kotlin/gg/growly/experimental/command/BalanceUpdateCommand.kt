package gg.growly.experimental.command

import com.github.shynixn.mccoroutine.bukkit.SuspendingCommandExecutor
import gg.growly.experimental.ExperimentalPlugin
import gg.growly.experimental.model.profileNonNull
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 5/30/2023
 */
class BalanceUpdateCommand(
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
        if (sender !is Player)
        {
            sender.sendMessage("command cannot be ran by console")
            return true
        }

        if (args.isEmpty())
        {
            sender.sendMessage("${ChatColor.RED}Usage: /$label <amount>")
            return true
        }

        val amount = args[0].toIntOrNull()
            ?: return run {
                sender.sendMessage("argument was not an integer")
                true
            }

        val profile = sender.profileNonNull()
        profile.increaseBalance(amount)

        sender.sendMessage("${ChatColor.GREEN}Added $$amount to your balance")
        return true
    }
}
