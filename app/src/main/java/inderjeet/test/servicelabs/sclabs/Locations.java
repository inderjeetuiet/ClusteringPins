package inderjeet.test.servicelabs.sclabs;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by isingh on 8/31/15.
 */
public class Locations implements ClusterItem
{
    private final LatLng mPosition;

    public Locations(double lat, double lng)
    {
        mPosition = new LatLng(lat, lng);
    }
    @Override
    public LatLng getPosition()
    {
        return mPosition;
    }
}
