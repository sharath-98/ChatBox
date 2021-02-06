package com.example.sharath.chatbox;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {


    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private DatabaseReference mDatabaseRef;
    private List<Upload> mUploads;

    private ProgressBar mProgressCircle;
    private View mMainView;
    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mMainview =  inflater.inflate(R.layout.fragment_requests, container, false);

        Button newbut = mMainview.findViewById(R.id.mapid);
       // Toast.makeText(getActivity(),"Please wait while the map loads",Toast.LENGTH_LONG).show();

        newbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(getContext(),MapsActivity.class);
                startActivity(intent);
            }
        });
        return mMainview;
    }

}
