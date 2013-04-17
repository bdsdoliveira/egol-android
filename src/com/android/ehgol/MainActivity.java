package com.android.ehgol;

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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;
import com.android.ehgol.R;

public class MainActivity extends Activity implements OnQueryTextListener {
	private final String GET_URL = "http://192.168.0.12:3000/games";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		GetGamesTask getGames = new GetGamesTask();
		getGames.execute();
    }
    
    
    private class GetGamesTask extends AsyncTask<String, Void, Void> {
    	private ProgressDialog p = new ProgressDialog(MainActivity.this);
		StringBuilder sb = new StringBuilder();
		JSONArray array;
		int status;
		
    	@Override
    	protected void onPreExecute() {
			p.setCancelable(false);
			p.setCanceledOnTouchOutside(false);
			p.setMessage("Getting games...");
			p.show();
		}
		
		@Override
		protected Void doInBackground(String... s) {
			try {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(GET_URL);
				get.setHeader("Accept", "application/json");
//				HttpConnectionParams.setConnectionTimeout(client.getParams(), 5000);
//				HttpConnectionParams.setSoTimeout(client.getParams(), 10000);
				HttpResponse response = client.execute(get);
				HttpEntity entity = response.getEntity();
				status = response.getStatusLine().getStatusCode();
				
				if (status == 200) {
					BufferedReader b = new BufferedReader(new InputStreamReader(entity.getContent()));
					String str = null;
					while ((str = b.readLine()) != null) sb.append(str);
					array = new JSONArray(sb.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPostExecute(Void v) {
			p.dismiss();

			if (array != null) {
		        ListView games_list = (ListView) findViewById(R.id.games_list);
				games_list.setAdapter(new GamesListAdapter(array, MainActivity.this));
				Toast.makeText(getApplicationContext(), "Games loaded.", Toast.LENGTH_SHORT).show();
			} else Toast.makeText(getApplicationContext(), "Failed: error " + status, Toast.LENGTH_SHORT).show();
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
			try {
				View v = view;
				JSONObject object = json.getJSONObject(i);
				String team1 = object.getString("team1").toString();
				String team2 = object.getString("team2").toString();
				String city = object.getString("city").toString();
				String stadium = object.getString("stadium").toString();
				
				if (v == null) {
					LayoutInflater l = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					v = l.inflate(R.layout.list_item, null);
				}
	
				TextView tT = (TextView) v.findViewById(R.id.list_item_teams);
				TextView tP = (TextView) v.findViewById(R.id.list_item_place);
				
				tT.setText(team1 + " × " + team2);
				tP.setText("\nLocal: " + city + ", " + stadium);
				
				return v;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.games, menu);

		SearchView s = (SearchView) menu.findItem(R.id.action_search).getActionView();
        s.setOnQueryTextListener(this);
        
        return true;
    }

    public boolean onQueryTextChange(String s) {
        s = s.isEmpty() ? "" : "Query so far: " + s;
        TextView t = (TextView) findViewById(R.id.games_search);
        t.setText(s);
        return true;
    }
 
    public boolean onQueryTextSubmit (String s) {
    	TextView t = (TextView) findViewById(R.id.games_search);
    	t.setText("Searching for: " + s + "...");
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.action_reload:
    		GetGamesTask getGames = new GetGamesTask();
    		getGames.execute();
    		break;
    	case R.id.action_settings:
    		Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
    		break;
    	default:
    		break;
    	}
    	return true;
    } 
    
}
