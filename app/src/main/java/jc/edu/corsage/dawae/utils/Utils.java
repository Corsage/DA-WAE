package jc.edu.corsage.dawae.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import jc.edu.corsage.dawae.MainActivity;
import jc.edu.corsage.dawae.kudan.Kudan;
import jc.edu.corsage.dawae.mapquest.models.StartPoint;

/**
 * Utility class.
 * Note: All methods here will be static.
 */

public class Utils {
    public static double DEVICE_LATITUDE = -75.19077087;
    public static double DEVICE_LONGITUDE = 39.95155162;

    public static LocationListener locationListener;

    public static void startLocationListener(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location location = null;
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("Utils", "Going to request last known location.");
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        if (location != null) {
            Log.d("Utils", "location not null, setting coordinates.");
            DEVICE_LATITUDE = location.getLongitude();
            DEVICE_LONGITUDE = location.getLatitude();

            if (Kudan.route != null) {
                if (Math.sqrt(Math.pow(Kudan.route.legs.get(0).maneuvers.get(Kudan.CURRENT_MANUEVER).startPoint.lat - DEVICE_LATITUDE, 2) +
                        Math.pow(Kudan.route.legs.get(0).maneuvers.get(Kudan.CURRENT_MANUEVER).startPoint.lng - DEVICE_LONGITUDE, 2)) <= 0.0002) {
                    if (Kudan.CURRENT_MANUEVER >= Kudan.UPPER_BOUND) {
                        Kudan.kudanStates = Kudan.KUDAN_STATES.REACHED;
                    } else {
                        Kudan.CURRENT_MANUEVER++;
                        Kudan.kudanStates = Kudan.KUDAN_STATES.INTERSECTION;
                    }
                }

                // If Kudan started.
                if (Kudan.kudanStates == Kudan.KUDAN_STATES.START) {
                    float[] degrees = Utils.setDirectionalVector(Kudan.route.legs.get(0).maneuvers.get(Kudan.CURRENT_MANUEVER).startPoint);
                    MainActivity.changeDegreesOfArrow(degrees);
                }

                if (Kudan.kudanStates == Kudan.KUDAN_STATES.INTERSECTION) {
                    // CHANGE STUFF
                    Kudan.kudanStates = Kudan.KUDAN_STATES.START;
                }
            }

        }

        Log.d("Utils", "LAT: " + DEVICE_LATITUDE + " LONG: " + DEVICE_LONGITUDE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                DEVICE_LATITUDE = location.getLatitude();
                DEVICE_LONGITUDE = location.getLongitude();

                Log.d("Utils", "onLocationChanged, LAT: " + DEVICE_LATITUDE + " LONG: " + DEVICE_LONGITUDE);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("Utils", "Going to request location updates.");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
        }
    }

    public static void startARActivity(Context context, String to) {
    }

    static float[] setDirectionalVector(StartPoint startPoint) {
        double deltaX = startPoint.lng - Utils.DEVICE_LONGITUDE;
        double deltaY = startPoint.lat - Utils.DEVICE_LATITUDE;
        double degrees = Math.atan(deltaX/deltaY);
        Log.d("Utils", "degrees found: " + degrees);

        return new float[] { (float)Math.PI/2.0f, (float)degrees, 0.0f };
    }

    public static void permissionsRequest(Activity context) {

        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 111);

        }
    }
}
