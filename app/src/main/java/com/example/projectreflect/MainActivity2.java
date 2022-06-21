package com.example.projectreflect;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class MainActivity2 extends AppCompatActivity {

    Context con = this;

    ListView lvMain;
    ArrayList<String> st = new ArrayList<String>();
    MyTask mt;



    BoxAdapter adapter1 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        adapter1 = new BoxAdapter(con,st);
        lvMain = (ListView) findViewById(R.id.list);
        lvMain.setAdapter(adapter1);
        Bundle arguments = getIntent().getExtras();
        int pos = arguments.getInt("position");
        mt = new MyTask();
        mt.execute(Integer.toString(pos));
    }

    public void onclick(View v) {




    }


    class MyTask extends AsyncTask<String, Void, ArrayList<String[]>> {


        @Override
        protected void onPreExecute() {

            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(ArrayList<String[]> result) {
            super.onPostExecute(result);
            adapter1.notifyDataSetChanged();
            ListView lvMain = (ListView) findViewById(R.id.lvMain);


        }

        @Override
        protected ArrayList<String[]> doInBackground(String... params) {
            ArrayList<String[]> res = new ArrayList<>();
            HttpURLConnection myConnection = null;
            try {

                //myConnection = (HttpURLConnection) mySite.openConnection();

                myConnection = (HttpURLConnection) new URL("http://10.0.2.2:8080/kino?id=4&idTheater=" + params[0]).openConnection();
                myConnection.connect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            int i = 0;

            StringBuilder sb = new StringBuilder();

            try {
                if (HttpURLConnection.HTTP_OK == myConnection.getResponseCode()) {

                    BufferedReader in = new BufferedReader(new InputStreamReader(myConnection.getInputStream(), "utf8"));

                    String line;

                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                    }

                    line = sb.toString();

                    String pattern = "\\[\\d*]=>  string\\(\\d*\\)";
                    Pattern pat = Pattern.compile(pattern);

                    Pattern pat1 = Pattern.compile("\\[\\d*]=>  array\\(\\d*\\) \\{");
                    String[] parts = pat.split(line);

                    StringBuilder sb1 = new StringBuilder();



                    st.clear();


                    for (int it = 1; it < parts.length; it++) {
                        parts[it] = parts[it].replace("\"","");
                        parts[it] = parts[it].replace("}","");
                            st.add(parts[it]);
                        int jopa = 0;
                    }




                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (myConnection != null) {
                    myConnection.disconnect();
                }

            }


            return res;
        }
    }
        public class BoxAdapter extends BaseAdapter {
            Context ctx;
            LayoutInflater lInflater;
            ArrayList<String> objects;

            BoxAdapter(Context context, ArrayList<String> FilmName) {
                ctx = context;
                objects = FilmName;
                lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            // кол-во элементов
            @Override
            public int getCount() {
                return objects.size();
            }





            // элемент по позиции
            @Override
            public String getItem(int position) {
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
                    view = lInflater.inflate(R.layout.kinoadapter, parent, false);
                }

                String p = getItem(position);

                // заполняем View в пункте списка данными из товаров: наименование, цена
                // и картинка
                ((TextView) view.findViewById(R.id.name)).setText(p);




                return view;
            }

            // товар по позиции
            String getProduct(int position) {
                return ((String) getItem(position));
            }



        }

}