package pdasolucoes.com.br.homevacation.Util;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by PDA on 08/02/2017.
 */

public class VerificaConexao {

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

}
