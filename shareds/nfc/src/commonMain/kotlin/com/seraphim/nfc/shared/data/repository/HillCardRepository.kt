package com.seraphim.nfc.shared.data.repository

import com.seraphim.nfc.shared.data.db.dao.CardGroupDao
import com.seraphim.nfc.shared.data.db.dao.HillCardDao
import com.seraphim.nfc.shared.data.db.entity.CardGroupEntity
import com.seraphim.nfc.shared.data.db.entity.HillCardEntity
import com.seraphim.nfc.shared.domain.crypto.CardDataEncryptor
import com.seraphim.nfc.shared.domain.model.CardGroup
import com.seraphim.nfc.shared.domain.model.DefaultGroups
import com.seraphim.nfc.shared.domain.model.HillCardInfo
import com.seraphim.nfc.shared.domain.model.SectorData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * 卡片仓库
 *
 * 负责：
 * - 卡片 CRUD
 * - 分组管理
 * - 加密/解密扇区数据
 * - 预置分组初始化
 */
class HillCardRepository(
    private val cardDao: HillCardDao,
    private val groupDao: CardGroupDao,
    private val encryptor: CardDataEncryptor,
) {

    private val json = Json { ignoreUnknownKeys = true }

    // ── 卡片操作 ───────────────────────────────

    fun observeAllCards(): Flow<List<HillCardInfo>> =
        cardDao.observeAll().map { list -> list.map { it.toDomain() } }

    fun observeCardsByGroup(groupId: String): Flow<List<HillCardInfo>> =
        cardDao.observeByGroup(groupId).map { list -> list.map { it.toDomain() } }

    suspend fun getCardById(id: String): HillCardInfo? =
        cardDao.getById(id)?.toDomain()

    suspend fun getAllCards(): List<HillCardInfo> =
        cardDao.getAll().map { it.toDomain() }

    suspend fun searchCards(query: String): List<HillCardInfo> =
        cardDao.search(query).map { it.toDomain() }

    suspend fun insertCard(card: HillCardInfo) {
        val entity = card.toEntity(encryptor)
        cardDao.insert(entity)
    }

    suspend fun saveCard(card: HillCardInfo) = insertCard(card)

    suspend fun updateCard(card: HillCardInfo) {
        val entity = card.toEntity(encryptor).copy(
            updatedAt = Instant.fromEpochMilliseconds(java.lang.System.currentTimeMillis()),
        )
        cardDao.update(entity)
    }

    suspend fun deleteCard(id: String) {
        cardDao.deleteById(id)
    }

    suspend fun cardCount(): Int = cardDao.count()

    // ── 分组操作 ───────────────────────────────

    fun observeAllGroups(): Flow<List<CardGroup>> =
        groupDao.observeAll().map { list -> list.map { it.toDomain() } }

    suspend fun getGroupById(id: String): CardGroup? =
        groupDao.getById(id)?.toDomain()

    suspend fun saveGroup(group: CardGroup) {
        groupDao.insert(group.toEntity())
    }

    suspend fun deleteGroup(id: String) {
        groupDao.deleteById(id)
    }

    /**
     * 初始化预置分组（首次启动时调用）
     */
    suspend fun initDefaultGroups() {
        if (groupDao.count() == 0) {
            val now = Instant.fromEpochMilliseconds(java.lang.System.currentTimeMillis())
            DefaultGroups.map { it.copy(createdAt = now, updatedAt = now) }
                .map { it.toEntity() }
                .let { groupDao.insertAll(it) }
        }
    }

    // ── 转换方法 ───────────────────────────────

    private fun HillCardEntity.toDomain(): HillCardInfo {
        val sectors = try {
            encryptor.decryptSectors(sectorsJson)
        } catch (_: Exception) {
            emptyList()
        }
        return HillCardInfo(
            id = id,
            uid = uid,
            cardType = com.seraphim.nfc.shared.domain.model.CardType.valueOf(cardType),
            sectors = sectors,
            manufacturer = manufacturer,
            readAt = readAt,
            name = name,
            group = groupId ?: "未分组",
            note = note,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }

    private fun HillCardInfo.toEntity(encryptor: CardDataEncryptor): HillCardEntity {
        val now = Instant.fromEpochMilliseconds(java.lang.System.currentTimeMillis())
        return HillCardEntity(
            id = id,
            uid = uid,
            cardType = cardType.name,
            name = name,
            groupId = group.takeIf { it != "未分组" },
            note = note,
            sectorsJson = encryptor.encryptSectors(sectors),
            manufacturer = manufacturer,
            readAt = readAt,
            createdAt = createdAt ?: now,
            updatedAt = updatedAt ?: now,
        )
    }

    private fun CardGroupEntity.toDomain() = CardGroup(
        id = id,
        name = name,
        color = color,
        sortOrder = sortOrder,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    private fun CardGroup.toEntity() = CardGroupEntity(
        id = id,
        name = name,
        color = color,
        sortOrder = sortOrder,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}
