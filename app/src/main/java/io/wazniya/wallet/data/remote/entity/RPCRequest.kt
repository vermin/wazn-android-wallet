package io.wazniya.wallet.data.remote.entity

import kotlinx.serialization.Serializable

@Serializable
data class RPCRequest<T>(
    val jsonrpc: String = "2.0",
    val id: String = "wazniya",
    val method: String,
    val params: T?
)
