package com.example.projectreflect;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Theater extends AppCompatActivity {
    public TextView tvInfo;
    Context con = this;
    EditText tvName;
    ListView lvMain;
    TextView text;
    MyTask mt;
    ProgressDialog pdLoading;
    ProgressBar loading;
    ArrayList<Theaters> th = null;
    BoxAdapter adapter1 = null;

    Theaters[] theaters = new Theaters[]{
        new Theaters("a", "a"),
        new Theaters("b", "b"),
        new Theaters("c", "c"),
};
    String[] test22 = new String[] {
            "Рыжик", "Барсик", "Мурзик", "Мурка", "Васька",
            "Томасина", "Кристина", "Пушок", "Дымка", "Кузя",
            "Китти", "Масяня", "Симба"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theater);
        tvInfo = (TextView) findViewById(R.id.tvInfo);
        tvName = (EditText) findViewById(R.id.editTextTextPersonName);
        text = (TextView) findViewById(R.id.textView);
        loading = findViewById(R.id.loading);
        loading.setVisibility(View.INVISIBLE);
        lvMain = (ListView) findViewById(R.id.lvMain);


        th = new ArrayList<Theaters>();
        th.add(new Theaters("Пусто",""));


        adapter1 = new BoxAdapter(con,th);

        lvMain.setAdapter(adapter1);
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                          public void onItemClick(AdapterView<?> parent, View view,
                                                                  int position, long id) {

                                                                    setView(position+1);
                                          }
                                      });
// используем адаптер данных



    }
    public void onclick(View v) {
        mt = new MyTask();
        mt.execute(tvName.getText().toString());
        loading.setVisibility(View.VISIBLE);


    }


    public void setView(int pos){
        Intent intent = new Intent(this,MainActivity2.class);
        intent.putExtra("position",pos);
        startActivity(intent);
    }
    class MyTask extends AsyncTask<String, Void, ArrayList<String[]>> {



        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            tvInfo.setText("Begin");
        }

        @Override
        protected void onPostExecute(ArrayList<String[]> result) {
            super.onPostExecute(result);
            adapter1.notifyDataSetChanged();
            ListView lvMain = (ListView) findViewById(R.id.lvMain);

            tvInfo.setText("End");
        }

        @Override
        protected ArrayList<String[]> doInBackground(String... params) {
            ArrayList<String[]> res=new ArrayList <>();
            HttpURLConnection myConnection = null;
            try {

                //myConnection = (HttpURLConnection) mySite.openConnection();

                myConnection = (HttpURLConnection) new URL("http://10.0.2.2:8080/kino?id=1&name="+params[0]).openConnection();
                myConnection.connect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            int i=0;

            StringBuilder sb = new StringBuilder();

            try {
                if(HttpURLConnection.HTTP_OK == myConnection.getResponseCode()){

                    BufferedReader in = new BufferedReader(new InputStreamReader(myConnection.getInputStream(),"utf8"));

                    String line;

                    while((line=in.readLine())!=null){
                        sb.append(line);
                    }

                    line = sb.toString();

                    String pattern = "\\[\"\\w*\"]=>    string\\(\\d*\\)";
                    Pattern pat = Pattern.compile(pattern);
                    String[] parts = pat.split(line);
                    Pattern pat1 = Pattern.compile("\\[\\d*]=>  array\\(\\d*\\) \\{");



                    StringBuilder sb1 = new StringBuilder();
                    int lines = 1;

                    for(int it = 1;it<parts.length;it++){
                        sb1.append(parts[it]);
                        sb1.append("\r\n");


                        lines++;
                    }

                    line = sb1.toString();
                    String[] parts1 = pat1.split(line);




                    StringBuilder sb2 = new StringBuilder();

                    for(int it = 0;it<parts1.length;it++){
                        sb2.append(parts1[it]);


                    }
                    line  = sb2.toString();
                    line = line.replace("\"","");
                    line = line.replace("}"," ");

                    Pattern pat2 = Pattern.compile("\r\n");

                    String[] parts2 = pat2.split(line);
                    String name = null;
                    String desc = null;


                    th.clear();


                    for(int it = 1;it<parts2.length+1;it++){
                        if(it%2==0){
                            desc = parts2[it-1];
                            Theaters buf = new Theaters(name,desc);
                            th.add(buf);
                        }
                        else{
                            name = parts2[it-1];
                        }
                    }

                    if(th.isEmpty()){
                        th.add(new Theaters("Пусто",""));
                    }

                    text.setText(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if(myConnection!=null){
                    myConnection.disconnect();
                }
                loading.setVisibility(View.INVISIBLE);
            }


            return res;
        }



}



    class Theaters {
        String name;
        String description;

        public Theaters(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }

    public class BoxAdapter extends BaseAdapter {
        Context ctx;
        LayoutInflater lInflater;
        ArrayList<Theaters> objects;

        BoxAdapter(Context context, ArrayList<Theaters> products) {
            ctx = context;
            objects = products;
            lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        // кол-во элементов
        @Override
        public int getCount() {
            return objects.size();
        }



        public void add(Theaters t){

          objects.add(t);

        }

        // элемент по позиции
        @Override
        public Object getItem(int position) {
            return objects.get(position);
        }

        // id по позиции
        @Override
        public long getItemId(int position) {
            return position;
        }

        // пункт списка
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // используем созданные, но не используемые view
            View view = convertView;
            if (view == null) {
                view = lInflater.inflate(R.layout.item, parent, false);
            }

            Theaters p = getProduct(position);

            // заполняем View в пункте списка данными из товаров: наименование, цена
            // и картинка
            ((TextView) view.findViewById(R.id.cheese_name)).setText(p.name);
            ((TextView) view.findViewById(R.id.cheese_description)).setText(p.description);



            return view;
        }

        // товар по позиции
        Theaters getProduct(int position) {
            return ((Theaters) getItem(position));
        }



    }

}
