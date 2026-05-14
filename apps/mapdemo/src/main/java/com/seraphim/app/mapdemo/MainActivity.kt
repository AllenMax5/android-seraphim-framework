package com.seraphim.app.mapdemo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.seraphim.core.map.commons.MapFragment
import com.seraphim.core.map.commons.MapInitializer
import com.seraphim.core.map.commons.location.LocationResult
import com.seraphim.core.map.commons.model.CameraState
import com.seraphim.core.map.commons.model.CircleOptions
import com.seraphim.core.map.commons.model.LatLng
import com.seraphim.core.map.commons.model.MapType
import com.seraphim.core.map.commons.model.MarkerOptions
import com.seraphim.core.map.commons.model.PolygonOptions
import com.seraphim.core.map.commons.model.PolylineOptions
import com.seraphim.core.map.commons.registry.MapProviderRegistry
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var mapFragment: MapFragment
    private lateinit var map: com.seraphim.core.map.commons.MapInstance
    private var markerCount = 0
    private var locationMarkerId: String? = null
    private var isLocating = false
    private lateinit var locationProvider: com.seraphim.core.map.commons.location.UserLocationProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapFragment = MapFragment.create(MapInitializer.activeProvider)
        supportFragmentManager.beginTransaction()
            .add(R.id.map_container, mapFragment)
            .commit()

        setupButtons()
        lifecycleScope.launch {
            map = mapFragment.awaitMap()
            setupDemo(map)
        }
    }

    private fun setupButtons() {
        findViewById<Chip>(R.id.btn_location).setOnClickListener { toggleLocation() }
        findViewById<Chip>(R.id.btn_maptype).setOnClickListener { cycleMapType() }
        findViewById<Chip>(R.id.btn_marker).setOnClickListener { addRandomMarker() }
        findViewById<Chip>(R.id.btn_clear).setOnClickListener { clearMap() }
        findViewById<Chip>(R.id.btn_zoom_in).setOnClickListener { map.camera.zoomIn() }
        findViewById<Chip>(R.id.btn_zoom_out).setOnClickListener { map.camera.zoomOut() }
        findViewById<Chip>(R.id.btn_animate).setOnClickListener { animateCamera() }
        findViewById<Chip>(R.id.btn_switch).setOnClickListener { cycleProvider() }
        findViewById<Chip>(R.id.btn_info).setOnClickListener { showInfo() }
        findViewById<Chip>(R.id.btn_search).setOnClickListener { openSearch() }
    }

    private fun toggleLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }
        if (isLocating) stopLocation() else startLocation()
    }

    private fun startLocation() {
        if (!::map.isInitialized) return
        if (isLocating) return
        isLocating = true
        map.enableUserLocation(true)

        val factory = MapProviderRegistry.instance.get(MapInitializer.activeProvider)
        locationProvider = factory.createUserLocationProvider(this)

        lifecycleScope.launch {
            when (val result = locationProvider.requestSingleLocation(timeoutMs = 15000)) {
                is LocationResult.Success -> {
                    val pos = result.position
                    val latLng = pos.location
                    map.camera.animateTo(latLng, zoom = 16f, durationMs = 1000)
                    locationMarkerId?.let { map.removeMarkerById(it) }
                    val marker = map.addMarker(
                        MarkerOptions(
                            latLng,
                            "📍 我的位置",
                            "精度: ${"%.0f".format(pos.accuracy)}m"
                        )
                    )
                    locationMarkerId = marker.id
                    toast("定位成功: ${"%.4f".format(latLng.latitude)}, ${"%.4f".format(latLng.longitude)}")
                }

                is LocationResult.PermissionDenied -> {
                    toast("定位权限被拒绝")
                }

                is LocationResult.LocationDisabled -> {
                    toast("定位服务未开启")
                }

                is LocationResult.Timeout -> {
                    toast("定位超时")
                }
            }
            isLocating = false
        }
    }

    private fun stopLocation() {
        // requestSingleLocation is one-shot; no ongoing updates to stop.
        // Just reset the UI state if needed.
        isLocating = false
    }

    private var mapTypeIndex = 0
    private fun cycleMapType() {
        val types = arrayOf(MapType.NORMAL, MapType.SATELLITE, MapType.NORMAL)
        mapTypeIndex = (mapTypeIndex + 1) % types.size
        // MapType now set via MapStyle.Type in MapOptions
        // map.camera.mapType is removed; use updateUiSettings or re-init for map type changes
        toast("图层切换: ${types[mapTypeIndex]} (需通过 MapOptions 重新初始化)")
    }

    private fun addRandomMarker() {
        val c = map.camera.current.target
        val lat = c.latitude + (Math.random() - 0.5) * 0.02
        val lng = c.longitude + (Math.random() - 0.5) * 0.02
        markerCount++; map.addMarker(
            MarkerOptions(
                LatLng(lat, lng),
                "标记 #$markerCount",
                "手动添加"
            )
        )
        toast("已添加 #$markerCount")
    }

    private fun clearMap() {
        map.clearAll(); locationMarkerId = null; markerCount = 0; toast("已清除")
    }

    private fun animateCamera() {
        val c = map.camera.current.target
        map.camera.animateTo(
            LatLng(
                c.latitude + (Math.random() - 0.5) * 0.03,
                c.longitude + (Math.random() - 0.5) * 0.03
            ), zoom = 16f, durationMs = 1500
        )
    }

    private var providerIndex = 0
    private fun cycleProvider() {
        val providers = listOf("amap", "tmap", "yandex", "google", "here")
        providerIndex = (providerIndex + 1) % providers.size
        toast("切换到 ${providers[providerIndex]} ...")
        mapFragment.switchProvider(providers[providerIndex])
    }

    private fun showInfo() {
        val c = map.camera.current
        toast(
            "${MapInitializer.activeProvider} | z=${"%.1f".format(c.zoom)} | ${"%.4f".format(c.target.latitude)},${
                "%.4f".format(
                    c.target.longitude
                )
            }"
        )
    }

    private fun openSearch() {
        if (!::map.isInitialized) return
        val c = map.camera.current.target
        val intent = Intent(this, SearchActivity::class.java).apply {
            putExtra(SearchActivity.EXTRA_CENTER_LAT, c.latitude)
            putExtra(SearchActivity.EXTRA_CENTER_LNG, c.longitude)
        }
        startActivityForResult(intent, SEARCH_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SEARCH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val lat = data.getDoubleExtra(SearchActivity.EXTRA_RESULT_LAT, 0.0)
            val lng = data.getDoubleExtra(SearchActivity.EXTRA_RESULT_LNG, 0.0)
            val name = data.getStringExtra(SearchActivity.EXTRA_RESULT_NAME) ?: ""
            if (lat != 0.0 || lng != 0.0) {
                val latLng = LatLng(lat, lng)
                map.camera.animateTo(latLng, zoom = 16f, durationMs = 1000)
                markerCount++
                val marker = map.addMarker(MarkerOptions(latLng, name, "搜索结果"))
                toast("已定位到: $name")
            }
        }
    }

    private suspend fun setupDemo(map: com.seraphim.core.map.commons.MapInstance) {
        val data = DemoData.forProvider(MapInitializer.activeProvider)
        map.camera.moveTo(data.center, 14f)
        data.markers.forEach { m -> map.addMarker(MarkerOptions(m.pos, m.title, m.snippet)) }
        markerCount = data.markers.size
        if (data.markers.size >= 2) map.addPolyline(
            PolylineOptions(
                data.markers.map { it.pos },
                Color.BLUE,
                8f
            )
        )
        map.addPolygon(PolygonOptions(data.polygonPoints, Color.argb(80, 255, 0, 0), Color.RED, 4f))
        map.addCircle(CircleOptions(data.center, 500.0, Color.argb(50, 0, 255, 0), Color.GREEN, 3f))
        map.onMapClick =
            { loc -> markerCount++; map.addMarker(MarkerOptions(loc, "点 #$markerCount", "点击")) }
        map.onMapLongClick = { loc -> toast("%.4f, %.4f".format(loc.latitude, loc.longitude)) }
        map.onMarkerClick = { id -> toast("Marker: $id"); true }
        map.onCameraChange = { state ->
            when (state) {
                is CameraState.Idle -> Log.d(TAG, "Camera idle")
                is CameraState.Moving -> Log.d(TAG, "Camera moving")
                is CameraState.Started -> Log.d(TAG, "Camera started")
            }
        }
        toast("Demo 就绪 — ${data.markers.size} 个地标")
    }

    override fun onRequestPermissionsResult(code: Int, perms: Array<String>, results: IntArray) {
        super.onRequestPermissionsResult(code, perms, results)
        if (code == 1 && results.isNotEmpty() && results[0] == PackageManager.PERMISSION_GRANTED) startLocation()
    }

    private fun toast(msg: String) {
        runOnUiThread { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }
    }

    companion object {
        private const val TAG = "MapDemo"
        private const val SEARCH_REQUEST_CODE = 1001
    }
}

private data class DemoData(
    val center: LatLng,
    val markers: List<MarkerData>,
    val polygonPoints: List<LatLng>
) {
    data class MarkerData(val pos: LatLng, val title: String, val snippet: String)
    companion object {
        fun forProvider(id: String) = when (id) {
            "amap" -> DemoData(
                LatLng(39.9042, 116.4074),
                listOf(
                    MarkerData(LatLng(39.9163, 116.3972), "故宫", "紫禁城"),
                    MarkerData(LatLng(39.9042, 116.4074), "天安门广场", "北京市中心"),
                    MarkerData(LatLng(40.0026, 116.3858), "奥林匹克公园", "鸟巢"),
                ),
                listOf(
                    LatLng(39.91, 116.402),
                    LatLng(39.91, 116.412),
                    LatLng(39.90, 116.412),
                    LatLng(39.90, 116.402)
                )
            )

            "tmap" -> DemoData(
                LatLng(37.5665, 126.9780),
                listOf(
                    MarkerData(LatLng(37.5663, 126.9779), "Seoul City Hall", "Government"),
                    MarkerData(LatLng(37.5796, 126.9770), "Gyeongbokgung", "Historic palace"),
                    MarkerData(LatLng(37.5512, 126.9882), "N Seoul Tower", "Landmark"),
                ),
                listOf(
                    LatLng(37.561, 126.975),
                    LatLng(37.561, 126.985),
                    LatLng(37.565, 126.985),
                    LatLng(37.565, 126.975)
                )
            )

            "yandex" -> DemoData(
                LatLng(55.7558, 37.6173),
                listOf(
                    MarkerData(LatLng(55.752, 37.6176), "Red Square", "Historic plaza"),
                    MarkerData(LatLng(55.7512, 37.6203), "St. Basil's", "Cathedral"),
                    MarkerData(LatLng(55.7524, 37.6231), "GUM", "Department store"),
                ),
                listOf(
                    LatLng(55.756, 37.615),
                    LatLng(55.756, 37.622),
                    LatLng(55.751, 37.622),
                    LatLng(55.751, 37.615)
                )
            )

            "here" -> DemoData(
                LatLng(52.5200, 13.4050),
                listOf(
                    MarkerData(LatLng(52.5163, 13.3777), "Brandenburg Gate", "Landmark"),
                    MarkerData(LatLng(52.5206, 13.4094), "TV Tower", "Berlin"),
                    MarkerData(LatLng(52.5186, 13.3762), "Reichstag", "Parliament"),
                ),
                listOf(
                    LatLng(52.522, 13.400),
                    LatLng(52.522, 13.410),
                    LatLng(52.517, 13.410),
                    LatLng(52.517, 13.400)
                )
            )

            "google" -> DemoData(
                LatLng(37.4220, -122.0841),
                listOf(
                    MarkerData(LatLng(37.4220, -122.0841), "Googleplex", "HQ"),
                    MarkerData(LatLng(37.4231, -122.0823), "Android Statues", "Campus"),
                    MarkerData(LatLng(37.4213, -122.0857), "Charleston Park", "Park"),
                ),
                listOf(
                    LatLng(37.424, -122.086),
                    LatLng(37.424, -122.082),
                    LatLng(37.420, -122.082),
                    LatLng(37.420, -122.086)
                )
            )

            else -> DemoData(
                LatLng(39.9042, 116.4074),
                listOf(
                    MarkerData(LatLng(39.9163, 116.3972), "故宫", "紫禁城"),
                    MarkerData(LatLng(39.9042, 116.4074), "天安门", "市中心"),
                ),
                listOf(
                    LatLng(39.91, 116.402),
                    LatLng(39.91, 116.412),
                    LatLng(39.90, 116.412),
                    LatLng(39.90, 116.402)
                )
            )
        }
    }
}
