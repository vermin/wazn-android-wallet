package io.wazniya.wallet.data.remote.entity

import kotlinx.serialization.Serializable

@Serializable
data class RPCError(val code: Int, val message: String)
