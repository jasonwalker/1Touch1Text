package onemessage.jmw.com.onemessage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jasonw on 2/5/17.
 */

public class InsertAdapter extends ArrayAdapter<InsertEntry> {

        InsertAdapter(Context context, ArrayList<InsertEntry> inserts) {
        super(context, R.layout.item_entry, inserts);
        }

@Override
public View getView(int position, View convertView, ViewGroup parent) {
        InsertEntry entry = getItem(position);
        if (convertView == null) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_entry, parent, false);
        }
        TextView description = (TextView) convertView.findViewById(R.id.item_description);
        TextView value = (TextView) convertView.findViewById(R.id.item_value);
        if (entry != null) {
            description.setText(entry.getDescription());
            value.setText(entry.getValue());
        }

        return convertView;
        }
}
