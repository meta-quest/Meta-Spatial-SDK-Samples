// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.lang.ref.WeakReference
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.platform.ComposeView

class FirstFragment : Fragment() {

    private val _projects = mutableStateListOf<ProjectData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        instance = WeakReference(this)

        return ComposeView(requireContext()).apply {
            setContent {
                HomePanelFirstFragmentScreen(_projects)
            }
        }
    }

    companion object {
        lateinit var instance: WeakReference<FirstFragment>
    }

    fun moveToSecondFragment() {
        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
    }

    fun isCurrentlyVisible(): Boolean {
        return try {
            val navController = instance.get()?.findNavController()
            navController?.currentDestination?.id == R.id.FirstFragment
        } catch (e: Exception) {
            false
        }
    }

    fun refreshProjects() {
        _projects.clear()
        _projects.addAll(getProjectsFromDB())
    }
}
