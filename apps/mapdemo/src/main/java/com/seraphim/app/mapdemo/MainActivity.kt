package com.seraphim.app.mapdemo

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.seraphim.core.map.commons.MapFragment
import com.seraphim.core.map.commons.model.CameraState
import com.seraphim.core.map.commons.model.CircleOptions
import com.seraphim.core.map.commons.model.LatLng
import com.seraphim.core.map.commons.model.MarkerOptions
import com.seraphim.core.map.commons.model.PolygonOptions
import com.seraphim.core.map.commons.model.PolylineOptions
import com.seraphim.core.map.commons.registry.MapProviderRegistry
import com.seraphim.core.map.google.GoogleMapInstanceFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Demo app using [MapFragment] — the provider-agnostic map container.
 *
 * Demonstrates: markers, polylines, polygons, circles, camera control,
 * map type switching, events, and provider switching.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var mapFragment: MapFragment
    private lateinit var registry: MapProviderRegistry
    private val scope = CoroutineScope(Dispatchers.Main)
    private var markerCount = 0
    private var lastTapTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Register providers
        registry = MapProviderRegistry()
        registry.register(GoogleMapInstanceFactory())

        // 2. Create MapFragment — automatically manages host lifecycle
        mapFragment = MapFragment().apply {
            initialize(registry, "google")
        }

        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, mapFragment)
            .commit()

        // 3. Wait for map to be ready, then setup demo
        scope.launch {
            val map = mapFragment.getMap()
            setupDemo(map)
        }
    }

    private suspend fun setupDemo(map: com.seraphim.core.map.commons.MapInstance) {
        // Apply initial camera
        map.camera.moveTo(LatLng(37.5665, 126.9780), 14f)

        // ── Markers ──
        map.addMarker(MarkerOptions(LatLng(37.5663, 126.9779), "Seoul City Hall", "Government"))
        map.addMarker(MarkerOptions(LatLng(37.5796, 126.9770), "Gyeongbokgung", "Historic palace"))
        map.addMarker(MarkerOptions(LatLng(37.5512, 126.9882), "N Seoul Tower", "Landmark"))
        markerCount = 3
        toast("3 markers added")

        // ── Polyline ──
        map.addPolyline(
            PolylineOptions(
                points = listOf(
                    LatLng(37.5663, 126.9779),
                    LatLng(37.5665, 126.9780),
                    LatLng(37.5796, 126.9770),
                    LatLng(37.5759, 126.9768)
                ),
                color = Color.BLUE, width = 8f
            )
        )

        // ── Polygon ──
        map.addPolygon(
            PolygonOptions(
                points = listOf(
                    LatLng(37.5610, 126.9750),
                    LatLng(37.5610, 126.9850),
                    LatLng(37.5650, 126.9850),
                    LatLng(37.5650, 126.9750)
                ),
                fillColor = Color.argb(80, 255, 0, 0),
                strokeColor = Color.RED, strokeWidth = 4f
            )
        )

        // ── Circle ──
        map.addCircle(
            CircleOptions(
                center = LatLng(37.5665, 126.9780),
                radius = 500.0,
                fillColor = Color.argb(50, 0, 255, 0),
                strokeColor = Color.GREEN, strokeWidth = 3f
            )
        )

        // ── Events ──
        map.onMapClick = { loc ->
            markerCount++
            map.addMarker(MarkerOptions(loc, "Point #$markerCount", "tap"))
        }
        map.onMapLongClick = { loc ->
            toast("%.4f, %.4f".format(loc.latitude, loc.longitude))
        }
        map.onMarkerClick = { id -> toast("Marker: $id"); true }
        map.onCameraChange = { state ->
            when (state) {
                is CameraState.Idle -> Log.d(TAG, "Camera idle")
                is CameraState.Moving -> Log.d(
                    TAG,
                    "Camera moving z=%.1f".format(state.position.zoom)
                )
                is CameraState.Started -> Log.d(TAG, "Camera started: ${state.reason}")
            }
        }

        // ── Camera animation ──
        delay(3000)
        map.camera.animateTo(LatLng(37.5665, 126.9850), zoom = 16f, durationMs = 2000)
    }

    private fun toast(msg: String) {
        runOnUiThread { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }
    }

    companion object {
        private const val TAG = "MapDemo"
    }
}
