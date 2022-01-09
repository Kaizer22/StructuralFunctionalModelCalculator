package ru.desh.structuralfunctionalmodelcalculator.ui.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import ru.desh.structuralfunctionalmodelcalculator.R
import ru.desh.structuralfunctionalmodelcalculator.databinding.FragmentSfmFunctionsBinding
import ru.desh.structuralfunctionalmodelcalculator.model.ICSSystemDatabase
import ru.desh.structuralfunctionalmodelcalculator.model.dao.ICSDao
import ru.desh.structuralfunctionalmodelcalculator.model.entity.ICS
import ru.desh.structuralfunctionalmodelcalculator.model.entity.RealSFM
import ru.desh.structuralfunctionalmodelcalculator.model.entity.SFMFunction
import ru.desh.structuralfunctionalmodelcalculator.model.entity.StructuralDivision
import ru.desh.structuralfunctionalmodelcalculator.ui.adapter.SFMFunctionListAdapter
import java.util.*
import kotlin.random.Random

class SFMFunctionsFragment : Fragment() {
    private lateinit var binding: FragmentSfmFunctionsBinding

    private lateinit var currentICS: ICS
    private var currentICSId = 0L
    private var currentSFMId = 0L

    private var isBaseSFM = false

    private lateinit var sfmFunctionsController: SFMFunctionsController

    class SFMFunctionsController(
        private val icsDao: ICSDao,
        private val sfmFunctionsFragment: SFMFunctionsFragment
    ) {

        fun addFunction(sfmFunction: SFMFunction) {
            if (sfmFunctionsFragment.isBaseSFM) {
                sfmFunctionsFragment.currentICS.baseSFM.add(sfmFunction)
                sfmFunctionsFragment.currentICS.realSFMs.clear()
                icsDao.update(sfmFunctionsFragment.currentICS)
                sfmFunctionsFragment.updateFunctionList()
            }
        }

        fun deleteFunction(sfmFunction: SFMFunction) {
            if (sfmFunctionsFragment.isBaseSFM) {
                sfmFunctionsFragment.currentICS.baseSFM.remove(sfmFunction)
                sfmFunctionsFragment.currentICS.realSFMs.clear()
                var j = 0
                for (i in sfmFunctionsFragment.currentICS.baseSFM) {
                    i.functionPriority = j++
                }
                icsDao.update(sfmFunctionsFragment.currentICS)
                sfmFunctionsFragment.updateFunctionList()
            }
        }

        fun increaseFunctionPriority(position: Int) {
            if (sfmFunctionsFragment.isBaseSFM) {
                val functionList = sfmFunctionsFragment.currentICS.baseSFM
                if (position != 0) {
                    val buf = functionList[position - 1]
                    functionList[position - 1] = functionList[position]
                    functionList[position] = buf

                    functionList[position].functionPriority = position
                    functionList[position - 1].functionPriority = position - 1

                    sfmFunctionsFragment.currentICS.realSFMs.clear()
                    icsDao.update(sfmFunctionsFragment.currentICS)
                    sfmFunctionsFragment.updateFunctionList()
                } else {
                    Toast.makeText(sfmFunctionsFragment.requireContext(),
                        "Невозможно поднять приоритет функции", Toast.LENGTH_LONG).show()
                }
            }


        }

        fun decreaseFunctionPriority(position: Int) {
            if (sfmFunctionsFragment.isBaseSFM) {
                val functionList = sfmFunctionsFragment.currentICS.baseSFM
                if (position != functionList.size - 1) {
                    val buf = functionList[position + 1]
                    functionList[position + 1] = functionList[position]
                    functionList[position] = buf

                    functionList[position].functionPriority = position
                    functionList[position + 1].functionPriority = position + 1

                    sfmFunctionsFragment.currentICS.realSFMs.clear()
                    icsDao.update(sfmFunctionsFragment.currentICS)
                    sfmFunctionsFragment.updateFunctionList()
                } else {
                    Toast.makeText(sfmFunctionsFragment.requireContext(),
                        "Невозможно снизить приоритет функции", Toast.LENGTH_LONG).show()
                }
            }
        }

        fun getICS(icsID: Long) {
            sfmFunctionsFragment.currentICS = icsDao.getICSByID(icsID)
        }

        fun getFunctionsList(): List<SFMFunction> {
            if (sfmFunctionsFragment.isBaseSFM) {
                return sfmFunctionsFragment.currentICS.baseSFM
            } else {
                return sfmFunctionsFragment.currentICS.realSFMs
                    .first {
                        it.realSFMId == sfmFunctionsFragment.currentSFMId
                    }
                    .sfmFunctions
            }
        }

        fun getCurrentSFM(): RealSFM? {
            if (!sfmFunctionsFragment.isBaseSFM) {
                return sfmFunctionsFragment.currentICS.realSFMs
                    .first { it.realSFMId == sfmFunctionsFragment.currentSFMId}
            }
            return null
        }

        fun isBaseSFM(): Boolean {
            return sfmFunctionsFragment.isBaseSFM
        }

        fun getICSId(): Long {
            return sfmFunctionsFragment.currentICSId
        }
    }

    private fun updateFunctionList() {
        binding.sfmFunctionsList.adapter?.notifyDataSetChanged()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentICSId = SFMFunctionsFragmentArgs.fromBundle(requireArguments()).argCurrentIcsId
        currentSFMId = SFMFunctionsFragmentArgs.fromBundle(requireArguments()).argRealSfmId
        isBaseSFM = SFMFunctionsFragmentArgs.fromBundle(requireArguments()).argIsBaseSfm
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSfmFunctionsBinding.inflate(inflater, container, false)

        sfmFunctionsController = SFMFunctionsController(
            ICSSystemDatabase.getDB(requireContext()).icsDao(),
            this
        )
        sfmFunctionsController.getICS(currentICSId)

        initInteractions()
        initContent()
        return binding.root
    }

    private fun initContent() {
        binding.sfmFunctionsIcsName.text = currentICS.projectName
        if (isBaseSFM) {
            binding.sfmFunctionsSfmName.text = "Базовая СФМ. Внимание! При изменении параметров базовой СФМ, созданные для данной ИС реальные СФМ будут удалены."
        } else {
            binding.sfmFunctionsSfmName.text = sfmFunctionsController.getCurrentSFM()!!.realSFMName
        }
    }

    fun initInteractions() {
        binding.sfmFunctionsList.adapter = SFMFunctionListAdapter(sfmFunctionsController)
        binding.sfmFunctionsList.layoutManager = LinearLayoutManager(requireContext())

        if (isBaseSFM) {
            binding.sfmFunctionsButtonAddFunction.setOnClickListener {
                showCreateSFMFunctionDialog()
            }
        } else {
            binding.sfmFunctionsButtonAddFunction.visibility = View.GONE
        }


    }

    private fun showCreateSFMFunctionDialog() {
        val indicatorDialog = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val v = inflater.inflate(R.layout.dialog_create_function, null)
        val functionNameField = v.findViewById<EditText>(R.id.create_function_name_field)
        val functionDescriptionField = v.findViewById<EditText>(R.id.create_function_description_field)
        val functionUnitField = v.findViewById<EditText>(R.id.create_function_unit_field)

        indicatorDialog
            .setView(v)
            .setTitle("Добавление функции к базовой СФМ")
            .setPositiveButton("Добавить") { dialog: DialogInterface?, which: Int ->
                val name = functionNameField.text.toString()
                val description = functionDescriptionField.text.toString()
                val unit = functionUnitField.text.toString()
                if (name.isEmpty() || unit.isEmpty()) {
                    Toast.makeText(requireContext(),
                        "Введите наименование функции и структурное подразделение, ответсвенное за реализацию!",
                        Toast.LENGTH_LONG).show()
                } else {
                    val sfmFunction = SFMFunction(
                        Random.nextLong(),
                        name,
                        description,
                        sfmFunctionsController.getFunctionsList().size,
                        mutableListOf(),
                        StructuralDivision(Random.nextLong(), unit, "Структурное подразделение")
                    )
                    sfmFunctionsController.addFunction(sfmFunction)
                    binding.sfmFunctionsList.adapter?.notifyItemInserted(
                        sfmFunctionsController.getFunctionsList().size)
                    updateFunctionList()
                }
            }
            .setNegativeButton("Отмена") { dialog: DialogInterface?, _: Int -> dialog?.cancel()}
        indicatorDialog.create()
        indicatorDialog.show()
    }

}