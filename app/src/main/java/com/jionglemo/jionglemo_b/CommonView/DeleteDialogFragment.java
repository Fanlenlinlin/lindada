package com.jionglemo.jionglemo_b.CommonView;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.jionglemo.jionglemo_b.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class DeleteDialogFragment extends DialogFragment {

    private String title;
    private String message;
    private String buttonString;

    public DeleteDialogFragment() {
        // Required empty public constructor
    }

    public static DeleteDialogFragment newInstance(String title,String message,String buttonString){
        DeleteDialogFragment fragment=new DeleteDialogFragment();
        Bundle args=new Bundle();
        args.putString("title",title);
        args.putString("message",message);
        args.putString("buttonString",buttonString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title =  getArguments().getString("title");
            message =  getArguments().getString("message");
            buttonString =  getArguments().getString("buttonString");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_delete_dialog, null);
        TextView titleTV= (TextView) view.findViewById(R.id.titleTV);
        TextView messageTV= (TextView) view.findViewById(R.id.messageTV);
        TextView deleteTV= (TextView) view.findViewById(R.id.deleteTV);
        TextView cancelTV= (TextView) view.findViewById(R.id.cancelTV);

        if(title!=null)
            titleTV.setText(title);
        if(message!=null)
            messageTV.setText(message);
        if(buttonString!=null)
            deleteTV.setText(buttonString);

        deleteTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 delete();
                if(deleteListener!=null){
                    deleteListener.onDelete();
                }
            }
        });
        cancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        builder.setView(view);
        return builder.create();
    }

    //定义公有方法供外部调用
    public void delete(){
        dismiss();
    }

    /**
     * 定义一个接口供外部调用
     */
    public interface DeleteListener{
        void onDelete();
    }

    private DeleteListener deleteListener=null;

    public void setOnDeleteListener(DeleteListener deleteListener){
        this.deleteListener=deleteListener;
    }
}
