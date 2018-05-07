package android.lifeistech.com.memo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;

public class CreateActivity extends AppCompatActivity {

    public Realm realm;

    public EditText titleEditText;



    //SharedPreferences を宣言
    //命名変更
    SharedPreferences pref;

    SharedPreferences.Editor editor;

    public int total;




    public Spinner categorySpinner;

    //カテゴリーを動的に表示することを目指す。
    ArrayList<String> arrayList;

    ArrayAdapter adapter;

    Gson gson;








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
        pref = getSharedPreferences("pref_mb", Context.MODE_PRIVATE);
        editor = pref.edit();

        //gsonインスタンス形成

         gson = new Gson();

        arrayList = new ArrayList<>();



        //SettingActivity上のListViewを表示させるためのArrayAdapter
        adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item);


        //arrayListをGsonを通じてJsonに変換することでSharedPreferencesに保存可能に？？
//        editor.putString("category",gson.toJson(arrayList));
//        editor.apply();


        //SharedPreferencesからJsonを読み込んで、ArrayListへと再変換??

        arrayList = gson.fromJson(pref.getString("category","")
                ,new TypeToken<ArrayList<String>>(){}.getType());



        for(int i = 0; i < arrayList.size(); i++) {

            adapter.add(arrayList.get(i));


        }

        categorySpinner.setAdapter(adapter);








    }



    //id3はなんて名前でもよい

    public void save(final String title, final String updateDate,final int selectedCategoryPosition,
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

                topic.selectedCategoryPosition = selectedCategoryPosition;

                topic.id = id3;

                total = id3 + 1;


                //↓ここなら少なくとも動作した
                //editor = pref.edit();

                editor.putInt("goukei",total);
                editor.apply();

                //levelは最初は全て0でいい
                topic.level = 0;

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



        //Spinnerにはどんな型でも入れられる→getSelectedItem():オブジェクト型(多くの変数型のSuper.)を取得
        // ＋（String)でString型にCast
        int selectedCategoryPosition = (int) categorySpinner.getSelectedItemPosition();



        //topicのidを取得

        total = pref.getInt("goukei",0);


        //check(title,updateDate,content);


        save(title,updateDate,selectedCategoryPosition,total);


        //画面を終了する
        finish();
    }



    private void check(String title,String updateDate){

        Topic topic = new Topic();

        topic.title = title;
        topic.updateDate = updateDate;
        //topic.content = content;

        Log.d("Topic",topic.title);
        Log.d("Topic",topic.updateDate);
        //Log.d("Topic",topic.content);

    }

    //SettingActivityへ遷移。仮に。
    public void move(View v){

        Intent intent = new Intent(this,SettingActivity.class);

        startActivity(intent);

        finish();
    }

    public void reshow(){

        //ジャンルの追加を更新。onRestartとかに入れればいいはず。。。


        adapter.clear();

        arrayList = gson.fromJson(pref.getString("category","")
                ,new TypeToken<ArrayList<String>>(){}.getType());


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
    protected void onDestroy() {
        super.onDestroy();

        realm.close();

    }
}