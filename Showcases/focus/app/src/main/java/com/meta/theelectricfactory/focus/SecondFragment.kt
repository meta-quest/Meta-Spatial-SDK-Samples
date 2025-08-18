// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.meta.spatial.toolkit.Visible
import com.meta.theelectricfactory.focus.databinding.FragmentSecondBinding
import java.lang.ref.WeakReference

class SecondFragment : Fragment() {

  private var _binding: FragmentSecondBinding? = null
  private val binding
    get() = _binding!!

  private var envSelected = 3

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?,
  ): View {
    SecondFragment.instance = WeakReference(this)

    _binding = FragmentSecondBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.buttonClose.setOnClickListener {
      // If we don't have a current project open, we return Home Panel first view.
      if (ImmersiveActivity.instance.get()?.currentProject == null) {
        binding.projectInputText.setText("")
        ImmersiveActivity.instance.get()?.newProject()
        findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)

        // If it's an old project, we update project info each time the user closes or changes a
        // property
      } else {
        saveCurrentProject()
      }
    }

    // We select between the possible environments and save projects settings
    binding.buttonEnvironment1.setOnClickListener {
      ImmersiveActivity.instance.get()?.selectEnvironment(0)
      if (ImmersiveActivity.instance.get()?.currentProject != null) {
        ImmersiveActivity.instance
            .get()
            ?.saveProjectSettings(false, binding.projectInputText.text.toString())
      }
      selectEnv(0)
    }

    binding.buttonEnvironment2.setOnClickListener {
      ImmersiveActivity.instance.get()?.selectEnvironment(1)
      if (ImmersiveActivity.instance.get()?.currentProject != null) {
        ImmersiveActivity.instance
            .get()
            ?.saveProjectSettings(false, binding.projectInputText.text.toString())
      }
      selectEnv(1)
    }

    binding.buttonEnvironment3.setOnClickListener {
      ImmersiveActivity.instance.get()?.selectEnvironment(2)
      if (ImmersiveActivity.instance.get()?.currentProject != null) {
        ImmersiveActivity.instance
            .get()
            ?.saveProjectSettings(false, binding.projectInputText.text.toString())
      }
      selectEnv(2)
    }

    binding.buttonEnvironment4.setOnClickListener {
      ImmersiveActivity.instance.get()?.selectMRMode()
      if (ImmersiveActivity.instance.get()?.currentProject != null) {
        ImmersiveActivity.instance
            .get()
            ?.saveProjectSettings(true, binding.projectInputText.text.toString())
      }
      selectEnv(3)
    }

    binding.buttonCreateProject.setOnClickListener { saveCurrentProject() }

    if (ImmersiveActivity.instance.get()?.currentProject != null) {
      binding.buttonCreateProject.text = "Save project"
      binding.projectInputText.setText(ImmersiveActivity.instance.get()?.currentProject?.name)

      if (ImmersiveActivity.instance.get()?.currentProject?.MR == false) {
        selectEnv(ImmersiveActivity.instance.get()?.currentProject?.environment!!)
      } else {
        selectEnv(3)
      }
    } else {
      binding.buttonCreateProject.text = "Create project"
      selectEnv(3)
    }
  }

  // Show a border for selected environment image
  fun selectEnv(env: Int) {
    envSelected = env
    binding.borderEnvironment1.setBackgroundResource(R.drawable.transparent)
    binding.borderEnvironment2.setBackgroundResource(R.drawable.transparent)
    binding.borderEnvironment3.setBackgroundResource(R.drawable.transparent)
    binding.borderEnvironment4.setBackgroundResource(R.drawable.transparent)

    when (env) {
      0 -> {
        binding.borderEnvironment1.setBackgroundResource(R.drawable.selected_env_border)
      }
      1 -> {
        binding.borderEnvironment2.setBackgroundResource(R.drawable.selected_env_border)
      }
      2 -> {
        binding.borderEnvironment3.setBackgroundResource(R.drawable.selected_env_border)
      }
      3 -> {
        binding.borderEnvironment4.setBackgroundResource(R.drawable.selected_env_border)
      }
      else -> {
        //
      }
    }
  }

  fun saveCurrentProject() {
    var projectName = binding.projectInputText.text.toString()
    if (projectName == "") {
      projectName = "Untitled"
      binding.projectInputText.setText(projectName)
    }
    ImmersiveActivity.instance.get()?.saveProjectSettings(envSelected == 3, projectName)
    ImmersiveActivity.instance.get()?.homePanel?.setComponent(Visible(false))
  }

  public fun isActive(): Boolean {
    return _binding != null
  }

  public fun moveToFirstFragment() {
    findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  companion object {
    lateinit public var instance: WeakReference<SecondFragment>
  }
}
