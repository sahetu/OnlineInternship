package online.internship;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class CartFragment extends Fragment {

    RecyclerView recyclerView;
    SharedPreferences sp;
    SQLiteDatabase db;

    ArrayList<CartList> arrayList;
    public static Button checkout;
    public static int iTotalPrice = 0;

    public static RelativeLayout dataLayout,emptyLayout;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        db = getActivity().openOrCreateDatabase("Online_Internship", Context.MODE_PRIVATE, null);

        String tableQuery = "CREATE TABLE IF NOT EXISTS USERS(USERID INTEGER PRIMARY KEY AUTOINCREMENT,NAME VARCHAR(100),EMAIL VARCHAR(100),CONTACT INT(10),PASSWORD VARCHAR(20),GENDER VARCHAR(6),CITY VARCHAR(50),DOB VARCHAR(10))";
        db.execSQL(tableQuery);

        String cartTableQuery = "CREATE TABLE IF NOT EXISTS CART(CARTID INTEGER PRIMARY KEY AUTOINCREMENT,ORDERID INTEGER(10),USERID INTEGER(10),PRODUCTID INTEGER(10),PRODUCTNAME VARCHAR(100),PRODUCTIMAGE VARCHAR(100),PRODUCTDESCRIPTION TEXT,PRODUCTPRICE VARCHAR(20),PRODUCTQTY INTEGER(10),TOTALPRICE VARCHAR(20))";
        db.execSQL(cartTableQuery);

        String wishlistTableQuery = "CREATE TABLE IF NOT EXISTS WISHLIST(WISHLISTID INTEGER PRIMARY KEY AUTOINCREMENT,USERID INTEGER(10),PRODUCTID INTEGER(10),PRODUCTNAME VARCHAR(100),PRODUCTIMAGE VARCHAR(100),PRODUCTDESCRIPTION TEXT,PRODUCTPRICE VARCHAR(20))";
        db.execSQL(wishlistTableQuery);

        sp = getActivity().getSharedPreferences(ConstantSp.PREF,Context.MODE_PRIVATE);

        checkout = view.findViewById(R.id.cart_checkout);

        dataLayout = view.findViewById(R.id.cart_data_layout);
        emptyLayout = view.findViewById(R.id.cart_empty_layout);

        recyclerView = view.findViewById(R.id.cart_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        String selectQuery = "SELECT * FROM CART WHERE USERID='"+sp.getString(ConstantSp.ID,"")+"'";
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.getCount()>0){
            dataLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);

            arrayList = new ArrayList<>();
            while (cursor.moveToNext()){
                CartList list = new CartList();
                list.setCartId(cursor.getString(0));
                list.setProductId(cursor.getString(3));
                list.setProductName(cursor.getString(4));
                list.setProductImage(cursor.getString(5));
                list.setProductDesc(cursor.getString(6));
                list.setProductPrice(cursor.getString(7));
                list.setProductQty(cursor.getString(8));
                list.setTotalPrice(cursor.getString(9));
                arrayList.add(list);

                iTotalPrice += Integer.parseInt(cursor.getString(9));

                //iTotalPrice = iTotalPrice+Integer.parseInt(cursor.getString(9));

            }
            CartAdapter adapter = new CartAdapter(getActivity(),arrayList);
            recyclerView.setAdapter(adapter);

            checkout.setText("Checkout "+ConstantSp.PRICE_SYMBOL+iTotalPrice);

        }
        else{
            dataLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CommonMethod(getActivity(),ShippingActivity.class);
            }
        });

        return view;
    }
}