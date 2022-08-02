package com.sarabyeet.toget.ui.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.sarabyeet.toget.arch.ToGetEvents
import com.sarabyeet.toget.databinding.FragmentAddCategoryBinding
import com.sarabyeet.toget.db.model.CategoryEntity
import com.sarabyeet.toget.showKeyboard
import com.sarabyeet.toget.ui.fragments.BaseFragment
import java.util.*

class AddCategoryFragment : BaseFragment() {
    private var _binding: FragmentAddCategoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddCategoryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveBtn.setOnClickListener {
            saveCategoryToDatabase()
        }

        binding.categoryEditText.showKeyboard()

        lifecycleScope.launchWhenStarted {
            sharedViewModel.eventFlow.collect { event ->
                when (event) {
                    is ToGetEvents.DbTransaction -> {
                        Snackbar.make(requireView(), "Category saved successfully", Snackbar.LENGTH_SHORT)
                            .show()
                        navigateUp()
                    }
                }
            }
        }
    }

    private fun saveCategoryToDatabase() {
        val categoryName = binding.categoryEditText.text.toString().trim()
        if (categoryName.isEmpty()) {
            binding.categoryTextField.error = "* Required Field"
            return
        }
        val category = CategoryEntity(
            id = UUID.randomUUID().toString(),
            name = categoryName
        )
        sharedViewModel.insertCategory(category)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}