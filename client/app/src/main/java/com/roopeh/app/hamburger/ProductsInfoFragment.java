package com.roopeh.app.hamburger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import androidx.fragment.app.Fragment;

public class ProductsInfoFragment extends Fragment {
    final private Product _product;
    private int _quantity;

    public ProductsInfoFragment(Product product) {
        _product = product;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_products_info, container, false);

        final ImageButton returnButton = rootView.findViewById(R.id.productsInfoBackButton);
        returnButton.setOnClickListener(v -> Objects.requireNonNull((MainActivity)getActivity()).returnToPreviousFragment(false));

        final TextView name = rootView.findViewById(R.id.productsInfoName);
        name.setText(_product.getName());

        final ImageView image = rootView.findViewById(R.id.productsInfoImage);
        image.setImageResource(R.drawable.hamburger_icon);

        final TextView price = rootView.findViewById(R.id.productsInfoPrice);
        price.setText(getString(R.string.orderEuroAmount, _product.getPrice()));

        final Spinner drinkSpinner = rootView.findViewById(R.id.productsInfoDrink);
        final CheckBox drinkLarge = rootView.findViewById(R.id.productsInfoDrinkLarge);
        final FrameLayout extrasSpinnerLayout = rootView.findViewById(R.id.productsInfoExtraLayout);
        final Spinner extrasSpinner = rootView.findViewById(R.id.productsInfoExtra);
        final CheckBox extrasLarge = rootView.findViewById(R.id.productsInfoExtraLarge);
        if (_product.isMeal()) {
            final FrameLayout drinkSpinnerLayout = rootView.findViewById(R.id.productsInfoDrinkLayout);

            // Set meals only widgets visible
            drinkSpinnerLayout.setVisibility(View.VISIBLE);
            drinkLarge.setVisibility(View.VISIBLE);
            extrasSpinnerLayout.setVisibility(View.VISIBLE);
            extrasLarge.setVisibility(View.VISIBLE);

            // Add prices for large drinks and extras
            drinkLarge.setText(getString(R.string.productsLargeDrinkPrice, drinkLarge.getText()));
            extrasLarge.setText(getString(R.string.productsLargeExtraPrice, extrasLarge.getText()));

            final ArrayAdapter<CharSequence> drinkAdapter = ArrayAdapter.createFromResource(getContext(), R.array.stringMealDrinks, android.R.layout.simple_spinner_item);
            drinkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            drinkSpinner.setAdapter(drinkAdapter);

            final ArrayAdapter<CharSequence> extrasAdapter = ArrayAdapter.createFromResource(getContext(), R.array.stringMealExtras, android.R.layout.simple_spinner_item);
            extrasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            extrasSpinner.setAdapter(extrasAdapter);

            // Set correct layout params programmatically in case the product is a meal
            final LinearLayout layout = rootView.findViewById(R.id.productsInfoAmount);
            final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)layout.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.productsInfoExtraLarge);
            layout.setLayoutParams(params);
        }

        final ImageButton amountIncrease = rootView.findViewById(R.id.productsInfoAmountIncrease);
        final ImageButton amountDecrease = rootView.findViewById(R.id.productsInfoAmountDecrease);

        amountIncrease.setOnClickListener(v -> updateQuantity(rootView, 1));
        amountDecrease.setOnClickListener(v -> updateQuantity(rootView, -1));

        final TextView composition = rootView.findViewById(R.id.productsInfoKoostumus);
        composition.setText(_product.getKoostumus());
        final TextView nutritional = rootView.findViewById(R.id.productsInfoSisalto);
        nutritional.setText(_product.getRavinto());

        final Button addToCart = rootView.findViewById(R.id.productsInfoBuyButton);
        addToCart.setOnClickListener(v -> {
            final User user = Helper.getInstance().getUser();
            if (user == null) {
                Toast.makeText(getContext(), getString(R.string.appRequireLogin), Toast.LENGTH_SHORT).show();
                return;
            }

            final int selectedDrink = (int)drinkSpinner.getSelectedItemId();
            if (_product.isMeal() && selectedDrink == 0) {
                Toast.makeText(getContext(), getString(R.string.productsMissingDrink), Toast.LENGTH_SHORT).show();
                return;
            }

            final int selectedExtra = (int)extrasSpinner.getSelectedItemId();
            if (_product.isMeal() && selectedExtra == 0) {
                Toast.makeText(getContext(), getString(R.string.productsMissingExtra), Toast.LENGTH_SHORT).show();
                return;
            }

            // Create new shopping item(s)
            for (int i = 0; i < _quantity; ++i) {
                final ShoppingItem item = new ShoppingItem(_product);
                if (_product.isMeal()) {
                    item.setMealDrink(selectedDrink, drinkLarge.isChecked());
                    item.setMealExtra(selectedExtra, extrasLarge.isChecked());

                    if ((int)drinkSpinner.getSelectedItemId() > 0 && drinkLarge.isChecked())
                        item.setPrice(item.getPrice() + 0.50);

                    if ((int)extrasSpinner.getSelectedItemId() > 0 && extrasLarge.isChecked())
                        item.setPrice(item.getPrice() + 0.40);
                }

                user.getCart().addToCart(item);
            }
        });

        updateQuantity(rootView, 1);
        return rootView;
    }

    private void updateQuantity(View rootView, int amount) {
        _quantity += amount;
        if (_quantity <= 1)
            _quantity = 1;

        final TextView amountText = rootView.findViewById(R.id.productsInfoAmountText);
        amountText.setText(getString(R.string.productsQuantity, _quantity));
    }
}
