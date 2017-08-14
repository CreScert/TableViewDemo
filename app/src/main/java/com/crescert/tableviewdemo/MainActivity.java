package com.crescert.tableviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.crescert.tableview.TableView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TableView layout = (TableView) findViewById(R.id.gridLayout);
        layout.setWidthTableCount(5).setHeightTableCount(2048);

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.mipmap.ic_launcher);
        layout.setView(imageView,2,2);

        ImageView imageView1 = new ImageView(this);
        imageView1.setImageResource(R.mipmap.ic_launcher_round);
        layout.setView(imageView1,4,4);

        ImageView imageView2 = new ImageView(this);
        imageView2.setImageResource(R.mipmap.ic_launcher_round);
        layout.setView(imageView2,3,3);
        ImageView imageView3 = new ImageView(this);
        imageView3.setImageResource(R.mipmap.ic_launcher_round);
        layout.setView(imageView3,1,1);


    }
}
