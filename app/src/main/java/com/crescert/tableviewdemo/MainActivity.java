package com.crescert.tableviewdemo;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.crescert.tableview.TableView;
import com.crescert.tableview.TableView2;
import com.crescert.tableview.TableViewList;
import com.crescert.tableview.TableViewModel;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        TableView2 layout = (TableView2) findViewById(R.id.gridLayout);
//        layout.setWidthTableCount(5).setHeightTableCount(2048);
//
//        ImageView imageView = new ImageView(this);
//        imageView.setImageResource(R.mipmap.ic_launcher);
//        layout.setView(imageView,2,2);
//
//        ImageView imageView1 = new ImageView(this);
//        imageView1.setImageResource(R.mipmap.ic_launcher_round);
//        layout.setView(imageView1,4,4);
//
//        ImageView imageView2 = new ImageView(this);
//        imageView2.setImageResource(R.mipmap.ic_launcher_round);
//        layout.setView(imageView2,3,3);
//        ImageView imageView3 = new ImageView(this);
//        imageView3.setImageResource(R.mipmap.ic_launcher_round);
//        layout.setView(imageView3,1,1);

        try {
            //读取资产文件
            String s = parseJson("123.txt");
            //解析JSON
            Gson gson = new Gson();
            TableViewList tableViewModel = gson.fromJson(s, TableViewList.class);
            //为放入表格做最后一步处理，第二个参数是左右边界
            parseTable(tableViewModel.gridlist,tableViewModel.padding);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析
     * @param gridlist
     * @param padding
     */
    private void parseTable( List<TableViewModel> gridlist, int padding) {
        TableView table = (TableView) findViewById(R.id.gridList);

        //新建一个临时集合，因为要修改源集合的值，
        List<TableViewModel> CopyJson = new ArrayList<>(gridlist.size());

        //拷贝源集合到新集合中
        for (int i  = 0;  i < gridlist.size(); i++) {
            CopyJson.add(gridlist.get(i));
        }

        //适配
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;


        //适配
        double ratio = (width * 1.0f) / 750;

        //总高度
        int totalHeight = 0;

        //将集合中所有的高度修改为适配高度
        for (int i = 0; i < CopyJson.size(); i++) {
            //每修改一处，设置标记，否则会一直计算下去
            if(!CopyJson.get(i).isCalc) {
                CopyJson.get(i).height *= ratio;
                //修改标记
                CopyJson.get(i).isCalc = true;
            }
        }

        //将所有的高度加起来，其实可以和上面的循环连起来
        for (int i = 0; i < CopyJson.size(); i++) {
            totalHeight += CopyJson.get(i).height;
        }

        //设置组件的高度
        ViewGroup.LayoutParams params2 = table.getLayoutParams();
        params2.height = totalHeight;
        table.setLayoutParams(params2);
        //设置组件的左右边距
        table.setPadding((int) (padding*ratio),0, (int) (padding*ratio),0);

        //开始绘制组件
        table.initJson(CopyJson, ImageView.ScaleType.FIT_XY);
    }

    /**
     * 读取文件
     * @param filePath
     * @return
     * @throws IOException
     */
    public String parseJson(String filePath) throws IOException {
        InputStream open = getResources().getAssets().open(filePath);
        byte[] bytes = new byte[100];
        int len = 0;
        StringBuilder stringBuilder = new StringBuilder();

        while ((len = open.read(bytes)) != -1) {
            stringBuilder.append(new String(bytes, 0, len));
        }
        Log.e("sdf", stringBuilder.toString() + "");
        return stringBuilder.toString();
    }
}
