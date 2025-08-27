package io.github.xkaih
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import java.io.File

class AccountHandler {

    var userPassword = "" //This should be encrypted password and everytime it is used, decrypt on runtime, but ill keep it like this for the moment
    val accounts = mutableMapOf<Int, AccountData>()
    var nextId = 0
    val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    constructor(password: String?) {
        if (password == null)
            throw IllegalArgumentException("Password cannot be null")
        this.userPassword = password
        val file = File("accounts.json")
        if (file.exists()) {
            try {
                val content = file.readText()
                val accountStorage = json.decodeFromString<AccountStorage>(content)
                nextId = accountStorage.uniqueId
                accounts.putAll(accountStorage.accountData)
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getAccount(id: Int): AccountData? {
        return accounts[id]
    }

    fun getDecryptedAccounts(): Map<Int, AccountData> {
        /*
        * I just fought with this function because it was modifying the original password... I needed like 1.5 hours to just realize
        * that AccountData is OBVIOUSLY a reference so when I do putAll or toMap for creating a "copy", yeah, was created a copy of my og map
        * but cause AccountData is a reference to an object, the reference was copying too to de new map. So I just needed to create
        * a new AccountData object for every entry of my og map lol 🤡🤡🤡🤡🤡🤡🤡🤡
        */
        return accounts.mapValues {
            AccountData(
                it.value.name,
                it.value.email,
                EncryptionHandler.decrypt(it.value.cipherPassword, userPassword.toCharArray())
            )
        }

    }

    fun addAccount(accountData: AccountData) {
        val cipherPassword = EncryptionHandler.encrypt(accountData.cipherPassword.toByteArray(),userPassword.toCharArray())
        accounts[nextId++] = AccountData(accountData.name, accountData.email, cipherPassword)
    }

    fun deleteAccount(uniqueId: Int): Boolean {
        return accounts.remove(uniqueId) != null
    }

    fun modifyAccount(uniqueId: Int, name: String? = null, email: String? = null, password: String? = null) {
        var cipherPassword = ""
        if (password != null) {
            cipherPassword = EncryptionHandler.encrypt(password!!.toByteArray(), this.userPassword.toCharArray())
        }

        val account = getAccount(uniqueId)
        account?.name = name ?: account.name
        account?.email = email ?: account.email
        account?.cipherPassword = (if (!cipherPassword.isEmpty()) cipherPassword else account!!.cipherPassword)
    }

    fun saveChanges() {
        val content = json.encodeToString(AccountStorage(nextId, accounts))
        File("accounts.json").writeText(content)
    }

}