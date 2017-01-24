package com.example.andttapos.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {


    private ArrayList<Celebratry>  celebratyList;
    private ImageView imageView;
    private Button button0;
    private Button button1;
    private Button button2;
    private Button button3;
    private int correctAnser;
    private SecureRandom random = new SecureRandom();
    private int[] answers = new int[4];

    public void changeButton(View view) {

        generateImageAndQuestion();

    }

    public class DownloadTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.connect();
                InputStream inputStream = connection.getInputStream();
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


    public void generateImageAndQuestion() {
        int selectedImage = random.nextInt(celebratyList.size());
        int correctAnswer = random.nextInt(answers.length);
        int wrongAnswer;
        for (int i = 0; i < answers.length; i++) {
            if (i == correctAnswer) {
                answers[i] = selectedImage;
            } else {
                wrongAnswer = random.nextInt(celebratyList.size());
                while (wrongAnswer == selectedImage) {
                    wrongAnswer = random.nextInt(celebratyList.size());
                }
                answers[i] = wrongAnswer;
            }
        }
        this.correctAnser = correctAnswer;

        button0.setText(celebratyList.get(answers[0]).getName());
        button1.setText(celebratyList.get(answers[1]).getName());
        button2.setText(celebratyList.get(answers[2]).getName());
        button3.setText(celebratyList.get(answers[3]).getName());

        Bitmap img = null;

        DownloadTask task = new DownloadTask();
        try {
            img = task.execute(celebratyList.get(selectedImage).getUrl()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(img);
    }

    public class DownloadCelebratiesList extends AsyncTask<String, Void, ArrayList<Celebratry>> {


        @Override
        protected ArrayList<Celebratry> doInBackground(String... voids) {

            Document doc;
            ArrayList<Celebratry> list = new ArrayList<>();
            try {

                doc = Jsoup.connect(voids[0]).get();
                Elements newsHeadlines = doc.select(".channelListEntry");
                for (Element element :
                        newsHeadlines) {
                    String img = element.select(".image img").attr("src");
                    String name = element.select(".name").text();
                    list.add(new Celebratry(img, name));

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return list;
        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        button0 = (Button) findViewById(R.id.button0);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        DownloadCelebratiesList list = new DownloadCelebratiesList();
        try {
            this.celebratyList = list.execute("http://www.posh24.com/celebrities").get();
          //  this.celebratyList = list.execute("http://www.imdb.com/list/ls051594496/").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        generateImageAndQuestion();



    }

}
