package gg.growly.experimental

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.setSuspendingExecutor
import gg.growly.experimental.command.BalanceUpdateCommand
import gg.growly.experimental.command.Top10BalancesCommand
import gg.growly.experimental.model.PlayerProfileService
import org.bukkit.plugin.java.annotation.command.Command
import org.bukkit.plugin.java.annotation.plugin.Plugin
import org.bukkit.plugin.java.annotation.plugin.author.Author
import org.litote.kmongo.serialization.SerializationClassMappingTypeService
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.io.File

/**
 * @author GrowlyX
 * @since 5/30/2023
 */
@Plugin(
    name = "experimental",
    version = "1.0.0"
)
@Author("GrowlyX")
@Command(name = "top10balances")
@Command(name = "updatebal")
class ExperimentalPlugin : SuspendingJavaPlugin()
{
    var config = PluginConfig()

    override fun onEnable()
    {
        System.setProperty(
            "org.litote.mongo.mapping.service",
            SerializationClassMappingTypeService::class.qualifiedName!!
        )

        val loader = YamlConfigurationLoader
            .builder()
            .path(
                File(dataFolder, "config.yml").toPath()
            )
            .build()

        loader
            .load()
            .get(PluginConfig::class.java)
            ?.apply {
                config = this
            }

        PluginDatabaseService
            .configure(plugin = this)
        PlayerProfileService
            .configure(plugin = this)

        getCommand("top10balances")!!
            .setSuspendingExecutor(
                Top10BalancesCommand(plugin = this)
            )

        getCommand("updatebal")!!
            .setSuspendingExecutor(
                BalanceUpdateCommand(plugin = this)
            )
    }

    override fun onDisable()
    {
        PluginDatabaseService.close()
    }
}
