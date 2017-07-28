package com.android.spartanrides;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Main2Activity extends AppCompatActivity {
    FirebaseAuth.AuthStateListener authListner;
    FirebaseAuth mAuth;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        authListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    mAuth = firebaseAuth;

                } else {
                    // User is signed out

                }
                // ...
            }
        };
        FirebaseAuth.getInstance().addAuthStateListener(authListner);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.chat);
        tabLayout.getTabAt(1).setIcon(R.drawable.ride);
        tabLayout.getTabAt(2).setIcon(R.drawable.prf);
        tabLayout.getTabAt(3).setIcon(R.drawable.help);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if(id== R.id.menu_sign_out)
        {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            Intent intent = new Intent(Main2Activity.this, MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        /**
         * Declarations for Chat USers
         */

        ListView usersList;
        TextView noUsersText;
        ArrayList<String> al = new ArrayList<>();
        int totalUsers = 0;
        ProgressDialog pd;


        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_chat, container, false);

            switch (getArguments().getInt(ARG_SECTION_NUMBER))
            {
                case 1: {
                    rootView = inflater.inflate(R.layout.activity_users, container, false);
                    super.onCreate(savedInstanceState);
                    usersList = (ListView)rootView.findViewById(R.id.usersList);
                    noUsersText = (TextView)rootView.findViewById(R.id.noUsersText);

                    pd = new ProgressDialog(getContext());
                    pd.setMessage("Loading...");
                    pd.show();

                    String url = "https://androidchatapp-76776.firebaseio.com/users.json";

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                        @Override
                        public void onResponse(String s) {
                            doOnSuccess(s);
                        }
                    },new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            System.out.println("" + volleyError);
                        }
                    });

                    RequestQueue rQueue = Volley.newRequestQueue(getContext());
                    rQueue.add(request);

                    usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            UserDetails.chatWith = al.get(position);
                            startActivity(new Intent(getContext(), Chat.class));
                        }
                    });
                    break;
                }
                case 2: {
                     /*
                    Post data acepting User Inputs
                    */
                    rootView = inflater.inflate(R.layout.search_or_post, container, false);
                    TextView textView = rootView.findViewById(R.id.searchPostText);
                    Typeface typeface;

                    ImageView v =  rootView.findViewById(R.id.searchPostLogo);
                    blur(v);

                    try {
                        typeface = Typeface.createFromAsset(container.getContext().getAssets(), "fonts/Pacifico.ttf");
                        textView.setTypeface(typeface);
                        textView.setTextColor(Color.parseColor("#000000"));
                        textView.setTextSize(30);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                }
                case 3:
                {
                    rootView = inflater.inflate(R.layout.fragment_main2, container, false);
                    TextView name = (TextView)rootView.findViewById(R.id.username);
                    TextView email = (TextView)rootView.findViewById(R.id.emailid);
                    Button signout = (Button)rootView.findViewById(R.id.signoutButton);
                    break;
                }
                case 4:
                {
                    rootView = inflater.inflate(R.layout.activity_help, container, false);
                    ImageView v =  rootView.findViewById(R.id.helpLogo);
                    blur(v);
                    break;
                }
            }
            return rootView;
        }

        private void blur(ImageView v)
        {
            Bitmap blurredBitmap = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                blurredBitmap = BlurBitmap.blur( getContext(), BitmapFactory.decodeResource(getResources(), R.drawable.spartanlogo) );
                v.setImageBitmap(blurredBitmap);
            }
        }

        public void doOnSuccess(String s){
            try {
                JSONObject obj = new JSONObject(s);

                Iterator i = obj.keys();
                String key = "";

                while(i.hasNext()){
                    key = i.next().toString();

                    if(!key.equals(UserDetails.username)) {
                        al.add(key);
                    }

                    totalUsers++;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(totalUsers <=1){
                noUsersText.setVisibility(View.VISIBLE);
                usersList.setVisibility(View.GONE);
            }
            else{
                noUsersText.setVisibility(View.GONE);
                usersList.setVisibility(View.VISIBLE);
                usersList.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, al));
            }

            pd.dismiss();
        }

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
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }
    }

    public void doPost(View view)
    {
        Intent intent = new Intent(this, spartan_post.class);
        startActivity(intent);
    }

    public void doSearch(View view)
    {
        Intent intent = new Intent(this, PostsearchActivity.class);
        startActivity(intent);
    }

    public void submitSuggestion(View view)
    {
        String ratingBar =  String.valueOf(((RatingBar)findViewById(R.id.ratingBar2)).getRating());
        String suggestionText =  ((TextView)findViewById(R.id.suggestionText)).getText().toString();
        InputMethodManager imm = (InputMethodManager)this.getSystemService(this.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        if (!ratingBar.equals("0.0")) {
            HelpActivity helpActivity = new HelpActivity();
            try {
                Snackbar.make(view, "Thank you for your feedback!", Snackbar.LENGTH_INDEFINITE).show();
                new JSONActivity().JSONTransmitter(helpActivity.convertToJSON("venkat", ratingBar, suggestionText));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
            Snackbar.make(view, "Oops! Seems like you you forgot to rate us.", Snackbar.LENGTH_SHORT).show();
        }
    }
}
