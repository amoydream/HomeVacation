package pdasolucoes.com.br.homevacation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Property;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import pdasolucoes.com.br.homevacation.Model.Usuario;
import pdasolucoes.com.br.homevacation.Service.AutenticacaoService;
import pdasolucoes.com.br.homevacation.Util.CustomEditText;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private UserLoginTask mAuthTask = null;
    private CustomEditText mEmailView, mPasswordView;
    private ProgressDialog progressDialog;
    private SharedPreferences preferences;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        preferences = getSharedPreferences("Login", MODE_PRIVATE);


        progressDialog = new ProgressDialog(this);
        // Set up the login form.
        mEmailView = (CustomEditText) findViewById(R.id.email);

        mPasswordView = (CustomEditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);

        if (!preferences.getString("user", "").equals("")) {
            attemptLogin();
        } else {
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });
        }

    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        String email, password;
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        if (!preferences.getString("user", "").equals("")) {
            email = preferences.getString("user", "");
            password = preferences.getString("senha", "");
        } else {
            email = mEmailView.getText().toString();
            password = mPasswordView.getText().toString();
        }


        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 0;
    }

    private void showProgress(final boolean show) {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getString(R.string.load));
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.setCancelable(false);


        if (show) {
            progressDialog.show();
        } else if (progressDialog.isShowing() || !show) {
            progressDialog.dismiss();
        }
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        Usuario usuario = new Usuario();

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        SharedPreferences.Editor editor = preferences.edit();

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            usuario = AutenticacaoService.AutencicaoUsuario(mEmail, mPassword);

            if (!usuario.getErrorAutenticao().toString().equals("OK")) {
                return false;
            } else {
                editor.putString("user", usuario.getLogin());
                editor.putString("senha", mPassword);
                editor.putInt("idConta", usuario.getIdConta());
                editor.commit();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Intent i = new Intent(LoginActivity.this, OpcaoEntradaActivity.class);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, usuario.getErrorAutenticao(), Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

