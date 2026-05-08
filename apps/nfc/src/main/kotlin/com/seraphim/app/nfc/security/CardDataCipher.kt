package com.seraphim.app.nfc.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * 卡片数据 AES 加密骨架
 *
 * 使用 Android Keystore 存储密钥，AES-256-GCM 模式加密
 *
 * 注意：这是骨架实现，完整加密需要：
 * - 与 Biometric 绑定（setUserAuthenticationRequired）
 * - IV 持久化存储
 * - 密钥版本管理
 */
class CardDataCipher {

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "hill_card_encryption_key"
        private const val AES_MODE = "AES/GCM/NoPadding"
        private const val GCM_TAG_LENGTH = 128
        private const val GCM_IV_LENGTH = 12
    }

    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
        load(null)
    }

    /**
     * 获取或创建加密密钥
     */
    private fun getOrCreateKey(): SecretKey {
        val entry = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        return entry?.secretKey ?: generateKey()
    }

    /**
     * 生成新的 AES-256 密钥（存储在 Keystore 中）
     */
    private fun generateKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                // TODO: 生产环境启用生物识别绑定
                // .setUserAuthenticationRequired(true)
                // .setUserAuthenticationValidityDurationSeconds(30)
                .build()
        )
        return keyGenerator.generateKey()
    }

    /**
     * 加密数据
     * @return 密文 + IV 的拼接（前 12 字节为 IV）
     */
    fun encrypt(plaintext: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        val iv = cipher.iv // 12 bytes
        val ciphertext = cipher.doFinal(plaintext)
        return iv + ciphertext
    }

    /**
     * 解密数据
     * @param encryptedData 密文 + IV 的拼接（前 12 字节为 IV）
     */
    fun decrypt(encryptedData: ByteArray): ByteArray {
        val iv = encryptedData.copyOfRange(0, GCM_IV_LENGTH)
        val ciphertext = encryptedData.copyOfRange(GCM_IV_LENGTH, encryptedData.size)

        val cipher = Cipher.getInstance(AES_MODE)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), spec)
        return cipher.doFinal(ciphertext)
    }

    /**
     * 检查密钥是否存在
     */
    fun hasKey(): Boolean {
        return keyStore.containsAlias(KEY_ALIAS)
    }

    /**
     * 删除密钥（用于重置安全设置）
     */
    fun deleteKey() {
        keyStore.deleteEntry(KEY_ALIAS)
    }
}
