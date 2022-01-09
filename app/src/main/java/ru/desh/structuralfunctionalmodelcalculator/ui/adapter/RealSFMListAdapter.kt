package ru.desh.structuralfunctionalmodelcalculator.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import ru.desh.structuralfunctionalmodelcalculator.R
import ru.desh.structuralfunctionalmodelcalculator.ui.fragment.ICSDetailsFragment
import ru.desh.structuralfunctionalmodelcalculator.ui.fragment.ICSDetailsFragmentDirections

class RealSFMListAdapter(private val icsDetailsController: ICSDetailsFragment.ICSDetailsController) :
    RecyclerView.Adapter<RealSFMListAdapter.RealSFMViewHolder>() {

    class RealSFMViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var realSFMName: TextView? = null
        var realSFMIndicator: TextView? = null
        var buttonDeleteRealSFM: ImageButton? = null

        init {
            realSFMName = itemView.findViewById(R.id.item_real_sfm_name)
            realSFMIndicator = itemView.findViewById(R.id.item_real_sfm_coeff)
            buttonDeleteRealSFM = itemView.findViewById(R.id.item_real_sfm_button_delete)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RealSFMViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_real_sfm, parent, false)
        return RealSFMViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RealSFMViewHolder, position: Int) {
        val realSfm = icsDetailsController.getRealSFMList()[position]
        holder.realSFMName?.text = realSfm.realSFMName
        holder.realSFMIndicator?.text = "%.5f".format(realSfm.qualityComprehensiveIndicator)
        holder.buttonDeleteRealSFM?.setOnClickListener { icsDetailsController.deleteRealSFM(realSfm) }
        holder.itemView.setOnClickListener {
            val action = ICSDetailsFragmentDirections.actionICSDetailsToSFMFunctions()
            action.argCurrentIcsId = icsDetailsController.getCurrentICSId()
            action.argRealSfmId = realSfm.realSFMId
            action.argIsBaseSfm = false
            holder.itemView.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return icsDetailsController.getRealSFMList().size
    }

}