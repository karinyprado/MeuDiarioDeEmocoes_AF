package com.example.meudiariodeemocoes;

import static android.os.Build.VERSION_CODES.R;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.meudiariodeemocoes.R;
// import com.example.meudiariodeemocoes.ActivitySplashBinding; // Não usado neste exemplo simplificado
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final int SPLASH_TIMEOUT = 2500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                mAuth.signInAnonymously()
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                navigateToMain();
                            } else {
                                Toast.makeText(SplashActivity.this, "Falha no login anônimo. Verifique a conexão e configuração do Firebase.", Toast.LENGTH_LONG).show();
                                navigateToMain(); // Ou uma tela de erro
                            }
                        });
            } else {
                navigateToMain();
            }
        }, SPLASH_TIMEOUT);
    }

    private void navigateToMain() {
        Intent i = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
