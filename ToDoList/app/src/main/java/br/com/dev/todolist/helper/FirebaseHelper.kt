package br.com.dev.todolist.helper

import br.com.dev.todolist.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class FirebaseHelper {

    companion object {

        fun getDataBase() = FirebaseDatabase.getInstance().reference

        private fun getAuth() = FirebaseAuth.getInstance()

        fun getIdUser() = getAuth().uid

        fun validError(error: String): Int {
            return when {
                error.contains("There is no user record corresponding to this indentifier") -> {
                    R.string.account_not_registered_register
                }
                error.contains("The email address is badly formatted") -> {
                    R.string.invalid_email_register
                }
                error.contains("The password is invalid or the user does not have a password") -> {
                    R.string.invalid_password_register
                }
                error.contains("The email address is already in use by another account") -> {
                    R.string.email_in_use_register
                }
                error.contains("Password should be at least 6 characters") -> {
                    R.string.strong_password_register
                }
                else -> {
                    R.string.error_generic
                }
            }
        }

    }

}