package edwinvillatoro.snapfix;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edwinvillatoro.snapfix.objects.Report;
import edwinvillatoro.snapfix.objects.ReportAdapter;

public class MainActivity
        extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE_REQUEST = 2;
    private DatabaseReference mDatabase;
    private Toolbar mToolbar;
    private Button mBtnRefresh, mBtnChooseFromGallery, mBtnCamera, mBtnNoPicture;
    private RecyclerView mReportsView;
    private List<Report> mReportsList = new ArrayList<>();
    private ReportAdapter mReportAdapter;
    private String mUserType, mUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize all UI view objects
        mBtnRefresh = (Button) findViewById(R.id.btnRefresh);
        mBtnCamera = (Button) findViewById(R.id.btnCamera);
        mBtnChooseFromGallery = (Button) findViewById(R.id.btnChoose);
        mBtnNoPicture = (Button) findViewById(R.id.btnNoPicture);
        mReportsView = (RecyclerView) findViewById(R.id.report_list);

        // ????
        mBtnRefresh.setVisibility(View.GONE);

        // get intent and the user type and ID from LoginActivity
        Intent intent = getIntent();
        mUserType = intent.getStringExtra("userType");
        mUserID = intent.getStringExtra("userID");


        // initialize reference to Firebase database, specifically pointing at reports
        mDatabase = FirebaseDatabase.getInstance().getReference().child("reports");
        mDatabase.addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        // Check dataSnapshot of database and pass in children, then force load view

                        convertDbReportsOnChild((Map<String, Object>) dataSnapshot.getValue(), mUserType);
                        mReportAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        // TODO checks report changes from dataSnapshot
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        // TODO checks report deletion from dataSnapshot
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("Database Error", "Cancelled");
                    }
                });

        /*mDatabase.addValueEventListener(
                // for full database map vs child
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // get map of users in dataSnapshot
                        convertDbReports((Map<String,Object>) dataSnapshot.getValue());
                        System.out.println("We're done loading the initial " +
                                dataSnapshot.getChildrenCount() + " items");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("Database Error", "Listener error prompt");
                    }
                });*/

        // populates the report recycleview with values
        mReportAdapter = new ReportAdapter(this, mReportsList);
        mReportsView.setAdapter(mReportAdapter);
        mReportsView.setLayoutManager(new LinearLayoutManager(this));


        // set custom toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(mToolbar);

        // only regular users can see the buttons to submit reports
        if (!mUserType.equals("user")) {
            mBtnCamera.setVisibility(View.GONE);
            mBtnNoPicture.setVisibility(View.GONE);
            mBtnChooseFromGallery.setVisibility(View.GONE);
        }

        mBtnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCameraIntent();
            }
        });

        mBtnChooseFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchGalleryIntent();
            }
        });

        mBtnNoPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NoPictureActivity.class);
                //intent.putExtra("userType", mUserType);
                //intent.putExtra("userID", mUserID);
                //intent.putExtra("picture", false);
                startActivity(intent);
                //finish();
            }
        });

        // Force load view button
        mBtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReportAdapter.notifyDataSetChanged();

            }});

        // nav drawer functionality
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.navigation_drawer_layout);
        final ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                //hideKeyBoard();
                getCurrentFocus().clearFocus();
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //TODO: check permissions for going into gallery
        checkPermissions();

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 123);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //mReportAdapter.updateData(mReportsList);
    }

    private void convertDbReports(Map<String,Object> reportArr) {

        List<Report> reportArray = new ArrayList<>();

        // iterate through each report, converting it to go into the list of reports
        for (Map.Entry<String, Object> entry : reportArr.entrySet()){
            //Get report map
            Map singleReport = (Map) entry.getValue();
            String id = singleReport.get("id").toString();
            String userID = (String) singleReport.get("userID");
            String timestamp = (String) singleReport.get("timestamp");
            String problem_type = (String) singleReport.get("problem_type");
            String location = (String) singleReport.get("location");
            String description = (String) singleReport.get("description");
            String assigned_to = (String) singleReport.get("assigned_to");
            reportArray.add(new Report(id, userID, timestamp, problem_type, location,
                    description, assigned_to));
        }
        mReportsList = reportArray;
    }

    private void convertDbReportsOnChild(Map<String,Object> reportArr, String mUserType) {
        // iterate through a report, loading in values into report object


        String id = "";
        String userID = "";
        String timestamp = "";
        String problem_type = "";
        String location = "";
        String description = "";
        String assigned_to = "";

        for (Map.Entry<String, Object> entry : reportArr.entrySet()) {
            System.out.println(entry);
            if (entry.getKey().equals("id")) {
                id = entry.getValue().toString();
            }
            if (entry.getKey().equals("userID")) {
                userID = (String) entry.getValue();
            }
            if (entry.getKey().equals("timestamp")) {
                timestamp = (String) entry.getValue();
            }
            if (entry.getKey().equals("problem_type")) {
                problem_type = (String) entry.getValue();
            }
            if (entry.getKey().equals("location")) {
                location = (String) entry.getValue();
            }
            if (entry.getKey().equals("description")) {
                description = (String) entry.getValue();
            }
            if (entry.getKey().equals("assigned_to")) {
                assigned_to = (String) entry.getValue();
            }
        }

        // workers can only see reports assigned to them
        // regular users can only see reports they submitted
        // managers can see all reports
        Report report = new Report(id, userID, timestamp, problem_type, location,
                description, assigned_to);
        if (mUserType.equals("worker")) {
            if (assigned_to.equals(mUserID)) {
                mReportsList.add(report);
            }
        } else if (mUserType.equals("user")) {
            if (userID.equals(mUserID)) {
                mReportsList.add(report);
            }
        } else {
            mReportsList.add(report);
        }


    }

    private void launchGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = new Intent(MainActivity.this, NoPictureActivity.class);
        //intent.putExtra("userType", mUserType);
        //intent.putExtra("userID", mUserID);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // get the camera image
            Bundle extras = data.getExtras();
            intent.putExtra("camera", true);
            intent.putExtra("imageBitmap", (Bitmap) extras.get("data"));
            startActivity(intent);
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null
                && data.getData() != null) {
            Uri filePath = data.getData();
            intent.putExtra("gallery", true);
            intent.putExtra("filePath", filePath);
            startActivity(intent);
        }
    }

    // TODO
    private void checkPermissions() {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit")
                .setMessage("Do you want to sign out?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }
                });
        builder.create().show();
    }

}
