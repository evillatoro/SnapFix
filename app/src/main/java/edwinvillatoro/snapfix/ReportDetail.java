package edwinvillatoro.snapfix;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
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

public class ReportDetail extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private ImageView imageView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);

        // initialize reference to Firebase database, specifically pointing at reports
        mDatabase = FirebaseDatabase.getInstance().getReference().child("reports");

        mStorage = FirebaseStorage.getInstance().getReference();

        imageView = (ImageView) findViewById(R.id.imageFromFirebase);
        textView = (TextView) findViewById(R.id.description);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String reportID = bundle.getString("id");
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
                    Toast.makeText(getApplicationContext(), "" + report.getId(), Toast.LENGTH_SHORT).show();
                    //TODO: add the rest of the information of the report to the UI
                    textView.setText(report.getDescription());
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
}
