package online.internship;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class OtpActivity extends AppCompatActivity {

    EditText otp;
    Button submit;
    SharedPreferences sp;

    TextView resend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        sp = getSharedPreferences(ConstantSp.PREF, MODE_PRIVATE);

        otp = findViewById(R.id.otp_edit);
        submit = findViewById(R.id.otp_submit);

        resend = findViewById(R.id.otp_resend);
        resend.setPaintFlags(resend.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (otp.getText().toString().trim().equals("")) {
                    otp.setError("OTP Required");
                } else if (otp.getText().toString().trim().length() < 6) {
                    otp.setError("Valid OTP Required");
                } else {
                    if (sp.getString(ConstantSp.OTP_CODE, "").equals(otp.getText().toString())) {
                        new CommonMethod(OtpActivity.this, "Login Successfully");
                        new CommonMethod(OtpActivity.this, DashboardActivity.class);
                    } else {
                        new CommonMethod(OtpActivity.this, "Invalid OTP");
                    }
                }
            }
        });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sOTP = getRandomNumberString();
                sp.edit().putString(ConstantSp.OTP_CODE, sOTP).commit();
                SmsManager smsManager = SmsManager.getDefault();
                ArrayList<String> parts = smsManager.divideMessage("Your OTP Code Is : " + sOTP);
                smsManager.sendMultipartTextMessage(sp.getString(ConstantSp.CONTACT, ""), null, parts, null, null);
                new CommonMethod(OtpActivity.this, "Sms Send Successfully");
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

}