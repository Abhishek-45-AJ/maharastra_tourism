package com.abhishek.maharashtratourism;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottom_nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences=getSharedPreferences("MyAppPrefs",MODE_PRIVATE);
        String userId=sharedPreferences.getString("userId",null);

        // Initialize and set the custom toolbar as the ActionBar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottom_nav= findViewById(R.id.bottom_nav);

        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new HomeFragment(userId)).commit();
        }

        bottom_nav.setOnItemSelectedListener(item-> {
            int id = item.getItemId();

            if (id == R.id.home) {
                loadFragment(new HomeFragment(userId));
            } else if (id == R.id.search) {
                loadFragment(new SearchFragment(userId));
            } else if (id == R.id.event) {
                loadFragment(new EventFragment(userId));
            } else if (id == R.id.explore) {
                loadFragment(new ExploreFragment(userId));
            } else {
                loadFragment(new ProfileFragment(userId));
            }
            return true;
        });
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==R.id.logout){
            showLogoutConfirmation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLogoutConfirmation(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Do you want to logout?");
        builder.setPositiveButton("Yes",((dialog, which) -> logoutUser()));
        builder.setNegativeButton("No",((dialog, which) -> dialog.dismiss()));
        builder.create().show();
    }

    public void logoutUser(){
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(MainActivity.this,"Logout successfully",Toast.LENGTH_SHORT).show();

        Intent intent=new Intent(MainActivity.this,TouristLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void loadFragment(Fragment fragment){
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}