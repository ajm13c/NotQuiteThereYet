package edu.fsu.cs.mobile.notquitethereyet;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by eric on 11/13/2016.
 */

public class ContactListAdapter extends BaseAdapter {

    private Context context;
    private List<Contact> contactList;

    public ContactListAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    @Override
    public int getCount() {
        return contactList.size();
    }

    @Override
    public Object getItem(int position) {
        return contactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(context, R.layout.item_contact_list, null);
        TextView name = (TextView) v.findViewById(R.id.Name);
        TextView number = (TextView) v.findViewById(R.id.Number);
        final CheckBox cb = (CheckBox) v.findViewById(R.id.checkBox);
        final int p = position;
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactList.get(p).checked = cb.isChecked();
            }
        });

        name.setText(contactList.get(position).getName());
        number.setText(contactList.get(position).getNumber());
        cb.setChecked(false);

        v.setTag(contactList.get(position).getId());

        return v;
    }
}
