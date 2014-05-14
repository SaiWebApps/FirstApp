package com.firstApp;

import java.util.Collections;
import java.util.Map;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.os.Build;

public class MainActivity extends ActionBarActivity {

	private DatabaseManager mgr = new DatabaseManager(MainActivity.this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	private void updateListView(AutoCompleteTextView tv) {
		ListView listView = (ListView) findViewById(R.id.posted_messages);

		mgr.open();
		Map<String, String> data = mgr.getAllPosts();
		mgr.close();

		int c = 0;
		int size = data.size();
		String[] result = new String[size];
		String[] values = new String[size];
		for (String key : data.keySet()) {
			result[size-c-1] = key + ": " + data.get(key);
			values[size-c-1] = data.get(key);
			c++;
		}

		ArrayAdapter<String> lvAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, result);
		listView.setAdapter(lvAdapter);
		tv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, values));
	}

	@Override
	protected void onResume() {
		super.onResume();
		Button submitButton = (Button) findViewById(R.id.submit_button);
		AutoCompleteTextView text = (AutoCompleteTextView)findViewById(R.id.textfield);

		submitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AutoCompleteTextView text = (AutoCompleteTextView)findViewById(R.id.textfield);
				String msg = text.getText().toString();
				mgr.open();
				mgr.createPost(msg);
				mgr.close();
				updateListView(text);
				text.setText("");
			}
		});
		
		updateListView(text);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
}