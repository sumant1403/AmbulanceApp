package com.homestudy.demomap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;


public class CustomDialog implements DialogInterface {

    private static boolean isShowingDialog = false;
    private Context mContext;
    private CustomDialogClickListener mCustomDialogClickListener;
    private int mRequestCode;

    public CustomDialog(Context context, int code) {
        mContext = context;
        mRequestCode = code;
    }


    public void showCustomDialog(String title, String msg, String positiveBtnTitle, String negativeBtnTitle, final CustomDialogClickListener dialogClickListener) {
        if (!isShowingDialog) {
            mCustomDialogClickListener = dialogClickListener;
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(msg)
                    .setCancelable(false)
                    .setTitle(title);

            if (negativeBtnTitle != null && !negativeBtnTitle.isEmpty()) {
                builder.setNegativeButton(negativeBtnTitle, setNegativeButtonClickedListener1());
            }
            builder.setPositiveButton(positiveBtnTitle, setPositiveButtonClickedListener1());
            builder.show();
            isShowingDialog = true;
        }
    }


    private OnClickListener setPositiveButtonClickedListener1() {
        return new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mCustomDialogClickListener.onPositiveButtonClicked(dialog, mRequestCode);
                isShowingDialog = false;
            }
        };
    }

    private OnClickListener setNegativeButtonClickedListener1() {

        return new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mCustomDialogClickListener.onNegativeButtonClicked(dialog, mRequestCode);
                isShowingDialog = false;
            }
        };
    }


    @Override
    public void cancel() {
        //Do nothing
        isShowingDialog = false;
    }

    @Override
    public void dismiss() {
        //Do nothing
        isShowingDialog = false;
    }


    public interface CustomDialogClickListener {
        public abstract void onPositiveButtonClicked(DialogInterface dialog, int requestCode);

        public abstract void onNegativeButtonClicked(DialogInterface dialog, int requestCode);


    }


}
