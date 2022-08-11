package com.sarabyeet.toget.ui.fragments.home.bottomsheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sarabyeet.toget.arch.ToGetEvents
import com.sarabyeet.toget.arch.ToGetViewModel
import com.sarabyeet.toget.databinding.FragmentSortOrderBottomSheetBinding

class SortOrderBottomSheet: BottomSheetDialogFragment() {

    private var _binding: FragmentSortOrderBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ToGetViewModel by activityViewModels()

    companion object {
        const val TAG = "ModalBottomSheet"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSortOrderBottomSheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val controller = BottomSheetEpoxyController(ToGetEvents.HomeViewState.Sort.values(), viewModel.currentSort){
            Log.d("BOTTOM",ToGetEvents.HomeViewState.Sort.values().toString() )
            viewModel.currentSort = it
            dismiss()
        }
        binding.rvSortOrder.setControllerAndBuildModels(controller)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}