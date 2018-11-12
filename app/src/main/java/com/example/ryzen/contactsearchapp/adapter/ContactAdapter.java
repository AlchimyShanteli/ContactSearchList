package com.example.ryzen.contactsearchapp.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.ryzen.contactsearchapp.R;
import com.example.ryzen.contactsearchapp.model.ContactModel;
import com.example.ryzen.contactsearchapp.utils.GlideApp;
import java.util.List;
import java.util.Random;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder>  {
    private List<ContactModel> contactModels;
    private List<ContactModel> newFilterList;
    private Context mContext;
    private TypedArray colors;

    public ContactAdapter(List<ContactModel> contactModels, Context applicationContext) {
        this.contactModels = contactModels;
        this.colors = applicationContext.getResources().obtainTypedArray(R.array.paletteColors);
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView txtContactName;
        TextView txtPhoneNumber;
        ImageView contactImage;

        ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            txtContactName = itemView.findViewById(R.id.txtContactName);
            txtPhoneNumber = itemView.findViewById(R.id.txtPhoneNumber);
            contactImage = itemView.findViewById(R.id.ivContactImage);
        }
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.contact_list, viewGroup, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        ContactModel contactModel = contactModels.get(position);
        holder.txtContactName.setText(contactModel.getName());
        holder.txtPhoneNumber.setText(contactModel.getPhoneNumber());
        GlideApp.with(mContext)
                .load(new ColorDrawable(getRandomColor()))
                .transform(new CircleCrop())
                .into(holder.contactImage);
    }

    @Override
    public int getItemCount() {
        return contactModels.size();
    }

    private int getRandomColor() {
        return colors.getColor(new Random(System.nanoTime()).nextInt(colors.length()), Color.WHITE);
    }

}
