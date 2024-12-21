package com.hmir.goodfood.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

/**
 * Utility class for network-related operations.
 * Provides methods to check network connectivity and internet availability.
 */
public class NetworkUtil {
    /**
     * Checks if the device has an active and valid internet connection.
     * This method verifies both the presence of a network connection
     * and its capability to access the internet.
     *
     * @param context The application context needed to access system services
     * @return boolean Returns true if internet is available and validated, false otherwise
     */
    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                return networkCapabilities != null &&
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
            }
        }
        return false;
    }
}

