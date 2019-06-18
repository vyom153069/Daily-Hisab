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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText email,pass;
    private Button signup,signin;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email=findViewById(R.id.email_in);
        pass=findViewById(R.id.password_signin);
        signup=findViewById(R.id.sign_up);
        signin=findViewById(R.id.sign_in);
        FirebaseApp.initializeApp(this);
        firebaseAuth= FirebaseAuth.getInstance();

       if(firebaseAuth.getCurrentUser()!=null)
        {
            startActivity(new Intent(this,HomeActivity.class));
        }
        progressDialog=new ProgressDialog(MainActivity.this);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,signup.class);
                startActivity(i);
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
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
                firebaseAuth.signInWithEmailAndPassword(

                        emails,passs).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful()){
                           Toast.makeText(MainActivity.this, "complete", Toast.LENGTH_SHORT).show();
                           startActivity(new Intent(MainActivity.this,HomeActivity.class));
                           progressDialog.dismiss();
                       }else
                       {
                           Toast.makeText(MainActivity.this, "Please register first", Toast.LENGTH_SHORT).show();
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
