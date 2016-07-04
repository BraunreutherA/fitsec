package secureapps.com.fitsec;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


/**
 * Created by Sarah on 03.07.2016.
 */
public class LoginActivity extends AppCompatActivity {
    private Button loginButton;
    private EditText pinText;

    private KeyguardManager keyguardManager;

    private static final int LOGIN_INTENT_CODE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lock_screen);

        keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

        Intent intent  = keyguardManager.createConfirmDeviceCredentialIntent("Test", "Test");
        startActivityForResult(intent, LOGIN_INTENT_CODE);

        loginButton = (Button)findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_INTENT_CODE) {
            Log.e("LOCK", "Result" + resultCode);
            // Challenge completed, proceed with using cipher
            if (resultCode == RESULT_OK) {

            } else {

            }
        }
    }

    @Override
    public void onBackPressed(){
        // disable going back to the MainActivity
        //moveTaskToBack(true);
    }

    private void login() {

    }



}
