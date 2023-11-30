package nibm.project.weatherpoint

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.GoogleMap
import com.squareup.picasso.Picasso

class ForecastScreen : AppCompatActivity() {
    private lateinit var mMap: GoogleMap //googleMap provided by android API represents object mMap
    private lateinit var imgHomeButton : ImageButton

    private lateinit var cityName : TextView

    private lateinit var weatherIcon1 : ImageView
    private lateinit var weatherIcon2 : ImageView
    private lateinit var weatherIcon3 : ImageView
    private lateinit var weatherIcon4 : ImageView
    private lateinit var weatherIcon5 : ImageView

    private lateinit var weatherDate1 : TextView
    private lateinit var weatherDate2 : TextView
    private lateinit var weatherDate3 : TextView
    private lateinit var weatherDate4 : TextView
    private lateinit var weatherDate5 : TextView

    private lateinit var forecastDis1 : TextView
    private lateinit var forecastDis2 : TextView
    private lateinit var forecastDis3 : TextView
    private lateinit var forecastDis4 : TextView
    private lateinit var forecastDis5 : TextView

    private lateinit var temperature1 : TextView
    private lateinit var temperature2 : TextView
    private lateinit var temperature3 : TextView
    private lateinit var temperature4 : TextView
    private lateinit var temperature5 : TextView

    var searchView: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forcast_screen)

        searchView = findViewById(R.id.idSearchView)
        imgHomeButton = findViewById(R.id.img_btn_home)

        cityName = findViewById(R.id.cityLable)

        weatherIcon1 = findViewById(R.id.wIcon1)
        weatherIcon2 = findViewById(R.id.wIcon2)
        weatherIcon3 = findViewById(R.id.wIcon3)
        weatherIcon4 = findViewById(R.id.wIcon4)
        weatherIcon5 = findViewById(R.id.wIcon5)

        weatherDate1 = findViewById(R.id.day1)
        weatherDate2 = findViewById(R.id.day2)
        weatherDate3 = findViewById(R.id.day3)
        weatherDate4 = findViewById(R.id.day4)
        weatherDate5 = findViewById(R.id.day5)

        forecastDis1 = findViewById(R.id.forcastDis1)
        forecastDis2 = findViewById(R.id.forcastDis2)
        forecastDis3 = findViewById(R.id.forcastDis3)
        forecastDis4 = findViewById(R.id.forcastDis4)
        forecastDis5 = findViewById(R.id.forcastDis5)


        temperature1 = findViewById(R.id.celcius1)
        temperature2 = findViewById(R.id.celcius2)
        temperature3 = findViewById(R.id.celcius3)
        temperature4 = findViewById(R.id.celcius4)
        temperature5 = findViewById(R.id.celcius5)



        searchView?.getQuery().toString()


        imgHomeButton.setOnClickListener(){
            startActivity(Intent(this, MainScreen::class.java))
        }

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
//                searchInMap()
//                createMap() //to sync the map with the seach location
                loadForecast(searchView?.query.toString())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    fun loadForecast(city : String)
    {
        val url = "https://api.openweathermap.org/data/2.5/forecast?q=$city&appid=d13fd974b477f803c617fd941cd666aa"
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { data->
                Log.e("Response", data.toString())

                try{
                    cityName.text = data.getJSONObject("city").getString("name")

//                    val jsonArray = data.getJSONArray("list")
//                    val jsonObject = jsonArray.getJSONObject(1)
//                    val dt_txt = jsonObject.getString("dt_txt")
//                    weatherDate1.text = dt_txt
                    weatherDate1.text = data.getJSONArray("list").getJSONObject(1).getString("dt_txt").let { it.split(" ")[0] + "  |  " + it.split(" ")[1] }
                    //split where there is a space then we format it
                    weatherDate2.text = data.getJSONArray("list").getJSONObject(9).getString("dt_txt").let { it.split(" ")[0] }
                    weatherDate3.text = data.getJSONArray("list").getJSONObject(17).getString("dt_txt").let { it.split(" ")[0] }
                    weatherDate4.text = data.getJSONArray("list").getJSONObject(25).getString("dt_txt").let { it.split(" ")[0] }
                    weatherDate5.text = data.getJSONArray("list").getJSONObject(33).getString("dt_txt").let { it.split(" ")[0] }

                    forecastDis1.text = data.getJSONArray("list").getJSONObject(1).getJSONArray("weather").getJSONObject(0).getString("description")
                    forecastDis2.text = data.getJSONArray("list").getJSONObject(9).getJSONArray("weather").getJSONObject(0).getString("description")
                    forecastDis3.text = data.getJSONArray("list").getJSONObject(17).getJSONArray("weather").getJSONObject(0).getString("description")
                    forecastDis4.text = data.getJSONArray("list").getJSONObject(25).getJSONArray("weather").getJSONObject(0).getString("description")
                    forecastDis5.text = data.getJSONArray("list").getJSONObject(33).getJSONArray("weather").getJSONObject(0).getString("description")

                    temperature1.text = String.format("%.1f °C", data.getJSONArray("list").getJSONObject(1).getJSONObject("main").getDouble("temp") - 273.15)
                    temperature2.text = String.format("%.0f °C", data.getJSONArray("list").getJSONObject(9).getJSONObject("main").getDouble("temp") - 273.15)
                    temperature3.text = String.format("%.0f °C", data.getJSONArray("list").getJSONObject(17).getJSONObject("main").getDouble("temp") - 273.15)
                    temperature4.text = String.format("%.0f °C", data.getJSONArray("list").getJSONObject(25).getJSONObject("main").getDouble("temp") - 273.15)
                    temperature5.text = String.format("%.0f °C", data.getJSONArray("list").getJSONObject(33).getJSONObject("main").getDouble("temp") - 273.15)

                    val iconUrl1 = "https://openweathermap.org/img/w/" + data.getJSONArray("list").getJSONObject(1).getJSONArray("weather").getJSONObject(0).getString("icon")+".png"  //here @4x to increase resolution of the icon
                    Picasso.get().load(iconUrl1).into(weatherIcon1)
                    val iconUrl2 = "https://openweathermap.org/img/w/${data.getJSONArray("list").getJSONObject(9).getJSONArray("weather").getJSONObject(0).getString("icon")}.png"
                    Picasso.get().load(iconUrl2).into(weatherIcon2)
                    val iconUrl3 = "https://openweathermap.org/img/w/${data.getJSONArray("list").getJSONObject(17).getJSONArray("weather").getJSONObject(0).getString("icon")}.png"
                    Picasso.get().load(iconUrl3).into(weatherIcon3)
                    val iconUrl4 = "https://openweathermap.org/img/w/${data.getJSONArray("list").getJSONObject(25).getJSONArray("weather").getJSONObject(0).getString("icon")}.png"
                    Picasso.get().load(iconUrl4).into(weatherIcon4)
                    val iconUrl5 = "https://openweathermap.org/img/w/${data.getJSONArray("list").getJSONObject(33).getJSONArray("weather").getJSONObject(0).getString("icon")}.png"
                    Picasso.get().load(iconUrl5).into(weatherIcon5)

                }
                catch (e : Exception){
                    Log.e("Error",e.toString())
                }
            },
            { error ->
                Log.e("Response", error.toString())
            })

        Volley.newRequestQueue(this).add(request)
    }
}