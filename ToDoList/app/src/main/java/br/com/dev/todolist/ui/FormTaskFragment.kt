package br.com.dev.todolist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.dev.todolist.R
import br.com.dev.todolist.databinding.FragmentFormTaskBinding
import br.com.dev.todolist.helper.BaseFragment
import br.com.dev.todolist.helper.FirebaseHelper
import br.com.dev.todolist.helper.initToolbar
import br.com.dev.todolist.model.Task

class FormTaskFragment : BaseFragment() {

    private val args: FormTaskFragmentArgs by navArgs()

    private var _binding: FragmentFormTaskBinding? = null
    private val binding get() = _binding!!

    private lateinit var task: Task
    private var newTask: Boolean = true
    private var statusTask: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        initListeners()
        getArgs()
    }

    private fun getArgs() {
        args.task.let {
            if (it != null) {
                task = it
                configTask()
            }
        }
    }

    private fun configTask() {
        newTask = false
        statusTask = task.status
        binding.textToolbarNewTask.text = getString(R.string.text_title_editing_task_form_task)

        binding.editTextTask.setText(task.description)
        setStatus()
    }

    private fun setStatus() {
        binding.radioGroup.check(
            when (task.status) {
                0 -> {
                    R.id.btn_todo
                }
                1 -> {
                    R.id.btn_doing
                }
                else -> {
                    R.id.btn_done
                }
            }
        )
    }

    private fun initListeners() {
        binding.buttonSave.setOnClickListener {
            validateData()
        }
        binding.radioGroup.setOnCheckedChangeListener { _, id ->
            statusTask = when (id) {
                R.id.btn_todo -> 0
                R.id.btn_doing -> 1
                else -> 2
            }
        }
    }

    private fun validateData() {
        val description = binding.editTextTask.text.toString().trim()

        if (description.isNotEmpty()) {
            hideKeyboard()
            binding.progressBar.isVisible = true

            if (newTask) task = Task()
            task.description = description
            task.status = statusTask

            saveTask()
        } else {
            Toast.makeText(
                requireContext(),
                R.string.button_info_description_task_form_task,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun saveTask() {
        FirebaseHelper
            .getDataBase()
            .child("task")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(task.id)
            .setValue(task)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (newTask) { // nova tarefa
                        // voltar para a tela anterior
                        Toast.makeText(
                            requireContext(),
                            R.string.text_task_saved_sucess_form_task,
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().popBackStack()
                    } else { // editando tarefa
                        binding.progressBar.isVisible = false
                        Toast.makeText(
                            requireContext(),
                            R.string.text_task_update_sucess_form_task,
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().popBackStack()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        R.string.text_error_saved_tasks_form_task,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }.addOnFailureListener {
                binding.progressBar.isVisible = false
                Toast.makeText(
                    requireContext(),
                    R.string.text_error_saved_tasks_form_task,
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}