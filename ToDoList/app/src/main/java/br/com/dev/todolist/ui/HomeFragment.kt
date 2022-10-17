package br.com.dev.todolist.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.dev.todolist.R
import br.com.dev.todolist.databinding.FragmentHomeBinding
import br.com.dev.todolist.helper.showBottomSheet
import br.com.dev.todolist.ui.adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        configTablayout()
        initClicks()
    }

    private fun initClicks() {
        binding.ibLogout.setOnClickListener { logoutApp() }
    }

    private fun logoutApp() {
        showBottomSheet(
            titleButton = R.string.text_btn_confirm,
            message = R.string.text_ask_logout,
            onClick = {
                auth.signOut()
                findNavController().navigate(R.id.action_homeFragment_to_authentication)
            }
        )
    }

    private fun configTablayout() {
        val adapter = ViewPagerAdapter(requireActivity())
        binding.viewPager.adapter = adapter

        adapter.addFragment(ToDoFragment(), R.string.to_do)
        adapter.addFragment(DoingFragment(), R.string.doing)
        adapter.addFragment(DoneFragment(), R.string.done)

        binding.viewPager.offscreenPageLimit = adapter.itemCount

        TabLayoutMediator(
            binding.tabLayout, binding.viewPager
        ) { tab, position ->
            tab.text = getString(adapter.getTitle(position))
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}