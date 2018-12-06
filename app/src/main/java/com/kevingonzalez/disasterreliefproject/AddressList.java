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
 * This JAVA class is intended to hold the elements of the address_list_layout.xml and inflate it
 * for it to be used by the app.
 */

public class AddressList extends ArrayAdapter<Address> {

    // Instantiate objects.
    private Activity context;
    private List<Address> addressList;

    // Class constructor.
    public AddressList(Activity context, List<Address> addressList){
        super(context, R.layout.address_list_layout, addressList);
        this.context = context;
        this.addressList = addressList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.address_list_layout, null, true);

        // Cast objects to their respective elements within the view.
        TextView textViewAddress = listViewItem.findViewById(R.id.textViewAddress);

        // Get the position of the address within the list containing the addresses and then
        // set it to a new address object.
        Address address = addressList.get(position);

        // Set the textview elements within the view with their proper contents. in this case,
        // the address and userid are displayed.
        textViewAddress.setText(address.getAddress());

        // Return the inflated view.
        return listViewItem;
    }
}
