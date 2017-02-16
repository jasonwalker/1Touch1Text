package onemessage.jmw.com.onemessage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jasonw on 2/5/17.
 */

public class ChooseInsertDialog extends PopupDialogFragment {
    private Context context;
    private EditText editText;

    public ChooseInsertDialog() {
    }

    public static ChooseInsertDialog newInstance(EditText editText) {
        ChooseInsertDialog popup = new ChooseInsertDialog();
        popup.editText = editText;
        return popup;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Context context = getActivity();
        super.onCreateView(inflater, container, savedInstanceState);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams")
        final View dialogLayout = layoutInflater.inflate(R.layout.items_popup, null);

        Button cancelInsertButton = (Button) dialogLayout.findViewById(R.id.cancel_insert_button);
        cancelInsertButton.setOnTouchListener(new ChooseInsertDialog.OnCancelClickListener());

        ListView insertList = (ListView) dialogLayout.findViewById(R.id.insert_list);
        final ArrayList<InsertEntry> allInserts = new ArrayList<>();

        allInserts.add(new InsertEntry("Google map of location", MainActivity.LOCATION_MAP));
        allInserts.add(new InsertEntry("Latitude (decimal)", MainActivity.LATITUDE));
        allInserts.add(new InsertEntry("Longitude (decimal)", MainActivity.LONGITUDE));
        allInserts.add(new InsertEntry("Bearing (decimal)", MainActivity.BEARING));
        allInserts.add(new InsertEntry("Cardinal Direction", MainActivity.CARDINAL));
        allInserts.add(new InsertEntry("Speed miles per hour", MainActivity.SPEED_MPH));
        allInserts.add(new InsertEntry("Speed feet per second", MainActivity.SPEED_FPS));
        allInserts.add(new InsertEntry("Speed meters per second", MainActivity.SPEED_MPS));
        allInserts.add(new InsertEntry("Speed kilometers per hour", MainActivity.SPEED_KPH));
        allInserts.add(new InsertEntry("Altitude meters", MainActivity.ALTITUDE_M));
        allInserts.add(new InsertEntry("Altitude feet", MainActivity.ALTITUDE_F));
        allInserts.add(new InsertEntry("GPS dump (imperial units", MainActivity.GPS_DUMP));

        InsertAdapter adapter = new InsertAdapter(context, allInserts);
        insertList.setAdapter(adapter);
        insertList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                if (context instanceof IDataReceive) {
                    editText.getText().insert(editText.getSelectionStart(), allInserts.get(arg2).getValue());
                }
                dismiss();
            }
        });
        return dialogLayout;
    }
    private class OnCancelClickListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                dismiss();
            }
            return false;
        }
    }
}
