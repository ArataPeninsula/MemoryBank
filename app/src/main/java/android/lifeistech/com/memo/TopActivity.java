package android.lifeistech.com.memo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmResults;

public class TopActivity extends AppCompatActivity {

    public Realm realm;
    public int selectedNumber;

    SharedPreferences maxNumber;
    SharedPreferences.Editor editor;
    public int total;

    TextView titleText;
    TextView categoryText;
    Spinner categorySpinner;




    //とりあえずFloating Button で MainActivityに飛ぶ
    //余裕があればスワイプでの画面遷移を施す

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);

        //Realmインスタンス形成
        realm = Realm.getDefaultInstance();

        //SharedPreferences のインスタンス形成
        maxNumber = getSharedPreferences("maxNumber", Context.MODE_PRIVATE);
        editor = maxNumber.edit();


        titleText = (TextView) findViewById(R.id.titleText);
        categoryText = (TextView) findViewById(R.id.categoryText);
        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);


    }


    public void move(View v) {

        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);

    }


    public void select(View v) {

        //乱数を発生させて、それに一致するidをもつtopicを表示
        //swich?はどのような働きがあるの？？？？

        total = maxNumber.getInt("goukei", 0);

        //totalはidの最大値よりも1大きい数（記憶するときに+1されるから）。
        total = total--;


        if (total == 0) {

            titleText.setText("記録がありません！");


        } else {



            Topic topic = null;
            int selectedItemPosition2 = (int) categorySpinner.getSelectedItemPosition();


            //realmから検索
            //realmからソートを施した上で、ランダム検索したい！！！


            while (topic == null) {


                Random random = new Random();
                selectedNumber = random.nextInt(total);
                topic = realm.where(Topic.class).equalTo("id", selectedNumber)
                        .equalTo("selectedCategoryPosition",selectedItemPosition2).findFirst();




            }
            titleText.setText(topic.title);

            // res/array/list.xmlに定義したものから配列をつくる

            String[] categoryArray = this.getResources().getStringArray(R.array.list);

            // 保存してある番号から実際のCategoryを取得
            categoryText.setText(categoryArray[topic.selectedCategoryPosition]);
        }

    }



    @Override
    protected  void onDestroy(){
        super.onDestroy();

        realm.close();
    }

}
