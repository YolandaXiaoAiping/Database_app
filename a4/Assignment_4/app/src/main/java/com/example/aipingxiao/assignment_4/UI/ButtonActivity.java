package com.example.aipingxiao.assignment_4.UI;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.aipingxiao.assignment_4.BuildConfig;
import com.example.aipingxiao.assignment_4.Database.DatabaseHandler;
import com.example.aipingxiao.assignment_4.PersonInformation;
import com.example.aipingxiao.assignment_4.R;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Aiping Xiao on 2016-02-09.
 */
public class ButtonActivity extends AppCompatActivity{

    private static final String Tag = "ButtonActivity";
    private static Context mCotext = null;
     //Button loadDatabase;
     //Button viewDatabase;
     //Button clearDatabase;
    private static boolean mLoad = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button);
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        mCotext = this;

        checkButtonVisible();

    }

    //go to another activity
    public void onViewClicked(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        //Toast.makeText(this,"not implemented yet",Toast.LENGTH_SHORT).show();
    }

    public void onClearClicked(View view){
        DatabaseHandler.getHandler(this).deleteDatabase();
        checkButtonVisible();
        Toast.makeText(this,"databasse has been deleted successfully!",Toast.LENGTH_SHORT).show();

    }

    public void onLoadClicked(View view){
        //Enter URL
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Please Enter a URL");
        alert.setMessage("Click Enter to use the default URL");

        //allow the user to input a URL

        final EditText URLTText = new EditText(this);
        URLTText.setHint("http://www.eecg.utoronto.ca/~jayar/PeopleList");
        alert.setView(URLTText);

        alert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String URL = URLTText.getText().toString();
                if (URL.equals("")) {
                    loadURL("http://www.eecg.utoronto.ca/~jayar/PeopleList");
                } else {
                    loadURL(URL);
                }
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    //load the database from the given URL
    private void loadURL(String url){
        if (!mLoad){
            new BackgroundHTTP().execute(url);
        }
    }

    //get the image from URL in background
    private class BackgroundHTTP extends AsyncTask<String,Void,Void>{
        ProgressDialog progressDialog;

        @Override
        protected Void doInBackground(String... params) {
            String url = params[0];
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response ;
            String responseString = null;
            try {
                response = httpClient.execute(new HttpGet(url));
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode()==HttpStatus.SC_OK){
                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    response.getEntity().writeTo(bout);
                    responseString = bout.toString();
                    bout.close();
                }else {
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            }catch(ClientProtocolException e){

            }catch (IOException e){

            }

            addValue(url,responseString);
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            checkButtonVisible();
            mLoad = false;
            progressDialog.dismiss();
            Toast.makeText(mCotext,"Load succeed!",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPreExecute() {
            mLoad = true;
            progressDialog = new ProgressDialog(ButtonActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Loading the Database");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    private void addValue(String url,String responseString){
        if (!responseString.equals("")){
            //get the image url
            DatabaseHandler databaseHandler = DatabaseHandler.getHandler(mCotext);
            URI ImageUri = null;
            try {
                URI uri = new URI(url);
                ImageUri = uri.getPath().endsWith("/")?uri.resolve(".."):uri.resolve(".");
            }catch (URISyntaxException e){

            }

            String[] data = responseString.split("\n");
            for (int i = 0;i<data.length;i+=3){
                if (!databaseHandler.isInDatabase(data[i])){
                    databaseHandler.addValue(
                            new PersonInformation(data[i],data[i+1],ImageUri.resolve(data[i+2]).toString())
                    );
                }
            }
        }
    }

    //set if the button is enables
    public void checkButtonVisible(){
        int DatabaseExist = DatabaseHandler.getHandler(mCotext).getValuesCount();
        //Button loadDatabase = (Button)findViewById(R.id.LoadButton);
        Button viewDatabase = (Button)findViewById(R.id.ViewButton);
        Button clearDatabase = (Button)findViewById(R.id.ClearButton);
        if (BuildConfig.DEBUG){
            Log.d(Tag,Integer.toString(DatabaseExist));
        }
        if (DatabaseExist == 0){
            viewDatabase.setEnabled(false);
            viewDatabase.setClickable(false);
            viewDatabase.setAnimation(new AlphaAnimation(1.0f, 0.45f));
            clearDatabase.setEnabled(false);
            clearDatabase.setClickable(false);
            clearDatabase.setAnimation(new AlphaAnimation(1.0f,0.45f));
        }else{
            viewDatabase.setEnabled(true);
            viewDatabase.setClickable(true);
            viewDatabase.setAnimation(new AlphaAnimation(0.45f, 1.0f));
            clearDatabase.setEnabled(true);
            clearDatabase.setClickable(true);
            clearDatabase.setAnimation(new AlphaAnimation(0.45f,1.0f));
        }
    }


}
