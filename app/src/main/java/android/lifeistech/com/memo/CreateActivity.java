package android.lifeistech.com.memo;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;

public class CreateActivity extends AppCompatActivity {

    public Realm realm;

    public EditText titleEditText;



    //SharedPreferences を宣言
    SharedPreferences maxNumber;

    SharedPreferences.Editor editor;
    public int total;




    //contentEditTextはコメントアウトしておく
    //public EditText contentEditText;


    //
    public Spinner categorySpinner;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        //realmを開く
        realm = Realm.getDefaultInstance();


        titleEditText = (EditText) findViewById(R.id.titleEditText);


        //contentEditText = (EditText) findViewById(R.id.contentEditText);

        //
        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);


        //SharedPreferences のインスタンス形成
        maxNumber = getSharedPreferences("maxNumber", Context.MODE_PRIVATE);
        editor = maxNumber.edit();



    }

    //ここから下がわからない！　title,updateDate はどこで定義したんだろう？
    //CreateActivityの部分の教科書を見直す(→createメソッドからみる）

    //id3はなんて名前でもよい

    public void save(final String title, final String updateDate,final int selectedItemPosition,
                     final int id3){

        realm.executeTransaction(new Realm.Transaction(){
            @Override
            public void execute(Realm bgRealm){
                Topic topic = realm.createObject(Topic.class);
                topic.title = title;
                topic.updateDate = updateDate;

                //topic.content = content;


                //realmに保存されるのはCategoryの文字情報ではなく、listのうちの何番目か、という情報
                //topic.category = category;

                topic.selectedCategoryPosition = selectedItemPosition;

                topic.id = id3;

                total = id3 + 1;

                editor.putInt("goukei",total);

            }
        });

    }

    //EditTextに入れたデータを元にMemo(のちのTopic)を作る
    public void create(View view) {

        //タイトルを取得

        String title = titleEditText.getText().toString();

        //日付を取得
        Date date = new Date();
        SimpleDateFormat sdf =  new SimpleDateFormat("yyy-MM-dd HH:mm:ss", Locale.JAPANESE);
        String updateDate = sdf.format(date);

        //contentの取得はコメントアウト

        //String content = contentEditText.getText().toString();

        //カテゴリーを取得

        //Spinnerにはどんな型でも入れられる→getSelectedItem():オブジェクト型(多くの変数型のSuper.)を取得
        // ＋（String)でString型にCast
        int selectedItemPosition = (int) categorySpinner.getSelectedItemPosition();



        //topicのidを取得

        total = maxNumber.getInt("goukei",0);



        //int id2 = total;



        //check(title,updateDate,content);


        save(title,updateDate,selectedItemPosition,total);


        //画面を終了する
        finish();
    }


    //引数からcontentを消した
    private void check(String title,String updateDate){

        Topic topic = new Topic();

        topic.title = title;
        topic.updateDate = updateDate;
        //topic.content = content;

        Log.d("Topic",topic.title);
        Log.d("Topic",topic.updateDate);
        //Log.d("Topic",topic.content);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        realm.close();

    }
}