package com.along.android.healthmanagement.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.along.android.healthmanagement.R;
import com.along.android.healthmanagement.entities.Food;
import com.along.android.healthmanagement.entities.Medicine;
import com.along.android.healthmanagement.entities.User;
import com.along.android.healthmanagement.fragments.BmiFragment;
import com.along.android.healthmanagement.fragments.DatePickerFragment;
import com.along.android.healthmanagement.fragments.EmergencyFragment;
import com.along.android.healthmanagement.fragments.HomeFragment;
import com.along.android.healthmanagement.fragments.NotesFragment;
import com.along.android.healthmanagement.fragments.ProfileFragment;
import com.along.android.healthmanagement.fragments.diet.AddFoodFragment;
import com.along.android.healthmanagement.fragments.diet.AddMealFragment;
import com.along.android.healthmanagement.fragments.diet.BarcodeScannerFragment;
import com.along.android.healthmanagement.fragments.diet.DietFragment;
import com.along.android.healthmanagement.fragments.medication.AddMedicineFormFragment;
import com.along.android.healthmanagement.fragments.medication.AddPrescriptionFormFragment;
import com.along.android.healthmanagement.fragments.medication.MedicationMenuFragment;
import com.along.android.healthmanagement.fragments.vitalsigns.VitalSignTabFragment;
import com.along.android.healthmanagement.helpers.EntityManager;

import java.util.Calendar;

public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AddMedicineFormFragment.OnMedicineAddedListener,
        BarcodeScannerFragment.OnBarcodeDetectedListener, DatePickerFragment.OnDietDateChangeListener, AddFoodFragment.OnFoodAddedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                createFragment(new HomeFragment(), "homeFragment");
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);

        SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
        Long userIdS = sp.getLong("uid", 0);
        User user = EntityManager.findById(User.class, userIdS);

        if (user != null && user.getRealname() != null && user.getEmail() != null) {
            TextView tvNavUsername = (TextView) header.findViewById(R.id.tvNavUsername);
            tvNavUsername.setText(null != user.getRealname() ? user.getRealname() : "");
            TextView tvNavEmail = (TextView) header.findViewById(R.id.tvNavEmail);
            tvNavEmail.setText(null != user.getEmail() ? user.getEmail() : "");
        }

        createFragment(new HomeFragment(), "homeFragment");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
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
        } else if (id == R.id.action_signout) {
            return signOut();
        }

        return super.onOptionsItemSelected(item);
    }

    protected boolean signOut() {
        Intent intent = new Intent();
        intent.setClass(NavigationDrawerActivity.this, LoginActivity.class);
        startActivity(intent);

        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            createFragment(new ProfileFragment(), "profileFragment");
        } else if (id == R.id.nav_medication) {
            createFragment(new MedicationMenuFragment(), "medicationListingFragment");
        } else if (id == R.id.nav_diet) {
            createFragment(new DietFragment(), "dietFragment");
        } else if (id == R.id.nav_vital_signs) {
            createFragment(new VitalSignTabFragment(), "vitalSignsFragment");
        } else if (id == R.id.nav_note) {

//            Intent intent = new Intent();
//            intent.setClass(NavigationDrawerActivity.this, NoteDetailActivity.class);
//            startActivity(intent);
            createFragment(new NotesFragment(), "notesFragment");
        }
        else if (id == R.id.nav_emergency) {
            createFragment(new EmergencyFragment(), "emergencyFragment");
        }
        else if (id == R.id.nav_BMI){
            createFragment(new BmiFragment(), "bmiFragment");
        }
        else if (id == R.id.nav_settings) {

        }
        else if (id == R.id.nav_signout) {
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void createFragment(Fragment fragmentObject, String fragmentTag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_navigation_drawer, fragmentObject, fragmentTag).
                addToBackStack(fragmentTag).
                commit();
    }

    @Override
    public void onMedicineAdded(Medicine medicine) {
        AddPrescriptionFormFragment addPrescriptionFormFragment = (AddPrescriptionFormFragment)
                getSupportFragmentManager().findFragmentByTag("addPrescriptionFormFragment");
        if (addPrescriptionFormFragment != null) {
            addPrescriptionFormFragment.addMedicineToList(medicine);
        } else {
            // Create fragment and give it an argument for the selected article
            AddPrescriptionFormFragment newFragment = new AddPrescriptionFormFragment();
            Bundle args = new Bundle();
            args.putLong("medicineId", medicine.getMid());
            newFragment.setArguments(args);

           createFragment(newFragment, "addPrescriptionFormFragment");
        }
    }

    @Override
    public void onBarcoodeDetected(String detectedBarcode) {
        AddMealFragment addMealFragment = (AddMealFragment)
                getSupportFragmentManager().findFragmentByTag("addMealFragment");
        if (addMealFragment != null) {
            addMealFragment.receiveBarcode(detectedBarcode);
        } else {
            Log.e("AddMealFragment", "AddMealFragment not be found");
            createFragment(new AddMealFragment(), "addMealFragment");
        }
    }

    @Override
    public void onDietDateChange(Calendar selectedDate) {
        DietFragment dietFragment = (DietFragment)
                getSupportFragmentManager().findFragmentByTag("dietFragment");
        if (selectedDate != null) {
            dietFragment.receiveSelectedDate(selectedDate);
        } else {
            Log.e("DietFragment", "DietFragment not be found");
            createFragment(new DietFragment(), "dietFragment");
        }
    }

    @Override
    public void onFoodAdded(Food food) {
        AddMealFragment addMealFragment = (AddMealFragment)
                getSupportFragmentManager().findFragmentByTag("addMealFragment");
        if (addMealFragment != null) {
            addMealFragment.receiveAddedFood(food);
        } else {
            Log.e("AddMealFragment", "AddMealFragment not be found");
            createFragment(new AddMealFragment(), "addMealFragment");
        }
    }
}
