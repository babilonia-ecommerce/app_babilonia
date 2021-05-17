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

public class empleados extends AppCompatActivity {

    // Inicializar las variables
    DrawerLayout drawerLayout;
    public TextView tv_titulo_activity; // Titulo
    public TextView tv_usuario; // Usuario
    public LinearLayout menu_presupuesto_compras;
    public LinearLayout menu_empleados;
    public LinearLayout menu_ciudades;
    public LinearLayout menu_usuarios;
    public LinearLayout menu_marcas;
    public LinearLayout menu_roles;


    private ListView lv_empleados; // Lista de Empleados

    ArrayList<HashMap<String, String>> items; // Diccionario Array para valores de ciudades usuarios
    ArrayList<HashMap<String, String>> items_ciudades; // Diccionario Array para valores de ciudades
    ArrayList<HashMap<String, String>> items_usuarios; // Diccionario Array para valores de usuarios

    // Creamos los objetos para el activity
    private EditText et_id_empleado, et_nombres, et_apellidos, et_cedula_identidad, et_nacionalidad,  et_telefono, et_direccion, et_email, et_legajo, et_cuenta, et_password;
    private EditText et_id_ciudad, et_id_usuario;
    private EditText et_fecha_nacimiento, et_fecha_incorporacion;
    private Spinner sp_estado_civil, sp_sexo, sp_ciudad, sp_usuario;
    private ImageButton bt_fecha_nacimiento, bt_fecha_incorporacion;
    private CheckBox cbox_estado;
    private Button bt_nuevo, bt_modificar, bt_eliminar, bt_buscar, bt_cancelar;
    // calendarios
    final Calendar calendar_fecha_nacimiento = Calendar.getInstance();
    final Calendar calendar_fecha_incorporacion = Calendar.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_empleados);

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
        tv_usuario.setText(preferences.getString("nombre_empleado", ""));

        // Verificamos permisos
        String id_rol = preferences.getString("u.id_rol", "");
        menu_presupuesto_compras = (LinearLayout) findViewById(R.id.menu_presupuesto_compras);
        menu_empleados = (LinearLayout) findViewById(R.id.menu_empleados);
        menu_ciudades = (LinearLayout) findViewById(R.id.menu_ciudades);
        menu_usuarios = (LinearLayout) findViewById(R.id.menu_usuarios);
        menu_roles = (LinearLayout) findViewById(R.id.menu_roles);
        menu_marcas = (LinearLayout) findViewById(R.id.menu_marcas);
        if(!id_rol.equals("1")){ // si no es admin, ocultamos los siguientes menu
            menu_usuarios.setVisibility(View.INVISIBLE);
            menu_empleados.setVisibility(View.INVISIBLE);
            menu_ciudades.setVisibility(View.INVISIBLE);
            menu_roles.setVisibility(View.INVISIBLE);
            menu_marcas.setVisibility(View.INVISIBLE);

        }else {
            //    Toast.makeText(this, "Administrador de Sistemas", Toast.LENGTH_SHORT).show();
        }


        // Integracion de objetos / txt
        et_id_empleado = (EditText)findViewById(R.id.empleados_txt_id);
        et_nombres = (EditText)findViewById(R.id.empleados_txt_nombres);
        et_apellidos = (EditText)findViewById(R.id.empleados_txt_apellidos);
        et_cedula_identidad = (EditText)findViewById(R.id.empleados_txt_cedula_identidad);
        et_nacionalidad = (EditText)findViewById(R.id.empleados_txt_nacionalidad);
        et_telefono = (EditText)findViewById(R.id.empleados_txt_telefono);
        et_direccion = (EditText)findViewById(R.id.empleados_txt_direccion);
        et_email = (EditText)findViewById(R.id.empleados_txt_email);
        et_legajo = (EditText)findViewById(R.id.empleados_txt_legajo);
        et_id_ciudad = (EditText)findViewById(R.id.empleados_txt_id_ciudad);
        et_id_usuario = (EditText)findViewById(R.id.empleados_txt_id_usuario);
        et_fecha_nacimiento = (EditText)findViewById(R.id.empleados_txt_fecha_nacimiento);
        et_fecha_incorporacion = (EditText)findViewById(R.id.empleados_txt_fecha_incorporacion);

        et_cuenta = (EditText)findViewById(R.id.usuario_txt_cuenta);
        et_password = (EditText)findViewById(R.id.usuario_txt_password);

        // Integracion de objetos / spinner
        sp_estado_civil = (Spinner)findViewById(R.id.empleados_sp_estado_civil);
        sp_sexo = (Spinner)findViewById(R.id.empleados_sp_sexo);
        sp_ciudad = (Spinner)findViewById(R.id.empleados_sp_ciudad);
        sp_usuario = (Spinner)findViewById(R.id.empleados_sp_usuario);

        //Integracion de objetos / ImgBoton
        bt_fecha_nacimiento = (ImageButton)findViewById(R.id.empleados_bt_fecha_nacimiento);
        bt_fecha_incorporacion = (ImageButton)findViewById(R.id.empleados_bt_fecha_incorporacion);

        //Integracion de objetos / CBox
        cbox_estado = (CheckBox)findViewById(R.id.empleados_box_estado);

        //Integracion de objetos / Button
        bt_nuevo = (Button)findViewById(R.id.empleados_bt_nuevo);
        bt_modificar = (Button)findViewById(R.id.empleados_bt_modificar);
        bt_eliminar = (Button)findViewById(R.id.empleados_bt_eliminar);
        bt_buscar = (Button)findViewById(R.id.empleados_bt_buscar);
        bt_cancelar = (Button)findViewById(R.id.empleados_bt_cancelar);

        // Integracion de objetos / lista
        lv_empleados = (ListView)findViewById(R.id.empleados_lista);
        items = new ArrayList<HashMap<String, String>>();

        // Integracion de objetos / spiner dinamicos
        items_ciudades = new ArrayList<HashMap<String, String>>();
        items_usuarios = new ArrayList<HashMap<String, String>>();

        // Consulta SQL para spinner  dinamicos = ciudades y usuarios
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname,null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        // Consultamos los datos de las ciudades
        Cursor ciudad = db.rawQuery
                ("select id_ciudad, descripcion from ciudades order by descripcion", null);
        // ciclo while por cada fila
        while (ciudad.moveToNext()){
            // Creamos un diccionario con los valores
            HashMap<String, String> dict = new HashMap<String, String>();
            dict.put("id_ciudad", ciudad.getString(0));
            dict.put("descripcion", ciudad.getString(1));
            items_ciudades.add(dict); // Agregamos a la matrix
            // Agregamos los datos en la vista
            SimpleAdapter adapter_ciudades = new SimpleAdapter(this, items_ciudades, android.R.layout.simple_spinner_item, new String[] {"descripcion"}, new int[] {android.R.id.text1});
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
        // Consultamos los datos de los usuarios
        Cursor usuarios = db.rawQuery
                ("select id_usuario, descripcion from usuarios order by descripcion", null);
        // ciclo while por cada fila
        while (usuarios.moveToNext()){
            // Creamos un diccionario con los valores
            HashMap<String, String> dict = new HashMap<String, String>();
            dict.put("id_usuario", usuarios.getString(0));
            dict.put("descripcion", usuarios.getString(1));
            items_usuarios.add(dict); // Agregamos a la matrix
            // Agregamos los datos en la vista
            SimpleAdapter adapter_usuarios = new SimpleAdapter(this, items_usuarios, android.R.layout.simple_spinner_item, new String[] {"descripcion"}, new int[] {android.R.id.text1});
            sp_usuario.setAdapter(adapter_usuarios);
        }
        db.close();
        // Al momento de dar click sobre uno de los usuarios, se autocompletara los datos para la el item seleccionado
        sp_usuario.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> mymap = (HashMap<String, String>) parent.getItemAtPosition(position);
                et_id_usuario.setText(mymap.get("id_usuario"));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        // Integracion de objetos / spinner staticos
        String [] sexo = {"Masculino", "Femenino"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sexo);
        sp_sexo.setAdapter(adapter);

        String [] estado_civil = {"casado/a", "soltero", "viudo"};
        ArrayAdapter<String> adapter_2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, estado_civil);
        sp_estado_civil.setAdapter(adapter_2);

        // Preparamos el calendario de fecha de nacimiento
        DatePickerDialog.OnDateSetListener date_fecha_nacimiento = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar_fecha_nacimiento.set(Calendar.YEAR, year);
                calendar_fecha_nacimiento.set(Calendar.MONTH, monthOfYear);
                calendar_fecha_nacimiento.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SetCalendarioFechaNacimiento();
            }

        };

        bt_fecha_nacimiento.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(empleados.this, date_fecha_nacimiento, calendar_fecha_nacimiento
                        .get(Calendar.YEAR), calendar_fecha_nacimiento.get(Calendar.MONTH),
                        calendar_fecha_nacimiento.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // Preparamos el calendario fecha de incorporacion
        DatePickerDialog.OnDateSetListener date_fecha_incorporacion = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar_fecha_incorporacion.set(Calendar.YEAR, year);
                calendar_fecha_incorporacion.set(Calendar.MONTH, monthOfYear);
                calendar_fecha_incorporacion.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SetCalendarioFechaIncorporacion();
            }

        };

        bt_fecha_incorporacion.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(empleados.this, date_fecha_incorporacion, calendar_fecha_incorporacion
                        .get(Calendar.YEAR), calendar_fecha_incorporacion.get(Calendar.MONTH),
                        calendar_fecha_incorporacion.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        // Mostramos la lista
        EmpleadosListar();

    }

    private void SetCalendarioFechaNacimiento() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        et_fecha_nacimiento.setText(sdf.format(calendar_fecha_nacimiento.getTime()));
        Date d_entrada = calendar_fecha_nacimiento.getTime();
    }


    private void SetCalendarioFechaIncorporacion() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        et_fecha_incorporacion.setText(sdf.format(calendar_fecha_incorporacion.getTime()));
        Date d_salida = calendar_fecha_incorporacion.getTime();
    }
    //------------------Inicio: Menu de navegacion--------------------------------------------------

    public void ClickMenu(View view){
        MainActivity.openDrawer(drawerLayout);
    }

    public void ClickLogo(View view){
        MainActivity.closeDrawer(drawerLayout);
    }

    public static void redirectActivity(Activity activity, Class aClass) {
        // Inicializamos el Intent
        Intent intent = new Intent(activity, aClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public void ClickHome(View view){
        redirectActivity(this, MainActivity.class);
    }

    //    public void ClickPedidoCompras(View view){
    //       redirectActivity(this, pedido_compras.class);
    //    }

    //    public void ClickPresupuestoCompras(View view){
    //       redirectActivity(this, presupuesto_compras.class);
    //    }

    public void ClickPaquete(View view){
        redirectActivity(this, mercaderias.class);
    }

    //    public void ClickProveedores(View view){
    //       redirectActivity(this, proveedores.class);
    //    }

    public void ClickEmpleados(View view){
        redirectActivity(this, empleados.class);
    }

    public void ClickCiudades(View view){
        redirectActivity(this, ciudades.class);
    }

    //    public void ClickMarcas(View view){
    //       redirectActivity(this, marcas.class);
    //    }

    public void ClickUsuario(View view){
       redirectActivity(this, usuario.class);
    }

    //    public void ClickRoles(View view){
    //       redirectActivity(this, roles.class);
    //    }

    public void ClickPerfil(View view){
        redirectActivity(this, perfil.class);
    }

    public void ClickAcerca(View view){
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

    //------------------Fin Menu de Navegación------------------------------------------------------

    //------------------Metodo para nuevo empleado--------------------------------------------------
    public void EmpleadoNuevo(View view){
        // Consulta SQL
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname,null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        // Obtenemos el ultimo id del empleado registrado
        Cursor row = db.rawQuery("select coalesce(max(id_empleado),0) + 1 as id from empleados", null);
        String id_str = "";
        if (row.moveToFirst()){
            id_str = row.getString(0);
        } else {
            Toast.makeText(this, "Ha ocurrido un error. Consultar con el administrador.", Toast.LENGTH_SHORT).show();
        }

        String empleados_nombres_str = et_nombres.getText().toString();
        String empleados_apellidos_str = et_apellidos.getText().toString();
        String empleados_cedula_identidad_str = et_cedula_identidad.getText().toString();
        String empleados_nacionalidad_str = et_nacionalidad.getText().toString();
        String empleados_telefono_str = et_telefono.getText().toString();
        String empleados_direccion_str = et_direccion.getText().toString();
        String empleados_email_str = et_email.getText().toString();
        String empleados_legajo_str = et_legajo.getText().toString();
        String empleados_id_ciudad_str = et_id_ciudad.getText().toString();
        String empleados_id_usuario_str = et_id_usuario.getText().toString();
        String empleados_sexo_str = sp_sexo.getSelectedItem().toString();
        String empleados_estado_civil_str = sp_estado_civil.getSelectedItem().toString();
        String empleados_fecha_nacimiento_str = et_fecha_nacimiento.getText().toString();
        String empleados_fecha_incorporacion_str = et_fecha_incorporacion.getText().toString();
        String empleados_estado_str = "";

        if (cbox_estado.isChecked()){
            empleados_estado_str = "Activo";
        }else {
            empleados_estado_str = "Inactivo";
        }

        //Validamos que los campos no esten vacios
        if(!empleados_nombres_str.isEmpty() && !empleados_apellidos_str.isEmpty() && !empleados_cedula_identidad_str.isEmpty() && !empleados_fecha_nacimiento_str.isEmpty()
                && !empleados_sexo_str.isEmpty() && !empleados_estado_civil_str.isEmpty() && !empleados_nacionalidad_str.isEmpty() && !empleados_telefono_str.isEmpty()
                && !empleados_direccion_str.isEmpty() && !empleados_email_str.isEmpty() && !empleados_fecha_incorporacion_str.isEmpty() && !empleados_legajo_str.isEmpty()
                && !empleados_id_ciudad_str.isEmpty() && !empleados_id_usuario_str.isEmpty()){

            // Validamos si el empleado ya existe
            Cursor cedula = db.rawQuery("select id_empleado from empleados where cedula_identidad = '"+ empleados_cedula_identidad_str +"'", null);
            if (!cedula.moveToFirst()){

                // Validamos si el correo ya existe
                Cursor email = db.rawQuery("select id_empleado from empleados where email = '"+ empleados_email_str +"'", null);
                if (!email.moveToFirst()){

                    // Asignamos la descripcion seleccionada en la tabla usuarios
                    Cursor usuarios_descripcion = db.rawQuery("select descripcion from usuarios where id_usuario =" + empleados_id_usuario_str, null);
                    String usuarios_descripcion_str = "";
                    if(usuarios_descripcion.moveToFirst()){
                        usuarios_descripcion_str = usuarios_descripcion.getString(0);
                    }

                    ContentValues empleados = new ContentValues();
                    empleados.put("id_empleado", id_str);
                    empleados.put("id_ciudad", empleados_id_ciudad_str);
                    empleados.put("id_usuario", empleados_id_usuario_str);
                    empleados.put("nombres", empleados_nombres_str);
                    empleados.put("apellidos", empleados_apellidos_str);
                    empleados.put("cedula_identidad", empleados_cedula_identidad_str);
                    empleados.put("fecha_nac", empleados_fecha_nacimiento_str);
                    empleados.put("sexo", empleados_sexo_str);
                    empleados.put("estado_civil", empleados_estado_civil_str);
                    empleados.put("nacionalidad", empleados_nacionalidad_str);
                    empleados.put("telefono", empleados_telefono_str);
                    empleados.put("direccion", empleados_direccion_str);
                    empleados.put("email", empleados_email_str);
                    empleados.put("legajo", empleados_legajo_str);
                    empleados.put("estado", empleados_estado_str);
                    empleados.put("fecha_incorporacion", empleados_fecha_incorporacion_str);

                    db.insert("empleados", null, empleados);
                    db.close();

                    et_id_ciudad.setText("");
                    et_id_usuario.setText("");
                    et_nombres.setText("");
                    et_apellidos.setText("");
                    et_cedula_identidad.setText("");
                    et_fecha_nacimiento.setText("");
                    et_nacionalidad.setText("");
                    et_telefono.setText("");
                    et_direccion.setText("");
                    et_email.setText("");
                    et_legajo.setText("");
                    et_fecha_incorporacion.setText("");
                    cbox_estado.setChecked(false);


                    EmpleadosListar(); // Actualizar lista

                    Toast.makeText(this, "Se ha registrado al empleado exitosamente!", Toast.LENGTH_SHORT).show();

                }else { // si existe el correo, retornamos el siguiente mensaje
                    Toast.makeText(this, "El correo ya esta registrado con otro usuario", Toast.LENGTH_SHORT).show();
                }

            }else { // si existe el empleado, retorna el siguiente mensaje
            Toast.makeText(this, "El empleado ya se encuentra registrado", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Dede completar todos los campos para continuar.", Toast.LENGTH_SHORT).show();
        }

    }

    // Metodo para listar empleados
    public void EmpleadosListar(){

        items = new ArrayList<HashMap<String, String>>();

        // Consulta SQL para empleados
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname,null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        // Consultamos los datos de los usuarios
        Cursor rows = db.rawQuery
                ("select id_empleado, id_ciudad, id_usuario, nombres, apellidos, nombres || ' ' || apellidos as nombre_apellido, cedula_identidad, fecha_nac, sexo, " +
                        " estado_civil, nacionalidad, telefono, direccion, email, fecha_incorporacion, legajo, estado from empleados order by nombres", null);
        // ciclo while por cada fila
        while (rows.moveToNext()){
            // Creamos un diccionario con los valores
            HashMap<String, String> dict = new HashMap<String, String>();
            dict.put("id_empleado", rows.getString(0));
            dict.put("id_ciudad", rows.getString(1));
            dict.put("id_usuario", rows.getString(2));
            dict.put("nombres", rows.getString(3));
            dict.put("apellidos", rows.getString(4));
            dict.put("nombre_apellido", rows.getString(5));
            dict.put("cedula_identidad", rows.getString(6));
            dict.put("fecha_nac", rows.getString(7));
            dict.put("sexo", rows.getString(8));
            dict.put("estado_civil", rows.getString(9));
            dict.put("nacionalidad", rows.getString(10));
            dict.put("telefono", rows.getString(11));
            dict.put("direccion", rows.getString(12));
            dict.put("email", rows.getString(13));
            dict.put("fecha_incorporacion", rows.getString(14));
            dict.put("legajo", rows.getString(15));
            dict.put("estado", rows.getString(16));

            items.add(dict); // Agregamos a la matrix

            // Agregamos los datos en la vista
            SimpleAdapter adapter = new SimpleAdapter(this, items, R.layout.list_usuario, new String[] {"id_empleado","nombre_apellido","cedula_identidad","estado"}, new int[] {R.id.tv_id, R.id.tv_nombre_apellido, R.id.tv_cedula_identidad, R.id.tv_estado});
            lv_empleados.setAdapter(adapter);
        }
        db.close();

        // Al momento de dar click sobre uno de los valores, se autocompletara los datos para la modificacion o eliminación
        lv_empleados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> mymap = (HashMap<String, String>) parent.getItemAtPosition(position);

                et_id_empleado.setText(mymap.get("id_empleado"));
                et_nombres.setText(mymap.get("nombres"));
                et_apellidos.setText(mymap.get("apellidos"));
                et_cedula_identidad.setText(mymap.get("cedula_identidad"));
                et_fecha_nacimiento.setText(mymap.get("fecha_nac"));
                et_nacionalidad.setText(mymap.get("nacionalidad"));
                et_telefono.setText(mymap.get("telefono"));
                et_direccion.setText(mymap.get("direccion"));
                et_email.setText(mymap.get("email"));
                et_legajo.setText(mymap.get("legajo"));
                et_id_ciudad.setText(mymap.get("id_ciudad"));
                et_id_usuario.setText(mymap.get("id_usuario"));
                et_fecha_incorporacion.setText(mymap.get("fecha_incorporacion"));

                if (mymap.get("estado").equals("activo")){
                    cbox_estado.setChecked(true);
                }else {
                    cbox_estado.setChecked(false);
                }



                // Habilitamos los botones de Eliminar y Modificar
                bt_nuevo.setVisibility(View.INVISIBLE);
                bt_buscar.setVisibility(View.INVISIBLE);
                bt_modificar.setVisibility(View.VISIBLE);
                bt_eliminar.setVisibility(View.VISIBLE);
                bt_cancelar.setVisibility(View.VISIBLE);
            }
        });
    }

    // Metodo para modificar empleado
    public void EmpleadosModificar(View view){
        // Obtenemos los valores actuales
        String usu_cod_str = et_id_empleado.getText().toString();
        String usu_nombre_str = et_nombres.getText().toString();
        String usu_apellido_str = et_apellidos.getText().toString();
        String usu_telefono_str = et_telefono.getText().toString();
        String usu_email_str = et_email.getText().toString();
        String usu_cuenta_str = et_cuenta.getText().toString();
        String usu_password_str = et_password.getText().toString();
        String usu_tipo_str = "";
        String usu_estado_str = "";

        if (cbox_estado.isChecked()){
            usu_estado_str = "bloqueado";
        }else {
            usu_estado_str = "activo";
        }

        // Abrimos la DB para usuario mediante el id
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname,null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        // Consultamos si la cuenta pertenece al usuario antes de actualizar
        Cursor cuenta_uno = db.rawQuery("select usu_cod from usuario where usu_cuenta = '"+ usu_cuenta_str +"' and usu_cod =" + usu_cod_str, null);
        if(cuenta_uno.moveToFirst()){ // Si es correcto, procede a consultar el correo
            Cursor email_uno = db.rawQuery("select usu_cod from usuario where usu_email = '"+ usu_email_str +"' and usu_cod =" + usu_cod_str, null);
            if(email_uno.moveToFirst()){ // Si es correcto, procede a la modificación
                // Editamos el usuario
                if(!usu_cod_str.isEmpty()){
                    ContentValues datos = new ContentValues();
                    datos.put("usu_nombre", usu_nombre_str);
                    datos.put("usu_apellido", usu_apellido_str);
                    datos.put("usu_email", usu_email_str);
                    datos.put("usu_tel", usu_telefono_str);
                    datos.put("usu_cuenta", usu_cuenta_str);
                    datos.put("usu_email", usu_email_str);
                    datos.put("usu_pass", usu_password_str);
                    datos.put("usu_tipo", usu_tipo_str);
                    datos.put("usu_estado", usu_estado_str);

                    // Realizamos el update en la base de datos
                    int editar = db.update("usuario", datos, "usu_cod=" + usu_cod_str, null);
                    db.close();


                    if(editar >= 1){
                        Toast.makeText(this, "Registro Actualizado", Toast.LENGTH_SHORT).show();
                        EmpleadosListar(); // Actualizar lista
                    }else{ // En caso de que el id no exista
                        Toast.makeText(this, "¡El registro indicado no existe!", Toast.LENGTH_SHORT).show();
                    }

                    EmpleadosListar(); // Actualizamos lista

                }else {
                    Toast.makeText(this, "Ha ocurrido un error. Consultar con el administrador.", Toast.LENGTH_SHORT).show();
                }

            }else { // SI no es correcto, verifica que el correo no este asignado a otro usuario
                Cursor email_dos = db.rawQuery("select usu_cod from usuario where usu_email = '"+ usu_email_str +"'", null);
                if(!email_dos.moveToFirst()){ // Si no encuentra, procede a la actualizacion
                    // Editamos el usuario
                    if(!usu_cod_str.isEmpty()){
                        ContentValues datos = new ContentValues();
                        datos.put("usu_nombre", usu_nombre_str);
                        datos.put("usu_apellido", usu_apellido_str);
                        datos.put("usu_email", usu_email_str);
                        datos.put("usu_tel", usu_telefono_str);
                        datos.put("usu_cuenta", usu_cuenta_str);
                        datos.put("usu_email", usu_email_str);
                        datos.put("usu_pass", usu_password_str);
                        datos.put("usu_tipo", usu_tipo_str);
                        datos.put("usu_estado", usu_estado_str);

                        // Realizamos el update en la base de datos
                        int editar = db.update("usuario", datos, "usu_cod=" + usu_cod_str, null);
                        db.close();


                        if(editar >= 1){
                            Toast.makeText(this, "Registro Actualizado", Toast.LENGTH_SHORT).show();
                            EmpleadosListar(); // Actualizar lista
                        }else{ // En caso de que el id no exista
                            Toast.makeText(this, "¡El registro indicado no existe!", Toast.LENGTH_SHORT).show();
                        }

                        EmpleadosListar(); // Actualizamos lista

                    }else {
                        Toast.makeText(this, "Ha ocurrido un error. Consultar con el administrador.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(this, "Correo existente en el sistema", Toast.LENGTH_SHORT).show();
                }
            }
        }else { // Si no encuentra, consulta si el login no esta asignado a otro usuario
            Cursor cuenta_dos = db.rawQuery("select usu_cod from usuario where usu_cuenta = '"+ usu_cuenta_str +"'", null);
            if(!cuenta_dos.moveToFirst()){ // Si no encuentra, procede a consultar el correo
                Cursor email_uno = db.rawQuery("select usu_cod from usuario where usu_email = '"+ usu_email_str +"' and usu_cod =" + usu_cod_str, null);
                if(email_uno.moveToFirst()){ // Si es correcto, procede a la modificación
                    // Editamos el usuario
                    if(!usu_cod_str.isEmpty()){
                        ContentValues datos = new ContentValues();
                        datos.put("usu_nombre", usu_nombre_str);
                        datos.put("usu_apellido", usu_apellido_str);
                        datos.put("usu_email", usu_email_str);
                        datos.put("usu_tel", usu_telefono_str);
                        datos.put("usu_cuenta", usu_cuenta_str);
                        datos.put("usu_email", usu_email_str);
                        datos.put("usu_pass", usu_password_str);
                        datos.put("usu_tipo", usu_tipo_str);
                        datos.put("usu_estado", usu_estado_str);

                        // Realizamos el update en la base de datos
                        int editar = db.update("usuario", datos, "usu_cod=" + usu_cod_str, null);
                        db.close();


                        if(editar >= 1){
                            Toast.makeText(this, "Registro Actualizado", Toast.LENGTH_SHORT).show();
                            EmpleadosListar(); // Actualizar lista
                        }else{ // En caso de que el id no exista
                            Toast.makeText(this, "¡El registro indicado no existe!", Toast.LENGTH_SHORT).show();
                        }

                        EmpleadosListar(); // Actualizamos lista

                    }else {
                        Toast.makeText(this, "Ha ocurrido un error. Consultar con el administrador.", Toast.LENGTH_SHORT).show();
                    }
                }else { // SI no es correcto, verifica que el correo no este asignado a otro usuario
                    Cursor email_dos = db.rawQuery("select usu_cod from usuario where usu_email = '"+ usu_email_str +"'", null);
                    if(!email_dos.moveToFirst()){ // Si no encuentra, procede a la actualizacion
                        // Editamos el usuario
                        if(!usu_cod_str.isEmpty()){
                            ContentValues datos = new ContentValues();
                            datos.put("usu_nombre", usu_nombre_str);
                            datos.put("usu_apellido", usu_apellido_str);
                            datos.put("usu_email", usu_email_str);
                            datos.put("usu_tel", usu_telefono_str);
                            datos.put("usu_cuenta", usu_cuenta_str);
                            datos.put("usu_email", usu_email_str);
                            datos.put("usu_pass", usu_password_str);
                            datos.put("usu_tipo", usu_tipo_str);
                            datos.put("usu_estado", usu_estado_str);

                            // Realizamos el update en la base de datos
                            int editar = db.update("usuario", datos, "usu_cod=" + usu_cod_str, null);
                            db.close();


                            if(editar >= 1){
                                Toast.makeText(this, "Registro Actualizado", Toast.LENGTH_SHORT).show();
                                EmpleadosListar(); // Actualizar lista
                            }else{ // En caso de que el id no exista
                                Toast.makeText(this, "¡El registro indicado no existe!", Toast.LENGTH_SHORT).show();
                            }

                            EmpleadosListar(); // Actualizamos lista

                        }else {
                            Toast.makeText(this, "Ha ocurrido un error. Consultar con el administrador.", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(this, "Correo existente en el sistema", Toast.LENGTH_SHORT).show();
                    }
                }
            }else { // Si encuentra, lanza el siguiente mensaje
                Toast.makeText(this, "Usuario existente en el sistema", Toast.LENGTH_SHORT).show();
            }
        }

    }

    // Metodo para eliminar el usuario por el id
    public void UsuarioEliminar(View view){
        // Abrimos la DB para usuario mediante el id
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname,null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        // Eliminamos el usuario
        String usu_cod = et_id_empleado.getText().toString();
        if(!usu_cod.isEmpty()){
            int eliminar = db.delete("usuario", "usu_cod=" + usu_cod, null);
            db.close();

            et_nombres.setText("");
            et_apellidos.setText("");
            et_email.setText("");
            et_telefono.setText("");
            et_cuenta.setText("");
            et_password.setText("");

            if(eliminar >= 1){
                Toast.makeText(this, "Registro Eliminado", Toast.LENGTH_SHORT).show();
                EmpleadosListar(); // Actualizar lista
            }else{ // En caso de que el id no exista
                Toast.makeText(this, "¡El registro indicado no existe!", Toast.LENGTH_SHORT).show();
            }
        }else{ // en caso de que no complete el campo indicado
            Toast.makeText(this, "¡Debe indicar un codigo!", Toast.LENGTH_SHORT).show();
        }
    }

    public void UsuarioCancelar(View view){
        et_id_ciudad.setText("");
        et_id_usuario.setText("");
        et_nombres.setText("");
        et_apellidos.setText("");
        et_cedula_identidad.setText("");
        et_fecha_nacimiento.setText("");
        et_nacionalidad.setText("");
        et_telefono.setText("");
        et_direccion.setText("");
        et_email.setText("");
        et_legajo.setText("");
        et_fecha_incorporacion.setText("");
        cbox_estado.setChecked(false);
        recreate();
    }

    // Metodo para buscar usuarios
    public void EmpleadoBuscar(View view){
        // Buscaremos solo por la cuenta de usuario
        String usu_cuenta = et_cuenta.getText().toString();

        // Consulta SQL para usuario
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname,null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        // Verificar que el campo se encuentre cargado
        if(!usu_cuenta.isEmpty()){
            items = new ArrayList<HashMap<String, String>>();
            // Consultamos los datos de los usuarios
            Cursor rows = db.rawQuery
                    ("select usu_cod, usu_cuenta, usu_pass, usu_tipo, usu_estado, usu_nombre, usu_apellido, usu_nombre || ' ' || usu_apellido as nombre_apellido, usu_tel, usu_email from usuario where usu_cuenta like '%" + usu_cuenta + "%' order by usu_cod desc", null);
            // Verificamos si nos devuelve datos
            if (rows.getCount() >= 1){
                Toast.makeText(this, "Iniciando busqueda...", Toast.LENGTH_SHORT).show();
                // ciclo while por cada fila
                while (rows.moveToNext()) {
                    // Creamos un diccionario con los valores
                    HashMap<String, String> dict = new HashMap<String, String>();
                    dict.put("Codigo", rows.getString(0));
                    dict.put("Cuenta", rows.getString(1));
                    dict.put("Password", rows.getString(2));
                    dict.put("Tipo", rows.getString(3));
                    dict.put("Estado", rows.getString(4));
                    dict.put("Nombre", rows.getString(5));
                    dict.put("Apellido", rows.getString(6));
                    dict.put("Nombre_Apellido", rows.getString(7));
                    dict.put("Telefono", rows.getString(8));
                    dict.put("Email", rows.getString(9));
                    items.add(dict); // Agregamos a la matrix

                    // Agregamos los datos en la vista
                    SimpleAdapter adapter = new SimpleAdapter(this, items, R.layout.list_usuario, new String[]{"Codigo", "Nombre_Apellido", "Cuenta", "Tipo"}, new int[]{R.id.tv_id, R.id.tv_nombre_apellido, R.id.tv_cedula_identidad, R.id.tv_estado});
                    lv_empleados.setAdapter(adapter);
                }
                db.close();

                // Al momento de dar click sobre uno de los valores, se autocompletara los datos para la modificacion o eliminación
                lv_empleados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        HashMap<String, String> mymap = (HashMap<String, String>) parent.getItemAtPosition(position);
                        et_id_empleado.setText(mymap.get("Codigo"));
                        et_nombres.setText(mymap.get("Nombre"));
                        et_apellidos.setText(mymap.get("Apellido"));
                        et_telefono.setText(mymap.get("Telefono"));
                        et_email.setText(mymap.get("Email"));
                        et_cuenta.setText(mymap.get("Cuenta"));
                        et_password.setText(mymap.get("Password"));
                        if (mymap.get("Estado").equals("bloqueado")){
                            cbox_estado.setChecked(true);
                        }else {
                            cbox_estado.setChecked(false);
                        }

                        // Habilitamos los botones de Eliminar y Editar
                        bt_nuevo.setVisibility(View.INVISIBLE);
                        bt_buscar.setVisibility(View.INVISIBLE);
                        bt_modificar.setVisibility(View.VISIBLE);
                        bt_eliminar.setVisibility(View.VISIBLE);
                        bt_cancelar.setVisibility(View.VISIBLE);
                    }
                });
            } else{ // en caso de no devolver datos
                Toast.makeText(this, "No se encuentran registros", Toast.LENGTH_SHORT).show();
            }
        }else { // en caso de no completar el campo correspondiente
            Toast.makeText(this, "¡Debe indicar un usuario para buscar!", Toast.LENGTH_SHORT).show();
        }

    }


}