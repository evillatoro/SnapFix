package edwinvillatoro.snapfix;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edwinvillatoro.snapfix.objects.WorkerAdapter;
public class WorkerListActivity extends AppCompatActivity {
    private RecyclerView workersView;
    private List<String> workersList = new ArrayList<>();
    private DatabaseReference db;
    private WorkerAdapter workerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workers_list);

        //find the ui element for the list
        workersView = (RecyclerView) findViewById(R.id.workers_list);

        //gets a reference to the database, looking at the user list
        db = FirebaseDatabase.getInstance().getReference().child("users");

        db.addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        updateView((Map<String, Object>) dataSnapshot.getValue());
                        workerAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    }
                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                    }
                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("Database Error", "Cancelled");
                    }
                });

        //populate recyclerview
        workerAdapter = new WorkerAdapter(getApplicationContext(), workersList);
        workersView.setAdapter(workerAdapter);
        workersView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void updateView(Map<String,Object> workers) {
        String type = "";
        for (Map.Entry<String, Object> entry : workers.entrySet()) {
            if (entry.getKey().equals("type")){
                type = (String) entry.getValue();
                if (type.equals("worker")) {
                    workersList.add("temporary");
                }
            }
        }

    }
}
