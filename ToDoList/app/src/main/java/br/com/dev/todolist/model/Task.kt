package br.com.dev.todolist.model

import android.os.Parcelable
import br.com.dev.todolist.helper.FirebaseHelper
import kotlinx.parcelize.Parcelize

@Parcelize
class Task(
    var id: String = "",
    var description: String = "",
    var status: Int = 0) : Parcelable {
    init {
        this.id = FirebaseHelper.getDataBase().push().key ?: ""
    }

}