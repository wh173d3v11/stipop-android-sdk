package io.stipop.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.stipop.Config
import io.stipop.R
import io.stipop.Utils
import io.stipop.activity.Keyboard
import io.stipop.model.SPPackage
import org.json.JSONObject


class KeyboardPackageAdapter(private val dataList: ArrayList<SPPackage>, var context: Context, var keyboard: Keyboard):
    RecyclerView.Adapter<KeyboardPackageAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var mListener: OnItemClickListener? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val imageIV: ImageView
        val containerLL: LinearLayout

        init {
            imageIV = view.findViewById(R.id.imageIV)
            containerLL = view.findViewById(R.id.containerLL)
        }

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_keyboard_package, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]

        val packageImg = item.packageImg

        Glide.with(context).load(packageImg).into(holder.imageIV)


        if (keyboard.selectedPackageId == item.packageId) {
            holder.containerLL.setBackgroundColor(Color.parseColor(Config.themeContentsBgColor))
        } else {
            holder.containerLL.setBackgroundColor(Color.parseColor(Config.themeGroupedBgColor))
        }

        holder.containerLL.setOnClickListener {
            if (mListener != null) {
                mListener!!.onItemClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mListener = listener
    }

}