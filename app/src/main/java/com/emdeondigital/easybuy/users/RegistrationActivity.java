package com.emdeondigital.easybuy.users;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.emdeondigital.easybuy.LoginActivity;
import com.emdeondigital.easybuy.MainActivity;
import com.emdeondigital.easybuy.R;
import com.emdeondigital.easybuy.util.GeneralMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity {

    ImageView upload;
    CircleImageView image;
    EditText emailId, password;
    // Image request code for onActivityResult() .
    int Image_Request_Code = 7;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0;
    // Creating URI.
    Uri FilePathUri;
    Button signUp, login;
    FirebaseAuth mFirebaseAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Folder path for Firebase Storage.
    String Storage_Path = "All_Image_Uploads/";
    String imageURL,mobileNum;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    String UID = "";
    AppCompatCheckBox showHidePassword;
    TextView passwordHelp,login_now;
    AlertDialog.Builder builder;
    MaterialEditText mobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mFirebaseAuth = FirebaseAuth.getInstance();

        Typeface typeface = ResourcesCompat.getFont(this, R.font.blacklist);
        TextView appname = findViewById(R.id.appname);
        appname.setTypeface(typeface);

        // Assigning Id to ProgressDialog.
        progressDialog = new ProgressDialog(RegistrationActivity.this);
        upload=findViewById(R.id.uploadpic);
        image=findViewById(R.id.profilepic);
        emailId = findViewById(R.id.email);
        mobileNumber = findViewById(R.id.mobileNumber);
        password = findViewById(R.id.password);
        showHidePassword = (AppCompatCheckBox) findViewById(R.id.showHidePassword);

        showHidePassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    // show password
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    // hide password
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        passwordHelp = findViewById(R.id.passwordHelp);
        builder = new AlertDialog.Builder(this,R.style.CustomDialog);
        passwordHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Uncomment the below code to Set the message and title from the strings.xml file
                builder.setMessage(R.string.password_message) .setTitle(R.string.password_title)

                        .setCancelable(false)
                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'OK' Button
                                dialog.cancel();
                            }
                        });

                //Creating dialog box
                AlertDialog alert = builder.create();
                //2. now setup to change color of the button
                /*alert.setOnShowListener( new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface arg0) {
                        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.gen_black));
                    }
                });*/
                alert.show();
            }
        });
        // Assign FirebaseStorage instance to storageReference.
        storageReference = FirebaseStorage.getInstance().getReference();

        // Adding click listener to Choose image button.
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Creating intent.
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);
            }
        });

        login_now = findViewById(R.id.login_now);
        login_now.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                                             startActivity(i);
                                             finish();
                                         }
                                     });


        signUp = (Button) findViewById(R.id.register);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Setting progressDialog Title.
                progressDialog.setTitle("Registration in process...");
                // Showing progressDialog.
                progressDialog.show();
                String email = emailId.getText().toString();
                String pwd = password.getText().toString();
                if (email.isEmpty()) {
                    // Hiding the progressDialog after done uploading.
                    progressDialog.dismiss();
                    emailId.setError("Email is required");
                    emailId.requestFocus();
                } else if (!isValidEmail(email)) {
                    // Hiding the progressDialog after done uploading.
                    progressDialog.dismiss();
                    emailId.setError("Enter a valid email");
                    emailId.requestFocus();
                } else if (pwd.isEmpty() || pwd.length()<8 || !isValidPassword(pwd)) {
                    // Hiding the progressDialog after done uploading.
                    progressDialog.dismiss();
                    password.setError("Password is Invalid");
                    password.requestFocus();
                } else if (!(email.isEmpty() && pwd.isEmpty())) {
                    mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).
                            addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                                                      @Override
                                                      public void onComplete(@NonNull Task<AuthResult> task) {
                                                          if (!task.isSuccessful()) {
                                                              // Hiding the progressDialog after getting result.
                                                              progressDialog.dismiss();
                                                              Toast.makeText(RegistrationActivity.this, "Sign Up Unsuccessful",
                                                                      Toast.LENGTH_SHORT).show();
                                                          } else {
                                                              UID = task.getResult().getUser().getUid();
                                                              UploadImageFileToFirebaseStorage();
                                                          }
                                                      }
                                                  }
                            );
                }else{
                    // Hiding the progressDialog after done uploading.
                    progressDialog.dismiss();
                    Toast.makeText(RegistrationActivity.this, "Error Occured",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {
            FilePathUri = data.getData();
            try {
                Glide.with(this)
                        .load(FilePathUri.toString())
                        .into(image);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isValidEmail(String target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    //*****************************************************************
    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    public void register(){
        //Validations
            // Create a new user with a first and last name
            Map<String, Object> user = new HashMap<>();
            user.put("uid", UID);
            user.put("email", emailId.getText().toString());
            user.put("imageURL", imageURL);
            if(mobileNumber.getText() != null) {
                user.put("mobile", mobileNumber.getText().toString());
                mobileNum = mobileNumber.getText().toString();
            }
            try {
                // Add a new document with a generated ID
                db.collection("users").document(UID)
                        .set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if(mobileNumber.getText() != null) {
                                    //sendSMSMessage();
                                }
                                // Hiding the progressDialog after done uploading.
                                progressDialog.dismiss();
                                //Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                                startActivity(i);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Hiding the progressDialog after done uploading.
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_LONG).show();
                            }
                        });
            } catch (Exception e) {
                // Hiding the progressDialog after done uploading.
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Failure"+e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

    }

    // Creating UploadImageFileToFirebaseStorage method to upload image on storage.
    public void UploadImageFileToFirebaseStorage() {

        // Checking whether FilePathUri Is empty or not.
        if (FilePathUri != null) {
            String usersPath = "Users/"+UID+"/";

            // Creating second StorageReference.
            final StorageReference storageReference2nd = storageReference.child(Storage_Path +usersPath+ System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));

            // Adding addOnSuccessListener to second StorageReference.
            storageReference2nd.putFile(FilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            storageReference2nd.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final Uri downloadUrl = uri;
                                    imageURL = downloadUrl.toString();
                                    // Showing toast message after done uploading.
                                    Toast.makeText(getApplicationContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();
                                    register();
                                }
                            });


                        }
                    })
                    // If something goes wrong .
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Hiding the progressDialog.
                            progressDialog.dismiss();

                            // Showing exception erro message.
                            Toast.makeText(RegistrationActivity.this, "Exception is this: "+exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })

                    // On progress change upload time.
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            // Setting progressDialog Title.
                            progressDialog.setTitle("Image is Uploading...");
                        }
                    });
        }
        else {
            imageURL = null;
            register();
        }
    }

    // Creating Method to get the selected image file Extension from File Path URI.
    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }

    protected void sendSMSMessage() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    GeneralMethods.sendSMS(mobileNum, getString(R.string.registrationCompleted), null, null, null);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }

}
