
package com.crescert.tableview;

import java.util.List;

/**
 * Created by CreScert on 2017/8/14.
 */

public class TableViewModel {


    /**
     * subitems : [{"img":"https://i.huim.com/miaoquan/14966511524892.SS2!/both/300x300/unsharp/true","weight":2,"url":"emeker://productdetail?pdid=240","height":212,"orientation":"h"}]
     * img : https://i.huim.com/miaoquan/14966511524892.SS2!/both/300x300/unsharp/true
     * weight : 2
     * url : emeker://productdetail?pdid=240
     * height : 212
     * orientation : h
     */

    public String img;
    public float weight;
    public String url;
    public float height;
    public String orientation;
    public List<SubitemsInfo> subitems;
    public boolean isCalc;
    @Override
    public String toString() {
        return "TableViewModel{" +
                "img='" + img + '\'' +
                ", weight=" + weight +
                ", url='" + url + '\'' +
                ", height=" + height +
                ", orientation='" + orientation + '\'' +
                ", subitems=" + subitems +
                '}';
    }

    public static class SubitemsInfo {
        /**
         * img : https://i.huim.com/miaoquan/14966511524892.SS2!/both/300x300/unsharp/true
         * weight : 2
         * url : emeker://productdetail?pdid=240
         * height : 212
         * orientation : h
         */

        public String img;
        public float weight;
        public String url;
        public float height;
        public String orientation;
        public List<SubitemsInfo> subitems;

        @Override
        public String toString() {
            return "SubitemsInfo{" +
                    "img='" + img + '\'' +
                    ", weight=" + weight +
                    ", url='" + url + '\'' +
                    ", height=" + height +
                    ", orientation='" + orientation + '\'' +
                    ", subitems=" + subitems +
                    '}';
        }
    }


}


