package com.izmirsoftware.tatilci.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.izmirsoftware.tatilci.databinding.RowReservationBinding
import com.izmirsoftware.tatilci.model.ApprovalStatus
import com.izmirsoftware.tatilci.model.PropertyType
import com.izmirsoftware.tatilci.model.ReservationModel
import com.izmirsoftware.tatilci.view.host.reservation.HostReservationFragmentDirections

class HostReservationAdapter : RecyclerView.Adapter<HostReservationAdapter.HostReservationViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<ReservationModel>() {
        override fun areItemsTheSame(
            oldItem: ReservationModel,
            newItem: ReservationModel
        ): Boolean {
            return oldItem.reservationId == newItem.reservationId
        }

        override fun areContentsTheSame(
            oldItem: ReservationModel,
            newItem: ReservationModel
        ): Boolean {
            return oldItem.reservationId == newItem.reservationId
        }
    }
    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var reservationList: List<ReservationModel>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    inner class HostReservationViewHolder(val binding: RowReservationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // Görünümleri buraya ekle
        val ivBestHouse = binding.ivBestHouse
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HostReservationViewHolder {
        val binding =
            RowReservationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HostReservationViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HostReservationViewHolder, position: Int) {
        val myReservation = reservationList[position]

        try {
            holder.binding.apply {
                reservation = myReservation
                if (myReservation.propertyType != null) {
                    when (myReservation.propertyType) {
                        PropertyType.HOUSE -> tvPropertyType.text = "Villa"
                        PropertyType.APARTMENT -> tvPropertyType.text = "Apartman"
                        PropertyType.GUEST_HOUSE -> tvPropertyType.text = "Misafir evi"
                        PropertyType.HOTEL -> tvPropertyType.text = "Otel"
                        else->{

                        }
                    }

                } else {
                    tvPropertyType.text = "Villa"
                }

                if (myReservation.approvalStatus != null) {
                    when (myReservation.approvalStatus) {
                        ApprovalStatus.APPROVED -> tvApprovalStatus.text = "Onaylandı"
                        ApprovalStatus.NOT_APPROVED -> tvApprovalStatus.text = "Reddedildi"
                        ApprovalStatus.WAITING_FOR_APPROVAL -> tvApprovalStatus.text = "Onay bekliyor"
                        else -> {

                        }
                    }
                } else {
                    tvApprovalStatus.text = "Onay bekliyor"
                }
            }
            //holder.binding.tvPropertyType.text = myReservation.propertyType ?: "Apartman"



            holder.itemView.setOnClickListener {
                val directions = HostReservationFragmentDirections.actionNavigationHostReservationToHostReservationDetailsFragment(myReservation.reservationId.toString())
                Navigation.findNavController(it).navigate(directions)
            }
        } catch (e: Exception) {
            // Hata oluştuğunda Toast göster
            Toast.makeText(holder.itemView.context, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return reservationList.size
    }
}
