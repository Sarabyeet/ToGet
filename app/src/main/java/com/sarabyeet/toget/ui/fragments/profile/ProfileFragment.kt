package com.sarabyeet.toget.ui.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.airbnb.epoxy.EpoxyTouchHelper
import com.google.android.material.snackbar.Snackbar
import com.sarabyeet.toget.databinding.FragmentProfileBinding
import com.sarabyeet.toget.db.model.CategoryEntity
import com.sarabyeet.toget.ui.ProfileScreenActions
import com.sarabyeet.toget.ui.fragments.BaseFragment
import com.sarabyeet.toget.util.UserColorsObject

class ProfileFragment : BaseFragment(), ProfileScreenActions {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setting up the profile epoxy controller while bubbling up on Click event
        val profileController = ProfileEpoxyController(::onEmptyCategoryStateClicked, this)
        binding.epoxyRecyclerView.setController(profileController)

        sharedViewModel.categoryListLiveData.observe(viewLifecycleOwner) { categories ->
            profileController.categories = categories
        }
        swipeToDelete()

        UserColorsObject.userData.apply {
            lifecycleScope.launchWhenCreated {
                getHighPriorityColor().collect {
                    profileController.highPriority = it
                }
            }
            lifecycleScope.launchWhenCreated {
                getMediumPriorityColor().collect {
                    profileController.mediumPriority = it
                }
            }
            lifecycleScope.launchWhenCreated {
                getLowPriorityColor().collect {
                    profileController.lowPriority = it
                }
            }
        }

//        binding.epoxyRecyclerView.postDelayed({
//            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToCustomColorFragment(
//                "High"))
//        }, 1_000)
    }

    private fun swipeToDelete() {
        EpoxyTouchHelper.initSwiping(binding.epoxyRecyclerView)
            .right()
            .withTarget(CategoryEntityEpoxy::class.java)
            .andCallbacks(object :
                EpoxyTouchHelper.SwipeCallbacks<CategoryEntityEpoxy>() {
                override fun onSwipeCompleted(
                    model: CategoryEntityEpoxy?,
                    itemView: View?,
                    position: Int,
                    direction: Int,
                ) {
                    val deletedItem = model?.category ?: return
                    sharedViewModel.deleteCategory(deletedItem)
                    // Making a Snack-bar with undo functionality
                    showSnackBarWithUndo(deletedItem)
                }
            })
    }

    private fun showSnackBarWithUndo(category: CategoryEntity) {
        Snackbar.make(requireView(), "Category has been deleted", Snackbar.LENGTH_SHORT)
            .setAction("Undo") {
                sharedViewModel.reInsertCategory(category)
            }
            .show()
    }

    private fun onEmptyCategoryStateClicked() {
        findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToAddCategoryFragment())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClickCategory(category: CategoryEntity) {
        findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToAddCategoryFragment(
            category.id))
    }

    override fun onDeleteCategory(category: CategoryEntity) {
        sharedViewModel.deleteCategory(category)
    }

    override fun onPrioritySelected(priorityName: String) {
        findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToCustomColorFragment(priorityName))
    }
}