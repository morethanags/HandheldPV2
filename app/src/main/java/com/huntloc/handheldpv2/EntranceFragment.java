package com.huntloc.handheldpv2;

import android.content.Context;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class EntranceFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private OnEntranceFragmentInteractionListener mListener;
    private SwipeRefreshLayout swipeRefreshLayout;
    public EntranceFragment() {
        
    }


    public static EntranceFragment newInstance() {
        EntranceFragment fragment = new EntranceFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entrance,
                container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view
                .findViewById(R.id.list_Entrance_Layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        updateEntrances();
        return view;
    }
    @Override
    public void onRefresh() {
        updateEntrances();
    }

    private void updateEntrances() {
        swipeRefreshLayout.setRefreshing(false);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEntranceFragmentInteractionListener) {
            mListener = (OnEntranceFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnEntranceFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnEntranceFragmentInteractionListener {
        void onEntranceFragmentInteraction();
    }
}
