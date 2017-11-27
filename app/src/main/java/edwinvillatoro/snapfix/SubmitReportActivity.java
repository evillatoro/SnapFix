package edwinvillatoro.snapfix;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edwinvillatoro.snapfix.objects.NoPictureProblemEnum;
import edwinvillatoro.snapfix.objects.Report;

public class SubmitReportActivity extends AppCompatActivity {

    private Spinner typeSpinner;
    private TextView locationView;
    private EditText descriptionBox;
    private LinearLayout submitButton, cancelButton;
    private ImageView imageView;
    private ProgressDialog mProgressDialog;
    private Bitmap mImageBitmap;

    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private FirebaseUser mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_report);

        typeSpinner = (Spinner) findViewById(R.id.noPic_problemTypeBox);
        locationView = (TextView) findViewById(R.id.noPic_locationBox);
        descriptionBox = (EditText) findViewById(R.id.noPic_descriptionBox);
        submitButton = (LinearLayout) findViewById(R.id.noPic_submitButton);
        cancelButton = (LinearLayout) findViewById(R.id.noPic_cancelButton);
        imageView = (ImageView) findViewById(R.id.imageInActivity);

        mProgressDialog = new ProgressDialog(this);

        // initialize Firebase
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference("reports");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mImageBitmap = null;
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            if (bundle.getBoolean("camera")) {
                mImageBitmap = getIntent().getExtras().getParcelable("imageBitmap");
                imageView.setImageBitmap(mImageBitmap);
            } else if (bundle.getBoolean("gallery")) {
                Uri filePath = bundle.getParcelable("filePath");
                try {
                    mImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    imageView.setImageBitmap(mImageBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            imageView.setVisibility(View.GONE);
        }

        // fills spinner with values
        typeSpinner.setAdapter(new ArrayAdapter<NoPictureProblemEnum>(this,
                android.R.layout.simple_spinner_item, NoPictureProblemEnum.values()));

        // automatically retrieves and calculates closest GPS address
        GPSLocationListener gps = new GPSLocationListener(getApplicationContext());
        Location point = gps.getLocation();
        if (point == null){
            Toast.makeText(getApplicationContext(),"Unable to retrieve GPS location",
                    Toast.LENGTH_SHORT).show();
        } else {
            locationView.setText(gps.getAddress(point));
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReport();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    private void addReport() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE,  MMM dd, yyyy  (hh:mm)", Locale.ENGLISH);

        String id = Long.toString(date.getTime());
        String uid = mCurrentUser.getUid();
        String imageID = "";
        if(mImageBitmap != null) {
            imageID = "filename_" + id;
        }
        String timestamp = sdf.format(date);
        String assigned_to = "Pending";
        String type = typeSpinner.getSelectedItem().toString();
        String description = descriptionBox.getText().toString();
        String location = locationView.getText().toString();


        if (!TextUtils.isEmpty(description)) {
            Report report = new Report(id, uid, timestamp, type, location, description, assigned_to, imageID);
            mDatabase.child(id).setValue(report);
            uploadPictureToDatabase(id);
        } else {
            Toast.makeText(this, "The description cannot be left blank", Toast.LENGTH_SHORT).show();
        }
    }


    private void uploadPictureToDatabase(String id) {
        // set the progress dialog
        mProgressDialog.setMessage("Uploading Image...");
        mProgressDialog.show();

        if (mImageBitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] dataBAOS = baos.toByteArray();

            /*************** UPLOADS THE PIC TO FIREBASE***************/
            StorageReference storageRef = mStorage.child("images");

            //name of the image file (added time to have different files to avoid rewrite on the same file)
            StorageReference imagesRef = storageRef.child("filename_" + id);

            //upload image
            UploadTask uploadTask = imagesRef.putBytes(dataBAOS);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getApplicationContext(), "Uploading failed", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(), "Your request has been submitted", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                    finish();
                }
            });

        } else {
            Toast.makeText(this, "Your request has been submitted", Toast.LENGTH_SHORT).show();
            mProgressDialog.dismiss();
            finish();
        }
    }

    // disables back press. Forces user to click cancel to exit
    @Override
    public void onBackPressed() {

    }
}
