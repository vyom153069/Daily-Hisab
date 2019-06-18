package vyomchandra.com.completeproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class signup extends AppCompatActivity {

    private EditText email,pass;
    private Button signin,signup;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        signup=findViewById(R.id.sign_up_in);
       signin=findViewById(R.id.sign_in);
       email=findViewById(R.id.email_signup);
       pass=findViewById(R.id.password_signup);


        firebaseAuth= FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);

        signin.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent i=new Intent(signup.this,MainActivity.class);
               startActivity(i);
           }
       });
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
               progressDialog.setMessage("processing...");
               progressDialog.show();
               firebaseAuth.createUserWithEmailAndPassword(emails,passs).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                      if(task.isSuccessful()){
                          Toast.makeText(signup.this, "complete", Toast.LENGTH_SHORT).show();
                          startActivity(new Intent(signup.this,HomeActivity.class));
                          progressDialog.dismiss();

                      }else{
                          Toast.makeText(signup.this, "registration error", Toast.LENGTH_SHORT).show();

                      }
                   }
               });
           }
       });
    }

    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
    }
}
