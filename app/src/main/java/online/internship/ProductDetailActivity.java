package online.internship;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class ProductDetailActivity extends AppCompatActivity implements PaymentResultListener {

    ImageView imageView;
    TextView name,price,desc;
    Button buyNow;
    SharedPreferences sp;
    Checkout checkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        sp = getSharedPreferences(ConstantSp.PREF,MODE_PRIVATE);

        name = findViewById(R.id.product_detail_name);
        imageView = findViewById(R.id.product_detail_image);
        price = findViewById(R.id.product_detail_price);
        desc = findViewById(R.id.product_detail_desc);
        buyNow = findViewById(R.id.product_detail_buy_now);

        name.setText(sp.getString(ConstantSp.PRODUCT_NAME,""));
        imageView.setImageResource(sp.getInt(ConstantSp.PRODUCT_IMAGE,0));
        price.setText(ConstantSp.PRICE_SYMBOL+sp.getString(ConstantSp.PRODUCT_PRICE,""));

        desc.setText(sp.getString(ConstantSp.PRODUCT_DESC,""));

        Checkout.preload(getApplicationContext());

        checkout = new Checkout();
        checkout.setKeyID("rzp_test_xsiOz9lYtWKHgF");

        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPayment();
            }
        });

    }

    private void startPayment() {
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            options.put("name", "Merchant Name");
            options.put("description", "Reference No. #123456");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg");
            options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", "50000");//pass amount in currency subunits
            options.put("prefill.email", "gaurav.kumar@example.com");
            options.put("prefill.contact","9988776655");
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            checkout.open(activity, options);

        } catch(Exception e) {
            Log.e("RESPONSE", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        new CommonMethod(ProductDetailActivity.this,"Payment Success");
    }

    @Override
    public void onPaymentError(int i, String s) {
        new CommonMethod(ProductDetailActivity.this,"Payment Failed");
    }
}