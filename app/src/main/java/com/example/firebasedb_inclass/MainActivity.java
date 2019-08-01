package com.example.firebasedb_inclass;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //views
    EditText editTextArtistName;
    Spinner spinnerGenre;
    Button buttonAddArtist;

    //views
    TextView textViewArtistName;
    TextView textViewArtistGenre;
    ListView listViewArtists;

    //data objects
    List<Artist> artists;
    DatabaseReference db;

    FirebaseAuth authDb;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authDb = FirebaseAuth.getInstance();

        // authenticate

        if(authDb.getCurrentUser() == null){
            //close this activity
            finish();
            //opening login activity
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }



        setContentView(R.layout.activity_main);

        db = FirebaseDatabase.getInstance().getReference("artists");

        //instantiate our views
        editTextArtistName = findViewById(R.id.editTextArtistName);
        spinnerGenre = findViewById(R.id.spinnerGenre);
        buttonAddArtist = findViewById(R.id.buttonAddToDatabase);

        toolbar = findViewById(R.id.appToolbar);
        setSupportActionBar(toolbar);

        listViewArtists = findViewById(R.id.listViewArtists);
        artists = new ArrayList<>();

        listViewArtists.setOnTouchListener(new ListView.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

                listViewArtists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //getting the selected artists
                        Artist a = artists.get(position);

                        //creating and intent
                        Intent i = new Intent(getApplicationContext(), ArtistActivity.class);

                        //putting artist name and id to intent
                        i.putExtra("ARTIST_ID", a.getArtistId());
                        i.putExtra("ARTIST_NAME", a.getArtistName());

                        //starting the activity with intent
                        startActivity(i);
                    }
                });

    }

    @Override
    protected void onStart(){
        super.onStart();

        //event listender for the db
        db.addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //clear our list
                artists.clear();

                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Artist artist = postSnapshot.getValue(Artist.class);
                    artists.add(artist);
                }

                //create adapter & attach it to our listview
                ArtistListAdapter myArtistAdapter =
                        new ArtistListAdapter(MainActivity.this,artists);
                listViewArtists.setAdapter(myArtistAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addArtist(View view) {
        //code that runs when button is clicked
        String name = editTextArtistName.getText().toString().trim();
        String genre = spinnerGenre.getSelectedItem().toString();

        if(!TextUtils.isEmpty(name)){
            //runs if there is a name typed
            String id = db.push().getKey();

            Artist a = new Artist(id,name,genre);
            db.child(id).setValue(a);

            //update our UI
            editTextArtistName.setText("");
            Toast.makeText(this, "Artist Added!", Toast.LENGTH_LONG).show();
        }else{
            //else runs if the edit text IS empty
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
        }

    }

    public void logout(View view) {
        authDb.signOut();

        finish();

        Intent newActivity = new Intent(this, LoginActivity.class);

        startActivity(newActivity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.action_main) {
            return true;
        } else if (id == R.id.action_profile) {
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

}
