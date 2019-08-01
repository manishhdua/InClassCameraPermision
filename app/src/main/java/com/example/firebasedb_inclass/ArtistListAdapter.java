package com.example.firebasedb_inclass;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ArtistListAdapter extends ArrayAdapter<Artist> {

    //variables
    private Activity context;
    List<Artist> artists;

    //constructor that inherits from the super
    public ArtistListAdapter(Activity context, List<Artist> artists){
        super(context, R.layout.layout_artist_list, artists);
        this.context = context;
        this.artists = artists;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        //this method will instantiate our layout file from the layout_artist_list.xml into
        //our view objects



        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_artist_list
                , null
                , true);

        TextView textViewName = listViewItem.findViewById(R.id.textViewName);
        TextView textViewGenre = listViewItem.findViewById(R.id.textViewGenre);

        Artist artist = artists.get(position);
        textViewName.setText(artist.getArtistName());
        textViewGenre.setText(artist.getArtistGenre());

        //replace later
        return listViewItem;
    }

}
