package android.lifeistech.com.memo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import io.realm.Realm;

public class DetailActivity extends AppCompatActivity {

    public Realm realm;

    public EditText titleText;
    public Spinner categorySpinner;
    public TextView levelText;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //Realmを開く
        realm = Realm.getDefaultInstance();

        titleText =(EditText) findViewById(R.id.titleEditText);
        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        levelText = (TextView) findViewById(R.id.levelText);



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