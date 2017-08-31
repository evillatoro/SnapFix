package edwinvillatoro.snapfix;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Toolbar mToolbar;
    private ImageView mImage;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE_REQUEST = 2;
    private Uri filePath;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private ProgressDialog mProgressDialog;
    private Bitmap mImageBitmap;

    private Button mBtnChooseFromGallery, mBtnCamera, mBtnUpload, mBtnNoPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImage = (ImageView) findViewById(R.id.image);
        mBtnCamera = (Button) findViewById(R.id.btnCamera);
        mBtnChooseFromGallery = (Button) findViewById(R.id.btnChoose);
        mBtnUpload = (Button) findViewById(R.id.btnUpload);
        mBtnNoPicture = (Button) findViewById(R.id.btnNoPicture);

        mProgressDialog = new ProgressDialog(this);

        // initialize Firebase
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference().child("images");

        // set custom toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(mToolbar);

        mImageBitmap = null;

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

        mBtnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPictureToDatabase();
            }
        });

        //TODO: make mBtnNoPicture take you to another activity
        mBtnNoPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NoPictureActivity.class);
                startActivity(intent);
            }
        });


        //TODO: check permissions for going into gallery
        checkPermissions();
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void launchCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void uploadPictureToDatabase() {
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
            StorageReference imagesRef = storageRef.child("filename" + new Date().getTime());

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
                    Toast.makeText(getApplicationContext(), "Uploading successful", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            });

            mImageBitmap = null;
        } else {
            Toast.makeText(getApplicationContext(), "Null", Toast.LENGTH_SHORT).show();
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // set the progress dialog
            mProgressDialog.setMessage("Uploading Image...");
            mProgressDialog.show();

            // get the camera image
            Bundle extras = data.getExtras();
            mImageBitmap = (Bitmap) data.getExtras().get("data");

            // set the image into the imageview
            mImage.setImageBitmap(mImageBitmap);

        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            try {
                mImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                mImage.setImageBitmap(mImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkPermissions() {

    }

}
