package com.sarabyeet.toget.arch

import androidx.lifecycle.*
import com.sarabyeet.toget.db.AppDatabase
import com.sarabyeet.toget.db.model.CategoryEntity
import com.sarabyeet.toget.db.model.ItemEntity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ToGetViewModel : ViewModel() {

    private lateinit var itemRepository: ToGetRepository

    // Backing property
    private var _itemListLiveData = MutableLiveData<List<ItemEntity>>()
    val itemListLiveData: LiveData<List<ItemEntity>> = _itemListLiveData

    // Backing property
    private var _categoryListLiveData = MutableLiveData<List<CategoryEntity>>()
    val categoryListLiveData: LiveData<List<CategoryEntity>> = _categoryListLiveData

    // Used to check whether the insert or update item to database was successfully performed or not
    private val transactionChannel = Channel<ToGetEvents>()
    val eventFlow = transactionChannel.receiveAsFlow()

    // Initialize the connectivity of our FLows with db for Item Entities and Category Entities
    fun init(appDatabase: AppDatabase) {
        itemRepository = ToGetRepository(appDatabase)

        viewModelScope.launch {
            // As collect is a suspend function, if you want to collect
            // multiple flows in parallel, you need to do so in
            // different coroutines
            launch {
                itemRepository.getAllItems().collect { items ->
                    _itemListLiveData.postValue(items)
                }
            }
            launch {
                itemRepository.getAllCategories().collect { categories ->
                    _categoryListLiveData.postValue(categories)
                }
            }
        }
    }

    // region Item Entity
    fun insertItem(itemEntity: ItemEntity) {
        viewModelScope.launch {
            itemRepository.insertItem(itemEntity)
            transactionChannel.send(ToGetEvents.DbTransaction(true))
        }
    }
    fun reInsertItem(itemEntity: ItemEntity) {
        viewModelScope.launch {
            itemRepository.insertItem(itemEntity)
        }
    }

    fun deleteItem(itemEntity: ItemEntity) {
        viewModelScope.launch {
            itemRepository.deleteItem(itemEntity)
        }
    }

    fun updateItem(itemEntity: ItemEntity) {
        viewModelScope.launch {
            itemRepository.updateItem(itemEntity)
            transactionChannel.send(ToGetEvents.DbTransaction(true))
        }
    }
    // endregion Item Entity

    // region Category Entity
    fun insertCategory(itemEntity: CategoryEntity) {
        viewModelScope.launch {
            itemRepository.insertCategory(itemEntity)
            transactionChannel.send(ToGetEvents.DbTransaction(true))
        }
    }

    fun deleteCategory(itemEntity: CategoryEntity) {
        viewModelScope.launch {
            itemRepository.deleteCategory(itemEntity)
        }
    }

    fun updateCategory(itemEntity: CategoryEntity) {
        viewModelScope.launch {
            itemRepository.updateCategory(itemEntity)
        }
    }
    // endregion Category Entity


}