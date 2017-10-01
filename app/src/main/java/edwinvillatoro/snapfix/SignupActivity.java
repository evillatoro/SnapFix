package edwinvillatoro.snapfix;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    private EditText emailText, passwordText;
    private Button registerButton, loginButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();

        emailText = (EditText) findViewById(R.id.email);
        passwordText = (EditText) findViewById(R.id.password);
        registerButton = (Button) findViewById(R.id.register_button);
        loginButton = (Button) findViewById(R.id.login_button);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchRegisterIntent();
                //finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }


    private void launchRegisterIntent() {
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        if(TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Email address is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Password is required", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        if(!task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent goToMainActivityIntent = new Intent(SignupActivity.this, MainActivity.class);
                            goToMainActivityIntent.putExtra("TYPE", "user");
                            goToMainActivityIntent.putExtra("userID", auth.getCurrentUser().getUid());
                            startActivity(goToMainActivityIntent);
                            finish();
                        }
                    }
                });
    }

}
