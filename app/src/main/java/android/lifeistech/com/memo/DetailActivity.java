package android.lifeistech.com.memo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import io.realm.Realm;

public class DetailActivity extends AppCompatActivity {

    public Realm realm;

    public EditText titleText;
    public EditText contentText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //Realmを開く
        realm = Realm.getDefaultInstance();

        titleText =(EditText) findViewById(R.id.titleEditText);
        contentText = (EditText) findViewById(R.id.contentEditText);

        showData();

    }

    public void showData(){

        final Memo memo = realm.where(Memo.class).equalTo("updateDate",
                getIntent().getStringExtra("updateDate")).findFirst();

        titleText.setText(memo.title);
        contentText.setText(memo.content);
    }

    public void update (View view){

        final Memo memo = realm.where(Memo.class).equalTo("updateDate"
                ,getIntent().getStringExtra("updateDate")).findFirst();

        //更新する
        realm.executeTransaction(new Realm.Transaction(){
            @Override
            public void execute(Realm realm){
                memo.title = titleText.getText().toString();
                memo.content = contentText.getText().toString();

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