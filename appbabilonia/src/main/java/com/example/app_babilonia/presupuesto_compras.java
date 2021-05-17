package com.example.app_babilonia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.app_babilonia.database.SQLControl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class presupuesto_compras extends AppCompatActivity {

    private static final String TAG = "MyActivity";


    // Inicializar variable
    DrawerLayout drawerLayout;
    public TextView tv_titulo_activity; // Titulo
    public TextView tv_usuario; // Usuario
    public TextView tv_id_empleado; // Usuario
    public LinearLayout menu_pedido_compras;
    public LinearLayout menu_presupuesto_compras;
    public LinearLayout menu_empleados;
    public LinearLayout menu_ciudades;
    public LinearLayout menu_usuarios;
    public LinearLayout menu_marcas;
    public LinearLayout menu_roles;


    private ListView lv_items; // Lista de valores
    ArrayList<HashMap<String, String>> items; // Diccionario Array para valores
    ArrayList<HashMap<String, String>> items_proveedores; // Diccionario Array para valores de proveedores

    // Creamos los objetos para el activity
    private EditText et_id, et_id_empleado, et_id_proveedor;
    private EditText et_empleado, et_observacion, et_fecha;
    private Spinner sp_estado, sp_provedor;
    //private ImageButton bt_fecha_nacimiento, bt_fecha_incorporacion;
    private Button bt_nuevo, bt_modificar, bt_eliminar, bt_listar, bt_cancelar;

    // calendarios
    //final Calendar calendar_fecha_nacimiento = Calendar.getInstance();
    //final Calendar calendar_fecha_incorporacion = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presupuesto_compras);

        // Asignar Variable
        drawerLayout = findViewById(R.id.drawer_layout);
        // Definimos el titulo
        tv_titulo_activity = (TextView) findViewById(R.id.titulo_activity);
        String titulo = getResources().getString(R.string.menu_presupuesto_compras).toString();
        tv_titulo_activity.setText(titulo);

        // Definimos el usuario
        tv_usuario = (TextView) findViewById(R.id.nav_usuario);
        tv_id_empleado = (TextView) findViewById(R.id.presupuesto_compras_txt_id_empleado);
        et_empleado = (EditText) findViewById(R.id.presupuesto_compras_txt_empleado);


        // Recuperamos valores almacenados
        SharedPreferences preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
        tv_usuario.setText(preferences.getString("nombre_apellido", ""));
        tv_id_empleado.setText(preferences.getString("e.id_empleado", ""));
        et_empleado.setText(preferences.getString("nombre_apellido", ""));
//        String nombre_empleado = preferences.getString("nombre_apellido", "");
        //      et_empleado.setText(nombre_empleado);


        // Verificamos permisos
        String id_rol = preferences.getString("u.id_rol", "");
        menu_pedido_compras = (LinearLayout) findViewById(R.id.menu_pedido_compras);
        menu_presupuesto_compras = (LinearLayout) findViewById(R.id.menu_presupuesto_compras);
        menu_empleados = (LinearLayout) findViewById(R.id.menu_empleados);
        menu_ciudades = (LinearLayout) findViewById(R.id.menu_ciudades);
        menu_usuarios = (LinearLayout) findViewById(R.id.menu_usuarios);
        menu_roles = (LinearLayout) findViewById(R.id.menu_roles);
        menu_marcas = (LinearLayout) findViewById(R.id.menu_marcas);
        if (!id_rol.equals("1")) { // si no es admin, ocultamos los siguientes menu
            menu_usuarios.setVisibility(View.INVISIBLE);
            menu_empleados.setVisibility(View.INVISIBLE);
            menu_ciudades.setVisibility(View.INVISIBLE);
            menu_roles.setVisibility(View.INVISIBLE);
            menu_marcas.setVisibility(View.INVISIBLE);

        } else {
            //    Toast.makeText(this, "Administrador de Sistemas", Toast.LENGTH_SHORT).show();
        }


        // Integracion de objetos / campos
        et_id = (EditText) findViewById(R.id.presupuesto_compras_txt_id);
        et_id_proveedor = (EditText) findViewById(R.id.presupuesto_compras_txt_id_proveedor);
        et_observacion = (EditText) findViewById(R.id.presupuesto_compras_txt_observacion);
        et_fecha = (EditText) findViewById(R.id.presupuesto_compras_txt_fecha);


        //SACAMOS LA FECHA COMPLETA
        Date d = new Date();
        SimpleDateFormat fecc = new SimpleDateFormat("d/MM/yyyy");
        String fecha = fecc.format(d);
        et_fecha.setText(fecha);

        // Integracion de objetos / botones
        bt_nuevo = (Button) findViewById(R.id.presupuesto_compras_bt_nuevo);
        bt_listar = (Button) findViewById(R.id.presupuesto_compras_bt_listar);

        // Integracion de objetos / spinner
        sp_estado = (Spinner) findViewById(R.id.presupuesto_compras_sp_estado);
        sp_provedor = (Spinner) findViewById(R.id.presupuesto_compras_sp_proveedores);

        // Integracion de objetos / lista
        lv_items = (ListView) findViewById(R.id.item_lista);
        items = new ArrayList<HashMap<String, String>>();
        items_proveedores = new ArrayList<HashMap<String, String>>();

        // Integracion de objetos / spinner staticos
        String[] estado = {"Pendiente", "Procesado", "Anulado"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, estado);
        sp_estado.setAdapter(adapter);


        // Integracion de objetos / spinner ciudad
        items_proveedores = new ArrayList<HashMap<String, String>>();
        // Consulta SQL para usuario y contraseña
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname, null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        // Consultamos los datos de la ciudad
        Cursor proveedor = db.rawQuery
                ("select id_proveedor, razon_social || ', RUC: ' || ruc as proveedor_ruc from proveedores order by razon_social ", null);
        // ciclo while por cada fila
        while (proveedor.moveToNext()) {
            // Creamos un diccionario con los valores
            HashMap<String, String> dict = new HashMap<String, String>();
            dict.put("id", proveedor.getString(0));
            dict.put("proveedor_ruc", proveedor.getString(1));
            items_proveedores.add(dict); // Agregamos a la matrix

            // Agregamos los datos en la vista
            SimpleAdapter adapter_proveedores = new SimpleAdapter(this, items_proveedores, android.R.layout.simple_spinner_item, new String[]{"proveedor_ruc"}, new int[]{android.R.id.text1});
            sp_provedor.setAdapter(adapter_proveedores);
        }
        db.close();

        // Al momento de dar click sobre uno de los valores, se autocompletara los datos para la el item seleccionado
        sp_provedor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> mymap = (HashMap<String, String>) parent.getItemAtPosition(position);
                et_id_proveedor.setText(mymap.get("id"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        // Mostramos la lista
        //PaqueteListar();
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

    public void ClickPedidoCompras(View view) { redirectActivity(this, presupuesto_compras.class); }

    public void ClickPresupuestoCompras(View view) { redirectActivity(this, presupuesto_compras.class); }

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

/*    // Metodo registrar
    public void PaqueteRegistrar(View View) throws ParseException {
        // Consulta SQL para registrar
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname,null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        // Obtenemos el ultimo codigo del item registrado
        Cursor row = db.rawQuery("select coalesce(max(paq_cod),0) + 1 as cod from paquete", null);
        String paquete_cod_str = "";
        if (row.moveToFirst()){
            paquete_cod_str = row.getString(0);
        } else {
            Toast.makeText(this, "Ha ocurrido un error. Consultar con el administrador.", Toast.LENGTH_SHORT).show();
        }
        String paquete_nombre_str = et_nombre.getText().toString();
        String paquete_fecha_salida_str = et_salida.getText().toString();
        String paquete_fecha_entrada_str = et_entrada.getText().toString();
        String paquete_cant_persona_str = et_cant_persona.getText().toString();
        String paquete_financiacion_str = sp_financiacion.getSelectedItem().toString();
        String paquete_codigo_ciudad_str = et_id_ciudad.getText().toString();
        String paquete_codigo_item_str = et_id_item.getText().toString();
        String paquete_paqciu_cant_dia_str = sp_cant_dias.getSelectedItem().toString();
        String paquete_paqciu_precio_str = et_ciudad_precio.getText().toString();

        if(!paquete_nombre_str.isEmpty() && !paquete_fecha_salida_str.isEmpty() && !paquete_financiacion_str.isEmpty()){
            // Validamos is el paquete ya existe
            Cursor nombre = db.rawQuery("select paq_cod from paquete where paq_descr = '"+ paquete_nombre_str +"'", null);
            if(!nombre.moveToFirst()){

                // Consultamos el precio del item
                Cursor item_precio = db.rawQuery("select item_precio from item where item_cod =" + paquete_codigo_item_str, null);
                String paquete_paqitem_precio_str = "";
                if(item_precio.moveToFirst()){
                    paquete_paqitem_precio_str = item_precio.getString(0);
                }

                // Obtenemos los datos tipo text y lo pasamos a Date para hacer el calculo de fecha
                DateFormat format = new SimpleDateFormat("dd/MM/yy", Locale.US);
                Date fecha_entrada  = format.parse(paquete_fecha_entrada_str);
                Date fecha_salida = format.parse(paquete_fecha_salida_str);
                // Si la fecha de salida es menor a la fecha de entrada
                if (fecha_salida.compareTo(fecha_entrada) < 0){

                    // Verificamos si la suma de los días en la ciudad se encuentra en el rango de llegada
                    // Primero sumamos los dias seleccionados a la fecha de salida
                    Calendar salida_dias = Calendar.getInstance();
                    salida_dias.setTime(fecha_salida);
                    salida_dias.add(Calendar.DATE, Integer.valueOf(paquete_paqciu_cant_dia_str));
                    // Si la fecha de salida es menor a la fecha de entrada
                    if (salida_dias.getTime().compareTo(fecha_entrada) < 0){

                         // Insertamos el paquete
                         ContentValues paquete = new ContentValues();
                         paquete.put("paq_cod", paquete_cod_str);
                         paquete.put("paq_descr", paquete_nombre_str);
                         paquete.put("paq_fecha_salida", paquete_fecha_salida_str);
                         paquete.put("paq_fecha_llegada", paquete_fecha_entrada_str);
                         paquete.put("paq_cant_persona", paquete_cant_persona_str);
                         paquete.put("paq_fin_cuota", paquete_financiacion_str);
                         paquete.put("paq_precio_ciudad", paquete_paqciu_precio_str);
                         paquete.put("paq_precio_item", paquete_paqitem_precio_str);

                         db.insert("paquete", null, paquete);

                         // Insertamos el paquete ciudad
                         ContentValues paquete_ciudad = new ContentValues();
                         paquete_ciudad.put("paq_cod", paquete_cod_str);
                         paquete_ciudad.put("ciu_cod", paquete_codigo_ciudad_str);
                         paquete_ciudad.put("paqciu_cant_dia", paquete_paqciu_cant_dia_str);
                         paquete_ciudad.put("paqciu_precio", paquete_paqciu_precio_str);

                         db.insert("paquete_ciudad", null, paquete_ciudad);

                         // Insertamos el paquete item
                         ContentValues paquete_item = new ContentValues();
                         paquete_item.put("paq_cod", paquete_cod_str);
                         paquete_item.put("item_cod", paquete_codigo_item_str);
                         paquete_item.put("paqitem_total", paquete_paqitem_precio_str);

                         db.insert("paquete_item", null, paquete_item);

                         // Cerramos la conexion
                         db.close();

                         et_nombre.setText("");
                         et_entrada.setText("");
                         et_salida.setText("");
                         et_cant_persona.setText("");
                         et_ciudad_precio.setText("");

                        Toast.makeText(this, "Registro Completado", Toast.LENGTH_SHORT).show();

                    }else { // en caso contrario, lanzar el siguiente mensaje
                        Toast.makeText(this, "La cantidad de días en la ciudad supera la fecha de entrada", Toast.LENGTH_SHORT).show();
                    }

                }else{ // caso contrario, lanza el siguiente mensaje
                    Toast.makeText(this, "La Fecha de Entrada es menor a la Fecha de Salida", Toast.LENGTH_LONG).show();
                }


            }else {
                Toast.makeText(this, "Paquete existente en el sistema", Toast.LENGTH_SHORT).show();

            }
        }else {
            Toast.makeText(this, "Dede completar los campos.", Toast.LENGTH_SHORT).show();

        }



    }


    public void PaqueteBuscar(View view){
        // obtenemos el valor a buscar
        String busqueda = et_nombre.getText().toString();
        // Consultamos si contiene datos
        if (!busqueda.isEmpty()){
            // Listamos los paquetes registrados mediante un filtro
            Intent intent = new Intent(this, list_presupuesto_compras.class);
            intent.putExtra("filtro", "1");
            intent.putExtra("nombre", busqueda);
            startActivity(intent);
        }else {
            Toast.makeText(this, "¡Debe indicar una descripción para buscar!", Toast.LENGTH_SHORT).show();
        }
    }*/

    public void PresupuestoCompraListar(View view) {
        // Listamos los paquetes registrados
        Intent intent = new Intent(this, list_presupuesto_compras.class);
        startActivity(intent);
    }
}