package com.example.ehgol;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GamesActivity extends Activity {
	private String get_url = "http://192.168.0.12:3000/games";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView games_list = (ListView) findViewById(R.id.games_list);
        
        try {
			GetGamesTask getGames = new GetGamesTask();
			JSONArray json_array = new JSONArray(getGames.execute().get());
			games_list.setAdapter(new GamesListAdapter(json_array, this));
		} catch (Exception e) {
			e.printStackTrace();
		}
        
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
    
    
    
    public class GamesListAdapter extends BaseAdapter {
    	private JSONArray json;
    	Context c;
    	
    	GamesListAdapter(JSONArray json, Context c) {
    		this.json = json;
    		this.c = c;
    	}
    	
		@Override
		public int getCount() {
			return json.length();
		}
		
		@Override
		public JSONObject getItem(int i) {
			try {
				return json.getJSONObject(i);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		public long getItemId(int i) {
			return i;
		}
		
		@Override
		public View getView(int i, View view, ViewGroup parent) {
			View v = view;
			
			if (v == null) {
				LayoutInflater l = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = l.inflate(R.layout.list_item, null);
			}

			TextView t = (TextView) v.findViewById(R.id.list_item_text);
			try {
			t.setText(json.getJSONObject(i).getString("team1").toString() +
					" vs " +
					json.getJSONObject(i).getString("team2").toString() +
					"\nLocal: " +
					json.getJSONObject(i).getString("city").toString() +
					", " +
					json.getJSONObject(i).getString("stadium").toString());;
			} catch (Exception e) {
			}
			return v;
		}
    }
    
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.games, menu);
        return true;
    }
    
}
