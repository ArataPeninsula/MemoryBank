package android.lifeistech.com.memo;

import io.realm.RealmObject;

/**
 * Created by 0wh34 on 2018/05/01.
 */

public class Topic extends  RealmObject{
    //Memo を　Topic（話題）に　変える！


        public String title;

        public String updateDate;



        //category は「ジャンル」に相当するパーツ(contentをcategoryに書き換えればよい）
        //public String category;


        //categoryの配列（list）の番号
        public int selectedCategoryPosition;

        //topicのidを宣言
        public int id;

        //鉄板どの宣言
        public int level;








}
