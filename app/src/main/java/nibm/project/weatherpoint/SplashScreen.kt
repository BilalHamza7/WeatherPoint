package nibm.project.weatherpoint

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        var weatherLogo = findViewById<ImageView>(R.id.Weather_logo)
        weatherLogo.alpha = 0f
        weatherLogo.animate().setDuration(1500).alpha(1f).withEndAction {
            val i = Intent(this, MainScreen::class.java)
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.slide_out_right)
        }

//        var slideAnimation = AnimationUtils.loadAnimation(this, R.anim.slide)
//        weatherLogo.startAnimation(slideAnimation)
//
//        Handler().postDelayed({
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()
//        }, 3000)

    }
}