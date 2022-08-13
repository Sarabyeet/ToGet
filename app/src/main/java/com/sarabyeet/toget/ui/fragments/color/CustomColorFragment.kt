package com.sarabyeet.toget.ui.fragments.color

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.sarabyeet.toget.arch.CustomColorViewModel
import com.sarabyeet.toget.databinding.FragmentCustomColorBinding
import com.sarabyeet.toget.ui.fragments.BaseFragment
import com.sarabyeet.toget.util.UserColorsObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class CustomColorFragment : BaseFragment() {
    private var _binding: FragmentCustomColorBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CustomColorViewModel by viewModels()
    private val safeArgs: CustomColorFragmentArgs by navArgs()


    private class SeekBarChangeListener(
        val onProgressChange: (Int) -> Unit,
    ) : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            onProgressChange(progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            /** Nothing to do */
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            /** Nothing to do */
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

        // Loading Settings
        when (safeArgs.priorityName.lowercase(Locale.US)) {
            "low" -> lifecycleScope.launchWhenCreated {
                UserColorsObject.userData.getLowPriorityColor().collect {
                    viewModel.setPriorityName(safeArgs.priorityName, it) { red, green, blue ->
                        binding.apply {
                            redColorLayout.seekBar.progress = red
                            greenColorLayout.seekBar.progress = green
                            blueColorLayout.seekBar.progress = blue
                        }
                    }
                }
            }
            "medium" -> lifecycleScope.launchWhenCreated {
                UserColorsObject.userData.getMediumPriorityColor().collect {
                    viewModel.setPriorityName(safeArgs.priorityName, it) { red, green, blue ->
                        binding.apply {
                            redColorLayout.seekBar.progress = red
                            greenColorLayout.seekBar.progress = green
                            blueColorLayout.seekBar.progress = blue
                        }
                    }
                }
            }
            "high" -> lifecycleScope.launchWhenCreated {
                UserColorsObject.userData.getHighPriorityColor().collect {
                    viewModel.setPriorityName(safeArgs.priorityName, it) { red, green, blue ->
                        binding.apply {
                            redColorLayout.seekBar.progress = red
                            greenColorLayout.seekBar.progress = green
                            blueColorLayout.seekBar.progress = blue
                        }
                    }
                }
            }
        }

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

        viewModel.viewStateLiveData.observe(viewLifecycleOwner) {
            binding.titleTextView.text = it.getFormattedTitle()
            val color = Color.rgb(it.red, it.green, it.blue)
            binding.colorView.setBackgroundColor(color)
        }

        // Save to DataStore
        binding.saveButton.setOnClickListener {
            val viewState = viewModel.viewStateLiveData.value ?: return@setOnClickListener
            val color = Color.rgb(viewState.red, viewState.green, viewState.blue)
            when (safeArgs.priorityName.lowercase(Locale.US)) {
                "low" -> lifecycleScope.launchWhenCreated {
                    UserColorsObject.userData.setLowPriorityColor(color)
                }
                "medium" -> lifecycleScope.launchWhenCreated {
                    UserColorsObject.userData.setMediumPriorityColor(color)
                }
                "high" -> lifecycleScope.launchWhenCreated {
                    UserColorsObject.userData.setHighPriorityColor(color)
                }
            }
            Snackbar.make(requireView(),"Saved successfully!", Snackbar.LENGTH_SHORT).show()
            navigateUp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}