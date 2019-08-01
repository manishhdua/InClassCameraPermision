package com.example.firebasedb_inclass;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    Button registerButton;

    EditText emailEditText, passwordEditText, confirmEditText;

    FirebaseAuth authDb;

    ProgressDialog progressDialog;

    TextView termsAndConditionsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authDb = FirebaseAuth.getInstance();

        registerButton = findViewById(R.id.loginButton);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.loginPassword);
        confirmEditText = findViewById(R.id.confirmEditText);

        progressDialog = new ProgressDialog(this);

        termsAndConditionsTextView = findViewById(R.id.termsAndConditionsTextView);

        termsAndConditionsTextView.setMovementMethod(new ScrollingMovementMethod());
    }

    /**
     * Register user
     * @param view
     */
    public void registerUser(View view) {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmEditText.getText().toString();

        // show a toast with a warning if email, password, or confirmPassword are empty
        // if empty, simply return; which will exit and stop the register process
        // also, verify that password and confirmPassword are the same value
        // if not thr same value, return; which will stop the register process

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter an email.", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter a password.", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please confirm your password.", Toast.LENGTH_LONG).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("Registering account...");
        progressDialog.show();


        authDb.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Register success!", Toast.LENGTH_SHORT).show();
                            loginInstead(null);
                        } else {
                            Toast.makeText(RegisterActivity.this, "Register failure. Please try again.", Toast.LENGTH_SHORT).show();
                        }

                        progressDialog.dismiss();
                    }
                });

                // use a listener to know when the server responds with information
                // from our call above.
                // check if the operation was successful, if so
                // show a toast indicating success, if not
                // show a toast indicating failure
    }


    public void loginInstead(View view) {
        Intent newActivity = new Intent(this, LoginActivity.class);

        //if (bundle != null) newActivity.putExtras(bundle);

        this.startActivity(newActivity);
    }
}
