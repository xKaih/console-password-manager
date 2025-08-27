package io.github.xkaih

import java.nio.ByteBuffer
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

//I found that object is a better way to do a "static class" than make one class and inside a companion object
//but, the thing is that everything in the object is "static"
object EncryptionHandler {
    private const val SALT_LEN = 16 // 128-bit salt -> This makes the key different even when u uses the same password
    private const val IV_LEN = 12 // This is to ensure different results when u encrypt the same text with the same password
    private const val KEY_LEN_BITS = 256 // AES-256
    private const val PBKDF2_ITERS = 210_000 //PBKDF2 iterations
    private const val GCM_TAG_BITS = 128 // For AES GCM

    private val rng = SecureRandom()

    //Derive an AES key from a password and a random salt
    private fun deriveKey(password: CharArray, salt: ByteArray): SecretKey {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(password,salt,PBKDF2_ITERS,KEY_LEN_BITS)
        val keyBytes = factory.generateSecret(spec).encoded
        spec.clearPassword()
        return SecretKeySpec(keyBytes,"AES")
    }

    fun encrypt(plainText: ByteArray, password: CharArray): String {
        val salt = ByteArray(SALT_LEN).also { rng.nextBytes(it) } // Generate random salt
        val key = deriveKey(password, salt)
        val iv = ByteArray(IV_LEN).also { rng.nextBytes(it) } // Generate random IV

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(GCM_TAG_BITS, iv))

        val ciphertext = cipher.doFinal(plainText)

        // Empaquetado: [salt | iv | ciphertext+tag]
        val out = ByteBuffer.allocate(salt.size + iv.size + ciphertext.size)
            .put(salt)
            .put(iv)
            .put(ciphertext)
            .array()

        password.fill('\u0000') // Clean the password

        return Base64.getEncoder().encodeToString(out)
    }

    fun decrypt(inputB64: String, password: CharArray): String {
        val all = Base64.getDecoder().decode(inputB64)
        require(all.size >= SALT_LEN + IV_LEN + 16) {"Invalid data"}

        val salt = all.copyOfRange(0, SALT_LEN) // I always forget that coptOfRange excludes the last index
        val iv = all.copyOfRange(SALT_LEN, SALT_LEN + IV_LEN)
        val cipherText = all.copyOfRange(SALT_LEN + IV_LEN, all.size)

        val key = deriveKey(password, salt)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(GCM_TAG_BITS, iv))

        //Throws AEADBadTagException if the password is incorrect or there are som corruption
        val plainText = cipher.doFinal(cipherText)

        password.fill('\u0000')
        return plainText.decodeToString()
    }

}