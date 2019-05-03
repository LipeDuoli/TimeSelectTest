package br.com.duoli.timeselect

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import br.com.duoli.timeselect.databinding.DialogTimeSelectBinding
import br.com.duoli.timeselect.databinding.TimeItemBinding
import com.google.android.material.animation.ArgbEvaluatorCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yarolegovich.discretescrollview.DiscreteScrollView
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import java.text.DecimalFormat

class TimeSelectDialog(
    context: Context, private val minuteStep: Int = 15, onTimeSelect: (hourOfDay: Int, minutes: Int) -> Unit
) : MaterialAlertDialogBuilder(context), DiscreteScrollView.ScrollListener<TimeSelectDialog.TimeAdapter.ViewHolder>,
    DiscreteScrollView.OnItemChangedListener<TimeSelectDialog.TimeAdapter.ViewHolder> {

    private val evaluator = ArgbEvaluatorCompat()
    private var sideColor: Int = 0
    private var centerColor: Int = 0

    init {
        val viewBinding = DialogTimeSelectBinding.inflate(LayoutInflater.from(getContext()))
        setView(viewBinding.root)

        sideColor = ContextCompat.getColor(context, R.color.side_hour)
        centerColor = ContextCompat.getColor(context, R.color.colorPrimary)

        val list = createHoursList()
        val infiniteScrollAdapter = InfiniteScrollAdapter.wrap(TimeAdapter(list))
        viewBinding.scrollView.apply {
            adapter = infiniteScrollAdapter
            scrollToPosition(infiniteScrollAdapter.getClosestPosition(list.size / 2))
            setSlideOnFling(true)
            setSlideOnFlingThreshold(1000)
            addScrollListener(this@TimeSelectDialog)
            addOnItemChangedListener(this@TimeSelectDialog)
            setItemTransformer(
                ScaleTransformer.Builder().setMinScale(0.8f).build()
            )
        }
        setPositiveButton("Selecionar") { dialog, _ ->
            val currentTime = list[infiniteScrollAdapter.realCurrentPosition]
            val split = currentTime.split(":")
            onTimeSelect(split[0].toInt(), split[1].toInt())
            dialog.dismiss()
        }
    }

    override fun onScroll(
        scrollPosition: Float,
        currentIndex: Int,
        newIndex: Int,
        currentHolder: TimeAdapter.ViewHolder?,
        newCurrent: TimeAdapter.ViewHolder?
    ) {
        if (currentHolder != null && newCurrent != null) {
            val position = Math.abs(scrollPosition)
            currentHolder.setTextColor(interpolate(position, centerColor, sideColor))
            newCurrent.setTextColor(interpolate(position, sideColor, centerColor))
        }
    }

    override fun onCurrentItemChanged(viewHolder: TimeAdapter.ViewHolder?, adapterPosition: Int) {
        viewHolder?.setTextColor(centerColor)
    }

    private fun createHoursList(): List<String> {
        val hours = mutableListOf<String>()
        val format = DecimalFormat("00")
        for (hour in 0..23) {
            for (minute in 0..59 step minuteStep) {
                hours.add("${format.format(hour)}:${format.format(minute)}")
            }
        }
        return hours
    }

    private fun interpolate(fraction: Float, c1: Int, c2: Int): Int {
        return evaluator.evaluate(fraction, c1, c2) as Int
    }

    inner class TimeAdapter(private val times: List<String>) : RecyclerView.Adapter<TimeAdapter.ViewHolder>() {

        var itemWidth = 0

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            super.onAttachedToRecyclerView(recyclerView)
            val context = recyclerView.context
            itemWidth = ((context.resources.displayMetrics.widthPixels * 0.7) / 3).toInt()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = TimeItemBinding.inflate(layoutInflater, parent, false)
            binding.tvTime.updateLayoutParams {
                width = itemWidth
            }
            return ViewHolder(binding)
        }

        override fun getItemCount() = times.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.binding.tvTime.text = times[position]
        }

        inner class ViewHolder(val binding: TimeItemBinding) : RecyclerView.ViewHolder(binding.root) {
            fun setTextColor(color: Int) {
                binding.tvTime.setTextColor(color)
            }
        }
    }
}