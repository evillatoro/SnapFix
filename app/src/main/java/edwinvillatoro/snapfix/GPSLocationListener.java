package edwinvillatoro.snapfix;

/**
 * Created by edwinvillatoro on 9/7/17.
 */

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Geocoder;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;


public class GPSLocationListener implements LocationListener {

    Context context;

    public GPSLocationListener(Context context) {
        super();
        this.context = context;
    }

    public String getAddress(Location point) {
        String address = "";
        Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocation(point.getLatitude(),
                    point.getLongitude(),1);

            if (!addresses.isEmpty()) {
                for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++) {
                    address += addresses.get(0).getAddressLine(i) + " ";
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }

    public Location getLocation(){
        Location location = null;

        if (ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            Log.e("fist","error");
        } else {
            try {
                LocationManager lm = (LocationManager) context.getSystemService(LOCATION_SERVICE);
                boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (isGPSEnabled){
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000,10,this);
                    location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }else{
                    Log.e("sec","error");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return location;
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}
