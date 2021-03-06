package com.example.zoardgeocze.clickonmap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.zoardgeocze.clickonmap.Adapter.PendingCollabAdapter;
import com.example.zoardgeocze.clickonmap.Model.Collaboration;
import com.example.zoardgeocze.clickonmap.Model.User;
import com.example.zoardgeocze.clickonmap.Model.VGISystem;
import com.example.zoardgeocze.clickonmap.Singleton.SingletonFacadeController;
import com.example.zoardgeocze.clickonmap.helper.CallbackItemSwipe;
import com.example.zoardgeocze.clickonmap.helper.CollaborationSender;
import com.example.zoardgeocze.clickonmap.helper.ItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.List;

public class PendingCollabActivity extends AppCompatActivity implements CallbackItemSwipe {

    public static final String NAME = "PendingCollabActivity";

    private SingletonFacadeController generalController;

    private static final int SEND_ID = 1;
    private static final int EDIT_ID = 2;
    private static final int DELETE_ID = 3;

    private Intent intent;
    private Bundle bundle;

    private User user;
    private VGISystem vgiSystem;
    private List<Collaboration> pendingCollabs = new ArrayList<>();

    private RecyclerView pendingCollabRecycler;

    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_collab);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.pending_collab_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        this.generalController = SingletonFacadeController.getInstance();

        this.pendingCollabRecycler = (RecyclerView) findViewById(R.id.pending_collab_recycler);

        this.intent = getIntent();
        this.bundle = this.intent.getExtras();

        this.vgiSystem = (VGISystem) this.bundle.getSerializable("vgiSystem");
        this.user = this.generalController.getUser(this.vgiSystem);

        this.pendingCollabs = this.generalController.getPendingCollaborations(this.vgiSystem.getAddress(),this.user.getId());

        this.pendingCollabRecycler.setAdapter(new PendingCollabAdapter(this.pendingCollabs,this, this.vgiSystem));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        this.pendingCollabRecycler.setLayoutManager(layoutManager);

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(this);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(this.pendingCollabRecycler);
    }

    public int getPosition() {
        return this.position;
    }

    public void closePendingCollab(View view) {
        finish();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item != null) {

            this.position = item.getGroupId();

            Log.i("ItemSelected_Title: ", String.valueOf(item.getTitle()));
            Log.i("ItemSelected_ID: ", String.valueOf(item.getItemId()));
            Log.i("ItemSelected_Position: ", String.valueOf(position));
            Log.i("ItemSelected_Collab: ", this.pendingCollabs.get(position).getTitle());

            switch (item.getItemId()) {

                case SEND_ID:

                    Collaboration collaboration = this.pendingCollabs.get(position);

                    CollaborationSender collaborationSender = new CollaborationSender(collaboration,this.vgiSystem.getAddress(),
                                                                            PendingCollabActivity.this,true);

                    if(collaboration.getPhoto().equals("") && collaboration.getVideo().equals("")) {
                        collaborationSender.sendCollaborationToServer();
                    } else {
                        collaborationSender.sendMidiaCollaboration();
                    }

                    break;

                case EDIT_ID:
                    //TODO: Implementar a Edição da Colaboração
                    break;

                case DELETE_ID:
                    deletePendingCollaboration(position);
                    Toast.makeText(this,"Colaboração Deletada",Toast.LENGTH_SHORT).show();

                    break;

            }

        }

        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_pending_collab_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            /*case R.id.send_pending_collab:
                Toast.makeText(this,"Envia todos",Toast.LENGTH_SHORT).show();
                break;*/
            case R.id.delete_pending_collab:
                deleteAllPendingCollaborations();

                break;
        }

        return false;
    }

    private void deleteAllPendingCollaborations() {

        this.generalController.deleteAllPendingCollaborations(this.user.getId(),this.vgiSystem.getAddress());
        this.pendingCollabs.clear();
        this.pendingCollabRecycler.getAdapter().notifyDataSetChanged();
        
    }

    public void deletePendingCollaboration(int position) {
        //Deleção no Banco de Dados
        this.generalController.deletePendingCollaboration(this.pendingCollabs.get(position).getCollaborationId());

        //Deleção Local
        this.pendingCollabs.remove(position);
        this.pendingCollabRecycler.getAdapter().notifyDataSetChanged();
    }


    @Override
    public void onItemDismiss(int position) {



    }

    @Override
    public void onItemSend(int position) {



    }
}
