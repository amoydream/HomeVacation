package pdasolucoes.com.br.homevacation.Util;

import android.content.DialogInterface;

import android.view.KeyEvent;


import pdasolucoes.com.br.homevacation.OpcaoEntradaActivity;

/**
 * Created by PDA on 24/10/2017.
 */

public class DialogKeyListener implements DialogInterface.OnKeyListener {
    private String uiiStr;
    private ItemEPC itemEPC;

    public interface ItemEPC {
        void onClickEpc(String epc);
    }

    public void ItemEpcListener(ItemEPC itemEPC) {
        this.itemEPC = itemEPC;
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

        if (keyCode == 139) {

            uiiStr = OpcaoEntradaActivity.mReader.inventorySingleTag();

            while (uiiStr == null) {
                uiiStr = OpcaoEntradaActivity.mReader.inventorySingleTag();
            }

            itemEPC.onClickEpc(uiiStr);

            return true;
        }
        return false;
    }
}
