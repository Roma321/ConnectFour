package com.example.inrow.stats

import android.app.AlertDialog
import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.inrow.R
import com.example.inrow.database.GameRecord

class GameRecordAdapter(val context: Context) : RecyclerView.Adapter<GameRecordViewHolder>() {

    var data = listOf<GameRecord>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameRecordViewHolder {
        val myLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.game_record_layout, parent, false)
        return GameRecordViewHolder(myLayout as ConstraintLayout)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: GameRecordViewHolder, position: Int) {
        val item = data[position]
        holder.view.findViewById<TextView>(R.id.THE_TV).text = "${item.player1} - ${item.player2}"
        holder.view.findViewById<TextView>(R.id.textViewResult_InItem).text =
            item.resultAsText()
        holder.view.findViewById<TextView>(R.id.textViewLength_InItem).text =
            "${DateUtils.formatElapsedTime(item.totalLength().toLong())}, ${item.movesCount} ходов"
        holder.view.findViewById<ImageView>(R.id.imageViewShowMore).setOnClickListener {
            val dialog = context.let {
                val builder = AlertDialog.Builder(it)
                builder.setTitle("Информация об игре")
                    .setMessage(item.toString())
                    .setPositiveButton("ОК") { dialog, _ ->
                        dialog.cancel()
//                            goBack()
                    }
                builder.create()
            } ?: throw IllegalStateException("Activity cannot be null")
            dialog.show()
//            val dialog = Dialog(context)
//            dialog.setContentView(R.layout.game_record_layout)
//            dialog.setTitle("Информация об игре")
//            dialog.setMe
//            dialog.show()
        }
    }

}