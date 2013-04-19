package com.android.ehgol;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnQueryTextListener {
	private final String GET_URL = "http://192.168.0.12:3000/games";
	SearchView s;
	GamesListAdapter gamesListAdapter;
	
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
				array = new JSONArray("[{\"city\":\"São Paulo\",\"group\":\"Verde\",\"id\":1,\"stadium\":\"Arena de São Paulo\",\"team1\":\"A1\",\"team2\":\"A2\"}," +
						"{\"city\":\"Rio de Janeiro\",\"group\":\"Verde\",\"id\":8,\"stadium\":\"Estádio do Maracanã\",\"team1\":\"A3\",\"team2\":\"A4\"}," +
						"{\"city\":\"Salvador\",\"group\":\"Rosa\",\"id\":9,\"stadium\":\"Arena Fonte Nova\",\"team1\":\"B1\",\"team2\":\"B2\"}," +
						"{\"city\":\"Cuiabá\",\"group\":\"Rosa\",\"id\":11,\"stadium\":\"Arena Pantanal\",\"team1\":\"B3\",\"team2\":\"B4\"}," +
						"{\"city\":\"Belo Horizonte\",\"group\":\"Preto\",\"id\":12,\"stadium\":\"Estádio Mineirão\",\"team1\":\"C1\",\"team2\":\"C2\"}," +
						"{\"city\":\"Recife\",\"group\":\"Preto\",\"id\":13,\"stadium\":\"Arena Pernambuco\",\"team1\":\"C3\",\"team2\":\"C4\"}," +
						"{\"city\":\"Fortaleza\",\"group\":\"Azul\",\"id\":14,\"stadium\":\"Estádio Castelão\",\"team1\":\"D1\",\"team2\":\"D2\"}," +
						"{\"city\":\"Manaus\",\"group\":\"Azul\",\"id\":15,\"stadium\":\"Arena Amazônia\",\"team1\":\"D3\",\"team2\":\"D4\"}]");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			/* ********************************** */
			
			if (array != null) {
		        ListView games_list = (ListView) findViewById(R.id.games_list);
		        games_list.setTextFilterEnabled(true);
		        gamesListAdapter = new GamesListAdapter(array);
				games_list.setAdapter(gamesListAdapter);
				Toast.makeText(getApplicationContext(), "Games loaded.", Toast.LENGTH_SHORT).show();
			} else Toast.makeText(getApplicationContext(), "Failed: error " + status, Toast.LENGTH_SHORT).show();
		}
    }
    
    
    
    private class GamesListAdapter extends BaseAdapter {
    	private List<String> items = new ArrayList<String>();
    	private List<String> items_showing = new ArrayList<String>();
    	
    	GamesListAdapter(JSONArray array) {
    		for (int i = 0; i < array.length(); i++) {
    			try {
    				items.add("[" + array.getJSONObject(i).toString() + "]");
    				items_showing.add("[" + array.getJSONObject(i).toString() + "]");
    			} catch (JSONException e) {
    				e.printStackTrace();
    			}
    		}
    	}
    	
		@Override
		public int getCount() {
			return items_showing.size();
		}
		
		@Override
		public String getItem(int i) {
			return items_showing.get(i);
		}
		
		@Override
		public long getItemId(int i) {
			return i;
		}
		
		@Override
		public View getView(final int i, View view, ViewGroup parent) {
			View v = view;
			JSONObject o;
			JSONArray a;
			String team1 = null;
			String team2 = null;
			String city = null;
			String stadium = null;
			
			try {
				a = new JSONArray(getItem(i));
				o = a.getJSONObject(0);
				team1 = o.getString("team1").toString();
				team2 = o.getString("team2").toString();
				city = o.getString("city").toString();
				stadium = o.getString("stadium").toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			if (v == null) {
				LayoutInflater l = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = l.inflate(R.layout.list_item, null);
			}

			TextView mTeams = (TextView) v.findViewById(R.id.list_item_teams);
			TextView mCity = (TextView) v.findViewById(R.id.list_item_city);
			TextView mStadium = (TextView) v.findViewById(R.id.list_item_stadium);
			
			mTeams.setText(team1 + " × " + team2);
			mCity.setText("Cidade: " + city);
			mStadium.setText("Local: " + stadium);
			
			v.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(MainActivity.this, GameActivity.class);
					intent.putExtra("JSON", GamesListAdapter.this.getItem(i).toString());
					startActivity(intent);
				}
			});
			
			return v;
		}
		
		public void filterSearch(String s) {
	    	List<String> filtered = new ArrayList<String>();
			JSONObject o;
			JSONArray a;
			
			// Reset items to original values
			items_showing = items;

			// Search in each game inside its details
			for (int i = 0; i < this.items_showing.size(); i++) {
				try {
					a = new JSONArray(getItem(i));
					o = a.getJSONObject(0);
					if (o.getString("team1").toLowerCase().contains(s) ||
						o.getString("team2").toLowerCase().contains(s) ||
						o.getString("city").toLowerCase().contains(s) ||
						o.getString("stadium").toLowerCase().contains(s) ||
						o.getString("team1").toUpperCase().contains(s) ||
						o.getString("team2").toUpperCase().contains(s) ||
						o.getString("city").toUpperCase().contains(s) ||
						o.getString("stadium").toUpperCase().contains(s)) {
						filtered.add("[" + o.toString() + "]");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			this.items_showing = filtered;
			notifyDataSetChanged();
		}
		
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem menuItemSearch = menu.findItem(R.id.action_search);
        s = (SearchView) menuItemSearch.getActionView();
        menuItemSearch.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				return true;
			}
			
			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				s.setQuery("", true);
				return true;
			}
		});
        s.setQueryHint("Search for teams and places");
        s.setOnQueryTextListener(this);
        
        
        return true;
    }

    public boolean onQueryTextChange(String query) {
    	gamesListAdapter.filterSearch(query);
        return true;
    }
 
    public boolean onQueryTextSubmit (String query) {
        // Hide keyboard because search was already
    	// made in real time using onQueryTextChange
    	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(s.getWindowToken(), 0);
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
