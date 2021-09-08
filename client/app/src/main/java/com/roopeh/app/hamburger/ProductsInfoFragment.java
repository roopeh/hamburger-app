package com.roopeh.app.hamburger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

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
        View rootView = inflater.inflate(R.layout.fragment_products_info, container, false);

        ImageButton returnButton = rootView.findViewById(R.id.productsInfoBackButton);
        final int category = _product.isMeal() ? ProductsListFragment.CATEGORY_MEAL : ProductsListFragment.CATEGORY_HAMBURGER;
        returnButton.setOnClickListener(v -> Objects.requireNonNull((MainActivity)getActivity()).loadFragment(new ProductsListFragment(category), false));

        TextView name = rootView.findViewById(R.id.productsInfoName);
        name.setText(_product.getName());

        TextView price = rootView.findViewById(R.id.productsInfoPrice);
        price.setText(String.valueOf(_product.getPrice()) + " €");

        if (_product.isMeal()) {
            FrameLayout drinkSpinnerLayout = rootView.findViewById(R.id.productsInfoDrinkLayout);
            Spinner drinkSpinner = rootView.findViewById(R.id.productsInfoDrink);
            CheckBox drinkLarge = rootView.findViewById(R.id.productsInfoDrinkLarge);
            FrameLayout extrasSpinnerLayout = rootView.findViewById(R.id.productsInfoExtraLayout);
            Spinner extrasSpinner = rootView.findViewById(R.id.productsInfoExtra);
            CheckBox extrasLarge = rootView.findViewById(R.id.productsInfoExtraLarge);

            // Set meals only widgets visible
            drinkSpinnerLayout.setVisibility(View.VISIBLE);
            drinkLarge.setVisibility(View.VISIBLE);
            extrasSpinnerLayout.setVisibility(View.VISIBLE);
            extrasLarge.setVisibility(View.VISIBLE);

            ArrayAdapter<CharSequence> drinkAdapter = ArrayAdapter.createFromResource(getContext(), R.array.stringMealDrinks, android.R.layout.simple_spinner_item);
            drinkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            drinkSpinner.setAdapter(drinkAdapter);

            ArrayAdapter<CharSequence> extrasAdapter = ArrayAdapter.createFromResource(getContext(), R.array.stringMealExtras, android.R.layout.simple_spinner_item);
            extrasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            extrasSpinner.setAdapter(extrasAdapter);

            // Set correct layout params programmatically in case the product is a meal
            LinearLayout layout = rootView.findViewById(R.id.productsInfoAmount);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)layout.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.productsInfoExtraLarge);
            layout.setLayoutParams(params);
        }

        ImageButton amountIncr = rootView.findViewById(R.id.productsInfoAmountIncrease);
        ImageButton amountDecr = rootView.findViewById(R.id.productsInfoAmountDecrease);

        amountIncr.setOnClickListener(v -> updateQuantity(rootView, 1));
        amountDecr.setOnClickListener(v -> updateQuantity(rootView, -1));

        updateQuantity(rootView, 1);
        return rootView;
    }

    private void updateQuantity(View rootView, int amount) {
        _quantity += amount;
        if (_quantity <= 1)
            _quantity = 1;

        TextView amountText = rootView.findViewById(R.id.productsInfoAmountText);
        amountText.setText(_quantity + "x");
    }
}
