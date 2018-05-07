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
import com.google.gson.reflect.TypeToken;
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
    ArrayList<Integer> arrayList2;


    //categoryを動的表示＋編集
    Gson gson;

    ArrayList<String> arrayList;

    ArrayAdapter<String> adapter;









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

        //


        gson = new Gson();

        arrayList = new ArrayList<>();

        adapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item);

        //今の所ここに書いてあるものがSharedPreferences上に記憶され、呼び出されている。
        //onCreateが呼ばれるたびにデフォの選択肢が追加され続けてしまうのでは？？　Jsonのおかげ？　重複不可？？


        //このままではまずい！！　戻ってくるたびにSharedPreferencesのcategoryが最初に戻ってしまう！！！
        //if と　boolean と　SharedPreferences を利用してアプリの初回起動時だけデフォのリストが呼び出されるようにする。


        if(pref.getBoolean("firstTimeOnly",true)) {
            arrayList.add("");
            arrayList.add("悲しい");
            arrayList.add("怒った");
            arrayList.add("アイデア");
            arrayList.add("ハプニング");


            editor.putString("category", gson.toJson(arrayList));
            editor.apply();


            //これ以降は常にfalseを取るのでTopActivityに戻って来ても、リストは初期化されない。
            editor.putBoolean("firstTimeOnly",false);
            editor.apply();

        }else{

            arrayList = gson.fromJson(pref.getString("category", "")
                    , new TypeToken<ArrayList<String>>() {}.getType());


        }

        //全ジャンルの追加（TopActivityのみ）

        arrayList.add(0,"全ジャンル");

        for (int i = 0; i < arrayList.size(); i++) {

            adapter.add(arrayList.get(i));

        }


        categorySpinner.setAdapter(adapter);





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



            arrayList2 = new ArrayList<>();

            //+1はいらないのでは？？
            for(int i = 0; i < total+1; i ++){

                arrayList2.add(i);

            }

            Collections.shuffle(arrayList2);


            //全ジャンルの時（0）はselectedCategoryPositionで縛らずにランダム表示。
            //全ジャンル以外の時は縛るが、selectedCategoryPositionを-1することに注意！！（(0)に全ジャンルを挿入したから）


            if(selectedCategoryPosition == 0){

                if (selectedLevelPosition == 0) {

                    for (int i = 0; i < total + 1; i++) {


                        topic = realm.where(Topic.class)
                                .equalTo("id", arrayList2.get(i))
                                .findFirst();


                        if (topic != null) {
                            break;
                        }
                    }
                } else if (selectedLevelPosition == 1) {

                    for (int i = 0; i < total + 1; i++) {

                        topic = realm.where(Topic.class).equalTo("id", arrayList2.get(i))
                                .lessThan("level", 3)
                                .greaterThan("level", -3)
                                .findFirst();

                        if (topic != null) {
                            break;
                        }

                    }
                } else if (selectedLevelPosition == 2) {


                    for (int i = 0; i < total + 1; i++) {


                        topic = realm.where(Topic.class).equalTo("id", arrayList2.get(i))
                                .greaterThan("level", 2)
                                .findFirst();

                        if (topic != null) {
                            break;
                        }
                    }
                } else if (selectedLevelPosition == 3) {

                    for (int i = 0; i < total + 1; i++) {

                        topic = realm.where(Topic.class).equalTo("id", arrayList2.get(i))
                                .lessThan("level", -2)
                                .findFirst();

                        if (topic != null) {
                            break;
                        }
                    }


                }


            }else {

                //全ジャンル以外が選ばれた時の処理


                if (selectedLevelPosition == 0) {

                    for (int i = 0; i < total + 1; i++) {


                        topic = realm.where(Topic.class)
                                .equalTo("id", arrayList2.get(i))
                                .equalTo("selectedCategoryPosition", selectedCategoryPosition - 1)
                                .findFirst();


                        if (topic != null) {
                            break;
                        }
                    }
                } else if (selectedLevelPosition == 1) {

                    for (int i = 0; i < total + 1; i++) {

                        topic = realm.where(Topic.class).equalTo("id", arrayList2.get(i))
                                .equalTo("selectedCategoryPosition", selectedCategoryPosition - 1)
                                .lessThan("level", 3)
                                .greaterThan("level", -3)
                                .findFirst();

                        if (topic != null) {
                            break;
                        }

                    }
                } else if (selectedLevelPosition == 2) {


                    for (int i = 0; i < total + 1; i++) {


                        topic = realm.where(Topic.class).equalTo("id", arrayList2.get(i))
                                .equalTo("selectedCategoryPosition", selectedCategoryPosition - 1)
                                .greaterThan("level", 2)
                                .findFirst();

                        if (topic != null) {
                            break;
                        }
                    }
                } else if (selectedLevelPosition == 3) {

                    for (int i = 0; i < total + 1; i++) {

                        topic = realm.where(Topic.class).equalTo("id", arrayList2.get(i))
                                .equalTo("selectedCategoryPosition", selectedCategoryPosition - 1)
                                .lessThan("level", -2)
                                .findFirst();

                        if (topic != null) {
                            break;
                        }
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

  //ArrayListをSharedPreferencesから呼び出してselecedCategoryPositionに該当するものをViewに表示


                    arrayList = gson.fromJson(pref.getString("category", "")
                            , new TypeToken<ArrayList<String>>() {}.getType());

                    categoryText.setText(arrayList.get(topic.selectedCategoryPosition));


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


    public void reshow(){

        //ジャンルの追加を更新。onRestartとかに入れればいいはず。。。


        adapter.clear();

        arrayList = gson.fromJson(pref.getString("category","")
                ,new TypeToken<ArrayList<String>>(){}.getType());

        //全ジャンルの追加

        arrayList.add(0,"全ジャンル");


        for(int i = 0; i < arrayList.size(); i++) {

            adapter.add(arrayList.get(i));


        }

        categorySpinner.setAdapter(adapter);

    }

    @Override
    protected  void onRestart(){
        super.onRestart();

        reshow();
    }




    @Override
    protected  void onDestroy(){
        super.onDestroy();

        realm.close();
    }

}
