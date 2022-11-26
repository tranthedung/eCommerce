package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.Model.Cart;
import com.example.ecommerce.Prevalent.Prevalent;
import com.example.ecommerce.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CardActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private AppCompatButton NextProcessBtn;
    private TextView txtTotalAmount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        NextProcessBtn = findViewById(R.id.next_process_btn);
        txtTotalAmount = findViewById(R.id.total_price);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final DatabaseReference cartlistRef = FirebaseDatabase.getInstance().getReference().child("Card List");

        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(cartlistRef.child("User View")
                                .child(Prevalent.currentOnlineUser.getPhone())
                                .child("Products"), Cart.class)
                        .build();
        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {
                        holder.txtProductQuantity.setText("Quantity = " + model.getQuantity());
                        holder.txtProductPrice.setText("Price " + model.getPrice()+"$");
                        holder.txtProductName.setText(model.getPname());


                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CharSequence options [] = new CharSequence[]{
                                        "Edit",
                                        "Remove"
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(CardActivity.this);
                                builder.setTitle("Cart Options:");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if(i ==0){
                                            Intent intent = new Intent(CardActivity.this, ProductDetailsActivity.class);
                                            intent.putExtra("pid", model.getPid());
                                            startActivity(intent);
                                        }
                                        if(i == 1){
                                            cartlistRef.child("User View")
                                                    .child(Prevalent.currentOnlineUser.getPhone())
                                                    .child("Products")
                                                    .child(model.getPid())
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Toast.makeText(CardActivity.this, "Item remove successfully.", Toast.LENGTH_SHORT).show();

                                                                Intent intent = new Intent(CardActivity.this, HomeActivity.class);
                                                                startActivity(intent);

                                                            }
                                                        }
                                                    });
                                        }

                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                        CartViewHolder holder = new CartViewHolder(view);
                        return holder;

                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }
}