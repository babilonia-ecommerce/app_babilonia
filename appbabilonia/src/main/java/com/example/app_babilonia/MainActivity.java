package com.example.app_babilonia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Asignar las variables
        drawerLayout = findViewById(R.id.drawer_layout);
        // Definimos el titulo
        tv_titulo_activity = (TextView)findViewById(R.id.titulo_activity);
        String titulo = getResources().getString(R.string.bienvenido).toString();
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
    }

    // Metodo para abrir el menu
    public void ClickMenu(View view){
        // Abrimos el menu mediante el metodo "openDrawer"
        openDrawer(drawerLayout);
    }
    // Declaramos el metodo "openDrawer"
    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    // Metodo para cerrar el menu
    public void ClickLogo(View view){
        // cerramos el menu con el metodo "closeDrawer"
        closeDrawer(drawerLayout);
    }
    // Declaramos el metodo "closeDrawer"
    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void ClickHome(View view){
        recreate();
    }

    public void ClickUsuario(View view){
        // Cambiamos al activity seleccionado
        redirectActivity(this, usuario.class);
    }

    public void ClickCiudades(View view){
        // Cambiamos al activity seleccionado
        redirectActivity(this, ciudades.class);
    }


    public void ClickEmpleados(View view){
        redirectActivity(this, empleados.class);
    }

    public void ClickPedidoCompras(View view){
        redirectActivity(this, pedido_compras.class);
    }

    public void ClickPresupuestoCompras(View view){
        redirectActivity(this, presupuesto_compras.class);
    }

    public void ClickProveedores(View view) {

        redirectActivity(this, proveedores.class);
    }

    public void ClickPaquete(View view){
        redirectActivity(this, mercaderias.class);
    }

    public void ClickPerfil(View view){
        redirectActivity(this, perfil.class);
    }

    public void ClickAcerca(View view){
        Acerca(this);
    }
    public static void Acerca(final Activity activity) {
        // Creamos una alerta de dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("BabiloniaCenter"); // Titulo
        builder.setMessage("Tienda de Accesorios y articulos electronicos \n \nDesarrollado por: \nDamian Centurion \nLourdes Lezcano \nLaura Bogado"); // Mensaje de cierre
        builder.show();
    }

    public void ClickLogout(View view){
        // Cerramos la aplicacion
        logout(this);
    }
    public static void logout(final Activity activity) {
        // Creamos una alerta de dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Salir"); // Titulo
        builder.setMessage("¿Esta seguro de que desea cerrar sesion?"); // Mensaje de cierre
        // En caso de que seleccione 'Si'
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finishAffinity(); // Finalizamos el activity actual
                MainActivity.redirectActivity(activity, login.class); // Lo enviamos al activity 'login'
            }
        });
        // En caso de que seleccione 'NO'
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public static void redirectActivity(Activity activity, Class aClass) {
        // Inicializamos el Intent
        Intent intent = new Intent(activity, aClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Cerramos  el navegador
        closeDrawer(drawerLayout);
    }
}