package nibm.project.weatherpoint

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentContainerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import nibm.project.weatherpoint.databinding.MainScreenBinding
import java.io.IOException

class MainScreen : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap //googleMap provided by android API represents object mMap
    private lateinit var binding: MainScreenBinding //used for data binding

    //for expandable content
    private lateinit var expandableContent : CardView
    private lateinit var constantLayout : LinearLayout
    private lateinit var cvExpand : CardView
    private lateinit var rlContent : RelativeLayout
    private lateinit var imgMenuButton : ImageView
    private lateinit var imgHomeButton : ImageButton
    private lateinit var imgForecastButton : ImageButton
    private lateinit var imgWeatherButton : ImageButton
    private lateinit var imgLocationButton : ImageButton

    var searchView: SearchView? = null
    var clickedButton: View? = null //to manage navigation bar btn clicks


    //---------LocationVariables----------
    lateinit var locationRequest : LocationRequest
    val locationClient : FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this) //Computed properties
    }
    var currentLocation : Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //declaring views
        expandableContent = findViewById(R.id.expandable_card)
        constantLayout = findViewById(R.id.ll_constant)
        cvExpand = findViewById(R.id.cv_expand)
        rlContent = findViewById(R.id.rl_content)
        imgMenuButton = findViewById(R.id.img_btn_menu_out)
        imgHomeButton = findViewById(R.id.img_btn_home)
        imgForecastButton = findViewById(R.id.img_btn_forecast)
        imgWeatherButton = findViewById(R.id.img_btn_weather)
        imgLocationButton = findViewById(R.id.img_btn_location)

        clickedButton = imgHomeButton //initial button background
        btnOnClick()

        //setting initial image
        imgMenuButton.setBackgroundResource(R.drawable.madmenuout)

        //declaring search bar
        searchView = findViewById(R.id.idSearchView)

        createMap() //creates the map with a marker at beruwala

        //when user submits a search
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchInMap()
                createMap() //to sync the map with the seach location
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        //when user click location details
        cvExpand.setOnClickListener {
            toggleWeatherContent()
        }

        //navigation
        imgHomeButton.setOnClickListener(){
            clickedButton = imgHomeButton
            btnOnClick()
        }

        imgForecastButton.setOnClickListener(){
            clickedButton = imgForecastButton
            btnOnClick()
            startActivity(Intent(this, ForecastScreen::class.java))
        }

        imgWeatherButton.setOnClickListener(){
            clickedButton = imgWeatherButton
            btnOnClick()
        }

        imgLocationButton.setOnClickListener(){
            clickedButton = imgLocationButton
            btnOnClick()
        }

        // current location   ----------------------------------------------------------------------------------------------
        checkPermission()
    }

    fun checkPermission(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        { //to execute if the location is already granted by the user.
            accessLocation()

        }
        else{ //system will request for location permission
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),100) //array for multiple permission requests, request code can be used for verification
        }
    }

    @SuppressLint("MissingPermission")
    fun accessLocation() {
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100).build() //the lower the requesting frequency the higher the battery drainage
        val locationCallBack = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                p0.locations.lastOrNull()?.let { location ->
                    currentLocation?.latitude = location.latitude
                    currentLocation?.longitude = location.longitude


                }
            }
        }
        locationClient.requestLocationUpdates(locationRequest, locationCallBack, Looper.getMainLooper())
    }

    //might have to change this when current location is to be displayed
    //creates the map when screen opens
    private fun createMap(){
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    //searchs for the location when user submits a search
    private fun searchInMap() {
        // on below line we are getting the location name from search view.
        val location = searchView?.getQuery().toString()
        // below line is to create a list of address where we will store the list of all address.
        var addressList: List<Address>? = null

        // checking if the entered location is null or not.
        if (location != null || location == "") {
            // on below line we are creating and initializing a geo coder.
            val geocoder = Geocoder(this@MainScreen)
            try {
                // on below line we are getting location from the location name and adding that location to address list.
                addressList = geocoder.getFromLocationName(location, 1)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            //getting the location from our list at first position.
            val address = addressList!![0]

            //search locations latitude and longitude.
            val latLng = LatLng(address.latitude, address.longitude)

            mMap?.clear()//remove existing markers

            //adding marker to that position.
            mMap!!.addMarker(MarkerOptions().position(latLng).title(location))

            // below line is to animate camera to that position.
            mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8f))
        }
    }

    //opening and closing fragment
    private fun toggleWeatherContent(){
        val isVisible = expandableContent.visibility == View.VISIBLE

        if (!isVisible) {
            // Slide in animation
            expandableContent.visibility = View.VISIBLE
            expandableContent.post {
                expandableContent.translationX = expandableContent.width.toFloat()
                expandableContent.animate()
                    .translationX(0f)
                    .setDuration(500)
                    .start()
            }
        } else {
            // Slide out animation
            expandableContent.animate()
                .translationX(expandableContent.width.toFloat())
                .setDuration(500)
                .withEndAction {
                    expandableContent.visibility = View.GONE
                }
                .start()
        }
    }

    private fun btnOnClick(){

        if(clickedButton == imgHomeButton) {
            imgHomeButton.setBackgroundColor(ContextCompat.getColor(this,R.color.btn_focused))
            imgForecastButton.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_unfocused))
            imgWeatherButton.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_unfocused))
            imgLocationButton.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_unfocused))
        }
        else if(clickedButton == imgForecastButton) {
            imgHomeButton.setBackgroundColor(ContextCompat.getColor(this,R.color.btn_unfocused))
            imgForecastButton.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_focused))
            imgWeatherButton.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_unfocused))
            imgLocationButton.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_unfocused))
        }
        else if(clickedButton == imgWeatherButton) {
            imgHomeButton.setBackgroundColor(ContextCompat.getColor(this,R.color.btn_unfocused))
            imgForecastButton.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_unfocused))
            imgWeatherButton.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_focused))
            imgLocationButton.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_unfocused))
        }
        else if(clickedButton == imgLocationButton) {
            imgHomeButton.setBackgroundColor(ContextCompat.getColor(this,R.color.btn_unfocused))
            imgForecastButton.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_unfocused))
            imgWeatherButton.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_unfocused))
            imgLocationButton.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_focused))
        }
    }

    //initializes a marker in a location
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap //googleMap object

        mMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {

            override fun onMarkerDragStart(marker: Marker) {}
            override fun onMarkerDrag(marker: Marker) {
            }

            override fun onMarkerDragEnd(marker: Marker) {
                //get weather details and display
            }
        })
    }
}