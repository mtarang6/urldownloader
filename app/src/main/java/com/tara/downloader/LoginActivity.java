package com.tara.downloader;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity implements AuthenticationListener {
    private Button btn_login;
    AuthenticationDialog authenticationDialog;
    private String token = null;
    AppPreferences appPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUi();
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(token!=null)
                {
                    logout();
                }
                else {
                    authenticationDialog = new AuthenticationDialog(LoginActivity.this,LoginActivity.this);
                    authenticationDialog.setCancelable(true);
                    authenticationDialog.show();
                }
            }
        });
    }

    private void initUi() {
        btn_login = (Button)findViewById(R.id.btn_login);
    }

    @Override
    public void onTokenReceived(String auth_token) {
        if (auth_token == null)
            return;
        appPreferences.putString(AppPreferences.TOKEN, auth_token);
        token = auth_token;
        getUserInfoByAccessToken(token);
    }

    private void getUserInfoByAccessToken(String token) {
        new RequestInstagramAPI().execute();
    }
    private class RequestInstagramAPI extends AsyncTask<Void, String, String> {

        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(getResources().getString(R.string.get_user_info_url) + token);
            try {
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                return EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonData = jsonObject.getJSONObject("data");
                    if (jsonData.has("id")) {
                        appPreferences.putString(AppPreferences.USER_ID, jsonData.getString("id"));
                        appPreferences.putString(AppPreferences.USER_NAME, jsonData.getString("username"));
                        appPreferences.putString(AppPreferences.PROFILE_PIC, jsonData.getString("profile_picture"));
                        login();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast toast = Toast.makeText(getApplicationContext(),"Login error!",Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
    public void login() {
        Intent intent  = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
       /* Button button = findViewById(R.id.btn_login);
        View info = findViewById(R.id.info);
        ImageView pic = findViewById(R.id.pic);
        TextView id = findViewById(R.id.id);
        TextView name = findViewById(R.id.name);
        info.setVisibility(View.VISIBLE);
        button.setText("LOGOUT");
        name.setText(appPreferences.getString(AppPreferences.USER_NAME));
        id.setText(appPreferences.getString(AppPreferences.USER_ID));
        Picasso.get().load(appPreferences.getString(AppPreferences.PROFILE_PIC)).
                into(pic);*/
    }
    public void logout() {
        btn_login.setText("INSTAGRAM LOGIN");
        token = null;
        // info.setVisibility(View.GONE);
        appPreferences.clear();
    }
}
