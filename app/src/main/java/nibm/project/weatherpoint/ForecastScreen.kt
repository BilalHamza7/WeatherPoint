package nibm.project.weatherpoint

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.widget.SearchView
import com.google.android.gms.maps.GoogleMap

class ForecastScreen : AppCompatActivity() {
    private lateinit var mMap: GoogleMap //googleMap provided by android API represents object mMap
    private lateinit var imgHomeButton : ImageButton
    var searchView: SearchView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forcast_screen)

        searchView = findViewById(R.id.idSearchView)
        imgHomeButton = findViewById(R.id.img_btn_home)


        imgHomeButton.setOnClickListener(){
            startActivity(Intent(this, MainScreen::class.java))
        }


    }
}