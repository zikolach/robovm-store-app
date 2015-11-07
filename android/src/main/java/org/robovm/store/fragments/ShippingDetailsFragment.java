/*
 * Copyright (C) 2013-2015 RoboVM AB
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package org.robovm.store.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.robovm.store.R;
import org.robovm.store.api.RoboVMWebService;
import org.robovm.store.model.Country;
import org.robovm.store.model.User;
import org.robovm.store.payments.PaymentAPI;
import org.robovm.store.util.Countries;

import java.util.ArrayList;
import java.util.List;

public class ShippingDetailsFragment extends Fragment {
    private User user;

    private EditText firstNameField;
    private EditText lastNameField;
    private EditText address1Field;
    private EditText address2Field;
    private EditText zipCodeField;
    private EditText cityField;
    private AutoCompleteTextView stateField;
    private EditText phoneNumberField;
    private AutoCompleteTextView countryField;

    private Runnable shippingDetailsEnteredListener;

    public ShippingDetailsFragment() {
        this.user = RoboVMWebService.getInstance().getCurrentUser();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View shippingDetailsView = inflater.inflate(R.layout.shipping_details, container, false);

        Button placeOrder = (Button) shippingDetailsView.findViewById(R.id.placeOrder);

        phoneNumberField = (EditText) shippingDetailsView.findViewById(R.id.phone);
        phoneNumberField.setText(user.getPhone());

        firstNameField = (EditText) shippingDetailsView.findViewById(R.id.firstName);
        firstNameField.setText(user.getFirstName());

        lastNameField = (EditText) shippingDetailsView.findViewById(R.id.lastName);
        lastNameField.setText(user.getLastName());

        address1Field = (EditText) shippingDetailsView.findViewById(R.id.streetAddress1);
        address1Field.setText(user.getAddress1());

        address2Field = (EditText) shippingDetailsView.findViewById(R.id.streetAddress2);
        address2Field.setText(user.getAddress2());

        cityField = (EditText) shippingDetailsView.findViewById(R.id.city);
        cityField.setText(user.getCity());

        stateField = (AutoCompleteTextView) shippingDetailsView.findViewById(R.id.state);
        stateField.setText(user.getState());

        zipCodeField = (EditText) shippingDetailsView.findViewById(R.id.postalCode);
        zipCodeField.setText(user.getZipCode());

        countryField = (AutoCompleteTextView) shippingDetailsView.findViewById(R.id.country);
        user.setCountry(user.getCountry() != null ? user.getCountry() : "United States");
        countryField.setText(user.getCountry());
        countryField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadStates();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        placeOrder.setOnClickListener((b) -> saveShippingDetails());

        loadCountries();
        loadStates();
        return shippingDetailsView;
    }

    private void loadCountries() {
        Country[] countries = Countries.getCountries();
        List<String> items = new ArrayList<>();
        for (Country country : countries) {
            items.add(country.getName());
        }
        countryField.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, items));
    }

    private void loadStates() {
        Country country = Countries.getCountryForName(countryField.getText().toString());
        if (country != null) {
            List<String> states = country.getStates();
            stateField
                    .setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, states));
        }
    }

    private void saveShippingDetails() {
        EditText[] entries = new EditText[] { phoneNumberField, address1Field, address2Field, cityField, stateField,
                zipCodeField, countryField };
        for (EditText entry : entries) {
            entry.setEnabled(false);
        }

        user.setFirstName(firstNameField.getText().toString());
        user.setLastName(lastNameField.getText().toString());
        user.setPhone(phoneNumberField.getText().toString());
        user.setAddress1(address1Field.getText().toString());
        user.setAddress2(address2Field.getText().toString());
        user.setCity(cityField.getText().toString());
        user.setState(stateField.getText().toString());
        user.setZipCode(zipCodeField.getText().toString());
        Country selectedCountry = Countries.getCountryForName(countryField.getText().toString());
        if (selectedCountry != null) {
            user.setCountry(selectedCountry.getCode());
        }

        if (shippingDetailsEnteredListener != null) {
            shippingDetailsEnteredListener.run();
        }

    }

    public void setShippingDetailsEnteredListener(Runnable shippingDetailsEnteredListener) {
        this.shippingDetailsEnteredListener = shippingDetailsEnteredListener;
    }
}
