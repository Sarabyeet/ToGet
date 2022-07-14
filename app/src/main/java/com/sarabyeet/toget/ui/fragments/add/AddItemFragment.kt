package com.sarabyeet.toget.ui.fragments.add

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.sarabyeet.toget.R
import com.sarabyeet.toget.databinding.FragmentAddItemBinding
import com.sarabyeet.toget.db.model.ItemEntity
import com.sarabyeet.toget.ui.fragments.BaseFragment
import java.util.*


class AddItemFragment : BaseFragment() {
    private var _binding: FragmentAddItemBinding? = null
    private val binding get() = _binding!!

    private val safeArgs: AddItemFragmentArgs by navArgs()

    private val selectedItem: ItemEntity? by lazy {
        sharedViewModel.itemListLiveData.value?.find {
            it.id == safeArgs.selectedItemId
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

        binding.saveBtn.setOnClickListener {
            saveItemToDatabase()
        }

        // Show Item is saved, make a snack-bar, set fields to be empty
        sharedViewModel.transactionLiveData.observe(viewLifecycleOwner) { complete ->
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
        }

        // Pops the keyboard open with focus (cursor) on title edit text
        binding.titleEditText.showKeyboard()

        /** Checking the passed in item and populating add item fragment with it's data */
        selectedItem?.let { itemEntity ->
            isEditMode = true

            binding.titleEditText.setText(itemEntity.title)
            binding.descriptionEditText.setText(itemEntity.description)
            when (itemEntity.priority) {
                1 -> binding.radioGroup.check(R.id.lowPriorityBtn)
                2 -> binding.radioGroup.check(R.id.mediumPriorityBtn)
                else -> binding.radioGroup.check(R.id.highPriorityBtn)
            }

            binding.saveBtn.text = "Update"
            mainActivity.supportActionBar?.title = "Update Item"
        }

    }


    private fun EditText.showKeyboard() {
        post {
            requestFocus()
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
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

        // Update and save an item
        if (isEditMode) {
            val itemEntity = selectedItem?.copy(
                title = itemTitle,
                description = itemDescription,
                priority = itemPriority,
            )

            if (itemEntity != null) {
                sharedViewModel.updateItem(itemEntity)
                sharedViewModel.transactionLiveData.postValue(true)
            }
            return
        }

        // Add a new item
        val itemEntity = ItemEntity(
            id = UUID.randomUUID().toString(),
            title = itemTitle,
            description = itemDescription,
            priority = itemPriority,
            createdAt = System.currentTimeMillis(),
            categoryId = "" // todo implement later
        )
        sharedViewModel.insertItem(itemEntity)
        sharedViewModel.transactionLiveData.postValue(true)

    }

    override fun onPause() {
        sharedViewModel.transactionLiveData.postValue(false)
        super.onPause()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}