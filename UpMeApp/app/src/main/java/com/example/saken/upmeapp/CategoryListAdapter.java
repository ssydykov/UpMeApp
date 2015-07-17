package com.example.saken.upmeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class CategoryListAdapter extends ArrayAdapter<CategoryModel> {

    List<CategoryModel> mylist;

    public CategoryListAdapter(Context _context, List<CategoryModel> _mylist) {
        super(_context, R.layout.activity_categories_list_item, _mylist);

        this.mylist = _mylist;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = new LinearLayout(getContext());
        String inflater = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);
        convertView = vi.inflate(R.layout.activity_categories_list_item, parent, false);


        // Product object
        CategoryModel product = getItem(position);


        //
        TextView categoryName = (TextView) convertView.findViewById(R.id.categoryName);
        categoryName.setText(product.name);

        TextView subcategoryName = (TextView) convertView.findViewById(R.id.subcategoryName);
        subcategoryName.setText(product.sub_name);

        // show image
        ImageView img = (ImageView)convertView.findViewById(R.id.categoryImage);

        // download image
        ImageDownloader imageDownloader = new ImageDownloader();
        imageDownloader.download(product.img_url, img);

        return convertView;
    }
}
