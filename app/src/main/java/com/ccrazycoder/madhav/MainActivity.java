package com.ccrazycoder.madhav;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Madhav";
    static boolean isInitialized = false;
    ArrayList<Contact> cArr = new ArrayList<Contact>();
    private Typeface myTypeface;
    private ListView lstView;
    private MySimpleArrayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        lstView = (ListView) findViewById(R.id.lstContacts);

        mAdapter = new MySimpleArrayAdapter(this);
        lstView.setAdapter(mAdapter);


        try {
            if (!isInitialized) {
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                isInitialized = true;
            } else {
                Log.d(TAG, "Already Initialized");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();

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
        public View getView(final int position, View convertView, ViewGroup parent) {

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
                holder.vehicleone = (TextView) vi.findViewById(R.id.txtVehicleOne);
                holder.vehicletwo = (TextView) vi.findViewById(R.id.txtVehicleTwo);
                holder.carone = (TextView) vi.findViewById(R.id.txtCarOne);
                holder.cartwo = (TextView) vi.findViewById(R.id.txtCarTwo);
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
                holder.cellone.setVisibility(View.VISIBLE);
                holder.cellone.setText(cArr.get(position).CellOne);
            } else {
                holder.cellone.setVisibility(View.GONE);
            }

            if (cArr.get(position).CellTwo != null && cArr.get(position).CellTwo.length() > 0) {
                holder.celltwo.setVisibility(View.VISIBLE);
                holder.celltwo.setText(cArr.get(position).CellTwo);
            } else {
                holder.celltwo.setVisibility(View.GONE);
            }

            if (cArr.get(position).VehicleOne != null && cArr.get(position).VehicleOne.length() > 0) {
                holder.vehicleone.setVisibility(View.VISIBLE);
                holder.vehicleone.setText(cArr.get(position).VehicleOne);
            } else {
                holder.vehicleone.setVisibility(View.GONE);
            }

            if (cArr.get(position).VehicleTwo != null && cArr.get(position).VehicleTwo.length() > 0) {
                holder.vehicletwo.setVisibility(View.VISIBLE);
                holder.vehicletwo.setText(cArr.get(position).VehicleTwo);
            } else {
                holder.vehicletwo.setVisibility(View.GONE);
            }

            if (cArr.get(position).CarOne != null && cArr.get(position).CarOne.length() > 0) {
                holder.carone.setVisibility(View.VISIBLE);
                holder.carone.setText(cArr.get(position).CarOne);
            } else {
                holder.carone.setVisibility(View.GONE);
            }

            if (cArr.get(position).CarTwo != null && cArr.get(position).CarTwo.length() > 0) {
                holder.cartwo.setVisibility(View.VISIBLE);
                holder.cartwo.setText(cArr.get(position).CarTwo);
            } else {
                holder.cartwo.setVisibility(View.GONE);
            }

            holder.cellone.setTag(cArr.get(position).CellOne);
            holder.cellone.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(final View v) {

                    final CharSequence[] items = {"Call", "SMS"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(cArr.get(position).Name);

                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {

                            if (items[item].equals("Call")) {
                                // invoke call functionality
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + (String) v.getTag()));
                                startActivity(intent);
                            }

                            if (items[item].equals("SMS")) {
                                String number = ((String) v.getTag());  // The number on which you want to send SMS
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));
                                // invoke sms functionality
                            }
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                    alert.setCanceledOnTouchOutside(true);

                }
            });

            holder.celltwo.setTag(cArr.get(position).CellTwo);
            holder.celltwo.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(final View v) {
                    final CharSequence[] items = {"Call", "SMS"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(cArr.get(position).Name);

                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {

                            if (items[item].equals("Call")) {
                                // invoke call functionality
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + (String) v.getTag()));
                                startActivity(intent);
                            }

                            if (items[item].equals("SMS")) {
                                String number = ((String) v.getTag());  // The number on which you want to send SMS
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));
                                // invoke sms functionality
                            }
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                    alert.setCanceledOnTouchOutside(true);
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
            public TextView vehicleone;
            public TextView vehicletwo;
            public TextView carone;
            public TextView cartwo;

        }
    }
}
