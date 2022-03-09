package Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.handyapp_v2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    EditText address, password;
    Button update;
    String Password;
    String Address;
    String oldpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        address = findViewById(R.id.address);
        password = findViewById(R.id.password);
        update = findViewById(R.id.update);

        getOldPassword();

    }

    private void UpdatePassword(String password, String address, String oldpass) {
        FirebaseUser user;
        user = FirebaseAuth.getInstance().getCurrentUser();
        final String email = user.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(email, oldpass);

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    user.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(EditProfileActivity.this, "Something went wrong. Please try again later", Toast.LENGTH_SHORT).show();
                            } else {
                                UpdateAddress();
                                Toast.makeText(EditProfileActivity.this, "Password Successfully Modified", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(EditProfileActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void UpdateAddress() {
        FirebaseFirestore firestore;
        firestore = FirebaseFirestore.getInstance();

        HashMap<String, Object> map = new HashMap<>();
        map.put("Address", address.getText().toString());
        map.put("Password", password.getText().toString());

        firestore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Address updated", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

    }

    private void getOldPassword() {
        FirebaseAuth firebaseAuth;
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore;
        firebaseFirestore = FirebaseFirestore.getInstance();

        String firebaseUser = firebaseAuth.getCurrentUser().getUid();


        firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot dataSnapshot = task.getResult();
                            if (dataSnapshot.exists()) {
                                oldpass = dataSnapshot.get("Password").toString();
                                String userAddress = dataSnapshot.get("Address").toString();

                                address.setText(userAddress);
                                update.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Password = password.getText().toString();
                                        Address = address.getText().toString();
                                        if (Password.length() <= 5) {
                                            Toast.makeText(EditProfileActivity.this, "password should be minimum 6 characters long", Toast.LENGTH_SHORT).show();
                                        } else if (Password.equals("")) {
                                            Toast.makeText(EditProfileActivity.this, "Write password", Toast.LENGTH_SHORT).show();
                                        } else if (Address.equals("")) {
                                            Toast.makeText(EditProfileActivity.this, "Write Address", Toast.LENGTH_SHORT).show();
                                        } else {

                                            UpdatePassword(Password, Address, oldpass);
                                        }
                                    }
                                });

//                                Toast.makeText(getActivity(), "getprofile data", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(EditProfileActivity.this, "Error While getting data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }
}