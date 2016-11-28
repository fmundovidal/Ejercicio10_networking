package com.example.a5alumno.ejercicio10_networking;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String URL_GUARDIAN = "https://theguardian.com/international/rss";
    private static final String TAG_MAIN_ACTIVITY = MainActivity.class.getSimpleName();

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button btnReadFeed = (Button)this.findViewById(R.id.btnReadFeed);
        btnReadFeed.setOnClickListener(this);
        this.mImageView = (ImageView)this.findViewById(R.id.imageViewMain);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.btnReadFeed){
            new MyAlternativeThread(this).execute(URL_GUARDIAN);
        }
        else if(view.getId()==R.id.imgBtnDownload){
            final String urlString = "http://www.tutorialspoint.com/green/images/logo.png";
            Picasso.with(this).load(urlString).into(this.mImageView);
        }
    }

    private class MyAlternativeThread extends AsyncTask<String,Void,String>{

        private Context mThreadContext;
        private final String TAG = MyAlternativeThread.class.getSimpleName();

        public MyAlternativeThread(Context anyContext){
            this.mThreadContext = anyContext;

        }
        //String fullTitleString="";

        @Nullable
        @Override
        protected String doInBackground(String[] params) {
            try {
                URL myUrl = new URL(params[0]);
                HttpURLConnection myConnection = (HttpURLConnection)myUrl.openConnection();
                    myConnection.setRequestMethod("GET");
                    myConnection.setDoInput(true);

                //Starting HHTP query
                    myConnection.connect();
                int respCode = myConnection.getResponseCode();
                Log.i(TAG, "Response code: "+respCode);

                if(respCode==HttpURLConnection.HTTP_OK){
                    InputStream myInStream = myConnection.getInputStream();
                    XmlPullParser myXmlParser = Xml.newPullParser();
                    myXmlParser.setInput(myInStream,null);

                    StringBuilder strBuilder = new StringBuilder("");

                    int event = myXmlParser.nextTag();

                    while(myXmlParser.getEventType() != XmlPullParser.END_DOCUMENT)
                    {
                        switch(event)
                        {
                            case XmlPullParser.START_TAG:
                                if(myXmlParser.getName().equals("item"))
                                {
                                    myXmlParser.nextTag();
                                    myXmlParser.next();//saltas hasta el contenido de title (por la patilla)
                                    //fullTitleString += "- "+ myXmlParser.getText() + "\n";
                                    strBuilder.append(myXmlParser.getText()).append("\n");
                                }
                                break;
                        }
                        event = myXmlParser.next();
                    }

                    myInStream.close();
                    return strBuilder.toString();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            if(string != null){
                Toast.makeText(this.mThreadContext,string,Toast.LENGTH_LONG).show();
            }
            else
                Log.i(MainActivity.TAG_MAIN_ACTIVITY,"No feed to be loaded");
        }
    }
}
