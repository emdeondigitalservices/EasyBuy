package com.emdeondigital.easybuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.emdeondigital.easybuy.model.SingleProductModel;
import com.emdeondigital.easybuy.products.AddToCartActivity;
import com.emdeondigital.easybuy.sessions.UserSession;
import com.emdeondigital.easybuy.users.ForgotPasswordActivity;
import com.emdeondigital.easybuy.users.RegistrationActivity;
import com.github.florent37.materialtextfield.MaterialTextField;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private TextView registernow,forgotPassword;
    EditText emailId, password, phoneNumberText,otpText;
    Button signUp, login, sendPhoneNumber,verifyBtn,viaEmail,viaPhone,resendBtn;
    FirebaseAuth mFirebaseAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private String verificationCode;
    private String UID = "";
    private UserSession session;

    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationStateChangedCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    MaterialTextField enterEmail,enterPassword,enterPhone,enterOTP;

    @Override
    protected void onStart(){
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session= new UserSession(getApplicationContext());
        checkLogin();
    }

    private void checkLogin(){
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

                if(mFirebaseUser != null){
                    UID = mFirebaseUser.getUid();
                    getUserDetails();
                }else{
                    initialize();
                }
            }

        };
    }

    private void initialize(){
        setContentView(R.layout.activity_login);

        emailId = findViewById(R.id.email);
        password = findViewById(R.id.password);
        phoneNumberText = findViewById(R.id.phone);
        otpText = findViewById(R.id.otp);
        viaEmail = findViewById(R.id.email_button);
        viaPhone = findViewById(R.id.phone_button);
        sendPhoneNumber = findViewById(R.id.send_button);
        verifyBtn = findViewById(R.id.verify_button);
        enterEmail = findViewById(R.id.enterEmail);
        enterPassword = findViewById(R.id.enterPassword);
        enterPhone = findViewById(R.id.enterPhone);
        enterOTP = findViewById(R.id.enterOTP);
        forgotPassword = findViewById(R.id.forgot_pass);
        StartFirebaseLogin();

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        viaEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterEmail.setVisibility(View.VISIBLE);
                enterPassword.setVisibility(View.VISIBLE);
                login.setVisibility(View.VISIBLE);
                enterPhone.setVisibility(View.GONE);
                enterOTP.setVisibility(View.GONE);
                sendPhoneNumber.setVisibility(View.GONE);
                verifyBtn.setVisibility(View.GONE);
            }
        });

        viaPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterPhone.setVisibility(View.VISIBLE);
                enterOTP.setVisibility(View.VISIBLE);
                sendPhoneNumber.setVisibility(View.VISIBLE);
                verifyBtn.setVisibility(View.GONE);
                enterEmail.setVisibility(View.GONE);
                enterPassword.setVisibility(View.GONE);
                login.setVisibility(View.GONE);
            }
        });

        sendPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber=phoneNumberText.getText().toString();

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,                     // Phone number to verify
                        60,                           // Timeout duration
                        TimeUnit.SECONDS,                // Unit of timeout
                        LoginActivity.this,        // Activity (for callback binding)
                        verificationStateChangedCallbacks);                      // OnVerificationStateChangedCallbacks
            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp=otpText.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, otp);
                SigninWithPhone(credential);
            }
        });

        login = (Button) findViewById(R.id.login_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();
                String pwd = password.getText().toString();
                if (email.isEmpty()) {
                    emailId.setError("Email is required");
                    emailId.requestFocus();
                } else if (!isValidEmail(email)) {
                    emailId.setError("Enter a valid email");
                    emailId.requestFocus();
                } else if (pwd.isEmpty()) {
                    password.setError("Email is required");
                    password.requestFocus();
                } else if (!(email.isEmpty() && pwd.isEmpty())) {
                    mFirebaseAuth.signInWithEmailAndPassword(email,pwd)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){

                                    }else{
                                        Toast.makeText(LoginActivity.this, "Login Error, Please Login Again",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else{
                    Toast.makeText(LoginActivity.this, "Error Occured",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        //if user wants to register
        registernow= findViewById(R.id.register_now);
        registernow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
                finish();
            }
        });
    }

    private void SigninWithPhone(PhoneAuthCredential credential) {
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //startActivity(new Intent(FirebaseLoginActivity.this,CountryDetailsActivity.class));
                            //startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                            //finish();
                        } else {
                            Toast.makeText(LoginActivity.this,"Incorrect OTP",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void StartFirebaseLogin() {

        verificationStateChangedCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(LoginActivity.this,"verification completed",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(LoginActivity.this,"verification fialed",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                Toast.makeText(LoginActivity.this,"Code sent",Toast.LENGTH_SHORT).show();
                resendToken = forceResendingToken;
            }
        };
    }

    public static boolean isValidEmail(String target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void getUserDetails(){
        DocumentReference docRef = db.collection("users").document(UID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String email = document.getData().get("email").toString();

                        String imageURL = document.getData().get("imageURL") != null ? document.getData().get("imageURL").toString() : "";
                        try {
                            //create shared preference and store data
                            session.createLoginSession(UID,"",email,"",imageURL);
                            addWishListToSession();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(LoginActivity.this,"No such document.",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void addWishListToSession(){
        DocumentReference docRef = db.collection("wishlist").document(UID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("Date for SMS : ", "DocumentSnapshot data: " + document.getData());
                        String productId = document.getData().get("productId").toString();

                        try {
                            //create shared preference and store data
                            Set<String> productSet = new HashSet<String>();
                            productSet.add(productId);
                            session.setKeyWishlistProducts(productSet);
                            addCartToSession();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    } else {
                        //Toast.makeText(LoginActivity.this,"No such document.",Toast.LENGTH_SHORT).show();
                        addCartToSession();
                    }
                } else {
                    //Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void addCartToSession(){
        db.collection("cart")
                .whereEqualTo("uid", UID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            session.setCartValue(task.getResult().size());
                            Set<String> productSet = new HashSet<String>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String productId = document.getData().get("prid").toString();
                                productSet.add(productId);
                            }
                            session.setKeyAddToCartProducts(productSet);
                        } else {
                            session.setCartValue(0);
                        }
                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                    }
                });
    }
}
