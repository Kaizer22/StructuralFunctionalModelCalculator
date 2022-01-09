package ru.desh.structuralfunctionalmodelcalculator.ui.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.desh.structuralfunctionalmodelcalculator.R
import ru.desh.structuralfunctionalmodelcalculator.core.ICSQualityComprehensiveIndicatorCalculator
import ru.desh.structuralfunctionalmodelcalculator.databinding.FragmentIcsDetailsBinding
import ru.desh.structuralfunctionalmodelcalculator.model.ICSSystemDatabase
import ru.desh.structuralfunctionalmodelcalculator.model.dao.ICSDao
import ru.desh.structuralfunctionalmodelcalculator.model.entity.ICS
import ru.desh.structuralfunctionalmodelcalculator.model.entity.RealSFM
import ru.desh.structuralfunctionalmodelcalculator.ui.adapter.RealSFMListAdapter
import java.util.*
import kotlin.random.Random
import kotlinx.coroutines.*

class ICSDetailsFragment : Fragment() {
    private lateinit var binding: FragmentIcsDetailsBinding
    private lateinit var currentICS: ICS
    private var currentICSId: Long = 0


    private lateinit var icsDetailsController: ICSDetailsController
    class ICSDetailsController (
        private val icsDao: ICSDao,
        private val icsDetailsFragment: ICSDetailsFragment
        ) {

        fun addRealSFM(realSFM: RealSFM) {
            icsDetailsFragment.currentICS.realSFMs.add(realSFM)
            icsDao.update(icsDetailsFragment.currentICS)
            icsDetailsFragment.updateRealSFMList()
            if (getRealSFMList().size == 2) {
                icsDetailsFragment.binding.icsDetailsButtonSortRealSfm.visibility = View.VISIBLE
            }
        }

        fun deleteRealSFM(realSFM: RealSFM) {
            icsDetailsFragment.currentICS.realSFMs.remove(realSFM)
            icsDao.update(icsDetailsFragment.currentICS)
            icsDetailsFragment.updateRealSFMList()
            if (getRealSFMList().size == 1) {
                icsDetailsFragment.binding.icsDetailsButtonSortRealSfm.visibility = View.INVISIBLE
            }
        }

        fun calculateQualityIndicator() {
            ICSQualityComprehensiveIndicatorCalculator.calculateQualityIndicator(
                icsDetailsFragment.currentICS
            )
            icsDao.update(icsDetailsFragment.currentICS)
            icsDetailsFragment.updateRealSFMList()
        }

        fun getICS(icsID: Long) {
            icsDetailsFragment.currentICS = icsDao.getICSByID(icsID)
        }

        fun getRealSFMList(): List<RealSFM> {
            return icsDetailsFragment.currentICS.realSFMs
        }

        fun getCurrentICSId(): Long {
            return icsDetailsFragment.currentICSId
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentICSId = ICSDetailsFragmentArgs.fromBundle(requireArguments()).argCurrentIcsId
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIcsDetailsBinding.inflate(inflater, container, false)

        icsDetailsController = ICSDetailsController(
            ICSSystemDatabase.getDB(requireContext()).icsDao(),
            this
        )
        icsDetailsController.getICS(currentICSId)


        initInteractions()
        initICSValues()
        return binding.root
    }

    private fun initICSValues() {
        binding.icsName.text = currentICS.projectName
        for(task in currentICS.icsTasks) {

            val taskName = TextView(requireContext())
            val taskDescription = TextView(requireContext())

            val lp = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            taskName.layoutParams = lp
            taskDescription.layoutParams = lp

            taskName.gravity = Gravity.START
            taskDescription.gravity = Gravity.START

            taskName.setTextColor(Color.BLACK)
            taskName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)
            taskName.setTypeface(taskName.typeface, Typeface.BOLD)

            taskName.text = task.taskTitle
            taskDescription.text = task.taskDescription

            val scale = resources.displayMetrics.density
            val dpAsPixels = (8 * scale + 0.5f).toInt()
            taskDescription.setPadding(0, 0, 0, dpAsPixels)

            binding.icsDetailsTasksList.addView(taskName)
            binding.icsDetailsTasksList.addView(taskDescription)
        }

        binding.icsDetailsEstimatedTimeValue.text = currentICS.estimatedPeriodDays.toString()
        binding.icsDetailsEstimatedResourcesValue.text = currentICS.cashFlow.toString()
    }

    private fun initInteractions() {

        binding.icsDetailsRealSfmList.adapter = RealSFMListAdapter(icsDetailsController)
        binding.icsDetailsRealSfmList.layoutManager = LinearLayoutManager(requireContext())

        binding.icsDetailsButtonEditBaseSfm.setOnClickListener {
            val action = ICSDetailsFragmentDirections.actionICSDetailsToSFMFunctions()
            action.argCurrentIcsId = icsDetailsController.getCurrentICSId()
            action.argRealSfmId = 0L
            action.argIsBaseSfm = true
            findNavController().navigate(action)
        }
        binding.icsDetailsButtonSortRealSfm.setOnClickListener {
            val popupMenu = androidx.appcompat.widget.PopupMenu(requireContext(), binding.icsDetailsButtonSortRealSfm)
            popupMenu.inflate(R.menu.menu_sort_real_sfm)
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.sort_by_abc -> sortByAbc()
                    R.id.sort_by_coeff -> sortByCoeff()
                    else -> false
                }
            }
            popupMenu.show()
        }

        if (icsDetailsController.getRealSFMList().size < 2) {
            binding.icsDetailsButtonSortRealSfm.visibility = View.INVISIBLE
        }


        binding.icsDetailsButtonAddRealSfm.setOnClickListener {
            showCreateRealSFMDialog()
        }

        binding.icsDetailsButtonCalculateQualityIndicator.setOnClickListener {
            icsDetailsController.calculateQualityIndicator()
        }

        binding.icsDetailsButtonToVisualisation.setOnClickListener {
            findNavController().navigate(R.id.SFMComparisonVisualisationFragment)
        }
    }

    private fun sortByCoeff(): Boolean {
        val coeffComparator = Comparator<RealSFM> { o1, o2 ->
            when {
                o1.qualityComprehensiveIndicator > o2.qualityComprehensiveIndicator -> 1
                o1.qualityComprehensiveIndicator < o2.qualityComprehensiveIndicator -> -1
                else -> 0
            }
        }
        Collections.sort(currentICS.realSFMs, coeffComparator)
        updateRealSFMList()
        return true
    }

    private fun sortByAbc(): Boolean {
        val abcComparator = Comparator<RealSFM> { o1, o2 ->
            val name1 = o1.realSFMName
            val name2 = o2.realSFMName
            for (i in 0 until name1.length.coerceAtMost(name2.length)) {
                when {
                    name1[i] > name2[i] -> return@Comparator 1
                    name1[i] < name2[i] -> return@Comparator -1
                }
            }
            return@Comparator 0
        }
        Collections.sort(currentICS.realSFMs, abcComparator)
        updateRealSFMList()
        return true
    }

    private fun showCreateRealSFMDialog() {
        val indicatorDialog = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val v = inflater.inflate(R.layout.dialog_create_real_sfm, null)
        val realSFMNameField = v.findViewById<EditText>(R.id.create_real_sfm_name_field)

        indicatorDialog
            .setView(v)
            .setTitle("Создание реальной СФМ")
            .setPositiveButton("Добавить") { _: DialogInterface?, _: Int ->
                val name = realSFMNameField.text.toString()
                if (name.isEmpty()) {
                    Toast.makeText(requireContext(),
                        "Введите наименование реальной СФМ", Toast.LENGTH_LONG).show()
                } else {
                    val rSFM = RealSFM(
                        Random.nextLong(),
                        name,
                        Date(),
                        currentICS.baseSFM.map { it.copy() },
                        -1.0F
                    )
                    icsDetailsController.addRealSFM(rSFM)
                    binding.icsDetailsRealSfmList.adapter?.notifyItemInserted(
                        icsDetailsController.getRealSFMList().size)
                    updateRealSFMList()
                }
            }
            .setNegativeButton("Отмена") { dialog: DialogInterface?, _: Int -> dialog?.cancel()}
        indicatorDialog.create()
        indicatorDialog.show()
    }

    private fun updateRealSFMList() {
        binding.icsDetailsRealSfmList.adapter?.notifyDataSetChanged()
    }
}