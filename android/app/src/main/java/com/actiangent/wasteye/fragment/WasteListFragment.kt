package com.actiangent.wasteye.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.actiangent.wasteye.databinding.FragmentWasteListBinding
import com.actiangent.wasteye.model.Waste

class WasteListFragment : Fragment() {

    private var _fragmentWasteListBinding: FragmentWasteListBinding? = null
    private val fragmentWasteListBinding get() = _fragmentWasteListBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentWasteListBinding = FragmentWasteListBinding.inflate(inflater, container, false)

        return fragmentWasteListBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val wasteListAdapter = WasteListAdapter(::navigateToWasteDetail)
        fragmentWasteListBinding.recyclerViewWastes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = wasteListAdapter

            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        wasteListAdapter.submitList(Waste.entries)
    }

    private fun navigateToWasteDetail(waste: Waste) {
        val ordinal = waste.ordinal
        val action = WasteListFragmentDirections
            .actionWasteListFragmentToWasteDetailFragment(ordinal)
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _fragmentWasteListBinding = null
    }
}