package com.crescert.tableview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by CreScert on 2017/8/11.
 */

public class TableView extends ViewGroup {

    private List<TableViewModel> json;      //原始数据
    private List<TableViewModel> CopyJson;  //拷贝数据
    private float mScreenWidth;               //总宽度
    private float mScreenHeight;              //总高度

    //图片缩放方式int
    ImageView.ScaleType mScaleType = ImageView.ScaleType.FIT_XY;

    public TableView(Context context) {
        this(context, null);
    }

    public TableView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public TableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPixel();
    }


    /**
     * 解析bean类并绘制
     *
     * @param json
     */
    public void initJson(List<TableViewModel> json) {
        initJson(json, mScaleType);
    }

    /**
     * 解析bean类绘制组件但是不刷新
     *
     * @param json
     * @param scaleType 图片的缩放方式
     */
    public void initJsonNoFrush(List<TableViewModel> json, ImageView.ScaleType scaleType) {
        //保存值
        this.json = json;
        mScaleType = scaleType;

        //临时变量
        CopyJson = new ArrayList<>(json.size());
        //填充数据
        for (int i = 0; i < json.size(); i++) {
            CopyJson.add(null);
        }
        //拷贝原数据
        Collections.copy(CopyJson, json);

        //总高度
        float mHeight = 0;
        //高度累积
        for (int i = 0; i < json.size(); i++) {
            mHeight += json.get(i).height;
        }
//        mHeight = json.height;
        //测量控件
        measure((int) mScreenWidth, (int) mHeight);

    }

    /**
     * 解析bean类并绘制，设置图片的缩放方式
     *
     * @param json
     * @param scaleType 图片的缩放方式
     */
    public void initJson(List<TableViewModel> json, ImageView.ScaleType scaleType) {
        //解析bean类
        initJsonNoFrush(json, scaleType);
        //绘制
        requestLayout();
    }

    /**
     * 初始化像素值
     */
    private void initPixel() {
        //获取屏幕的宽和高
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        mScreenWidth = displayMetrics.widthPixels;
        mScreenHeight = displayMetrics.heightPixels;
    }


    /**
     * 设置图片的显示方式
     *
     * @param scaleType
     */
    public void setScaleType(ImageView.ScaleType scaleType) {
        mScaleType = scaleType;
        //请求父控件刷新
        requestLayout();
    }

    /**
     * 设置宽度
     *
     * @param width
     */
    public void setWidth(float width) {
        //设置宽度
        LayoutParams layoutParams = getLayoutParams();
        if (null != layoutParams) {
            getLayoutParams().width = (int) width;
        }
        //保存宽度
        mScreenWidth = width;
        requestLayout();
    }

    /**
     * 转换权值，将高度
     *
     * @param imagesInfo
     * @param width
     * @param height
     * @param orientation
     */
    public void converWeight(List<TableViewModel.SubitemsInfo> imagesInfo, float width, float height, String orientation) {
        ArrayList<Float> integers = new ArrayList<>();
        //总权值
        float totalWeight = 0;

        //相加所有的权重
        for (int i = 0; i < imagesInfo.size(); i++) {
            //获取到各个权重
            integers.add( imagesInfo.get(i).weight);
            //累积权重
            totalWeight += imagesInfo.get(i).weight;
        }

        float calc = 0;
        //如果是水平的
        if (orientation.equals("h")) {
            calc = width;
        } else {
            calc = height;
        }
        //以屏幕大小转换权重
        for (int i = 0; i < integers.size(); i++) {
            imagesInfo.get(i).weight = calc * integers.get(i) / totalWeight;
        }
    }

    /**
     * 对控件进行布局位置设置
     *
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        if (null != json) {
            //移除所有的控件
            removeAllViews();
            //初始化坐标
            initPosition();

            //最外层遍历
            for (int i = 0; i < json.size(); i++) {
                //每次进来都从屏幕左边开始
                startX = 0;
                //第一次进来，从左上角开始
                if (i == 0) {
                    startY = 0;
                }

                //中间这层
                TableViewModel imagesInfo = json.get(i);
                if (null != imagesInfo) {
                    //最里层
                    layoutView(imagesInfo.subitems, mScreenWidth,  imagesInfo.height, imagesInfo.orientation, imagesInfo);
                }
                //高度加保存的Y值，作为新的Y值
                startY = (imagesInfo.height + TotalStartY);
                //并保存
                TotalStartY = startY;
            }

        }
    }

    /**
     * 初始化坐标以及数据
     */
    private void initPosition() {
        //初始化坐标
        startX = 0;
        startY = 0;

        TotalStartY = 0;
        //我为啥要拷贝？
        Collections.copy(json, CopyJson);
        initJsonNoFrush(json, mScaleType);
    }


    public float startX = 0;
    public float startY = 0;
    //随时保存Y值
    public float TotalStartY = 0;

    /**
     * 循环去查找，如果没有孩子，就显示当前的布局
     * @param imagesInfo    子控件的数据
     * @param width         子控件所拥有的宽度
     * @param height        子控件所拥有的高度
     * @param orientation   排序方式
     * @param fatherInfo    最上层的数据，父类，保持不变的往下传
     */
    private void layoutView(final List<TableViewModel.SubitemsInfo> imagesInfo, float width, float height, String orientation, TableViewModel fatherInfo) {
        //转换下权重
        converWeight(imagesInfo, width, height, orientation);

        //每个子类拥有的总宽和总长
        float childHeight = height;
        float childWidth = width;

        //子类所减小剩余的宽度和高度
        float haveHeight = childHeight;
        float haveWidth = childWidth;

        //之前的X和Y
        float startX_Old = startX;
        float startY_Old = startY;

        if (null != imagesInfo) {
            //总权重值
            int weight = 0;
            //获取总权重值
            for (int i = 0; i < imagesInfo.size(); i++) {
                weight += imagesInfo.get(i).weight;
            }
            int haveWeight = weight;

            //遍历查找孩子
            for (int i = 0; i < imagesInfo.size(); i++) {
                //如果有孩子，继续向下寻找
                if (null != imagesInfo.get(i).subitems) {
                    //垂直
                    if (imagesInfo.get(i).orientation.equals("v"))
                        layoutView(imagesInfo.get(i).subitems,/* haveWidth / haveWeight **/  imagesInfo.get(i).weight, haveHeight, imagesInfo.get(i).orientation, fatherInfo);
                    else//水平
                        layoutView(imagesInfo.get(i).subitems, haveWidth, /*haveHeight **/  imagesInfo.get(i).weight/*/ haveWeight*/, imagesInfo.get(i).orientation, fatherInfo);
                } else {  //没有孩子，就显示控件
                    //创建图片
                    ImageView imageView = new ImageView(getContext());

                    //测量下
                    imageView.measure(0, 0);
                    //设置图片缩放方式
                    imageView.setScaleType(mScaleType);

                    //点击事件
                    final int finalI = i;
                    imageView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), "点击了图片", Toast.LENGTH_SHORT).show();
                        }
                    });
                    //图片展示
//                    Glide.loadHomePage(getContext(), AccountClient.QINIUPIC + imagesInfo.get(i).img, imageView);
                    Log.e("aaaa",imagesInfo.get(i).img);
                    Picasso.with(getContext()).load(imagesInfo.get(i).img)
                            .error(R.mipmap.ic_launcher_round)
                            .placeholder(R.mipmap.ic_launcher).into(imageView);
                    //水平方向，从左往右
                    if (orientation.equals("h")) {

                        //右下角的x轴，也是这个控件所占用的宽度
                        //现在所在的X轴点坐标+ 占用的大小（当前控件所拥有的宽度/总权重*当前对象控件的权重（每一份））
                        float fitWidth = startX + childWidth / weight * imagesInfo.get(i).weight;

                        //布局当前控件
                        imageView.layout((int)startX, (int)startY, (int)fitWidth, (int)(startY + childHeight));

                        //当前控件的右下角作为现在的X轴
                        startX = fitWidth;

                        //计算剩余的宽度（当前控件所拥有的宽度-占用宽度后的X轴）
                        haveWidth = Math.abs(childWidth - startX);

                        //如果是当前行最后一个
                        if (imagesInfo.size() - 1 == i) {
                            //如果当前父类拥有高度+之前保存的高度值-现在的高度-所占有的高度小于5，
                            // 说明要换到下一行，只是绘制一小部分，要换行了
                            //x轴还原
                            if (!(Math.abs(fatherInfo.height + TotalStartY - startY - childHeight) < 5)) {
                                //高度累积
                                startY += childHeight;
                                //X轴还原
                                startX = startX_Old;
                            } else {
                                //换到右边
                                //高度还原
                                startY = TotalStartY;
                                //X轴到控件的右下角
                                startX = fitWidth;
                            }
                        }

                    } else {
                        //从上到下

                        //占用的高度，同上，右下角Y轴
                        float fitHeight = startY + childHeight / weight * imagesInfo.get(i).weight;

                        //绘制控件，X轴要用之前的X轴累积
                        imageView.layout((int)startX,(int) startY, (int)(startX + childWidth), (int)fitHeight);

                        //占用的高度，代替现在的Y轴
                        startY = fitHeight;
                        //当前控件剩余的高度（当前控件拥有的高度-占有的高度+之前的Y轴）
                        haveHeight = Math.abs(childHeight - startY + TotalStartY);

                        //最后一列
                        if (imagesInfo.size() - 1 == i) {
                            //X轴累加
                            startX += childWidth;
                            //Y轴还原
                            startY = startY_Old;
                        }
                    }
                    //剩余的高度，减去现在的权重（当前json是权重为高度），没用到
                    haveWeight -= imagesInfo.get(i).weight;

                    //给父控件添加子控件
                    addView(imageView);
                }
            }
        }
    }


    private float getWeight(List<TableViewModel.SubitemsInfo> imagesInfo) {
        float weight = 0;
        if (null != imagesInfo && 0 < imagesInfo.size()) {
            for (int i = 0; i < imagesInfo.size(); i++) {
                weight += imagesInfo.get(i).weight;
                weight += getWeight(imagesInfo.get(i).subitems);
            }
        }
        return weight;
    }
}

