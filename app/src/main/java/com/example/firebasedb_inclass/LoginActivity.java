package com.example.firebasedb_inclass;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    TextView termsAndConditionsTextView;
    private FirebaseAuth db;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // instantiate
        db = FirebaseAuth.getInstance();

        //if the objects getcurrentuser method is not null
        //means user is already logged in
        if(db.getCurrentUser() != null){
            //close this activity
            finish();
            //opening main activity
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.loginPassword);
        progressDialog = new ProgressDialog(this);
        termsAndConditionsTextView = findViewById(R.id.termsAndConditionsTextView);

        termsAndConditionsTextView.setMovementMethod(new ScrollingMovementMethod());
    }


    public void login(View view) {
        String email = emailEditText.getText().toString().trim();
        String password  = passwordEditText.getText().toString().trim();


        //checking if email and passwords are empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        //logging in the user
        db.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        //if the task is successfull
                        if(task.isSuccessful()){
                            //start the profile activity
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Invalid Login", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    public void registerInsteadButtonClick(View view) {
        Intent newActivity = new Intent(this, RegisterActivity.class);

        //if (bundle != null) newActivity.putExtras(bundle);

        this.startActivity(newActivity);
    }
}
