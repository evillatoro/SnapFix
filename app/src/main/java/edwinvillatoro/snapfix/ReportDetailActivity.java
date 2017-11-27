package edwinvillatoro.snapfix;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import edwinvillatoro.snapfix.objects.Report;

public class ReportDetailActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private ImageView imageView;
    private TextView descriptionTV, problemTypeTV, locationTV, assignedToTV, arrivalTV;
    private LinearLayout mBtnCancel, mBtnChat;
    private String reportID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);

        final WorkerListFragment workerList = new WorkerListFragment();
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.workerList, workerList, "workerList");
        transaction.hide(workerList);
        transaction.commit();

        // initialize reference to Firebase database, specifically pointing at reports
        mDatabase = FirebaseDatabase.getInstance().getReference().child("reports");

        mStorage = FirebaseStorage.getInstance().getReference();

        imageView = (ImageView) findViewById(R.id.imageFromFirebase);
        descriptionTV = (TextView) findViewById(R.id.description);
        problemTypeTV = (TextView) findViewById(R.id.problemTypeTV);
        locationTV = (TextView) findViewById(R.id.locationTV);
        assignedToTV = (TextView) findViewById(R.id.assignedToTV);
        arrivalTV = (TextView) findViewById(R.id.arrivalTV);
        mBtnCancel = (LinearLayout) findViewById(R.id.report_detail_cancel);
        mBtnChat = (LinearLayout) findViewById(R.id.report_detail_chat);

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String userType = bundle.getString("userType");
            if (userType.equals("manager")) {
                assignedToTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleWorkerList(workerList);
                        assign(workerList);
                    }
                });
            }

            reportID = bundle.getString("id");
            // gets the image of the reportID from storage
            StorageReference imagesRef = mStorage.child("images").child("filename_" + reportID);

            // Glide is used to put the image of the report into the image view
            Glide.with(this)
                    .using(new FirebaseImageLoader())
                    .load(imagesRef)
                    .into(imageView);

            // gets all the information of a report by the reportID
            Query mQuery = mDatabase.orderByChild("id").equalTo(reportID);
            mQuery.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Report report = dataSnapshot.getValue(Report.class);
                    //TODO: add the rest of the information of the report to the UI
                    descriptionTV.setText(report.getDescription());
                    problemTypeTV.setText(report.getProblem_type());
                    locationTV.setText(report.getLocation());
                    assignedToTV.setText(report.getAssigned_to());
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

                }
            });
        }

    }

    public void toggleWorkerList(Fragment fragment) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if(fragment.isHidden()) {
            transaction.show(fragment);
        } else {
            transaction.hide(fragment);
        }
        transaction.commit();
    }

    private void assign(WorkerListFragment fragment) {
        String worker = fragment.getSelectedWorker();
        mDatabase.child(reportID).child("assigned_to").setValue(worker);
        assignedToTV.setText(worker);
    }
}
