package com.huntloc.handheldpv2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.UUID;


public class HandheldFragment extends Fragment {

    public static final String PERSONNEL_MESSAGE = "com.huntloc.handheldpv2.PERSONNEL";
    private OnHandheldFragmentInteractionListener mListener;
    private EditText mCredentialId;
    private Button buttonCkeck;
    public HandheldFragment() {

    }


    public static HandheldFragment newInstance() {
        HandheldFragment fragment = new HandheldFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_handheld, container, false);

        mCredentialId = (EditText) view
                .findViewById(R.id.editText_CredentialId);
        mCredentialId.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (mCredentialId.getText().toString().isEmpty()) {
                        Toast.makeText(getActivity(),
                                "Tap a Badge or Enter Credential ID",
                                Toast.LENGTH_LONG).show();
                    } else {
                        sendRequest();
                    }
                    return true;
                }
                return false;
            }
        });

        buttonCkeck = (Button) view.findViewById(R.id.button_Register);
        buttonCkeck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCredentialId.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(),
                            "Tap a Badge or Enter Credential ID",
                            Toast.LENGTH_LONG).show();
                } else {
                    sendRequest();
                }
            }
        });
        return view;
    }
    private void sendRequest() {
        Log.d("Printed Code", mCredentialId.getText().toString());
        Intent intent = new Intent(getActivity(),
                PersonnelActivity.class);
        intent.putExtra(PERSONNEL_MESSAGE, mCredentialId.getText().toString());
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        clearCredentialId();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnHandheldFragmentInteractionListener) {
            mListener = (OnHandheldFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnHandheldFragmentInteractionListener {
        void onHandheldFragmentInteraction();
    }
    public void setCredentialId(String id) {
        mCredentialId.setText(id);
        sendRequest();
    }
    public void clearCredentialId() {
        if(mCredentialId!=null){
            mCredentialId.setText("");
        }
    }
}
