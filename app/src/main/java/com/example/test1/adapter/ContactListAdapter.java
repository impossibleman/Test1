package com.example.test1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.test1.R;
import com.example.test1.dataconstruct.CellContact;

import java.util.ArrayList;

public class ContactListAdapter extends ArrayAdapter<CellContact> {

    public ContactListAdapter(Context context, int resource, ArrayList<CellContact> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.contact_item, null);
        }
        CellContact sigleContact = (CellContact) getItem(position);
        TextView tvName = convertView.findViewById(R.id.tv_name);
        TextView tvPhoneNumber = convertView.findViewById(R.id.tv_phone_number);
        tvName.setText(sigleContact.getName());
        tvPhoneNumber.setText(sigleContact.getPhoneNumber());
        return convertView;
    }
}
