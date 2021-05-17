package com.example.app_babilonia;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.app_babilonia.database.SQLControl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class proveedores extends AppCompatActivity {

    // Inicializar las variables
    DrawerLayout drawerLayout;
    public TextView tv_titulo_activity; // Titulo
    public TextView tv_usuario; // Usuario
    public LinearLayout menu_pedido_compras;
    public LinearLayout menu_presupuesto_compras;
    public LinearLayout menu_proveedores;
    public LinearLayout menu_ciudades;
    public LinearLayout menu_usuarios;
    public LinearLayout menu_marcas;
    public LinearLayout menu_roles;


    private ListView lv_proveedores; // Lista de proveedores

    ArrayList<HashMap<String, String>> items; // Diccionario Array para valores de ciudades usuarios
    ArrayList<HashMap<String, String>> list_pro;
    ArrayList<HashMap<String, String>> items_ciudades; // Diccionario Array para valores de ciudades


    // Creamos los objetos para el activity
    private EditText et_id_proveedor, et_razon_social, et_ruc, et_telefono, et_direccion, et_email, et_id_ciudad;
    private Spinner sp_tipo_persona, sp_ciudad;
    private Button bt_nuevo, bt_modificar, bt_eliminar, bt_buscar, bt_cancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_proveedores);

        // Asignar Variable
        drawerLayout = findViewById(R.id.drawer_layout);
        // Definimos el titulo
        tv_titulo_activity = (TextView) findViewById(R.id.titulo_activity);
        String titulo = getResources().getString(R.string.menu_proveedores).toString();
        tv_titulo_activity.setText(titulo);

// Definimos el usuario
        tv_usuario = (TextView) findViewById(R.id.nav_usuario);
        // Recuperamos valores almacenados
        SharedPreferences preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
        tv_usuario.setText(preferences.getString("nombre_empleado", ""));

        // Verificamos permisos
        String id_rol = preferences.getString("u.id_rol", "");
        menu_pedido_compras = (LinearLayout) findViewById(R.id.menu_pedido_compras);
        menu_presupuesto_compras = (LinearLayout) findViewById(R.id.menu_presupuesto_compras);
        menu_proveedores = (LinearLayout) findViewById(R.id.menu_proveedores);
        menu_ciudades = (LinearLayout) findViewById(R.id.menu_ciudades);
        menu_usuarios = (LinearLayout) findViewById(R.id.menu_usuarios);
        menu_roles = (LinearLayout) findViewById(R.id.menu_roles);
        menu_marcas = (LinearLayout) findViewById(R.id.menu_marcas);
        if (!id_rol.equals("1")) { // si no es admin, ocultamos los siguientes menu
            menu_usuarios.setVisibility(View.INVISIBLE);
            menu_proveedores.setVisibility(View.INVISIBLE);
            menu_ciudades.setVisibility(View.INVISIBLE);
            menu_roles.setVisibility(View.INVISIBLE);
            menu_marcas.setVisibility(View.INVISIBLE);

        } else {
            //    Toast.makeText(this, "Administrador de Sistemas", Toast.LENGTH_SHORT).show();
        }


        // Integracion de objetos / txt
        et_id_proveedor = (EditText) findViewById(R.id.proveedores_txt_id);
        et_razon_social = (EditText) findViewById(R.id.proveedores_txt_razon_social);
        et_ruc = (EditText) findViewById(R.id.proveedores_txt_ruc);
        et_telefono = (EditText) findViewById(R.id.proveedores_txt_telefono);
        et_direccion = (EditText) findViewById(R.id.proveedores_txt_direccion);
        et_email = (EditText) findViewById(R.id.proveedores_txt_email);
        et_id_ciudad = (EditText) findViewById(R.id.proveedores_txt_id_ciudad);

        // Integracion de objetos / spinner
        sp_tipo_persona = (Spinner) findViewById(R.id.proveedores_sp_tipo_persona);
        sp_ciudad = (Spinner) findViewById(R.id.proveedores_sp_ciudad);

        //Integracion de objetos / Button
        bt_nuevo = (Button) findViewById(R.id.proveedores_bt_nuevo);
        bt_modificar = (Button) findViewById(R.id.proveedores_bt_modificar);
        bt_eliminar = (Button) findViewById(R.id.proveedores_bt_eliminar);
        bt_buscar = (Button) findViewById(R.id.proveedores_bt_buscar);
        bt_cancelar = (Button) findViewById(R.id.proveedores_bt_cancelar);

        // Integracion de objetos / lista
        lv_proveedores = (ListView) findViewById(R.id.proveedores_lista);
        items = new ArrayList<HashMap<String, String>>();

        // Integracion de objetos / spiner dinamicos
        items_ciudades = new ArrayList<HashMap<String, String>>();

        // Consulta SQL para spinner  dinamicos = ciudades y usuarios
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname, null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        // Consultamos los datos de las ciudades
        Cursor ciudad = db.rawQuery
                ("select id_ciudad, descripcion from ciudades order by descripcion", null);
        // ciclo while por cada fila
        while (ciudad.moveToNext()) {
            // Creamos un diccionario con los valores
            HashMap<String, String> dict = new HashMap<String, String>();
            dict.put("id_ciudad", ciudad.getString(0));
            dict.put("descripcion", ciudad.getString(1));
            items_ciudades.add(dict); // Agregamos a la matrix
            // Agregamos los datos en la vista
            SimpleAdapter adapter_ciudades = new SimpleAdapter(this, items_ciudades, android.R.layout.simple_spinner_item, new String[]{"descripcion"}, new int[]{android.R.id.text1});
            sp_ciudad.setAdapter(adapter_ciudades);
        }
        // Al momento de dar click sobre uno de los valores, se autocompletara los datos para el item seleccionado
        sp_ciudad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> mymap = (HashMap<String, String>) parent.getItemAtPosition(position);
                et_id_ciudad.setText(mymap.get("id_ciudad"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        // Integracion de objetos / spinner staticos

        String[] tipo_persona = {"Fisica", "Juridica"};
        ArrayAdapter<String> adapter_2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipo_persona);
        sp_tipo_persona.setAdapter(adapter_2);


        // Mostramos la lista
        proveedoresListar();

    }


    //------------------Inicio: Menu de navegacion--------------------------------------------------

    public void ClickMenu(View view) {
        MainActivity.openDrawer(drawerLayout);
    }

    public void ClickLogo(View view) {
        MainActivity.closeDrawer(drawerLayout);
    }

    public static void redirectActivity(Activity activity, Class aClass) {
        // Inicializamos el Intent
        Intent intent = new Intent(activity, aClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public void ClickHome(View view) {
        redirectActivity(this, MainActivity.class);
    }

    public void ClickPedidoCompras(View view){
           redirectActivity(this, pedido_compras.class);
    }

    //    public void ClickPresupuestoCompras(View view){
    //       redirectActivity(this, presupuesto_compras.class);
    //    }

    public void ClickPaquete(View view) {
        redirectActivity(this, mercaderias.class);
    }

    public void ClickProveedores(View view) {
        redirectActivity(this, proveedores.class);
    }

    public void ClickEmpleados(View view) {
        redirectActivity(this, proveedores.class);
    }

    public void ClickCiudades(View view) {
        redirectActivity(this, ciudades.class);
    }

    //    public void ClickMarcas(View view){
    //       redirectActivity(this, marcas.class);
    //    }

    public void ClickUsuario(View view) {
        redirectActivity(this, usuario.class);
    }

    //    public void ClickRoles(View view){
    //       redirectActivity(this, roles.class);
    //    }

    public void ClickPerfil(View view) {
        redirectActivity(this, perfil.class);
    }

    public void ClickAcerca(View view) {
        MainActivity.Acerca(this);
    }

    public void ClickLogout(View view) {
        MainActivity.logout(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.closeDrawer(drawerLayout);
    }

    //------------------Fin Menu de Navegación------------------------------------------------------

    //------------------Metodo para nuevo proveedor--------------------------------------------------
    public void proveedoresNuevo(View view) {
        // Consulta SQL
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname, null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        // Obtenemos el ultimo id del proveedor registrado
        Cursor row = db.rawQuery("select coalesce(max(id_proveedor),0) + 1 as id from proveedores", null);
        String id_str = "";
        if (row.moveToFirst()) {
            id_str = row.getString(0);
        } else {
            Toast.makeText(this, "Ha ocurrido un error. Consultar con el administrador.", Toast.LENGTH_SHORT).show();
        }

        String proveedores_razon_social_str = et_razon_social.getText().toString();
        String proveedores_ruc_str = et_ruc.getText().toString();
        String proveedores_telefono_str = et_telefono.getText().toString();
        String proveedores_direccion_str = et_direccion.getText().toString();
        String proveedores_email_str = et_email.getText().toString();
        String proveedores_id_ciudad_str = et_id_ciudad.getText().toString();
        String proveedores_tipo_persona_str = sp_tipo_persona.getSelectedItem().toString();


        //Validamos que los campos no esten vacios
        if (!proveedores_razon_social_str.isEmpty() && !proveedores_ruc_str.isEmpty() && !proveedores_tipo_persona_str.isEmpty() && !proveedores_telefono_str.isEmpty()
                && !proveedores_direccion_str.isEmpty() && !proveedores_email_str.isEmpty() && !proveedores_id_ciudad_str.isEmpty()) {

            // Validamos si el proveedor ya existe
            Cursor cedula = db.rawQuery("select id_proveedor from proveedores where ruc = '" + proveedores_ruc_str + "'", null);
            if (!cedula.moveToFirst()) {

                // Validamos si el correo ya existe
                Cursor email = db.rawQuery("select id_proveedor from proveedores where email = '" + proveedores_email_str + "'", null);
                if (!email.moveToFirst()) {


                    ContentValues proveedores = new ContentValues();
                    proveedores.put("id_proveedor", id_str);
                    proveedores.put("id_ciudad", proveedores_id_ciudad_str);
                    proveedores.put("razon_social", proveedores_razon_social_str);
                    proveedores.put("ruc", proveedores_ruc_str);
                    proveedores.put("tipo_persona", proveedores_tipo_persona_str);
                    proveedores.put("telefono", proveedores_telefono_str);
                    proveedores.put("direccion", proveedores_direccion_str);
                    proveedores.put("email", proveedores_email_str);

                    db.insert("proveedores", null, proveedores);
                    db.close();

                    et_id_ciudad.setText("");
                    et_razon_social.setText("");
                    et_ruc.setText("");
                    et_telefono.setText("");
                    et_direccion.setText("");
                    et_email.setText("");


                    proveedoresListar(); // Actualizar lista

                    Toast.makeText(this, "Se ha registrado al proveedor exitosamente!", Toast.LENGTH_SHORT).show();

                } else { // si existe el correo, retornamos el siguiente mensaje
                    Toast.makeText(this, "El correo ya esta registrado para otro proveedor", Toast.LENGTH_SHORT).show();
                }

            } else { // si existe el proveedor, retorna el siguiente mensaje
                Toast.makeText(this, "El proveedor ya se encuentra registrado", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Dede completar todos los campos para continuar.", Toast.LENGTH_SHORT).show();
        }

    }

    // Metodo para listar proveedores
    public void proveedoresListar() {

        list_pro = new ArrayList<HashMap<String, String>>();

        // Consulta SQL para proveedores
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname, null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        // Consultamos los datos de los proveedores
        Cursor list_proveedores = db.rawQuery
                ("select id_proveedor, id_ciudad, razon_social, ruc, tipo_persona, telefono, direccion, email from proveedores order by razon_social", null);

        // ciclo while por cada fila
        while (list_proveedores.moveToNext()) {
            // Creamos un diccionario con los valores
            HashMap<String, String> dict = new HashMap<String, String>();
            dict.put("id_proveedor", list_proveedores.getString(0));
            dict.put("id_ciudad", list_proveedores.getString(1));
            dict.put("razon_social", list_proveedores.getString(2));
            dict.put("ruc", list_proveedores.getString(3));
            dict.put("tipo_persona", list_proveedores.getString(4));
            dict.put("telefono", list_proveedores.getString(5));
            dict.put("direccion", list_proveedores.getString(6));
            dict.put("email", list_proveedores.getString(7));


            list_pro.add(dict); // Agregamos a la matrix

            // Agregamos los datos en la vista
            SimpleAdapter adapter = new SimpleAdapter(this, list_pro, R.layout.list_proveedores, new String[]{"id_proveedor", "razon_social", "ruc", "tipo_persona", "telefono"}, new int[]{R.id.tv_proveedor_id, R.id.tv_proveedor_razon_social, R.id.tv_proveedor_ruc, R.id.tv_proveedor_tipo_persona, R.id.tv_proveedor_telefono});
            lv_proveedores.setAdapter(adapter);
        }
        db.close();

        // Al momento de dar click sobre uno de los valores, se autocompletara los datos para la modificacion o eliminación
        lv_proveedores.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> mymap = (HashMap<String, String>) parent.getItemAtPosition(position);

                et_id_proveedor.setText(mymap.get("id_proveedor"));
                et_razon_social.setText(mymap.get("razon_social"));
                et_ruc.setText(mymap.get("ruc"));
                et_telefono.setText(mymap.get("telefono"));
                et_direccion.setText(mymap.get("direccion"));
                et_email.setText(mymap.get("email"));
                et_id_ciudad.setText(mymap.get("id_ciudad"));


                // Habilitamos los botones de Eliminar y Modificar
                bt_nuevo.setVisibility(View.INVISIBLE);
                bt_buscar.setVisibility(View.INVISIBLE);
                bt_modificar.setVisibility(View.VISIBLE);
                bt_eliminar.setVisibility(View.VISIBLE);
                bt_cancelar.setVisibility(View.VISIBLE);
            }
        });
    }

    // Metodo para modificar proveedor
    public void proveedoresModificar(View view) {
        // Obtenemos los valores actuales

        String id_proveedor_str = et_id_proveedor.getText().toString();
        String proveedores_razon_social_str = et_razon_social.getText().toString();
        String proveedores_ruc_str = et_ruc.getText().toString();
        String proveedores_telefono_str = et_telefono.getText().toString();
        String proveedores_direccion_str = et_direccion.getText().toString();
        String proveedores_email_str = et_email.getText().toString();
        String proveedores_id_ciudad_str = et_id_ciudad.getText().toString();
        String proveedores_tipo_persona_str = sp_tipo_persona.getSelectedItem().toString();


        // Abrimos la DB para proveedores mediante el id
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname, null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        //Validamos que los campos no esten vacios
        if (!proveedores_razon_social_str.isEmpty() && !proveedores_ruc_str.isEmpty() && !proveedores_tipo_persona_str.isEmpty() && !proveedores_telefono_str.isEmpty()
                && !proveedores_direccion_str.isEmpty() && !proveedores_email_str.isEmpty() && !proveedores_id_ciudad_str.isEmpty()) {

            // Validamos si el proveedor ya existe
            Cursor cedula = db.rawQuery("select id_proveedor from proveedores where ruc = '" + proveedores_ruc_str + "'", null);

            if (!cedula.moveToFirst()) {

                // Validamos si el correo ya existe
                Cursor email = db.rawQuery("select id_proveedor from proveedores where email = '" + proveedores_email_str + "'", null);
                if (!email.moveToFirst()) { // Si es correcto, procede a la modificación

                    // Editamos el usuario
                    if (!id_proveedor_str.isEmpty()) {
                        ContentValues proveedores = new ContentValues();

                        proveedores.put("id_ciudad", proveedores_id_ciudad_str);
                        proveedores.put("razon_social", proveedores_razon_social_str);
                        proveedores.put("ruc", proveedores_ruc_str);
                        proveedores.put("tipo_persona", proveedores_tipo_persona_str);
                        proveedores.put("telefono", proveedores_telefono_str);
                        proveedores.put("direccion", proveedores_direccion_str);
                        proveedores.put("email", proveedores_email_str);

                        // Realizamos el update en la base de datos
                        int editar = db.update("proveedores", proveedores, "id_proveedor=" + id_proveedor_str, null);
                        db.close();

                        if (editar >= 1) {
                            Toast.makeText(this, "Registro Actualizado", Toast.LENGTH_SHORT).show();
                            proveedoresListar(); // Actualizar lista
                        } else { // En caso de que el id no exista
                            Toast.makeText(this, "¡El registro indicado no existe!", Toast.LENGTH_SHORT).show();
                        }

                        proveedoresListar(); // Actualizamos lista

                    } else {
                        Toast.makeText(this, "Ha ocurrido un error. Consultar con el administrador.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, "El correo ya esta registrado para otro proveedor", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, "El proveedor ya se encuentra registrado", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Dede completar todos los campos para continuar.", Toast.LENGTH_SHORT).show();
        }
    }

    // Metodo para eliminar el proveedor por el id
    public void proveedoresEliminar(View view) {
        // Abrimos la DB para usuario mediante el id
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname, null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        // Eliminamos el proveedor
        String id_proveedor = et_id_proveedor.getText().toString();
        if (!id_proveedor.isEmpty()) {
            int eliminar = db.delete("proveedores", "id_proveedor=" + id_proveedor, null);
            db.close();

            et_id_ciudad.setText("");
            et_razon_social.setText("");
            et_ruc.setText("");
            et_telefono.setText("");
            et_direccion.setText("");
            et_email.setText("");

            if (eliminar >= 1) {
                Toast.makeText(this, "El proveedor fue eliminado exitosamente", Toast.LENGTH_SHORT).show();
                proveedoresListar(); // Actualizar lista
            } else { // En caso de que el id no exista
                Toast.makeText(this, "¡El registro que intenta eliminar no existe!", Toast.LENGTH_SHORT).show();
            }
        } else { // en caso de que no complete el campo indicado
            Toast.makeText(this, "¡Debe seleccionar un proveedor para eliminar!", Toast.LENGTH_SHORT).show();
        }
    }

    public void proveedoresCancelar(View view) {

        et_id_ciudad.setText("");
        et_razon_social.setText("");
        et_ruc.setText("");
        et_telefono.setText("");
        et_direccion.setText("");
        et_email.setText("");
        recreate();

    }

    // Metodo para buscar empleados
    public void proveedoresBuscar(View view) {

        // Buscaremos por RUC o RAZON SOCIAL
        String ruc = et_ruc.getText().toString();
        String razon_social = et_razon_social.getText().toString();

        // Consulta SQL para usuario
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname, null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        // Verificar que el campo se encuentre cargado
        if (!ruc.isEmpty() && !razon_social.isEmpty()) {
            items = new ArrayList<HashMap<String, String>>();

            // Consultamos los datos de los usuarios
            Cursor rows = db.rawQuery
                    ("select id_proveedor, id_ciudad, razon_social, ruc, tipo_persona, telefono, direccion, email from proveedores where ruc and razon_social like '%\" + usu_cuenta + \"%' order by razon_social", null);
            // Verificamos si nos devuelve datos
            if (rows.getCount() >= 1) {
                Toast.makeText(this, "Busqueda en proceso...", Toast.LENGTH_SHORT).show();
                // ciclo while por cada fila
                while (rows.moveToNext()) {
                    // Creamos un diccionario con los valores
                    HashMap<String, String> dict = new HashMap<String, String>();
                    dict.put("id_proveedor", rows.getString(0));
                    dict.put("id_ciudad", rows.getString(1));
                    dict.put("razon_social", rows.getString(2));
                    dict.put("ruc", rows.getString(3));
                    dict.put("tipo_persona", rows.getString(4));
                    dict.put("telefono", rows.getString(5));
                    dict.put("direccion", rows.getString(6));
                    dict.put("email", rows.getString(7));


                    items.add(dict); // Agregamos a la matrix

                    // Agregamos los datos en la vista
                    SimpleAdapter adapter = new SimpleAdapter(this, items, R.layout.list_usuario, new String[]{"id_proveedor", "razon_social", "ruc", "tipo_persona", "telefono"}, new int[]{R.id.tv_id, R.id.tv_proveedor_id, R.id.tv_proveedor_ruc, R.id.tv_proveedor_tipo_persona, R.id.tv_proveedor_telefono});
                    lv_proveedores.setAdapter(adapter);
                }
                db.close();

                // Al momento de dar click sobre uno de los valores, se autocompletara los datos para la modificacion o eliminación
                lv_proveedores.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        HashMap<String, String> mymap = (HashMap<String, String>) parent.getItemAtPosition(position);

                        et_id_proveedor.setText(mymap.get("id_proveedor"));
                        et_razon_social.setText(mymap.get("razon_social"));
                        et_ruc.setText(mymap.get("ruc"));
                        et_telefono.setText(mymap.get("telefono"));
                        et_direccion.setText(mymap.get("direccion"));
                        et_email.setText(mymap.get("email"));
                        et_id_ciudad.setText(mymap.get("id_ciudad"));


                        // Habilitamos los botones de Eliminar y Modificar
                        bt_nuevo.setVisibility(View.INVISIBLE);
                        bt_buscar.setVisibility(View.INVISIBLE);
                        bt_modificar.setVisibility(View.VISIBLE);
                        bt_eliminar.setVisibility(View.VISIBLE);
                        bt_cancelar.setVisibility(View.VISIBLE);
                    }
                });
            } else { // en caso de no devolver datos
                Toast.makeText(this, "No se encontro proveedor", Toast.LENGTH_SHORT).show();
            }
        } else { // en caso de no completar el campo correspondiente
            Toast.makeText(this, "¡Debe indicar un usuario para buscar!", Toast.LENGTH_SHORT).show();
        }

    }


}