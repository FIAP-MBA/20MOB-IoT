package com.github.cesar1287.iot20mob

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.cesar1287.iot20mob.databinding.ActivityMainBinding
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var cyclistList: List<Cyclist>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Firebase.firestore.collection(KEY_FIRESTORE_COLLECTION_CYCLISTS)
            .addSnapshotListener { value, _ ->
                cyclistList = value?.toObjects(Cyclist::class.java)?.map {
                    val simpleDateFormat =
                        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    val newDate = simpleDateFormat.parse(it.timestamp)
                    Cyclist(
                        action = it.action,
                        timestamp = it.timestamp,
                        timestampDate = newDate
                    )
                }
                binding.tvMainCyclists.text = value?.documents?.size.toString()
                loadChart()
            }
    }

    private fun loadChart() {
        val quarters = arrayOf("06-12h", "13-18h", "19-22h", "Após as 23h")
        val formatter: ValueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase): String {
                return quarters[value.toInt()]
            }
        }
        val xAxis: XAxis = binding.chart.xAxis
        xAxis.granularity = 1f // minimum axis-step (interval) is 1
        xAxis.valueFormatter = formatter

        val entries = mutableListOf<BarEntry>()
        entries.add(BarEntry(0f, cyclistList?.filter {
            it.timestampDate?.hours ?: 0 in 6..12
        }?.size?.toFloat() ?: 0f))
        entries.add(BarEntry(1f, cyclistList?.filter {
            it.timestampDate?.hours ?: 0 in 13..18
        }?.size?.toFloat() ?: 0f))
        entries.add(BarEntry(2f, cyclistList?.filter {
            it.timestampDate?.hours ?: 0 in 19..22
        }?.size?.toFloat() ?: 0f))
        entries.add(BarEntry(3f, cyclistList?.filter {
            it.timestampDate?.hours ?: 0 >= 23
        }?.size?.toFloat() ?: 0f))

        val set = BarDataSet(entries, "Gráfico de ciclistas por período")
        set.valueTextSize = 16f
        val data = BarData(set)
        data.barWidth = 0.9f // set custom bar width

        with(binding) {
            chart.data = data
            chart.setFitBars(true) // make the x-axis fit exactly all bars

            chart.invalidate() // refresh
        }
    }

    companion object {
        const val KEY_FIRESTORE_COLLECTION_CYCLISTS = "cyclists"
    }
}