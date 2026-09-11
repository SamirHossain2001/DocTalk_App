package edu.ewubd.cse489.group7.doctalk;

import android.content.Context;
import android.content.SharedPreferences;

public class UserManager {
    private static final String PREF_NAME = "_DOCTALK_APP_";
    private static final String KEY_IS_LOGGED_IN = "_IS_LOGGED_IN_";
    private static final String KEY_USER_NAME = "_USER_NAME_";
    private static final String KEY_USER_PASSWORD = "_USER_PASSWORD_";
    private static final String KEY_USER_EMAIL = "_USER_EMAIL_";
    private static final String KEY_USER_PHONE = "_USER_PHONE_";
    private static final String KEY_REMEMBER_USER = "_USER_REMEMBER_";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public UserManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveUserSession(String name, String email, String password, String phone, boolean isRemembered){
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_PASSWORD, password);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_PHONE, phone);
        editor.putBoolean(KEY_REMEMBER_USER, isRemembered);
        editor.apply();
    }

    public void setLoggedIn(boolean loggedIn){
        editor.putBoolean(KEY_IS_LOGGED_IN, loggedIn);
        editor.apply();
    }

    public void checkSessionOnAppStart(){
        boolean isRemembered = getIsUserRemembered();
        boolean isLoggedIn = isLoggedIn();
        if(isLoggedIn && !isRemembered){
            setLoggedIn(false);
        }
    }

    // Logout
    public void logout(){
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.putBoolean(KEY_REMEMBER_USER, false);
        editor.apply();
    }


    // Update user details
    public void updateUserDetails(String name, String email, String password, String phone){
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_PASSWORD, password);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_PHONE, phone);
        editor.apply();
    }

    public boolean isLoggedIn(){
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserName(){
        return sharedPreferences.getString(KEY_USER_NAME, "");
    }

    public String getUserPassword(){
        return sharedPreferences.getString(KEY_USER_PASSWORD, "");
    }

    public String getUserEmail(){
        return sharedPreferences.getString(KEY_USER_EMAIL, "");
    }

    public String getUserPhone(){
        return sharedPreferences.getString(KEY_USER_PHONE, "");
    }

    public boolean getIsUserRemembered(){
        return sharedPreferences.getBoolean(KEY_REMEMBER_USER, false);
    }

    public void setIsUserRemembered(boolean checked){
        editor.putBoolean(KEY_REMEMBER_USER, checked);
        editor.apply();
    }
}
