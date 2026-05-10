package com.seraphim.app.mapdemo

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.SupportMapFragment
import com.seraphim.core.map.commons.MapInstance
import com.seraphim.core.map.commons.MapOptions
import com.seraphim.core.map.commons.MapStyle
import com.seraphim.core.map.commons.model.CameraState
import com.seraphim.core.map.commons.model.CircleOptions
import com.seraphim.core.map.commons.model.IconProvider
import com.seraphim.core.map.commons.InitialCamera
import com.seraphim.core.map.commons.model.LatLng
import com.seraphim.core.map.commons.model.MapType
import com.seraphim.core.map.commons.model.MarkerOptions
import com.seraphim.core.map.commons.model.PolygonOptions
import com.seraphim.core.map.commons.model.PolylineOptions
import com.seraphim.core.map.commons.registry.MapInstanceFactory
import com.seraphim.core.map.commons.registry.MapProviderRegistry
import com.seraphim.core.map.google.GoogleMapHost
import com.seraphim.core.map.google.GoogleMapInstanceFactory
import com.seraphim.core.map.google.GoogleClusterableMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Demo app showcasing the unified map abstraction layer.
 *
 * Demonstrates:
 * - Provider registration and MapInstance creation
 * - Markers (with click events)
 * - Polylines, Polygons, Circles
 * - Camera control (moveTo, animateTo)
 * - Map type switching
 * - Map events (tap, long-press, camera change)
 * - Clustering (ClusterableMap)
 */
class MainActivity : AppCompatActivity() {

    private lateinit var map: MapInstance
    private lateinit var host: GoogleMapHost
    private val scope = CoroutineScope(Dispatchers.Main)
    private var markerCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(android.R.id.content)

        // 1. Register provider
        val registry = MapProviderRegistry()
        registry.register(GoogleMapInstanceFactory())

        // 2. Create MapHost from SupportMapFragment
        val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, mapFragment)
            .commitNow()

        host = GoogleMapHost(mapFragment)

        // 3. Create MapInstance with options
        val factory = registry.get("google")
        map = factory.createMapInstance(
            this,
            MapOptions(
                initialCamera = InitialCamera.Position(
                    LatLng(37.5665, 126.9780), // Seoul
                    zoom = 14f
                ),
                mapType = MapType.NORMAL,
                style = MapStyle.Default
            )
        )

        // 4. Initialize map
        scope.launch {
            map.init(
                host, MapOptions(
                    initialCamera = InitialCamera.Position(LatLng(37.5665, 126.9780), 14f)
                )
            )
            setupDemo()
        }
    }

    private suspend fun setupDemo() {
        // ── Demo 1: Markers ──
        addDemoMarkers()

        // ── Demo 2: Polylines ──
        addDemoPolyline()

        // ── Demo 3: Polygon ──
        addDemoPolygon()

        // ── Demo 4: Circle ──
        addDemoCircle()

        // ── Demo 5: Events ──
        setupEvents()

        // ── Demo 6: Clustering ──
        // addDemoClustering()

        // ── Demo 7: Camera animation ──
        runDelayed(3000) {
            map.camera.animateTo(
                LatLng(37.5665, 126.9850),
                zoom = 15f,
                durationMs = 2000
            )
        }
    }

    private fun addDemoMarkers() {
        // Standard marker at Seoul City Hall
        map.addMarker(
            MarkerOptions(
                position = LatLng(37.5663, 126.9779),
                title = "Seoul City Hall",
                snippet = "Government building",
                icon = IconProvider.Default
            )
        )

        // Marker at Gyeongbokgung Palace
        map.addMarker(
            MarkerOptions(
                position = LatLng(37.5796, 126.9770),
                title = "Gyeongbokgung Palace",
                snippet = "Historic palace",
                icon = IconProvider.Default
            )
        )

        // Marker at Namsan Tower
        map.addMarker(
            MarkerOptions(
                position = LatLng(37.5512, 126.9882),
                title = "N Seoul Tower",
                snippet = "Landmark observation tower"
            )
        )

        markerCount = 3
        toast("Added $markerCount markers")
    }

    private fun addDemoPolyline() {
        val points = listOf(
            LatLng(37.5663, 126.9779), // City Hall
            LatLng(37.5665, 126.9780), // Center
            LatLng(37.5796, 126.9770), // Gyeongbokgung
            LatLng(37.5759, 126.9768)  // Gwanghwamun
        )
        map.addPolyline(
            PolylineOptions(
                points = points,
                color = Color.BLUE,
                width = 8f
            )
        )
    }

    private fun addDemoPolygon() {
        val points = listOf(
            LatLng(37.5610, 126.9750),
            LatLng(37.5610, 126.9850),
            LatLng(37.5650, 126.9850),
            LatLng(37.5650, 126.9750)
        )
        map.addPolygon(
            PolygonOptions(
                points = points,
                fillColor = Color.argb(80, 255, 0, 0),
                strokeColor = Color.RED,
                strokeWidth = 4f
            )
        )
    }

    private fun addDemoCircle() {
        map.addCircle(
            CircleOptions(
                center = LatLng(37.5665, 126.9780),
                radius = 500.0,
                fillColor = Color.argb(50, 0, 255, 0),
                strokeColor = Color.GREEN,
                strokeWidth = 3f
            )
        )
    }

    private fun setupEvents() {
        // Map tap: add a marker at tapped location
        map.onMapClick = { location ->
            markerCount++
            map.addMarker(
                MarkerOptions(
                    position = location,
                    title = "Point #$markerCount",
                    snippet = "${location.latitude}, ${location.longitude}"
                )
            )
            toast("Marker added at tap point")
        }

        // Map long-press: show coordinates
        map.onMapLongClick = { location ->
            toast("Long press: %.4f, %.4f".format(location.latitude, location.longitude))
        }

        // Marker click
        map.onMarkerClick = { markerId ->
            toast("Marker clicked: $markerId")
            true // consume event
        }

        // Camera change
        map.onCameraChange = { state ->
            when (state) {
                is CameraState.Idle -> Log.d(TAG, "Camera idle")
                is CameraState.Moving -> Log.d(TAG, "Camera moving: zoom=${state.position.zoom}")
                is CameraState.Started -> Log.d(TAG, "Camera started: ${state.reason}")
            }
        }

        // Double-tap to toggle map type
        var lastTapTime = 0L
        val originalTapHandler = map.onMapClick
        map.onMapClick = { location ->
            val now = System.currentTimeMillis()
            if (now - lastTapTime < 300) {
                // Double-tap: toggle map type
                val newType = when (map.mapType) {
                    MapType.NORMAL -> MapType.SATELLITE
                    MapType.SATELLITE -> MapType.HYBRID
                    else -> MapType.NORMAL
                }
                map.mapType = newType
                toast("Map type: $newType")
            } else {
                originalTapHandler?.invoke(location)
            }
            lastTapTime = now
        }
    }

    private fun toast(msg: String) {
        runOnUiThread { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }
    }

    private fun runDelayed(delayMs: Long, block: () -> Unit) {
        scope.launch {
            kotlinx.coroutines.delay(delayMs)
            block()
        }
    }

    override fun onResume() {
        super.onResume()
        host.onResume()
    }

    override fun onPause() {
        super.onPause()
        host.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        host.onDestroy()
    }

    companion object {
        private const val TAG = "MapDemo"
    }
}
