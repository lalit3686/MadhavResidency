package com.example.csvtocontact;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class MainActivity extends ListActivity {
	private static final String TAG = "Madhav";
	ArrayList<MyContact> cArr = new ArrayList<MyContact>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		boolean flg = readCSV("");

		((ListView) findViewById(android.R.id.list))
				.setAdapter(new MySimpleArrayAdapter(this));

		FirebaseDatabase database = FirebaseDatabase.getInstance();
		FirebaseDatabase.getInstance().setPersistenceEnabled(true);

		DatabaseReference myRef = database.getReference("members");
		myRef.keepSynced(true);


		myRef.addChildEventListener(new ChildEventListener() {
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String s) {
				Contact value = dataSnapshot.getValue(Contact.class);
				Log.d(TAG, "Value is: " + value.Name);
				Log.d(TAG, "Value is: " + value.Cell_1);
			}

			@Override
			public void onChildChanged(DataSnapshot dataSnapshot, String s) {
				Contact value = dataSnapshot.getValue(Contact.class);
				Log.d(TAG, "Value is: " + value.Name);
				Log.d(TAG, "Value is: " + value.Cell_1);
			}

			@Override
			public void onChildRemoved(DataSnapshot dataSnapshot) {
				Contact value = dataSnapshot.getValue(Contact.class);
				Log.d(TAG, "Value is: " + value.Name);
			}

			@Override
			public void onChildMoved(DataSnapshot dataSnapshot, String s) {
				Contact value = dataSnapshot.getValue(Contact.class);
				Log.d(TAG, "Value is: " + value.Name);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				Log.w(TAG, "Failed to read value.", databaseError.toException());
			}
		});
	}

	public class MySimpleArrayAdapter extends BaseAdapter {
		private final Context context;
		

		public MySimpleArrayAdapter(Context context) {
			this.context = context;
		}

		/********* Create a holder Class to contain inflated xml file elements *********/
		public class ViewHolder {

			public TextView name;
			public TextView mobile;
			public TextView cell;

		}

		ViewHolder holder;
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View vi = convertView;

			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				/****** Inflate tabitem.xml file for each row ( Defined below ) *******/
				vi = inflater.inflate(R.layout.row_list, null);

				/****** View Holder Object to contain tabitem.xml file elements ******/

				holder = new ViewHolder();
				holder.name = (TextView) vi.findViewById(R.id.txtName);
				holder.mobile = (TextView) vi.findViewById(R.id.txtMobile);
				holder.cell = (TextView) vi.findViewById(R.id.txtCell);

				/************ Set holder with LayoutInflater ************/
				vi.setTag(holder);
			} else
				holder = (ViewHolder) vi.getTag();

			holder.name.setText(cArr.get(position).name + "\n"
					+ cArr.get(position).block + " - "
					+ cArr.get(position).blockno);
			holder.mobile.setText(cArr.get(position).mobile);
			if(cArr.get(position).cell!= null && cArr.get(position).cell.length()>0){
				holder.cell.setText(cArr.get(position).cell);
			}
			
			holder.mobile.setTag(cArr.get(position).mobile);
			holder.mobile.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + (String)v.getTag()));
					startActivity(intent);
				}
			});
			
			holder.cell.setTag(cArr.get(position).cell);
			holder.cell.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + (String)v.getTag()));
					startActivity(intent);
				}
			});
			
			return vi;
		}

		
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}


		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return cArr.size();
		}
	}

	private boolean readCSV(String path) {
		cArr.clear();
		try {

			// create BufferedReader to read CSV file
			// BufferedInputStream bri = new
			// BufferedInputStream(getAssets().open(getAssets().list("")[0]));
			// AssetFileDescriptor descriptor =
			// getAssets().openFd("MRContacts.csv");
			// FileReader reader = new
			// FileReader(descriptor.getFileDescriptor());
			BufferedReader br = new BufferedReader(new InputStreamReader(
					getAssets().open("MRContacts.csv"), "UTF-8"));
			// BufferedReader br = new BufferedReader( new
			// FileReader(createFileFromInputStream(getAssets().open("MRContacts.csv"))));
			String record = "";
			StringTokenizer st = null;
			int rowNumber = 0;
			int cellIndex = 0;

			// read comma separated file line by line
			while ((record = br.readLine()) != null) {
				rowNumber++;
				MyContact c = new MyContact();
				// break comma separated line using ","
				st = new StringTokenizer(record, ",");
				while (st.hasMoreTokens()) {
					// display CSV values
					cellIndex++;
					String tmp = st.nextToken();
					if (cellIndex == 2) {
						c.name = tmp;
					} else if (cellIndex == 3) {
						c.block = tmp;
					} else if (cellIndex == 4) {
						c.blockno = tmp;
					} else if (cellIndex == 5) {
						c.mobile = tmp;
					} else if (cellIndex == 6) {
						c.cell = tmp;
					}
					System.out.println("Cell column index: " + cellIndex);
					System.out.println("Cell Value: " + tmp);
					
					System.out.println("---");
				}

				cArr.add(c);

				// reset cell Index number
				cellIndex = 0;

			}

			System.out.println("rowNumber: " + rowNumber);

		} catch (Exception e) {
			System.out.println("Exception while reading csv file: " + e);
		}
		return true;
	}
	public static class MyContact {
		public String name, block, blockno, mobile, cell;
	}
}
