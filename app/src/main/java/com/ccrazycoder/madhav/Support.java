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
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Support extends AppCompatActivity {

    private static final String TAG = "Madhav";
    static boolean isInitialized = false;
    ArrayList<Contact> cArr = new ArrayList<Contact>();
    private Typeface myTypeface;
    private ListView lstView;
    private MySimpleArrayAdapter mAdapter;
    private TextView txtError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        lstView = (ListView) findViewById(R.id.lstContacts);
        txtError = (TextView) findViewById(R.id.txtError);
        txtError.setVisibility(View.GONE);

        mAdapter = new MySimpleArrayAdapter(this);
        lstView.setAdapter(mAdapter);

        if (Constants.isNetworkAvailable(Support.this)) {
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

            DatabaseReference myRef = database.getReference("support");
            myRef.keepSynced(true);

            cArr.clear();
            myRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Contact value = null;
                    try {
                        Log.e(TAG, dataSnapshot.getKey());
                        value = dataSnapshot.getValue(Contact.class);
                        value.Sr = dataSnapshot.getKey();
                        if (value.Name.trim().length() > 0) {
                            addContacts(value);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } else {
            lstView.setVisibility(View.GONE);
            txtError.setVisibility(View.VISIBLE);
        }
        myTypeface = Typeface.createFromAsset(
                this.getAssets(),
                "fonts/Roboto-Regular.ttf");
        txtError.setTypeface(myTypeface);
    }

    private void addContacts(Contact objContact) {
        cArr.add(objContact);
    }

    public class MySimpleArrayAdapter extends BaseAdapter {
        private final Context context;
        MySimpleArrayAdapter.ViewHolder holder;

        public MySimpleArrayAdapter(Context context) {
            this.context = context;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View vi = convertView;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                vi = inflater.inflate(R.layout.row_support, null);

                holder = new ViewHolder();
                holder.flHeader = (FrameLayout) vi.findViewById(R.id.flHeader);
                holder.index = (TextView) vi.findViewById(R.id.txtIndex);
                holder.name = (TextView) vi.findViewById(R.id.txtName);
                holder.block = (TextView) vi.findViewById(R.id.txtBlock);
                holder.cellone = (TextView) vi.findViewById(R.id.txtMobile);
                vi.setTag(holder);
            } else
                holder = (MySimpleArrayAdapter.ViewHolder) vi.getTag();


            if (position > 0) {
                if (cArr.get(position).Block.equalsIgnoreCase(cArr.get(position - 1).Block)) {
                    holder.flHeader.setVisibility(View.GONE);
                } else {
                    holder.flHeader.setVisibility(View.VISIBLE);
                    holder.block.setText(cArr.get(position).Block);
                }
            } else {
                holder.flHeader.setVisibility(View.VISIBLE);
                holder.block.setText(cArr.get(position).Block);
            }

            holder.index.setTypeface(myTypeface);
            holder.name.setTypeface(myTypeface);
            holder.block.setTypeface(myTypeface);
            holder.cellone.setTypeface(myTypeface);

            holder.name.setText(cArr.get(position).Name);
            if (cArr.get(position).Number.length() > 0) {
                holder.index.setText(cArr.get(position).Number);
            } else {
                holder.index.setVisibility(View.GONE);
            }

            if (cArr.get(position).CellOne != null && cArr.get(position).CellOne.length() > 0) {
                holder.cellone.setVisibility(View.VISIBLE);
                holder.cellone.setText(cArr.get(position).CellOne);
            } else {
                holder.cellone.setVisibility(View.GONE);
            }

            holder.cellone.setTag(cArr.get(position).CellOne);
            holder.cellone.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(final View v) {

                    final CharSequence[] items = {"Call", "SMS"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(Support.this);
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

        public int getCount() {
            return cArr.size();
        }

        public Object getItem(int position) {
            return cArr.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {
            public FrameLayout flHeader;
            public TextView index;
            public TextView name;
            public TextView block;
            public TextView cellone;
        }
    }
}
