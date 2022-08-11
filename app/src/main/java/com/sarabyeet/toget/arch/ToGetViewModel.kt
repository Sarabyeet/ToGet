package com.sarabyeet.toget.arch

import androidx.lifecycle.*
import com.sarabyeet.toget.db.AppDatabase
import com.sarabyeet.toget.db.model.CategoryEntity
import com.sarabyeet.toget.db.model.ItemEntity
import com.sarabyeet.toget.db.model.ItemWithCategoryEntity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ToGetViewModel : ViewModel() {

    private lateinit var itemRepository: ToGetRepository

    // Items list from Item table in database
    private var _itemListLiveData = MutableLiveData<List<ItemEntity>>()
    // val itemListLiveData: LiveData<List<ItemEntity>> = _itemListLiveData

    // Items and Categories list that has been joined from ItemWithCategory Table in database
    private var _itemWithCategoryLiveData = MutableLiveData<List<ItemWithCategoryEntity>>()
    val itemWithCategoryLiveData: LiveData<List<ItemWithCategoryEntity>> = _itemWithCategoryLiveData

    // Categories list from Category table in database
    private var _categoryListLiveData = MutableLiveData<List<CategoryEntity>>()
    val categoryListLiveData: LiveData<List<CategoryEntity>> = _categoryListLiveData

    // Used to check whether the insert or update item to database was successfully performed or not
    // One time event
    private val transactionChannel = Channel<ToGetEvents>()
    val eventFlow = transactionChannel.receiveAsFlow()

    // Handling Categories view state so it's displayed correctly, View State for Categories list
    private var _categoriesViewStateLiveData = MutableLiveData<ToGetEvents.CategoriesViewState>()
    val categoriesViewStateLiveData: LiveData<ToGetEvents.CategoriesViewState>
        get() = _categoriesViewStateLiveData

    // Home fragment list view state
    private var _homeViewState = MutableLiveData<ToGetEvents.HomeViewState>()
    val homeViewState: LiveData<ToGetEvents.HomeViewState>
        get() = _homeViewState

    // Used to determine the current sorting order in Home Fragment
    var currentSort = ToGetEvents.HomeViewState.Sort.NONE
        set(value) {
            field = value
            updateHomeViewState(itemWithCategoryLiveData.value!!)
        }

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
                itemRepository.getAllItemWithCategory().collect { items ->
                    _itemWithCategoryLiveData.postValue(items)

                    updateHomeViewState(items)
                }
            }
            launch {
                itemRepository.getAllCategories().collect { categories ->
                    _categoryListLiveData.postValue(categories)
                }
            }
        }
    }

    // region HomeViewState
    // Updating HomeViewState when we collect data from database for ItemWithCategories
    private fun updateHomeViewState(items: List<ItemWithCategoryEntity>) {
        val dataList = ArrayList<ToGetEvents.HomeViewState.DataItem<*>>()

        when (currentSort) {
            ToGetEvents.HomeViewState.Sort.NONE -> {
                var currentPriority = -1
                items.sortedByDescending { it.itemEntity.priority }.forEach { item ->
                    if (item.itemEntity.priority != currentPriority) {
                        currentPriority = item.itemEntity.priority
                        val headerItem = ToGetEvents.HomeViewState.DataItem(
                            data = getHeaderTextForPriority(item.itemEntity.priority),
                            isHeader = true
                        )
                        dataList.add(headerItem)
                    }
                    val dataItem = ToGetEvents.HomeViewState.DataItem(data = item)
                    dataList.add(dataItem)
                }
            }
            ToGetEvents.HomeViewState.Sort.CATEGORY -> {
                var currentCategory = "No id"
                items.sortedBy { it.categoryEntity?.name ?: CategoryEntity.DEFAULT_CATEGORY_ID }
                    .forEach { item ->
                        if (item.itemEntity.categoryId != currentCategory) {
                            currentCategory = item.itemEntity.categoryId
                            val headerItem = ToGetEvents.HomeViewState.DataItem(
                                data = item.categoryEntity?.name
                                    ?: CategoryEntity.DEFAULT_CATEGORY_ID,
                                isHeader = true
                            )
                            dataList.add(headerItem)
                        }
                        val dataItem = ToGetEvents.HomeViewState.DataItem(data = item)
                        dataList.add(dataItem)
                    }
            }
            ToGetEvents.HomeViewState.Sort.NEWEST -> {
                val headerItem = ToGetEvents.HomeViewState.DataItem(
                    data = "Newest",
                    isHeader = true
                )
                dataList.add(headerItem)

                items.sortedByDescending { it.itemEntity.createdAt }.forEach { item ->
                    val dataItem = ToGetEvents.HomeViewState.DataItem(data = item)
                    dataList.add(dataItem)
                }
            }
            ToGetEvents.HomeViewState.Sort.OLDEST -> {
                val headerItem = ToGetEvents.HomeViewState.DataItem(
                    data = "Oldest",
                    isHeader = true
                )
                dataList.add(headerItem)

                items.sortedBy { it.itemEntity.createdAt }.forEach { item ->
                    val dataItem = ToGetEvents.HomeViewState.DataItem(data = item)
                    dataList.add(dataItem)
                }
            }
        }
        // Posting data to Home ViewState after the sort is selected.
        _homeViewState.postValue(
            ToGetEvents.HomeViewState(
                dataList = dataList,
                isLoading = false,
                sort = currentSort
            )
        )
    }

    private fun getHeaderTextForPriority(priority: Int): String {
        return when (priority) {
            1 -> "Low"
            2 -> "Medium"
            else -> "High"
        }
    }
    // endregion HomeViewState

    // region CategoryViewState
    fun onCategorySelected(categoryId: String, showLoading: Boolean = false) {
        if (showLoading) {
            val loadingViewState = ToGetEvents.CategoriesViewState(isLoading = true)
            _categoriesViewStateLiveData.value = loadingViewState
        }

        val categories = _categoryListLiveData.value ?: return
        val viewStateItemList =
            ArrayList<ToGetEvents.CategoriesViewState.Item>()

        // Default category / un-selecting a category
        viewStateItemList.add(
            ToGetEvents.CategoriesViewState.Item(
                categoryEntity = CategoryEntity.getDefaultCategory(),
                isSelected = categoryId == CategoryEntity.DEFAULT_CATEGORY_ID
            )
        )

        categories.forEach {
            viewStateItemList.add(
                ToGetEvents.CategoriesViewState.Item(
                    categoryEntity = it,
                    isSelected = it.id == categoryId
                )
            )
        }
        val viewState = ToGetEvents.CategoriesViewState(itemList = viewStateItemList)
        _categoriesViewStateLiveData.postValue(viewState)
    }
    // endregion CategoryViewState

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

    fun updateItemPriority(itemEntity: ItemEntity) {
        viewModelScope.launch {
            itemRepository.updateItem(itemEntity)
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

    fun reInsertCategory(itemEntity: CategoryEntity) {
        viewModelScope.launch {
            itemRepository.insertCategory(itemEntity)
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
            transactionChannel.send(ToGetEvents.DbTransaction(true))
        }
    }
    // endregion Category Entity

}