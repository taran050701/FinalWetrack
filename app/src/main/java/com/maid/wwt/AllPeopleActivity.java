package com.maid.wwt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.maid.wwt.Interface.IFirebaseLoadDone;
import com.maid.wwt.Interface.IRecyclerItemClickListener;
import com.maid.wwt.Model.MyResponse;
import com.maid.wwt.Model.Request;
import com.maid.wwt.Model.User;
import com.maid.wwt.Remote.IFCMService;
import com.maid.wwt.Utils.Common;
import com.maid.wwt.ViewHolder.UserViewHolder;
import com.mancj.materialsearchbar.MaterialSearchBar;
import io.reactivex.disposables.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AllPeopleActivity extends AppCompatActivity implements IFirebaseLoadDone {
    FirebaseRecyclerAdapter<User, UserViewHolder>adapter,searchAdapter;
    RecyclerView recycler_all_user;
    IFirebaseLoadDone firebaseLoadDone;

    MaterialSearchBar searchBar;
    List<String> suggestList=new ArrayList<>();

    IFCMService ifcmService;
    CompositeDisposable compositeDisposable=new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_people);

        //init api
        ifcmService=Common.getFCMService();

        //init View
        searchBar=(MaterialSearchBar)findViewById(R.id.material_search_bar);
        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> suggest = new ArrayList<>();
                for(String search:suggestList){
                    if(search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                searchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if(!enabled){
                    if (adapter!=null){
                        //if close search restore default
                        recycler_all_user.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text.toString());
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        recycler_all_user=(RecyclerView)findViewById(R.id.recycler_all_people);
        recycler_all_user.setHasFixedSize(true);


        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(this);
        recycler_all_user.setLayoutManager(layoutManager);
        recycler_all_user.addItemDecoration(new DividerItemDecoration(this,((LinearLayoutManager)layoutManager)
                .getOrientation()));


        firebaseLoadDone=this;

        loadUserList();
        loadSearchData();

    }

    private void loadSearchData() {
         final List<String> lstUserEmail=new ArrayList<>();
        DatabaseReference userList=FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION);
        userList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()){
                    User user =userSnapshot.getValue(User.class);
                    lstUserEmail.add(user.getEmail());
                }
                firebaseLoadDone.onFirebaseLoadUserNameDone(lstUserEmail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                firebaseLoadDone.onFirebaseLoadFailed(databaseError.getMessage());
            }
        });
    }

    private void loadUserList() {

        Query query=FirebaseDatabase.getInstance().getReference().child(Common.USER_INFORMATION);
        FirebaseRecyclerOptions<User> options=new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query,User.class).build();

        adapter=new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User model) {
                if (model.getEmail().equals(Common.loggedUser.getEmail())){
                    holder.txt_user_email.setText(new StringBuilder(model.getEmail()).append("Me"));
                    holder.txt_user_email.setTypeface(holder.txt_user_email.getTypeface(), Typeface.ITALIC);
                }else {
                    holder.txt_user_email.setText(new StringBuilder(model.getEmail()));
                }
                //event
                holder.setiRecyclerItemClickListener(new IRecyclerItemClickListener() {
                    @Override
                    public void onItemClickListener(View view, int position1) {
                        showDialogRequest(model);
                    }
                });
            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView=LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_user,viewGroup,false);
                return new UserViewHolder(itemView);
            }
        };

        adapter.startListening();
        recycler_all_user.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        if (adapter != null)
            adapter.stopListening();
        if (searchAdapter != null)
            searchAdapter.stopListening();

        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter!=null)
            adapter.startListening();
        if (searchAdapter!=null)
            searchAdapter.startListening();

    }

    private void startSearch(String text_search) {
        Query query=FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION)
                .orderByChild("name")
                .startAt(text_search);


        FirebaseRecyclerOptions<User> options=new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query,User.class)
                .build();

        searchAdapter=new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User model) {
                if (model.getEmail().equals(Common.loggedUser.getEmail())){
                    holder.txt_user_email.setText(new StringBuilder(model.getEmail()).append("Me"));
                    holder.txt_user_email.setTypeface(holder.txt_user_email.getTypeface(), Typeface.ITALIC);
                }else {
                    holder.txt_user_email.setText(new StringBuilder(model.getEmail()));
                }
                //event
                holder.setiRecyclerItemClickListener((view, position1) -> {
                    showDialogRequest(model);

                });
            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView=LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_user,viewGroup,false);
                return new UserViewHolder(itemView);
            }
        };

        searchAdapter.startListening();
        recycler_all_user.setAdapter(searchAdapter);
    }

    private void showDialogRequest(User model) {
        AlertDialog.Builder alertDialog= new AlertDialog.Builder(this,R.style.MyRequestDialog);
        alertDialog.setTitle("Request Friend");
        alertDialog.setMessage("Do you want to send request friend"+model.getEmail());
        alertDialog.setIcon(R.drawable.ic_menu_vector);

        alertDialog.setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss());
        alertDialog.setPositiveButton("SEND", (dialog, which) -> {
            //Add to accept List
            DatabaseReference acceptList= FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION)
                    .child(Common.loggedUser.getUid())
                    .child(Common.ACCEPT_LIST);
            acceptList.orderByKey().equalTo(model.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue()==null)//if not friend before
                                sendFriendRequest(model);
                            else
                            Toast.makeText(AllPeopleActivity.this, "You and "+model.getEmail()+" Already are friend", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        });
        alertDialog.show();
    }

    private void sendFriendRequest(User model) {
        //get tokens to send
        DatabaseReference tokens= FirebaseDatabase.getInstance().getReference(Common.TOKENS);
        tokens.orderByKey().equalTo(model.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()==null)
                            Toast.makeText(AllPeopleActivity.this,"Token Error",Toast.LENGTH_SHORT).show();
                        else
                        {
                            //create Request
                            Request request=new Request();

                            //create data
                            Map<String,String> dataSend= new HashMap<>();
                            dataSend.put(Common.FROM_UID,Common.loggedUser.getUid());
                            dataSend.put(Common.FROM_NAME,Common.loggedUser.getEmail());
                            dataSend.put(Common.TO_UID,model.getUid());
                            dataSend.put(Common.TO_NAME,model.getEmail());

                            request.setTo(dataSnapshot.child(model.getUid()).getValue(String.class));
                            request.setData(dataSend);

                            //send
                            compositeDisposable.add(ifcmService.sendFriendRequestToUser(request)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<MyResponse>(){

                                        @Override
                                        public void accept(MyResponse myResponse) throws Exception {
                                            if (myResponse.success==1)
                                                Toast.makeText(AllPeopleActivity.this,"Request Sent!",Toast.LENGTH_SHORT).show();
                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            Toast.makeText(AllPeopleActivity.this,throwable.getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                                        }));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onFirebaseLoadUserNameDone(List<String> lstEmail) {
        searchBar.setLastSuggestions(lstEmail);
    }

    @Override
    public void onFirebaseLoadFailed(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}