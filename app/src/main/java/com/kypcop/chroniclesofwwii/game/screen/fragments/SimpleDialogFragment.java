package com.kypcop.chroniclesofwwii.game.screen.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import static android.content.DialogInterface.OnClickListener;

public class SimpleDialogFragment extends DialogFragment {

    private final Dialog dialog;

    private SimpleDialogFragment(Builder builder){
        dialog = builder.createDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return dialog;
    }

    public static class Builder{


        private final AlertDialog.Builder dialogBuilder;

        public Builder(Context context){
            dialogBuilder = new AlertDialog.Builder(context);
        }

        public Builder setTitle(String title){
            dialogBuilder.setTitle(title);
            return this;
        }

        public Builder setMessage(String message){
            dialogBuilder.setMessage(message);
            return this;
        }

        public Builder setMessage(int messageId){
            dialogBuilder.setMessage(messageId);
            return this;
        }

        public Builder setPositiveButton(String title, OnClickListener onClickListener){
            dialogBuilder.setPositiveButton(title, onClickListener);
            return this;
        }

        public Builder setPositiveButton(int titleId, OnClickListener onClickListener){
            dialogBuilder.setPositiveButton(titleId, onClickListener);
            return this;
        }

        public Builder setNeutralButton(String title, OnClickListener onClickListener){
            dialogBuilder.setNeutralButton(title, onClickListener);
            return this;
        }

        public Builder setNeutralButton(int titleId, OnClickListener onClickListener){
            dialogBuilder.setNeutralButton(titleId, onClickListener);
            return this;
        }

        public Builder setNegativeButton(String title, OnClickListener onClickListener){
            dialogBuilder.setNegativeButton(title, onClickListener);
            return this;
        }

        public Builder setNegativeButton(int titleId, OnClickListener onClickListener){
            dialogBuilder.setNegativeButton(titleId, onClickListener);
            return this;
        }

        public Builder setCancelable(boolean b){
            dialogBuilder.setCancelable(b);
            return this;
        }

        public Builder setOnCancelListener(DialogInterface.OnCancelListener listener){
            dialogBuilder.setOnCancelListener(listener);
            return this;
        }

        private Dialog createDialog(){
            return dialogBuilder.create();
        }

        public SimpleDialogFragment build(){
            return new SimpleDialogFragment(this);
        }
    }

}
