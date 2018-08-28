package itstam.masboletos;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MAPSFR#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MAPSFR extends SupportMapFragment implements  OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener{

    String[] latLngs;
    GoogleMap mimapa;
    int getlocate = 0;
    LocationRequest mLocationRequest;
    protected GoogleApiClient mGoogleApiClient;

    public MAPSFR() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        latLngs = getArguments().getStringArray("latLngs");
        getMapAsync(this);

        return rootView;
    }

    private void buildGoogleApiClient() {
        //Instantiating the GoogleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mimapa = googleMap;
        // Posicionar el mapa en una localización y con un nivel de zoom
        for (int i = 0; i < latLngs.length; i++) {
            String[] cord = latLngs[i].split(",");
            LatLng latLng = new LatLng(Double.parseDouble(cord[0]), Double.parseDouble(cord[1]));
            googleMap.addMarker(new MarkerOptions().position(latLng).title("Marcador "+i).snippet("Más Boletos "+1));
        }
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
        mimapa.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                ((UbicacionAct)getActivity()).rlinfopunto.setVisibility(View.VISIBLE);
                ((UbicacionAct)getActivity()).txvinfomar.setText(marker.getTitle());
                ((UbicacionAct)getActivity()).txvcerrarinfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((UbicacionAct)getActivity()).rlinfopunto.setVisibility(View.GONE);
                    }
                });
                return false;
            }
        });
        mimapa.setMyLocationEnabled(true);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,  this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        if(getlocate==0) {//Se activa cuando detecta tu ubicacion por primera vez
            mimapa.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, (float) 10.0));
            getlocate=1;
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(getActivity(), Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String state = addresses.get(0).getAdminArea();
                Log.d("Estado ",state);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Error con localizacion"," Error");
            }
        }
    }

    @Override
    public void onStart() {
        buildGoogleApiClient();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
}
