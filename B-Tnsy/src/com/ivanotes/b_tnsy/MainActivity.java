package com.ivanotes.b_tnsy;

import java.util.Locale;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements
		ActionBar.TabListener {
	
	// Debugging
    private static final String TAG = "btnsyapp";
    private static final String DSTR = "MainActivity"; //Stands for debug string
    private static final boolean D = true;
    //It is used like this: if(D) Log.d(TAG, DSTR + "My debug message");
    
    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the bluetooth services
    private BtSerialService mBtService = null;
    // Check if phone has a bluetooth adapter
    public boolean btExists = false;
    
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_CODE_ENABLE_BLUETOOTH = 3;
    private static final int REQUEST_CONNECT_DEVICE_OTHER = 4;
    
    // Message types sent from the BtSerialService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
		/**
		 * Most of the above is auto-generated code
		 * below here is what i've added to onCreate
		 **/
		
		// Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btExists=(mBluetoothAdapter!=null);
        
		if(btExists){
			Toast.makeText(getApplicationContext(), R.string.txt_bluetooth_exists, Toast.LENGTH_SHORT).show();
			//If the bluetooth adapter exists but is not enabled
			if(!mBluetoothAdapter.isEnabled()){
				turnOnBluetooth();
			}else{
				//Initialize bt service
				mBtService = new BtSerialService(this, mHandler);
			}
		}
		else{
			Toast.makeText(getApplicationContext(), R.string.txt_bluetooth_not_exist, Toast.LENGTH_SHORT).show();
		}		
	}

	private void turnOnBluetooth() {
		Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		startActivityForResult(intent, REQUEST_CODE_ENABLE_BLUETOOTH);		
	}
	 @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		 if(btExists){
			 if(mBluetoothAdapter.isEnabled()){
					mBluetoothAdapter.disable();
				}
		 }
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_connect) {
			if(mBluetoothAdapter.isEnabled()){
				if (mBtService.connectSucess){
	            	Toast.makeText(getApplicationContext(), R.string.toast_bt_already_paired, Toast.LENGTH_LONG).show();
	            }
				else{
	            	Intent intent = new Intent(this, ConnectActivity.class);
	    			startActivityForResult(intent, REQUEST_CONNECT_DEVICE_OTHER);
	            }
			}
			else{
            	Toast.makeText(getApplicationContext(), R.string.toast_bt_enable_first, Toast.LENGTH_LONG).show();
			}
            return true;
		}
		else if (id == R.id.action_enable_bt) {
			
			if(!mBluetoothAdapter.isEnabled()){
				turnOnBluetooth();
			}else{
				Toast.makeText(getApplicationContext(),
						R.string.toast_bt_already_enabled, Toast.LENGTH_SHORT).show();
			}
            return true;
		}
		else if (id == R.id.action_about) {
			Intent intent = new Intent(this, AboutActivity.class);
	        this.startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}
	
	public void addTab(int iTab){
		//android.app.ActionBar ab = getActionBar();
		//ab.
	}
	public void removeTab(int iTab){
		android.app.ActionBar ab = getActionBar();
		ab.removeTab(ab.getTabAt(iTab));
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			switch (position) {
            case 0:
                // Command fragment activity
                return TabWelcome.newInstance(position + 1);
            case 1:
                // Connection fragmenW activity
            	return TabCommand.newInstance(position + 1);
            case 2:
                // Other fragment activity
                return PlaceholderFragment.newInstance(position + 1);
            }
			return null;
		}
		

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE_SECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, true);
            }
            break;
        case REQUEST_CONNECT_DEVICE_INSECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, false);
            }
            break;
        case REQUEST_CONNECT_DEVICE_OTHER:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, false);
            }
            break;
        case REQUEST_CODE_ENABLE_BLUETOOTH:
            // When the request to enable Bluetooth returns
        	if(resultCode==RESULT_CANCELED){
    			Toast.makeText(getApplicationContext(), 
    					R.string.txt_bluetooth_necessary, Toast.LENGTH_SHORT).show();
    		}else{
    			// Initialize the BtSerialService to perform bluetooth connections
    	        mBtService = new BtSerialService(this, mHandler);
    		}
        }
    }
	
	// The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BtSerialService.STATE_CONNECTED:
                    setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                    //mConversationArrayAdapter.clear();
                    break;
                case BtSerialService.STATE_CONNECTING:
                    setStatus(R.string.title_connecting);
                    break;
                case BtSerialService.STATE_LISTEN:
                case BtSerialService.STATE_NONE:
                    setStatus(R.string.title_not_connected);
                    if (!mBtService.connectSucess){
                    	Toast.makeText(getApplicationContext(), R.string.toast_bt_cant_pair, Toast.LENGTH_LONG).show();
                    }
                    break;
                }
                break;
            case MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                String writeMessage = new String(writeBuf);
                //mConversationArrayAdapter.add("Me:  " + writeMessage);
                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                //mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
                break;
                /*
                 case MESSAGE_READ :
    				String s = new String((byte[])msg.obj);
    				Log.i(TAGG,"Handler Message read: "+s);
    				Toast.makeText(getApplicationContext(), 
    						"Incomming message is: "+s
    						, Toast.LENGTH_SHORT).show();
    				//cT.cancel();
        			break;
                 */
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };
    
    private final void setStatus(int resId) {
        final android.app.ActionBar actionBar = getActionBar(); //? used to be type ActionBar
        actionBar.setSubtitle(resId);
    }
    private final void setStatus(CharSequence subTitle) {
        final android.app.ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(subTitle);
    }
	
	private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
            .getString(ConnectActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        if(D) Log.d(TAG, DSTR + " Its about to connect aaand... "+address);
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if(D) Log.d(TAG, DSTR + " There is no error well... "+device.getName());
        // Attempt to connect to the device
        mBtService.connect(device, secure);
        
        
        if(D) Log.d(TAG, DSTR + " Hell yeah!");
    }

}
