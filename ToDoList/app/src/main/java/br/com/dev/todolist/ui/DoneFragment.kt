package br.com.dev.todolist.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.dev.todolist.R
import br.com.dev.todolist.databinding.FragmentDoneBinding
import br.com.dev.todolist.helper.BaseFragment
import br.com.dev.todolist.helper.FirebaseHelper
import br.com.dev.todolist.helper.showBottomSheet
import br.com.dev.todolist.model.Task
import br.com.dev.todolist.ui.adapter.TaskAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class DoneFragment : BaseFragment() {

    private var _binding: FragmentDoneBinding? = null
    private val binding get() = _binding!!

    private lateinit var taskAdapter: TaskAdapter

    private val taskList = mutableListOf<Task>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClicks()
        getTasks()
    }

    private fun initClicks() {
        binding.buttonFloatAction.setOnClickListener {
            val action = HomeFragmentDirections
                .actionHomeFragmentToFormTaskFragment(null)
            findNavController().navigate(action)
        }
    }

    private fun getTasks() {
        FirebaseHelper
            .getDataBase()
            .child("task")
            .child(FirebaseHelper.getIdUser() ?: "")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //se eu tenho registros a serem recuperados
                    if (snapshot.exists()) {
                        taskList.clear()
                        for (snap in snapshot.children) {
                            val task = snap.getValue(Task::class.java) as Task

                            if (task.status == 2) taskList.add(task)
                        }

                        taskList.reverse()
                        initAdapter()
                    }

                    tasksEmpty()
                    binding.progressBar.isVisible = false
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        requireContext(),
                        R.string.text_error_get_tasks_done,
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
    }

    private fun tasksEmpty() {
        binding.textInfo.text = if (taskList.isEmpty()) {
            getText(R.string.text_task_list_empty_done_fragment)
        } else {
            ""
        }
    }

    private fun initAdapter() {
        binding.recyclerViewDone.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewDone.setHasFixedSize(true)
        taskAdapter = TaskAdapter(requireContext(), taskList) { task, select ->
            optionSelect(task, select)
        }
        binding.recyclerViewDone.adapter = taskAdapter
    }

    private fun optionSelect(task: Task, select: Int) {
        when (select) {
            TaskAdapter.SELECT_REMOVE -> {
                deleteTask(task)
                taskList.clear()
            }
            TaskAdapter.SELECT_EDIT -> {
                val action = HomeFragmentDirections
                    .actionHomeFragmentToFormTaskFragment(task)
                findNavController().navigate(action)
            }
            TaskAdapter.SELECT_DETAIL -> {
                showBottomSheet(
                    message = R.string.text_function_developer,
                )
            }
            TaskAdapter.SELECT_BACK -> {
                task.status = 1
                updateTask(task)
            }
        }
    }

    private fun updateTask(task: Task) {
        FirebaseHelper
            .getDataBase()
            .child("task")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(task.id)
            .setValue(task)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        R.string.text_task_update_sucess_done,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    showBottomSheet(message = R.string.error_generic)
                }
            }.addOnFailureListener {
                binding.progressBar.isVisible = false
                showBottomSheet(message = R.string.error_generic)
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun deleteTask(task: Task) {
        showBottomSheet(
            titleButton = R.string.text_btn_confirm,
            message = R.string.text_message_delete_task_done,
            onClick = {
                FirebaseHelper
                    .getDataBase()
                    .child("task")
                    .child(FirebaseHelper.getIdUser() ?: "")
                    .child(task.id)
                    .removeValue()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                R.string.text_task_update_sucess_done,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            showBottomSheet(message = R.string.error_generic)
                        }
                    }.addOnFailureListener {
                        binding.progressBar.isVisible = false
                        showBottomSheet(message = R.string.error_generic)
                    }
                taskList.remove(task)
                taskAdapter.notifyDataSetChanged()

                Toast.makeText(
                    requireContext(),
                    R.string.text_task_delete_sucess_done,
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}