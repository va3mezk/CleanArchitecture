package com.test.cleanArchRoomTest.character.view.character

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.test.cleanArchRoomTest.R
import com.test.cleanArchRoomTest.character.domain.model.CharactersData
import com.test.cleanArchRoomTest.databinding.FragmentDashboardBinding
import com.test.cleanArchRoomTest.utils.ext.addTo
import com.test.cleanArchRoomTest.utils.network.NetworkConnection
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable

@AndroidEntryPoint
class DashboardFragment : Fragment(), CharacterClicked {

    private val dashboardViewModel: DashboardViewModel by viewModels()
    private val disposables = CompositeDisposable()

    private lateinit var binding: FragmentDashboardBinding
    private lateinit var adapter: DashBoardAdapter
    private var characterList = ArrayList<CharactersData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        getCharacters()
    }

    private fun getCharacters() {
        dashboardViewModel.bound(NetworkConnection.checkNetwork())
        observeData()
    }

    private fun initAdapter() {
        adapter = DashBoardAdapter(this)
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.charactersList.layoutManager = layoutManager
        binding.charactersList.adapter = adapter
    }

    private fun observeData() {
        dashboardViewModel.charsList.observe(viewLifecycleOwner, Observer {
            adapter.fillData(it.toMutableList())
        })
    }

    override fun onResume() {
        super.onResume()
        dashboardViewModel.showErrorGettingChars.observe()
            .subscribe {
                context?.let { it1 ->
                    AlertDialog.Builder(it1)
                        .setTitle(getString(R.string.error_title))
                        .setNeutralButton(getString(R.string.ok)) { dialog, _ -> dialog.dismiss() }
                }
            }.addTo(disposables)
    }
    override fun onCharacterClicked(characterId: Int?) {
        val bundle = bundleOf("characterId" to characterId.toString())
        findNavController().navigate(R.id.action_navigation_dashboard_to_episodesFragment, bundle)
    }
    override fun onPause() {
        disposables.clear()
        super.onPause()
    }

}

