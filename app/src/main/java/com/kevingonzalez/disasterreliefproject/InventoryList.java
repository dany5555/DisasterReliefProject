package com.kevingonzalez.disasterreliefproject;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Kevin Gonzalez on 11/29/2017.
 *
 * This JAVA class is intended to hold the elements of the inventory_list_layout.xml and inflate it
 * for it to be used by the app.
 */

public class InventoryList extends ArrayAdapter<Inventory> {

    // Instantiate objects.
    private Activity context;
    private List<Inventory> inventoryList;

    // Class constructor.
    public InventoryList(Activity context, List<Inventory> inventoryList){
        super(context, R.layout.inventory_list_layout, inventoryList);
        this.context = context;
        this.inventoryList = inventoryList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.inventory_list_layout, null, true);

        // Cast objects to their respective elements within the view.
        TextView textViewInventoryType = listViewItem.findViewById(R.id.textViewInventoryType);
        TextView textViewInventoryAmount = listViewItem.findViewById(R.id.textViewInventoryAmount);

        // Get the position of the inventory item within the list containing the items and then
        // set it to a new inventory object.
        Inventory inventory = inventoryList.get(position);

        // Set the textview elements within the view with their proper contents. in this case,
        // the inventory type and the amount are displayed.
        textViewInventoryType.setText(inventory.getInventory_name());
        textViewInventoryAmount.setText(inventory.getAmount_inventory());

        // Return the inflated view.
        return listViewItem;
    }
}
