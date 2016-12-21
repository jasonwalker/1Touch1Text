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
import android.widget.ListView;

import java.util.ArrayList;


public class ChooseNumberTypeDialog extends PopupDialogFragment {
    private Context context;
    private ArrayList<PhoneEntry> numbers;

    public ChooseNumberTypeDialog() {

    }

    public static ChooseNumberTypeDialog newInstance(ArrayList<PhoneEntry> numbers) {
        ChooseNumberTypeDialog popup = new ChooseNumberTypeDialog();
        Bundle args = new Bundle();
        args.putParcelableArrayList("numbers", numbers);
        popup.setArguments(args);
        return popup;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        numbers = getArguments().getParcelableArrayList("numbers");
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
        final View dialogLayout = layoutInflater.inflate(R.layout.numbers_popup, null);

        Button cancelNumberButton = (Button) dialogLayout.findViewById(R.id.cancel_number_button);
        cancelNumberButton.setOnTouchListener(new OnCancelClickListener());

        ListView numbersList = (ListView) dialogLayout.findViewById(R.id.phone_list);
        PhoneNumberAdapter adapter = new PhoneNumberAdapter(context, numbers);
        numbersList.setAdapter(adapter);
        numbersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                if (context instanceof IDataReceive) {
                    ((IDataReceive) getActivity()).setDataString(numbers.get(arg2).getNumber());
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