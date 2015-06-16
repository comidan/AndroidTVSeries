package test.tvdb.dev.com.tvdb_test;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity
{
    private String[] menuTitles;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private Toolbar toolbar;
    private Handler searchHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        if (toolbar!=null) {
            setSupportActionBar(toolbar);
            DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                    this,  mDrawerLayout, toolbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close
            ){
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    invalidateOptionsMenu();
                }

                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    invalidateOptionsMenu();
                }
            };
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            mDrawerToggle.syncState();
        }
        searchHandler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                Fragment fragment=new SearchFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .commit();
                drawerList.setItemChecked(0,true);
                getSupportActionBar().setTitle(menuTitles[0]);
                return false;
            }
        });
        menuTitles=getResources().getStringArray(R.array.menu_titles);
        drawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList=(ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(new ArrayAdapter<String>(this,R.layout.drawer_list_item,menuTitles));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame,new MyTVSeriesListFragment()).commit();
        drawerList.setItemChecked(1,true);
        getSupportActionBar().setTitle(menuTitles[1]);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Fragment fragment;
            switch(position)
            {
                case 0: fragment=new SearchFragment(); break;
                case 1: fragment=new MyTVSeriesListFragment(); ((MyTVSeriesListFragment)fragment).setHandler(searchHandler); break;
                case 2: fragment=new TrendsFragment(); break;
                case 3: fragment=new PopularsFragment(); break;
                default: fragment=new MyTVSeriesListFragment(); ((MyTVSeriesListFragment)fragment).setHandler(searchHandler);
            }
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame,fragment).commit();
            drawerList.setItemChecked(position, true);
            getSupportActionBar().setTitle(menuTitles[position]);
            drawerLayout.closeDrawer(drawerList);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }
}
