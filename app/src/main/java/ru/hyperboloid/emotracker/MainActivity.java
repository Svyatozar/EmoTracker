package ru.hyperboloid.emotracker;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import ru.hyperboloid.emotracker.fragments.FragmentEvents;
import ru.hyperboloid.emotracker.fragments.FragmentIndicators;
import ru.hyperboloid.emotracker.fragments.FragmentSettings;
import ru.hyperboloid.emotracker.fragments.NavigationDrawerFragment;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position)
    {
        FragmentManager fragmentManager = getFragmentManager();

        switch (position)
        {
            case 0:
                mTitle = getString(R.string.title_section1);

                fragmentManager.beginTransaction()
                        .replace(R.id.container, new FragmentEvents())
                        .commit();
                break;
            case 1:
                mTitle = getString(R.string.title_section2);

                fragmentManager.beginTransaction()
                        .replace(R.id.container, new FragmentIndicators())
                        .commit();
                break;
            case 2:
                mTitle = getString(R.string.title_section3);

                fragmentManager.beginTransaction()
                        .replace(R.id.container, new FragmentSettings())
                        .commit();
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_power_on)
        {
            Toast.makeText(this, "POWER IS ON", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.action_power_off)
        {
            Toast.makeText(this, "POWER IS OFF", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
