package inderjeet.test.servicelabs.sclabs;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity
{
    private GoogleMap mMap;
    String jsonString = null;
    ArrayList<jsonProperty> coordinates;
    private ClusterManager<Locations> mClusterManager;
    private static String TAG = MapsActivity.class.getName();;
    MarkerOptions  markerOptions;
    private ArrayList<LatLng> markerPoints = new ArrayList<>();
    PolylineOptions polylineOptions;

    Marker marker;
    Button animate;
    int currentPoint;

    private static int ZOOM_BAR = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        animate = (Button) findViewById(R.id.btn_animate);
        setUpMapIfNeeded();
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMyLocationEnabled(true);
        polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.BLUE);
        polylineOptions.width(5);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d(TAG, " TEstststsysy#1211 ");
                if (marker != null) {
                    markerOptions = new MarkerOptions().position(marker.getPosition());
                    Log.d(TAG, " TEstststsysy ");
                    new reverseTask().execute(marker.getPosition());
                }
                return true;
            }
        });
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(coordinates.get(0).longitude, coordinates.get(0).latitute), 10));
                if (markerPoints != null && markerPoints.size() > 0)
                    markerPoints.clear();

                mClusterManager = new ClusterManager<>(MapsActivity.this, mMap);
                setingUpClusterer();
                return true;
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                markerOptions = new MarkerOptions().position(latLng);
                marker = mMap.addMarker(markerOptions);
                coordinates.add(new jsonProperty.Builder(latLng.latitude, latLng.longitude).build());
                mClusterManager.addItem(new Locations(latLng.longitude, latLng.latitude));

                markerPoints.add(marker.getPosition());
                polylineOptions.addAll(markerPoints);
                mMap.addPolyline(polylineOptions);
            }
        });
        animate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(markerPoints == null || markerPoints.size() == 0 || markerPoints.isEmpty()){
                    Toast.makeText(getApplicationContext(), " Sorry no points to animate ", Toast.LENGTH_LONG).show();
                } else {
                    float zoomValue = (float)(ZOOM_BAR + 2);

                    mMap.animateCamera(
                            CameraUpdateFactory.zoomTo(zoomValue),
                            5000, animateCallback);
                    currentPoint = 0-1;
                }
            }
        });
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        setUpMapIfNeeded();
    }
    private void setUpMapIfNeeded()
    {
        if (mMap == null)
        {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null)
            {
                readJSON();

            }
        }
    }
    /**
     * Read the JSON file pinball_berlin.json from assets folder and creating jsonString to parse
     */
    private void readJSON()
    {
        AssetManager manager = MapsActivity.this.getAssets();
        try
        {
            InputStream is = manager.open("pinball_berlin.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer);
        } catch (IOException exception)
        {
            exception.printStackTrace();
        }
        parseJson();
    }

    /**
     * Parse JSON String and extract strings out of it to create arralylist of object jsonProperty
     */

    private void parseJson()
    {
        try
        {
            JSONArray jsonArray = new JSONArray(jsonString);
            coordinates = new ArrayList<>();
            coordinates.clear();
            for(int i = 0; i < jsonArray.length(); i++)
            {
                JSONArray object = jsonArray.getJSONObject(i).getJSONObject("pin").getJSONArray("coordinates");
                JSONObject address = jsonArray.getJSONObject(i);
                coordinates.add(jsonProperty.insertObject(address.getString("address"), address.getString("city"), address.getString("country"),
                        address.getString("lastupdate"), address.getString("machine"), address.getString("note"),
                        object.getDouble(0), object.getDouble(1),jsonArray.getJSONObject(i).getJSONObject("pin").getString("type"),
                        address.getString("place"), address.getInt("stars"), address.getString("street"), Integer.parseInt(address.getString("zipcode").replaceAll("[-+.^:,?]", ""))));
            }
        } catch (JSONException exception)
        {
            exception.printStackTrace();
        }
    }
    /**
     *adding markers object to clusters to manage the clustering
     */

    private void setingUpClusterer()
    {

        for (int i  = 0; i < coordinates.size(); i++)
        {
            mClusterManager.addItem(new Locations(coordinates.get(i).longitude, coordinates.get(i).latitute));
        }
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        addItemsToCluster();
    }

    /**
     * adding and making group of clusterss based on distance and number of items
     */

    private void addItemsToCluster()
    {
        mMap.clear();
        double lat =coordinates.get(0).longitude;
        double lng = coordinates.get(0).latitute;
        for (int i = 0; i < 25; i++)
        {
            double offset = i / 60d;
            Locations offSetLoc = new Locations(lat + offset, lng + offset);
            mClusterManager.addItem(offSetLoc);
        }
    }

    /**
     * Async class to apply the reverse geoCoding
     */

    private class reverseTask extends AsyncTask<LatLng, Void, String>
    {
        @Override
        protected String doInBackground(LatLng... params)
        {
            Geocoder geocoder = new Geocoder(MapsActivity.this);
            List<Address> addresses = null;
            String title= null;
            try
            {
                addresses = geocoder.getFromLocation(params[0].latitude, params[0].longitude,1);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            if(addresses != null && addresses.size() > 0 )
            {
                Address address = addresses.get(0);
                title = String.format("%s, %s, %s",address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "", address.getLocality(), address.getCountryName());
            }
            return title;
        }
        @Override
        protected void onPostExecute(String title)
        {
            if(title != null)
                mMap.addMarker(markerOptions.title(title)).showInfoWindow();
        }
    }

    GoogleMap.CancelableCallback animateCallback = new GoogleMap.CancelableCallback()
    {
        @Override
        public void onCancel(){}
        @Override
        public void onFinish()
        {
            if(++currentPoint < markerPoints.size())
            {

                Location startingLocation = new Location("starting point");
                startingLocation.setLatitude(mMap.getCameraPosition().target.latitude);
                startingLocation.setLongitude(mMap.getCameraPosition().target.longitude);
                Location endingLocation = new Location("ending point");
                endingLocation.setLatitude(markerPoints.get(currentPoint).latitude);
                endingLocation.setLongitude(markerPoints.get(currentPoint).longitude);

                float targetBearing = startingLocation.bearingTo(endingLocation);

                LatLng targetLatLng = markerPoints.get(currentPoint);
                float targetZoom = ZOOM_BAR;

                CameraPosition cameraPosition = new CameraPosition.Builder()
                                                .target(targetLatLng)
                                                .bearing(targetBearing)
                                                .zoom(targetZoom)
                                                .build();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                        5000, animateCallback);
            }else{
                Toast.makeText(getApplicationContext(), " Animation finished ", Toast.LENGTH_LONG).show();
            }
        }

    };
}
