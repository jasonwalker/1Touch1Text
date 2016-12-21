package onemessage.jmw.com.onemessage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;


public class Dialog {
    public static AlertDialog showOK(Context context, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg)
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogSlide;
        dialog.show();
        return dialog;
    }

    public static AlertDialog showOK(Context context, String msg, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg)
                .setPositiveButton(context.getString(R.string.ok), listener);
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogSlide;
        dialog.show();
        return dialog;
    }


    public static AlertDialog showYesNo(Context context, String msg, DialogInterface.OnClickListener onYes,
                                        DialogInterface.OnClickListener onNo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg)
                .setPositiveButton(context.getString(R.string.yes), onYes)
                .setNegativeButton(context.getString(R.string.no), onNo);

        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogSlide;
        dialog.show();
        return dialog;
    }

}
