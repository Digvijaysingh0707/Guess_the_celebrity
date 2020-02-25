package com.example.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.service.autofill.FieldClassification;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> celebsUrls = new ArrayList<String>();
    ArrayList<String> celebsName = new ArrayList<String>();
    int chosenCelebs =0;
    int locationOfCorectAnswer=0;
    String[] answer = new String[4];
    ImageView imageView;
    Button button0;
    Button button1;
    Button button2;
    Button button3;
    public void celebChosen(View view){
        if (view.getTag().toString().equals(Integer.toString(locationOfCorectAnswer))) {
            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "Wrong!"+celebsName.get(chosenCelebs), Toast.LENGTH_LONG).show();
        }

    }







    public class ImageDownloader extends AsyncTask<String,Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);

                    HttpURLConnection connection =(HttpURLConnection) url.openConnection();
                    connection.connect();
                    InputStream inputStream =connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                    return myBitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            String result ="";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url=new URL(urls[0]);
                urlConnection=(HttpURLConnection)url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reder = new InputStreamReader(in);
                int data =reder.read();
                while (data!=-1){
                    char current =(char) data;
                    result+=current;
                    data = reder.read();
                }
                return result;
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;

        }

    }
    public void createNewQuestion(){
        Random random = new Random();
        chosenCelebs = random.nextInt(celebsUrls.size());

        ImageDownloader imageTask = new ImageDownloader();
        Bitmap celebsImage;
        try {
            celebsImage = imageTask.execute(celebsUrls.get(chosenCelebs)).get();
            imageView.setImageBitmap(celebsImage);
            locationOfCorectAnswer = random.nextInt(4);
            int incorrectAnswerLocation;
            for (int i = 0; i < 4; i++) {
                if (i == locationOfCorectAnswer) {
                    answer[i] = celebsName.get(chosenCelebs);
                } else {
                    incorrectAnswerLocation = random.nextInt(celebsUrls.size());
                    while (incorrectAnswerLocation == chosenCelebs) {
                        incorrectAnswerLocation = random.nextInt(celebsUrls.size());

                    }
                    answer[i] = celebsName.get(incorrectAnswerLocation);
                }
            }
            button0.setText(answer[0]);
            button1.setText(answer[0]);
            button2.setText(answer[0]);
            button3.setText(answer[0]);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button0 =(Button)findViewById(R.id.button);
        button1 =(Button)findViewById(R.id.button1);
        button2 =(Button)findViewById(R.id.button2);
        button3 =(Button)findViewById(R.id.button3);
        imageView =(ImageView)findViewById(R.id.imageView);
        DownloadTask task = new DownloadTask();
        String result = null;
        try {
            result = task.execute("http://www.posh24.se/kandisar").get();
            String[] splitResult = result.split("<div class=\"sidebarContainer\">");
            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);
            while (m.find()) {
                celebsUrls.add(m.group(1));
            }
            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);
            while (m.find()) {
                celebsName.add(m.group(1));
            }
        }

        catch (InterruptedException e) {
            e.printStackTrace();
        }catch (ExecutionException e) {
            e.printStackTrace();
        }
    }{
        createNewQuestion();

    }

}
