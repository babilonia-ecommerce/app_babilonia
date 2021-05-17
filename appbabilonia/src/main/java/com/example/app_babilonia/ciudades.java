package com.example.app_babilonia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app_babilonia.database.SQLControl;

import java.util.ArrayList;
import java.util.HashMap;

public class ciudades extends AppCompatActivity {

    // Inicializar variable
    DrawerLayout drawerLayout;
    public TextView tv_titulo_activity; // Titulo
    public TextView tv_usuario; // Usuario logueado

    private ListView lv_items; // Lista de Ciudades
    ArrayList<HashMap<String, String>> items; // Diccionario Array para valores de ciudades

    // Creamos los objetos para el activity
    private EditText et_id, et_descripcion;
    private Button bt_nuevo, bt_modificar, bt_eliminar, bt_buscar, bt_cancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ciudades);

        // Asignar Variable
        drawerLayout = findViewById(R.id.drawer_layout);
        // Definimos el titulo
        tv_titulo_activity = (TextView)findViewById(R.id.titulo_activity);
        String titulo = getResources().getString(R.string.menu_ciudades).toString();
        tv_titulo_activity.setText(titulo);

        // Definimos el usuario logueado
        tv_usuario = (TextView)findViewById(R.id.nav_usuario);
        // Recuperamos valores almacenados
        SharedPreferences preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
        tv_usuario.setText(preferences.getString("descripcion", ""));

        // Integracion de objetos / campos
        et_id = (EditText)findViewById(R.id.ciudades_txt_id);
        et_descripcion = (EditText)findViewById(R.id.ciudades_txt_descripcion);
        bt_nuevo = (Button)findViewById(R.id.ciudades_bt_nuevo);
        bt_modificar = (Button)findViewById(R.id.ciudades_bt_modificar);
        bt_eliminar = (Button)findViewById(R.id.ciudades_bt_eliminar);
        bt_buscar = (Button)findViewById(R.id.ciudades_bt_buscar);
        bt_cancelar = (Button)findViewById(R.id.ciudades_bt_cancelar);

        // Integracion de objetos / lista
        lv_items = (ListView)findViewById(R.id.ciudad_lista);
        items = new ArrayList<HashMap<String, String>>();

        // Mostramos la lista
        CiudadesListar();
    }
    // Inicio: Menu de navegacion
    public void ClickMenu(View view){
        // Abrir menu
        MainActivity.openDrawer(drawerLayout);
    }

    public void ClickLogo(View view){
        // Cerrar menu
        MainActivity.closeDrawer(drawerLayout);
    }

    public void ClickHome(View view){
        // Ir a Home
        MainActivity.redirectActivity(this, MainActivity.class);
    }

    public void ClickUsuario(View view){
        // Cambiamos al activity seleccionado
        MainActivity.redirectActivity(this, usuario.class);
    }

    public void ClickItem(View view){
        // Cambiamos al activity seleccionado
        MainActivity.redirectActivity(this, item.class);
    }

    public void ClickCiudades(View view){
        recreate();
    }

    public void ClickPaquete(View view){
        // Cambiamos al activity seleccionado
        MainActivity.redirectActivity(this, mercaderias.class);
    }
    public void ClickPerfil(View view){
        // Cambiamos al activity seleccionado
        MainActivity.redirectActivity(this, perfil.class);
    }

    public void ClickAcerca(View view){
        // Cerramos la aplicacion
        MainActivity.Acerca(this);
    }

    public void ClickLogout(View view){
        MainActivity.logout(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.closeDrawer(drawerLayout);
    }
    // Fin Menu de Navegación

    // Metodo registrar ciudad
    public void CiudadesNuevo(View view){
        // Consulta SQL para registrar
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname,null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        // Obtenemos el ultimo id de ciudad registrada
        Cursor row = db.rawQuery("select coalesce(max(id_ciudad),0) + 1 as id_ciudad from ciudades", null);
        String ciu_id_str = "";
        if (row.moveToFirst()){
            ciu_id_str = row.getString(0);
        } else {
            Toast.makeText(this, "Ha ocurrido un error. Consultar con el administrador.", Toast.LENGTH_SHORT).show();
        }

        String ciu_descripcion_str = et_descripcion.getText().toString();

        if(!ciu_descripcion_str.isEmpty()){
            // Validamos si la ciudad ya existe
            Cursor descripcion = db.rawQuery("select id_ciudad from ciudades where descripcion = '"+ ciu_descripcion_str +"'", null);
            if(!descripcion.moveToFirst()){
                ContentValues ciudades = new ContentValues();
                ciudades.put("id_ciudad", ciu_id_str);
                ciudades.put("descripcion", ciu_descripcion_str);

                db.insert("ciudades", null, ciudades);
                db.close();

                et_descripcion.setText("");
                Toast.makeText(this, "Se ha registrado la ciudad correctamente", Toast.LENGTH_SHORT).show();

                CiudadesListar();

            }else {
                Toast.makeText(this, "La ciudad ingresada ya existe", Toast.LENGTH_SHORT).show();

            }
        }else {
            Toast.makeText(this, "Debe completar los campos.", Toast.LENGTH_SHORT).show();
        }
    }
    // Metodo para listar ciudades
    public void CiudadesListar(){
        items = new ArrayList<HashMap<String, String>>();
        // Consulta SQL para usuario y contraseña
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname,null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        // Consultamos los datos de los usuarios
        Cursor rows = db.rawQuery
                ("select id_ciudad, descripcion from ciudades order by descripcion ", null);
        // ciclo while por cada fila
        while (rows.moveToNext()){
            // Creamos un diccionario con los valores
            HashMap<String, String> dict = new HashMap<String, String>();
            dict.put("id", rows.getString(0));
            dict.put("descripcion", rows.getString(1));
            items.add(dict); // Agregamos a la matrix

            // Agregamos los datos en la vista
            SimpleAdapter adapter = new SimpleAdapter(this, items, R.layout.list_ciudad, new String[] {"id","descripcion"}, new int[] {R.id.ciudades_txt_id, R.id.ciudades_txt_descripcion});
            lv_items.setAdapter(adapter);
        }
        db.close();

        // Al momento de dar click sobre uno de los valores, se autocompletara los datos para la modificacion o eliminación
        lv_items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> mymap = (HashMap<String, String>) parent.getItemAtPosition(position);
                et_id.setText(mymap.get("id"));
                et_descripcion.setText(mymap.get("descripcion"));

                // Habilitamos los botones de Eliminar y Editar
                bt_nuevo.setVisibility(View.INVISIBLE);
                bt_buscar.setVisibility(View.INVISIBLE);
                bt_modificar.setVisibility(View.VISIBLE);
                bt_eliminar.setVisibility(View.VISIBLE);
                bt_cancelar.setVisibility(View.VISIBLE);
            }
        });
    }

    // Metodo para editar ciudad
    public void CiudadesModificar(View view){
        // Obtenemos los valores actuales
        String ciu_id_str = et_id.getText().toString();
        String ciu_descripcion_str = et_descripcion.getText().toString();

        // Abrimos la DB para usuario mediante el id
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname,null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        // Verificamos si la ciudad ya se encuentra registrada
        Cursor descripcion = db.rawQuery("select id_ciudad from ciudades where descripcion = '"+ ciu_descripcion_str +"'", null);
        if(!descripcion.moveToFirst()){
            ContentValues datos = new ContentValues();
            datos.put("id_ciudad", ciu_id_str);
            datos.put("descripcion", ciu_descripcion_str);

            int editar = db.update("ciudades", datos, "id_ciudad=" + ciu_id_str, null);
            db.close();

            if(editar >= 1){
                Toast.makeText(this, "Registro Actualizado", Toast.LENGTH_SHORT).show();
                CiudadesListar(); // Actualizar lista
            }else{ // En caso de que el id no exista
                Toast.makeText(this, "¡El registro indicado no existe!", Toast.LENGTH_SHORT).show();
            }
            CiudadesListar();

        }else {
            Toast.makeText(this, "Ciudad existente en el sistema", Toast.LENGTH_SHORT).show();

        }

    }

    // Metodo para eliminar
    public void CiudadesEliminar(View view){
        // abrimos la db
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname,null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        // Eliminamos el item
        String id_ciudad = et_id.getText().toString();
        if(!id_ciudad.isEmpty()){
            int eliminar = db.delete("ciudades", "id_ciudad=" + id_ciudad, null);
            db.close();
            et_descripcion.setText("");

            if(eliminar >= 1){
                Toast.makeText(this, "La ciudad fue eliminada", Toast.LENGTH_SHORT).show();
                CiudadesListar(); // Actualizar lista
            }else {
                Toast.makeText(this, "¡La ciudad seleccionada no existe!", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "¡Debe indicar una Ciudad!", Toast.LENGTH_SHORT).show();
        }
    }

    // Metodo para cancelar
    public void CiudadesCancelar(View view){
        et_descripcion.setText("");
        recreate();
    }

    // Metodo Buscar
    public void CiudadesBuscar(View view){
        // Buscamos solo por la descripion de la ciudad
        String ciu_descripcion = et_descripcion.getText().toString();

        // Consulta SQL para ciudad
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname, null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        // Verificamos que el campo se encuentre cargado
        if (!ciu_descripcion.isEmpty()){
            items = new ArrayList<HashMap<String, String>>();
            // Consultamos los datos de la ciudad
            Cursor rows = db.rawQuery("select id_ciudad, descripcion from ciudades where descripcion like '%" + ciu_descripcion + "%' order by descripcion ", null);
            // Verificamos si nos retorna datos
            if(rows.getCount() >= 1){
                Toast.makeText(this, "Iniciando busqueda...", Toast.LENGTH_SHORT).show();

                // ciclo while por cada fila
                while (rows.moveToNext()){
                    // Creamos un diccionario con los valores
                    HashMap<String, String> dict = new HashMap<String, String>();
                    dict.put("Id", rows.getString(0));
                    dict.put("Descripcion", rows.getString(1));
                    items.add(dict); // Agregamos a la matrix

                    // Agregamos los datos en la vista
                    SimpleAdapter adapter = new SimpleAdapter(this, items, R.layout.list_ciudad, new String[] {"Id","Descripcion"}, new int[] {R.id.ciudades_txt_id, R.id.ciudades_txt_descripcion});
                    lv_items.setAdapter(adapter);
                }
                db.close();

                // Al momento de dar click sobre uno de los valores, se autocompletara los datos para la modificacion o eliminación
                lv_items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        HashMap<String, String> mymap = (HashMap<String, String>) parent.getItemAtPosition(position);
                        et_id.setText(mymap.get("Id"));
                        et_descripcion.setText(mymap.get("Descripcion"));

                        // Habilitamos los botones de Eliminar y Editar
                        bt_nuevo.setVisibility(View.INVISIBLE);
                        bt_buscar.setVisibility(View.INVISIBLE);
                        bt_modificar.setVisibility(View.VISIBLE);
                        bt_eliminar.setVisibility(View.VISIBLE);
                        bt_cancelar.setVisibility(View.VISIBLE);
                    }
                });

            }else { // en caso de que no se encuentre registros
                Toast.makeText(this, "No se encuentran registros", Toast.LENGTH_SHORT).show();
            }
        }else { // en caso de que no este completo el campo indicado
            Toast.makeText(this, "¡Debe indicar una ciudad para buscar!", Toast.LENGTH_SHORT).show();
        }
    }
}