package com.example.qrapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;



public class MainActivity extends AppCompatActivity {

    Button btn_scan ;
    RecyclerView recyclerView;
    ImageButton menuBtn;
    ScanAdapter scanAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_scan=findViewById(R.id.btn_scan);
        menuBtn = findViewById(R.id.menu_btn);
        recyclerView = findViewById(R.id.recycler_view);

        btn_scan.setOnClickListener(v -> {
            scanCode();
        });
        menuBtn.setOnClickListener((v)->showMenu());

        setuprecyclerview();
    }

    private void setuprecyclerview() {
        Query query = Utility.getCollectionReferenceForNotes();
        FirestoreRecyclerOptions<scans> options = new FirestoreRecyclerOptions.Builder<scans>()
                .setQuery(query,scans.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        scanAdapter = new ScanAdapter(options);
        recyclerView.setAdapter(scanAdapter);
    }
    @Override
    protected void onStart() {
        super.onStart();
    scanAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        scanAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
       scanAdapter.notifyDataSetChanged();
    }

    private void showMenu() {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this,menuBtn);
        popupMenu.getMenu().add("Logout");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getTitle()=="Logout"){
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });
    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("QR CODE SCANNER");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barlauncher.launch(options);



    }
    ActivityResultLauncher<ScanOptions> barlauncher = registerForActivityResult(new ScanContract(), result -> {
     if (result.getContents()!=null){
         AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
         builder.setTitle("Result");
         String val = result.getContents();
        scans scan = new scans();
        scan.setScan(val);

        savescantofirebase(scan);
         builder.setMessage(val);
         builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
             }
         }).show();

     }
    });

    private void savescantofirebase(scans scan) {
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForNotes().document();
        documentReference.set(scan).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Scan Added to Firebase",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Failed to add scan",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}