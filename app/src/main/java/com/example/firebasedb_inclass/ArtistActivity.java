package com.example.firebasedb_inclass;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SeekBar;
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

public class ArtistActivity extends AppCompatActivity {

    Button buttonAddTrack;
    EditText editTextTrackName;
    RatingBar ratingBar;
    TextView textViewRating, textViewArtist;
    ListView listViewTracks;

    DatabaseReference databaseTracks;

    List<Track> tracks;

    FirebaseAuth authDb;

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


        setContentView(R.layout.activity_artist);

        Intent intent = getIntent();

        databaseTracks = FirebaseDatabase.getInstance().getReference("tracks").child(intent.getStringExtra("ARTIST_ID"));

        buttonAddTrack = findViewById(R.id.buttonAddTrack);
        editTextTrackName = findViewById(R.id.editTextName);
        ratingBar =  findViewById(R.id.ratingBar);
        textViewRating = findViewById(R.id.textViewRating);
        textViewArtist = findViewById(R.id.textViewArtist);
        listViewTracks = findViewById(R.id.listViewTracks);

        tracks = new ArrayList<>();

        textViewArtist.setText(intent.getStringExtra("ARTIST_NAME"));

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
           @Override
               public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                   textViewRating.setText(String.valueOf(rating));
               }
           });


                buttonAddTrack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveTrack();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseTracks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tracks.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Track track = postSnapshot.getValue(Track.class);
                    tracks.add(track);
                }
                TrackList trackListAdapter = new TrackList(ArtistActivity.this, tracks);
                listViewTracks.setAdapter(trackListAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void saveTrack() {
        String trackName = editTextTrackName.getText().toString().trim();
        int rating = ratingBar.getProgress();
        if (!TextUtils.isEmpty(trackName)) {
            String id  = databaseTracks.push().getKey();
            Track track = new Track(id, trackName, rating);
            databaseTracks.child(id).setValue(track);
            Toast.makeText(this, "Track saved", Toast.LENGTH_LONG).show();
            editTextTrackName.setText("");
        } else {
            Toast.makeText(this, "Please enter track name", Toast.LENGTH_LONG).show();
        }
    }
}