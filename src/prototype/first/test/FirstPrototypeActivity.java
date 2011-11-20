package prototype.first.test;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class FirstPrototypeActivity extends Activity {

	/** Members */
	private ListView stagesView;
	private ArrayList<HashMap<String, String>> stageList;
	private SimpleAdapter adapter;
	private int cursor = -1;
	private View addNew;

	private SharedPreferences savedStages;
	private SharedPreferences.Editor editor;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stagescreen);

		stagesView = (ListView) findViewById(R.id.stagelist);
	}

	public void onPause() {
		super.onPause();
		saveStageList();
	}
	
	public void onResume() {
		super.onResume();
		reBuildStageList();
	}

	/** Utilities */
	private void reBuildStageList() {
		savedStages = this.getSharedPreferences("ListOfAllStages",
				Context.MODE_PRIVATE);
		String[] packStage = savedStages.getString("Stages",
				"").split("!");
		stageList = new ArrayList<HashMap<String, String>>();
		
		Log.e("Size","pack size = "+packStage.length);
		for (int i = 0; i < packStage.length; i++) {
			HashMap<String, String> item = new HashMap<String, String>();
			SharedPreferences stageInfo = this.getSharedPreferences(
					packStage[i], Context.MODE_PRIVATE);
			item.put("Code_ID", packStage[i]);
			item.put("Name", stageInfo.getString("Name", "Empty"));
			item.put("Description",
					stageInfo.getString("Description", "Empty stage"));

			stageList.add(item);
		}
		
		adapter = new SimpleAdapter(this, stageList,
				android.R.layout.simple_list_item_2, new String[] { "Name",
						"Description" }, new int[] { android.R.id.text1,
						android.R.id.text2 });

		stagesView.setAdapter(adapter);

		stagesView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				cursor = arg2;
				FirstPrototypeActivity.this.setTitle("Selected Stage "
						+ stageList.get(cursor).get("Name"));
			}

		});

	}

	private void saveStageList() {
		// store list
		String write = "";
		savedStages = this.getSharedPreferences("ListOfAllStages",
				Context.MODE_PRIVATE);
		editor = savedStages.edit();
		for (int i = 0; i < stageList.size(); i++) {
			write = write + stageList.get(i).get("Code_ID") + "!";
		}
		Log.e("List", write);
		editor.putString("Stages", write);
		editor.commit();
	}

	/** Menu Control */

	/** Button onClick listeners */
	public void playClicked(View view) {
		if (cursor < 0) {
			Toast.makeText(this, "What Stage to play ?", Toast.LENGTH_SHORT)
					.show();
		} else {
			ContainerBox.modifyable = false;
			ContainerBox.playingStageID = stageList.get(cursor).get("Code_ID");
			Intent stage = new Intent();
			stage.setClass(this, MapMode.class);
			startActivity(stage);
		}
		cursor = -1;
	}

	public void addClicked(View view) {
		LayoutInflater flat = this.getLayoutInflater();
		addNew = flat.inflate(R.layout.addnew, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Create Stage :");
		builder.setView(addNew);

		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}

				});

		builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				EditText nameField = (EditText) addNew
						.findViewById(R.id.name_ip);
				String name = nameField.getText().toString();
				if (!name.contentEquals("")) {
					String codeId = name + (int)(Math.random()*2147483647);
					SharedPreferences stageInfo = FirstPrototypeActivity.this
							.getSharedPreferences(codeId, Context.MODE_PRIVATE);
					// Check empty input
					// save to preference
					editor = stageInfo.edit();
					editor.putString("Name", name);
					editor.putString("Description", "");
					editor.commit();

					// update list
					HashMap<String, String> item = new HashMap<String, String>();
					item.put("Code_ID", codeId);
					item.put("Name", name);
					item.put("Description", "");
					stageList.add(item);
					adapter.notifyDataSetChanged();

				}

			}
		});
		builder.show();
		cursor = -1;
	}

	public void deleteClicked(View view) {
		if (cursor < 0) {
			Toast.makeText(this, "Select a stage first", Toast.LENGTH_SHORT)
					.show();
		} else {
			String target = stageList.get(cursor).get("Code_ID");
			editor = this.getSharedPreferences(target, Context.MODE_PRIVATE)
					.edit();
			editor.clear();
			editor.commit();
			stageList.remove(cursor);
			adapter.notifyDataSetChanged();
		}
		cursor = -1;
	}

	public void modifyClicked(View view) {
		if (cursor < 0) {
			Toast.makeText(this, "What Stage to modify ?", Toast.LENGTH_SHORT)
					.show();
		} else {
			ContainerBox.modifyable = true;
			ContainerBox.playingStageID = stageList.get(cursor).get("Code_ID");
			Intent stage = new Intent();
			stage.setClass(this, MapMode.class);
			startActivity(stage);
		}
		cursor = -1;
	}
}