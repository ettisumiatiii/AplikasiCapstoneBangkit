package com.example.projekaplikasibangkitcapstone.ui.lapor

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.projekaplikasibangkitcapstone.R
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


class LaporFragment : Fragment(), View.OnClickListener {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var imageURI:Any

    //ID VIEW IN LAYOUT
    private lateinit var btn_fromCamera:Button
    private lateinit var btn_fromGallery:Button
    private lateinit var photoResult:ImageView
    private lateinit var btn_clearPhoto:TextView
    private lateinit var txt_currentLocation:TextView
    private lateinit var layout_special:ConstraintLayout
    private lateinit var btn_special:Button
    private lateinit var progressBar: ProgressBar
    private lateinit var txt_special:TextView
    private lateinit var btn_submitreport:TextView

    private val REQUEST_PICK_FROM_CAMERA = 42
    private val REQUEST_PICK_FROM_GALLERY = 43
    private val PERMISSION_ID = 100
    private var REQUEST_CODE = 0

    private lateinit var locationRequest: LocationRequest

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_lapor, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_fromCamera = view.findViewById(R.id.fragmentlapor_btn_fromcamera)
        photoResult = view.findViewById(R.id.fragmentlapor_photoresult)
        btn_clearPhoto = view.findViewById(R.id.fragmentlapor_clearphoto)
        btn_fromGallery = view.findViewById(R.id.fragmentlapor_btn_fromgallery)
        txt_currentLocation = view.findViewById(R.id.fragmentlapor_txt_currentlocation)
        layout_special = view.findViewById(R.id.fragmentlapor_containerspecial)
        btn_special = view.findViewById(R.id.fragmentlapor_btnspecial)
        progressBar = view.findViewById(R.id.fragmentlapor_progressbar)
        txt_special = view.findViewById(R.id.fragmentlapor_txtspecial)
        btn_submitreport = view.findViewById(R.id.fragmentlapor_submitreport)

        btn_fromCamera.setOnClickListener(this)
        btn_clearPhoto.setOnClickListener(this)
        btn_fromGallery.setOnClickListener(this)
        btn_special.setOnClickListener(this)
        btn_submitreport.setOnClickListener(this)
        btn_clearPhoto.visibility = View.INVISIBLE

        txt_currentLocation.setOnClickListener(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.context)

        if (checkLocationPermission()){
            if (isLocationEnabled()){
                //LOCATION PERMISSION TRUE & LOCATION IS TRUE
                layout_special.visibility = View.INVISIBLE
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.context)
                println("BENER")
                getLocation()
            } else{
                //LOCATION PERMISSION TRUE & LOCATION IS NOT TRUE
                txt_currentLocation.visibility = View.INVISIBLE
                photoResult.visibility = View.INVISIBLE
                btn_fromCamera.visibility = View.INVISIBLE
                btn_fromGallery.visibility = View.INVISIBLE
                progressBar.visibility = View.INVISIBLE
                btn_submitreport.visibility = View.INVISIBLE
                txt_special.text = "PLEASE TURN ON YOUR LOCATION AND CLICK REFRESH"
            }
        }else{
            //LOCATION PERMISSION IS DENY
            txt_currentLocation.visibility = View.INVISIBLE
            photoResult.visibility = View.INVISIBLE
            btn_fromCamera.visibility = View.INVISIBLE
            btn_fromGallery.visibility = View.INVISIBLE
            progressBar.visibility = View.INVISIBLE
            btn_submitreport.visibility = View.INVISIBLE
            txt_special.text = "PLEASE ACTIVATE LOCATION PERMISSION ON YOUR SMARTPHONE AND CLICK REFRESH"
            getLocationPermission()
        }
    }

    private fun checkLocationPermission():Boolean{
        var isLocationPermissionEnabled:Boolean = false
        if (ActivityCompat.checkSelfPermission(this.requireView().context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this.requireView().context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            isLocationPermissionEnabled = true
        }
        return isLocationPermissionEnabled
    }
    private fun getLocationPermission(){
        if (ActivityCompat.checkSelfPermission(this.requireView().context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this.requireView().context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_ID)
            return
        }
    }
    private fun getLocation() {
       val task:Task<Location> = fusedLocationProviderClient.lastLocation

        if (ActivityCompat.checkSelfPermission(this.requireView().context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this.requireView().context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ){
            ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_ID
            )
            return
        }
        task.addOnSuccessListener {
            if (it!=null){
                txt_currentLocation.visibility = View.VISIBLE
                progressBar.visibility = View.INVISIBLE
                var geocoder = Geocoder(this.requireView().context, Locale.getDefault())
                var adress = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                txt_currentLocation.text = adress.get(0).locality.toString()
            }else{
                txt_currentLocation.visibility = View.INVISIBLE
                progressBar.visibility = View.VISIBLE
                getNewLocation()
            }
        }

    }
    private fun getNewLocation(){
        println("MASUK NEW LOCATION")
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2
        if (checkLocationPermission()){
            if (isLocationEnabled()){
                fusedLocationProviderClient!!.requestLocationUpdates(
                        locationRequest, locationCallback,Looper.myLooper()
                )
            }
        }
    }
    private val locationCallback = object :LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            var lastLocation = p0.lastLocation
            println(lastLocation.latitude)
            getLocation()
        }
    }

    private fun isLocationEnabled():Boolean{
        var locationManager = this.requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data!=null){
            btn_clearPhoto.visibility = View.VISIBLE
        }
        if (requestCode == REQUEST_PICK_FROM_CAMERA && resultCode == Activity.RESULT_OK){
            var fotoYgDiAmbil = data?.extras?.get("data") as Bitmap
            imageURI = fotoYgDiAmbil!!
            REQUEST_CODE = requestCode
            photoResult.setImageBitmap(fotoYgDiAmbil)
        }else if (requestCode == REQUEST_PICK_FROM_GALLERY && resultCode == Activity.RESULT_OK){
            var fotoYgDiAmbil = data?.data
            imageURI = fotoYgDiAmbil!!
            REQUEST_CODE = requestCode
            photoResult.setImageURI(fotoYgDiAmbil)
        } else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.fragmentlapor_btn_fromcamera->{
                val ambilfotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(ambilfotoIntent, REQUEST_PICK_FROM_CAMERA)
            }
            R.id.fragmentlapor_clearphoto->{
                photoResult.setImageResource(R.drawable.ic_camera)
                btn_clearPhoto.visibility = View.INVISIBLE
            }
            R.id.fragmentlapor_btn_fromgallery->{
                ambilFotoDariGaleri()
            }
            R.id.fragmentlapor_btnspecial->{
                layout_special.visibility = View.INVISIBLE
                txt_currentLocation.visibility = View.VISIBLE
                photoResult.visibility = View.VISIBLE
                btn_fromCamera.visibility = View.VISIBLE
                btn_fromGallery.visibility = View.VISIBLE
                var navController = findNavController()
                navController.navigate(R.id.navigation_home)
                navController.navigate(R.id.navigation_lapor)

            }
            R.id.fragmentlapor_submitreport->{
                if (REQUEST_CODE!=0){
                    var image = imageURI
                    var intent = Intent(this.activity, DetailLaporActivity::class.java)
                    if (REQUEST_CODE==REQUEST_PICK_FROM_GALLERY){
                        image = imageURI as Uri
                        intent.putExtra(DetailLaporActivity.IMAGE_RESULT, image)
                        intent.putExtra(DetailLaporActivity.IMAGE_REQUEST_TYPE, "GALLERY")
                    }
                    if (REQUEST_CODE==REQUEST_PICK_FROM_CAMERA){
                        image = imageURI as Bitmap
                        intent.putExtra(DetailLaporActivity.IMAGE_RESULT, image)
                        intent.putExtra(DetailLaporActivity.IMAGE_REQUEST_TYPE, "CAMERA")
                    }
                    startActivity(intent)
                }
            }
        }
    }

    private fun ambilFotoDariGaleri() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_PICK_FROM_GALLERY)
    }

}