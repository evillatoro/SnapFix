package edwinvillatoro.snapfix;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText mEmailInput, mPasswordInput;
    private Button mBtnLogin, mBtnReset, mBtnRegister;
    private FirebaseAuth auth;
    private DatabaseReference mUsersReference;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailInput = (EditText) findViewById(R.id.email);
        mPasswordInput = (EditText) findViewById(R.id.password);
        mBtnRegister = (Button) findViewById(R.id.register_button);
        mBtnLogin = (Button) findViewById(R.id.login_button);
        mBtnReset = (Button) findViewById(R.id.reset_button);

        // initialze a database reference pointing at the users
        mUsersReference = FirebaseDatabase.getInstance().getReference().child("users");
        // initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance();
        // don't have to sign in if never signed out
        if(auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                finish();
            }
        });
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchLoginIntent();
            }
        });
        mBtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchSendEmailIntent();
            }
        });
    }


    private void launchLoginIntent() {
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

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, R.string.login_fail,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            //look up user type
                            mCurrentUser = auth.getCurrentUser();
                            final String id = mCurrentUser.getUid();
                            mUsersReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String accountType = (String) dataSnapshot.child(id)
                                            .child("type").getValue();
                                    Intent goToMainActivityIntent = new Intent(LoginActivity.this,
                                            MainActivity.class);
                                    if (accountType == null) {
                                        goToMainActivityIntent.putExtra("userType", "user");
                                        goToMainActivityIntent.putExtra("userID", id);
                                        startActivity(goToMainActivityIntent);
                                        finish();
                                    } else {
                                        // else the account is a worker or manager
                                        Log.d(TAG, "account is a " + accountType);
                                        goToMainActivityIntent.putExtra("userType", accountType);
                                        goToMainActivityIntent.putExtra("userID", id);
                                        startActivity(goToMainActivityIntent);
                                        finish();
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                });
    }

    private void launchSendEmailIntent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText inputScreen = new EditText(getApplicationContext());
        builder.setView(inputScreen);
        builder.setTitle("Forgot Password");
        builder.setMessage("Please enter your email address. A reset password will be sent shortly.");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // TODO : fix bug when click "OK" but input is empty
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = inputScreen.getText().toString().trim();
                if(!TextUtils.isEmpty(email)) {
                    auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, R.string.reset_email_success,
                                                Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(LoginActivity.this, R.string.reset_email_fail,
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
                dialog.cancel();

            }
        });
        builder.create().show();
    }
}
