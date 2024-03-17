package com.tom.atm;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TransActivity extends AppCompatActivity {

    private String TAG = TransActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<Transation> transations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        new TransTask().execute("https://atm201605.appspot.com/h");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://atm201605.appspot.com/h")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String json = response.body().string();
                Log.d(TAG, "onResponse: " + json);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        parseJSON(json);
                        parseGSON(json);
                    }
                });
            }
        });
    }

    private void parseGSON(String json) {
        Gson gson = new Gson();
        transations = gson.fromJson(json,
                new TypeToken<ArrayList<Transation>>() {
                }.getType()
        );
        TransAdapter adapter = new TransAdapter();
        recyclerView.setAdapter(adapter);

    }

    private void parseJSON(String json) {
        transations = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                transations.add(new Transation(object));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        TransAdapter adapter = new TransAdapter();
        recyclerView.setAdapter(adapter);
    }

    public class TransAdapter extends RecyclerView.Adapter<TransAdapter.TransHolder> {

        @NonNull
        @Override
        public TransHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_trans, parent, false);
            return new TransHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TransHolder holder, int position) {
            Transation tran = transations.get(position);
            holder.bindTo(tran);
        }

        @Override
        public int getItemCount() {
            return transations.size();
        }

        public class TransHolder extends RecyclerView.ViewHolder {
            TextView dateText;
            TextView amountText;
            TextView typeText;

            public TransHolder(@NonNull View itemView) {
                super(itemView);
                dateText = itemView.findViewById(R.id.item_date);
                amountText = itemView.findViewById(R.id.item_amount);
                typeText = itemView.findViewById(R.id.item_type);
            }

            public void bindTo(Transation tran) {
                dateText.setText(tran.getDate());
                amountText.setText(String.valueOf(tran.getAmount()));
                typeText.setText(String.valueOf(tran.getType()));
            }
        }

    }


    public class TransTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder sb;
            try {
                URL url = new URL(strings[0]);
                InputStream is = url.openStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                sb = new StringBuilder();
                String line = in.readLine();
                while (line != null) {
                    sb.append(line);
                    line = in.readLine();
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute: " + s);
        }
    }
}