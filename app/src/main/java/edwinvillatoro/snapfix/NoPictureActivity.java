package edwinvillatoro.snapfix;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import edwinvillatoro.snapfix.objects.NoPictureProblemEnum;
import edwinvillatoro.snapfix.objects.Report;

public class NoPictureActivity extends AppCompatActivity {

    private Spinner typeSpinner;
    private TextView locationView;
    private EditText descriptionBox;
    private Button submitButton, cancelButton;
    private Toolbar noPicToolbar;

    private FirebaseDatabase database;
    private DatabaseReference reports_ref;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private FirebaseUser currentUser;

    private Double latitude, longitude;
    private ImageView imageView;
    private ProgressDialog mProgressDialog;
    private Bitmap mImageBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_picture);

        typeSpinner = (Spinner) findViewById(R.id.noPic_problemTypeBox);
        locationView = (TextView) findViewById(R.id.noPic_locationBox);
        descriptionBox = (EditText) findViewById(R.id.noPic_descriptionBox);
        submitButton = (Button) findViewById(R.id.noPic_submitButton);
        cancelButton = (Button) findViewById(R.id.noPic_cancelButton);
        noPicToolbar = (Toolbar) findViewById(R.id.toolBar);
        imageView = (ImageView) findViewById(R.id.imageInActivity);

        setSupportActionBar(noPicToolbar);

        mProgressDialog = new ProgressDialog(this);

        // initialize Firebase
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference();

        database = FirebaseDatabase.getInstance();
        reports_ref = database.getReference("reports");

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        mImageBitmap = null;
        Bundle bundle = getIntent().getExtras();
        if (bundle!= null) {
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

        getUserCurrentLocation();
    }


    private void addReport() {
        String type = typeSpinner.getSelectedItem().toString();
        String description = descriptionBox.getText().toString();

        if (!TextUtils.isEmpty(description)) {
            Date date = new Date();
            String id = Long.toString(date.getTime());
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
            String timestamp = sdf.format(date);
            String uid = currentUser.getUid();

            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(4);
            String location = nf.format(latitude) + "," + nf.format(longitude);

            //String id = reports_ref.push().getKey();
            Report report = new Report(id, uid, timestamp, type, location, description);
            reports_ref.child(id).setValue(report);
            uploadPictureToDatabase(id);
        } else {
            Toast.makeText(this, "The description cannot be left blank", Toast.LENGTH_SHORT).show();
        }

    }

    private void getUserCurrentLocation() {
        GpsTracker gt = new GpsTracker(getApplicationContext());
        Location l = gt.getLocation();
        if (l == null){
            Toast.makeText(getApplicationContext(),"GPS unable to get Value",Toast.LENGTH_SHORT).show();
        } else {
            latitude = l.getLatitude();
            longitude = l.getLongitude();
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(4);
            //Toast.makeText(getApplicationContext(),"GPS\nLat = "+lat+"\n lon = "+lon,Toast.LENGTH_SHORT).show();
            locationView.setText(" GPS:     " + nf.format(latitude) + ", " + nf.format(longitude));
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
            StorageReference storageRef = mStorageReference.child("images");

            //name of the image file (added time to have different files to avoid rewrite on the same file)
            StorageReference imagesRef = storageRef.child("filename_" + id);

            //upload image
            UploadTask uploadTask = imagesRef.putBytes(dataBAOS);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // handle failed upload
                    Toast.makeText(getApplicationContext(), "Uploading failed", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // handle successful upload
                    //Toast.makeText(getApplicationContext(), "Uploading successful", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "Your request has been submitted", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                    finish();
                }
            });

        } else {
            //Toast.makeText(getApplicationContext(), "Null", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Your request has been submitted", Toast.LENGTH_SHORT).show();
            mProgressDialog.dismiss();
            finish();
        }
    }
}
