package vyomchandra.com.completeproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import hotchemi.android.rate.AppRate;
import vyomchandra.com.completeproject.modal.Data;


public class HomeActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    boolean doublebackpressedOnce=false;
    boolean action=false;
    int total=0;



    private AdView mAdView;
    private InterstitialAd interstitialAd;



    //globel variable

    private String Title;
    private String Description;
    private String Budget;
    private String post_key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //toolbaar
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView=findViewById(R.id.recyler);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        //firebase
        mAuth= FirebaseAuth.getInstance();

        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("AllData").child(uid);


        //floating button
        floatingActionButton=findViewById(R.id.flotingbtn);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addData();

            }
        });

        //admob
        MobileAds.initialize(this, "R.string.app_id");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        interstitialAd=new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-4861743746184237/3074552108");
        interstitialAd.loadAd(new AdRequest.Builder().build());

        interstitialAd.setAdListener(new AdListener()


                                     {
                                         @Override
                                         public void onAdClosed() {
                                             startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                         }
                                     }

        );


        AppRate.with(this)
                .setInstallDays(2)
                .setLaunchTimes(3)
                .setRemindInterval(2)
                .monitor();
        AppRate.showRateDialogIfMeetsConditions(this);
        //AppRate.with(this).showRateDialog(this);
    }


    public void addData(){
        AlertDialog.Builder mydialoge=new AlertDialog.Builder(this);
        LayoutInflater inflater=LayoutInflater.from(this);
        View myview=inflater.inflate(R.layout.inputlayout,null);
        mydialoge.setView(myview);
        final AlertDialog dialog=mydialoge.create();

        final EditText mTitle=myview.findViewById(R.id.title);
        final EditText mDescription=myview.findViewById(R.id.description);
        final EditText mBudget=myview.findViewById(R.id.budget);
        final Button mSave=myview.findViewById(R.id.save);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title=mTitle.getText().toString().trim();
                String description=mDescription.getText().toString().trim();
                String budget=mBudget.getText().toString().trim();

                String mDate= DateFormat.getDateInstance().format(new Date());
                String id=mDatabase.push().getKey();

                if(TextUtils.isEmpty(title)||TextUtils.isEmpty(budget)||!TextUtils.isDigitsOnly(budget)) {
                    if (TextUtils.isEmpty(title)) {
                        mTitle.setError("required");
                    }
                    if (TextUtils.isEmpty(budget)) {
                        mBudget.setError("required");
                    }
                    if(!TextUtils.isDigitsOnly(budget)){
                        mBudget.setError("Should be a number");
                    }
                }
                else {
                    Data data = new Data(title, description, id, budget, mDate);
                    mDatabase.child(id).setValue(data);

                    Toast.makeText(HomeActivity.this, "Data inserted", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
        dialog.show();

    }

    private void firebaseSearch(String searchText){
        action=true;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Query firebaseSearchQuary=mDatabase.orderByChild("title").startAt(searchText.toUpperCase()).endAt(searchText.toUpperCase()+"\uf8ff");
        FirebaseRecyclerAdapter<Data,myviewHolder> adapter=new FirebaseRecyclerAdapter<Data, myviewHolder>(
                Data.class,R.layout.dataitem,myviewHolder.class,firebaseSearchQuary
        ) {
            @Override
            protected void populateViewHolder(myviewHolder viewHolder, final Data model, final int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setbudget(model.getBudget());
                viewHolder.setDate(model.getData());
                viewHolder.setDescription(model.getDescription());
                viewHolder.myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        post_key=getRef(position).getKey();
                        Title=model.getTitle();
                        Description=model.getDescription();
                        Budget=model.getBudget();
                        updateData();

                    }
                });

            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void firebaseSearchDate(){
        action=true;
        //doublebackpressedOnce=false;
        Query firebaseSearchQuary=mDatabase.orderByChild("data").startAt(DateFormat.getDateInstance().format(new Date())).endAt(DateFormat.getDateInstance().format(new Date()));
        FirebaseRecyclerAdapter<Data,myviewHolder> adapter=new FirebaseRecyclerAdapter<Data, myviewHolder>(
                Data.class,R.layout.dataitem,myviewHolder.class,firebaseSearchQuary
        ) {
            @Override
            protected void populateViewHolder(myviewHolder viewHolder, final Data model, final int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setbudget(model.getBudget());
                viewHolder.setDate(model.getData());
                viewHolder.setDescription(model.getDescription());
                viewHolder.myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        post_key=getRef(position).getKey();
                        Title=model.getTitle();
                        Description=model.getDescription();
                        Budget=model.getBudget();
                        updateData();

                    }
                });
            }
        };
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView.setAdapter(adapter);
    }
    private void firebaseSearchMonth(){
        action=true;
        Calendar c=Calendar.getInstance();
        int year=c.get(Calendar.YEAR);
        Query firebaseSearchQuary=mDatabase.orderByChild("data").startAt(1+" "+Calendar.MONTH+" "+year).endAt(DateFormat.getDateInstance().format(new Date()));
        FirebaseRecyclerAdapter<Data,myviewHolder> adapter=new FirebaseRecyclerAdapter<Data, myviewHolder>(
                Data.class,R.layout.dataitem,myviewHolder.class,firebaseSearchQuary
        ) {
            @Override
            protected void populateViewHolder(myviewHolder viewHolder, final Data model, final int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setbudget(model.getBudget());
                viewHolder.setDate(model.getData());
                viewHolder.setDescription(model.getDescription());
                viewHolder.myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        post_key=getRef(position).getKey();
                        Title=model.getTitle();
                        Description=model.getDescription();
                        Budget=model.getBudget();
                        //sum=sum+Integer.parseInt(model.getBudget());
                        updateData();

                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }



    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Data,myviewHolder> adapter=new FirebaseRecyclerAdapter<Data, myviewHolder>(
                Data.class,R.layout.dataitem,myviewHolder.class,mDatabase
        ) {
            @Override
            protected void populateViewHolder(myviewHolder viewHolder, final Data model, final int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setbudget(model.getBudget());
                viewHolder.setDate(model.getData());
                viewHolder.setDescription(model.getDescription());
                viewHolder.myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                     post_key=getRef(position).getKey();
                     Title=model.getTitle();
                     Description=model.getDescription();
                     Budget=model.getBudget();
                     updateData();

                    }
                });


            }
        };
        recyclerView.setAdapter(adapter);
    }
    public static class myviewHolder extends RecyclerView.ViewHolder{
        View myView;
        public myviewHolder(@NonNull View itemView) {
            super(itemView);
            myView=itemView;
        }
        public void setTitle(String title){
            TextView mTitle=myView.findViewById(R.id.title_item);
            mTitle.setText(title);
        }
        public void setDescription(String Description){
            TextView mDescripton=myView.findViewById(R.id.description_item);
            mDescripton.setText(Description);
        }
        public void setbudget(String Budget){
            TextView mBudget=myView.findViewById(R.id.budget_item);
            mBudget.setText("₹"+Budget);
        }
        public void setDate(String Date){
            TextView mDate=myView.findViewById(R.id.date_item);
            mDate.setText(Date);
        }

    }
    public void updateData(){
        AlertDialog.Builder myDialog=new AlertDialog.Builder(this);
        LayoutInflater inflater=LayoutInflater.from(this);
        View myView=inflater.inflate(R.layout.updatelayout,null);
        myDialog.setView(myView);
        final AlertDialog dialog=myDialog.create();

        final EditText mTitle=myView.findViewById(R.id.title_upd);
        final EditText mDescription=myView.findViewById(R.id.description_upd);
        final EditText mBudget=myView.findViewById(R.id.budget_upd);
        Button mupdate=myView.findViewById(R.id.btnUpdateUpd);
        Button mDelete=myView.findViewById(R.id.Delete);
       //we need to set server data incise edit text
        mTitle.setText(Title);
        mTitle.setSelection(Title.length());


        mDescription.setText(Description);
        mDescription.setSelection(Description.length());

        mBudget.setText(Budget);
        mBudget.setSelection(Budget.length());


        mupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Title=mTitle.getText().toString().trim();
                Description=mDescription.getText().toString().trim();
                Budget=mBudget.getText().toString().trim();
                String mDate=DateFormat.getDateInstance().format(new Date());

                if(TextUtils.isEmpty(Title)||TextUtils.isEmpty(Budget)||!TextUtils.isDigitsOnly(Budget)){
                    if(TextUtils.isEmpty(Title)){
                        mTitle.setError("Required");
                    }

                    if(TextUtils.isEmpty(Budget)){
                        mBudget.setError("Required");
                    }
                    if(!TextUtils.isDigitsOnly(Budget)){
                        mBudget.setError("should be number");
                    }
                }
                else {

                    Data data = new Data(Title, Description, post_key, Budget, mDate);
                    mDatabase.child(post_key).setValue(data);
                    dialog.dismiss();
                }
            }
        });
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child(post_key).removeValue();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu,menu);
        MenuItem menuItem=menu.findItem(R.id.search);
//        MenuItem shareitem=menu.findItem(R.id.shareItem);
//        shareActionProvider=(ShareActionProvider) shareitem.getActionProvider();



        SearchView searchView= (SearchView)MenuItemCompat.getActionView(menuItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                firebaseSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                firebaseSearch(s);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:

                AlertDialog.Builder builder=new AlertDialog.Builder(HomeActivity.this);
                builder.setMessage("Do you want to logout?");
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //mAuth.signOut();
                        FirebaseAuth.getInstance().signOut();
                        if(interstitialAd.isLoaded()){
                            interstitialAd.show();
                        }else{
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    }
                    }

                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog alert=builder.create();
                alert.setTitle("Exit");
                alert.show();
                break;
            case R.id.credits:
                Intent i=new Intent(this,credits.class);
                startActivity(i);
                break;
            case R.id.shareItem:
                Intent share=new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                String shareBody="Your body here";
                String shareSub="four subjects here";
                share.putExtra(Intent.EXTRA_SUBJECT,shareSub);
                share.putExtra(Intent.EXTRA_TEXT,shareBody);
                startActivity(share);
                break;
            case R.id.search:
                return true;

            case R.id.sort:
                showSortDialoge();
                return true;
            case android.R.id.home:
                //super.onBackPressed();
                onBackPressed();
                return true;



        }
        return super.onOptionsItemSelected(item);
    }

    private void showSortDialoge() {

        String[] sortOptions={"Today","This Month"};

        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Sort by")
                .setIcon(getResources().getDrawable(android.R.drawable.ic_menu_sort_by_size,getTheme()))
                .setCancelable(true)
                .setItems(sortOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(i==0){
                           firebaseSearchDate();


                        }
                        if(i==1){
                            firebaseSearchMonth();

                        }

                    }
                });
        builder.show();
    }

    @Override
    public void onBackPressed() {
        if(action==true){
            super.onBackPressed();
            action=false;
            //doublebackpressedOnce=false;
        }
        else if(doublebackpressedOnce){
            finishAffinity();
        }
        else {
            this.doublebackpressedOnce = true;
            Toast.makeText(this, "Please press back again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doublebackpressedOnce = false;
                }
            }, 2000);
        }
    }
}

