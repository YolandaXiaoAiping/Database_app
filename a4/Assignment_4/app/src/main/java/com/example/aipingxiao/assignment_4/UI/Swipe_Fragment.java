package com.example.aipingxiao.assignment_4.UI;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aipingxiao.assignment_4.Database.DatabaseHandler;
import com.example.aipingxiao.assignment_4.PersonInformation;
import com.example.aipingxiao.assignment_4.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Aiping Xiao on 2016-02-11.
 */
public class Swipe_Fragment extends Fragment {

    TextView ShowName;
    ImageView ShowImage;
    TextView ShowIntro;
    Button SearchButton;
    private int mID;
    private int mNum = 0;

    private static final String PersonID = "personID";


    //get the position id from maiN activity
    public static Swipe_Fragment newInstance(int position){
        Swipe_Fragment fragment = new Swipe_Fragment();
        Bundle bundle = new Bundle();
        bundle.putInt(PersonID,position);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static Swipe_Fragment newInstance(){
        Swipe_Fragment swipe_fragment = new Swipe_Fragment();
        return swipe_fragment;
    }

    public static Swipe_Fragment newInstance(int position,int count){
        Swipe_Fragment swipe_fragment = newInstance(position);
        swipe_fragment.mNum = count;
        return swipe_fragment;
    }

    //empty constructor
    public Swipe_Fragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            mID = getArguments().getInt(PersonID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main,container,false);

        //get the person information from database
        final PersonInformation personInformation = DatabaseHandler.getHandler(this.getActivity()).getValue(mID);

        ShowName = (TextView)rootView.findViewById(R.id.ShowFileName_Text);
        ShowImage = (ImageView)rootView.findViewById(R.id.show_Image);
        ShowIntro = (TextView)rootView.findViewById(R.id.Content_textView);
        SearchButton = (Button)rootView.findViewById(R.id.Search_button);

        ShowName.setText(personInformation.getName());
        ShowIntro.setText(personInformation.getIntro());

        //get the Image from URL

        /*String imagURL = personInformation.getImageUrl();
        try {
            URL url = new URL(imagURL);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            ShowImage.setImageBitmap(myBitmap);
        }catch (IOException e){
            e.printStackTrace();
        }*/

        SearchButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse("http://www.google.com/#q="+personInformation.getName());
                        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                        startActivity(intent);
                        Toast.makeText(getActivity(),"Search the person",Toast.LENGTH_SHORT).show();
                    }
                }
        );

        new LoadImageTask(ShowImage).execute(personInformation.getImageUrl());

        return rootView;
    }

    private class LoadImageTask extends AsyncTask<String, Void, Bitmap>{
        ImageView imageView;



        public LoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls){
            String url = urls[0];
            Bitmap bitmap = null;
            try{
                InputStream inputStream = new URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            }catch (Exception e){
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ShowImage.setImageBitmap(bitmap);
        }
    }

    public void SearchPerson(View view){
        //open webview

        Intent intent = new Intent(getActivity(),Webview_Activity.class);
        startActivity(intent);
    }
}
