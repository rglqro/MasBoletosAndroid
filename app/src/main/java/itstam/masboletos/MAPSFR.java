package itstam.masboletos;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MAPSFR extends SupportMapFragment implements  OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener{

    String[] latLngs;
    GoogleMap mimapa;
    int getlocate = 0,ancho,alto;
    LocationRequest mLocationRequest;
    protected GoogleApiClient mGoogleApiClient;
    JSONArray Elementos=null;
    String nombrepv,direccionpv,telpv,urlimagen,latc,longc;

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

    Marker previousMarker = null;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mimapa = googleMap;
        // Posicionar el mapa en una localización y con un nivel de zoom
        for (int i = 0; i < latLngs.length; i++) {
            String[] cord = latLngs[i].split(",");
            LatLng latLng = new LatLng(Double.parseDouble(cord[0]), Double.parseDouble(cord[1]));
            googleMap.addMarker(new MarkerOptions().position(latLng).zIndex(i).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_sel)));
        }
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
        mimapa.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                consulta_puntoventa((int) marker.getZIndex());

                if(previousMarker!=null){
                    previousMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_sel));
                }
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_local));
                previousMarker=marker; //Now the clicked marker becomes previousMarker
                return false;
            }
        });
        mimapa.setMyLocationEnabled(true);
        mimapa.getUiSettings().setZoomControlsEnabled(true);
    }


    void consulta_puntoventa(final int indice){
        ((UbicacionAct)getActivity()).iniciar_cargando();
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String URL="https://www.masboletos.mx/appMasboletos/getDatosPuntoVenta.php?idpuntoventa="+((UbicacionAct)getActivity()).idpuntoventa[indice]; Log.e("URL",URL);
        // Initialize a new JsonArrayRequest instance
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("Respuesta Json",response.toString());
                        ((UbicacionAct)getActivity()).cerrar_cargando();
                        try{
                            Elementos = response;
                            for (int i=0;i<Elementos.length();i++){
                                JSONObject datos = Elementos.getJSONObject(i);
                                nombrepv=datos.getString("nombre");
                                direccionpv=datos.getString("domicilio");
                                telpv=datos.getString("telefono");
                                urlimagen="https://www.masboletos.mx/sica/images/"+datos.getString("imagen");
                            }
                            ((UbicacionAct)getActivity()).rlinfopunto.setVisibility(View.VISIBLE);
                            ((UbicacionAct)getActivity()).txvnpunto.setText(nombrepv);
                            ((UbicacionAct)getActivity()).txvinfomar.setText(direccionpv+"\n"+telpv);
                            Picasso.get().load(urlimagen).error(R.drawable.ic_local).into(((UbicacionAct)getActivity()).imvpunto);
                            ((UbicacionAct)getActivity()).txvcerrarinfo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ((UbicacionAct)getActivity()).rlinfopunto.setVisibility(View.GONE);
                                }
                            });

                            ((UbicacionAct)getActivity()).txvcomollegar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String[] cord = latLngs[indice].split(",");
                                    String url2="http://maps.google.com/maps?saddr="+latc+","+longc+"&daddr="+cord[0]+","+cord[1];
                                    Log.e("Direccion Mapa",url2);
                                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                            Uri.parse(url2));
                                    startActivity(intent);
                                }
                            });
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred
                        Toast.makeText(getActivity(),"Ha ocurrido un error en la consulta: \n"+error.toString(),Toast.LENGTH_SHORT).show();
                        ((UbicacionAct)getActivity()).cerrar_cargando();
                    }
                }
        );
        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest);
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
            mimapa.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, (float) 11.0));
            getlocate=1;
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(getActivity(), Locale.getDefault());
            try {
                latc= String.valueOf(location.getLatitude()); longc= String.valueOf(location.getLongitude());
                addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String state = addresses.get(0).getAdminArea();
                Log.e("Estado ",state);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Error con localizacion"," Error");
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
