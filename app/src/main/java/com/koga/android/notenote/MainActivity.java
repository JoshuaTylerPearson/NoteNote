package com.koga.android.notenote;


import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

@SuppressLint("InflateParams")
public class MainActivity extends Activity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private ListView mDrawerList;
    private ArrayList<String> subjectsList;
    private ArrayAdapter<String> subjectAdapter;

    private ListView slctList;
    private ArrayList<String> divNotesList;
    private ArrayAdapter<String> divNotesAdapter;

    private Button newNoteBtn;
    private Button cancelBtn;
    private boolean drawerOpen = false;
    private boolean isNote = false;
    private boolean inflated = false;

    private String result;
    protected static String sbj;
    protected static String div;
    protected static String note;
    private int id;
    private View promptsView;
    private View dividerView;
    private View notesView;
    private AlertDialog.Builder alertDialogBuilder;

    private DataBase db;


    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        subjectsList = new ArrayList<String>();
        divNotesList = new ArrayList<String>();

        mTitle = mDrawerTitle = getTitle();
        
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerList = (ListView) findViewById(R.id.left_drawer);


        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        initializeElements();
        subjectAdapter = new ArrayAdapter<String>(this,
               R.layout.drawer_list_item, subjectsList);
        mDrawerList.setAdapter(subjectAdapter);
        subjectAdapter.notifyDataSetChanged();
        //previous 4 lines populate list from db

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
                drawerOpen = false;

                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                drawerOpen = true;

                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
            
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sbj = mDrawerList.getItemAtPosition(i).toString();
                setContentView(R.layout.slct_dlg_fgmt);
                slctList = (ListView) findViewById(R.id.expandableListView);
                int numNotes = 0;
                divNotesList = db.getDividers(sbj);

                divNotesAdapter = new ArrayAdapter<String>(getApplicationContext(),
                        R.layout.drawer_list_item, divNotesList);
                slctList.setAdapter(divNotesAdapter);
                divNotesAdapter.notifyDataSetChanged();

                ArrayList<String> temp = divNotesList;
                int tSize = temp.size();
                for(int x = 0; x < tSize; x++) {
                    String d = divNotesList.get(x);
                    int place = divNotesList.indexOf(d);
                    ArrayList<String> tempnote = db.getNotes(sbj, d);
                    for(int y = 0; y < tempnote.size(); y++) {
                        //int gerp = divNotesList.indexOf(x);
                        divNotesList.add(place + 1, "\t\t\t" + tempnote.get(y));
                    }
                }

                divNotesAdapter.notifyDataSetChanged();


                newNoteBtn = (Button) findViewById(R.id.new_noteBtn);
                cancelBtn = (Button) findViewById(R.id.can_slct);
                newNoteBtn.setOnClickListener(new slctListener());
                cancelBtn.setOnClickListener(new slctListener());

                registerForContextMenu(slctList);
                slctList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        String select = ((String) slctList.getItemAtPosition(i));
                        if(select.contains("\t\t\t")) {
                            note = select;
                            if(div == null){
                                for(int f = divNotesList.indexOf(note); f >= 0; f--)
                                {
                                    String check = divNotesList.get(f);
                                    if(!check.contains("\t\t\t")) {
                                        div = check;
                                        //Toast.makeText(getApplicationContext(),check + " " + note + " " + divNotesList.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }
                            //inflated = true;
                            //Toast.makeText(getApplicationContext(), db.getBitmaps().get, Toast.LENGTH_SHORT).show();
                            setContentView(R.layout.note_view);
                        }
                        else {
                            div = select;
                            showNotes(div);
                        }
                    }
                });
            }
        });//close onItemClick drawer listview


        registerForContextMenu(mDrawerList);


    } //end onCreate

/////////////////////////////////////slct_dlg_fgmt btn listener/////////////////////////////////////
    private class slctListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.new_noteBtn:
                    showDivider(sbj);
                    //if(!itemSelected)
                       // Toast.makeText(getApplicationContext(), "Please select a divider or press '+' to create one.", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.can_slct:
                    setContentView(R.layout.activity_main);
                    onCreate(Bundle.EMPTY);
                    break;
            }

        }
    }

    //////////////////////////////////begin long press////////////////////////////////////
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        ListView lv = (ListView) v;
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        if(R.id.left_drawer == v.getId()) {
            //Toast.makeText(getApplicationContext(), "subject list", Toast.LENGTH_SHORT).show();
            id = R.id.left_drawer;
            sbj = ((String) mDrawerList.getItemAtPosition(info.position));
            menu.setHeaderTitle(sbj);
            menu.add(Menu.NONE, 1, Menu.NONE, "Delete");
        }
        if(R.id.expandableListView == v.getId()){
            //Toast.makeText(getApplicationContext(), "divider list", Toast.LENGTH_SHORT).show();
            id = R.id.expandableListView;
            div = ((String) slctList.getItemAtPosition(info.position));
            if(div.contains("\t\t\t")){
                note = ((String) slctList.getItemAtPosition(info.position));
                menu.setHeaderTitle(note);
                menu.add(Menu.NONE, 1, Menu.NONE, "Delete this note?");
                isNote = true;
            }
            else{
                div = ((String) slctList.getItemAtPosition(info.position));
                menu.setHeaderTitle(div);
                menu.add(Menu.NONE, 1, Menu.NONE, "Delete this divider?");
            }

        }

    }//tied to mDrawerList, should try to tie to slct listview (expandable listview)

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 1:
                if(id == R.id.left_drawer) {
                    //Toast.makeText(getApplicationContext(), "subject list", Toast.LENGTH_SHORT).show();
                    db.deleteSbj(sbj);
                    subjectsList.remove(sbj);
                    mDrawerList.setAdapter(subjectAdapter);
                    subjectAdapter.notifyDataSetChanged();
                }
                if(id == R.id.expandableListView){
                    if(!isNote) {
                        db.deleteDiv(div);
                        divNotesList.remove(div);
                        divNotesAdapter.notifyDataSetChanged();
                    }
                    else{

                        db.deleteNote(note);
                        divNotesList.remove(note);
                        divNotesAdapter.notifyDataSetChanged();
                        isNote = false;
                    }
                }
            return true;
            default:
                return super.onContextItemSelected(item);
        }

    }
//////////////////////////////////end long press////////////////////////////////////
    public void initializeElements() {

        db = new DataBase(this);
        subjectsList = db.getSubjects();

    }

    public void showDivider(String subject){

        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        dividerView = li.inflate(R.layout.dividers, null);

        alertDialogBuilder = new AlertDialog.Builder(
                MainActivity.this);

        //set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(dividerView);

        final EditText userInput = (EditText) dividerView
                .findViewById(R.id.editTextDialogUserInput);
        final String sbj = subject;
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text
                                result = userInput.getText().toString();
                                if (!result.trim().isEmpty() && result != null) {

                                    if (!db.isDivider(sbj, result)) {
                                        //Toast.makeText(getApplicationContext(), "Subject: " + sbj + "bool: " + (db.isDivider(sbj, result)), Toast.LENGTH_SHORT).show();
                                        db.addDivider(sbj, result);

                                        divNotesList.add(result);
                                        //slctList.setAdapter(divNotesAdapter);
                                        divNotesAdapter.notifyDataSetChanged();

                                    }
                                } else {

                                    Toast.makeText(getApplicationContext(), "Please enter a title.", Toast.LENGTH_SHORT).show();

                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();

        //Toast.makeText(getApplicationContext(), "Things can happen here!", Toast.LENGTH_SHORT).show();

    }//close showDivider

    public void showNotes(String divider){

        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        notesView = li.inflate(R.layout.notes, null);

        alertDialogBuilder = new AlertDialog.Builder(
                MainActivity.this);

        //set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(notesView);

        final EditText userInput = (EditText) notesView
                .findViewById(R.id.editTextDialogUserInput);
        final String div = divider;
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                result = userInput.getText().toString();
                                if (!result.trim().isEmpty() && result != null) {

                                    if (!db.isNote(sbj, div, result)) {
                                        // Toast.makeText(getApplicationContext(), "Subject: " + sbj + " div:" + div + " bool: " + (db.isDivider(sbj, result)), Toast.LENGTH_SHORT).show();
                                        /*
                                        DisplayMetrics display = getApplicationContext().getResources().getDisplayMetrics();
                                        int w = display.widthPixels;
                                        int h = display.heightPixels;

                                        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
                                        Bitmap bit = Bitmap.createBitmap(w, h, conf);
                                        */
                                        db.addNote(result, sbj, div, null);//bit

                                        divNotesList.add(divNotesList.indexOf(div) + 1, "\t\t\t" + result); //
                                        //slctList.setAdapter(divNotesAdapter);
                                        divNotesAdapter.notifyDataSetChanged();

                                    }
                                } else {

                                    Toast.makeText(getApplicationContext(), "Please enter a title.", Toast.LENGTH_SHORT).show();

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

        //Toast.makeText(getApplicationContext(), "Things can happen here!", Toast.LENGTH_SHORT).show();

    }//close showDivider

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.slide_menu_actions, menu);
        return super.onCreateOptionsMenu(menu);

    }

    /* Called by invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
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
        case R.id.action_add:
            finishActivity(R.id.doodleFragment);

            /*
            if(inflated) {
                //findViewById(R.id.doodleFragment).setVisibility(View.GONE);
                finish();
                inflated = false;
            }

            setContentView(R.layout.activity_main);
            onCreate(Bundle.EMPTY);

            //onCreate(bundle);
            */
            if(!drawerOpen)
                mDrawerLayout.openDrawer(Gravity.LEFT);

           // if(drawerOpen) {
                LayoutInflater li = LayoutInflater.from(MainActivity.this);
                promptsView = li.inflate(R.layout.prompts, null);

                alertDialogBuilder = new AlertDialog.Builder(
                        MainActivity.this);

                //set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        // edit text
                                        result = userInput.getText().toString();
                                        if (!result.trim().isEmpty() && result != null) {

                                            if (!db.isSubject(result)) {
                                                db.addSubject(result);

                                                subjectsList.add(result);
                                                mDrawerList.setAdapter(subjectAdapter);
                                                subjectAdapter.notifyDataSetChanged();
                                            }
                                        } else {

                                            Toast.makeText(getApplicationContext(), "Please enter a title.", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
            //}
            return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }//onOptionsItemSelected
/////////////////////////////////////////useless?//////////////////////////////////////////////////
    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //selectItem(position);
        }
    }
/////////////////////////////////////////useless?//////////////////////////////////////////////////
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
}//MainActivity
