package online.internship;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Random;

public class LoginWithOtpActivity extends AppCompatActivity {

    Button login, signup;
    EditText contact;
    SQLiteDatabase db;

    SharedPreferences sp;
    CheckBox rememberMe;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_otp);
        sp = getSharedPreferences(ConstantSp.PREF, MODE_PRIVATE);

        db = openOrCreateDatabase("Online_Internship", MODE_PRIVATE, null);
        String tableQuery = "CREATE TABLE IF NOT EXISTS USERS(USERID INTEGER PRIMARY KEY AUTOINCREMENT,NAME VARCHAR(100),EMAIL VARCHAR(100),CONTACT INT(10),PASSWORD VARCHAR(20),GENDER VARCHAR(6),CITY VARCHAR(50),DOB VARCHAR(10))";
        db.execSQL(tableQuery);

        contact = findViewById(R.id.login_with_otp_contact);

        rememberMe = findViewById(R.id.login_with_otp_remeber);

        signup = findViewById(R.id.login_with_otp_signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(LoginWithOtpActivity.this, SignupActivity.class);
                startActivity(intent);*/
                new CommonMethod(LoginWithOtpActivity.this, SignupActivity.class);
            }
        });

        login = findViewById(R.id.login_with_otp_login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contact.getText().toString().trim().equals("")) {
                    contact.setError("Contact No. Required");
                }
                else if (contact.getText().toString().trim().length()<10) {
                    contact.setError("Valid Contact No. Required");
                }
                else {
                    String selectQuery = "SELECT * FROM USERS WHERE CONTACT='" + contact.getText().toString() + "'";
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

                        /*new CommonMethod(LoginWithOtpActivity.this, "Login Successfully");
                        new CommonMethod(view, "Login Successfully");
                        new CommonMethod(LoginWithOtpActivity.this, DashboardActivity.class);*/
                        String sOTP = getRandomNumberString();
                        sp.edit().putString(ConstantSp.OTP_CODE,sOTP).commit();
                        SmsManager smsManager = SmsManager.getDefault();
                        ArrayList<String> parts = smsManager.divideMessage("Your OTP Code Is : "+sOTP);
                        smsManager.sendMultipartTextMessage(contact.getText().toString(), null, parts, null, null);
                        new CommonMethod(LoginWithOtpActivity.this,"Sms Send Successfully");
                        new CommonMethod(LoginWithOtpActivity.this,OtpActivity.class);
                    } else {
                        new CommonMethod(LoginWithOtpActivity.this, "Login Unsuccessfully");
                    }
                }
            }
        });

    }

    public static String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finishAffinity();
    }
}