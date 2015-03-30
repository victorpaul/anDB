package com.sukinsan.anDB;

import android.app.Activity;
import android.app.Fragment;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import com.sukinsan.anDB.anDB.DBHandler;
import com.sukinsan.anDB.entity.User;


public class Main extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
        }

		User user = new User();


		DBHandler dbHandler = new DBHandler(getApplicationContext());

		//dbHandler.drop(user);
		dbHandler.create(User.class);

        user.email = "ukropia";
		dbHandler.insert(user);
        user.email = "ukrop";
		dbHandler.insert(user);
        user.email = "buhaha";
		dbHandler.insert(user);

		List<User> users = dbHandler.select("SELECT * FROM user",User.class);
        Log.i("ALL USERS","users:"+users);

/*
		dbHandler.delete(users.get(1));

		List<User> nextUsers = dbHandler.select("SELECT * FROM user",User.class);
		Log.i("ALL USERS","users after we deleted on:"+nextUsers);

		dbHandler.delete(users.get(1));

		//dbHandler.dropTable(user);
		//*/
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
}
