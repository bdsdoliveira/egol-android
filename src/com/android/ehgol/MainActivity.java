package com.android.ehgol;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnQueryTextListener {
//	private final String GET_URL = "http://192.168.0.12:3000/games";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		GetGamesTask getGames = new GetGamesTask();
		getGames.execute();
    }
    
    
    public class GetGamesTask extends AsyncTask<String, Void, Void> {
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
				// Set up object for HTTP request
				HttpClient client = new DefaultHttpClient();
				/* Temporary code for offline testing */
				String GET_URL = "";
				/* ********************************** */
				HttpGet get = new HttpGet(GET_URL);
				
				// Set options for the request 
				get.setHeader("Accept", "application/json");
				HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);
				HttpConnectionParams.setSoTimeout(client.getParams(), 20000);
				
				// Execute and save response and status 
				HttpResponse response = client.execute(get);
				status = response.getStatusLine().getStatusCode();
				
				// Only set the array if status is OK (200)
				if (status == 200) {
					BufferedReader b = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
					String aux = null;
					while ((aux = b.readLine()) != null) sb.append(aux);
					array = new JSONArray(sb.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPostExecute(Void v) {
			p.dismiss();
			
			/* Temporary code for offline testing */
			try {
				array = new JSONArray("[{\"city\":\"São Paulo\",\"group\":\"Verde\",\"id\":1,\"stadium\":\"Arena de São Paulo\",\"team1\":\"A1\",\"team2\":\"A2\"},{\"city\":\"Rio de Janeiro\",\"group\":\"Verde\",\"id\":8,\"stadium\":\"Estádio do Maracanã\",\"team1\":\"A3\",\"team2\":\"A4\"},{\"city\":\"Salvador\",\"group\":\"Azul\",\"id\":9,\"stadium\":\"Estádio BLA\",\"team1\":\"B1\",\"team2\":\"B2\"}]");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			/* ********************************** */
			
			if (array != null) {
		        ListView games_list = (ListView) findViewById(R.id.games_list);
				games_list.setAdapter(new GamesListAdapter(array, MainActivity.this));
				Toast.makeText(getApplicationContext(), "Games loaded.", Toast.LENGTH_SHORT).show();
			} else Toast.makeText(getApplicationContext(), "Failed: error " + status, Toast.LENGTH_SHORT).show();
		}
    }
    
    
    
    private class GamesListAdapter extends BaseAdapter {
    	private JSONArray array;
    	Context c;
    	
    	GamesListAdapter(JSONArray array, Context c) {
    		this.array = array;
    		this.c = c;
    	}
    	
		@Override
		public int getCount() {
			return array.length();
		}
		
		@Override
		public JSONObject getItem(int i) {
			try {
				return array.getJSONObject(i);
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
		public View getView(final int i, View view, ViewGroup parent) {
			View v = view;
			JSONObject o;
			String team1 = null;
			String team2 = null;
			String city = null;
			String stadium = null;
			try {
				o = getItem(i);
				team1 = o.getString("team1").toString();
				team2 = o.getString("team2").toString();
				city = o.getString("city").toString();
				stadium = o.getString("stadium").toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			if (v == null) {
				LayoutInflater l = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = l.inflate(R.layout.list_item, null);
			}

			TextView mTeams = (TextView) v.findViewById(R.id.list_item_teams);
			TextView mPlace = (TextView) v.findViewById(R.id.list_item_place);
			
			mTeams.setText(team1 + " × " + team2);
			mPlace.setText("\nLocal: " + city + ", " + stadium);
			
			v.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(c, GameActivity.class);
					intent.putExtra("JSON", GamesListAdapter.this.getItem(i).toString());
					c.startActivity(intent);
				}
			});
			
			return v;
		}
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

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
