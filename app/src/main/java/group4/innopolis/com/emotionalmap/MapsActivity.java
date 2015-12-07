package group4.innopolis.com.emotionalmap;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import group4.innopolis.com.emotionalmap.Database.DbConverter;
import group4.innopolis.com.emotionalmap.Database.EmotionMapContentProvider;

import group4.innopolis.com.emotionalmap.Database.EmotionMapContract.EmotionMapEntry;
import group4.innopolis.com.emotionalmap.Network.ServerHelper;
import group4.innopolis.com.emotionalmap.R.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback{

    private GoogleMap mMap;
    // private LatLng myLocation;
    private LocationManager locationManager;
    private boolean lmenabled = true;
    private NotificationManager notificationManager;
    private Notification notification;
    private String username;
    private int ID = 74392;

    private Runnable notifications = new Runnable() {
        @Override
        public void run() {
            try {
                while (true) {
                    notificationManager.notify(ID, notification);
                    Thread.sleep(10000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    public void initializeNotification()
    {
        Context context = getApplicationContext();
        Intent notificationIntent = new Intent(context, MapsActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentIntent(contentIntent)
                .setContentTitle("Emotion Map App")
                .setContentText("Hey! How are you doing? Post your mood!")
                .setAutoCancel(true)
                .setSmallIcon(mipmap.mobile);

        notification = builder.build();
        long[] vibrate = new long[] { 1000, 1000, 1000, 1000, 1000 };
        notification.vibrate = vibrate;
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        new Thread(notifications).start();
    }

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
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
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        initializeSyncButton();
        initializePostButton();
        initializeNotification();
        username = getUsername();
        //cleanServer();
    }

    public void cleanServer()
    {
        new ServerHelper("GET", null) {
            @Override
            protected void onPostExecute(final ArrayList<EmotionMapRecord> list){

                for (EmotionMapRecord r: list)
                {
                    new ServerHelper("DELETE", r){
                        @Override
                        protected void onPostExecute(final ArrayList<EmotionMapRecord> list){
                            //donothing
                        }
                    }.execute();
                }

            }
        }.execute();
    }

    private void initializeSyncButton() {
        Button connect = (Button) findViewById(R.id.Sync);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronize();
            }
        });
    }

    private void initializePostButton() {
        Button post_btn = (Button) findViewById(R.id.Post);

        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (locationManager == null || !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    lmenabled = false;
                    buildAlertMessageNoGps();
                }

                if (lmenabled == true) {
                    registerForContextMenu(v);
                    openContextMenu(v);
                    unregisterForContextMenu(v);
                }
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()== id.Post){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.mood_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        int type;
        Location currentLocation;
        switch (item.getItemId()){
            case id.good:
                type = 1;
                break;
            case id.neutral:
                type = 2;
                break;
            case id.bad:
                type = 3;
                break;
            default:
                return super.onContextItemSelected(item);
        }

        try {
            currentLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            double longitude = currentLocation.getLongitude();
            double latitude = currentLocation.getLatitude();
            String text = ((EditText) findViewById(id.text)).getText().toString().trim();
            addMarkerToTheMap(username, type, new LatLng(latitude, longitude), text);
            EmotionMapRecord r = new EmotionMapRecord(username, type, latitude, longitude, text, null);
            getContentResolver().insert(EmotionMapContentProvider.CONTENT_URI_EMOTION_MAP, DbConverter.convertToContentValues(r));



            /*Server Update Realized in this part, not in EmotionMapContentProvider, because
            * we need to use insert method which converts object into another object which represent
            * sql table fields. We use different objects in order to reduce memory which one class with all fields
              will consume in memory*/
            new ServerHelper("POST", r) {
                @Override
                protected void onPostExecute(final ArrayList<EmotionMapRecord> list) {

                }
            }.execute();
        }
        catch (SecurityException e) {

        }
        return super.onContextItemSelected(item);
    }

    private void addMarkerToTheMap(String Username, int Type, LatLng myLocation, String Text) {
        switch (Type)
        {
            case 1:
                Type = mipmap.goodmood;
                break;
            case 2:
                Type = mipmap.nomood;
                break;
            case 3:
                Type = mipmap.badmood;
                break;
        }

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(myLocation)
                .title(Username)
                .snippet(Text)
                .icon(BitmapDescriptorFactory.fromResource(Type))); //todo: replace for "Me" icon
        marker.showInfoWindow();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if ( locationManager == null || !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER ) ) {
            lmenabled = false;
            buildAlertMessageNoGps();
        }

        mMap.setMyLocationEnabled(true);
        displayMapRecords();
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        lmenabled = true;
                        try {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, mLocationListener);
                        } catch (SecurityException e) {

                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    public void synchronize() {

        new ServerHelper("GET", null) {
            @Override
            protected void onPostExecute(final ArrayList<EmotionMapRecord> list) {
                boolean insert = true;
                String[] projectionColumns =
                        {
                                EmotionMapEntry.COLUMN_NAME_OBJECT_ID
                        };
                Cursor cursor =
                        getContentResolver().query(EmotionMapContentProvider.CONTENT_URI_EMOTION_MAP,
                                projectionColumns,
                                null,
                                null,
                                null);

                    for (EmotionMapRecord r : list) {
                        if (cursor.moveToFirst()) {
                            do {
                                if (r.objectId == cursor.getString(0))
                                    insert = false;
                            } while (cursor.moveToNext());
                        }

                        if (insert) {
                            getContentResolver().insert(EmotionMapContentProvider.CONTENT_URI_EMOTION_MAP, DbConverter.convertToContentValues(r));
                            addMarkerToTheMap(r.UserName, r.Type, new LatLng(r.Lat, r.Lng), r.Text);
                            insert = true;
                        }
                    }
                }
            }.execute();
    }


    public void displayMapRecords() {
        String[] projectionColumns =
                {
                        EmotionMapEntry.COLUMN_NAME_USER,
                        EmotionMapEntry.COLUMN_NAME_LAT,
                        EmotionMapEntry.COLUMN_NAME_LNG,
                        EmotionMapEntry.COLUMN_NAME_EMOTION,
                        EmotionMapEntry.COLUMN_NAME_TEXT
                };
        Cursor cursor =
                getContentResolver().query(EmotionMapContentProvider.CONTENT_URI_EMOTION_MAP,
                        projectionColumns,
                        null,
                        null,
                        null);

        if (cursor.moveToFirst()) {
            do {
                String user = cursor.getString(cursor.getColumnIndex(EmotionMapEntry.COLUMN_NAME_USER));
                double lat = cursor.getDouble(cursor.getColumnIndex(EmotionMapEntry.COLUMN_NAME_LAT));
                double lng = cursor.getDouble(cursor.getColumnIndex(EmotionMapEntry.COLUMN_NAME_LNG));
                int emotion = cursor.getInt(cursor.getColumnIndex(EmotionMapEntry.COLUMN_NAME_EMOTION));
                String text = cursor.getString(cursor.getColumnIndex(EmotionMapEntry.COLUMN_NAME_TEXT));
                addMarkerToTheMap(user, emotion,  new LatLng(lat, lng), text);
            } while (cursor.moveToNext());
        }
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
