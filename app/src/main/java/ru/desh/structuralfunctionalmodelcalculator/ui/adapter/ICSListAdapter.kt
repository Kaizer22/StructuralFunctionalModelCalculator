package ru.desh.structuralfunctionalmodelcalculator.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import ru.desh.structuralfunctionalmodelcalculator.R
import ru.desh.structuralfunctionalmodelcalculator.model.entity.ICS
import ru.desh.structuralfunctionalmodelcalculator.ui.fragment.ICSListFragment
import ru.desh.structuralfunctionalmodelcalculator.ui.fragment.ICSListFragmentDirections

class ICSListAdapter(private val icsDataController: ICSListFragment.ICSDataController) :
    RecyclerView.Adapter<ICSListAdapter.ICSViewHolder>() {

    class ICSViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var icsName: TextView? = null
        var icsIndicator: TextView? = null
        var buttonDeleteICS: ImageButton? = null

        init {
            icsName = itemView.findViewById(R.id.ics_name)
            icsIndicator = itemView.findViewById(R.id.ics_indicator)
            buttonDeleteICS = itemView.findViewById(R.id.button_delete_ics)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ICSViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_ics, parent, false)
        return ICSViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ICSViewHolder, position: Int) {
        val ics = icsDataController.icsList[position]
        holder.icsName?.text = ics.projectName
        holder.buttonDeleteICS?.setOnClickListener { icsDataController.deleteICS(ics) }
        holder.icsIndicator?.text = "%.5f".format(getICSAverageIndicator(ics))
        holder.itemView.setOnClickListener {
            val action = ICSListFragmentDirections.actionICSListToICSDetails()
            action.argCurrentIcsId = ics.icsSystemId
            holder.itemView.findNavController().navigate(action)
        }
    }

    private fun getICSAverageIndicator(ics: ICS): Float {
        var sum = 0F
        ics.realSFMs.forEach { sum+=it.qualityComprehensiveIndicator}
        return sum / ics.realSFMs.size
    }

    override fun getItemCount(): Int {
        return icsDataController.icsList.size
    }

}