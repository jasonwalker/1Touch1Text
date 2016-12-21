package onemessage.jmw.com.onemessage;


import android.app.DialogFragment;
import android.os.Bundle;

public class PopupDialogFragment extends DialogFragment {

    @Override
    public android.app.Dialog onCreateDialog(Bundle saveInstanceState) {
        final android.app.Dialog dialog = super.onCreateDialog(saveInstanceState);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogSlide;
        return dialog;
    }

}
