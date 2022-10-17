package br.com.dev.todolist.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import br.com.dev.todolist.R
import br.com.dev.todolist.databinding.ItemAdapterBinding
import br.com.dev.todolist.model.Task

class TaskAdapter(
    private val context: Context,
    private val taskList: List<Task>,
    val taskSelected: (Task, Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.MyViewHolder>() {

    companion object {
        val SELECT_BACK: Int = 1
        val SELECT_REMOVE: Int = 2
        val SELECT_EDIT: Int = 3
        val SELECT_DETAIL: Int = 4
        val SELECT_NEXT: Int = 5
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemAdapterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val task = taskList[position]

        holder.binding.textDescriptionTask.text = task.description

        holder.binding.btnRemoveTask.setOnClickListener { taskSelected(task, SELECT_REMOVE) }
        holder.binding.btnEditTask.setOnClickListener { taskSelected(task, SELECT_EDIT) }
        holder.binding.btnDetailTask.setOnClickListener { taskSelected(task, SELECT_DETAIL) }

        when (task.status) {
            0 -> {
                holder.binding.btnBack.isVisible = false

                holder.binding.btnNext.setColorFilter(
                    ContextCompat.getColor(context, R.color.color_doing)
                )

                holder.binding.btnNext.setOnClickListener { taskSelected(task, SELECT_NEXT) }
            }
            1 -> {
                holder.binding.btnBack.setColorFilter(
                    ContextCompat.getColor(context, R.color.color_todo)
                )

                holder.binding.btnNext.setColorFilter(
                    ContextCompat.getColor(context, R.color.color_done)
                )

                holder.binding.btnBack.setOnClickListener { taskSelected(task, SELECT_BACK) }
                holder.binding.btnNext.setOnClickListener { taskSelected(task, SELECT_NEXT) }
            }
            else -> {
                holder.binding.btnNext.isVisible = false

                holder.binding.btnBack.setColorFilter(
                    ContextCompat.getColor(context, R.color.color_doing)
                )

                holder.binding.btnBack.setOnClickListener { taskSelected(task, SELECT_BACK) }
            }
        }

    }

    override fun getItemCount() = taskList.size

    inner class MyViewHolder(val binding: ItemAdapterBinding) :
        RecyclerView.ViewHolder(binding.root)

}