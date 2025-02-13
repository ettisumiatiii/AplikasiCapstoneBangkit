package com.example.projekaplikasibangkitcapstone.utils

import com.example.projekaplikasibangkitcapstone.R
import com.example.projekaplikasibangkitcapstone.model.PenanggulanganBencanaModel

object PenanggulanganBencanaData {
    private val namaBencana = arrayOf(
        "Gempa Bumi",
        "Banjir",
        "Kebakaran",
        "Tanah Longsor"
    )
    private val fotoBencana = intArrayOf(
        R.drawable.earhquake,
        R.drawable.flood,
        R.drawable.wildfire,
        R.drawable.landslide
    )
    val listData:ArrayList<PenanggulanganBencanaModel>
    get() {
        val list = arrayListOf<PenanggulanganBencanaModel>()
        for (position in namaBencana.indices){
            var bencana = PenanggulanganBencanaModel()
            bencana.name = namaBencana[position]
            bencana.photo = fotoBencana[position]
            list.add(bencana)
        }
        return list
    }
}