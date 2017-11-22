package com.gts.toc.view;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.gts.toc.R;


/**
 * Created by warsono on 11/12/16.
 */

public class Dialog {

    public static void AboutDialog(final Context mContext, String Description, String Version){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View alertView    = inflater.inflate(R.layout.dialog_about, null, true);
        TextView mDescription   = (TextView) alertView.findViewById(R.id.description);
        mDescription.setText(Description);
        TextView mVersion   = (TextView) alertView.findViewById(R.id.versionDesc);
        mVersion.setText(Version);
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(alertView);
        builder.setCancelable(false);
        builder.setPositiveButton(mContext.getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                builder.setCancelable(true);
            }
        });
        builder.show();
    }

    public static void InformationDialog(final Context mContext, String Message){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View alertView    = inflater.inflate(R.layout.dialog_information, null, true);
        TextView mMessage       = (TextView) alertView.findViewById(R.id.dialog_message);
        mMessage.setText(Message);
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(alertView);
        builder.setCancelable(false);
        builder.setPositiveButton(mContext.getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                builder.setCancelable(true);
            }
        });
        builder.show();
    }

    public static void ActionDialog(final Context mContext, String Message, final Runnable mAction){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View alertView    = inflater.inflate(R.layout.dialog_information, null, true);
        TextView mMessage       = (TextView) alertView.findViewById(R.id.dialog_message);
        mMessage.setText(Message);
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(alertView);
        builder.setCancelable(false);
        builder.setPositiveButton(mContext.getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (mAction != null)
                    mAction.run();
            }
        });
        builder.show();
    }

    public static void ConfirmationDialog(final Context mContext, String TextInput,
                                          String PositiveButton, final Runnable mPositiveAction,
                                          String NegativeButton){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View alertView    = inflater.inflate(R.layout.dialog_information, null, true);
        TextView mText          = (TextView) alertView.findViewById(R.id.dialog_message);
        mText.setText(TextInput);

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(alertView);
        builder.setCancelable(false);
        builder.setPositiveButton(PositiveButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (mPositiveAction != null)
                    mPositiveAction.run();
            }
        });
        builder.setNegativeButton(NegativeButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                builder.setCancelable(true);
            }
        });
        builder.show();
    }

    public static void ApprovalDialog(final Context mContext, String TextInput,
                                          String PositiveButton, final Runnable mPositiveAction,
                                          String NegativeButton, final Runnable mNegativeAction){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View alertView    = inflater.inflate(R.layout.dialog_information, null, true);
        TextView mText          = (TextView) alertView.findViewById(R.id.dialog_message);
        mText.setText(TextInput);

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(alertView);
        builder.setCancelable(true);
        builder.setPositiveButton(PositiveButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                builder.setCancelable(true);
                if (mPositiveAction != null)
                    mPositiveAction.run();
            }
        });
        builder.setNegativeButton(NegativeButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                builder.setCancelable(true);
                if (mNegativeAction != null)
                    mNegativeAction.run();
            }
        });
        builder.show();
    }
}
