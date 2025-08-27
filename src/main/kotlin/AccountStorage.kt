package io.github.xkaih

import kotlinx.serialization.Serializable

@Serializable
data class AccountStorage(val uniqueId: Int, val accountData: Map<Int, AccountData>)
