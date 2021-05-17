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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app_babilonia.database.SQLControl;

import java.util.ArrayList;
import java.util.HashMap;

public class usuario extends AppCompatActivity {

    // Inicializar variable
    DrawerLayout drawerLayout;
    public TextView tv_titulo_activity; // Titulo
    public TextView tv_usuario; // Usuario

    private ListView lv_items; // Lista de Usuarios
    ArrayList<HashMap<String, String>> items; // Diccionario Array para valores de usuarios

    // Creamos los objetos para el activity
    private EditText et_codigo, et_nombre, et_apellido, et_tel, et_email, et_cuenta, et_password;
    private Button btn_registrar, btn_editar, btn_eliminar, btn_buscar, btn_cancelar;
    private CheckBox cbox_admin, cbox_bloqueado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        // Asignar Variable
        drawerLayout = findViewById(R.id.drawer_layout);
        // Definimos el titulo
        tv_titulo_activity = (TextView)findViewById(R.id.titulo_activity);
        String titulo = getResources().getString(R.string.menu_presupuesto_compras).toString();
        tv_titulo_activity.setText(titulo);

        // Definimos el usuario
        tv_usuario = (TextView)findViewById(R.id.nav_usuario);
        // Recuperamos valores almacenados
        SharedPreferences preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
        tv_usuario.setText(preferences.getString("nombre_apellido", ""));

        // Integracion de objetos / campos
        et_codigo = (EditText)findViewById(R.id.usuario_txt_codigo);
        et_nombre = (EditText)findViewById(R.id.usuario_txt_nombre);
        et_apellido = (EditText)findViewById(R.id.usuario_txt_apellido);
        et_tel = (EditText)findViewById(R.id.usuario_txt_telefono);
        et_email = (EditText)findViewById(R.id.usuario_txt_email);
        et_cuenta = (EditText)findViewById(R.id.usuario_txt_cuenta);
        et_password = (EditText)findViewById(R.id.usuario_txt_password);
        btn_registrar = (Button)findViewById(R.id.usuario_btn_registrar);
        btn_editar = (Button)findViewById(R.id.usuario_btn_editar);
        btn_eliminar = (Button)findViewById(R.id.usuario_btn_eliminar);
        btn_buscar = (Button)findViewById(R.id.usuario_btn_buscar);
        btn_cancelar = (Button)findViewById(R.id.usuario_btn_cancelar);
        cbox_admin = (CheckBox)findViewById(R.id.usuario_cbox_admin);
        cbox_bloqueado = (CheckBox)findViewById(R.id.usuario_cbox_bloqueado);

        // Integracion de objetos / lista
        lv_items = (ListView)findViewById(R.id.usuario_lista);
        items = new ArrayList<HashMap<String, String>>();

        // Mostramos la lista
        UsuarioListar();

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
        recreate();
    }

    public void ClickCiudad(View view){
        // Cambiamos al activity seleccionado
        MainActivity.redirectActivity(this, ciudades.class);
    }

    public void ClickItem(View view){
        // Cambiamos al activity seleccionado
        MainActivity.redirectActivity(this, item.class);
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

    // Metodo Registrar
    public void UsuarioRegistrar(View view){
        // Consulta SQL para usuario y contraseña
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname,null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        // Obtenemos el ultimo codigo de usuario registrado
        Cursor row = db.rawQuery("select coalesce(max(usu_cod),0) + 1 as cod from usuario", null);
        String usu_cod_str = "";
        if (row.moveToFirst()){
            usu_cod_str = row.getString(0);
        } else {
            Toast.makeText(this, "Ha ocurrido un error. Consultar con el administrador.", Toast.LENGTH_SHORT).show();
        }

        String usu_nombre_str = et_nombre.getText().toString();
        String usu_apellido_str = et_apellido.getText().toString();
        String usu_telefono_str = et_tel.getText().toString();
        String usu_email_str = et_email.getText().toString();
        String usu_cuenta_str = et_cuenta.getText().toString();
        String usu_password_str = et_password.getText().toString();
        String usu_tipo_str = "";
        String usu_estado_str = "";
        if (cbox_admin.isChecked()){
            usu_tipo_str = "admin";
        }else{
            usu_tipo_str = "cliente";
        }
        if (cbox_bloqueado.isChecked()){
            usu_estado_str = "bloqueado";
        }else {
            usu_estado_str = "activo";
        }

        if(!usu_nombre_str.isEmpty() && !usu_apellido_str.isEmpty() && !usu_cuenta_str.isEmpty() && !usu_password_str.isEmpty()){

            // Validamos si el usuario existe
            Cursor cuenta = db.rawQuery("select usu_cod from usuario where usu_cuenta = '"+ usu_cuenta_str +"'", null);
            if (!cuenta.moveToFirst()){

                // Validamos si el correo existe
                Cursor email = db.rawQuery("select usu_cod from usuario where usu_email = '"+ usu_email_str +"'", null);
                if (!email.moveToFirst()){

                    ContentValues usuario = new ContentValues();
                    usuario.put("usu_cod", usu_cod_str);
                    usuario.put("usu_nombre", usu_nombre_str);
                    usuario.put("usu_apellido", usu_apellido_str);
                    usuario.put("usu_tel", usu_telefono_str);
                    usuario.put("usu_email", usu_email_str);
                    usuario.put("usu_cuenta", usu_cuenta_str);
                    usuario.put("usu_pass", usu_password_str);
                    usuario.put("usu_tipo", usu_tipo_str);
                    usuario.put("usu_estado", usu_estado_str);

                    db.insert("usuario", null, usuario);
                    db.close();

                    et_nombre.setText("");
                    et_apellido.setText("");
                    et_email.setText("");
                    et_tel.setText("");
                    et_cuenta.setText("");
                    et_password.setText("");
                    cbox_admin.setChecked(false);
                    cbox_bloqueado.setChecked(false);

                    UsuarioListar(); // Actualizar lista

                    Toast.makeText(this, "Registro Completado", Toast.LENGTH_SHORT).show();

                }else { // si existe el correo, retornamos el siguiente mensaje
                    Toast.makeText(this, "Correo existente en el sistema", Toast.LENGTH_SHORT).show();
                }

            }else { // si existe el usuario, retorna el siguiente mensaje
            Toast.makeText(this, "Usuario existente en el sistema", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Dede completar los campos.", Toast.LENGTH_SHORT).show();
        }

    }

    // Metodo para listar usuarios
    public void UsuarioListar(){

        items = new ArrayList<HashMap<String, String>>();

        // Consulta SQL para usuario y contraseña
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname,null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        // Consultamos los datos de los usuarios
        Cursor rows = db.rawQuery
                ("select usu_cod, usu_cuenta, usu_pass, usu_tipo, usu_estado, usu_nombre, usu_apellido, usu_nombre || ' ' || usu_apellido as nombre_apellido, usu_tel, usu_email from usuario order by usu_cod desc", null);
        // ciclo while por cada fila
        while (rows.moveToNext()){
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
            SimpleAdapter adapter = new SimpleAdapter(this, items, R.layout.list_usuario, new String[] {"Codigo","Nombre_Apellido","Cuenta","Tipo"}, new int[] {R.id.tv_id, R.id.tv_nombre_apellido, R.id.tv_cedula_identidad, R.id.tv_estado});
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
                et_apellido.setText(mymap.get("Apellido"));
                et_tel.setText(mymap.get("Telefono"));
                et_email.setText(mymap.get("Email"));
                et_cuenta.setText(mymap.get("Cuenta"));
                et_password.setText(mymap.get("Password"));
                if (mymap.get("Tipo").equals("admin")){
                    cbox_admin.setChecked(true);
                }else {
                    cbox_admin.setChecked(false);
                }
                if (mymap.get("Estado").equals("bloqueado")){
                    cbox_bloqueado.setChecked(true);
                }else {
                    cbox_bloqueado.setChecked(false);
                }

                // Habilitamos los botones de Eliminar y Editar
                btn_registrar.setVisibility(View.INVISIBLE);
                btn_buscar.setVisibility(View.INVISIBLE);
                btn_editar.setVisibility(View.VISIBLE);
                btn_eliminar.setVisibility(View.VISIBLE);
                btn_cancelar.setVisibility(View.VISIBLE);
            }
        });
    }

    // Metodo para editar usuario
    public void UsuarioEditar(View view){
        // Obtenemos los valores actuales
        String usu_cod_str = et_codigo.getText().toString();
        String usu_nombre_str = et_nombre.getText().toString();
        String usu_apellido_str = et_apellido.getText().toString();
        String usu_telefono_str = et_tel.getText().toString();
        String usu_email_str = et_email.getText().toString();
        String usu_cuenta_str = et_cuenta.getText().toString();
        String usu_password_str = et_password.getText().toString();
        String usu_tipo_str = "";
        String usu_estado_str = "";
        if (cbox_admin.isChecked()){
            usu_tipo_str = "admin";
        }else{
            usu_tipo_str = "cliente";
        }
        if (cbox_bloqueado.isChecked()){
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
                        UsuarioListar(); // Actualizar lista
                    }else{ // En caso de que el id no exista
                        Toast.makeText(this, "¡El registro indicado no existe!", Toast.LENGTH_SHORT).show();
                    }

                    UsuarioListar(); // Actualizamos lista

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
                            UsuarioListar(); // Actualizar lista
                        }else{ // En caso de que el id no exista
                            Toast.makeText(this, "¡El registro indicado no existe!", Toast.LENGTH_SHORT).show();
                        }

                        UsuarioListar(); // Actualizamos lista

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
                            UsuarioListar(); // Actualizar lista
                        }else{ // En caso de que el id no exista
                            Toast.makeText(this, "¡El registro indicado no existe!", Toast.LENGTH_SHORT).show();
                        }

                        UsuarioListar(); // Actualizamos lista

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
                                UsuarioListar(); // Actualizar lista
                            }else{ // En caso de que el id no exista
                                Toast.makeText(this, "¡El registro indicado no existe!", Toast.LENGTH_SHORT).show();
                            }

                            UsuarioListar(); // Actualizamos lista

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
        String usu_cod = et_codigo.getText().toString();
        if(!usu_cod.isEmpty()){
            int eliminar = db.delete("usuario", "usu_cod=" + usu_cod, null);
            db.close();

            et_nombre.setText("");
            et_apellido.setText("");
            et_email.setText("");
            et_tel.setText("");
            et_cuenta.setText("");
            et_password.setText("");

            if(eliminar >= 1){
                Toast.makeText(this, "Registro Eliminado", Toast.LENGTH_SHORT).show();
                UsuarioListar(); // Actualizar lista
            }else{ // En caso de que el id no exista
                Toast.makeText(this, "¡El registro indicado no existe!", Toast.LENGTH_SHORT).show();
            }
        }else{ // en caso de que no complete el campo indicado
            Toast.makeText(this, "¡Debe indicar un codigo!", Toast.LENGTH_SHORT).show();
        }
    }

    public void UsuarioCancelar(View view){
        et_nombre.setText("");
        et_apellido.setText("");
        et_email.setText("");
        et_tel.setText("");
        et_cuenta.setText("");
        et_password.setText("");
        cbox_admin.setChecked(false);
        cbox_bloqueado.setChecked(false);
        recreate();
    }

    // Metodo para buscar usuarios
    public void UsuarioBuscar(View view){
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
                        et_apellido.setText(mymap.get("Apellido"));
                        et_tel.setText(mymap.get("Telefono"));
                        et_email.setText(mymap.get("Email"));
                        et_cuenta.setText(mymap.get("Cuenta"));
                        et_password.setText(mymap.get("Password"));
                        if (mymap.get("Tipo").equals("admin")){
                            cbox_admin.setChecked(true);
                        }else {
                            cbox_admin.setChecked(false);
                        }
                        if (mymap.get("Estado").equals("bloqueado")){
                            cbox_bloqueado.setChecked(true);
                        }else {
                            cbox_bloqueado.setChecked(false);
                        }

                        // Habilitamos los botones de Eliminar y Editar
                        btn_registrar.setVisibility(View.INVISIBLE);
                        btn_buscar.setVisibility(View.INVISIBLE);
                        btn_editar.setVisibility(View.VISIBLE);
                        btn_eliminar.setVisibility(View.VISIBLE);
                        btn_cancelar.setVisibility(View.VISIBLE);
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