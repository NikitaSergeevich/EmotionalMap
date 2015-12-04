package group4.innopolis.com.emotionalmap;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import group4.innopolis.com.emotionalmap.Database.EmotionMapDbHelper;
import group4.innopolis.com.emotionalmap.Database.EmotionMapContract.EmotionMapEntry;
import group4.innopolis.com.emotionalmap.Network.ServerHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback{

    private GoogleMap mMap;
    private LatLng myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        initializeSyncButton();
    }

    private void initializeSyncButton() {
        Button connect = (Button) findViewById(R.id.Sync);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new Thread(new synchronize()).start();
            }
        });
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        new Thread(new ServerHelper("DELETE", new EmotionMapRecord(100, 100, 1, "Nikitos"))).start();

        setMyLocation();
        displayMapRecords();
        addMeToTheMap();
    }


    public void addDataToDatabase() {
        EmotionMapDbHelper mDbHelper = new EmotionMapDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        for (int i = 1; i < 15; i++) {
            final ContentValues values = new ContentValues();
            values.put(EmotionMapEntry.COLUMN_NAME_USER, "User " + i);
            values.put(EmotionMapEntry.COLUMN_NAME_EMOTION, i%3);
            values.put(EmotionMapEntry.COLUMN_NAME_LAT, 100);
            values.put(EmotionMapEntry.COLUMN_NAME_LNG, 100);

            db.insert(EmotionMapEntry.TABLE_NAME,
                    null,
                    values
            );
        }
    }




    private void setMyLocation() {
        //Location location = mMap.getMyLocation();
        myLocation = new LatLng(55.753, 48.744);
    }

    public void displayMapRecords() { //todo: add button to execute this method
        Set<EmotionMapRecord> records = getMapData();

        if (records == null)
            return;

        for (EmotionMapRecord record : records) {
            LatLng latLng = new LatLng(record.Lat, record.Lng);
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(record.UserName)
                    .icon(BitmapDescriptorFactory.fromResource(getMarkerImage(record.Type))));
        }
    }

    private void addMeToTheMap() {

        mMap.addMarker(new MarkerOptions()
                .position(myLocation)
                .title("Me")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_media_play))); //todo: replace for "Me" icon
        moveToMyLocation();
    }


    public void moveToMyLocation() //todo: add button to execute this method
    {
        setMyLocation();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
    }


    public int getMarkerImage(int recordType) {
        //todo: reconsider
        return recordType;
    }

    /*Here we will get data from current database after synchronization*/
    public Set<EmotionMapRecord> getMapData() {
        //todo: connect to parse.com and get data
        return null;
    }

    public void updateMyRecord(int type) {
        //MapRecord myRecord = new MapRecord(myLocation.latitude, myLocation.longitude, type, getUsername());

        //todo: 1. connect to parse.com and post (put) data (must have)
        //todo: 2. push notification to other clients (low priority)

        mMap.addMarker(new MarkerOptions()
                .position(myLocation)
                .title(getUsername())
                .icon(BitmapDescriptorFactory.fromResource(getMarkerImage(type))));
    }

    private String getUsername() {
        AccountManager manager = AccountManager.get(this);
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<String>();

        for (Account account : accounts) {
            // TODO: Check possibleEmail against an email regex or treat
            // account.name as an email address only for certain account.type values.
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);
            String[] parts = email.split("@");

            if (parts.length > 1)
                return parts[0];
        }
        return null;
    }

    @Override
    public void onMapLoaded() {

    }
}
