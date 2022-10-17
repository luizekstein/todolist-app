package br.com.dev.todolist.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import br.com.dev.todolist.R
import br.com.dev.todolist.databinding.FragmentRegisterBinding
import br.com.dev.todolist.helper.BaseFragment
import br.com.dev.todolist.helper.FirebaseHelper
import br.com.dev.todolist.helper.initToolbar
import br.com.dev.todolist.helper.showBottomSheet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterFragment : BaseFragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        auth = Firebase.auth
        initClicks()
    }

    private fun initClicks() {
        binding.buttonCreateAccount.setOnClickListener { validateDate() }
    }

    private fun validateDate() {
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()

        if (email.isNotEmpty()) {
            if (email.isNotEmpty()) {
                hideKeyboard()

                binding.progressBar.isVisible = true
                registerUser(email, password)
            } else {
                showBottomSheet(message = R.string.text_info_password)
            }
        } else {
            showBottomSheet(message = R.string.text_info_email)
        }
    }

    private fun registerUser(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        findNavController().navigate(R.id.action_global_homeFragment)
                    } else {
                        showBottomSheet(
                            message = FirebaseHelper.validError(task.exception?.message ?: "")
                        )
                        binding.progressBar.isVisible = false
                    }
                }
        } else {
            showBottomSheet(
                message = R.string.text_invalid_email_or_password,
                onClick = {
                    binding.editTextEmail.text.clear()
                }
            )
            binding.progressBar.isVisible = false
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}