package com.sarabyeet.toget.ui.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.sarabyeet.toget.arch.ToGetEvents
import com.sarabyeet.toget.databinding.FragmentAddCategoryBinding
import com.sarabyeet.toget.db.model.CategoryEntity
import com.sarabyeet.toget.util.showKeyboard
import com.sarabyeet.toget.ui.fragments.BaseFragment
import java.util.*

class AddCategoryFragment : BaseFragment() {
    private var _binding: FragmentAddCategoryBinding? = null
    private val binding get() = _binding!!

    private val safeArgs: AddCategoryFragmentArgs by navArgs()

    private val selectedCategory: CategoryEntity? by lazy {
        sharedViewModel.categoryListLiveData.value?.find {
            it.id == safeArgs.selectedCategoryId
        }
    }
    private var isEditMode: Boolean = false

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

        selectedCategory?.let { category ->
            isEditMode = true
            binding.categoryEditText.setText(category.name)
            binding.categoryEditText.setSelection(category.name.length)
            binding.saveBtn.text = "Update"
            mainActivity.supportActionBar?.title = "Update Category"
        }

        lifecycleScope.launchWhenStarted {
            sharedViewModel.eventFlow.collect { event ->
                when (event) {
                    is ToGetEvents.DbTransaction -> {
                        if (isEditMode) {
                            navigateUp()
                            Snackbar.make(requireView(),
                                "Category updated successfully",
                                Snackbar.LENGTH_SHORT)
                                .show()
                            return@collect
                        }
                        Snackbar.make(requireView(),
                            "Category saved successfully",
                            Snackbar.LENGTH_SHORT)
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

        binding.categoryTextField.error = null

        val category = CategoryEntity(
            id = UUID.randomUUID().toString(),
            name = categoryName
        )

        if (isEditMode) {
            val categoryEntity = selectedCategory!!.copy(
                name = categoryName
            )
            sharedViewModel.updateCategory(categoryEntity)
            return
        }
        sharedViewModel.insertCategory(category)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}