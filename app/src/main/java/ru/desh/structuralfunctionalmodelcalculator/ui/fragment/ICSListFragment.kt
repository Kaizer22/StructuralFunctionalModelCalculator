package ru.desh.structuralfunctionalmodelcalculator.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.desh.structuralfunctionalmodelcalculator.R
import ru.desh.structuralfunctionalmodelcalculator.databinding.ActivityMainBinding
import ru.desh.structuralfunctionalmodelcalculator.databinding.FragmentIcsListBinding
import ru.desh.structuralfunctionalmodelcalculator.model.ICSSystemDatabase
import ru.desh.structuralfunctionalmodelcalculator.model.dao.ICSDao
import ru.desh.structuralfunctionalmodelcalculator.model.entity.*
import ru.desh.structuralfunctionalmodelcalculator.ui.adapter.ICSListAdapter
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random


class ICSListFragment : Fragment() {

    private lateinit var binding: FragmentIcsListBinding
    private lateinit var icsList: List<ICS>


    private lateinit var icsDataController: ICSDataController
    class ICSDataController(
        private val icsDao: ICSDao,
        private val icsListFragment: ICSListFragment,
        var icsList: List<ICS>
        ) {

        fun getData() {
            icsList = icsDao.getAll()
            icsListFragment.updateList()
        }

        fun deleteICS(ics: ICS) {
            icsDao.delete(ics)
            icsList = icsDao.getAll()
            icsListFragment.updateList()
        }

        fun createICS(ics: ICS) {
            icsDao.insertAll(ics)
            icsList = icsDao.getAll()
            icsListFragment.updateList()
        }

    }

    private fun updateList() {
        binding.rvIcsList.adapter!!.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIcsListBinding.inflate(inflater, container, false)
        icsList = ArrayList()
        icsDataController = ICSDataController(
            ICSSystemDatabase.getDB(requireContext()).icsDao(),
            this,
            icsList
        )
        initInteractions()
        icsDataController.getData()
        return binding.root
    }

    private fun initInteractions() {
        binding.rvIcsList.layoutManager = LinearLayoutManager(context)
        binding.rvIcsList.adapter = ICSListAdapter(icsDataController)

        binding.buttonAddIcs.setOnClickListener {
            findNavController().navigate(R.id.CreateICSFragment)
        }
    }

//    companion object {
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            ICSSystemsListFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}