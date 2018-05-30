package example.com.appchat.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import example.com.appchat.R;
import example.com.appchat.databinding.ActivityLoginBinding;
import example.com.appchat.utility.AppConstants;
import example.com.appchat.utility.EmailValidator;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private ActivityLoginBinding loginBinding;
    private FirebaseAuth mAuth;
    private String sEmail;
    private String sPassword;
    private String sName;
    private FirebaseUser currentUser;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();

        loginBinding.btnLogin.setOnClickListener(v -> {

            if (checkValidation()) {

                if (checkIfUserAlreadyExistOrNot())
                    navigateToUsersScreen();
                else
                    signUpNewUser();

            }
        });

        // setValueToDatabase();

        //readDataFromDatabase();
    }

    private void signUpNewUser() {
        mAuth.createUserWithEmailAndPassword(sEmail, sPassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        currentUser = mAuth.getCurrentUser();

                        if (currentUser != null) {
                            saveValuesToDatabase();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            currentUser = null;
                        }
                    }
                });
    }

    private void saveValuesToDatabase() {

        progressDialog.show();
        String userId = currentUser.getUid();

        myRef = database.getReference().child(AppConstants.sUserTable).child(userId);

        HashMap<String, String> userDetailsHashMap = new HashMap<>();
        userDetailsHashMap.put(AppConstants.sName, sName);
        myRef.setValue(userDetailsHashMap);

        progressDialog.dismiss();
        navigateToUsersScreen();
    }

    private void navigateToUsersScreen() {
         Intent intent = new Intent(this, AllUsersActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private boolean checkValidation() {

        sEmail = loginBinding.etEmail.getText().toString();
        sName = loginBinding.etName.getText().toString();
        sPassword = loginBinding.etPassword.getText().toString();

        if (sEmail.isEmpty()) {
            Toast.makeText(this, AppConstants.sEmailEmpty, Toast.LENGTH_SHORT).show();
            return false;

        } else if (!new EmailValidator().validate(sEmail)) {
            Toast.makeText(this, AppConstants.sInvalidEmail, Toast.LENGTH_SHORT).show();
            return false;

        } else if (sPassword.isEmpty()) {
            Toast.makeText(this, AppConstants.sPasswordEmpty, Toast.LENGTH_SHORT).show();
            return false;

        } else if (sName.isEmpty()) {
            Toast.makeText(this, AppConstants.sNameEmpty, Toast.LENGTH_SHORT).show();
            return false;

        } else
            return true;
    }

    private boolean checkIfUserAlreadyExistOrNot() {

        mAuth.signInWithEmailAndPassword(sEmail, sPassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        currentUser = mAuth.getCurrentUser();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                    }
                });

        if (currentUser != null)
            return true;
        else {
            Toast.makeText(LoginActivity.this, AppConstants.sAuthFailed,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void init() {

        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        database = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(AppConstants.sPleaseWait);
        progressDialog.setCancelable(false);
        currentUser = null;
        mAuth = FirebaseAuth.getInstance();
    }

    private void readDataFromDatabase() {
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                HashMap value = dataSnapshot.getValue(HashMap.class);
                Log.d(TAG, "Value is: " + value.get("name"));
                Log.d(TAG, "password is: " + value.get("password"));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void setValueToDatabase() {

        HashMap<String, String> mUser = new HashMap<>();
        mUser.put("name", "sachin");
        mUser.put("password", "123456");
        // Write a message to the database
        myRef.setValue(mUser);
    }


}
