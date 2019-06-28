package vyomchandra.com.completeproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class
signup extends AppCompatActivity {

    private EditText email,pass,confirm;
    private Button signup;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private RelativeLayout rlayout;
    private Animation animation;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        signup=findViewById(R.id.sign_up_in);
        //signin=findViewById(R.id.sign_in);
        email=findViewById(R.id.email_signup);
        pass=findViewById(R.id.password_signup);
        confirm=findViewById(R.id.confirm_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        rlayout     = findViewById(R.id.rlayout);
        animation   = AnimationUtils.loadAnimation(this,R.anim.uptodown);
        rlayout.setAnimation(animation);




        firebaseAuth= FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);

//        signin.setOnClickListener(new View.OnClickListener() {
//           @Override
//           public void onClick(View view) {
//               Intent i=new Intent(signup.this,MainActivity.class);
//               startActivity(i);
//           }
//       });
       signup.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String emails=email.getText().toString().trim();
               String passs=pass.getText().toString().trim();
               if(TextUtils.isEmpty(emails)){
                   email.setError("required");
                   return;

               }
               if(TextUtils.isEmpty(passs)){
                   pass.setError("required");
                   return;
               }
               progressDialog.setMessage("Processing...");
               progressDialog.show();
               firebaseAuth.createUserWithEmailAndPassword(emails,passs).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                      if(task.isSuccessful()){

                          firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {
                               if(task.isSuccessful()){
                                   Toast.makeText(signup.this, "Registered Successfully please check email for verification", Toast.LENGTH_LONG).show();
                                   email.setText("");
                                   pass.setText("");
                                   confirm.setText("");
                               }
                               else
                               {
                                   Toast.makeText(signup.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                               }
                              }
                          });
                          progressDialog.dismiss();

                      }else{
                          Toast.makeText(signup.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                          progressDialog.dismiss();
                      }
                   }
               });
           }
       });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
