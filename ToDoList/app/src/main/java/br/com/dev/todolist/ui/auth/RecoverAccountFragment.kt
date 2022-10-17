package br.com.dev.todolist.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import br.com.dev.todolist.R
import br.com.dev.todolist.databinding.FragmentRecoverAccountBinding
import br.com.dev.todolist.helper.BaseFragment
import br.com.dev.todolist.helper.FirebaseHelper
import br.com.dev.todolist.helper.initToolbar
import br.com.dev.todolist.helper.showBottomSheet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RecoverAccountFragment : BaseFragment() {

    private var _binding: FragmentRecoverAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecoverAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        auth = Firebase.auth
        initClicks()
    }

    private fun initClicks() {
        binding.btnSend.setOnClickListener { validateDate() }
    }

    private fun validateDate() {
        val email = binding.editTextEmail.text.toString().trim()

        if (email.isNotEmpty()) {
            hideKeyboard()

            binding.progressBar.isVisible = true
            recoverAccountUser(email)
        } else {
            showBottomSheet(
                message = R.string.text_info_email
            )
        }
    }

    private fun recoverAccountUser(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    showBottomSheet(
                        message = R.string.text_send_email_password,
                        onClick = {
                            auth.signOut()
                            findNavController().navigate(R.id.loginFragment)
                        }
                    )
                } else {
                    showBottomSheet(
                        message = FirebaseHelper.validError(task.exception?.message ?: "")
                    )
                }
                binding.progressBar.isVisible = false
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}