package com.rayson.eldoretonlinemarket.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rayson.eldoretonlinemarket.BoundLocationManager;
import com.rayson.eldoretonlinemarket.FirebaseUI;
import com.rayson.eldoretonlinemarket.R;
import com.rayson.eldoretonlinemarket.UserDestinationInfo;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment implements LifecycleOwner, GoogleMap.OnMarkerClickListener {
    private GoogleMap mMap;
    private FirebaseUser mUser;
    // LifecycleOwner lifecycleOwner;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final LatLng mDefaultLocation = new LatLng(0.5143, 35.2698);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;
    private boolean mLocationPermissionGranted;
    private LocationListener mGpsListener = new MyLocationListener();

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.


    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private Location mLastKnownLocation;
    private Context mContext;
    private boolean mStoragePermissionGranted;
    private String longitude;
    private String latitude;
    private Boolean open;
    private List<UserDestinationInfo> stagesList;
    private TextView edtLatitude,edtLongitude;
    private EditText edtNickname;
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private String TAG= "MainActivity";

    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);
        mContext = getContext();
        getLocationPermission();
        DevicePermission();
        open = false;
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
        Places.initialize(getContext(), getString(R.string.google_maps_key));

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap map) {
                mMap = map;
                getDeviceLocation();
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setZoomGesturesEnabled(true);
                mMap.getUiSettings().setCompassEnabled(true);
                updateLocationUI();
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                getLocations();


                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                    @Override
                    public View getInfoWindow(Marker arg0) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {

                        LinearLayout info = new LinearLayout(mContext);
                        info.setOrientation(LinearLayout.VERTICAL);

                        TextView title = new TextView(mContext);
                        title.setTextColor(Color.parseColor("#0BF5AB"));
                        SpannableString spanTitle = new SpannableString(marker.getTitle());
                        spanTitle.setSpan(new UnderlineSpan(), 0, spanTitle.length(), 0);
                        title.setGravity(Gravity.CENTER);
                        title.setTypeface(null, Typeface.BOLD);
                        title.setTextSize(22);
                        title.setText(spanTitle);

                        TextView snippet = new TextView(mContext);
                        snippet.setTextColor(Color.BLACK);
                        snippet.setTypeface(null, Typeface.BOLD);
                        snippet.setTextSize(20);
                        snippet.setText(marker.getSnippet());

                        info.addView(title);
                        info.addView(snippet);

                        return info;
                    }
                });

                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(final LatLng latLng) {
                        if (mUser != null) {
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(latLng);
                            markerOptions.title("Click to add new location");
                            mMap.clear();
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                            mMap.addMarker(markerOptions);
                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setTitle("Confirmation.")
                                            .setMessage("Are you sure you want to add this location to your list?")
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                    mMap.setOnMarkerClickListener(MapsFragment.this);
                                                    getLocations();
                                                }
                                            })
                                            .setCancelable(false)
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    //   Integer clickCount = (Integer) marker.getTag();
                                                    longitude = String.valueOf(latLng.longitude);
                                                    latitude = String.valueOf(latLng.latitude);
                                                    showDialog(latitude, longitude);
                                                }
                                            });
                                    AlertDialog alertDialogConfirm = builder.create();
                                    alertDialogConfirm.show();
                                    return true;
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "Please login to continue", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getContext(), FirebaseUI.class));
                        }
                    }

                });
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                        if (open) {
                            marker.hideInfoWindow();
                            open = false;
                        } else {
                            marker.showInfoWindow();
                            open = true;
                        }
                        return true;
                    }
                });
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        LatLng markerPos = marker.getPosition();
                        latitude = String.valueOf(markerPos.latitude);
                        longitude = String.valueOf(markerPos.longitude);
                        if (mUser != null) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Choose an action");
                            String[] options = {"Delete Location", "Edit Location info"};
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            db.collection(mUser.getEmail()).document(encode(latitude) + ":" + encode(longitude)).collection("allUserLocations").document()
                                                    .delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                            Toast.makeText(mContext, "successfully deleted!", Toast.LENGTH_LONG).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error deleting document", e);
                                                        }
                                                    });
                                            break;
                                        case 1:
                                            showEditDialog(latitude, longitude);
                                            break;

                                    }
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            Toast.makeText(getContext(), "Please login to get more information on this stage", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        return rootView;
    }
    private void showDialog(final String latitude, final String longitude) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Add new Location");
        alertDialog.setMessage("Fill all the details.");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.activity_add_location,null);

        edtLatitude = add_menu_layout.findViewById(R.id.textViewAddLocationLatitude);
        edtLatitude.setText(latitude);
        edtLongitude = add_menu_layout.findViewById(R.id.textViewAddLocationLongitude);
        edtLongitude.setText(longitude);
        edtNickname = add_menu_layout.findViewById(R.id.editTextLocationNickNameAdd);

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.logo);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                UserDestinationInfo newDest = new UserDestinationInfo(latitude,longitude,mUser.getEmail(),edtNickname.getText().toString());
                if(newDest !=  null)
                {

                    db.collection(mUser.getEmail()).document(encode(latitude)+ ":" + encode(longitude)).collection("allUserLocations").document(encode(latitude)+ ":" + encode(longitude))
                            .set(newDest)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                                    Toast.makeText(getContext(),"Stage saved successfully",Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(),"Not saved. Try again later.",Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        alertDialog.show();

    }
    private void showEditDialog(String lat,String longt) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Edit current Location");
        alertDialog.setMessage("Fill all the details.");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.activity_add_location,null);

        edtLatitude = add_menu_layout.findViewById(R.id.textViewAddLocationLatitude);
        edtLongitude.setText(lat);
        edtLongitude = add_menu_layout.findViewById(R.id.textViewAddLocationLongitude);
        edtLongitude.setText(longt);
        edtNickname = add_menu_layout.findViewById(R.id.editTextLocationNickNameAdd);

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.logo);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                UserDestinationInfo newDest = new UserDestinationInfo(edtLatitude.getText().toString(),edtLongitude.getText().toString(),mUser.getEmail(),edtNickname.getText().toString());
                if(newDest !=  null)
                {

                    db.collection(mUser.getEmail()).document(encode(latitude)+ ":" + encode(longitude)).collection("allUserLocations").document(encode(latitude)+ ":" + encode(longitude))
                            .set(newDest)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                                    Toast.makeText(getContext(),"Location saved successfully",Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(),"Not saved. Try again later.",Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        alertDialog.show();

    }
    private void getLocations() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        stagesList = new ArrayList<>();
        db.collectionGroup("allUserLocations").whereEqualTo("userName",mUser.getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "listen:error", e);
                    return;
                }

                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                        case MODIFIED:
                        case REMOVED:
                            queryDocSnapShotMethod(queryDocumentSnapshots);
                            break;
                    }
                }

            }
        });

    }
    public static String encode(String coOrdns){
        return coOrdns.replace(".",",");
    }
    private void queryDocSnapShotMethod(QuerySnapshot queryDocumentSnapshots) {
        mMap.clear();
        open = false;
        stagesList = new ArrayList<>();
        if (!queryDocumentSnapshots.isEmpty()){
            for (DocumentSnapshot snapshot:queryDocumentSnapshots)
                stagesList.add(snapshot.toObject(UserDestinationInfo.class));
            int size = stagesList.size();
            int position;
            for (position=0;position<size;position++) {
                String snip = "Location info :" + "\n";
                UserDestinationInfo markerInfo = stagesList.get(position);
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(markerInfo.latitude),Double.parseDouble(markerInfo.longitude)))
                        .icon(bitmapDescriptorFromVector(mContext,R.drawable.ic_baseline_location))
                        .title(markerInfo.destinationNickName)
                        .snippet(snip)
                );
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(Double.parseDouble(markerInfo.latitude),
                                Double.parseDouble(markerInfo.longitude)), DEFAULT_ZOOM));

            }
        }
    }


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(mContext, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {

                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        mStoragePermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    bindLocationListener();
                }
                else {
                    Toast.makeText(mContext,"Location permission denied",Toast.LENGTH_LONG).show();
                }
            }
            case PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
            {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mStoragePermissionGranted = true;
                }
                else {
                    DevicePermission();
                }
            }
        }
        updateLocationUI();
    }
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(mContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            bindLocationListener();
        }
        else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(mContext,"The app needs this permission to show you your places.",Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    private void DevicePermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            mStoragePermissionGranted = true;
        }
        else if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(mContext,"The app needs this permission to upload your profile image.",Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
        else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        if (open) {
            marker.hideInfoWindow();
            open = false;
        } else {
            marker.showInfoWindow();
            open = true;
        }
        return true;
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            //textView.setText(location.getLatitude() + ", " + location.getLongitude());
            mLastKnownLocation = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getContext(),
                    "Provider enabled: " + provider, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    }
    private void bindLocationListener() {
        BoundLocationManager.bindLocationListenerIn(this, mGpsListener, getContext());
    }

}