package online.internship;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    Button login, signup;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    EditText email, password;
    SQLiteDatabase db;

    SharedPreferences sp;
    CheckBox rememberMe;
    ImageView passwordHide, passwordShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences(ConstantSp.PREF, MODE_PRIVATE);

        db = openOrCreateDatabase("Online_Internship", MODE_PRIVATE, null);
        String tableQuery = "CREATE TABLE IF NOT EXISTS USERS(USERID INTEGER PRIMARY KEY AUTOINCREMENT,NAME VARCHAR(100),EMAIL VARCHAR(100),CONTACT INT(10),PASSWORD VARCHAR(20),GENDER VARCHAR(6),CITY VARCHAR(50),DOB VARCHAR(10))";
        db.execSQL(tableQuery);

        email = findViewById(R.id.main_email);
        password = findViewById(R.id.main_password);

        passwordHide = findViewById(R.id.main_password_hide);
        passwordShow = findViewById(R.id.main_password_show);

        passwordHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordHide.setVisibility(View.GONE);
                passwordShow.setVisibility(View.VISIBLE);
                password.setTransformationMethod(null);
            }
        });

        passwordShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordHide.setVisibility(View.VISIBLE);
                passwordShow.setVisibility(View.GONE);

                password.setTransformationMethod(new PasswordTransformationMethod());

            }
        });

        rememberMe = findViewById(R.id.main_remeber);

        signup = findViewById(R.id.main_signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);*/
                new CommonMethod(MainActivity.this, SignupActivity.class);
            }
        });

        login = findViewById(R.id.main_login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().toString().trim().equals("")) {
                    email.setError("Email Id Required");
                } else if (!email.getText().toString().trim().matches(emailPattern)) {
                    email.setError("Valid Email Id Required");
                } else if (password.getText().toString().trim().equals("")) {
                    password.setError("Password Required");
                } else if (password.getText().toString().trim().length() < 6) {
                    password.setError("Min. 6 Char Password Required");
                } else {

                    String selectQuery = "SELECT * FROM USERS WHERE EMAIL='" + email.getText().toString() + "' AND PASSWORD='" + password.getText().toString() + "'";
                    Cursor cursor = db.rawQuery(selectQuery, null);
                    //Log.d("CURSOR_COUNT", String.valueOf(cursor.getCount()));

                    if (cursor.getCount() > 0) {

                        while (cursor.moveToNext()) {
                            String sUserId = cursor.getString(0);
                            String sName = cursor.getString(1);
                            String sEmail = cursor.getString(2);
                            String sContact = cursor.getString(3);
                            String sPassword = cursor.getString(4);
                            String sGender = cursor.getString(5);
                            String sCity = cursor.getString(6);
                            String sDob = cursor.getString(7);

                            sp.edit().putString(ConstantSp.ID, sUserId).commit();
                            sp.edit().putString(ConstantSp.NAME, sName).commit();
                            sp.edit().putString(ConstantSp.EMAIL, sEmail).commit();
                            sp.edit().putString(ConstantSp.CONTACT, sContact).commit();
                            sp.edit().putString(ConstantSp.PASSWORD, sPassword).commit();
                            sp.edit().putString(ConstantSp.GENDER, sGender).commit();
                            sp.edit().putString(ConstantSp.CITY, sCity).commit();
                            sp.edit().putString(ConstantSp.DOB, sDob).commit();
                            if (rememberMe.isChecked()) {
                                sp.edit().putString(ConstantSp.REMEMBER, "Yes").commit();
                            } else {
                                sp.edit().putString(ConstantSp.REMEMBER, "").commit();
                            }

                            Log.d("USER_DATA", sUserId + "\n" + sName + "\n" + sEmail + "\n" + sContact + "\n" + sPassword + "\n" + sGender + "\n" + sCity + "\n" + sDob);
                        }

                        System.out.println("Login Successfully");
                        //Toast.makeText(MainActivity.this, "Login Successfully", Toast.LENGTH_LONG).show();
                        new CommonMethod(MainActivity.this, "Login Successfully");
                        //Snackbar.make(view, "Login Successfully", Snackbar.LENGTH_SHORT).show();
                        new CommonMethod(view, "Login Successfully");
                        /*Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);*/
                        new CommonMethod(MainActivity.this, DashboardActivity.class);
                    } else {
                        new CommonMethod(MainActivity.this, "Login Unsuccessfully");
                    }
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finishAffinity();
    }
}