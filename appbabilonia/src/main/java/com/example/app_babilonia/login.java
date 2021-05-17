package com.example.app_babilonia;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.app_babilonia.api.bean.CityBean;
import com.example.app_babilonia.api.client.CityService;
import com.example.app_babilonia.database.SQLControl;
import com.example.app_babilonia.R;

import java.util.List;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class login extends AppCompatActivity {

    private EditText et_usuario, et_password;
    private List<CityBean> cities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getSupportActionBar().hide(); // Ocultamos la barra de titulo
        setContentView(R.layout.activity_login);

        // Inicializar variables
        et_usuario = (EditText)findViewById(R.id.login_txt_usuario);
        et_password = (EditText)findViewById(R.id.login_txt_password);
        this.testRetrofit();

    }

    private void testRetrofit() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:8080")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        CityService service = retrofit.create(CityService.class);
        Call<List<CityBean>> repos = service.getCities();
        repos.enqueue(new Callback<List<CityBean>>() {
            @Override
            public void onResponse(Call<List<CityBean>> call, Response<List<CityBean>> response) {
                Logger.getLogger("log").info("Estado: "+response.message());
                if(response.isSuccessful()){
                    System.out.println(response.body());
                    cities = response.body();
                    Logger.getLogger("log").info("respuesta"+response.body().toString());
                }
            }
            @Override
            public void onFailure(Call<List<CityBean>> call, Throwable t) {
                Logger.getLogger("log").info(t.getMessage());
            }
        });

    }



    // Metodo para mostrar información
    public void ClickInfo(View view){
        Info(this);
    }
    public static void Info(final Activity activity) {
        // Creamos una alerta de dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Babilonia Center"); // Titulo
        builder.setMessage("En caso de no recordar sus credenciales, contacte con los administradores del sistema \n \n" +
                "Desarrollado por: \nGerardo Ocampos gerardo.ocampos@turismospy.com \nDayanara Gaona dayanara.gaona@turismospy.com \n" +
                "Daniel Centurion daniel.centurion@turismospy.com \n \n Paraguay 2021 - UTIC"); // Mensaje de cierre
        builder.show();
    }


    // Metodo para iniciar sesión
    public void IniciarSesion(View view){
        // Obtenemos los valores de los campos del formulario
        String usu_cuenta_str = et_usuario.getText().toString();
        String usu_pass_str = et_password.getText().toString();

        if(!usu_cuenta_str.isEmpty() && !usu_pass_str.isEmpty()){
            // Consulta SQL para usuario y contraseña
            String dbname = getResources().getString(R.string.dbname).toString();
            SQLControl admin = new SQLControl(this, dbname,null, 1);
            SQLiteDatabase db = admin.getReadableDatabase();

            // Consultamos los datos del usuario
            Cursor row = db.rawQuery
                    ("select u.id_usuario, e.nombres || ' ' || e.apellidos as nombre_empleado, u.id_rol, u.estado, e.id_empleado  " +
                            " from usuarios u " +
                            " left join empleados e on u.id_usuario = e.id_usuario " +
                            " where  u.descripcion = '" + usu_cuenta_str + "' " +
                            " and u.contrasena = '" + usu_pass_str + "'", null);
            if(row.moveToFirst()){ // Si existe el usuario
                db.close();
                // Validamos si el usuario esta bloqueado
                String estado = row.getString(3);
                if (!estado.equals("bloqueado")){
                    // Guardamos los datos de inicio de sesion del usuario
                    SharedPreferences preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
                    SharedPreferences.Editor obj_editor = preferences.edit();
                    obj_editor.putString("u.id_usuario",row.getString(0));
                    obj_editor.putString("nombre_empleado",row.getString(1));
                    obj_editor.putString("u.id_rol",row.getString(2));
                    obj_editor.putString("e.id_empleado",row.getString(4));
                    obj_editor.commit();

                    // Iniciamos el menu principal
                    Intent menu = new Intent(this, MainActivity.class);
                    startActivity(menu);

                }else{ // En caso de que se encuentre bloqueado
                    Toast.makeText(this, "El usuario se encuentra bloqueado, debe contactar al administrador", Toast.LENGTH_SHORT).show();
                }
            } else { // No existe el usuario
                Toast.makeText(this, "Usuario o Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                db.close();
            }
        } else { // No completa los campos
            Toast.makeText(this, "Debe completar los campos usuario y contraseña para continuar", Toast.LENGTH_SHORT).show();
        }

    }



}