package android.lifeistech.com.memo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class SettingActivity extends AppCompatActivity {


    Realm realm;
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

        realm = Realm.getDefaultInstance();


        listView = (ListView) findViewById(R.id.listView);

        categoryEditText = (EditText) findViewById(R.id.categoryEditText);





//

        //SharedPreferences のインスタンス形成
        pref = getSharedPreferences("pref_mb", Context.MODE_PRIVATE);
        editor = pref.edit();

        //Gsonインスタンス形成

        gson = new Gson();

        arrayList = new ArrayList<>();


        //SettingActivity上のListViewを表示させるためのArrayAdapter
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1);

        //SharedPreferencesからJsonを読み込んで、ArrayListへと再変換??

        arrayList = gson.fromJson(pref.getString("category","")
                ,new TypeToken<ArrayList<String>>(){}.getType());


        adapter.clear();

        for(int i = 0; i < arrayList.size(); i++) {

            adapter.add(arrayList.get(i));




        }


        //一旦listViewを初期化してから再度記録されたListを表示

        listView.setAdapter(adapter);




        //onClickListenerでジャンルの削除機能。
        //アラート画面＋realmに保存されたselectedCategoryPositionの調整
        //0番目の「未分類」は消せないようにする。消されたジャンルで登録されていたトピックは「未分類」に行く

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);

                builder.setMessage("このジャンルを消去しますか？　このジャンルの話題は「未分類」になります。")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                adapter = (ArrayAdapter) listView.getAdapter();


                                //object型
                                Object selectedCategory =  adapter.getItem(position);




                                //データを消して、リストを並べなおす。見た目上消去される。
                                adapter.remove(selectedCategory);

                                adapter.notifyDataSetChanged();


                                //削除を反映させたArraylistを記憶させる
                                //arraylistを初期化して、positionに保存されているものを消去
                                //SharedPreferencesへと反映

                                arrayList = gson.fromJson(pref.getString("category","")
                                        ,new TypeToken<ArrayList<String>>(){}.getType());


                                arrayList.remove(position);


                                editor.remove("category");

                                editor.putString("category",gson.toJson(arrayList));
                                editor.apply();



                                //realmから該当するカテゴリーのtopicを全て取り出して、0を代入

                                int total = pref.getInt("goukei",0);

                                for(int i = 0; i < total; i++) {
                                    final Topic topic = realm.where(Topic.class)
                                            .equalTo("id", i)
                                            .equalTo("selectedCategoryPosition", position)
                                            .findFirst();

                                    if( topic != null) {

                                        realm.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {

                                                topic.selectedCategoryPosition = 0;

                                            }
                                        });
                                    }

                                }


                                //削除されるカテゴリーより下に表示されていたカテゴリーのselectedCategoryPositionを一律で-1する

                                    for(int i = 0; i < total; i++){

                                        final Topic topic2 = realm.where(Topic.class)
                                                        .equalTo("id",i)
                                                        .greaterThan("selectedCategoryPosition",position)
                                                        .findFirst();


                                        if (topic2 != null) {


                                            realm.executeTransaction(new Realm.Transaction() {
                                                @Override
                                                public void execute(Realm realm) {

                                                    topic2.selectedCategoryPosition = topic2.selectedCategoryPosition - 1;

                                                }
                                            });
                                        }
                                    }



                            }
                        })

                        .setNegativeButton("キャンセル",null)
                        .setCancelable(true);

                builder.show();
                return false;

            }
        });

    }


    public void categoryAdd(View v){

       adapter.clear();

        //ジャンルを動的に編集（追加）する
        arrayList = gson.fromJson(pref.getString("category","")
                ,new TypeToken<ArrayList<String>>(){}.getType());

        arrayList.add(categoryEditText.getText().toString());



        //EditTextから文字を消去
        categoryEditText.setText("");


        for(int i = 0; i < arrayList.size(); i++) {

            adapter.add(arrayList.get(i));


        }


        listView.setAdapter(adapter);

        //↓が問題。このままだともともと保存されていたリストが重複して保存されてしまう。一旦データを消して再登録。

        editor.remove("category");

        editor.putString("category",gson.toJson(arrayList));
        editor.apply();



    }

    public void close(View v){

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

        listView.setAdapter(adapter);

    }

    @Override
    protected  void onRestart(){
        super.onRestart();

        reshow();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        realm.close();


    }
}
