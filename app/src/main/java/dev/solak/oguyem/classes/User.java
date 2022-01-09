package dev.solak.oguyem.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class User {

    public static Context context;
    public static dev.solak.oguyem.models.User user;

    public static void init(Context _context) {
        context = _context;
    }

    public static boolean isRegistered() {
        readUser();
        return !user.getDeviceId().isEmpty();
    }

    public static void autoRegister() {
        //  generate android_identifier as uuidv4
        String android_identifier = getUniquePseudoID();
        user.setAndroidIdentifier(android_identifier);
        Call<dev.solak.oguyem.models.User> call = API.apiService.registerDevice(user);
        call.enqueue(new Callback<dev.solak.oguyem.models.User>() {
            @Override
            public void onResponse(@NonNull Call<dev.solak.oguyem.models.User> call, @NonNull Response<dev.solak.oguyem.models.User> response) {
                int statusCode = response.code();
                Log.d("USER", "status code" + statusCode);
                if (statusCode == 201) {
                    Log.d("USER", "register success");
                    user = response.body();
                    writeUser();
                }
            }

            @Override
            public void onFailure(@NonNull Call<dev.solak.oguyem.models.User> call, @NonNull Throwable t) {
                // Log error here since request failed
                Log.d("USER", "register failed");
                t.printStackTrace();
            }
        });
    }

    private static void readUser() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        Integer id = sharedPreferences.getInt("id", -1);
        String device_id = sharedPreferences.getString("device_id", "");
        String android_identifier = sharedPreferences.getString("android_identifier", "");

        user = new dev.solak.oguyem.models.User();
        user.setId(id);
        user.setDeviceId(device_id);
        user.setAndroidIdentifier(android_identifier);
    }

    private static void writeUser() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        prefEditor.putInt("id", user.getId());
        prefEditor.putString("device_id", user.getDeviceId());
        prefEditor.putString("android_identifier", user.getAndroidIdentifier());
        prefEditor.apply();
    }

    // from https://stackoverflow.com/a/17625641/10873011
    private static String getUniquePseudoID() {
        // If all else fails, if the user does have lower than API 9 (lower
        // than Gingerbread), has reset their device or 'Secure.ANDROID_ID'
        // returns 'null', then simply the ID returned will be solely based
        // off their Android device information. This is where the collisions
        // can happen.
        // Thanks http://www.pocketmagic.net/?p=1662!
        // Try not to use DISPLAY, HOST or ID - these items could change.
        // If there are collisions, there will be overlapping data
        String m_szDevIDShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);

        // Thanks to @Roman SL!
        // https://stackoverflow.com/a/4789483/950427
        // Only devices with API >= 9 have android.os.Build.SERIAL
        // http://developer.android.com/reference/android/os/Build.html#SERIAL
        // If a user upgrades software or roots their device, there will be a duplicate entry
        String serial = null;
        try {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();

            // Go ahead and return the serial for api => 9
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            // String needs to be initialized
            serial = "serial"; // some value
        }

        // Thanks @Joe!
        // https://stackoverflow.com/a/2853253/950427
        // Finally, combine the values we have found by using the UUID class to create a unique identifier
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }
}
