package android.lifeistech.com.memo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;


import com.google.gson.Gson;

import java.util.ArrayList;

import java.util.Collections;


import io.realm.Realm;

public class TopActivity extends AppCompatActivity {

    public Realm realm;



    SharedPreferences pref;
    SharedPreferences.Editor editor;

    //idの最大値を記憶してくれる
    public int total;




    TextView titleText;
    TextView categoryText;
    TextView levelText;


    Spinner categorySpinner;
    Spinner levelSpinner;



    //level計算に使うint
    int number1;
    int number2;
    int number3;

    //ボタンの宣言→一回の思い出すにつき、一度しかボタンを押せなくする！

    Button plusButton;
    Button minusButton;
    Button resetButton;




    //ランダムを利用したい
    ArrayList<Integer> arrayList;


    //categoryを動的表示＋編集
    Gson gson;

    ArrayList<String> arrayList2;







    //とりあえずFloating Button で MainActivityに飛ぶ
    //余裕があればスワイプでの画面遷移を施す

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);

        //Realmインスタンス形成
        realm = Realm.getDefaultInstance();

        //SharedPreferences のインスタンス形成
        pref = getSharedPreferences("pref_mb", Context.MODE_PRIVATE);
        editor = pref.edit();




        titleText = (TextView) findViewById(R.id.titleText);
        categoryText = (TextView) findViewById(R.id.categoryText);
        levelText = (TextView) findViewById(R.id.levelText);

        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        levelSpinner = (Spinner) findViewById(R.id.levelSpinner);


        plusButton = (Button) findViewById(R.id.plusButton);
        minusButton = (Button) findViewById(R.id.minusButton);
        resetButton = (Button) findViewById(R.id.resetButton);

        plusButton.setEnabled(false);
        minusButton.setEnabled(false);
        resetButton.setEnabled(false);


        //Jsonを利用してcategoryをDBに記録＋編集可能に!!!!

        //Gsonインスタンス形成


        gson = new Gson();

        arrayList2 = new ArrayList<>();

        //とりあえず適当に入れる。

        arrayList2.add("");
        arrayList2.add("悲しい");
        arrayList2.add("怒った");
        arrayList2.add("アイデア");
        arrayList2.add("ハプニング");





        //SettingActivity上のListViewを表示させるためのArrayAdapter
        //ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item);


        //arrayListをGsonを通じてJsonに変換することでSharedPreferencesに保存可能に？？
        editor.putString("category",gson.toJson(arrayList2));
        editor.apply();





    }


    public void move(View v) {

        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);

        //finish()して大丈夫???
        finish();

    }


    public void select(View v) {



        total = pref.getInt("goukei", 0);

        //totalはidの最大値よりも1大きい数（記憶するときに+1されるから）。
        total = total--;




        if (total <= -1) {

            titleText.setText("記録がありません！");


        } else {
            Topic topic = null;

            int selectedCategoryPosition = (int) categorySpinner.getSelectedItemPosition();
            int selectedLevelPosition = (int) levelSpinner.getSelectedItemPosition();



            arrayList = new ArrayList<>();

            //+1はいらないのでは？？
            for(int i = 0; i < total+1; i ++){

                arrayList.add(i);

            }

            Collections.shuffle(arrayList);


            if(selectedLevelPosition == 0) {

                    for (int i = 0; i < total+1; i++ ){



                        topic = realm.where(Topic.class).equalTo("id", arrayList.get(i))
                                .equalTo("selectedCategoryPosition", selectedCategoryPosition)
                                .findFirst();


                        if(topic != null){
                            break;
                        }
                    }
                }else if(selectedLevelPosition == 1){

                    for(int i = 0; i < total+1 ;i++ ) {

                        topic = realm.where(Topic.class).equalTo("id", arrayList.get(i))
                                .equalTo("selectedCategoryPosition", selectedCategoryPosition)
                                .lessThan("level", 3)
                                .greaterThan("level", -3)
                                .findFirst();

                        if(topic != null){
                            break;
                        }

                    }
                }else if(selectedLevelPosition == 2){


                    for(int i = 0; i < total + 1; i++) {


                        topic = realm.where(Topic.class).equalTo("id", arrayList.get(i))
                                .equalTo("selectedCategoryPosition", selectedCategoryPosition)
                                .greaterThan("level", 2)
                                .findFirst();

                        if(topic != null){
                            break;
                        }
                    }
                }else if(selectedLevelPosition == 3){

                    for(int i = 0; i < total + 1; i++) {

                        topic = realm.where(Topic.class).equalTo("id", arrayList.get(i))
                                .equalTo("selectedCategoryPosition", selectedCategoryPosition)
                                .lessThan("level", -2)
                                .findFirst();

                        if(topic != null){
                            break;
                        }
                    }
                }

                //該当するtopicがあったか否かで場合分け
                //nullならボタンを押せなくする
                if(topic == null){

                    titleText.setText("該当する記録がありません！");
                    plusButton.setEnabled(false);
                    minusButton.setEnabled(false);
                    resetButton.setEnabled(false);


                }else {

                    //該当するtopicがあれば、各種表示+ボタンをアクティブに


                    titleText.setText(topic.title);

                    // res/array/list.xmlに定義したものから配列をつくる

                    String[] categoryArray = this.getResources().getStringArray(R.array.list);

                    // 保存してある番号から実際のCategoryを取得
                    categoryText.setText(categoryArray[topic.selectedCategoryPosition]);

                    //level表示
                    levelText.setText(String.valueOf(topic.level));

                    //ボタンを操作可能にする
                    plusButton.setEnabled(true);
                    minusButton.setEnabled(true);
                    resetButton.setEnabled(true);

                    //selectされたtopicのupdateDateを,SharedPreferencesに送る→button群操作の時に使用

                    editor.putString("time",topic.updateDate);
                    editor.apply();



                }
        }
    }



    //同じtitle,levelのtopicを区別する手段がない！
    //selectの時にrealmからどのtopicが参照されたのかを伝えられればいいのだが……。
    //MemoAdapterからtopic.updateDate情報をもらうことで一意に定まるようになった。


    public void plus(View v){

        //selectを押さない限りButtonは動かせないのでnullは考えなくてもよいかも？
        
        if(levelText == null) {

            titleText.setText("思い出してください！");

        } else{
        number1 = Integer.parseInt(levelText.getText().toString());

        number1 = number1 + 1;


        levelText.setText(String.valueOf(number1));

        //ここでselectで覚えさせた、updateDateをもとに変更を加えるtopicを検索。
        String updateDate = pref.getString("time","");


        final Topic topic = realm.where(Topic.class).equalTo("updateDate"
                    ,updateDate).findFirst();

        realm.executeTransaction(new Realm.Transaction(){
            @Override
            public void execute(Realm realm){
               topic.level = number1;
                }


            });

        //改めてselectを押さない限りボタンは押せない
            plusButton.setEnabled(false);
            minusButton.setEnabled(false);
            resetButton.setEnabled(false);
        }

    }

    public void minus(View v){

        if(levelText != null){

            number2 = Integer.parseInt(levelText.getText().toString());

            number2 = number2 - 1;


            levelText.setText(String.valueOf(number2));

            String updateDate = pref.getString("time","");


            final Topic topic = realm.where(Topic.class).equalTo("updateDate"
                    ,updateDate).findFirst();


            realm.executeTransaction(new Realm.Transaction(){
                @Override
                public void execute(Realm realm){
                    topic.level = number2;
                }
            });

            //
            plusButton.setEnabled(false);
            minusButton.setEnabled(false);
            resetButton.setEnabled(false);

        }

    }

    public void reset(View v){

        if(levelText == null){



        }else{
            number3 = 0;


            levelText.setText(String.valueOf(number3));

            String updateDate = pref.getString("time","");


            final Topic topic = realm.where(Topic.class).equalTo("updateDate"
                    ,updateDate).findFirst();


            realm.executeTransaction(new Realm.Transaction(){
                @Override
                public void execute(Realm realm){
                    topic.level = number3;
                }
            });

            plusButton.setEnabled(false);
            minusButton.setEnabled(false);
            resetButton.setEnabled(false);
        }

    }


    @Override
    protected  void onResume(){
        super.onResume();



    }




    @Override
    protected  void onDestroy(){
        super.onDestroy();

        realm.close();
    }

}
