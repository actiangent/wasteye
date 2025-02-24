package com.actiangent.wasteye.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.actiangent.wasteye.MainActivity
import com.actiangent.wasteye.R
import com.actiangent.wasteye.WasteyeApplication
import com.actiangent.wasteye.databinding.FragmentSettingsBinding
import com.actiangent.wasteye.factory.WasteyeViewModelFactory
import com.actiangent.wasteye.model.Language
import com.yariksoffice.lingver.Lingver
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _fragmentSettingsBinding: FragmentSettingsBinding? = null
    private val fragmentSettingsBinding get() = _fragmentSettingsBinding!!

    private val viewModel: SettingsViewModel by viewModels(factoryProducer = {
        WasteyeViewModelFactory(WasteyeApplication.injection)
    })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentSettingsBinding = FragmentSettingsBinding.inflate(inflater, container, false)

        return fragmentSettingsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val languages = Language.entries
        val languagesAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, languages)

        fragmentSettingsBinding.apply {
            val dropdown = (dropdownLanguage.editText as AutoCompleteTextView)
            switchShowDetectionScore.setOnCheckedChangeListener { _, checked ->
                viewModel.setShowDetectionScore(checked)
            }
            dropdown.apply {
                setAdapter(languagesAdapter)
                setOnItemClickListener { _, _, position, _ ->
                    val language = languagesAdapter.getItem(position)
                    viewModel.setLanguagePreference(language!!)

                    Lingver.getInstance()
                        .setLocale(requireContext(), language.locale)

                    restart()
                }
            }

            lifecycleScope.launch {
                viewModel.userData.collect { userData ->
                    switchShowDetectionScore.isChecked = userData.isShowDetectionScore
                    dropdown.setText("${userData.languagePreference}", false)
                }
            }
        }
    }

    private fun restart() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}