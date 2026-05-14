package com.seraphim.app.mapdemo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seraphim.core.map.commons.MapInitializer
import com.seraphim.core.map.commons.model.LatLng
import com.seraphim.core.map.commons.registry.MapProviderRegistry
import com.seraphim.core.map.commons.search.PoiResult
import com.seraphim.core.map.commons.search.SearchResult
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CENTER_LAT = "center_lat"
        const val EXTRA_CENTER_LNG = "center_lng"
        const val EXTRA_RESULT_LAT = "result_lat"
        const val EXTRA_RESULT_LNG = "result_lng"
        const val EXTRA_RESULT_NAME = "result_name"
    }

    private lateinit var searchView: SearchView
    private lateinit var btnTextSearch: Button
    private lateinit var btnNearbySearch: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvError: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PoiResultAdapter

    private var centerLatLng: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        centerLatLng = LatLng(
            intent.getDoubleExtra(EXTRA_CENTER_LAT, 0.0),
            intent.getDoubleExtra(EXTRA_CENTER_LNG, 0.0)
        )

        searchView = findViewById(R.id.searchView)
        btnTextSearch = findViewById(R.id.btnTextSearch)
        btnNearbySearch = findViewById(R.id.btnNearbySearch)
        progressBar = findViewById(R.id.progressBar)
        tvError = findViewById(R.id.tvError)
        recyclerView = findViewById(R.id.recyclerView)

        adapter = PoiResultAdapter { poiResult ->
            onPoiSelected(poiResult)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnTextSearch.setOnClickListener {
            performTextSearch()
        }

        btnNearbySearch.setOnClickListener {
            performNearbySearch()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                performTextSearch()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })
    }

    private fun performTextSearch() {
        val query = searchView.query.toString().trim()
        if (query.isEmpty()) {
            Toast.makeText(this, "请输入搜索关键词", Toast.LENGTH_SHORT).show()
            return
        }
        executeSearch { poiSearch ->
            poiSearch.searchByText(query)
        }
    }

    private fun performNearbySearch() {
        val center = centerLatLng
        if (center == null || (center.latitude == 0.0 && center.longitude == 0.0)) {
            Toast.makeText(this, "地图中心未知，无法搜附近", Toast.LENGTH_SHORT).show()
            return
        }
        val query = searchView.query.toString().trim()
        executeSearch { poiSearch ->
            poiSearch.searchNearby(
                query = query.takeIf { it.isNotEmpty() },
                center = center,
                radius = 5000.0
            )
        }
    }

    private fun executeSearch(
        searchBlock: suspend (com.seraphim.core.map.commons.search.PoiSearch) -> SearchResult
    ) {
        val factory = MapProviderRegistry.instance.get(MapInitializer.activeProvider)
            ?: run {
                showError("Map provider not available")
                return
            }
        val poiSearch = factory.createPoiSearch(this)

        showLoading(true)
        tvError.visibility = View.GONE

        lifecycleScope.launch {
            val result = searchBlock(poiSearch)
            showLoading(false)
            when (result) {
                is SearchResult.Success -> {
                    if (result.results.isEmpty()) {
                        showError("未找到结果")
                    } else {
                        adapter.submitList(result.results)
                    }
                }

                is SearchResult.Error -> {
                    showError(result.message)
                }
            }
        }
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        recyclerView.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    private fun onPoiSelected(poiResult: PoiResult) {
        val intent = Intent().apply {
            putExtra(EXTRA_RESULT_LAT, poiResult.latLng.latitude)
            putExtra(EXTRA_RESULT_LNG, poiResult.latLng.longitude)
            putExtra(EXTRA_RESULT_NAME, poiResult.name)
        }
        setResult(RESULT_OK, intent)
        finish()
    }

    class PoiResultAdapter(
        private val onItemClick: (PoiResult) -> Unit
    ) : RecyclerView.Adapter<PoiResultAdapter.ViewHolder>() {

        private var items: List<PoiResult> = emptyList()

        fun submitList(newItems: List<PoiResult>) {
            items = newItems
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_poi_result, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val tvName: TextView = itemView.findViewById(R.id.tvName)
            private val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
            private val tvDistance: TextView = itemView.findViewById(R.id.tvDistance)

            fun bind(item: PoiResult) {
                tvName.text = item.name
                tvAddress.text = item.address ?: ""
                tvAddress.visibility = if (item.address.isNullOrEmpty()) View.GONE else View.VISIBLE
                tvDistance.text =
                    item.distance?.let { "${String.format("%.1f", it / 1000)} km" } ?: ""
                tvDistance.visibility = if (item.distance != null) View.VISIBLE else View.GONE
                itemView.setOnClickListener { onItemClick(item) }
            }
        }
    }
}
