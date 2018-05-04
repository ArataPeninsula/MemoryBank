package android.lifeistech.com.memo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import io.realm.Realm;

public class TopActivity extends AppCompatActivity {

    public Realm realm;
    public int selectedNumber;

    SharedPreferences maxNumber;
    SharedPreferences.Editor editor;
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


    //while文が無限に回るのを防ぐために必要
    int checkNumber;






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
        levelText = (TextView) findViewById(R.id.levelText);

        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        levelSpinner = (Spinner) findViewById(R.id.levelSpinner);


        plusButton = (Button) findViewById(R.id.plusButton);
        minusButton = (Button) findViewById(R.id.minusButton);
        resetButton = (Button) findViewById(R.id.resetButton);

        plusButton.setEnabled(false);
        minusButton.setEnabled(false);
        resetButton.setEnabled(false);

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

        checkNumber = 0;


        if (total == 0) {

            titleText.setText("記録がありません！");


        } else {
            Topic topic = null;
            int selectedCategoryPosition = (int) categorySpinner.getSelectedItemPosition();
            int selectedLevelPosition = (int) levelSpinner.getSelectedItemPosition();


            //realmから検索
            //realmからソートを施した上で、ランダム検索したい！！！
            //levelSpinnerによって場合分け

///ここはwhileだとまずい！！！（条件に合致するものがない時無限に回り続ける。。）
///乱数を発生させる回数は決まっている！！→for文にして、total回を上限にする。
///どうやって終わらせる？　topicが見つかる  OR total回繰り返す　を設定できるか？？
            while (topic == null) {


                //これではダメ！！　ランダムに重複があるから！　重複しない乱数を作る必要あり！
                //0~totalまでのリストを作って、シャッフル。

                checkNumber = checkNumber + 1;

                if(checkNumber == total){
                    break;
                }



                if(selectedLevelPosition == 0) {


                    Random random = new Random();
                    selectedNumber = random.nextInt(total);
                    topic = realm.where(Topic.class).equalTo("id", selectedNumber)
                            .equalTo("selectedCategoryPosition", selectedCategoryPosition)
                            .findFirst();



                }else if(selectedLevelPosition == 1){

                    Random random = new Random();
                    selectedNumber = random.nextInt(total);
                    topic = realm.where(Topic.class).equalTo("id", selectedNumber)
                            .equalTo("selectedCategoryPosition", selectedCategoryPosition)
                            .lessThan("level",3)
                            .greaterThan("level",-3)
                            .findFirst();


                }else if(selectedLevelPosition == 2){

                    Random random = new Random();
                    selectedNumber = random.nextInt(total);
                    topic = realm.where(Topic.class).equalTo("id", selectedNumber)
                            .equalTo("selectedCategoryPosition", selectedCategoryPosition)
                            .greaterThan("level",2)
                            .findFirst();


                }else if(selectedLevelPosition == 3){

                    Random random = new Random();
                    selectedNumber = random.nextInt(total);
                    topic = realm.where(Topic.class).equalTo("id", selectedNumber)
                            .equalTo("selectedCategoryPosition", selectedCategoryPosition)
                            .lessThan("level",-2)
                            .findFirst();

                }





            }
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



        }

    }



    //nullの時アプリが落ちる！！！！


    public void plus(View v){
//トーストがうまくいかない！！
        //selectを押さない限りButtonは動かせないのでnullは考えなくてもよいかも？
        
        if(levelText == null) {

            titleText.setText("思い出してください！");

        } else{
        number1 = Integer.parseInt(levelText.getText().toString());

        number1 = number1 + 1;


        levelText.setText(String.valueOf(number1));

        final Topic topic = realm.where(Topic.class).equalTo("title"
                    ,titleText.getText().toString()).findFirst();

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

            final Topic topic = realm.where(Topic.class).equalTo("title"
                    ,titleText.getText().toString()).findFirst();

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

            final Topic topic = realm.where(Topic.class).equalTo("title"
                    ,titleText.getText().toString()).findFirst();

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
    protected  void onDestroy(){
        super.onDestroy();

        realm.close();
    }

}
