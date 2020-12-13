package com.wusiko.game2048.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.wusiko.game2048.R;
import com.wusiko.game2048.ui.game.GameActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText mUsernameEditText = null;
    private EditText mPasswordEditText = null;
    private Button mLoginButton = null;
    private ProgressBar mLoadingProgressBar = null;
    private LoginViewModel loginViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory()).get(LoginViewModel.class);
        loginViewModel.setActivity(this);

        mUsernameEditText = findViewById(R.id.username);
        mPasswordEditText = findViewById(R.id.password);
        mLoginButton = findViewById(R.id.login);
        mLoadingProgressBar = findViewById(R.id.loading);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                mLoginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    mUsernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    mPasswordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                setLoginInProcess(false);
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                    startGame();
                }
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(mUsernameEditText.getText().toString(),
                        mPasswordEditText.getText().toString());
            }
        };
        mUsernameEditText.addTextChangedListener(afterTextChangedListener);
        mPasswordEditText.addTextChangedListener(afterTextChangedListener);
        mPasswordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    tryLogin();
                }
                return false;
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLogin();
            }
        });
    }

    private void startGame() {
        setResult(Activity.RESULT_OK);
        finish();
        Intent intent = new Intent(this, GameActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome).replace("%username%", model.getDisplayName());
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_SHORT).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void setLoginInProcess(final boolean value) {
        mLoginButton.setEnabled(!value);
        mUsernameEditText.setEnabled(!value);
        mPasswordEditText.setEnabled(!value);

        if (value) {
            mLoadingProgressBar.setVisibility(View.VISIBLE);
        } else {
            mLoadingProgressBar.setVisibility(View.GONE);
        }
    }

    private void tryLogin() {
        if (loginViewModel.isLoggedIn()) {
            loginViewModel.logout();
        }

        setLoginInProcess(true);
        loginViewModel.login(
                mUsernameEditText.getText().toString(),
                mPasswordEditText.getText().toString());
    }
}