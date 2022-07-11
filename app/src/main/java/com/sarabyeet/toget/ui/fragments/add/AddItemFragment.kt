package com.sarabyeet.toget.ui.fragments.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.sarabyeet.toget.R
import com.sarabyeet.toget.databinding.FragmentAddItemBinding
import com.sarabyeet.toget.db.model.ItemEntity
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
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}