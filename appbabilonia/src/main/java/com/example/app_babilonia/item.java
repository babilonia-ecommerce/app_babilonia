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

public class item extends AppCompatActivity {

    // Inicializar variable
    DrawerLayout drawerLayout;
    public TextView tv_titulo_activity; // Titulo
    public TextView tv_usuario; // Usuario

    private ListView lv_items; // Lista de valores
    ArrayList<HashMap<String, String>> items; // Diccionario Array para valores

    // Creamos los objetos para el activity
    private EditText et_codigo, et_nombre, et_precio;
    private Button btn_registrar, btn_editar, btn_eliminar, btn_buscar, btn_cancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        // Asignar Variable
        drawerLayout = findViewById(R.id.drawer_layout);
        // Definimos el titulo
        tv_titulo_activity = (TextView)findViewById(R.id.titulo_activity);
        String titulo = getResources().getString(R.string.menu_empleados).toString();
        tv_titulo_activity.setText(titulo);

        // Definimos el usuario
        tv_usuario = (TextView)findViewById(R.id.nav_usuario);
        // Recuperamos valores almacenados
        SharedPreferences preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
        tv_usuario.setText(preferences.getString("nombre_apellido", ""));

        // Integracion de objetos / campos
        et_codigo = (EditText)findViewById(R.id.item_txt_codigo);
        et_nombre = (EditText)findViewById(R.id.item_txt_nombre);
        et_precio = (EditText)findViewById(R.id.item_txt_precio);
        btn_registrar = (Button)findViewById(R.id.item_btn_registrar);
        btn_editar = (Button)findViewById(R.id.item_btn_editar);
        btn_eliminar = (Button)findViewById(R.id.item_btn_eliminar);
        btn_buscar = (Button)findViewById(R.id.item_btn_buscar);
        btn_cancelar = (Button)findViewById(R.id.item_btn_cancelar);

        // Integracion de objetos / lista
        lv_items = (ListView)findViewById(R.id.item_lista);
        items = new ArrayList<HashMap<String, String>>();

        // Mostramos la lista
        ItemListar();
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

    public void ClickCiudad(View view){
        // Cambiamos al activity seleccionado
        MainActivity.redirectActivity(this, ciudades.class);
    }

    public void ClickItem(View view){
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

    // Metodo registrar item
    public void ItemRegistrar(View view){
        // Consulta SQL para registrar
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname,null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        // Obtenemos el ultimo codigo del item registrado
        Cursor row = db.rawQuery("select coalesce(max(item_cod),0) + 1 as cod from item", null);
        String item_cod_str = "";
        if (row.moveToFirst()){
            item_cod_str = row.getString(0);
        } else {
            Toast.makeText(this, "Ha ocurrido un error. Consultar con el administrador.", Toast.LENGTH_SHORT).show();
        }

        String item_nombre_str = et_nombre.getText().toString();
        String item_precio_str = et_precio.getText().toString();

        if (!item_nombre_str.isEmpty() && !item_precio_str.isEmpty()){
            // Validamos si el item ya existe
            Cursor nombre = db.rawQuery("select item_cod from item where item_descr = '"+ item_nombre_str +"'", null);
            if(!nombre.moveToFirst()){
                ContentValues item = new ContentValues();
                item.put("item_cod", item_cod_str);
                item.put("item_descr", item_nombre_str);
                item.put("item_precio", item_precio_str);

                db.insert("item", null, item);
                db.close();

                et_nombre.setText("");
                et_precio.setText("");
                Toast.makeText(this, "Registro Completado", Toast.LENGTH_SHORT).show();

                ItemListar();

            }else {
                Toast.makeText(this, "Item existente en el sistema", Toast.LENGTH_SHORT).show();

            }
        }else {
            Toast.makeText(this, "Dede completar los campos.", Toast.LENGTH_SHORT).show();
        }
    }

    // Metodo para listar item
    public void ItemListar(){
        items = new ArrayList<HashMap<String, String>>();
        // Consulta SQL para usuario y contraseña
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname,null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        // Consultamos los datos de los usuarios
        Cursor rows = db.rawQuery
                ("select item_cod, item_descr, item_precio from item order by item_cod desc", null);
        // ciclo while por cada fila
        while (rows.moveToNext()){
            // Creamos un diccionario con los valores
            HashMap<String, String> dict = new HashMap<String, String>();
            dict.put("Codigo", rows.getString(0));
            dict.put("Nombre", rows.getString(1));
            dict.put("Precio", rows.getString(2));
            items.add(dict); // Agregamos a la matrix

            // Agregamos los datos en la vista
            SimpleAdapter adapter = new SimpleAdapter(this, items, R.layout.list_item, new String[] {"Codigo","Nombre", "Precio"}, new int[] {R.id.tv_id, R.id.tv_nombre, R.id.tv_precio});
            lv_items.setAdapter(adapter);
        }
        db.close();

        // Al momento de dar click sobre uno de los valores, se autocompletara los datos para la modificacion o eliminación
        lv_items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> mymap = (HashMap<String, String>) parent.getItemAtPosition(position);
                et_codigo.setText(mymap.get("Codigo"));
                et_nombre.setText(mymap.get("Nombre"));
                et_precio.setText(mymap.get("Precio"));

                // Habilitamos los botones de Eliminar y Editar
                btn_registrar.setVisibility(View.INVISIBLE);
                btn_buscar.setVisibility(View.INVISIBLE);
                btn_editar.setVisibility(View.VISIBLE);
                btn_eliminar.setVisibility(View.VISIBLE);
                btn_cancelar.setVisibility(View.VISIBLE);
            }
        });
    }

    // Metodo para editar item
    public void ItemEditar(View view){
        // Obtenemos los valores actuales
        String item_cod_str = et_codigo.getText().toString();
        String item_nombre_str = et_nombre.getText().toString();
        String item_precio_str = et_precio.getText().toString();

        // Abrimos la DB para usuario mediante el id
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname,null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        // Verificamos si el item a actualizar corresponde al id
        Cursor nombre_id = db.rawQuery("select item_cod from item where item_descr = '"+ item_nombre_str +"' and item_cod = " + item_cod_str, null);
        if (nombre_id.moveToFirst()){ // si corresponde, procedemos a actualizar
            ContentValues datos = new ContentValues();
            datos.put("item_cod", item_cod_str);
            datos.put("item_descr", item_nombre_str);
            datos.put("item_precio", item_precio_str);

            int editar = db.update("item", datos, "item_cod=" + item_cod_str, null);
            db.close();

            if(editar >= 1){
                Toast.makeText(this, "Registro Actualizado", Toast.LENGTH_SHORT).show();
                ItemListar(); // Actualizar lista
            }else{ // En caso de que el id no exista
                Toast.makeText(this, "¡El registro indicado no existe!", Toast.LENGTH_SHORT).show();
            }
            ItemListar();
        }else { // en caso de que no pertenezca al id, se verifica lo siguiente
            // Verificamos si el item ya se encuentra registrado
            Cursor nombre = db.rawQuery("select item_cod from item where item_descr = '"+ item_nombre_str +"'", null);
            if(!nombre.moveToFirst()){
                ContentValues datos = new ContentValues();
                datos.put("item_cod", item_cod_str);
                datos.put("item_descr", item_nombre_str);
                datos.put("item_precio", item_nombre_str);

                int editar = db.update("item", datos, "item_cod=" + item_cod_str, null);
                db.close();

                if(editar >= 1){
                    Toast.makeText(this, "Registro Actualizado", Toast.LENGTH_SHORT).show();
                    ItemListar(); // Actualizar lista
                }else{ // En caso de que el id no exista
                    Toast.makeText(this, "¡El registro indicado no existe!", Toast.LENGTH_SHORT).show();
                }
                ItemListar();

            }else {
                Toast.makeText(this, "Item existente en el sistema", Toast.LENGTH_SHORT).show();

            }
        }
    }

    // Metodo para eliminar
    public void ItemEliminar(View view){
        // abrimos la db
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname,null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        // Eliminamos el item
        String item_cod = et_codigo.getText().toString();
        if(!item_cod.isEmpty()){
            int eliminar = db.delete("item", "item_cod=" + item_cod, null);
            db.close();
            et_nombre.setText("");
            et_precio.setText("");

            if(eliminar >= 1){
                Toast.makeText(this, "Registro Eliminado", Toast.LENGTH_SHORT).show();
                ItemListar(); // Actualizar lista
            }else {
                Toast.makeText(this, "¡El registro indicado no existe!", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "¡Debe indicar un codigo!", Toast.LENGTH_SHORT).show();
        }
    }

    // Metodo para cancelar
    public void ItemCancelar(View view){
        et_nombre.setText("");
        et_precio.setText("");
        recreate();
    }

    // Metodo Buscar
    public void ItemBuscar(View view){
        // Buscamos solo por el nombre de la ciudad
        String item_nombre = et_nombre.getText().toString();

        // Consulta SQL para ciudad
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname, null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        // Verificamos que el campo se encuentre cargado
        if (!item_nombre.isEmpty()){
            items = new ArrayList<HashMap<String, String>>();
            // Consultamos los datos de la ciudad
            Cursor rows = db.rawQuery("select item_cod, item_descr, item_precio from item where item_descr like '%" + item_nombre + "%' order by item_cod desc", null);
            // Verificamos si nos retorna datos
            if(rows.getCount() >= 1){
                Toast.makeText(this, "Iniciando busqueda...", Toast.LENGTH_SHORT).show();

                // ciclo while por cada fila
                while (rows.moveToNext()){
                    // Creamos un diccionario con los valores
                    HashMap<String, String> dict = new HashMap<String, String>();
                    dict.put("Codigo", rows.getString(0));
                    dict.put("Nombre", rows.getString(1));
                    dict.put("Precio", rows.getString(2));
                    items.add(dict); // Agregamos a la matrix

                    // Agregamos los datos en la vista
                    SimpleAdapter adapter = new SimpleAdapter(this, items, R.layout.list_item, new String[] {"Codigo","Nombre", "Precio"}, new int[] {R.id.tv_id, R.id.tv_nombre, R.id.tv_precio});
                    lv_items.setAdapter(adapter);
                }
                db.close();

                // Al momento de dar click sobre uno de los valores, se autocompletara los datos para la modificacion o eliminación
                lv_items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        HashMap<String, String> mymap = (HashMap<String, String>) parent.getItemAtPosition(position);
                        et_codigo.setText(mymap.get("Codigo"));
                        et_nombre.setText(mymap.get("Nombre"));
                        et_precio.setText(mymap.get("Precio"));

                        // Habilitamos los botones de Eliminar y Editar
                        btn_registrar.setVisibility(View.INVISIBLE);
                        btn_buscar.setVisibility(View.INVISIBLE);
                        btn_editar.setVisibility(View.VISIBLE);
                        btn_eliminar.setVisibility(View.VISIBLE);
                        btn_cancelar.setVisibility(View.VISIBLE);
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