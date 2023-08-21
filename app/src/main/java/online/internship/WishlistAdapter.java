package online.internship;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.MyHolder> {

    Context context;
    ArrayList<WishlistList> arrayList;
    SharedPreferences sp;
    SQLiteDatabase db;

    public WishlistAdapter(Context context, ArrayList<WishlistList> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        sp = context.getSharedPreferences(ConstantSp.PREF,Context.MODE_PRIVATE);

        db = context.openOrCreateDatabase("Online_Internship", Context.MODE_PRIVATE, null);

        String tableQuery = "CREATE TABLE IF NOT EXISTS USERS(USERID INTEGER PRIMARY KEY AUTOINCREMENT,NAME VARCHAR(100),EMAIL VARCHAR(100),CONTACT INT(10),PASSWORD VARCHAR(20),GENDER VARCHAR(6),CITY VARCHAR(50),DOB VARCHAR(10))";
        db.execSQL(tableQuery);

        String cartTableQuery = "CREATE TABLE IF NOT EXISTS CART(CARTID INTEGER PRIMARY KEY AUTOINCREMENT,ORDERID INTEGER(10),USERID INTEGER(10),PRODUCTID INTEGER(10),PRODUCTNAME VARCHAR(100),PRODUCTIMAGE VARCHAR(100),PRODUCTDESCRIPTION TEXT,PRODUCTPRICE VARCHAR(20),PRODUCTQTY INTEGER(10),TOTALPRICE VARCHAR(20))";
        db.execSQL(cartTableQuery);

        String wishlistTableQuery = "CREATE TABLE IF NOT EXISTS WISHLIST(WISHLISTID INTEGER PRIMARY KEY AUTOINCREMENT,USERID INTEGER(10),PRODUCTID INTEGER(10),PRODUCTNAME VARCHAR(100),PRODUCTIMAGE VARCHAR(100),PRODUCTDESCRIPTION TEXT,PRODUCTPRICE VARCHAR(20))";
        db.execSQL(wishlistTableQuery);

    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_wishlist, parent, false);
        return new MyHolder(view);
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        ImageView imageView,deleteIv;
        TextView name,price;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            deleteIv = itemView.findViewById(R.id.custom_wishlist_delete);
            imageView = itemView.findViewById(R.id.custom_wishlist_image);
            name = itemView.findViewById(R.id.custom_wishlist_name);
            price = itemView.findViewById(R.id.custom_wishlist_price);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.imageView.setImageResource(Integer.parseInt(arrayList.get(position).getProductImage()));
        holder.name.setText(arrayList.get(position).getProductName());
        holder.price.setText(ConstantSp.PRICE_SYMBOL+arrayList.get(position).getProductPrice());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.edit().putString(ConstantSp.PRODUCT_ID,arrayList.get(position).getProductId()).commit();
                sp.edit().putString(ConstantSp.PRODUCT_NAME,arrayList.get(position).getProductName()).commit();
                sp.edit().putInt(ConstantSp.PRODUCT_IMAGE, Integer.parseInt(arrayList.get(position).getProductImage())).commit();
                sp.edit().putString(ConstantSp.PRODUCT_PRICE,arrayList.get(position).getProductPrice()).commit();
                sp.edit().putString(ConstantSp.PRODUCT_DESC,arrayList.get(position).getProductDesc()).commit();
                new CommonMethod(context, ProductDetailActivity.class);
            }
        });

        holder.deleteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String deleteQuery = "DELETE FROM WISHLIST WHERE WISHLISTID='"+arrayList.get(position).getWishlistId()+"'";
                db.execSQL(deleteQuery);
                new CommonMethod(context,"Product Removed From Wishlist");
                arrayList.remove(position);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
