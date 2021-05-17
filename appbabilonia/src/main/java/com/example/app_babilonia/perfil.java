package com.example.app_babilonia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app_babilonia.database.SQLControl;
import com.example.app_babilonia.R;

public class perfil extends AppCompatActivity {

    // Definimos los componenetes
    private EditText et_nombre, et_apellido, et_cuenta, et_password, et_email, et_telefono;
    private TextView tv_admin, tv_codigo;
    private Button btn_editar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Integracion de componentes
        tv_admin = (TextView)findViewById(R.id.perfil_txt_admin);
        et_nombre = (EditText)findViewById(R.id.perfil_txt_nombre);
        et_apellido = (EditText)findViewById(R.id.perfil_txt_apellido);
        et_cuenta = (EditText)findViewById(R.id.perfil_txt_cuenta);
        et_password = (EditText)findViewById(R.id.perfil_txt_password);
        et_email = (EditText)findViewById(R.id.perfil_txt_email);
        et_telefono = (EditText)findViewById(R.id.perfil_txt_tel);
        tv_codigo = (TextView)findViewById(R.id.perfil_txt_codigo);
        btn_editar = (Button)findViewById(R.id.perfil_btn_editar);

        // Recuperamos valores almacenados
        SharedPreferences preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
        String usu_cod = preferences.getString("codigo", ""); // Guardamos el id del login
        if(preferences.getString("tipo", "").equals("admin")){ // Verificamos si es un perfil admin
            tv_admin.setVisibility(View.VISIBLE); // Mostramos el icono de admin en caso de que si lo sea
        }

        // Establecemos los valores del perfil
        // Consulta SQL del perfil mediante el id del usuario
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname,null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();
        // Consultamos los datos de los usuarios
        Cursor row = db.rawQuery
                ("select usu_cod, usu_cuenta, usu_pass, usu_tipo, usu_estado, usu_nombre, usu_apellido, usu_tel, usu_email from usuario where usu_cod = " + usu_cod + " order by usu_cod desc", null);
        if(row.moveToFirst()){
            tv_codigo.setText(row.getString(0));
            et_cuenta.setText(row.getString(1));
            et_password.setText(row.getString(2));
            et_nombre.setText(row.getString(5));
            et_apellido.setText(row.getString(6));
            et_telefono.setText(row.getString(7));
            et_email.setText(row.getString(8));
        } else {
            Toast.makeText(this, "Ha ocurrido un problema con el perfil. Consultar con el administrador", Toast.LENGTH_SHORT).show();
        }

    }

    public void PerfilEditar(View view){
        // Obtenemos los valores actuales
        String usu_cod_str = tv_codigo.getText().toString();
        String usu_nombre_str = et_nombre.getText().toString();
        String usu_apellido_str = et_apellido.getText().toString();
        String usu_telefono_str = et_telefono.getText().toString();
        String usu_email_str = et_email.getText().toString();
        String usu_cuenta_str = et_cuenta.getText().toString();
        String usu_password_str = et_password.getText().toString();

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

                    // Realizamos el update en la base de datos
                    int editar = db.update("usuario", datos, "usu_cod=" + usu_cod_str, null);
                    db.close();

                    if(editar >= 1){
                        Toast.makeText(this, "Registro Actualizado", Toast.LENGTH_SHORT).show();

                    }else{ // En caso de que el id no exista
                        Toast.makeText(this, "¡El registro indicado no existe!", Toast.LENGTH_SHORT).show();
                    }

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

                        // Realizamos el update en la base de datos
                        int editar = db.update("usuario", datos, "usu_cod=" + usu_cod_str, null);
                        db.close();

                        if(editar >= 1){
                            Toast.makeText(this, "Registro Actualizado", Toast.LENGTH_SHORT).show();

                        }else{ // En caso de que el id no exista
                            Toast.makeText(this, "¡El registro indicado no existe!", Toast.LENGTH_SHORT).show();
                        }

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

                        // Realizamos el update en la base de datos
                        int editar = db.update("usuario", datos, "usu_cod=" + usu_cod_str, null);
                        db.close();

                        if(editar >= 1){
                            Toast.makeText(this, "Registro Actualizado", Toast.LENGTH_SHORT).show();

                        }else{ // En caso de que el id no exista
                            Toast.makeText(this, "¡El registro indicado no existe!", Toast.LENGTH_SHORT).show();
                        }

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

                            // Realizamos el update en la base de datos
                            int editar = db.update("usuario", datos, "usu_cod=" + usu_cod_str, null);
                            db.close();

                            if(editar >= 1){
                                Toast.makeText(this, "Registro Actualizado", Toast.LENGTH_SHORT).show();

                            }else{ // En caso de que el id no exista
                                Toast.makeText(this, "¡El registro indicado no existe!", Toast.LENGTH_SHORT).show();
                            }

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

        //lkasjdklas

    }
}