package com.optic.moveon.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.optic.moveon.R
import com.google.android.gms.maps.model.BitmapDescriptorFactory


class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private lateinit var map:GoogleMap

    companion object{
        const val REQUEST_CODE_LOCATION = 0

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        createFragment()
        val navigationView = findViewById<BottomNavigationView>(R.id.navigation_bar)
        navigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    // Al hacer clic en el elemento de inicio, abrir la actividad MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                // Aquí puedes agregar más casos para manejar los clics en otros elementos del menú
                else -> false
            }
        }
    }
    private fun createFragment(){
        val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        createMarker()
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMyLocationClickListener(this)
        enableLocation()
    }

    private fun createMarker(){
        val cHarvard= LatLng(42.374428, -71.118247)
        val cBarcelona = LatLng(41.38667245684843, 2.1638391946708917)
        val cLisboa = LatLng(38.75267764312458, -9.158204770017571)
        val cMelbourne = LatLng(-37.79709850852806, 144.96134525030638)
        val cParis = LatLng(48.83093237415609, 2.38185833669583)
        val cStuttgart = LatLng(48.78168503199983, 9.172333010746629)
        val cSaopaulo = LatLng(-23.561143384138067, -46.73082128989597)
        val cInstitutoarg = LatLng(-34.641503027982594, -58.40644444718995)
        val cPontificia = LatLng(-33.442091821372, -70.64060004723589)
        val cPanama = LatLng(9.027387872344724, -79.52997272392035)
        val cDominicana = LatLng(19.501863755668275, -70.656522775733)
        val cInstitutomex= LatLng(17.75666389041608, -92.9928467318843)
        val cInstitutomex2 = LatLng(19.509414195578103, -99.1393044998696)
        val cNacionalmex = LatLng(19.337087457741244, -99.18615118476319)
        val cPorto = LatLng(41.15989254667872, -8.624950153264914)
        val cValencia = LatLng(39.481080032143126, -0.34088462641984957)
        val cMadrid = LatLng(40.454308767245195, -3.706244007938191)
        val cNantes = LatLng(47.248469464344794, -1.549459943298134)
        val cGrenoble = LatLng(45.18435553709638, 5.75367759709846)
        val cPolonia = LatLng(50.064558029541985, 19.92360779119948)
        val marker1 = MarkerOptions().position(cHarvard).title("Universidad de Harvard")
        val marker2 = MarkerOptions().position(cBarcelona).title("Universidad de Barcelona")
        val marker3 = MarkerOptions().position(cLisboa).title("Universidad de Lisboa")
        val marker4 = MarkerOptions().position(cMelbourne).title("Universidad de Melbourne")
        val marker5 = MarkerOptions().position(cParis).title("Universidad de Paris")
        val marker6 = MarkerOptions().position(cStuttgart).title("Universidad de Stuttgart")
        val marker7 = MarkerOptions().position(cSaopaulo).title("Universidad de São Paulo")
        val marker8 = MarkerOptions().position(cInstitutoarg).title("Instituto Tecnológico de Buenos Aires")
        val marker9 = MarkerOptions().position(cPontificia).title("Pontificia Universidad Católica de Chile")
        val marker10 = MarkerOptions().position(cPanama).title("Universidad Tecnológica de Panamá")
        val marker11 = MarkerOptions().position(cDominicana).title("Pontificia Universidad Católica Madre y Maestra")
        val marker12 = MarkerOptions().position(cInstitutomex).title("Instituto Tecnológico y de Estudios Superiores de Monterrey ")
        val marker13 = MarkerOptions().position(cInstitutomex2).title("Instituto Politécnico Nacional")
        val marker14 = MarkerOptions().position(cNacionalmex).title("Universidad Nacional Autónoma de México")
        val marker15 = MarkerOptions().position(cPorto).title("Universidad de Oporto")
        val marker16 = MarkerOptions().position(cValencia).title("Universidad Politécnica de Valencia")
        val marker17 = MarkerOptions().position(cMadrid).title("Universidad Politécnica de Madrid")
        val marker18 = MarkerOptions().position(cNantes).title("Ecole Centrale de Nantes")
        val marker19 = MarkerOptions().position(cGrenoble).title("Instituto Politécnico de Grenoble")
        val marker20 = MarkerOptions().position(cPolonia).title("AGH Universidad de Ciencia y Tecnología")
        map.addMarker(marker1)
        map.addMarker(marker2)
        map.addMarker(marker3)
        map.addMarker(marker4)
        map.addMarker(marker5)
        map.addMarker(marker6)
        map.addMarker(marker7)
        map.addMarker(marker8)
        map.addMarker(marker9)
        map.addMarker(marker10)
        map.addMarker(marker11)
        map.addMarker(marker12)
        map.addMarker(marker13)
        map.addMarker(marker14)
        map.addMarker(marker15)
        map.addMarker(marker16)
        map.addMarker(marker17)
        map.addMarker(marker18)
        map.addMarker(marker19)
        map.addMarker(marker20)
        // map.animateCamera(
            //CameraUpdateFactory.newLatLngZoom(coordinates, 18f)
        // )
    }

    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun enableLocation(){
        if(!::map.isInitialized) return
        if(isLocationPermissionGranted()){
            map.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
        }
    }
    private fun requestLocationPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()

        }else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_CODE_LOCATION-> if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                map.isMyLocationEnabled = true
            } else{
                Toast.makeText(this, "Para activar la localizacion ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if(!::map.isInitialized) return
        if(!isLocationPermissionGranted()){
            map.isMyLocationEnabled = false
            Toast.makeText(this, "Para activar la localizacion ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "Ubicación Actual", Toast.LENGTH_SHORT).show()
        return false
    }

    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(this, "Estás en ${p0.latitude}, ${p0.longitude}", Toast.LENGTH_SHORT).show()
    }
}