package com.unc0ded.shopdeliver.repositories;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unc0ded.shopdeliver.listenerinterfaces.OnAuthenticationListener;

import java.util.Objects;

public class AuthenticationRepository {

    private static AuthenticationRepository instance;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static AuthenticationRepository getInstance(){
        if (instance == null)
            instance = new AuthenticationRepository();
        return instance;
    }

    //Login Fragment
    public void authenticateForSignIn(PhoneAuthCredential credentials, @NonNull OnAuthenticationListener listener){
        listener.onStart();
        auth.signInWithCredential(credentials).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    db.collection("Customers").document(Objects.requireNonNull(auth.getCurrentUser()).getUid()).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists() && documentSnapshot.getData() != null) {
                                    listener.onSuccess(new Throwable("customer"));
                                }
                                else {
                                    listener.onSuccess(new Throwable("vendor"));
                                }
                            })
                            .addOnFailureListener(listener::onFailure);
                }else{
                    if(task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        listener.onFailure(new Exception("WrongOTP"));
                    }else{
                        listener.onFailure(new Exception("signInWithPhone failed"));
                    }
                }
            }
        });
    }
    public void authenticateForSignIn(String email, String password, @NonNull OnAuthenticationListener listener){
        listener.onStart();
        auth.signInWithEmailAndPassword(Objects.requireNonNull(Objects.requireNonNull(email))
                , Objects.requireNonNull(password))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        db.collection("Customers").document(Objects.requireNonNull(auth.getCurrentUser()).getUid()).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists() && documentSnapshot.getData() != null) {
                                        listener.onSuccess(new Throwable("customer"));
                                    }
                                    else {
                                        listener.onSuccess(new Throwable("vendor"));
                                    }
                                })
                                .addOnFailureListener(listener::onFailure);
                    }else{
                        listener.onFailure(new Exception("signInWithEmailAndPassword failed"));
                    }
                });
    }

    //customerSignUpMain & vendorSignUpMain
    public void authenticateForSignUp(PhoneAuthCredential credential, @NonNull OnAuthenticationListener listener){
        listener.onStart();
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    listener.onSuccess(new Throwable("Authentication with phone successful"));
                }else{
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                        listener.onFailure(new Exception("WrongOTP"));
                    }else{
                        listener.onFailure(new Exception("signUpWithPhone failed"));
                    }
                }
            }
        });
    }

    //customerSignUpDetails & vendorSignUpDetails
    public void linkEmail(String email, String password, @NonNull OnAuthenticationListener listener){
        listener.onStart();
        AuthCredential emailCredential = EmailAuthProvider.getCredential(email, password);
        Objects.requireNonNull(auth.getCurrentUser()).linkWithCredential(emailCredential)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        listener.onSuccess(new Throwable("Email link success"));
                    }
                    else {
                        listener.onFailure(task.getException());
                    }
                });
    }
}
