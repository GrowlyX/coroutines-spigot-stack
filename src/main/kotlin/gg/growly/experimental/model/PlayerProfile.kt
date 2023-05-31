package gg.growly.experimental.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

/**
 * @author GrowlyX
 * @since 5/30/2023
 */
@Serializable
data class PlayerProfile(
    val uniqueId: @Contextual UUID,
)
{
    var balance: Int = 0
        private set

    suspend fun increaseBalance(amount: Int)
    {
        balance += amount
        // TODO: add in some booster logic for funsies

        PlayerProfileService
            .saveProfile(profile = this)
    }
}
