package com.sarabyeet.toget.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.sarabyeet.toget.arch.ToGetViewModel
import com.sarabyeet.toget.db.AppDatabase
import com.sarabyeet.toget.ui.MainActivity

abstract class BaseFragment: Fragment() {

    protected val mainActivity
        get() = (activity as MainActivity)

    protected val appDatabase: AppDatabase
        get() = AppDatabase.getDatabase(requireActivity())

    protected val sharedViewModel: ToGetViewModel by activityViewModels()
}