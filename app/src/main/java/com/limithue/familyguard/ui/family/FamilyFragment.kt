package com.limithue.familyguard.ui.family

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.limithue.familyguard.data.model.FamilyMember
import com.limithue.familyguard.data.repository.AppRepository
import com.limithue.familyguard.databinding.FragmentFamilyBinding
import com.limithue.familyguard.ui.MemberAdapter
import com.limithue.familyguard.ui.NavActionHandler
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FamilyFragment : Fragment(), NavActionHandler {

    private var _binding: FragmentFamilyBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: AppRepository
    private lateinit var adapter: MemberAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFamilyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = AppRepository.getInstance(requireContext())

        adapter = MemberAdapter { member ->
            Toast.makeText(requireContext(), "查看 ${member.name} 的位置", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerFamily.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FamilyFragment.adapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repository.members.collectLatest { adapter.submitList(it) }
        }

    }

    override fun onNavAction() {
        addDemoMember()
    }

    private fun addDemoMember() {
        viewLifecycleOwner.lifecycleScope.launch {
            val count = repository.countMembers() + 1
            repository.addMember(
                FamilyMember(
                    name = "新成员$count",
                    role = FamilyMember.ROLE_MEMBER,
                    avatarText = "新",
                    isOnline = true,
                    battery = 90
                )
            )
            Toast.makeText(requireContext(), "成员已添加", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
