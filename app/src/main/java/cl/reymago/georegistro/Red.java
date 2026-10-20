package cl.reymago.georegistro;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

public class Red {

    public static boolean hayInternet(Context contexto) {
        ConnectivityManager cm = (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network red = cm.getActiveNetwork();
        if (red == null) {
            return false;
        }
        NetworkCapabilities capacidades = cm.getNetworkCapabilities(red);
        return capacidades != null
                && capacidades.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }
}