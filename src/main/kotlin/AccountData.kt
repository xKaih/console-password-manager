package io.github.xkaih

import kotlinx.serialization.Serializable

@Serializable
data class AccountData(var name: String, var email: String, var cipherPassword: String)
