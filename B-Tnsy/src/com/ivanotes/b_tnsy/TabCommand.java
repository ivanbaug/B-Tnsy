package com.ivanotes.b_tnsy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link TabCommand.OnFragmentInteractionListener} interface
 * to handle interaction events. Use the {@link TabCommand#newInstance} factory
 * method to create an instance of this fragment.
 * 
 */
public class TabCommand extends Fragment{
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	
	private static final String ARG_SECTION_NUMBER = "section_number";
	
	private View rootView;
	private Button bAdd;
	private Button bRem;
	
	// TODO: Rename and change types and number of parameters
	public static TabCommand newInstance(int sectionNumber) {
		TabCommand fragment = new TabCommand();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public TabCommand() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		
		rootView =inflater.inflate(R.layout.fragment_tab_command, container, false);
		init();
		return rootView;

	}

	private void init() {
		
		bAdd=(Button)rootView.findViewById(R.id.btn_addt);
		bRem=(Button)rootView.findViewById(R.id.btn_remt);
		
		bAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "b1", Toast.LENGTH_SHORT).show();				
			}
		});
		bRem.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Toast.makeText(getActivity(), "b2", Toast.LENGTH_SHORT).show();
			}
		});
	}

}
