package com.uni.exam1.todo.data

import android.util.Log
import com.uni.exam1.core.TAG
import com.uni.exam1.core.data.remote.Api
import com.uni.exam1.todo.data.local.ItemDao
import com.uni.exam1.todo.data.remote.ItemEvent
import com.uni.exam1.todo.data.remote.ItemService
import com.uni.exam1.todo.data.remote.ItemWsClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class ItemRepository(
    private val itemService: ItemService,
    private val itemWsClient: ItemWsClient,
    private val itemDao: ItemDao
) {
    val itemStream by lazy { itemDao.getAll() }

    init {
        Log.d(TAG, "init")
    }

    private fun getBearerToken() = "Bearer ${Api.tokenInterceptor.token}"

    suspend fun refresh() {
        Log.d(TAG, "refresh started")
        try {
            val item = itemService.find(authorization = getBearerToken())
            itemDao.deleteAll()
            itemDao.insert(item)
            Log.d(TAG, "refresh succeeded")
        } catch (e: Exception) {
            Log.w(TAG, "refresh failed", e)
        }
    }

    suspend fun sync() {
        try {
            val items = itemDao.getAll().firstOrNull() ?: emptyList()
            Log.d(TAG, "repo sync $items...")

            val updatedItems = itemService.sync(authorization = getBearerToken(), items = items)
//            itemDao.deleteAll()
//            updatedItems.forEach { itemDao.insert(it) }
            Log.d(TAG, "sync $items succeeded")
        } catch (e: Exception) {
            Log.w(TAG, "sync failed", e)
        }
    }

    suspend fun openWsClient() {
        Log.d(TAG, "openWsClient")
        withContext(Dispatchers.IO) {
            getItemEvents().collect {
                Log.d(TAG, "Item event collected $it")
                if (it.isSuccess) {
                    val itemEvent = it.getOrNull();
                    when (itemEvent?.type) {
                        "created" -> handleItemCreated(itemEvent.payload)
                        "updated" -> handleItemUpdated(itemEvent.payload)
                        "deleted" -> handleItemDeleted(itemEvent.payload)
                    }
                }
            }
        }
    }

    suspend fun closeWsClient() {
        Log.d(TAG, "closeWsClient")
        withContext(Dispatchers.IO) {
            itemWsClient.closeSocket()
        }
    }

    suspend fun getItemEvents(): Flow<Result<ItemEvent>> = callbackFlow {
        Log.d(TAG, "getItemEvents started")
        itemWsClient.openSocket(
            onEvent = {
                Log.d(TAG, "onEvent $it")
                if (it != null) {
                    trySend(Result.success(it))
                }
            },
            onClosed = { close() },
            onFailure = { close() });
        awaitClose { itemWsClient.closeSocket() }
    }

    suspend fun downloadItem(itemId: String): Result<Item> {
        Log.d(TAG, "downloadItem $itemId...")
        return try {
            val item = itemService.read(itemId = itemId, authorization = getBearerToken())
            Log.d(TAG, "downloadItem $itemId succeeded")
            handleItemCreated(item)
            Result.success(item)
        } catch (e: Exception) {
            Log.w(TAG, "downloadItem $itemId failed", e)
            Result.failure(e)
        }
    }

    suspend fun update(item: Item, isOnline: Boolean): Item {
        Log.d(TAG, "update $item...")
        if (isOnline) {
            val updatedItem =
                itemService.update(itemId = item._id, item = item, authorization = getBearerToken())
            Log.d(TAG, "update $item succeeded")
            handleItemUpdated(updatedItem)
            return updatedItem
        } else {
            itemDao.update(item)

            Log.d(TAG, "update $item succeeded")
            return item
        }
    }

    suspend fun save(item: Item, isOnline: Boolean): Item {
        Log.d(TAG, "save $item...")
        return if (isOnline) {
            val createdItem = itemService.create(item = item, authorization = getBearerToken())
            Log.d(TAG, "save $item succeeded")
            handleItemCreated(createdItem)
            createdItem
        } else {
            itemDao.insert(item)
            Log.d(TAG, "save $item succeeded")
            item
        }
    }

    private suspend fun handleItemDeleted(item: Item) {
        Log.d(TAG, "handleItemDeleted - todo $item")
    }

    private suspend fun handleItemUpdated(item: Item) {
        Log.d(TAG, "handleItemUpdated...")
        itemDao.update(item)
    }

    private suspend fun handleItemCreated(item: Item) {
        Log.d(TAG, "handleItemCreated...")
        itemDao.insert(item)
    }

    suspend fun deleteAll() {
        itemDao.deleteAll()
    }

    fun setToken(token: String) {
        itemWsClient.authorize(token)
    }
}