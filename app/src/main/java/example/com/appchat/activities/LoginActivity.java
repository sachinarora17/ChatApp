/**
 * Module Name/Class			:	LoginActivity
 * Author Name					:	Sachin Arora
 * Date							:	May,30 2018
 * Purpose						:	This class login/ signup user to FCM Database
 */

package example.com.appchat.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
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

        setTextToToolbar();

        if (mAuth.getCurrentUser() != null) {

            navigateToUsersScreen();

        } else
            Log.e("current user is", "null");

        loginBinding.btnLogin.setOnClickListener(v -> {

            if (checkValidation()) {
                checkIfUserAlreadyExistOrNot();

            }
        });
    }


    /**
     * Module Name			        :	setTextToToolbar
     * Author Name					:	Sachin Arora
     * Date							:	May, 30 2018
     * Purpose						:	This method set the text to toolbar
     **/

    private void setTextToToolbar() {
        loginBinding.includeToolbar.tvToolbarWriteNoteTitle.setText(getString(R.string.login));
        loginBinding.includeToolbar.btnLogout.setVisibility(View.GONE);

    }

    /**
     * Module Name			        :	signUpNewUser
     * Author Name					:	Sachin Arora
     * Date							:	May, 30 2018
     * Purpose						:	This method sign up the user to FCM Authentication
     **/

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
                            progressDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(this, AppConstants.sErrorAddingUser, Toast.LENGTH_SHORT).show();

                            currentUser = null;
                        }
                    } else {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Toast.makeText(this, AppConstants.sErrorAddingUser, Toast.LENGTH_SHORT).show();

                    }
                });
    }

    /**
     * Module Name			        :	saveValuesToDatabase
     * Author Name					:	Sachin Arora
     * Date							:	May, 30 2018
     * Purpose						:	This method adds extra values(Apart form email,password) to FCM database.
     **/

    private void saveValuesToDatabase() {

        if (!progressDialog.isShowing())
            progressDialog.show();
        String userId = currentUser.getUid();

        DatabaseReference myRef = database.getReference().child(AppConstants.sUserTable).child(userId);

        HashMap<String, String> userDetailsHashMap = new HashMap<>();
        userDetailsHashMap.put(AppConstants.sName, sName);
        myRef.setValue(userDetailsHashMap);

        progressDialog.dismiss();
        navigateToUsersScreen();
    }

    /**
     * Module Name			        :	navigateToUsersScreen
     * Author Name					:	Sachin Arora
     * Date							:	May, 30 2018
     * Purpose						:	This method intent user to next activity
     **/

    private void navigateToUsersScreen() {
        Intent intent = new Intent(this, AllUsersActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Module Name			        :	checkValidation
     * Author Name					:	Sachin Arora
     * Date							:	May, 30 2018
     * Purpose						:	This method check validation on all fields
     **/

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

        } else if (sPassword.length() < 6) {
            Toast.makeText(this, AppConstants.sPasswordLength, Toast.LENGTH_SHORT).show();
            return false;

        } else if (sName.isEmpty()) {
            Toast.makeText(this, AppConstants.sNameEmpty, Toast.LENGTH_SHORT).show();
            return false;

        } else
            return true;
    }

    /**
     * Module Name			        :	checkIfUserAlreadyExistOrNot
     * Author Name					:	Sachin Arora
     * Date							:	May, 30 2018
     * Purpose						:	This method check whether user is already exists in FCM Authentication or not.
     **/

    private void checkIfUserAlreadyExistOrNot() {

        progressDialog.show();

        mAuth.signInWithEmailAndPassword(sEmail, sPassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        currentUser = mAuth.getCurrentUser();
                        progressDialog.dismiss();
                        navigateToUsersScreen();

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure" + task.getException());

                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Log.e("invalid credentials", "True");
                            progressDialog.dismiss();
                            Toast.makeText(this, AppConstants.sInvalidCredentials, Toast.LENGTH_SHORT).show();

                        } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            Log.e("invalid user", "True");
                            signUpNewUser();

                        } else {
                            Log.e("other", "error");
                            progressDialog.dismiss();
                            Toast.makeText(this, AppConstants.sUnknownError, Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    /**
     * Module Name			        :	init
     * Author Name					:	Sachin Arora
     * Date							:	May, 30 2018
     * Purpose						:	This method initials all the objects.
     **/

    private void init() {

        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        database = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(AppConstants.sPleaseWait);
        progressDialog.setCancelable(false);
        currentUser = null;
        mAuth = FirebaseAuth.getInstance();
    }
}
