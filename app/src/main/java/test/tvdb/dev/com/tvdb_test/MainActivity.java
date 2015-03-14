package test.tvdb.dev.com.tvdb_test;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity
{
    private String[] menuTitles;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private Toolbar toolbar;
    private Button addSeries;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        if (toolbar!=null)
            setSupportActionBar(toolbar);
        menuTitles=getResources().getStringArray(R.array.menu_titles);
        drawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList=(ListView) findViewById(R.id.left_drawer);
        addSeries=(Button)findViewById(R.id.add_series);
        addSeries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment=new SearchFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .commit();
                drawerList.setItemChecked(0,true);
                getSupportActionBar().setTitle(menuTitles[0]);
            }
        });
        drawerList.setAdapter(new ArrayAdapter<String>(this,R.layout.drawer_list_item,menuTitles));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Fragment fragment;
            switch(position)
            {
                case 0:fragment=new SearchFragment(); break;
                case 1:fragment=new MyTVSeriesList(); break;
                default:fragment=new MyTVSeriesList();
            }
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                           .replace(R.id.content_frame, fragment)
                           .commit();
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
