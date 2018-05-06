package android.lifeistech.com.memo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity {

    //SharedPreferences を宣言

    SharedPreferences pref;

    SharedPreferences.Editor editor;



    //カテゴリーを動的に表示することを目指す。
    ArrayList<String> arrayList;

    ArrayAdapter adapter;


    Gson gson;


    //
    ListView listView;
    EditText categoryEditText;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        listView = (ListView) findViewById(R.id.listView);

//

        //SharedPreferences のインスタンス形成
        pref = getSharedPreferences("pref_mb", Context.MODE_PRIVATE);
        editor = pref.edit();

        //Gsonインスタンス形成

        gson = new Gson();

        arrayList = new ArrayList<>();

//        //とりあえず適当に入れる。
//
//        arrayList.add("");
//        arrayList.add("悲しい");
//        arrayList.add("怒った");
//        arrayList.add("アイデア");


        //SettingActivity上のListViewを表示させるためのArrayAdapter
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1);


        //arrayListをGsonを通じてJsonに変換することでSharedPreferencesに保存可能に？？
//        editor.putString("category",gson.toJson(arrayList));
//        editor.apply();


        //SharedPreferencesからJsonを読み込んで、ArrayListへと再変換??

        arrayList = gson.fromJson(pref.getString("category","")
                ,new TypeToken<ArrayList<String>>(){}.getType());



        for(int i = 0; i < arrayList.size(); i++) {

            adapter.add(arrayList.get(i));


        }

        listView.setAdapter(adapter);


        //
        //
        categoryEditText = (EditText) findViewById(R.id.categoryEditText);



    }


    public void categoryAdd(View v){

        //ジャンルを動的に編集（追加）する
        arrayList = gson.fromJson(pref.getString("category","")
                ,new TypeToken<ArrayList<String>>(){}.getType());

        arrayList.add(categoryEditText.getText().toString());


        editor.putString("category",gson.toJson(arrayList));
        editor.apply();



        //EditTextから文字を消去
        categoryEditText.setText("");

        //リストを更新

        arrayList = gson.fromJson(pref.getString("category","")
                ,new TypeToken<ArrayList<String>>(){}.getType());



        for(int i = 0; i < arrayList.size(); i++) {

            adapter.add(arrayList.get(i));


        }

        listView.setAdapter(adapter);



    }

    public void close(View v){

        finish();
    }

    public void reshow(){




    }
}
