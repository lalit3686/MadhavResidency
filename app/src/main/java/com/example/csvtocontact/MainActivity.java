package com.example.csvtocontact;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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

import java.util.ArrayList;

public class MainActivity extends ListActivity {
    private static final String TAG = "Madhav";
    ArrayList<Contact> cArr = new ArrayList<Contact>();
    private Typeface myTypeface;
    private ListView lstView;
    private MySimpleArrayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lstView = (ListView) findViewById(android.R.id.list);

        mAdapter = new MySimpleArrayAdapter(this);
        lstView.setAdapter(mAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        DatabaseReference myRef = database.getReference("members");
        myRef.keepSynced(true);
        myTypeface = Typeface.createFromAsset(
                this.getAssets(),
                "fonts/Roboto-Regular.ttf");

        cArr.clear();
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Contact value = null;
                try {
                    Log.e(TAG, dataSnapshot.getKey());
                    value = dataSnapshot.getValue(Contact.class);
                    addContacts(value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Contact value = dataSnapshot.getValue(Contact.class);
                cArr.set(Integer.parseInt(dataSnapshot.getKey()) - 1, value);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Contact value = dataSnapshot.getValue(Contact.class);
                cArr.remove(Integer.parseInt(dataSnapshot.getKey()) - 1);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void addContacts(Contact objContact) {
        cArr.add(objContact);
    }

    public class MySimpleArrayAdapter extends BaseAdapter {
        private final Context context;
        ViewHolder holder;

        public MySimpleArrayAdapter(Context context) {
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View vi = convertView;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                vi = inflater.inflate(R.layout.row_list, null);

                holder = new ViewHolder();
                holder.name = (TextView) vi.findViewById(R.id.txtName);
                holder.block = (TextView) vi.findViewById(R.id.txtBlock);
                holder.cellone = (TextView) vi.findViewById(R.id.txtMobile);
                holder.celltwo = (TextView) vi.findViewById(R.id.txtCell);
                vi.setTag(holder);
            } else
                holder = (ViewHolder) vi.getTag();


            holder.name.setTypeface(myTypeface);
            holder.block.setTypeface(myTypeface);
            holder.cellone.setTypeface(myTypeface);
            holder.celltwo.setTypeface(myTypeface);

            holder.name.setText(cArr.get(position).Name);
            holder.block.setText(cArr.get(position).Block + " - " + cArr.get(position).Number);

            if (cArr.get(position).CellOne != null && cArr.get(position).CellOne.length() > 0) {
                holder.cellone.setText(cArr.get(position).CellOne);
            } else {
                holder.cellone.setVisibility(View.GONE);
            }

            if (cArr.get(position).CellTwo != null && cArr.get(position).CellTwo.length() > 0) {
                holder.celltwo.setText(cArr.get(position).CellTwo);
            } else {
                holder.celltwo.setVisibility(View.GONE);
            }

            holder.cellone.setTag(cArr.get(position).CellOne);
            holder.cellone.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + (String) v.getTag()));
                    startActivity(intent);
                }
            });

            holder.celltwo.setTag(cArr.get(position).CellTwo);
            holder.celltwo.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + (String) v.getTag()));
                    startActivity(intent);
                }
            });
            return vi;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getCount() {
            return cArr.size();
        }

        public class ViewHolder {
            public TextView name;
            public TextView block;
            public TextView cellone;
            public TextView celltwo;
        }
    }
}
