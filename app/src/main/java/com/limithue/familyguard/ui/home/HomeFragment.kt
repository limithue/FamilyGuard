package com.limithue.familyguard.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.limithue.familyguard.R
import com.limithue.familyguard.data.model.Alert
import com.limithue.familyguard.data.repository.AppRepository
import com.limithue.familyguard.databinding.FragmentHomeBinding
import com.limithue.familyguard.ui.MemberAdapter
import com.limithue.familyguard.ui.NavActionHandler
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), NavActionHandler {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: AppRepository
    private lateinit var memberAdapter: MemberAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = AppRepository.getInstance(requireContext())

        setupFeatureCards()
        setupMembers()
        observeMembers()

        binding.btnSos.setOnClickListener { sendSos() }
    }

    private fun setupFeatureCards() {
        val features = listOf(
            R.id.feature_fence to Pair(
                getString(R.string.feature_fence), getString(R.string.feature_fence_desc)
            ),
            R.id.feature_track to Pair(
                getString(R.string.feature_track), getString(R.string.feature_track_desc)
            ),
            R.id.feature_sos to Pair(
                getString(R.string.feature_sos), getString(R.string.feature_sos_desc)
            ),
            R.id.feature_health to Pair(
                getString(R.string.feature_health), getString(R.string.feature_health_desc)
            )
        )
        features.forEach { (id, pair) ->
            val card = requireView().findViewById<View>(id)
            card.findViewById<android.widget.TextView>(R.id.tv_feature_name).text = pair.first
            card.findViewById<android.widget.TextView>(R.id.tv_feature_desc).text = pair.second
        }
    }

    private fun setupMembers() {
        memberAdapter = MemberAdapter()
        binding.recyclerHomeMembers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = memberAdapter
        }
    }

    private fun observeMembers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repository.members.collectLatest { list ->
                memberAdapter.submitList(list.take(3))
            }
        }
    }

    override fun onNavAction() {
        sendSos()
    }

    private fun sendSos() {
        viewLifecycleOwner.lifecycleScope.launch {
            repository.addAlert(
                Alert(
                    type = Alert.TYPE_SOS,
                    level = Alert.LEVEL_URGENT,
                    memberName = "我",
                    content = "紧急求助：当前位置已发送给所有家庭成员"
                )
            )
            Toast.makeText(requireContext(), R.string.sos_sent, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
