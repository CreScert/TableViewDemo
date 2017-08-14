package com.crescert.tableview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

/**
 * Created by 续杰 on 2017/8/11.
 */

public class TableView extends ViewGroup {

    //屏幕的宽和高
    private int mScreenWidth = 0;
    private int mScreenHeight = 0;

    //总共的格子数
    private int mWidthTable = 8;//横向
    private int mHeightTable = 80;//纵向

    //每一个格子占的像素
    private int mPerPiexl = 0;
    //是否 通过View大小判断所需要的格子内部有没有被占用
    private boolean mIsFilterTable = true;

    /**
     * 格子占用情况
     */
    private int position[][] = null;

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
     * 初始化每个格子的像素大小，以及格子的分配
     */
    public void initPixel() {
        //保存屏幕的宽和高
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        mScreenWidth = displayMetrics.widthPixels;
        mScreenHeight = displayMetrics.heightPixels;
        mPerPiexl = mScreenWidth / mWidthTable;

        position = new int[mHeightTable][mWidthTable];
    }

    /**
     * 设置横向X轴的格子数量
     * @param widthTable
     * @return
     */
    public TableView setWidthTableCount(int widthTable) {
        mWidthTable = widthTable;
        initPixel();
        return this;
    }

    /**
     * 设置纵向Y轴的格子数量
     * @param heightTable
     * @return
     */
    public TableView setHeightTableCount(int heightTable) {

        if(heightTable <= 2048) {
            mHeightTable = heightTable;
            initPixel();
        }
        return this;
    }


    /**
     * 判断View大小区域内各自有没有被占用
     *
     * @param isFilterTable
     * @return
     */
    public TableView setFilterTable(boolean isFilterTable) {
        mIsFilterTable = isFilterTable;
        return this;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
        //获取有多少个子控件
        int childCount = getChildCount();
        //根据子控件数量设置
        for (int i = 0; i < childCount; i++) {
            //如果说第一次进来，或者再次调用这个方法时重置所有坐标
            if (i == 0) {
                for (int j = 0; j < position.length; j++) {
                    for (int k = 0; k < position[j].length; k++) {
                        position[j][k] = 0;
                    }
                }
            }
            //获取每一个子控件
            View view = getChildAt(i);
            //获取子控件的坐标
            HashMap<Integer, Integer> posValue = hash.get(view);

            //为Null判断
            if (posValue != null) {
                //获取子控件将要设置的大小
                for (Integer needWidthCount : posValue.keySet()) {
                    //获取需要的y轴方格子的数量
                    Integer needHeightCount = posValue.get(needWidthCount);

                    //寻找有空的方格子
                    Position empty = findEmpty(needWidthCount, needHeightCount);
                    //找到了
                    if (empty != null) {
                        //获取x,y轴的方格子坐标
                        int x = empty.x;
                        int y = empty.y;
                        //绘制布局
                        view.layout(x * mPerPiexl, y * mPerPiexl, needWidthCount * mPerPiexl + x * mPerPiexl, y * mPerPiexl + needHeightCount * mPerPiexl);

                        //填充方格子
                        fillTable(x, y, needWidthCount, needHeightCount);

                    } else {
                        Log.e("没有位置了", needWidthCount + "..." + needHeightCount);
                    }
                }
            }
        }

    }

    /**
     * 填充方格子，从之前找到的有位置的坐标开始，填充需要的方格子数量（图片大小）
     *
     * @param x
     * @param y
     * @param needWidthCount
     * @param needHeightCount
     */
    private void fillTable(int x, int y, Integer needWidthCount, Integer needHeightCount) {
        //填充方格子
        for (int j = y; j < needWidthCount + y; j++) {
            for (int k = x; k < needHeightCount + x; k++) {
                //防止坐标越界
                if (k == mWidthTable) break;
                position[j][k] = 1;
            }
        }
    }

    /**
     * 保存所占有的方格子的位置
     */
    class Position {
        int x;
        int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * 寻找新的方格子，
     *
     * @param integer
     * @param integer1
     * @return
     */
    private Position findEmpty(Integer integer, Integer integer1) {

        for (int i = 0; i < position.length; i++) {
            for (int j = 0; j < position[i].length; j++) {
                if (position[i][j] != 1) {
                    //判断在图片大小的位置上是否有占用的方格子，
                    if (mIsFilterTable) {
                        //如果有，继续寻找
                        if (isFill(i, j, integer, integer1)) {
                            continue;
                        }
                    }
                    //有足够的位置返回坐标
                    if (mWidthTable - integer > 0) {
                        return new Position(j, i);
                    }
                }
            }
        }
        return null;
    }

    /**
     * 判断在图片的位置是否有填充过
     *
     * @param i
     * @param j
     * @param integer
     * @param integer1
     * @return
     */
    private boolean isFill(int i, int j, Integer integer, Integer integer1) {
        for (int k = i; k < integer + i; k++) {
            for (int l = j; l < integer1 + j; l++) {
                //如果图片的大小超过了边界就继续寻找
                if (l == mWidthTable)
                    return true;
                //代表已经被占用
                if (position[k][l] == 1) {
                    return true;
                }

            }
        }
        return false;
    }


    /**
     * 保存View以及所需空间
     */
    private HashMap<View, HashMap<Integer, Integer>> hash = new HashMap<>();

    /**
     * 设置View
     *
     * @param view            指定的View
     * @param needWidthTable  需要的x轴方格子的数量
     * @param needHeightTable 需要的y轴方格子的数量
     */
    public void setView(View view, int needWidthTable, int needHeightTable) {
        if (needWidthTable > mWidthTable) {
            throw new ArrayIndexOutOfBoundsException("格子的大小超过原有设定");
        }
        if (null == view) {
            return;
        }
        HashMap<Integer, Integer> integerIntegerHashMap = new HashMap<>();
        integerIntegerHashMap.put(needWidthTable, needHeightTable);

        hash.put(view, integerIntegerHashMap);
        addView(view);
    }
}
