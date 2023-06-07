package com.greenhuecity.view.fragment.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputLayout;
import com.greenhuecity.R;
import com.greenhuecity.view.activity.SearchActivity;
import com.greenhuecity.data.contract.HomeContract;
import com.greenhuecity.data.model.Cars;
import com.greenhuecity.data.presenter.HomePresenter;
import com.greenhuecity.view.adapter.ViewPagerHomeAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements HomeContract.IView {
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    TextInputLayout inputLayout;
    AutoCompleteTextView completeTextView;
    ImageButton btnSearch;
    View view;
    List<Cars> carsList;
    HomePresenter mPresenter;
    String textSearch = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        initGUI();
        tabLayout.setSaveEnabled(false);
        viewPager2.setAdapter(new ViewPagerHomeAdapter(this.getActivity()));
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Nổi bật");
                    break;
                case 1:
                    tab.setText("Honda");
                    break;
                case 2:
                    tab.setText("Yamaha");
                    break;
                case 3:
                    tab.setText("Ducati");
                    break;
                case 4:
                    tab.setText("BMW");
                    break;
                case 5:
                    tab.setText("Aprilia");
                    break;

            }
        }).attach();
        viewPager2.setUserInputEnabled(false);
        mPresenter = new HomePresenter(this);
        mPresenter.getCarList();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (carsList != null) changeTextSearch();
            }
        }, 2000);
        return view;
    }

    private void initGUI() {
        tabLayout = view.findViewById(R.id.tablayout_brand);
        viewPager2 = view.findViewById(R.id.viewpager2_car);
        inputLayout = view.findViewById(R.id.textInputLayout);
        completeTextView = view.findViewById(R.id.autoCompleteText_search);
        btnSearch = view.findViewById(R.id.imageButton_search);
    }

    private void changeTextSearch() {
        try {
            List<String> suggestSearchResults = new ArrayList<>();
            for (Cars cars : carsList) {
                suggestSearchResults.add(cars.getCar_name());
            }
            if(suggestSearchResults == null || suggestSearchResults.isEmpty()) return;

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, suggestSearchResults);
            completeTextView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    inputLayout.setHintEnabled(false);
                    if (adapter != null) completeTextView.setAdapter(adapter);
                    textSearch = s.toString().toLowerCase();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        } catch (Exception e) {
            // Xử lý ngoại lệ ở đây.
        }

    }


    @Override
    public void searchTextChangedListener(List<Cars> carsList) {
        completeTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    intentSearch(carsList);
                    return true;
                }
                return false;
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentSearch(carsList);
            }
        });
    }

    @Override
    public void getCarsList(List<Cars> carsList) {
        this.carsList = carsList;
        if (carsList != null && textSearch != null) searchTextChangedListener(carsList);

    }

    void intentSearch(List<Cars> carsList) {
        List<Cars> searchList = mPresenter.filterCarList(textSearch, carsList);
        if (searchList != null) {
            Intent intent = new Intent(requireContext(), SearchActivity.class);
            intent.putExtra("list", (Serializable) searchList);
            startActivity(new Intent(intent));
        }
    }
}
