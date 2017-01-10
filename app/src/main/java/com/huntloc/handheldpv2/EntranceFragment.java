package com.huntloc.handheldpv2;

import android.content.Context;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.Date;

import android.text.format.DateFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EntranceFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private OnEntranceFragmentInteractionListener mListener;
    private SwipeRefreshLayout swipeRefreshLayout;
    ListView entranceList = null;
    ArrayList<HashMap<String, String>> list = null;

    public EntranceFragment() {
        // Required empty public constructor
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
        new Handler().postDelayed(new Runnable() {
            public void run() {
                updateEntrances();
            }
        }, 1500);
        return view;
    }

    @Override
    public void onRefresh() {
        updateEntrances();
    }

    public void updateEntrances() {
        try {
            entranceList = (ListView) getView().findViewById(R.id.list_Entrance);
        } catch (NullPointerException e) {
            return;
        }
        try {
            SQLiteHelper db = new SQLiteHelper(getContext());
            List<Journal> records = db.getJournal(getContext().getSharedPreferences(MainActivity.PREFS_NAME, 0).getString("logEntry_id", "EntryPV2"));
            list = new ArrayList<HashMap<String, String>>();
            for (int i = 0; i < records.size(); i++) {
                HashMap<String, String> item = new HashMap<String, String>();
                item.put("personnel", records.get(i).getPersonnel());
                item.put("credential", records.get(i).getCredential());
                //item.put("door", records.get(i).getDoor());
                String dateString = DateFormat.format("E, MMM dd, h:mm aa",
                        new Date(records.get(i).getTime())).toString();
                item.put("time", dateString);
                list.add(item);
            }
            String[] columns = new String[]{"personnel", "time", "credential"};
            int[] renderTo = new int[]{R.id.personnel, R.id.time, R.id.credential};

            ListAdapter listAdapter = new SimpleAdapter(getContext(), list,
                    R.layout.journallog_list_row, columns, renderTo);

            entranceList.setAdapter(listAdapter);
        } catch (Exception e) {
        } finally {
            swipeRefreshLayout.setRefreshing(false);
        }
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
