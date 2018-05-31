/**
 * Module Name/Class			:	AllUsersActivity
 * Author Name					:	Sachin Arora
 * Date							:	May,31 2018
 * Purpose						:	This class fetch all users from FCM database
 */

package example.com.appchat.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import example.com.appchat.R;
import example.com.appchat.adapters.AllUsersAdapter;
import example.com.appchat.databinding.ActivityAllUsersBinding;
import example.com.appchat.models.User;
import example.com.appchat.utility.AppConstants;

public class AllUsersActivity extends AppCompatActivity {

    private static final String TAG = AllUsersActivity.class.getSimpleName();
    private ActivityAllUsersBinding allUsersBinding;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private List<User> userArrayList;
    private DatabaseReference myRef;
    private AllUsersAdapter allUsersAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();

        setLayoutManager();

        getCurrentUserDetails();

        allUsersBinding.includeToolbar.btnLogout.setOnClickListener(v -> {

            signOutFirebaseAndNavigateToLoginPage();

        });
    }

    /**
     * Module Name			        :	setLayoutManager
     * Author Name					:	Sachin Arora
     * Date							:	May, 31 2018
     * Purpose						:	This method set layout to recycler view
     **/

    private void setLayoutManager() {
        LinearLayoutManager lmAllUsers = new LinearLayoutManager(this);
        allUsersBinding.rvUsers.setLayoutManager(lmAllUsers);

    }

    /**
     * Module Name			        :	getAllChildList
     * Author Name					:	Sachin Arora
     * Date							:	May, 31 2018
     * Purpose						:	This method make reference to database and get all its child
     **/

    private void getAllChildList() {
        DatabaseReference childReference = database.getReference(AppConstants.sUserTable);

        childReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                saveUsersDataToArrayList(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.e("onChildChanged", "called");
//                userArrayList.clear();
//                saveUsersDataToArrayList(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.e("onChildRemoved", "called");
//                userArrayList.clear();
//                saveUsersDataToArrayList(dataSnapshot);

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.e("onChildMoved", "called");
//                userArrayList.clear();
//                saveUsersDataToArrayList(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("onCancelled", "called");
                Toast.makeText(AllUsersActivity.this, AppConstants.sAuthFailed, Toast.LENGTH_SHORT).show();
                signOutFirebaseAndNavigateToLoginPage();
            }
        });
    }

    /**
     * Module Name			        :	saveUsersDataToArrayList
     * Author Name					:	Sachin Arora
     * Date							:	May, 31 2018
     * Purpose						:	This method save users data to array list
     **/

    private void saveUsersDataToArrayList(DataSnapshot dataSnapshot) {

        if (progressDialog.isShowing())
            progressDialog.dismiss();
        User user = dataSnapshot.getValue(User.class);
        userArrayList.add(user);

        Log.e("list size is", userArrayList.size() + "");

        for (int i = 0; i < userArrayList.size(); i++) {
            Log.e("names are", userArrayList.get(i).getName());
        }

        allUsersAdapter = new AllUsersAdapter(this, userArrayList, position -> {

            Intent intent = new Intent(this, MessageUserActivity.class);
            intent.putExtra(AppConstants.sUserData, userArrayList.get(position));
            startActivity(intent);


        });
        allUsersBinding.rvUsers.setAdapter(allUsersAdapter);

    }

    /**
     * Module Name			        :	getCurrentUserDetails
     * Author Name					:	Sachin Arora
     * Date							:	May, 31 2018
     * Purpose						:	This method get current user detail(who is logged in)
     **/

    private void getCurrentUserDetails() {

        progressDialog.show();

        FirebaseUser onlineUser = mAuth.getCurrentUser();
        if (onlineUser != null) {
            myRef = database.getReference(AppConstants.sUserTable).child(onlineUser.getUid()).child(AppConstants.sName);

            myRef.addValueEventListener(valueEventListener);

        }
    }

    /**
     * Module Name			        :	valueEventListener
     * Author Name					:	Sachin Arora
     * Date							:	May, 31 2018
     * Purpose						:	This is a listener which listens to data change in database
     **/

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            setTextToToolbar(dataSnapshot.getValue(String.class));

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "onCancelled", databaseError.toException());
            progressDialog.dismiss();
            signOutFirebaseAndNavigateToLoginPage();

        }
    };

    /**
     * Module Name			        :	setTextToToolbar
     * Author Name					:	Sachin Arora
     * Date							:	May, 31 2018
     * Purpose						:	This method set logged in user name to toolbar
     **/

    private void setTextToToolbar(String name) {
        allUsersBinding.includeToolbar.tvToolbarWriteNoteTitle.setText(name);
        myRef.removeEventListener(valueEventListener);
        getAllChildList();

    }

    /**
     * Module Name			        :	signOutFirebaseAndNavigateToLoginPage
     * Author Name					:	Sachin Arora
     * Date							:	May, 31 2018
     * Purpose						:	This method log out user from FCM database and then navigating it to login page
     **/

    private void signOutFirebaseAndNavigateToLoginPage() {

        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Module Name			        :	init
     * Author Name					:	Sachin Arora
     * Date							:	May, 31 2018
     * Purpose						:	This method initializes all the object
     **/

    private void init() {

        allUsersBinding = DataBindingUtil.setContentView(this, R.layout.activity_all_users);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(AppConstants.sPleaseWait);
        progressDialog.setCancelable(false);

        userArrayList = new ArrayList<>();

    }
}
