package com.example.week11

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.DocumentsContract.Root

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.week11.databinding.ActivityMapsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception

//private lateinit var binding: ActivityMapsBinding
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_maps)

        var dDatabaseRed: DatabaseReference? = null
        val bundle: Bundle? = intent.extras
        val phoneNumber = bundle?.getString("phoneNumber") ?: ""
        dDatabaseRed = FirebaseDatabase.getInstance().reference
        dDatabaseRed!!.child("Users").child(phoneNumber).child("location").addValueEventListener(object:ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val td = snapshot!!.value as HashMap<String,Any>
                    val lat = td["lat"].toString()
                    val log = td["log"].toString()
                    MapsActivity.sydney= LatLng(lat.toDouble(),log.toDouble())
                    MapsActivity.lastOnLine = td["lastOnline"].toString()
                    loadMap()
                } catch (e:Exception){}

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

    }


    fun loadMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    companion object{
        var sydney = LatLng(-34.0, 151.0)
        var lastOnLine = "not defined"
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}