package fr.greta60.planeteappgreta60;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import fr.greta60.planeteappgreta60.adapter.PlaneteAdapter;
import fr.greta60.planeteappgreta60.adapter.RecyclerPlaneteAdapter;
import fr.greta60.planeteappgreta60.model.Planete;
import fr.greta60.planeteappgreta60.model.PlaneteFields;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class RecyclerViewActivity extends AppCompatActivity {
    public static final String TAG = "RecyclerViewActivity";
    private Realm realm;
    int[] idImages;

    private RecyclerPlaneteAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        //récuperer une instance de la connection Realm (Singleton)

        realm = Realm.getDefaultInstance();
        //envoyer une requete pour récuperer les planetes stockées dans SGBD
        RealmResults<Planete> results = realm
                .where(Planete.class)
                .findAll();
//        Resources resources = getResources();
//        String[] nomsTab = resources.getStringArray(R.array.noms);
//        int[] distancesTab = resources.getIntArray(R.array.distances);
//

//        //créer un tableau des ressources images
        idImages = new int[]{
                R.drawable.mercury,
                R.drawable.venus,
                R.drawable.earth,
                R.drawable.mars,
                R.drawable.jupiter,
                R.drawable.saturn,
                R.drawable.uranus,
                R.drawable.neptune,
                R.drawable.pluto
        };
//
//        //créer la liste des planètes
//        ArrayList<Planete> list = new ArrayList<>();
//        for (int i = 0; i < nomsTab.length ; i++){
//            Planete p = new Planete(nomsTab[i], distancesTab[i], idImages[i]);
//            list.add(p);
//        }
        adapter =
                new RecyclerPlaneteAdapter(results);
        //associer adaptateur à ListView
        RecyclerView rv = (RecyclerView)findViewById(R.id.list);
        LinearLayoutManager llm =
                new LinearLayoutManager(this,
                        LinearLayoutManager.VERTICAL,
                        false);
//        GridLayoutManager llm =
//                new GridLayoutManager(this,
//                        2);
        rv.setLayoutManager(llm);//gestionnaire de mise en forme
        rv.setHasFixedSize(true);
        //séparateur
        DividerItemDecoration did = new DividerItemDecoration(rv.getContext(), llm.getOrientation());
        rv.addItemDecoration(did);
        adapter.setMenuListener(this);
        rv.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    /**
     * création de menu
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();//retourne id de l'item qui vient d'çetr activé
        switch (itemId){
            case R.id.menu_creer:
                //afficher le formulaire de création de planete
                Log.d(TAG, "dans menu_creer");
                Toast.makeText(this,"dans menu_creer", Toast.LENGTH_LONG).show();
                //créer un Intent explicite
                Intent i = new Intent(this, CreerPlaneteActivity.class);
                startActivityForResult(i, R.id.menu_creer);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == R.id.menu_creer && resultCode == RESULT_OK && null != data){
            //récupérer les données envoyées par CreerPlaneteActivity
            final String nomPlanete = data.getStringExtra("nomPlanete");
            final int distancePlanete = data.getIntExtra("distancePlanete", 0);
            int index = (int)(Math.random()*10);
            final Planete planete = new Planete(nomPlanete, distancePlanete, idImages[index]);
            realm.executeTransaction(new Realm.Transaction() { // must be in transaction for this to work
                @Override
                public void execute(Realm realm) {
                    // increment index
                    Number currentIdNum = realm.where(Planete.class).max(PlaneteFields.ID);
                    int nextId;
                    if(currentIdNum == null) {
                        nextId = 1;
                    } else {
                        nextId = currentIdNum.intValue() + 1;
                    }
                    planete.setId(nextId);
                    realm.insertOrUpdate(planete); // using insert API
                }
            });

            //adapter.addPlanete(planete);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.menu_modifier:
                //afficher le formulaire de modification de planete
                Log.i(TAG, "dans menu_modifier");
                Toast.makeText(this,"dans menu_modifier", Toast.LENGTH_LONG).show();

                return false;
            case R.id.menu_supprimer:
                //demander la confirmation avant de supprimer
                Log.i(TAG, "dans menu_supprimer");
                Toast.makeText(this,
                        "dans menu_supprimer",
                        Toast.LENGTH_LONG)
                        .show();
                int position = adapter.getClickedPosition();
                adapter.removePlanete(position);
                return false;
            default:
                return super.onContextItemSelected(item);
        }
    }

}
























