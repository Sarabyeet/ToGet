package com.sarabyeet.toget.ui.fragments.home

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.airbnb.epoxy.EpoxyTouchHelper
import com.google.android.material.snackbar.Snackbar
import com.sarabyeet.toget.R
import com.sarabyeet.toget.databinding.FragmentHomeBinding
import com.sarabyeet.toget.db.model.ItemEntity
import com.sarabyeet.toget.ui.ItemEntityActions
import com.sarabyeet.toget.ui.fragments.BaseFragment
import com.sarabyeet.toget.ui.fragments.home.bottomsheet.SortOrderBottomSheet

class HomeFragment : BaseFragment(), ItemEntityActions {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // region Menu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_home_fragment, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menuItemSort -> {
                        SortOrderBottomSheet().show(childFragmentManager, null)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        // endregion Menu

        binding.fab.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAddItemFragment())
        }

        val controller = HomeEpoxyController(this)
        binding.rvHome.setController(controller)

//        sharedViewModel.itemWithCategoryLiveData.observe(viewLifecycleOwner) { itemEntityList ->
//            controller.itemEntityList = itemEntityList as ArrayList<ItemWithCategoryEntity>
//        }

        sharedViewModel.homeViewState.observe(viewLifecycleOwner) { viewState ->
            controller.viewState = viewState
        }

        // Swipe-to-delete Epoxy
        swipeToDelete()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun swipeToDelete() {
        EpoxyTouchHelper.initSwiping(binding.rvHome)
            .right()
            .withTarget(HomeEpoxyController.ItemEntityEpoxyModel::class.java)
            .andCallbacks(object :
                EpoxyTouchHelper.SwipeCallbacks<HomeEpoxyController.ItemEntityEpoxyModel>() {
                override fun onSwipeCompleted(
                    model: HomeEpoxyController.ItemEntityEpoxyModel?,
                    itemView: View?,
                    position: Int,
                    direction: Int,
                ) {
                    val deletedItem = model?.item?.itemEntity ?: return
                    sharedViewModel.deleteItem(deletedItem)
                    // Making a Snack-bar with undo functionality
                    showSnackBarWithUndo(deletedItem)
                }
            })
    }

    private fun showSnackBarWithUndo(item: ItemEntity) {
        Snackbar.make(binding.clayout, "Item has been deleted", Snackbar.LENGTH_SHORT)
            .setAction("Undo") {
                sharedViewModel.reInsertItem(item)
            }
            .show()
    }

    override fun onClickItem(item: ItemEntity) {
        val directions = HomeFragmentDirections.actionHomeFragmentToAddItemFragment(item.id)
        findNavController().navigate(directions)
    }

    override fun onBumpPriority(item: ItemEntity) {
        val currentPriority = item.priority
        var newPriority = currentPriority + 1
        if (newPriority > 3) {
            newPriority = 1
        }
        val updatedPriority = item.copy(priority = newPriority)
        sharedViewModel.updateItemPriority(updatedPriority)
    }
}