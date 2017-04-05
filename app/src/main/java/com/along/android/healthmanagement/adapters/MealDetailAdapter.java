package com.along.android.healthmanagement.adapters;


import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.along.android.healthmanagement.R;
import com.along.android.healthmanagement.entities.Food;
import com.along.android.healthmanagement.entities.VitalSign;
import com.along.android.healthmanagement.helpers.Utility;

import java.util.List;

public class MealDetailAdapter extends ArrayAdapter<Food>{
    public MealDetailAdapter(@NonNull Context context, @NonNull List<Food> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        final ListView listView = (ListView) parent;
        final Food food = getItem(position);

        if(null == convertView) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_meal_detail, parent, false);
        }

        TextView foodName = (TextView) listItemView.findViewById(R.id.meal_detail_food_name);
        foodName.setText(food.getName());

        TextView foodCalsAmount = (TextView) listItemView.findViewById(R.id.meal_detail_food_cals_amount);
        foodCalsAmount.setText(food.getFoodCalories() + " cals   " + "amount : " + food.getAmount());

        ImageView deleteFood = (ImageView) listItemView.findViewById(R.id.meal_detail_delete);
        deleteFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteAlertDialog(food, listView);
            }
        });

        return listItemView;
    }


    private void showDeleteAlertDialog(final Food food, final ListView listView) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Confirm Delete");
        alert.setMessage("Are you sure to delete this food?");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }
}
