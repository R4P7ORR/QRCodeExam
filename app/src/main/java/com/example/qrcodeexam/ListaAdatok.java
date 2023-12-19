package com.example.qrcodeexam;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListaAdatok extends AppCompatActivity {

    private EditText txtName;
    private EditText txtGrade;
    private Button btnMod;
    private Button btnCancel;
    private ListView listView;
    private String url;
    private List<Student> people = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_adatok);

        SharedPreferences sharedPref = getSharedPreferences("SharedPreferences",Context.MODE_PRIVATE);
        url = sharedPref.getString("scannedCode", "No data found!");
        init();

    }

    public void init(){
        txtName = findViewById(R.id.txtName);
        txtGrade = findViewById(R.id.txtGrade);
        btnMod = findViewById(R.id.btnMod);
        btnCancel = findViewById(R.id.btnCancel);
        listView = findViewById(R.id.listView);
        listView.setAdapter(new StudentAdapter());

        RequestTask task = new RequestTask(url, "GET");
        task.execute();
    }
    private class StudentAdapter extends ArrayAdapter<Student> {
        public StudentAdapter() {
            super(ListaAdatok.this, R.layout.person_list_adapter, people);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.person_list_adapter, null, false);
            Student actualPerson = people.get(position);
            TextView textViewModify = findViewById(R.id.textViewModify);
            TextView textViewName = findViewById(R.id.textViewName);
            TextView textViewGrade = view.findViewById(R.id.textViewGrade);

            textViewName.setText(actualPerson.getName());
            textViewGrade.setText(actualPerson.getGrade());

            textViewModify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    txtName.setText(String.valueOf(actualPerson.getName()));
                    txtGrade.setText(String.valueOf(actualPerson.getGrade()));
                }
            });
            return view;
        }
    }

    private class RequestTask extends AsyncTask<Void, Void, Response> {
        String requestUrl;
        String requestType;
        String requestParams;

        public RequestTask(String requestUrl, String requestType, String requestParams) {
            this.requestUrl = requestUrl;
            this.requestType = requestType;
            this.requestParams = requestParams;
        }

        public RequestTask(String requestUrl, String requestType) {
            this.requestUrl = requestUrl;
            this.requestType = requestType;
        }

        @Override
        protected Response doInBackground(Void... voids) {
            Response response = null;
            try {
                switch (requestType) {
                    case "GET":
                        response = RequestHandler.get(url);
                        break;
                    case "PUT":
                        response = RequestHandler.put(url, requestParams);
                        break;
                }
            } catch (IOException e) {
                Toast.makeText(ListaAdatok.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
            return response;
        }
        @Override
        protected void onPostExecute(Response response) {super.onPostExecute(response);
            Gson converter = new Gson();
            if (response.getResponseCode() >= 400) {
                Toast.makeText(ListaAdatok.this, "Hiba történt a kérés feldolgozása során", Toast.LENGTH_SHORT).show();
                Log.d("onPostExecuteError: ", response.getContent());
            }switch (requestType) {
                case "GET":
                    Student[] peopleArray = converter.fromJson(response.getContent(), Student[].class);
                    people.clear();
                    people.addAll(Arrays.asList(peopleArray));
                    break;
                case "PUT":
                    Student updatePerson = converter.fromJson(response.getContent(), Student.class);
                    people.replaceAll(person1 -> person1.getId() == updatePerson.getId() ? updatePerson : person1);
                    break;
            }
        }
    }
}