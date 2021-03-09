package com.kontur.myapplication.ui.main

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kontur.myapplication.OnClickListener
import com.kontur.myapplication.R
import com.kontur.myapplication.databinding.MainFragmentBinding
import com.kontur.myapplication.models.Contact
import java.io.File

class MainFragment : Fragment() {


    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var binding: MainFragmentBinding
    private lateinit var file: File

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (!File(requireContext().filesDir, "cache.json").exists())
            File(requireContext().filesDir, "cache.json").createNewFile()
        file = File(requireContext().filesDir, "cache.json")
        mainViewModel.getInfo(file)
        initGUI()
        mainViewModel.contactList.observe(viewLifecycleOwner, this::onContactsFetched)
        mainViewModel.progress.observe(viewLifecycleOwner, this::onNetworkRequestUpdated)


    }


    private fun onNetworkRequestUpdated(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
    }


    private fun onContactsFetched(contacts: List<Contact>) {

        if (contacts.isNotEmpty()) {
            (binding.contactsList.adapter as MainContactListAdapter).updateList(contacts)
            binding.swipeRefreshLayout.isRefreshing = false
        } else
            Snackbar.make(binding.root, getString(R.string.error_text), Snackbar.LENGTH_LONG).show()
        saveList()
    }


    private fun initGUI() {

        binding.contactsList.apply {
            setHasFixedSize(true)
            adapter = MainContactListAdapter(object : OnClickListener {
                override fun onClick(contact: Contact) {
                    val action =
                        MainFragmentDirections.actionMainFragmentToDetailedContactFragment(contact)
                    findNavController().navigate(action)
                }
            })
            layoutManager = LinearLayoutManager(requireContext())
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            mainViewModel.updateInfo(file)
        }






        binding.searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                updateList(newText)
                return true
            }

        })

        saveList()

    }




    private fun updateList(newText: String) {
        (binding.contactsList.adapter as MainContactListAdapter).updateList(
            mainViewModel.getFilteredList(newText)
        )
    }

    private fun saveList() {
        mainViewModel.saveList((binding.contactsList.adapter as MainContactListAdapter).getItems())
    }


}