package edwinvillatoro.snapfix;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import edwinvillatoro.snapfix.R;

public class NoPictureActivity extends AppCompatActivity {

    private Spinner typeSpinner;
    private TextView locationView;
    private Button submitButton;
    private Button cancelButton;
    private Toolbar noPicToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_picture);

        typeSpinner = (Spinner) findViewById(R.id.noPic_problemTypeBox);
        locationView = (TextView) findViewById(R.id.noPic_locationBox);
        submitButton = (Button) findViewById(R.id.noPic_submitButton);
        cancelButton = (Button) findViewById(R.id.noPic_cancelButton);
        noPicToolbar = (Toolbar) findViewById(R.id.toolBar);

        setSupportActionBar(noPicToolbar);

        // fills spinner with values
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.problem_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
