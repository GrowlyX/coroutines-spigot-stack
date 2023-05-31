package gg.growly.experimental

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import gg.growly.experimental.model.PlayerProfile
import org.bson.UuidRepresentation
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

/**
 * @author GrowlyX
 * @since 5/30/2023
 */
object PluginDatabaseService
{
    private lateinit var client: CoroutineClient
    lateinit var playerProfileCollection: CoroutineCollection<PlayerProfile>

    fun configure(plugin: ExperimentalPlugin)
    {
        client = KMongo
            .createClient(
                MongoClientSettings
                    .builder()
                    .uuidRepresentation(UuidRepresentation.STANDARD)
                    .applyConnectionString(
                        ConnectionString(plugin.config.mongo.uri)
                    )
                    .build()
            )
            .coroutine

        playerProfileCollection = client
            .getDatabase(plugin.config.mongo.database)
            .getCollection<PlayerProfile>()

        plugin.logger.info("CoroutineClient")
    }

    fun close()
    {
        client.close()
    }
}
