package com.seraphim.app.mapdemo

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.google.android.material.chip.Chip
import com.seraphim.core.map.commons.MapFragment
import com.seraphim.core.map.commons.MapInitializer
import com.seraphim.core.map.commons.model.CameraState
import com.seraphim.core.map.commons.model.CircleOptions
import com.seraphim.core.map.commons.model.LatLng
import com.seraphim.core.map.commons.model.MapType
import com.seraphim.core.map.commons.model.MarkerOptions
import com.seraphim.core.map.commons.model.PolygonOptions
import com.seraphim.core.map.commons.model.PolylineOptions
import com.seraphim.core.map.commons.registry.MapProviderRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var mapFragment: MapFragment
    private lateinit var map: com.seraphim.core.map.commons.MapInstance
    private val scope = CoroutineScope(Dispatchers.Main)
    private var markerCount = 0

    // AMap location
    private var locationClient: AMapLocationClient? = null
    private var locationMarkerId: String? = null
    private var isLocating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapFragment = MapFragment().apply {
            initialize(MapProviderRegistry.instance, MapInitializer.activeProvider)
        }
        supportFragmentManager.beginTransaction()
            .add(R.id.map_container, mapFragment)
            .commit()

        setupButtons()
        scope.launch {
            map = mapFragment.getMap()
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
    }

    // ── Location ──

    private fun toggleLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
            )
            return
        }

        if (isLocating) {
            stopLocation()
        } else {
            startLocation()
        }
    }

    private fun startLocation() {
        isLocating = true
        map.enableUserLocation(true)

        locationClient?.stopLocation()
        locationClient = AMapLocationClient(this).apply {
            setLocationOption(AMapLocationClientOption().apply {
                locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
                isOnceLocation = true
                isNeedAddress = true
            })
            setLocationListener(object : AMapLocationListener {
                override fun onLocationChanged(loc: AMapLocation?) {
                    if (loc == null || loc.errorCode != 0) {
                        toast("定位失败: ${loc?.errorInfo}")
                        isLocating = false
                        return
                    }
                    val latLng = LatLng(loc.latitude, loc.longitude)
                    // Move camera to user location
                    map.camera.animateTo(latLng, zoom = 16f, durationMs = 1000)

                    // Remove old location marker
                    locationMarkerId?.let { map.removeMarkerById(it) }

                    // Add location marker
                    val marker = map.addMarker(
                        MarkerOptions(
                            latLng,
                            "📍 我的位置",
                            "精度: ${"%.0f".format(loc.accuracy)}m\n${loc.address ?: ""}"
                        )
                    )
                    locationMarkerId = marker.id

                    toast("定位成功: ${"%.4f".format(loc.latitude)}, ${"%.4f".format(loc.longitude)}")
                    isLocating = false
                    stopLocation()
                }
            })
            startLocation()
        }
    }

    private fun stopLocation() {
        locationClient?.stopLocation()
        locationClient?.onDestroy()
        locationClient = null
        isLocating = false
    }

    // ── Button actions ──

    private var mapTypeIndex = 0
    private fun cycleMapType() {
        val types = arrayOf(MapType.NORMAL, MapType.SATELLITE, MapType.NORMAL)
        mapTypeIndex = (mapTypeIndex + 1) % types.size
        map.mapType = types[mapTypeIndex]
        toast("图层: ${types[mapTypeIndex]}")
    }

    private fun addRandomMarker() {
        val center = map.camera.current.target
        val lat = center.latitude + (Math.random() - 0.5) * 0.02
        val lng = center.longitude + (Math.random() - 0.5) * 0.02
        markerCount++
        map.addMarker(MarkerOptions(LatLng(lat, lng), "标记 #$markerCount", "手动添加"))
        toast("已添加 #$markerCount")
    }

    private fun clearMap() {
        map.clearAll()
        locationMarkerId = null
        markerCount = 0
        toast("已清除")
    }

    private fun animateCamera() {
        val c = map.camera.current.target
        val lat = c.latitude + (Math.random() - 0.5) * 0.03
        val lng = c.longitude + (Math.random() - 0.5) * 0.03
        map.camera.animateTo(LatLng(lat, lng), zoom = 16f, durationMs = 1500)
    }

    private var providerIndex = 0
    private fun cycleProvider() {
        val providers = listOf("amap", "tmap", "yandex", "google")
        providerIndex = (providerIndex + 1) % providers.size
        val next = providers[providerIndex]
        toast("切换到 $next ...")
        mapFragment.switchProvider(next)
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

    // ── Demo setup ──

    private suspend fun setupDemo(map: com.seraphim.core.map.commons.MapInstance) {
        val data = DemoData.forProvider(MapInitializer.activeProvider)
        map.camera.moveTo(data.center, 14f)

        data.markers.forEach { m ->
            map.addMarker(MarkerOptions(m.pos, m.title, m.snippet))
        }
        markerCount = data.markers.size

        if (data.markers.size >= 2) {
            map.addPolyline(PolylineOptions(data.markers.map { it.pos }, Color.BLUE, 8f))
        }
        map.addPolygon(
            PolygonOptions(
                data.polygonPoints,
                Color.argb(80, 255, 0, 0), Color.RED, 4f
            )
        )
        map.addCircle(
            CircleOptions(
                data.center, 500.0,
                Color.argb(50, 0, 255, 0), Color.GREEN, 3f
            )
        )

        map.onMapClick = { loc ->
            markerCount++
            map.addMarker(MarkerOptions(loc, "点 #$markerCount", "点击"))
        }
        map.onMapLongClick = { loc ->
            toast("%.4f, %.4f".format(loc.latitude, loc.longitude))
        }
        map.onMarkerClick = { id -> toast("Marker: $id"); true }
        map.onCameraChange = { state ->
            when (state) {
                is CameraState.Idle -> Log.d(TAG, "Camera idle")
                is CameraState.Moving -> Log.d(TAG, "Camera moving")
                is CameraState.Started -> Log.d(TAG, "Camera started: ${state.reason}")
            }
        }
        toast("Demo 就绪 — ${data.markers.size} 个地标")
    }

    override fun onRequestPermissionsResult(code: Int, perms: Array<String>, results: IntArray) {
        super.onRequestPermissionsResult(code, perms, results)
        if (code == 1 && results.isNotEmpty() && results[0] == PackageManager.PERMISSION_GRANTED) {
            startLocation()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocation()
    }

    private fun toast(msg: String) {
        runOnUiThread { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }
    }

    companion object {
        private const val TAG = "MapDemo"
    }
}

private data class DemoData(
    val center: LatLng, val markers: List<MarkerData>, val polygonPoints: List<LatLng>
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
                    LatLng(39.9100, 116.4020),
                    LatLng(39.9100, 116.4120),
                    LatLng(39.9000, 116.4120),
                    LatLng(39.9000, 116.4020)
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
                    LatLng(37.5610, 126.9750),
                    LatLng(37.5610, 126.9850),
                    LatLng(37.5650, 126.9850),
                    LatLng(37.5650, 126.9750)
                )
            )

            "yandex" -> DemoData(
                LatLng(55.7558, 37.6173),
                listOf(
                    MarkerData(LatLng(55.7520, 37.6176), "Red Square", "Historic plaza"),
                    MarkerData(LatLng(55.7512, 37.6203), "St. Basil's", "Famous cathedral"),
                    MarkerData(LatLng(55.7524, 37.6231), "GUM", "Department store"),
                ),
                listOf(
                    LatLng(55.7560, 37.6150),
                    LatLng(55.7560, 37.6220),
                    LatLng(55.7510, 37.6220),
                    LatLng(55.7510, 37.6150)
                )
            )

            else -> DemoData(
                LatLng(39.9042, 116.4074),
                listOf(
                    MarkerData(LatLng(39.9163, 116.3972), "故宫", "紫禁城"),
                    MarkerData(LatLng(39.9042, 116.4074), "天安门广场", "北京市中心"),
                ),
                listOf(
                    LatLng(39.9100, 116.4020),
                    LatLng(39.9100, 116.4120),
                    LatLng(39.9000, 116.4120),
                    LatLng(39.9000, 116.4020)
                )
            )
        }
    }
}
