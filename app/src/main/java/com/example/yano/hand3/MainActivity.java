package com.example.yano.hand3;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;



import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import java.util.Map;
import android.util.Log;
import com.beacapp.BeaconEventListener;
import com.beacapp.FireEventListener;
import com.beacapp.JBCPException;
import com.beacapp.JBCPManager;
import com.beacapp.ShouldUpdateEventsListener;
import com.beacapp.UpdateEventsListener;
import com.beacapp.service.BeaconEvent;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap;

    private JBCPManager jbcpManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);


        activate();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(35.69643, 139.731058);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    private void activate()
    {
        //通信が走るため、別スレッドでの処理にする
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    jbcpManager = JBCPManager.getManager(MainActivity.this,
                            "アクティベーションキー",
                            "シークレットキー",
                            null);
                } catch (JBCPException e) {
                    return;
                }

                if (jbcpManager == null){
                    return;
                }
                // リスナーを登録
                try {
                    jbcpManager.setUpdateEventsListener(updateEventsListener);
                } catch (JBCPException e) {
                    e.printStackTrace();
                }
                try {
                    jbcpManager.setShouldUpdateEventsListener(shouldUpdateEventsListener);
                } catch (JBCPException e) {
                    e.printStackTrace();
                }
                try {
                    jbcpManager.setFireEventListener(fireEventListener);
                } catch (JBCPException e) {
                    e.printStackTrace();
                }

                // デバッグ用に使う
                /*
                JBCPManager.SCAN_MODE = 2;
                jbcpManager.setBeaconEventListener(beaconEventListener);
                */

                // イベントを更新する
                try {
                    jbcpManager.startUpdateEvents();
                } catch (JBCPException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void showExplanationDialog(final String permissions[], final int requestCode) {
        ActivityCompat.requestPermissions(MainActivity.this,
                permissions,requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (0 == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                activate();
            }
        }
    }

    // リスナーを生成
    private UpdateEventsListener updateEventsListener = new UpdateEventsListener() {
        @Override
        public void onProgress(int i, int i1) {
            //何もしない

        }

        @Override
        public void onFinished(JBCPException e) {
            if (e != null)
            {
                try {
                    jbcpManager.startScan();
                } catch (JBCPException e1) {
                    e1.printStackTrace();
                }
            }

        }
    };

    // リスナーを生成
    private ShouldUpdateEventsListener shouldUpdateEventsListener = new ShouldUpdateEventsListener() {
        @Override
        public boolean shouldUpdate(Map<String, Object> map) {
            return true;
        }
    };

    // リスナーを生成
    private FireEventListener fireEventListener = new FireEventListener() {
        @Override
        public void fireEvent(JSONObject jsonObject) {
            JSONObject action_data = jsonObject.optJSONObject("action_data");
            String action = action_data.optString("action");


            // URLの場合
            if(action.equals("jbcp_open_url"))
            {
                Log.d("DEBUG",action_data.optString("url"));
            }
            //画像の場合
            else if(action.equals("jbcp_open_image"))
            {
                Log.d("DEBUG",action_data.optString("image"));
            }
            //カスタムの場合
            else if(action.equals("jbcp_custom_key_value"))
            {
                Log.d("DEBUG",action_data.optString("key_values"));
            }
            //テキストの場合
            else if(action.equals("jbcp_open_text"))
            {
                Log.d("DEBUG",action_data.optString("text"));
            }

        }
    };

    //　リスナーを生成
    public BeaconEventListener beaconEventListener = new BeaconEventListener() {
        @Override
        public boolean targetBeaconDetected(BeaconEvent beaconEvent) {
            // CMSで検知対象に登録されているBeacon
            Log.d("DEBUG",beaconEvent.uuid);
            return false;
        }

        @Override
        public boolean nonTargetBeaconDetected(BeaconEvent beaconEvent) {
            // CMSで検知対象になっていないBeacon
            Log.d("DEBUG",beaconEvent.uuid);
            return false;
        }
    };


}
