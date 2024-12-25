package com.example.locationapp

import android.content.Context
import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.locationapp.ui.theme.LocationAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel:LocationViewModel = viewModel()

            LocationAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
MyApp(viewModel)
                }
            }
        }
    }
}


@Composable
fun MyApp(viewModel: LocationViewModel){

    val context= LocalContext.current
    val locationUtils=LocationUtils(context)
    LocationDisplay(localutils = locationUtils, viewModel = viewModel, context = context)
}
@Composable
fun LocationDisplay(
    viewModel: LocationViewModel,
    localutils :LocationUtils,
    context:Context


){

    val location =viewModel.location.value
    val address= location?.let {
        localutils.reverseGeocodeLocation(location)


    }
    val requestPermissionLauncher= rememberLauncherForActivityResult(contract =ActivityResultContracts.
    RequestMultiplePermissions()
        , onResult ={

            permissions ->
            if(permissions[Manifest.permission.ACCESS_COARSE_LOCATION]==true
                &&
                permissions[Manifest.permission.ACCESS_FINE_LOCATION]==true
                ){
                //I HAVE ACCESs TO LOACTION
                localutils.requestLocationUpdates(viewModel=viewModel)

            }else
            {
//request for permissions
                val rationalRequired=ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION


                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION


                )
if(rationalRequired){
    Toast.makeText(context,
        "Loaction Permission is required for this feature to work",
        Toast.LENGTH_LONG).show()
}else{
    Toast.makeText(context,
        "Loaction Permission is required.Please Enable it in Android Settings",
        Toast.LENGTH_LONG).show()

}


            }
        }

    )




    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        if(location!=null){
            Text("Address: ${location.latitude} ${location.longitude} \n",fontSize = 24.sp)
            Text("You Current Location is : \n \n$address", fontSize = 24.sp)


        }else{



        Text(text = "Location NOt Available", fontSize = 30.sp)
        }
        Button(onClick = {
        if(localutils.hasLocationPermission(context)) {
            //Permission already granted update the location
            localutils.requestLocationUpdates(viewModel)



        }else{

            //Request location permission
            requestPermissionLauncher.launch(arrayOf( Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION))
        }

        }) {
            Text(text = "Get Location")
        }
    }
}

