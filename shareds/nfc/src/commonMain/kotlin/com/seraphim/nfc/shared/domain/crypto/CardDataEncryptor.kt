package com.seraphim.nfc.shared.domain.crypto

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.seraphim.nfc.shared.domain.model.HillCardInfo
import com.seraphim.nfc.shared.domain.model.SectorData

/**
 * 卡片数据加密器接口
 *
 * PRD 要求：AES-256-GCM 加密，密钥由 PBKDF2 派生
 */
interface CardDataEncryptor {
    /**
     * 加密扇区数据
     */
    fun encryptSectors(sectors: List<SectorData>): String

    /**
     * 解密扇区数据
     */
    fun decryptSectors(encryptedJson: String): List<SectorData>

    /**
     * 检查是否已设置主密码
     */
    fun hasMasterPassword(): Boolean

    /**
     * 验证主密码
     */
    fun verifyPassword(password: String): Boolean

    /**
     * 设置/修改主密码
     */
    fun setPassword(oldPassword: String?, newPassword: String)
}

/**
 * 加密器骨架实现（TODO: 接入 Android Keystore + AES-256-GCM）
 */
class CardDataEncryptorStub : CardDataEncryptor {

    private val json = Json { ignoreUnknownKeys = true }

    override fun encryptSectors(sectors: List<SectorData>): String {
        // TODO: 接入 AES-256-GCM 加密
        return json.encodeToString(sectors)
    }

    override fun decryptSectors(encryptedJson: String): List<SectorData> {
        // TODO: 接入 AES-256-GCM 解密
        return json.decodeFromString(encryptedJson)
    }

    override fun hasMasterPassword(): Boolean {
        // TODO: 检查 KeyStore 中是否存在主密钥
        return false
    }

    override fun verifyPassword(password: String): Boolean {
        // TODO: PBKDF2 验证
        return true
    }

    override fun setPassword(oldPassword: String?, newPassword: String) {
        // TODO: 生成新密钥存入 KeyStore
    }
}
