package com.example.firebasedb_inclass;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebasedb_inclass.BuildConfig;
import com.example.firebasedb_inclass.LoginActivity;
import com.example.firebasedb_inclass.MainActivity;
import com.example.firebasedb_inclass.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ProfileActivity extends AppCompatActivity {


    FirebaseAuth authDb;
    TextView emailTextView;
    Toolbar appToolbar;

    // new vars for photo
    ImageView imageView;
    ImageView profileImageView;
    Button cameraButton;

    String currentPhotoPath;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_LOAD_IMAGE = 101;
    static final int REQUEST_PROFILE_IMAGE = 202;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // connect to db
        authDb = FirebaseAuth.getInstance();
        user = authDb.getCurrentUser();
        imageView = findViewById(R.id.imageView);
        // show user email
        emailTextView = findViewById(R.id.emailTextView);
        emailTextView.setText(authDb.getCurrentUser().getEmail());

        // toolbar
        appToolbar = findViewById(R.id.appToolbar);
        setSupportActionBar(appToolbar);

        // image view
        profileImageView = findViewById(R.id.profileImageView);
        cameraButton = findViewById(R.id.cameraButton);

        // check permissions, ask if not already granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            cameraButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
        else {
            if (user != null) {
                for (UserInfo profile : user.getProviderData()) {
                    // Id of the provider (ex: google.com)
                    String providerId = profile.getProviderId();

                    // UID specific to the provider
                    String uid = profile.getUid();

                    Uri photoUrl = profile.getPhotoUrl();

                    if (photoUrl != null) {

                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                        intent.putExtra("profilePhotoUrl", photoUrl);
                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
        }


    }



    // inflate the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_main:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                return true;
            case R.id.action_profile:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void logout(View view) {
        authDb.signOut();
        finish();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    public void takePhoto(View view) {
        //Toast.makeText(ProfileActivity.this, "We're working on it.  Relax.", Toast.LENGTH_LONG).show();
        // use an intent to invoke the camera
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
        /*
        // ensure we have an app available to perform this action
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();

                // if we get a file back, get the current file location
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photoFile);

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
            catch (IOException ex) {
                Toast.makeText(ProfileActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
       */
    }

    private File createImageFile() throws IOException {
        // uniquely name the file with the current date & time
        // save as a .jpg
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        // check if DIRECTORY_DCIM/Camera directory exists.  create if not
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(timeStamp, ".jpg", storageDir);

        // set the file path & return the image
        currentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Uri imageUri = Uri.parse(currentPhotoPath);
            //File file = new File(imageUri.getPath());

            profileImageView.setImageURI(imageUri);
            saveProfilePhoto(imageUri);
        }
        else if (requestCode == REQUEST_LOAD_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            profileImageView.setImageURI(selectedImage);
            saveProfilePhoto(selectedImage);
        }
        else if (requestCode == REQUEST_PROFILE_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            profileImageView.setImageURI(selectedImage);
        }
    }

    public void browsePhotos(View view) {
        // start intent to let user browse the device's images
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_LOAD_IMAGE);
    }

    public void saveProfilePhoto(Uri imageUri) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(imageUri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, "Profile Image Saved", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


   /* @Override
    public void onStart() {
        super.onStart();

        // display any current profile image for user with the provider they logged in with
        //FirebaseUser user = authDb.getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                // Id of the provider (ex: google.com)
                String providerId = profile.getProviderId();

                // UID specific to the provider
                String uid = profile.getUid();

                Uri photoUrl = profile.getPhotoUrl();

                if (photoUrl != null) {
                    //profileImageView.setImageURI(photoUrl);
                }
            }
        }
    }*/


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                cameraButton.setEnabled(true);

                if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    if (user != null) {
                        for (UserInfo profile : user.getProviderData()) {
                            // Id of the provider (ex: google.com)
                            String providerId = profile.getProviderId();

                            // UID specific to the provider
                            String uid = profile.getUid();

                            Uri photoUrl = profile.getPhotoUrl();

                            if (photoUrl != null) {
                                profileImageView.setImageURI(photoUrl);
                            }
                        }
                    }
                }
            }
        }
    }
}

