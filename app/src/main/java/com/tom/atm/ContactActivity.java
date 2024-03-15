package com.tom.atm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Pack200;

public class ContactActivity extends AppCompatActivity {

    private static final int REQUEST_CONTACTS = 80;
    private static final String TAG = ContactActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            readContacts();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACTS);
        }
    }

    private void readContacts() {
        //read contacts
        Cursor cursor = getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        List<Contact> contacts = new ArrayList<>();
        while (cursor.moveToNext()) {
            //Lint 是用來標示程式中可能會有 bug 的工具，此標示用來停用它檢查。
            //姓名
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            Log.d(TAG, "readContacts: name=" + name);
            //電話
            @SuppressLint("Range") int id = cursor.getInt(cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
//            Log.d(TAG, "readContacts: id=" + id);
            Contact contact = new Contact(id,name);
            @SuppressLint("Range") int hasPhone = cursor.getInt(cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
//            Log.d(TAG, "readContacts: hasPhone=" + hasPhone);
            if (hasPhone == 1) {
                Cursor c2 = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                        new String[]{String.valueOf(id)},
                        null
                );
                while (c2.moveToNext()) {
                    @SuppressLint("Range") String phone = c2.getString(c2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
                    Log.d(TAG, "readContacts: \t" + phone);
                    contact.getPhones().add(phone);

                }
            }
            contacts.add(contact);
        }
        ContactAdapter adapter = new ContactAdapter(contacts);
        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactHolder> {
        List<Contact> contacts;
        public ContactAdapter(List<Contact> contacts){
            this.contacts = contacts;

        }
        @NonNull
        @Override
        public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(android.R.layout.simple_list_item_2,parent,false);
            return new ContactHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ContactHolder holder, int position) {
            Contact contact = contacts.get(position);
            holder.name.setText(contact.getName());
            StringBuilder sb = new StringBuilder();
            for (String phone : contact.getPhones()) {
                sb.append(phone);
            }
            holder.phone.setText(sb.toString());


        }

        @Override
        public int getItemCount() {
            return contacts.size();
        }

        public class ContactHolder extends RecyclerView.ViewHolder {
            EditText name;
            EditText phone;
            public ContactHolder(View itemView){
                super(itemView);
                name = itemView.findViewById(android.R.id.text1);
                phone = itemView.findViewById(android.R.id.text2);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readContacts();
            }
        }
    }
}