package com.sarabyeet.toget.ui.fragments.color

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.sarabyeet.toget.arch.CustomColorViewModel
import com.sarabyeet.toget.databinding.FragmentCustomColorBinding
import com.sarabyeet.toget.ui.fragments.BaseFragment

class CustomColorFragment : BaseFragment() {
    private var _binding: FragmentCustomColorBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CustomColorViewModel by viewModels()
    private val safeArgs: CustomColorFragmentArgs by navArgs()

    private class SeekBarChangeListener(
        val onProgressChange: (Int) -> Unit
    ) : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            onProgressChange(progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            // Nothing to do
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            // Nothing to do
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCustomColorBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setPriorityName(safeArgs.priorityName)

        binding.redColorLayout.apply {
            colorTextView.text = "Red"
            seekBar.setOnSeekBarChangeListener(SeekBarChangeListener(viewModel::onRedChange))
        }
        binding.greenColorLayout.apply {
            colorTextView.text = "Green"
            seekBar.setOnSeekBarChangeListener(SeekBarChangeListener(viewModel::onGreenChange))
        }
        binding.blueColorLayout.apply {
            colorTextView.text = "Blue"
            seekBar.setOnSeekBarChangeListener(SeekBarChangeListener(viewModel::onBlueChange))
        }

        viewModel.viewStateLiveData.observe(viewLifecycleOwner){
            binding.titleTextView.text = it.getFormattedTitle()
            val color = Color.rgb(it.red, it.green, it.blue)
            binding.colorView.setBackgroundColor(color)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}