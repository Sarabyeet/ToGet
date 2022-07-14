package com.sarabyeet.toget.arch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarabyeet.toget.db.AppDatabase
import com.sarabyeet.toget.db.model.ItemEntity
import kotlinx.coroutines.launch

class ToGetViewModel : ViewModel() {

    private lateinit var itemRepository: ToGetRepository

    // Backing property
    private var _itemListLiveData = MutableLiveData<List<ItemEntity>>()
    val itemListLiveData: LiveData<List<ItemEntity>> = _itemListLiveData

    // used to check whether the insert or update item to database was successfully performed or not
    val transactionLiveData = MutableLiveData<Boolean>()

    fun init(appDatabase: AppDatabase) {
        itemRepository = ToGetRepository(appDatabase)

        viewModelScope.launch {
            itemRepository.getAllItems().collect { items ->
                _itemListLiveData.postValue(items)
            }
        }
    }

    fun insertItem(itemEntity: ItemEntity) {
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
        }
    }

}