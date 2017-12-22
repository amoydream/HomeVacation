package pdasolucoes.com.br.homevacation.Model;

import com.zebra.rfid.api3.Constants;

import java.util.ArrayList;
import java.util.Collection;

import static com.zebra.rfid.api3.Constants.TYPE_DEBUG;
import static pdasolucoes.com.br.homevacation.application.Constants.logAsMessage;

/**
 * Created by PDA Kleiton on 19/12/2017.
 */

public class MaxLimitArrayList extends ArrayList<InventoryListItem> {
    private static final int MAX_ITEMS = Constants.UNIQUE_TAG_LIMIT;

    @Override
    public synchronized boolean add(InventoryListItem inventoryListItem) {
        if (size() < MAX_ITEMS)
            return super.add(inventoryListItem);
        else {
            logAsMessage(TYPE_DEBUG, "InventoryList",
                    "Inventory list limit exceeded... not adding the item...");
            return false;
        }
    }

    @Override
    public synchronized void add(int index, InventoryListItem inventoryListItem) {
        if (size() < MAX_ITEMS)
            super.add(index, inventoryListItem);
        else {
            logAsMessage(TYPE_DEBUG, "InventoryList",
                    "Inventory list limit exceeded... not adding the item...");
        }
    }

    @Override
    public synchronized boolean addAll(Collection<? extends InventoryListItem> collection) {
        if (size() + collection.size() < MAX_ITEMS)
            return super.addAll(collection);
        else {
            logAsMessage(TYPE_DEBUG, "InventoryList",
                    "Inventory list limit exceeded... not adding the item...");
            return false;
        }
    }

    @Override
    public synchronized boolean addAll(int index, Collection<? extends InventoryListItem> collection) {
        if (size() + collection.size() < MAX_ITEMS)
            return super.addAll(index, collection);
        else {
            logAsMessage(TYPE_DEBUG, "InventoryList",
                    "Inventory list limit exceeded... not adding the item...");
            return false;
        }
    }
}
