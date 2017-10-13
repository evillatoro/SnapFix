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

    private EditText mEmailInput, mPasswordInput;
    private Button mBtnRegister, mBtnLogin;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mEmailInput = (EditText) findViewById(R.id.email);
        mPasswordInput = (EditText) findViewById(R.id.password);
        mBtnRegister = (Button) findViewById(R.id.register_button);
        mBtnLogin = (Button) findViewById(R.id.login_button);

        auth = FirebaseAuth.getInstance();

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchRegisterIntent();
            }
        });

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
    }


    private void launchRegisterIntent() {
        String email = mEmailInput.getText().toString().trim();
        String password = mPasswordInput.getText().toString().trim();

        if(TextUtils.isEmpty(email)) {
            Toast.makeText(this, R.string.require_email, Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.require_password, Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, R.string.login_fail,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Intent goToMainActivityIntent = new Intent(SignupActivity.this,
                                    MainActivity.class);
                            goToMainActivityIntent.putExtra("userType", "user");
                            goToMainActivityIntent.putExtra("userID", auth.getCurrentUser().getUid());
                            startActivity(goToMainActivityIntent);
                            finish();
                        }
                    }
                });
    }

}
