// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.lang.ref.WeakReference

class SecondFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        instance = WeakReference(this)

        return ComposeView(requireContext()).apply {
            setContent {
                HomePanelSecondFragmentScreen()
            }
        }
    }

    fun isCurrentlyVisible(): Boolean {
        return try {
            val navController = SecondFragment.instance.get()?.findNavController()
            navController?.currentDestination?.id == R.id.SecondFragment
        } catch (e: Exception) {
            false
        }
    }

    fun moveToFirstFragment() {
        findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
    }

    companion object {
        lateinit var instance: WeakReference<SecondFragment>
    }
}