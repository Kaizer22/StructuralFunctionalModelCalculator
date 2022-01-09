package ru.desh.structuralfunctionalmodelcalculator.ui.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import ru.desh.structuralfunctionalmodelcalculator.R
import ru.desh.structuralfunctionalmodelcalculator.databinding.FragmentTechnicalSystemsBinding
import ru.desh.structuralfunctionalmodelcalculator.model.ICSSystemDatabase
import ru.desh.structuralfunctionalmodelcalculator.model.dao.ICSDao
import ru.desh.structuralfunctionalmodelcalculator.model.entity.ICS
import ru.desh.structuralfunctionalmodelcalculator.model.entity.SFMFunction
import ru.desh.structuralfunctionalmodelcalculator.model.entity.SFMIndicator
import ru.desh.structuralfunctionalmodelcalculator.model.entity.TechnicalSystem
import ru.desh.structuralfunctionalmodelcalculator.ui.adapter.TechnicalSystemListAdapter
import kotlin.random.Random

class TechnicalSystemsFragment : Fragment() {
    private lateinit var binding: FragmentTechnicalSystemsBinding

    private lateinit var currentICS: ICS
    private var currentICSId = 0L
    private var currentSFMId = 0L
    private var currentSFMFunctionId = 0L

    private var isBaseSFM = false

    private lateinit var technicalSystemsController: TechnicalSystemsController

    class TechnicalSystemsController(
        private val icsDao: ICSDao,
        private val technicalSystemsFragment: TechnicalSystemsFragment
    ) {

        fun addTechnicalSystem(techSystem: TechnicalSystem) {
            if (technicalSystemsFragment.isBaseSFM) {
                getCurrentSFMFunction().technicalSystems.add(techSystem)
                technicalSystemsFragment.currentICS.realSFMs.clear()
                icsDao.update(technicalSystemsFragment.currentICS)
                technicalSystemsFragment.updateTechSystemList()
            }
        }

        fun deleteTechnicalSystem(techSystem: TechnicalSystem) {
            if (technicalSystemsFragment.isBaseSFM) {
                getCurrentSFMFunction().technicalSystems.remove(techSystem)
                technicalSystemsFragment.currentICS.realSFMs.clear()
                icsDao.update(technicalSystemsFragment.currentICS)
                technicalSystemsFragment.updateTechSystemList()
            }
        }

        fun increaseTechnicalSystemPriority(position: Int) {
            if (technicalSystemsFragment.isBaseSFM) {
                val techSysList = getTechSystemsList()
                if (position != 0) {
                    val buf = techSysList[position - 1]
                    techSysList[position - 1] = techSysList[position]
                    techSysList[position] = buf

                    techSysList[position].systemPriority = position
                    techSysList[position - 1].systemPriority = position - 1

                    technicalSystemsFragment.currentICS.realSFMs.clear()
                    icsDao.update(technicalSystemsFragment.currentICS)
                    technicalSystemsFragment.updateTechSystemList()
                } else {
                    Toast.makeText(technicalSystemsFragment.requireContext(),
                        "Невозможно поднять приоритет TC", Toast.LENGTH_LONG).show()
                }
            }
        }

        fun decreaseTechnicalSystemPriority(position: Int) {
            if (technicalSystemsFragment.isBaseSFM) {
                val techSysList = getTechSystemsList()
                if (position != techSysList.size - 1) {
                    val buf = techSysList[position + 1]
                    techSysList[position + 1] = techSysList[position]
                    techSysList[position] = buf

                    techSysList[position].systemPriority = position
                    techSysList[position + 1].systemPriority = position + 1

                    technicalSystemsFragment.currentICS.realSFMs.clear()
                    icsDao.update(technicalSystemsFragment.currentICS)
                    technicalSystemsFragment.updateTechSystemList()
                } else {
                    Toast.makeText(technicalSystemsFragment.requireContext(),
                        "Невозможно снизить приоритет ТС", Toast.LENGTH_LONG).show()
                }
            }

        }

        fun updateIndicator(
            techSys: TechnicalSystem,
            indicator: SFMIndicator,
            minVal: Float,
            maxVal: Float
        ) {
            val ind = getTechSystemsList().first { it.systemId == techSys.systemId }
                .systemIndicators.first { it.indicatorId == indicator.indicatorId }
            ind.minValue = minVal
            ind.maxValue = maxVal

            icsDao.update(technicalSystemsFragment.currentICS)
            technicalSystemsFragment.updateTechSystemList()
        }

        fun getICS(icsId: Long) {
            technicalSystemsFragment.currentICS = icsDao.getICSByID(icsId)
        }

        //TODO bad; refactor DB and entity relations
        fun getTechSystemsList(): MutableList<TechnicalSystem> {
            return if (technicalSystemsFragment.isBaseSFM) {
                technicalSystemsFragment.currentICS.baseSFM
                    .first { it.functionId == technicalSystemsFragment.currentSFMFunctionId }.technicalSystems
            } else {
                technicalSystemsFragment.currentICS.realSFMs
                    .first { it.realSFMId == technicalSystemsFragment.currentSFMId }
                    .sfmFunctions
                    .first { it.functionId == technicalSystemsFragment.currentSFMFunctionId }
                    .technicalSystems
            }
        }

        fun getCurrentSFMFunction(): SFMFunction {
            return if (technicalSystemsFragment.isBaseSFM) {
                technicalSystemsFragment.currentICS.baseSFM
                    .first { it.functionId == technicalSystemsFragment.currentSFMFunctionId }
            } else {
                technicalSystemsFragment.currentICS.realSFMs
                    .first { it.realSFMId == technicalSystemsFragment.currentSFMId }
                    .sfmFunctions
                    .first { it.functionId == technicalSystemsFragment.currentSFMFunctionId }
            }
        }

        fun isBaseSFM(): Boolean {
            return technicalSystemsFragment.isBaseSFM
        }

        fun showIndicatorValuesDialog(techSystem: TechnicalSystem, sfmIndicator: SFMIndicator, itemView: View) {
            technicalSystemsFragment.showIndicatorDialog(techSystem, sfmIndicator, itemView)
        }
    }

    fun updateTechSystemList() {
        binding.techSystemsList.adapter?.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentICSId = TechnicalSystemsFragmentArgs.fromBundle(requireArguments()).argCurrentIcsId
        currentSFMId = TechnicalSystemsFragmentArgs.fromBundle(requireArguments()).argRealSfmId
        currentSFMFunctionId = TechnicalSystemsFragmentArgs.fromBundle(requireArguments()).argSfmFunctionId
        isBaseSFM = TechnicalSystemsFragmentArgs.fromBundle(requireArguments()).argIsBaseSfm

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTechnicalSystemsBinding.inflate(inflater, container, false)

        technicalSystemsController = TechnicalSystemsController(
            ICSSystemDatabase.getDB(requireContext()).icsDao(),
            this
        )
        technicalSystemsController.getICS(currentICSId)

        initInteractions()
        initValues()
        return binding.root
    }

    private fun initValues() {
        val currentSFMFunction = technicalSystemsController.getCurrentSFMFunction()
        binding.techSystemsSfmFunctionName.text = currentSFMFunction.functionName
        binding.techSystemsUnitValue.text = currentSFMFunction.structuralDivision.divisionName

        binding.techSystemsList.adapter = TechnicalSystemListAdapter(technicalSystemsController)
        binding.techSystemsList.layoutManager = LinearLayoutManager(requireContext())

    }
    private fun initInteractions() {
        if (isBaseSFM) {
            binding.techSystemsButtonAddSystem.setOnClickListener {
                showAddTechSystemDialog()
            }
        } else {
            binding.techSystemsButtonAddSystem.visibility = View.GONE
        }

    }

    private fun showAddTechSystemDialog() {
        val techSystemDialog = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val v = inflater.inflate(R.layout.dialog_create_tech_system, null)
        val techSystemName = v.findViewById<EditText>(R.id.create_tech_system_name_field)

        techSystemDialog
            .setView(v)
            .setTitle("Добавление технической системы, реализующей Функцию")
            .setPositiveButton("Добавить") { dialog: DialogInterface?, which: Int ->
                val name = techSystemName.text.toString()
                if (name.isEmpty()) {
                    Toast.makeText(requireContext(),
                        "Введите наименование технической системы!",
                        Toast.LENGTH_LONG).show()
                } else {
                    val techSystem = TechnicalSystem(
                        Random.nextLong(),
                        name,
                        currentICS.systemIndicators.map { it.copy() },
                        technicalSystemsController.getTechSystemsList().size
                    )
                    technicalSystemsController.addTechnicalSystem(techSystem)
                    binding.techSystemsList.adapter?.notifyItemInserted(
                        technicalSystemsController.getTechSystemsList().size)
                    updateTechSystemList()
                }
            }
            .setNegativeButton("Отмена") { dialog: DialogInterface?, _: Int -> dialog?.cancel()}
        techSystemDialog.create()
        techSystemDialog.show()
    }

    private fun showIndicatorDialog(techSystem: TechnicalSystem, sfmIndicator: SFMIndicator, itemView: View) {
        val indicatorValDialog = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val v = inflater.inflate(R.layout.dialog_indicator, null)
        val indicatorMin = v.findViewById<EditText>(R.id.dialog_indicator_min_value)
        val indicatorMax = v.findViewById<EditText>(R.id.dialog_indicator_max_value)

        val indicatorBaseMax = v.findViewById<TextView>(R.id.dialog_indicator_base_max_value)
        val indicatorBaseMin = v.findViewById<TextView>(R.id.dialog_indicator_base_min_value)

        val indicatorBaseMaxT = v.findViewById<TextView>(R.id.dialog_indicator_base_max_title)
        val indicatorBaseMinT = v.findViewById<TextView>(R.id.dialog_indicator_base_min_title)

        if (isBaseSFM) {
            indicatorBaseMax.visibility = View.GONE
            indicatorBaseMin.visibility = View.GONE

            indicatorBaseMaxT.visibility = View.GONE
            indicatorBaseMinT.visibility = View.GONE
        } else {
            val sfmF = technicalSystemsController.getCurrentSFMFunction()
            val baseIndicator = currentICS.baseSFM
                .first { it.functionName == sfmF.functionName }
                .technicalSystems.first { it.systemName == techSystem.systemName}
                .systemIndicators.first { it.indicatorName == sfmIndicator.indicatorName}
            indicatorBaseMax.text = baseIndicator.maxValue.toString()
            indicatorBaseMin.text = baseIndicator.minValue.toString()
        }

        indicatorValDialog
            .setView(v)
            .setTitle("Изменение значений показателя ${sfmIndicator.indicatorName}")
            .setPositiveButton("Добавить") { dialog: DialogInterface?, which: Int ->
                val maxVal = indicatorMax.text.toString().toFloatOrNull()
                val minVal = indicatorMin.text.toString().toFloatOrNull()
                if (maxVal == null || minVal == null) {
                    Toast.makeText(requireContext(),
                        "Введите корректные значения параметра!",
                        Toast.LENGTH_LONG).show()
                } else {
                    technicalSystemsController.updateIndicator(techSystem, sfmIndicator, minVal, maxVal)

                    updateTechSystemList()
                }
            }
            .setNegativeButton("Отмена") { dialog: DialogInterface?, _: Int -> dialog?.cancel()}
        indicatorValDialog.create()
        indicatorValDialog.show()
    }
}