package edwinvillatoro.snapfix;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edwinvillatoro.snapfix.R;
import edwinvillatoro.snapfix.objects.Report;

public class NoPictureActivity extends AppCompatActivity {

    private Spinner typeSpinner;
    private TextView locationView;
    private EditText descriptionBox;
    private Button submitButton;
    private Button cancelButton;
    private Toolbar noPicToolbar;

    private FirebaseDatabase database;
    private DatabaseReference reports_ref;


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

        setSupportActionBar(noPicToolbar);


        database = FirebaseDatabase.getInstance();
        reports_ref = database.getReference("reports");



        // fills spinner with values
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.problem_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

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
        String type = typeSpinner.getSelectedItem().toString();
        String description = descriptionBox.getText().toString();

        if(!TextUtils.isEmpty(description)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
            String timestamp = simpleDateFormat.format(new Date());

            String id = reports_ref.push().getKey();
            Report report = new Report(id, timestamp, type, "N/A", description);
            locationView.setText(id);
            reports_ref.child(id).setValue(report);
            Toast.makeText(this, "Your request has been submitted", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "The description cannot be left blank", Toast.LENGTH_SHORT).show();
        }

    }
}
