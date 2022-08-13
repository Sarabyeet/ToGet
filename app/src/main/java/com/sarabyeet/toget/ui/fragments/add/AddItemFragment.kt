package com.sarabyeet.toget.ui.fragments.add

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.sarabyeet.toget.R
import com.sarabyeet.toget.arch.ToGetEvents
import com.sarabyeet.toget.databinding.FragmentAddItemBinding
import com.sarabyeet.toget.db.model.CategoryEntity
import com.sarabyeet.toget.db.model.ItemEntity
import com.sarabyeet.toget.db.model.ItemWithCategoryEntity
import com.sarabyeet.toget.util.showKeyboard
import com.sarabyeet.toget.ui.fragments.BaseFragment
import java.util.*


class AddItemFragment : BaseFragment() {
    private var _binding: FragmentAddItemBinding? = null
    private val binding get() = _binding!!

    private val safeArgs: AddItemFragmentArgs by navArgs()

    private val selectedItem: ItemWithCategoryEntity? by lazy {
        sharedViewModel.itemWithCategoryLiveData.value?.find {
            it.itemEntity.id == safeArgs.selectedItemId
        }
    }
    private var isEditMode: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddItemBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Triggering the save action
        binding.saveBtn.setOnClickListener { saveItemToDatabase() }

        // Setting up the seekbar - fetching current title, adding quantity in it [quantity] and updating that text
        // as the seek bar value changes
        binding.quantitySeekbar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val currentText = binding.titleEditText.text.toString().trim()
                if (currentText.isEmpty()) {
                    return // Null title
                }

                val endIndex = currentText.indexOf("[") - 1
                val newText = if (endIndex > 0) {
                    "${currentText.substring(0, endIndex)} [$progress]"
                } else {
                    "$currentText [$progress]"
                }

                val sanitizedText = newText.replace(" [1]", "")
                binding.titleEditText.setText(sanitizedText)
                binding.titleEditText.setSelection(sanitizedText.length)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Nothing to do here
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Nothing to do here
            }
        })


        // Show Item is saved, make a snack-bar, set fields to be empty
        /*sharedViewModel.transactionLiveData.observe(viewLifecycleOwner) { complete ->
            if (complete) {
                /** If the item was updated, just navigate up */
                if (isEditMode) {
                    navigateUp()
                    Snackbar.make(requireView(), "Item updated successfully", Snackbar.LENGTH_SHORT)
                        .show()
                    return@observe
                }

                /** Else set all the fields to null */
                Snackbar.make(requireView(), "Item saved successfully", Snackbar.LENGTH_SHORT)
                    .show()
                binding.titleEditText.text = null
                binding.titleEditText.requestFocus()

                binding.descriptionEditText.text = null
                binding.radioGroup.check(R.id.lowPriorityBtn)
            }
        } */

        lifecycleScope.launchWhenStarted {
            sharedViewModel.eventFlow.collect { event ->
                when (event) {
                    is ToGetEvents.DbTransaction -> {
                        /** If the item was updated, just navigate up */
                        if (isEditMode) {
                            navigateUp()
                            Snackbar.make(requireView(),
                                "Item updated successfully",
                                Snackbar.LENGTH_SHORT)
                                .show()
                            return@collect
                        }

                        /** Else set all the fields to null */
                        Snackbar.make(requireView(),
                            "Item saved successfully",
                            Snackbar.LENGTH_SHORT)
                            .show()
                        binding.titleEditText.text = null
                        binding.titleEditText.requestFocus()

                        binding.descriptionEditText.text = null
                        binding.radioGroup.check(R.id.lowPriorityBtn)
                    }
                }
            }
        }

        // Pops the keyboard open with focus (cursor) on title edit text
        binding.titleEditText.showKeyboard()

        /** Checking the passed in item and populating add item fragment with it's data */
        selectedItem?.let { item ->
            isEditMode = true

            binding.titleEditText.setText(item.itemEntity.title)
            binding.titleEditText.setSelection(item.itemEntity.title.length)
            binding.descriptionEditText.setText(item.itemEntity.description)
            when (item.itemEntity.priority) {
                1 -> binding.radioGroup.check(R.id.lowPriorityBtn)
                2 -> binding.radioGroup.check(R.id.mediumPriorityBtn)
                else -> binding.radioGroup.check(R.id.highPriorityBtn)
            }

            binding.saveBtn.text = "Update"
            mainActivity.supportActionBar?.title = "Update Item"

            val itemTitle = binding.titleEditText.text.toString()
            if (itemTitle.contains("[")) {
                val startIndex =
                    itemTitle.indexOf("[") + 1 // because we want to find what comes after [, ie. [2 - Here two comes after [
                val endIndex = itemTitle.indexOf("]")

                try {
                    val progress = itemTitle.substring(startIndex, endIndex).toInt()
                    binding.quantitySeekbar.progress = progress
                } catch (e: Exception) {
                    Log.d("AddItem", e.stackTraceToString())
                }
            }
        }

        // Setting up the Categories view in Add fragment
        val categoriesViewStateController = CategoriesViewStateController { selectedId ->
            sharedViewModel.onCategorySelected(selectedId)
        }
        binding.rvCategories.setController(categoriesViewStateController)

        sharedViewModel.onCategorySelected(selectedItem?.itemEntity?.categoryId
            ?: CategoryEntity.DEFAULT_CATEGORY_ID, showLoading = true)
        sharedViewModel.categoriesViewStateLiveData.observe(viewLifecycleOwner) { viewState ->
            categoriesViewStateController.viewState = viewState
        }

    }

    /** Saves or updates an item */
    private fun saveItemToDatabase() {
        val itemTitle = binding.titleEditText.text.toString().trim()
        if (itemTitle.isEmpty()) {
            binding.titleTextField.error = "* Required Field"
            return
        }
        binding.titleTextField.error = null

        val itemDescription = binding.descriptionEditText.text.toString().trim()
        val itemPriority = when (binding.radioGroup.checkedRadioButtonId) {
            R.id.lowPriorityBtn -> 1
            R.id.mediumPriorityBtn -> 2
            R.id.highPriorityBtn -> 3
            else -> 0
        }

        val categoryId =
            sharedViewModel.categoriesViewStateLiveData.value?.getCategoryId() ?: return
        // Update and save an item
        if (isEditMode) {
            val itemEntity = selectedItem!!.itemEntity.copy(
                title = itemTitle,
                description = itemDescription,
                priority = itemPriority,
                categoryId = categoryId
            )
            sharedViewModel.updateItem(itemEntity)
            //sharedViewModel.transactionLiveData.postValue(true)
            return
        }

        // Add a new item
        val itemEntity = ItemEntity(
            id = UUID.randomUUID().toString(),
            title = itemTitle,
            description = itemDescription,
            priority = itemPriority,
            createdAt = System.currentTimeMillis(),
            categoryId = categoryId
        )
        sharedViewModel.insertItem(itemEntity)
        //sharedViewModel.transactionLiveData.postValue(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}