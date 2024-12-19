package com.permata.mystoryyapp.ui.location

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.permata.mystoryyapp.R
import com.permata.mystoryyapp.network.response.LocationResponse

@Suppress("DEPRECATION")
class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var sharedPreferences: SharedPreferences
    private val boundsBuilder = LatLngBounds.Builder()
    private lateinit var progressBar: ProgressBar

    private val mapsViewModel: MapsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false).apply {
            progressBar = findViewById(R.id.progressBar)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        setHasOptionsMenu(true)

        mapsViewModel.locationData.observe(viewLifecycleOwner) { locationResponse ->
            if (isAdded) {
                locationResponse?.let {
                    progressBar.visibility = View.GONE
                    addManyMarkers(it)
                }
            }
        }

        mapsViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (isAdded) {
                errorMessage?.let {
                    progressBar.visibility = View.GONE
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        mapsViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isAdded) {
                progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        progressBar.visibility = View.VISIBLE
        getStoriesWithLocation()
    }

    override fun onPause() {
        super.onPause()
        progressBar.visibility = View.GONE
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getStoriesWithLocation()
        getMyLocation()
        setMapStyle()

    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }


    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun getStoriesWithLocation() {
        val token = sharedPreferences.getString("TOKEN", null)
        if (token == null) {
            val message = getString(R.string.notoken)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            return
        }

        mapsViewModel.fetchStoriesWithLocation(token)
    }


    private fun addManyMarkers(locationResponse: LocationResponse) {
        if (::mMap.isInitialized) {
            val storyLocations = locationResponse.listStory

            storyLocations.forEach { story ->
                val latLng = LatLng(story.latitude, story.longitude)
                val markerOptions = MarkerOptions()
                    .position(latLng)
                    .title(story.name)
                    .snippet(story.description)

                mMap.addMarker(markerOptions)
                boundsBuilder.include(latLng)
            }

            if (storyLocations.isNotEmpty()) {
                val bounds = boundsBuilder.build()
                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        bounds,
                        resources.displayMetrics.widthPixels,
                        resources.displayMetrics.heightPixels,
                        300
                    )
                )
            } else {
                Toast.makeText(context, getString(R.string.no_location_found), Toast.LENGTH_SHORT).show()
            }

            mMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
                override fun getInfoWindow(marker: Marker): View? {
                    return null
                }

                @SuppressLint("InflateParams")
                override fun getInfoContents(marker: Marker): View {
                    val view = layoutInflater.inflate(R.layout.info_window_layout, null)

                    val storyId = marker.snippet
                    val storyName = marker.title

                    val nameTextView = view.findViewById<TextView>(R.id.story_name)
                    val descriptionTextView = view.findViewById<TextView>(R.id.story_description)

                    nameTextView.text = storyName
                    descriptionTextView.text = storyId

                    return view
                }
            })

            mMap.setOnMarkerClickListener { marker ->
                marker.showInfoWindow()
                true
            }
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_option, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }

            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }

            R.id.terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }

            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}
