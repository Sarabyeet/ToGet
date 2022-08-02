package com.sarabyeet.toget.ui.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.sarabyeet.toget.databinding.FragmentProfileBinding

import com.sarabyeet.toget.ui.fragments.BaseFragment

class ProfileFragment: BaseFragment() {
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
        val profileController = ProfileEpoxyController(::onEmptyCategoryStateClicked)
        binding.epoxyRecyclerView.setController(profileController)

        sharedViewModel.categoryListLiveData.observe(viewLifecycleOwner){ categories ->
            profileController.categories = categories
        }
    }

    private fun onEmptyCategoryStateClicked() {
        findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToAddCategoryFragment())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}