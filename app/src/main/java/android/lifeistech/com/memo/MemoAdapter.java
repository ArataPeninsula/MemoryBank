package android.lifeistech.com.memo;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class MemoAdapter extends ArrayAdapter<Topic> {

    private LayoutInflater layoutinflater;

    MemoAdapter(Context context, int textViewResourceId, List<Topic> objects) {
        super(context, textViewResourceId, objects);
        layoutinflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(int position, View convertView,  ViewGroup parent) {

        Topic topic = getItem(position);

        if (convertView == null) {
            convertView = layoutinflater.inflate(R.layout.layout_item_memo, null);
        }

        TextView titleText = (TextView) convertView.findViewById(R.id.titleText);
        TextView categoryText = (TextView) convertView.findViewById(R.id.categoryText);

        //level追加
        TextView levelText = (TextView) convertView.findViewById(R.id.levelText);


        titleText.setText(topic.title);

        //contentText.setText(topic.content);



//        // res/array/list.xmlに定義したものから配列をつくる
//         String[] categoryArray = getContext().getResources().getStringArray(R.array.list);

        //Jsonを利用してSharedPreferencesからAllayListを取得


        SharedPreferences pref;
        pref = getContext().getSharedPreferences("pref_mb", Context.MODE_PRIVATE);
        Gson gson = new Gson();

         ArrayList<String> arrayList = gson.fromJson(pref.getString("category","")
                 ,new TypeToken<ArrayList<String>>(){}.getType());


         //出て来たarrayListはどうやらobject型らしい。Cast

        //ArrayList<> to Object[] これは使用上仕方ない！脳死してコピペしてる（引数にStringの空配列を入れてるらしい）。

        String[] categoryArray = (String[]) arrayList.toArray(new String[]{});


        // 保存してある番号から実際のCategoryを取得
        categoryText.setText(categoryArray[topic.selectedCategoryPosition]);

        //categoryText.setText(arrayList.get(topic.selectedCategoryPosition));


        // 鉄板度の表示
        levelText.setText(String.valueOf(topic.level));




        return convertView;
    }
}
