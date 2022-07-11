package com.sarabyeet.toget.ui.fragments.add

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.sarabyeet.toget.R
import com.sarabyeet.toget.databinding.FragmentAddItemBinding
import com.sarabyeet.toget.db.model.ItemEntity
import com.sarabyeet.toget.ui.MainActivity
import com.sarabyeet.toget.ui.fragments.BaseFragment
import java.util.*

class AddItemFragment: BaseFragment() {
    private var _binding: FragmentAddItemBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddItemBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveBtn.setOnClickListener {
            saveItemToDatabase()
        }

        sharedViewModel.transactionLiveData.observe(viewLifecycleOwner){ isSaved ->
            if (isSaved){
                Snackbar.make(requireView(),"Item saved successfully", Snackbar.LENGTH_SHORT).show()
                binding.titleEditText.text = null
                binding.titleEditText.requestFocus()

                binding.descriptionEditText.text = null
                binding.radioGroup.check(R.id.lowPriorityBtn)
            }

            binding.titleEditText.requestFocus()
            binding.titleEditText.showKeyboard()

        }

    }

    override fun onPause() {
        super.onPause()
        sharedViewModel.transactionLiveData.postValue(false)
    }
    private fun EditText.showKeyboard() {
        post {
            requestFocus()
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun saveItemToDatabase() {
        val itemTitle = binding.titleEditText.text.toString().trim()
        if (itemTitle.isEmpty()){
            binding.titleTextField.error = "* Required Field"
            return
        }
        binding.titleTextField.error = null

        val itemDescription = binding.descriptionEditText.text.toString().trim()
        val itemPriority = when (binding.radioGroup.checkedRadioButtonId){
            R.id.lowPriorityBtn -> 1
            R.id.mediumPriorityBtn -> 2
            R.id.highPriorityBtn -> 3
            else -> 0
        }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}