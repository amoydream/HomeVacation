package pdasolucoes.com.br.homevacation;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import pdasolucoes.com.br.homevacation.Util.CustomViewGroup;
import pdasolucoes.com.br.homevacation.Util.ImageResizeUtils;

/**
 * Created by PDA on 19/10/2017.
 */

public class PopupImage extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //para status bar hide in versão 4.4.2, preciso chamar esse códig

        if (Build.VERSION.SDK_INT <= 19) {
            WindowManager manager = ((WindowManager) getApplicationContext()
                    .getSystemService(Context.WINDOW_SERVICE));

            WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
            localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
            localLayoutParams.gravity = Gravity.TOP;
            localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |

                    // this is to enable the notification to receive touch events
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                    // Draws over status bar
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

            localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            localLayoutParams.height = (int) (50 * getResources()
                    .getDisplayMetrics().scaledDensity);
            localLayoutParams.format = PixelFormat.TRANSPARENT;

            CustomViewGroup view = new CustomViewGroup(this);

            manager.addView(view, localLayoutParams);

            //fullscreen
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            checkDrawOverlayPermission();
        }
        //chamando o layout para ser criado
        setContentView(R.layout.popup_apresenta_imagem);

        //para versões acima de 4.1
        View decorView = getWindow().getDecorView();
        int uiOptions =
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        ImageView imageView = (ImageView) findViewById(R.id.imageEvidence);

        //recebendo a imagem e setando na tela
        Uri imageUri = getIntent().getParcelableExtra("imageUri");

        int w = imageView.getWidth();
        int h = imageView.getHeight();
        Bitmap bitmap = ImageResizeUtils.getResizedImage(imageUri, w, h, false);
        imageView.setImageBitmap(bitmap);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkDrawOverlayPermission() {
        /** check if we already  have permission to draw over other apps */
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(PopupImage.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1234);
            }
        } else {
            Intent intent = new Intent(PopupImage.this, Service.class);
            startService(intent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Settings.canDrawOverlays(this)) {
            // continue here - permission was granted

        }
    }
}
