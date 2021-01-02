package com.MCardenas.todo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT= "item_text";
    public static final String KEY_ITEM_POSITION= "item_position";
    public static final int EDIT_TEXT_CODE= 20;

    List<String> items;

    Button btnAdd;
    EditText etItem;
    RecyclerView rvItems;
    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //selects elements from app
        btnAdd= findViewById(R.id.btnAdd);
        etItem= findViewById(R.id.etItem);
        rvItems= findViewById(R.id.rvItems);

        //Load items when app start
        loadItems();

        //Click event handlers
        ItemsAdapter.OnLongClickListener onLongClickListener= new ItemsAdapter.OnLongClickListener(){
            @Override
            public void onItemLongClicked(int position) {
                items.remove(position);
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(),"Item Removed!",Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };

        ItemsAdapter.OnClickListener clickListener= new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
//                Log.d("main activity","Single click at position "+position);
                //MainActivity -> EditActivity
                Intent i= new Intent(MainActivity.this,EditActivity.class);
                i.putExtra(KEY_ITEM_TEXT,items.get(position));
                i.putExtra(KEY_ITEM_POSITION,position);
                startActivityForResult(i,EDIT_TEXT_CODE);
            }
        };

        itemsAdapter= new ItemsAdapter(items, onLongClickListener, clickListener);
        rvItems.setAdapter(itemsAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        //Add button click event
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todoItem= etItem.getText().toString();
                items.add(todoItem);
                itemsAdapter.notifyItemInserted(items.size()-1);
                etItem.setText("");
                Toast.makeText(getApplicationContext(),"Item added!",Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });

    }

    //Update item selected
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    //    super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);
            items.set(position, itemText);
            itemsAdapter.notifyItemChanged(position);
            saveItems();
            Toast.makeText(getApplicationContext(), "Item updated!", Toast.LENGTH_SHORT).show();
        } else {
            Log.w("Main Activity", "Unknown call to onActivityResult");
        }
    }


    //Getting Item Data
    private File getDatafile(){
        return new File(getFilesDir(),"data.txt");
    }

    //Load Item Data
    private void loadItems(){
        try {
            items = new ArrayList<>(org.apache.commons.io.FileUtils.readLines(getDatafile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("Main Activity","Error reading items",e);
            items= new ArrayList<>();
        }
    }

    //Save Item data
    private void saveItems(){
        try{
            org.apache.commons.io.FileUtils.writeLines(getDatafile(),items);
        }catch(IOException e){
            Log.e("MainActivity","Error writing items",e);
        }
    }
}