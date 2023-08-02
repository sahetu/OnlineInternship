package online.internship;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity {

    Button login, signup;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    EditText name,email,contact, password,confirmPassword;

    //RadioButton male,female;
    RadioGroup gender;

    Spinner city;
    //String[] cityArray = {"Ahmedabad","Vadodara","Surat","Rajkot","Gandhinagar","Kalol","Kadi","Mehsana","Dahod","Bharuch","Veraval","Ahmedabad","Vadodara","Surat","Rajkot","Gandhinagar","Kalol","Kadi","Mehsana","Dahod","Bharuch","Veraval"};
    ArrayList<String> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name = findViewById(R.id.signup_name);
        email = findViewById(R.id.signup_email);
        contact = findViewById(R.id.signup_contact);
        password = findViewById(R.id.signup_password);
        confirmPassword = findViewById(R.id.signup_confirm_password);

        city = findViewById(R.id.signup_city);

        arrayList = new ArrayList<>();

        /*for(int i=0;i<20;i++){
            arrayList.add("Index "+i);
        }*/

        arrayList.add("Select City");
        arrayList.add("Gandhinagar");
        arrayList.add("Rajkot");
        arrayList.add("Ahmedabad");
        arrayList.add("Demo");
        arrayList.add("XYZ");
        arrayList.add("Surat");

        arrayList.remove(3);
        arrayList.set(3,"Vadodara");

        ArrayAdapter adapter = new ArrayAdapter(SignupActivity.this,android.R.layout.simple_list_item_1,arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
        city.setAdapter(adapter);

        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){

                }
                else {
                    new CommonMethod(SignupActivity.this, arrayList.get(i));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        gender = findViewById(R.id.signup_gender);
        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = findViewById(i); //i = R.id.signup_male,R.id.signup_female;
                new CommonMethod(SignupActivity.this,radioButton.getText().toString());
            }
        });

        /*male = findViewById(R.id.signup_male);
        female = findViewById(R.id.signup_female);

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CommonMethod(SignupActivity.this,male.getText().toString());
            }
        });

        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CommonMethod(SignupActivity.this,female.getText().toString());
            }
        });*/

        signup = findViewById(R.id.signup_signup);
        login = findViewById(R.id.signup_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText().toString().trim().equals("")){
                    name.setError("Name Required");
                }
                else if (email.getText().toString().trim().equals("")) {
                    email.setError("Email Id Required");
                } else if (!email.getText().toString().trim().matches(emailPattern)) {
                    email.setError("Valid Email Id Required");
                }
                else if(contact.getText().toString().trim().equals("")){
                    contact.setError("Contact No. Required");
                }
                else if(contact.getText().toString().trim().length()<10){
                    contact.setError("Valid Contact No. Required");
                }
                else if (password.getText().toString().trim().equals("")) {
                    password.setError("Password Required");
                } else if (password.getText().toString().trim().length() < 6) {
                    password.setError("Min. 6 Char Password Required");
                }else if (confirmPassword.getText().toString().trim().equals("")) {
                    confirmPassword.setError("Confirm Password Required");
                } else if (confirmPassword.getText().toString().trim().length() < 6) {
                    confirmPassword.setError("Min. 6 Char Confirm Password Required");
                }
                else if(!confirmPassword.getText().toString().trim().matches(password.getText().toString().trim())){
                    confirmPassword.setError("Password Does Not Match");
                }
                else {
                    System.out.println("Signup Successfully");
                    //Toast.makeText(MainActivity.this, "Login Successfully", Toast.LENGTH_LONG).show();
                    new CommonMethod(SignupActivity.this, "Signup Successfully");
                    //Snackbar.make(view, "Login Successfully", Snackbar.LENGTH_SHORT).show();
                    new CommonMethod(view, "Login Successfully");
                    /*Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);*/
                    onBackPressed();
                }
            }
        });
    }
}