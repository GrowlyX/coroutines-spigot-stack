package gg.growly.experimental

import org.bukkit.plugin.java.JavaPlugin
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
class ExperimentalPlugin : JavaPlugin()
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
    }
}
