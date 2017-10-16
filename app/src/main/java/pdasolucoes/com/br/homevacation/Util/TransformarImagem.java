package pdasolucoes.com.br.homevacation.Util;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

/**
 * Created by PDA on 15/10/2017.
 */

public class TransformarImagem {

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
        return outputStream.toByteArray();
    }
}
