package vyomchandra.com.completeproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.DateFormat;
import java.util.Date;

import vyomchandra.com.completeproject.modal.Data;


public class HomeActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private ShareActionProvider shareActionProvider;


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
        Query firebaseSearchQuary=mDatabase.orderByChild("title").startAt(searchText).endAt(searchText+"\uf8ff");
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

//    private void setShareIntent(Intent shareIntent){
//        if(shareActionProvider!=null){
//            shareActionProvider.setShareIntent(shareIntent);
//        }
//    }

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
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mAuth.signOut();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
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

                //mAuth.signOut();

                // finish();
                break;
            case R.id.credits:
                //startActivity(new Intent(this,credits.class));
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
                Toast.makeText(this, "share", Toast.LENGTH_SHORT).show();
                break;
            case R.id.search:
                Toast.makeText(this, "search", Toast.LENGTH_SHORT).show();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}

