package com.kunthu.gudangin;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

import static com.google.firebase.storage.FirebaseStorage.getInstance;


public class home extends Fragment {

    LinearLayoutManager mLayoutManager;
    SharedPreferences mSharedPref;
    RecyclerView mRecycleView;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    DatabaseReference userRef;

    FirebaseRecyclerAdapter<Model, ViewHolder> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Model> options;

    private TextView namaAkun;



    public home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView;
        mView = inflater.inflate(R.layout.fragment_home, container, false);

        ImageView inbox = mView.findViewById(R.id.pesan);

        inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), com.kunthu.gudangin.inbox.class);
                startActivity(intent);
            }
        });

        namaAkun = mView.findViewById(R.id.namaa);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null){

            String uid = mAuth.getCurrentUser().getUid();
            userRef = FirebaseDatabase.getInstance().getReference().child("User").child(uid);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        String nama = dataSnapshot.child("fullname").getValue().toString();
                        namaAkun.setText(nama);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else {

            namaAkun.setText("Pengunjung");

        }







        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle("Home");

        mSharedPref = getActivity().getSharedPreferences("SortSettings", Context.MODE_PRIVATE);
        String mSorting = mSharedPref.getString("Sort", "newest");

        if (mSorting.equals("newest")){
            mLayoutManager = new LinearLayoutManager(getContext());
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);

        }

        else if (mSorting.equals("oldest")){
            mLayoutManager = new LinearLayoutManager(getContext());
            mLayoutManager.setReverseLayout(false);
            mLayoutManager.setStackFromEnd(false);

        }

        mRecycleView = mView.findViewById(R.id.recyclerView);



        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("Data");

        showData();


        setHasOptionsMenu(true);
        return mView;
    }


    private void showDeleteDataDialog(final String currentTitle, final String currentImage) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete");
        builder.setMessage("Are you Sure Delete This Post?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Query mQuery = mRef.orderByChild("title").equalTo(currentTitle);
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                final String uid = mAuth.getCurrentUser().getUid();
                mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            String uidpost =  ds.child("uid").getValue().toString();
                            if (uid.equals(uidpost)){
                                ds.getRef().removeValue();
                                Toast.makeText(getContext(), "Post Deleted Successfully!", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getContext(), "Error, this is not your post!", Toast.LENGTH_SHORT).show();
                            }


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

                StorageReference mPictureRef = getInstance().getReferenceFromUrl(currentImage);
                mPictureRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();


    }

    private void showData(){

        options = new FirebaseRecyclerOptions.Builder<Model>().setQuery(mRef, Model.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Model, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Model model) {

                holder.setDetails(getActivity().getApplicationContext(), model.getTitle(), model.getDescription(), model.getImage());

            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {

                View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row, viewGroup,false);

                ViewHolder viewHolder = new ViewHolder(itemView);

                viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        String mTitle = getItem(position).getTitle();
                        String mDesc =  getItem(position).getDescription();
                        String mImage = getItem(position).getImage();

                        Intent intent = new Intent(view.getContext(), Detail.class);
                        intent.putExtra("title", mTitle);
                        intent.putExtra("description",mDesc);
                        intent.putExtra("image", mImage);
                        startActivity(intent);

                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                        final String cTitle = getItem(position).getTitle();
                        final String cDescr = getItem(position).getDescription();
                        final String cImage = getItem(position).getImage();
                        final String cUid = getItem(position).getUid();
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        final String uid = mAuth.getCurrentUser().getUid();


                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        String [] options = {"Update", "Delete"};

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (which==0){

                                    if (uid.equals(cUid)){

                                        Intent intent = new Intent(getContext(), add.class);
                                        intent.putExtra("cTitle", cTitle);
                                        intent.putExtra("cDescr", cDescr);
                                        intent.putExtra("cImage", cImage);
                                        startActivity(intent);

                                    }else {
                                        Toast.makeText(getContext(), "Error! this is not your post!", Toast.LENGTH_SHORT).show();
                                    }



                                }

                                if(which==1){

                                    showDeleteDataDialog(cTitle, cImage);

                                }

                            }
                        });
                        builder.create().show();

                    }
                });
                return viewHolder;
            }
        };

        mRecycleView.setLayoutManager(mLayoutManager);
        firebaseRecyclerAdapter.startListening();

        mRecycleView.setAdapter(firebaseRecyclerAdapter);

    }

    private void firebaseSearch (String searchText){

        String query = searchText.toLowerCase();

        Query firebaseSearchQuery = mRef.orderByChild("search").startAt(query).endAt(query + "\uf8ff");

        options = new FirebaseRecyclerOptions.Builder<Model>().setQuery(firebaseSearchQuery, Model.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Model, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Model model) {

                holder.setDetails(getActivity().getApplicationContext(), model.getTitle(), model.getDescription(), model.getImage());

            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row, viewGroup,false);

                ViewHolder viewHolder = new ViewHolder(itemView);

                viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        String mTitle = getItem(position).getTitle();
                        String mDesc =  getItem(position).getDescription();
                        String mImage = getItem(position).getImage();

                        Intent intent = new Intent(view.getContext(), Detail.class);
                        intent.putExtra("title", mTitle);
                        intent.putExtra("description",mDesc);
                        intent.putExtra("image", mImage);
                        startActivity(intent);

                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        final String cTitle = getItem(position).getTitle();
                        final String cDescr = getItem(position).getDescription();
                        final String cImage = getItem(position).getImage();

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        String [] options = {"Update", "Delete"};

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (which==0){

                                    Intent intent = new Intent(getContext(), add.class);
                                    intent.putExtra("cTitle", cTitle);
                                    intent.putExtra("cDescr", cDescr);
                                    intent.putExtra("cImage", cImage);
                                    startActivity(intent);

                                }

                                if(which==1){

                                    showDeleteDataDialog(cTitle, cImage);

                                }

                            }
                        });
                        builder.create().show();
                    }
                });
                return viewHolder;
            }
        };

        mRecycleView.setLayoutManager(mLayoutManager);
        firebaseRecyclerAdapter.startListening();

        mRecycleView.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();

        if (firebaseRecyclerAdapter != null){
            firebaseRecyclerAdapter.startListening();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                firebaseSearch(s);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                firebaseSearch(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_sort){

            showSortDialog();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void showSortDialog() {

        String [] sortOptions = {"Newest", "Oldest"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Sort by")
                .setIcon(R.drawable.ic_action_sort)
                .setItems(sortOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(which==0){

                            SharedPreferences.Editor editor = mSharedPref.edit();
                            editor.putString("Sort", "newest");
                            editor.apply();
                            getActivity().recreate();

                        }
                        else if (which==1){

                            SharedPreferences.Editor editor = mSharedPref.edit();
                            editor.putString("Sort", "oldest");
                            editor.apply();
                            getActivity().recreate();

                        }

                    }
                });

        builder.show();
    }
}
