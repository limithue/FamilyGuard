package com.limithue.familyguard.ui.alert

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.limithue.familyguard.data.model.Alert
import com.limithue.familyguard.data.repository.AppRepository
import com.limithue.familyguard.databinding.FragmentAlertBinding
import com.limithue.familyguard.ui.AlertAdapter
import com.limithue.familyguard.ui.NavActionHandler
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AlertFragment : Fragment(), NavActionHandler {

    private var _binding: FragmentAlertBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: AppRepository
    private lateinit var adapter: AlertAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = AppRepository.getInstance(requireContext())

        adapter = AlertAdapter { alert ->
            viewLifecycleOwner.lifecycleScope.launch {
                repository.markAlertHandled(alert.id)
            }
        }
        binding.recyclerAlert.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AlertFragment.adapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repository.alerts.collectLatest { list ->
                adapter.submitList(list)
                binding.tvAlertEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            }
        }

    }

    override fun onNavAction() {
        addDemoAlert()
    }

    private fun addDemoAlert() {
        viewLifecycleOwner.lifecycleScope.launch {
            repository.addAlert(
                Alert(
                    type = Alert.TYPE_FENCE,
                    level = Alert.LEVEL_IMPORTANT,
                    memberName = "母亲",
                    content = "母亲离开了设定的安全区域（家附近 500 米）"
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
