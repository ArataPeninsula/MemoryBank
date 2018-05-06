package android.lifeistech.com.memo;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import io.realm.Realm;

public class DetailActivity extends AppCompatActivity {

    public Realm realm;

    public EditText titleText;
    public Spinner categorySpinner;
    public TextView levelText;


    SharedPreferences pref;

    SharedPreferences.Editor editor;


    //カテゴリーを動的に表示することを目指す。
    ArrayList<String> arrayList;

    ArrayAdapter adapter;

    Gson gson;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //Realmを開く
        realm = Realm.getDefaultInstance();

        titleText =(EditText) findViewById(R.id.titleEditText);
        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        levelText = (TextView) findViewById(R.id.levelText);


        //GsonでcategorySpinnerにリストを表示
        pref = getSharedPreferences("pref_mb",MODE_PRIVATE);
        gson = new Gson();

        arrayList = new ArrayList<>();

        adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item);


        arrayList = gson.fromJson(pref.getString("category","")
                ,new TypeToken<ArrayList<String>>(){}.getType());


        for(int i = 0; i < arrayList.size(); i++){

            adapter.add(arrayList.get(i));

        }



        categorySpinner.setAdapter(adapter);





        showData();

    }

    public void showData(){

        final Topic topic = realm.where(Topic.class).equalTo("updateDate",
                getIntent().getStringExtra("updateDate")).findFirst();

        titleText.setText(topic.title);
        //contentText.setText(topic.content);

        categorySpinner.setSelection(topic.selectedCategoryPosition);

        //鉄板度を表示
        levelText.setText(String.valueOf(topic.level));

    }

    public void update (View view){

        final Topic topic = realm.where(Topic.class).equalTo("updateDate"
                ,getIntent().getStringExtra("updateDate")).findFirst();

        //更新する
        realm.executeTransaction(new Realm.Transaction(){
            @Override
            public void execute(Realm realm){
                topic.title = titleText.getText().toString();
                //topic.content = contentText.getText().toString();
                topic.selectedCategoryPosition = categorySpinner.getSelectedItemPosition();


            }
        });

        //画面を閉じる
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //realmを閉じる

        realm.close();
    }
}