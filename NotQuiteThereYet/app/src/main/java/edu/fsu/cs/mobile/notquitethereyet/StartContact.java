package edu.fsu.cs.mobile.notquitethereyet;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by eric on 11/13/2016.
 */

public class StartContact extends Activity {

    private ListView lvContact;
    private ContactListAdapter adapter;
    private List<Contact> contactList;
    Cursor cursor;
    int radius;
    double lat;
    double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_contact);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            radius = bundle.getInt("radius");
            lat = bundle.getDouble("lat");
            lng = bundle.getDouble("lng");
        }
        lvContact = (ListView) findViewById(R.id.listview_contact);

        contactList = new ArrayList<>();

        String name;
        String number;

        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,  null, null, null);
        startManagingCursor(cursor);

        int id = 1;
        cursor.moveToFirst();
        do {
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            contactList.add(new Contact(id, name, number));
            id = id + 1;
        }while(cursor.moveToNext());
        Collections.sort(contactList);
        adapter = new ContactListAdapter(getApplicationContext(), contactList);
        lvContact.setAdapter(adapter);

        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Toast.makeText(getApplicationContext(), "Clicked contact id: " + view.getTag(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void OnStartClick(View v){
        List<String> phone = new ArrayList<String>();
        for(Contact C: contactList){
            if(C.checked){
                phone.add(C.getNumber());
            }
        }
        String[] contactNumbers = new String[phone.size()];
        contactNumbers = phone.toArray(contactNumbers);
        Intent intent = new Intent(this,LocationService.class);
        intent.putExtra(LocationService.CONTACT_LIST_PARAM,contactNumbers);
        intent.putExtra(LocationService.DISTANCE_PARAM,radius);
        intent.putExtra(LocationService.LATITUDE_PARAM,lat);
        intent.putExtra(LocationService.LONGITUDE_PARAM,lng);
        startService(intent);
    }
}

