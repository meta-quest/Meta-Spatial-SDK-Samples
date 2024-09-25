// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.meta.theelectricfactory.focus.databinding.FragmentFirstBinding
import java.lang.ref.WeakReference

class FirstFragment : Fragment() {

  private var _binding: FragmentFirstBinding? = null
  private val binding
    get() = _binding!!

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View {
    FirstFragment.instance = WeakReference(this)

    _binding = FragmentFirstBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.buttonNewProject.setOnClickListener {
      ImmersiveActivity.instance.get()?.newProject()
      findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
    }

    // Projects are added into the scroll view of the Home Panel
    addProjects()
  }

  public fun isActive(): Boolean {
    return _binding != null
  }

  public fun moveToSecondFragment() {
    findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
  }

  fun refreshProjects() {
    binding.projectsTable.removeAllViews()
    addProjects()
  }

  @SuppressLint("Range")
  private fun addProjects() {

    val cursor = ImmersiveActivity.instance.get()?.DB?.getProjects()
    var index = 0
    var tableRow = TableRow(this.context)

    // We create a card with info of each project in the database and place it on the Home Panel
    // scroll view
    if (cursor?.moveToFirst() == true) {
      while (!cursor.isAfterLast) {
        val uuid = cursor.getInt(cursor.getColumnIndex(DatabaseManager.PROJECT_UUID))
        val projectName = cursor.getString(cursor.getColumnIndex(DatabaseManager.PROJECT_NAME))
        val lastOpening =
            cursor.getString(cursor.getColumnIndex(DatabaseManager.PROJECT_LAST_OPENING)).toLong()

        if (index % 4 == 0) {
          tableRow = TableRow(this.context)
          binding.projectsTable.addView(tableRow)
        }

        // We use the project_layout template for each "project card"
        val customItem: View =
            LayoutInflater.from(this.context).inflate(R.layout.project_layout, tableRow, false)

        val projectButton = customItem.findViewById<ImageButton>(R.id.projectButton)
        projectButton.id = uuid
        projectButton.setOnClickListener { ImmersiveActivity.instance.get()?.loadProject(uuid) }

        // Show info of the last time the user opened the project
        val projectTextName = customItem.findViewById<TextView>(R.id.projectName)
        projectTextName.text = projectName

        val timePassed = System.currentTimeMillis() - lastOpening

        val seconds = timePassed / 1000
        val minutes = timePassed / (1000 * 60)
        val hours = timePassed / (1000 * 60 * 60)
        val days = timePassed / (1000 * 60 * 60 * 24)

        var timePassedString = ""

        when {
          days > 0 -> timePassedString = "Edited $days days ago"
          hours > 0 -> timePassedString = "Edited $hours hours ago"
          minutes > 0 -> timePassedString = "Edited $minutes minutes ago"
          else -> timePassedString = "Edited $seconds seconds ago"
        }

        val projectLastOpening = customItem.findViewById<TextView>(R.id.lastOpening)
        projectLastOpening.text = timePassedString

        val deleteButton = customItem.findViewById<ImageButton>(R.id.deleteProjectButton)
        deleteButton.setOnClickListener {
          ImmersiveActivity.instance
              .get()
              ?.scene
              ?.playSound(ImmersiveActivity.instance.get()?.deleteSound!!, 1f)
          ImmersiveActivity.instance.get()?.DB?.deleteProject(uuid)
          refreshProjects()
        }

        tableRow.addView(customItem)

        index += 1
        cursor.moveToNext()
      }
    }
    cursor?.close()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  companion object {
    lateinit public var instance: WeakReference<FirstFragment>
  }
}
