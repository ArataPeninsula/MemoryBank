package android.lifeistech.com.memo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    public Realm realm;

    public ListView listView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //realmを開く
        realm = Realm.getDefaultInstance();

        listView = (ListView) findViewById(R.id.listView);

        //編集画面に遷移（クリック）
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Topic topic = (Topic) parent.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                intent.putExtra("updateDate",topic.updateDate);
                startActivity(intent);

            }
        });


    }

    public void setMemoList(){

        //realmから読み取る
        RealmResults<Topic> results = realm.where(Topic.class).findAll();
        List<Topic> items = realm.copyFromRealm(results);

        MemoAdapter adapter = new MemoAdapter(this,R.layout.layout_item_memo,items);

        listView.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();

        setMemoList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        realm.close();

    }


    public void create(View view){

        Intent intent = new Intent(this,CreateActivity.class);
        startActivity(intent);



    }

    public void move(View v){

        Intent intent = new Intent(this,TopActivity.class);
        startActivity(intent);



    }
}
