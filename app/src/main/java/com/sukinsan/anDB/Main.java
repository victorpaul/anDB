package com.sukinsan.anDB;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import com.sukinsan.anDB.anDB.DBHandler;
import com.sukinsan.anDB.entity.Shop;
import com.sukinsan.anDB.entity.User;


public class Main extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
        }

		DBHandler dbHandler = new DBHandler(getApplicationContext());

        dbHandler.getQM().drop(User.class);
        dbHandler.getQM().create(User.class);

        dbHandler.getQM().drop(Shop.class);
        dbHandler.getQM().create(Shop.class);

        Shop shop = new Shop();
        shop.name = "silpo";
        dbHandler.getQM().insert(shop);

        User user = new User();
        user.email = "ukropia";
        user.fieldInt = 2;
        user.fieldReal2 = 3;
        user.name = "name";
        user.password = "asdasd";
		dbHandler.getQM().insert(user);


        dbHandler.getQM().querySelectMultipleFrom("WHERE `user`.`id` = `shop`.`id`",User.class,Shop.class);

/*
        user.email = "ukrop";
		dbHandler.getQM().insert(user);
        user.email = "buhaha";
		dbHandler.getQM().insert(user);

		List<User> users = dbHandler.getQM().querySelectFrom("ORDER BY `id` DESC", User.class);
        Log.i("ALL USERS","users:"+users);

		dbHandler.getQM().delete(users.get(1));

		List<User> nextUsers = dbHandler.getQM().querySelectFrom("", User.class);
		Log.i("ALL USERS","users after we deleted on:"+nextUsers);

		dbHandler.getQM().delete(users.get(1));
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
