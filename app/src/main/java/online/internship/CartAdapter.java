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
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyHolder> {

    Context context;
    ArrayList<CartList> arrayList;
    SharedPreferences sp;
    SQLiteDatabase db;

    public CartAdapter(Context context, ArrayList<CartList> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        sp = context.getSharedPreferences(ConstantSp.PREF, Context.MODE_PRIVATE);

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
    public CartAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_cart, parent, false);
        return new CartAdapter.MyHolder(view);
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        ImageView imageView, deleteIv, add, remove;
        TextView name, price, total, qty;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            deleteIv = itemView.findViewById(R.id.custom_cart_delete);
            imageView = itemView.findViewById(R.id.custom_cart_image);
            name = itemView.findViewById(R.id.custom_cart_name);
            price = itemView.findViewById(R.id.custom_cart_price);
            total = itemView.findViewById(R.id.custom_cart_total);

            add = itemView.findViewById(R.id.custom_cart_plus);
            remove = itemView.findViewById(R.id.custom_cart_minus);
            qty = itemView.findViewById(R.id.custom_cart_qty);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.MyHolder holder, int position) {
        holder.imageView.setImageResource(Integer.parseInt(arrayList.get(position).getProductImage()));
        holder.name.setText(arrayList.get(position).getProductName());
        holder.price.setText(ConstantSp.PRICE_SYMBOL + arrayList.get(position).getProductPrice());
        holder.total.setText(ConstantSp.PRICE_SYMBOL + arrayList.get(position).getTotalPrice());

        holder.qty.setText(arrayList.get(position).getProductQty());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.edit().putString(ConstantSp.PRODUCT_ID, arrayList.get(position).getProductId()).commit();
                sp.edit().putString(ConstantSp.PRODUCT_NAME, arrayList.get(position).getProductName()).commit();
                sp.edit().putInt(ConstantSp.PRODUCT_IMAGE, Integer.parseInt(arrayList.get(position).getProductImage())).commit();
                sp.edit().putString(ConstantSp.PRODUCT_PRICE, arrayList.get(position).getProductPrice()).commit();
                sp.edit().putString(ConstantSp.PRODUCT_DESC, arrayList.get(position).getProductDesc()).commit();
                new CommonMethod(context, ProductDetailActivity.class);
            }
        });

        holder.deleteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String deleteQuery = "DELETE FROM CART WHERE CARTID='" + arrayList.get(position).getCartId() + "'";
                db.execSQL(deleteQuery);
                new CommonMethod(context, "Product Removed From Cart");

                CartFragment.iTotalPrice -= Integer.parseInt(arrayList.get(position).getTotalPrice());
                CartFragment.checkout.setText("Checkout " + ConstantSp.PRICE_SYMBOL + CartFragment.iTotalPrice);

                if (CartFragment.iTotalPrice == 0) {
                    CartFragment.dataLayout.setVisibility(View.GONE);
                    CartFragment.emptyLayout.setVisibility(View.VISIBLE);
                } else {
                    CartFragment.dataLayout.setVisibility(View.VISIBLE);
                    CartFragment.emptyLayout.setVisibility(View.GONE);
                }

                arrayList.remove(position);
                notifyDataSetChanged();

            }
        });

        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int iQty = Integer.parseInt(arrayList.get(position).getProductQty()) + 1;
                int iTotalPrice = Integer.parseInt(arrayList.get(position).getProductPrice()) * iQty;

                holder.qty.setText(String.valueOf(iQty));
                holder.price.setText(ConstantSp.PRICE_SYMBOL + arrayList.get(position).getProductPrice());
                holder.total.setText(ConstantSp.PRICE_SYMBOL + iTotalPrice);

                CartList list = new CartList();
                list.setCartId(arrayList.get(position).getCartId());
                list.setProductId(arrayList.get(position).getProductId());
                list.setProductName(arrayList.get(position).getProductName());
                list.setProductImage(arrayList.get(position).getProductImage());
                list.setProductDesc(arrayList.get(position).getProductDesc());
                list.setProductPrice(arrayList.get(position).getProductPrice());
                list.setProductQty(String.valueOf(iQty));
                list.setTotalPrice(String.valueOf(iTotalPrice));
                arrayList.set(position, list);

                CartFragment.iTotalPrice += Integer.parseInt(arrayList.get(position).getProductPrice());
                CartFragment.checkout.setText("Checkout " + ConstantSp.PRICE_SYMBOL + CartFragment.iTotalPrice);

                CartFragment.dataLayout.setVisibility(View.VISIBLE);
                CartFragment.emptyLayout.setVisibility(View.GONE);

                String updateQuery = "UPDATE CART SET PRODUCTQTY='"+iQty+"',TOTALPRICE='"+iTotalPrice+"' WHERE CARTID='"+arrayList.get(position).getCartId()+"'";
                db.execSQL(updateQuery);

                notifyDataSetChanged();
            }
        });

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int iQty = Integer.parseInt(arrayList.get(position).getProductQty()) - 1;
                int iTotalPrice = Integer.parseInt(arrayList.get(position).getProductPrice()) * iQty;

                holder.qty.setText(String.valueOf(iQty));
                holder.price.setText(ConstantSp.PRICE_SYMBOL + arrayList.get(position).getProductPrice());
                holder.total.setText(ConstantSp.PRICE_SYMBOL + iTotalPrice);

                CartFragment.iTotalPrice -= Integer.parseInt(arrayList.get(position).getProductPrice());
                CartFragment.checkout.setText("Checkout " + ConstantSp.PRICE_SYMBOL + CartFragment.iTotalPrice);

                if (CartFragment.iTotalPrice == 0) {
                    CartFragment.dataLayout.setVisibility(View.GONE);
                    CartFragment.emptyLayout.setVisibility(View.VISIBLE);
                } else {
                    CartFragment.dataLayout.setVisibility(View.VISIBLE);
                    CartFragment.emptyLayout.setVisibility(View.GONE);
                }

                if(iQty>0) {
                    CartList list = new CartList();
                    list.setCartId(arrayList.get(position).getCartId());
                    list.setProductId(arrayList.get(position).getProductId());
                    list.setProductName(arrayList.get(position).getProductName());
                    list.setProductImage(arrayList.get(position).getProductImage());
                    list.setProductDesc(arrayList.get(position).getProductDesc());
                    list.setProductPrice(arrayList.get(position).getProductPrice());
                    list.setProductQty(String.valueOf(iQty));
                    list.setTotalPrice(String.valueOf(iTotalPrice));
                    arrayList.set(position, list);

                    String updateQuery = "UPDATE CART SET PRODUCTQTY='"+iQty+"',TOTALPRICE='"+iTotalPrice+"' WHERE CARTID='"+arrayList.get(position).getCartId()+"'";
                    db.execSQL(updateQuery);

                }
                else{
                    String updateQuery = "DELETE FROM CART WHERE CARTID='"+arrayList.get(position).getCartId()+"'";
                    db.execSQL(updateQuery);
                    arrayList.remove(position);
                }

                notifyDataSetChanged();

            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
