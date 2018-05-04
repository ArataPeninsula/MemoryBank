package android.lifeistech.com.memo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

    MemoAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //realmを開く
        realm = Realm.getDefaultInstance();

        listView = (ListView) findViewById(R.id.listView);


        //onResumeのshowメソッドをonCreateでも行う。削除機能のために。
        RealmResults<Topic> results = realm.where(Topic.class).findAll();
        List<Topic> items = realm.copyFromRealm(results);

        adapter = new MemoAdapter(this,R.layout.layout_item_memo,items);

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

        //長押しでダイアログ→okを押すとリストとrealmから削除される。

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setMessage("削除してよろしいですか？")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                adapter = (MemoAdapter) listView.getAdapter();

                                Topic topic = adapter.getItem(position);



                                //データを消して、リストを並べなおす
                                adapter.remove(topic);

                                adapter.notifyDataSetChanged();

                                //realmからの消去
                                RealmResults<Topic> list = realm.where(Topic.class)
                                        .equalTo("updateDate",topic.updateDate).findAll();


                                //begin-commitに挟むことで更新
                                realm.beginTransaction();

                                list.deleteFirstFromRealm();

                                realm.commitTransaction();






                            }
                        })

                        .setNegativeButton("キャンセル",null)
                        .setCancelable(true);

                builder.show();
                return false;

            }
        });




    }

    public void setMemoList(){

        //realmから読み取る
        //削除機能の関係で、updateDateでのソートが必要！！！
        RealmResults<Topic> results = realm.where(Topic.class).findAll().sort("updateDate");

        List<Topic> items = realm.copyFromRealm(results);

        //削除機能の関係で一旦onCreateへ
        adapter = new MemoAdapter(this,R.layout.layout_item_memo,items);

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
