package com.example.ehgol;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GamesActivity extends Activity {
	private String get_url = "http://192.168.0.12:3000/games";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);

        Button b = (Button) findViewById(R.id.games_button);
        b.setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		GetGamesTask g = new GetGamesTask();
        		JSONArray games_json = null;
        		TextView t = (TextView) findViewById(R.id.games_text);
        		try {
					games_json = new JSONArray(g.execute().get());
        			t.setText(games_json.toString());
        		} catch (Exception e) {
        			e.printStackTrace();
        		}
        		//for(String info : games_json.names()) {
//        			t.append(games_json.toString() + "...\n");
        		//}
        	}
        });
    }

    private class GetGamesTask extends AsyncTask<String, Void, String> {
    	private ProgressDialog p = new ProgressDialog(GamesActivity.this);
    	
    	@Override
    	protected void onPreExecute() {
			p.setCancelable(false);
			p.setCanceledOnTouchOutside(false);
			p.setMessage("Getting games...");
			p.show();
		}
		
		@Override
		protected String doInBackground(String... s) {
			try {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(get_url);
				get.setHeader("Accept", "application/json");
				HttpResponse response = client.execute(get);
				HttpEntity entity = response.getEntity();
				
				StringBuilder sb = new StringBuilder();
				BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
				String str = null;
				while ((str = br.readLine()) != null) sb.append(str);
				return sb.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPostExecute(String s) {
			p.dismiss();
			Toast.makeText(getApplicationContext(), "Games loaded.", Toast.LENGTH_SHORT).show();
		}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.games, menu);
        return true;
    }
    
}
