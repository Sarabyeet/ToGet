package com.sarabyeet.toget.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.sarabyeet.toget.databinding.FragmentHomeBinding
import com.sarabyeet.toget.db.model.ItemEntity
import com.sarabyeet.toget.ui.ItemEntityActions
import com.sarabyeet.toget.ui.fragments.BaseFragment
import kotlin.collections.ArrayList

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
        binding.fab.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAddItemFragment())
        }

        val controller = HomeEpoxyController(this)
        binding.rvHome.setController(controller)

        sharedViewModel.itemListLiveData.observe(viewLifecycleOwner) { itemEntityList ->
            controller.itemEntityList = itemEntityList as ArrayList<ItemEntity>
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDeleteItemEntity(item: ItemEntity) {
        //todo setup
    }

    override fun onBumpPriority(item: ItemEntity) {
        //todo setup
    }
}