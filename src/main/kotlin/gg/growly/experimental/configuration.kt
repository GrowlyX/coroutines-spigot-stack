package gg.growly.experimental

import org.spongepowered.configurate.objectmapping.ConfigSerializable

/**
 * @author GrowlyX
 * @since 5/30/2023
 */
@ConfigSerializable
data class PluginConfig(
    val mongo: MongoConfig = MongoConfig()
)

@ConfigSerializable
data class MongoConfig(
    val uri: String = "mongodb://127.0.0.1:27017",
    val database: String = "experimental"
)
