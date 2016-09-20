package com.ccrazycoder.madhav;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Madhav";
    static boolean isInitialized = false;
    ArrayList<Contact> cArr = new ArrayList<Contact>();
    private Typeface myTypeface;
    private ListView lstView;
    private MySimpleArrayAdapter mAdapter;
    private EditText edtSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        lstView = (ListView) findViewById(R.id.lstContacts);

        mAdapter = new MySimpleArrayAdapter(this);
        lstView.setAdapter(mAdapter);

        edtSearch = (EditText) findViewById(R.id.edtSearch);
        edtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }
        });
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
        private List<Contact> originalData = null;
        private List<Contact> filteredData = null;
        private ItemFilter mFilter = new ItemFilter();
        public MySimpleArrayAdapter(Context context) {
            this.context = context;
            this.filteredData = cArr;
            this.originalData = cArr;
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


            if (position % 2 == 0) {
                vi.setBackgroundColor(Color.LTGRAY);
            } else {
                vi.setBackgroundColor(Color.WHITE);
            }
            holder.name.setTypeface(myTypeface);
            holder.block.setTypeface(myTypeface);
            holder.cellone.setTypeface(myTypeface);
            holder.celltwo.setTypeface(myTypeface);

            holder.name.setText(filteredData.get(position).Name);
            holder.block.setText(filteredData.get(position).Block + " - " + filteredData.get(position).Number);

            if (filteredData.get(position).CellOne != null && filteredData.get(position).CellOne.length() > 0) {
                holder.cellone.setVisibility(View.VISIBLE);
                holder.cellone.setText(filteredData.get(position).CellOne);
            } else {
                holder.cellone.setVisibility(View.GONE);
            }

            if (filteredData.get(position).CellTwo != null && filteredData.get(position).CellTwo.length() > 0) {
                holder.celltwo.setVisibility(View.VISIBLE);
                holder.celltwo.setText(filteredData.get(position).CellTwo);
            } else {
                holder.celltwo.setVisibility(View.GONE);
            }

            if (filteredData.get(position).VehicleOne != null && filteredData.get(position).VehicleOne.length() > 0) {
                holder.vehicleone.setVisibility(View.VISIBLE);
                holder.vehicleone.setText(filteredData.get(position).VehicleOne);
            } else {
                holder.vehicleone.setVisibility(View.GONE);
            }

            if (filteredData.get(position).VehicleTwo != null && filteredData.get(position).VehicleTwo.length() > 0) {
                holder.vehicletwo.setVisibility(View.VISIBLE);
                holder.vehicletwo.setText(filteredData.get(position).VehicleTwo);
            } else {
                holder.vehicletwo.setVisibility(View.GONE);
            }

            if (filteredData.get(position).CarOne != null && filteredData.get(position).CarOne.length() > 0) {
                holder.carone.setVisibility(View.VISIBLE);
                holder.carone.setText(filteredData.get(position).CarOne);
            } else {
                holder.carone.setVisibility(View.GONE);
            }

            if (filteredData.get(position).CarTwo != null && filteredData.get(position).CarTwo.length() > 0) {
                holder.cartwo.setVisibility(View.VISIBLE);
                holder.cartwo.setText(filteredData.get(position).CarTwo);
            } else {
                holder.cartwo.setVisibility(View.GONE);
            }

            holder.cellone.setTag(filteredData.get(position).CellOne);
            holder.cellone.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(final View v) {

                    final CharSequence[] items = {"Call", "SMS"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(filteredData.get(position).Name);

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

            holder.celltwo.setTag(filteredData.get(position).CellTwo);
            holder.celltwo.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(final View v) {
                    final CharSequence[] items = {"Call", "SMS"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(filteredData.get(position).Name);

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

        public Filter getFilter() {
            return mFilter;
        }

        public int getCount() {
            return filteredData.size();
        }

        public Object getItem(int position) {
            return filteredData.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        private class ItemFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String filterString = constraint.toString().toLowerCase();

                FilterResults results = new FilterResults();

                final List<Contact> list = cArr;

                int count = list.size();
                final ArrayList<Contact> nlist = new ArrayList<Contact>(count);

                Contact filterableString;

                for (int i = 0; i < count; i++) {
                    filterableString = list.get(i);
                    if (filterableString.Name.toLowerCase().contains(filterString)) {
                        nlist.add(filterableString);
                    } else {
                        filterString = filterString.replaceAll("\\s", "");
                        if ((filterableString.Block.toLowerCase() + filterableString.Number.toLowerCase()).contains(filterString)) {
                            nlist.add(filterableString);
                        } else if (filterableString.VehicleOne.toLowerCase().contains(filterString) || filterableString.VehicleTwo.toLowerCase().contains(filterString) || filterableString.CarOne.toLowerCase().contains(filterString) || filterableString.CarTwo.toLowerCase().contains(filterString)) {
                            nlist.add(filterableString);
                        }
                    }
                }

                results.values = nlist;
                results.count = nlist.size();

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredData = (ArrayList<Contact>) results.values;
                notifyDataSetChanged();
            }

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
