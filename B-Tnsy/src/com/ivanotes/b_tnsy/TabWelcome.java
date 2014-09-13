package com.ivanotes.b_tnsy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link TabWelcome.OnFragmentInteractionListener} interface
 * to handle interaction events. Use the {@link TabWelcome#newInstance} factory
 * method to create an instance of this fragment.
 * 
 */
public class TabWelcome extends Fragment{
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	
	private static final String ARG_SECTION_NUMBER = "section_number";
	
	private View rootView;
	
	// TODO: Rename and change types and number of parameters
	public static TabWelcome newInstance(int sectionNumber) {
		TabWelcome fragment = new TabWelcome();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public TabWelcome() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		
		rootView =inflater.inflate(R.layout.fragment_tab_welcome, container, false);

		return rootView;

	}

}
