package com.example.Quill.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.Quill.MainActivity;
import com.example.Quill.R;
import com.example.Quill.ReplacerActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CreateAccountFragment extends Fragment {
    private EditText nameET,emailET,passwordET,confirmPasswordET;
    private ProgressBar progressBar;
    private TextView loginTV;
    private Button signupBtn;
    private FirebaseAuth auth;
    public static final String EMAIL_REGEX ="^(.+)@(.+)$";

    public CreateAccountFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        clickListener();
    }
    private void init(View view){
        nameET=view.findViewById(R.id.nameET);
        emailET=view.findViewById(R.id.emailET);
        passwordET=view.findViewById(R.id.passwordET);
        confirmPasswordET=view.findViewById(R.id.confirmpassET);
        loginTV=view.findViewById(R.id.loginTV);
        signupBtn=view.findViewById(R.id.signupBtn);
        progressBar=view.findViewById(R.id.progressbar);


        auth=FirebaseAuth.getInstance();
    }
    private void clickListener(){
        loginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ReplacerActivity) getActivity()).setFragment(new LoginFragment());
            }
        });
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name= nameET.getText().toString();
                String email= emailET.getText().toString();
                String password= passwordET.getText().toString();
                String confirmPassword= confirmPasswordET.getText().toString();

                if(name.isEmpty() || name.equals("")){
                    nameET.setError("Please Input Valid name");
                    return;
                }
                if(email.isEmpty() || !email.matches(EMAIL_REGEX)){
                    nameET.setError("Please Input Valid email");
                    return;
                }
                if(password.isEmpty() || password.length()<6){
                    nameET.setError("Please Input Password Greater Than 6 Words");
                    return;
                }
                if(!confirmPassword.equals(password)){
                    nameET.setError("Password not match!");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                createAccount(name,email,password);
            }
        });
    }
    private void createAccount(String name ,String email,String password){
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user =auth.getCurrentUser();

                            String image="https://png.pngtree.com/png-vector/20190411/ourmid/pngtree-vector-business-men-icon-png-image_925963.jpg";

                            UserProfileChangeRequest.Builder request=new UserProfileChangeRequest.Builder();
                            request.setDisplayName(name);
                            request.setPhotoUri(Uri.parse(image));

                            user.updateProfile(request.build());

                            user.sendEmailVerification()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(getContext(), "Email verification link send", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                            uploadUser(user,name,email);
                        }else{
                            progressBar.setVisibility(View.GONE);
                            String exception=task.getException().getMessage();
                            Toast.makeText(getContext(), "Error :"+exception, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
    private void uploadUser(FirebaseUser user,String name,String email){

        List<String> list = new ArrayList<>();
        List<String> list1 = new ArrayList<>();


        Map<String ,Object> map = new HashMap<>();
        map.put("name",name);
        map.put("email",email);
        map.put("profileImage","");
        map.put("uid",user.getUid());
        map.put("status"," ");
        map.put("search",name.toLowerCase());

        map.put("followers",list);
        map.put("following",list1);

        FirebaseFirestore.getInstance().collection("Users").document(user.getUid())
                .set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            assert getActivity()!= null;
                            progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
                            getActivity().finish();
                        }else{
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Eror: "+task.getException().getMessage()
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}