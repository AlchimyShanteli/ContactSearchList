package com.example.ryzen.contactsearchapp.main;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import com.example.ryzen.contactsearchapp.R;
import com.example.ryzen.contactsearchapp.adapter.ContactAdapter;
import com.example.ryzen.contactsearchapp.model.ContactModel;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISION_REQUEST_CODE = 1;

    private String wantPermission = Manifest.permission.READ_CONTACTS;
    private Cursor cursor;
    private String TAG = "MainActivity";

    private EditText searchTxt;

    private RecyclerView recyclerViewContacts;

    private ContactAdapter contactAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerViewContacts = findViewById(R.id.recyclerViewContacts);
        searchTxt = findViewById(R.id.searchEditText);

        if (!checkPermission(wantPermission)) {
            requestPermission(wantPermission);
        } else {
            initTextWatcher();
        }
    }

    private void initTextWatcher() {
        final List<ContactModel> contactModels = readContacts();
        populateRecyclerView(contactModels);
        searchTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<ContactModel> filteredList = filter(contactModels, s.toString());
                populateRecyclerView(filteredList);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private List<ContactModel> filter(List<ContactModel> sourceList, String filterContent) {
        List<ContactModel> newList = new ArrayList<>();
        for (int i = 0; i < sourceList.size(); i++) {
            String[] names = maybeExplodeName(sourceList.get(i).getName());
            boolean containsName = false;
            for (String name : names) {
                if (name.toLowerCase().contains(filterContent.toLowerCase())) {
                    containsName = true;
                }
            }
            boolean containsPhone = sourceList.get(i).getPhoneNumber().toLowerCase().contains(filterContent.toLowerCase());
            if (containsName || containsPhone) {
                newList.add(sourceList.get(i));
            }
        }
        return newList;
    }

    private String[] maybeExplodeName(String name) {
        return name.split(Pattern.quote(" "));
    }

    private List<ContactModel> readContacts() {
        List<ContactModel> contactModelList = new ArrayList<>();
        ContactModel contactModel;
        String id;
        String name;

        String order = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";
        ContentResolver contentResolver = getContentResolver();
        cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, order);

        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                    contactModel = new ContactModel();
                    contactModel.setName(name);

                    Cursor cursorPhone = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "= ?",
                            new String[]{id}, null);
                    Log.d(TAG, "readContacts: " + name);

                    if (cursorPhone.moveToFirst()) {
                        String phoneNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contactModel.setPhoneNumber(phoneNumber);
                        Log.d(TAG, "phone number" + phoneNumber);

                    }
                    cursorPhone.close();
                    contactModelList.add(contactModel);
                }
            }
        }
        cursor.close();
        return contactModelList;
    }

    private void populateRecyclerView(List<ContactModel> contactModelList) {
        contactAdapter = new ContactAdapter(contactModelList, getApplicationContext());
        recyclerViewContacts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewContacts.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerViewContacts.setAdapter(contactAdapter);
    }

    private void requestPermission(String permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            Toast.makeText(this, "Reqad contact permission", Toast.LENGTH_LONG).show();
        }
        ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initTextWatcher();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private boolean checkPermission(String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            int result = ContextCompat.checkSelfPermission(this, permission);
            if (result == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

}
