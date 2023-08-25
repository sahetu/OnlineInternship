package online.internship;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ShippingActivity extends AppCompatActivity implements PaymentResultWithDataListener {

    Button continueButton;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    EditText name, email, contact, address;

    RadioGroup paymentType;

    Spinner city;
    //String[] cityArray = {"Ahmedabad","Vadodara","Surat","Rajkot","Gandhinagar","Kalol","Kadi","Mehsana","Dahod","Bharuch","Veraval","Ahmedabad","Vadodara","Surat","Rajkot","Gandhinagar","Kalol","Kadi","Mehsana","Dahod","Bharuch","Veraval"};
    ArrayList<String> arrayList;

    String sCity;
    String sPaymentType;
    SQLiteDatabase db;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping);

        sp = getSharedPreferences(ConstantSp.PREF, MODE_PRIVATE);

        db = openOrCreateDatabase("Online_Internship", MODE_PRIVATE, null);

        String tableQuery = "CREATE TABLE IF NOT EXISTS USERS(USERID INTEGER PRIMARY KEY AUTOINCREMENT,NAME VARCHAR(100),EMAIL VARCHAR(100),CONTACT INT(10),PASSWORD VARCHAR(20),GENDER VARCHAR(6),CITY VARCHAR(50),DOB VARCHAR(10))";
        db.execSQL(tableQuery);

        String cartTableQuery = "CREATE TABLE IF NOT EXISTS CART(CARTID INTEGER PRIMARY KEY AUTOINCREMENT,ORDERID INTEGER(10),USERID INTEGER(10),PRODUCTID INTEGER(10),PRODUCTNAME VARCHAR(100),PRODUCTIMAGE VARCHAR(100),PRODUCTDESCRIPTION TEXT,PRODUCTPRICE VARCHAR(20),PRODUCTQTY INTEGER(10),TOTALPRICE VARCHAR(20))";
        db.execSQL(cartTableQuery);

        String wishlistTableQuery = "CREATE TABLE IF NOT EXISTS WISHLIST(WISHLISTID INTEGER PRIMARY KEY AUTOINCREMENT,USERID INTEGER(10),PRODUCTID INTEGER(10),PRODUCTNAME VARCHAR(100),PRODUCTIMAGE VARCHAR(100),PRODUCTDESCRIPTION TEXT,PRODUCTPRICE VARCHAR(20))";
        db.execSQL(wishlistTableQuery);

        String orderTableQuery = "CREATE TABLE IF NOT EXISTS SHIPPING_ORDER(ORDERID INTEGER PRIMARY KEY AUTOINCREMENT,USERID INTEGER(10),NAME VARCHAR(100),EMAIL VARCHAR(100),CONTACT VARCHAR(10),ADDRESS TEXT,CITY VARCHAR(50),TOTALAMOUNT VARCHAR(50),PAYMENTTYPE VARCHAR(20),TRANSACTIONID VARCHAR(50))";
        db.execSQL(orderTableQuery);

        name = findViewById(R.id.shipping_name);
        email = findViewById(R.id.shipping_email);
        contact = findViewById(R.id.shipping_contact);

        name.setText(sp.getString(ConstantSp.NAME, ""));
        email.setText(sp.getString(ConstantSp.EMAIL, ""));
        contact.setText(sp.getString(ConstantSp.CONTACT, ""));

        address = findViewById(R.id.shipping_address);

        city = findViewById(R.id.shipping_city);

        arrayList = new ArrayList<>();
        arrayList.add("Select City");
        arrayList.add("Gandhinagar");
        arrayList.add("Rajkot");
        arrayList.add("Ahmedabad");
        arrayList.add("Demo");
        arrayList.add("XYZ");
        arrayList.add("Surat");

        arrayList.remove(3);
        arrayList.set(3, "Vadodara");

        ArrayAdapter adapter = new ArrayAdapter(ShippingActivity.this, android.R.layout.simple_list_item_1, arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
        city.setAdapter(adapter);

        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    sCity = "";
                } else {
                    sCity = arrayList.get(i);
                    new CommonMethod(ShippingActivity.this, sCity);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        paymentType = findViewById(R.id.shipping_payment_type);
        paymentType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = findViewById(i); //i = R.id.shipping_male,R.id.shipping_female;
                sPaymentType = radioButton.getText().toString();
                new CommonMethod(ShippingActivity.this, sPaymentType);
            }
        });

        continueButton = findViewById(R.id.shipping_continue);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().trim().equals("")) {
                    name.setError("Name Required");
                } else if (email.getText().toString().trim().equals("")) {
                    email.setError("Email Id Required");
                } else if (!email.getText().toString().trim().matches(emailPattern)) {
                    email.setError("Valid Email Id Required");
                } else if (contact.getText().toString().trim().equals("")) {
                    contact.setError("Contact No. Required");
                } else if (contact.getText().toString().trim().length() < 10) {
                    contact.setError("Valid Contact No. Required");
                } else if (address.getText().toString().trim().equals("")) {
                    address.setError("Address Required");
                } else if (sCity.equals("")) {
                    new CommonMethod(ShippingActivity.this, "Please Select City");
                } else if (paymentType.getCheckedRadioButtonId() == -1) {
                    new CommonMethod(ShippingActivity.this, "Please Select Payment Type");
                } else {
                    if (sPaymentType.equalsIgnoreCase("Cash On Delivery")) {
                        createOrderMethod("");
                    } else {
                        startPayment();
                    }
                }
            }
        });
    }

    private void createOrderMethod(String transactionid) {
        String insertQuery = "INSERT INTO SHIPPING_ORDER VALUES(NULL,'" + sp.getString(ConstantSp.ID, "") + "','" + name.getText().toString() + "','" + email.getText().toString() + "','" + contact.getText().toString() + "','" + address.getText().toString() + "','" + CartFragment.iTotalPrice + "','" + sCity + "','" + sPaymentType + "','"+transactionid+"')";
        db.execSQL(insertQuery);

        String selectQuery = "SELECT MAX(ORDERID) FROM SHIPPING_ORDER LIMIT 1";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String lastOrderId = cursor.getString(0);
                Log.d("LAST_ORDER_ID",lastOrderId);
                String updateCartQuery = "UPDATE CART SET ORDERID='"+lastOrderId+"' WHERE USERID='"+sp.getString(ConstantSp.ID,"")+"' AND ORDERID='0'";
                db.execSQL(updateCartQuery);
            }
        }

        new CommonMethod(ShippingActivity.this, "Order Placed Successfully");
        new CommonMethod(ShippingActivity.this, DashboardActivity.class);
    }

    public void startPayment() {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();

        co.setKeyID("rzp_test_xsiOz9lYtWKHgF");

        try {
            JSONObject options = new JSONObject();
            options.put("name", getResources().getString(R.string.app_name));
            options.put("description", "Online Order Payment");
            options.put("send_sms_hash", true);
            options.put("allow_rotation", true);
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://cdn-icons-png.flaticon.com/512/1933/1933833.png");
            options.put("currency", "INR");
            options.put("amount", CartFragment.iTotalPrice*100);

            JSONObject preFill = new JSONObject();
            preFill.put("email", sp.getString(ConstantSp.EMAIL,""));
            preFill.put("contact", sp.getString(ConstantSp.CONTACT,""));

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        try {
            //Toast.makeText(this, "Payment Successful :\nPayment ID: " + s + "\nPayment Data: " + paymentData.getData(), Toast.LENGTH_SHORT).show();
            Log.d("RESPONSE_SUCCESS", "Payment Successful :\nPayment ID: " + s + "\nPayment Data: " + paymentData.getData());
            createOrderMethod(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        try {
            Toast.makeText(this, "Payment Failed:\nPayment Data: " + paymentData.getData(), Toast.LENGTH_SHORT).show();
            Log.d("RESPONSE_FAIL", "Payment Failed:\nPayment Data: " + paymentData.getData());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}