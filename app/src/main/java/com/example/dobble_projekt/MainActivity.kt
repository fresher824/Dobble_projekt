package com.example.dobble_projekt

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import com.example.dobble_projekt.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private val MY_PERMISSION_REQEST_ACCESS_FINELOCATION = 101
    private lateinit var layoutBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layoutBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(layoutBinding.root)

        //Pozwolenie na dostep do lokalizaci
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSION_REQEST_ACCESS_FINELOCATION)
            return
        }
    }

    fun onlineButtonClick(view: View) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
        {
            findNavController(R.id.fragmentContainerView3).navigate(R.id.action_chooseGameFragment_to_onlineFragmentMenu)
        }
        else {
            Toast.makeText(this, "No i co tam biedaku?", Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == MY_PERMISSION_REQEST_ACCESS_FINELOCATION){
            val indexOf = permissions.indexOf(Manifest.permission.ACCESS_FINE_LOCATION)
            if(indexOf != -1 && grantResults[indexOf] != PackageManager.PERMISSION_GRANTED){
                Snackbar.make(layoutBinding.root, getString(R.string.permission_msg), Snackbar.LENGTH_LONG).setAction("RETRY?"){
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSION_REQEST_ACCESS_FINELOCATION)
                }.show()
            }
        }
        return super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}