package br.com.dev.todolist.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import br.com.dev.todolist.R
import br.com.dev.todolist.databinding.FragmentLoginBinding
import br.com.dev.todolist.helper.BaseFragment
import br.com.dev.todolist.helper.FirebaseHelper
import br.com.dev.todolist.helper.showBottomSheet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : BaseFragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        initClicks()
    }

    private fun initClicks() {
        binding.buttonLogin.setOnClickListener { validateDate() }

        binding.buttonCreateAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            // registerFragment
        }

        binding.buttonRecoverAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_recoverAccountFragment)
            // recoverAccountFragment
        }
    }

    private fun validateDate() {
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()

        if (email.isNotEmpty()) {
            if (email.isNotEmpty()) {
                hideKeyboard()

                binding.progressBar.isVisible = true
                loginUser(email, password)
            } else {
                showBottomSheet(
                    message = R.string.text_info_password
                )
            }
        } else {
            showBottomSheet(
                message = R.string.text_info_email
            )
        }
    }

    private fun loginUser(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
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