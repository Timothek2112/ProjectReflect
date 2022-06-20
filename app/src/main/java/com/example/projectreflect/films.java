package com.example.projectreflect;

import androidx.appcompat.app.AppCompatActivity;

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

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class films extends AppCompatActivity {

    ArrayList<TheatersFilm> th = new ArrayList<TheatersFilm>();
    EditText editText;
    TextView tvInfo;
    ProgressBar loading;
    ConnectTask ct;
    ListView lvMain;
    ThreeStrAdapter adapter;
    Context con = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_films);

        lvMain = (ListView) findViewById(R.id.lvMain);
        tvInfo = (TextView) findViewById(R.id.tvInfo);
        loading = (ProgressBar) findViewById(R.id.loading);
        editText = (EditText) findViewById(R.id.editTextTextPersonName);
        th.add(new TheatersFilm("Пусто","",""));
        adapter = new ThreeStrAdapter(con,th);

        lvMain.setAdapter(adapter);

    }

    public void OnClick(View v){
        ct = new ConnectTask();
        ct.execute(editText.getText().toString());
        loading.setVisibility(View.VISIBLE);
    }


    class ConnectTask extends AsyncTask<String, Void, ArrayList<String[]>> {

        @Override
        protected void onPostExecute(ArrayList<String[]> result){
            super.onPostExecute(result);
            ListView lvMain = (ListView) findViewById(R.id.lvMain);
            tvInfo.setText("End");
            adapter.notifyDataSetChanged();
            loading.setVisibility(View.INVISIBLE);
        }


        @Override
        protected ArrayList<String[]> doInBackground(String... params){
            ArrayList<String[]> res=new ArrayList <>();

            HttpURLConnection connection = null;
                    try{
                        connection = (HttpURLConnection) new URL("http://10.0.2.2:8080/kino?id=2&name="+params[0]).openConnection();
                        connection.connect();

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    StringBuilder sb = new StringBuilder();

            try {
                if(HttpURLConnection.HTTP_OK == connection.getResponseCode()){

                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf8"));

                    String line;

                    while((line = in.readLine()) !=null){
                        sb.append(line);
                    }

                    line = sb.toString();

                    String pattern = "\\[\"\\w*\"]=>    string\\(\\d*\\)";
                    Pattern pat = Pattern.compile(pattern);
                    String[] parts = pat.split(line);
                    Pattern pat1 = Pattern.compile("\\[\\d*]=>  array\\(\\d*\\) \\{");



                    StringBuilder sb1 = new StringBuilder();


                    for(int it = 1;it<parts.length;it++){
                        sb1.append(parts[it]);
                        sb1.append("\r\n");



                    }

                    line = sb1.toString();
                    String[] parts1 = pat1.split(line);




                    StringBuilder sb2 = new StringBuilder();

                    for(int it = 0;it<parts1.length;it++){
                        sb2.append(parts1[it]);


                    }
                    line  = sb2.toString();

                    line = line.replace("}"," ");
                    line = line.replace("\"","");
                    Pattern pat2 = Pattern.compile("\r\n");

                    String[] parts2 = pat2.split(line);




                    th.clear();

                    for(int i = 0;i<parts2.length;i+=3){

                        TheatersFilm t = new TheatersFilm(parts2[i],parts2[i+1],parts2[i+2]);
                        th.add(t);

                    }



                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            return res;
        }

        protected void onPreExecute() {

            super.onPreExecute();
            tvInfo.setText("Begin");
        }

    }



    class TheatersFilm {
        String name;
        String address;
        String film;

        public TheatersFilm(String name, String address,String film) {
            this.name = name;
            this.address = address;
            this.film = film;
        }
    }




    public class ThreeStrAdapter extends BaseAdapter {
        Context ctx;
        LayoutInflater lInflater;
        ArrayList<TheatersFilm> objects;

        ThreeStrAdapter(Context context, ArrayList<TheatersFilm> products) {
            ctx = context;
            objects = products;
            lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        // кол-во элементов
        @Override
        public int getCount() {
            return objects.size();
        }


        public void add(TheatersFilm t) {

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

            View view = convertView;
            if (view == null) {
                view = lInflater.inflate(R.layout.kinofilmsitem, parent, false);
            }

            TheatersFilm p = getProduct(position);


            ((TextView) view.findViewById(R.id.Name)).setText(p.name);
            ((TextView) view.findViewById(R.id.Address)).setText(p.address);
            ((TextView) view.findViewById(R.id.FilmName)).setText(p.film);

            return view;
        }
        TheatersFilm getProduct(int position) {
    return ((TheatersFilm) getItem(position));
}
    }
}