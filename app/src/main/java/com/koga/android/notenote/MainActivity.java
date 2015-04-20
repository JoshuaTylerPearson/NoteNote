package com.koga.android.notenote;


import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("InflateParams")
public class MainActivity extends Activity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private ArrayList<String> notesDividersSubjects;
    private ArrayAdapter<String> notesAdapter;
    //private ArrayAdapter<String> spinnerAdapter;

    private String result;
    private int promptIndex;
    private Spinner promptSpinner;
    private View promptsView;
    private Spinner directorySpinner;
    private TextView directoryLabel;
    private AlertDialog.Builder alertDialogBuilder;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        notesDividersSubjects = new ArrayList<String>();
        mTitle = mDrawerTitle = getTitle();
        
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
       
        notesAdapter = new ArrayAdapter<String>(this,
               R.layout.drawer_list_item, notesDividersSubjects);
        
        
        //mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                //R.layout.drawer_list_item, notesDividersSubjects)); 
        /////////unable to use adapter name this way /////////
        
        
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
                

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
            
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            //selectItem(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
    	if(!drawerOpen){
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    	}
    	else{
    		MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.slide_menu_actions, menu);
            return super.onCreateOptionsMenu(menu);
    	}
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if(drawerOpen)
        menu.findItem(R.id.action_add).setVisible(drawerOpen);
        
        return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
        case R.id.action_websearch:
            // create intent to perform web search for this planet
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
            // catch event that there's no activity to handle intent
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
            }
            return true;
            
        /*
         * 
         *   
         *   
         *   
         *   Add button functionality needs work
         *   
         *   
         *   
         *   
         *   
         *   
         */
        case R.id.action_add:
        	
        	LayoutInflater li = LayoutInflater.from(MainActivity.this);
			promptsView = li.inflate(R.layout.prompts, null);
			promptSpinner = (Spinner) promptsView.findViewById(R.id.spinner1);
			directorySpinner = (Spinner) promptsView.findViewById(R.id.directorySpinner);
			directoryLabel = (TextView) promptsView.findViewById(R.id.textView2);
			
			//directorySpinner.setAdapter(notesAdapter);
			//notesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
        	//take user input, create new array index
        	alertDialogBuilder = new AlertDialog.Builder(
					MainActivity.this);
        	
			//set prompts.xml to alertdialog builder
			alertDialogBuilder.setView(promptsView);
			
			promptIndex = promptSpinner.getSelectedItemPosition();
			PromptSpinnerListener promptListen = new PromptSpinnerListener();
			promptSpinner.setOnItemSelectedListener((OnItemSelectedListener) promptListen);
			
			final EditText userInput = (EditText) promptsView
					.findViewById(R.id.editTextDialogUserInput);

			// set dialog message
			alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("OK",
				  new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog,int id) {
					// get user input and set it to result
					// edit text
				    	
					    promptIndex = promptSpinner.getSelectedItemPosition();
						result = userInput.getText().toString();
						if(promptIndex == 2)
						{
							notesDividersSubjects.add(result);
							mDrawerList.setAdapter(notesAdapter);
							notesAdapter.notifyDataSetChanged();
	
						}
						else if(promptIndex == 1)
						{
							//Toast.makeText(getApplicationContext(),
								      //  "Selected: " + directorySpinner.getSelectedItem(), Toast.LENGTH_LONG).show();
							notesDividersSubjects.add("   " + result); //notesDividersSubjects.indexOf(directorySpinner.getSelectedItem()),
							mDrawerList.setAdapter(notesAdapter);
							notesAdapter.notifyDataSetChanged();
						}
						else if(promptIndex == 0)
						{
							notesDividersSubjects.add("       " + result); // notesDividersSubjects.indexOf(directorySpinner.getSelectedItem()),
							mDrawerList.setAdapter(notesAdapter);
							notesAdapter.notifyDataSetChanged();
						}
				    
				    }
				  })
				.setNegativeButton("Cancel",
				  new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
				    }
				  });

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
		
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //selectItem(position);
        }
    }
    private class PromptSpinnerListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			
		//Toast.makeText(parent.getContext(),
			       //  "Position: " + position, Toast.LENGTH_LONG).show();
			
			if(position == 2)
			{
				
				directorySpinner.setEnabled(false);
				directoryLabel.setEnabled(false);
				alertDialogBuilder.setView(promptsView);
			}
			else
			{
				directorySpinner.setEnabled(true);
				directoryLabel.setEnabled(true);
				alertDialogBuilder.setView(promptsView);
			}
		
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		
		}
    	
    }
/*
    private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment = new PlanetFragment();
        Bundle args = new Bundle();
        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }
*/
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    
    //    EDIT FOLLOWING CODE TO MAKE LIST ITEMS
    public class NoteFragment extends Fragment {
        //public static final String ARG_PLANET_NUMBER = "planet_number";

        public NoteFragment() {
            // Empty constructor required for fragment subclasses
        }

       
    }
}