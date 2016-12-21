package onemessage.jmw.com.onemessage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class PhoneNumberAdapter extends ArrayAdapter<PhoneEntry> {

    PhoneNumberAdapter(Context context, ArrayList<PhoneEntry> users) {
        super(context, R.layout.item_phone, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PhoneEntry entry = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_phone, parent, false);
        }
        TextView type = (TextView) convertView.findViewById(R.id.phoneType);
        TextView number = (TextView) convertView.findViewById(R.id.phoneNumber);
        if (entry != null) {
            type.setText(entry.getType());
            number.setText(entry.getNumber());
        }

        return convertView;
    }
}