package com.sarabyeet.toget.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.sarabyeet.toget.arch.ToGetViewModel
import com.sarabyeet.toget.ui.MainActivity

abstract class BaseFragment: Fragment() {

    protected val mainActivity
        get() = (activity as MainActivity)

    protected val sharedViewModel: ToGetViewModel by activityViewModels()

    // region Navigation helper methods
    protected fun navigateUp() {
        mainActivity.navController.navigateUp()
    }

}