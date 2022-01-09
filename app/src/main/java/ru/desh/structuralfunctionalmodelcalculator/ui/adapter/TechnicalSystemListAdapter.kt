package ru.desh.structuralfunctionalmodelcalculator.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.desh.structuralfunctionalmodelcalculator.R
import ru.desh.structuralfunctionalmodelcalculator.model.entity.TechnicalSystem
import ru.desh.structuralfunctionalmodelcalculator.ui.fragment.TechnicalSystemsFragment

class TechnicalSystemListAdapter(
   private val technicalSystemsController: TechnicalSystemsFragment.TechnicalSystemsController
): RecyclerView.Adapter<TechnicalSystemListAdapter.TechnicalSystemViewHolder>() {

    class TechnicalSystemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var techSystemName: TextView? = null
        var techSystemPriority: TextView? = null
        var buttonDelete: ImageButton? = null
        var buttonIncreasePriority: ImageButton? = null
        var buttonDecreasePriority: ImageButton? = null
        var indicatorsContainer: LinearLayout? = null

        init {
            techSystemName = itemView.findViewById(R.id.item_tech_system_name)
            techSystemPriority = itemView.findViewById(R.id.item_tech_system_priority_value)
            buttonDelete = itemView.findViewById(R.id.item_tech_system_button_delete)
            buttonIncreasePriority = itemView.findViewById(R.id.item_tech_system_button_inc_priority)
            buttonDecreasePriority = itemView.findViewById(R.id.item_tech_system_button_dec_priority)
            indicatorsContainer = itemView.findViewById(R.id.item_tech_system_indicators_container)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TechnicalSystemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tech_system, parent, false)
        return TechnicalSystemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TechnicalSystemViewHolder, position: Int) {
        val techSys = technicalSystemsController.getTechSystemsList()[position]
        holder.techSystemName?.text = techSys.systemName
        holder.techSystemPriority?.text = techSys.systemPriority.toString()

        if (technicalSystemsController.isBaseSFM()) {
            holder.buttonDelete?.setOnClickListener { technicalSystemsController.deleteTechnicalSystem(techSys) }
            holder.buttonIncreasePriority?.setOnClickListener { technicalSystemsController.increaseTechnicalSystemPriority(position)}
            holder.buttonDecreasePriority?.setOnClickListener { technicalSystemsController.decreaseTechnicalSystemPriority(position)}
        } else {
            holder.buttonDelete?.visibility = View.GONE
            holder.buttonDecreasePriority?.visibility = View.GONE
            holder.buttonIncreasePriority?.visibility = View.GONE
        }

        addIndicators(holder.itemView, techSys)
    }

    private fun addIndicators(
        itemView: View,
        technicalSystem: TechnicalSystem
    ) {
        val mInflater = LayoutInflater.from(itemView.context)
        val container = itemView.findViewById<LinearLayout>(R.id.item_tech_system_indicators_container)
        container.removeAllViews()
        for (sI in technicalSystem.systemIndicators) {
            val indicatorView = mInflater.inflate(R.layout.subitem_indicator, null, false)

            val indicatorName = indicatorView.findViewById<TextView>(R.id.subitem_indicator_name)
            val indicatorPriority = indicatorView.findViewById<TextView>(R.id.subitem_indicator_priority)
            val indicatorMin = indicatorView.findViewById<TextView>(R.id.subitem_indicator_min_value)
            val indicatorMax = indicatorView.findViewById<TextView>(R.id.subitem_indicator_max_value)

            indicatorName.text = sI.indicatorName
            indicatorPriority.text = sI.indicatorPriority.toString()
            indicatorMin.text = sI.minValue.toString()
            indicatorMax.text = sI.maxValue.toString()

            indicatorView.setOnClickListener {
                technicalSystemsController.showIndicatorValuesDialog(technicalSystem, sI, itemView)
            }

            container.addView(indicatorView)
        }
    }

    override fun getItemCount(): Int {
        return technicalSystemsController.getTechSystemsList().size
    }
}