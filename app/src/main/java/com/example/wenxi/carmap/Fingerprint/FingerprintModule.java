package com.example.wenxi.carmap.Fingerprint;

/**
 * Created by wenxi on 16/3/23.
 */

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.preference.PreferenceManager;
import android.security.keystore.KeyProperties;
import android.view.inputmethod.InputMethodManager;

import com.example.wenxi.carmap.Fingerprint.server.StoreBackend;
import com.example.wenxi.carmap.Fingerprint.server.StoreBackendImpl;
import com.example.wenxi.carmap.MainActivity;

import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module for Fingerprint APIs.
 */
@Module(
        library = true,
        injects = {MainActivity.class}
)
@SuppressLint("NewApi")
public class FingerprintModule {

    private final Context mContext;

    public FingerprintModule(Context context) {
        mContext = context;
    }

    @Provides
    public Context providesContext() {
        return mContext;
    }

    @Provides
    public FingerprintManager providesFingerprintManager(Context context) {
        return context.getSystemService(FingerprintManager.class);
    }

    @Provides
    public DialogFragment_lollipop dialogFragment_lollipop(Context context){
        return new DialogFragment_lollipop();
    }
    @Provides
    public KeyguardManager providesKeyguardManager(Context context) {

        return context.getSystemService(KeyguardManager.class);
    }

    @Provides
    public KeyStore providesKeystore() {
        try {
            return KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            throw new RuntimeException("Failed to get an instance of KeyStore", e);
        }
    }

    @Provides
    public KeyPairGenerator providesKeyPairGenerator() {
        try {
            return KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get an instance of KeyPairGenerator", e);
        }
    }

    @Provides
    public Signature providesSignature(KeyStore keyStore) {
        try {
            return Signature.getInstance("SHA256withECDSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to get an instance of Signature", e);
        }
    }

    @Provides
    public InputMethodManager providesInputMethodManager(Context context) {
        return (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Provides
    public SharedPreferences providesSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    public StoreBackend providesStoreBackend() {
        return new StoreBackendImpl();
    }
}

