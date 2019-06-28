package vyomchandra.com.completeproject;

import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Calendar;

import vyomchandra.com.completeproject.BroadcastReciver.AlarmReciver;

public class MainActivity extends AppCompatActivity {

    private EditText email,pass;
    private Button signin,btFacebook;
    private ImageButton signup;
    TextView tvLogin,tvForgot;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private CallbackManager mCallbackManager;
    String TAG="tag";






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email=findViewById(R.id.email_signin);
        pass=findViewById(R.id.password_signin);
        signup=findViewById(R.id.sign_up);
        signin=findViewById(R.id.sign_in);
        tvLogin=findViewById(R.id.tvLogin);
        tvForgot=findViewById(R.id.tvForgot);
        btFacebook=findViewById(R.id.btFacebook);

        firebaseAuth=FirebaseAuth.getInstance();

        registerAlarm();





        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
       // LoginButton loginButton = findViewById(R.id.buttonFacebookLogin);
        btFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("email","public_profile"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "facebook:onCancel");
                        // ...
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, "facebook:onError", error);
                        // ...
                    }
                });
            }
        });

        progressDialog=new ProgressDialog(MainActivity.this);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,signup.class);
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View,String> (tvLogin,"login");
                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs);
                startActivity(i,activityOptions.toBundle());

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

                progressDialog.setMessage("Processing...");
                progressDialog.show();
                firebaseAuth.signInWithEmailAndPassword(

                        emails,passs).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful()){
                           if(firebaseAuth.getCurrentUser().isEmailVerified()) {
                               Toast.makeText(MainActivity.this, "logged in", Toast.LENGTH_LONG).show();
                               startActivity(new Intent(MainActivity.this, HomeActivity.class));
                               progressDialog.dismiss();
                           }else{
                               Toast.makeText(MainActivity.this, "Please verify your email address ", Toast.LENGTH_LONG).show();
                               progressDialog.dismiss();
                           }
                       }else
                       {
                           progressDialog.dismiss();
                           Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                       }
                    }
                });
            }
        });

        tvForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(MainActivity.this,forgot_pass.class));
                Intent i=new Intent(MainActivity.this,forgot_pass.class);
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View,String> (tvLogin,"login");
                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs);
                startActivity(i,activityOptions.toBundle());

            }
        });
    }

    private void registerAlarm() {
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,22);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);

        if(calendar.getTimeInMillis()>System.currentTimeMillis()) {
            Intent intent = new Intent(MainActivity.this, AlarmReciver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) this.getSystemService(this.ALARM_SERVICE);
            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                           startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if((firebaseAuth.getCurrentUser() != null) && firebaseAuth.getCurrentUser().isEmailVerified())
        {
            startActivity(new Intent(this,HomeActivity.class));
        }
    }
}
