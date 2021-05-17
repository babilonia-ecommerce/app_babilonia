package com.example.app_babilonia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app_babilonia.database.SQLControl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class mercaderias extends AppCompatActivity {

    private static final String TAG = "MyActivity";


    // Inicializar variable
    DrawerLayout drawerLayout;
    public TextView tv_titulo_activity; // Titulo
    public TextView tv_usuario; // Usuario

    private ListView lv_items; // Lista de valores
    ArrayList<HashMap<String, String>> items; // Diccionario Array para valores
    ArrayList<HashMap<String, String>> items_marcas; // Diccionario Array para valores ciudad
    ArrayList<HashMap<String, String>> items_item; // Diccionario Array para valores item

    // Creamos los objetos para el activity
    private EditText et_codigo, et_nombre, et_salida, et_entrada, et_cant_persona, et_codigo_ciudad, et_ciudad_precio, et_codigo_item;
    private Spinner sp_financiacion, sp_ciudad, sp_item, sp_cant_dias;
    private Button btn_registrar, btn_editar, btn_eliminar, btn_buscar, btn_cancelar;
    private ImageButton btn_calendario_salida, btn_calendario_entrada;
    
    // calendarios
    final Calendar calendario_salida = Calendar.getInstance();
    final Calendar calendario_entrada = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mercaderias);

        // Asignar Variable
        drawerLayout = findViewById(R.id.drawer_layout);
        // Definimos el titulo
        tv_titulo_activity = (TextView)findViewById(R.id.titulo_activity);
        String titulo = getResources().getString(R.string.menu_mercaderias).toString();
        tv_titulo_activity.setText(titulo);

        // Definimos el usuario
        tv_usuario = (TextView)findViewById(R.id.nav_usuario);
        // Recuperamos valores almacenados
        SharedPreferences preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
        tv_usuario.setText(preferences.getString("nombre_apellido", ""));

        // Integracion de objetos / campos
        et_codigo = (EditText)findViewById(R.id.txt_id_pedido_compras);
        et_nombre = (EditText)findViewById(R.id.paquete_txt_nombre);
        et_salida = (EditText)findViewById(R.id.paquete_txt_salida);
        et_entrada = (EditText)findViewById(R.id.paquete_txt_entrada);
        et_cant_persona = (EditText)findViewById(R.id.paquete_txt_cantidad_persona);
        et_codigo_ciudad = (EditText)findViewById(R.id.paquete_txt_codigo_ciudad);
        et_ciudad_precio = (EditText)findViewById(R.id.paquete_txt_ciudad_precio);
        et_codigo_item = (EditText)findViewById(R.id.paquete_txt_codigo_item);
        sp_ciudad = (Spinner)findViewById(R.id.paquete_sp_ciudad);
        sp_financiacion = (Spinner)findViewById(R.id.paquete_sp_financiacion);
        sp_item = (Spinner)findViewById(R.id.paquete_sp_item);
        sp_cant_dias = (Spinner)findViewById(R.id.paquete_sp_cant_dias);
        btn_registrar = (Button)findViewById(R.id.paquete_btn_registrar);
        btn_editar = (Button)findViewById(R.id.paquete_btn_editar);
        btn_eliminar = (Button)findViewById(R.id.paquete_btn_eliminar);
        btn_buscar = (Button)findViewById(R.id.paquete_btn_buscar);
        btn_cancelar = (Button)findViewById(R.id.paquete_btn_cancelar);
        btn_calendario_entrada = (ImageButton)findViewById(R.id.paquete_btn_calendario_entrada);
        btn_calendario_salida = (ImageButton)findViewById(R.id.paquete_btn_calendario_salida);

        // Integracion de objetos / lista
        lv_items = (ListView)findViewById(R.id.item_lista);
        items = new ArrayList<HashMap<String, String>>();
        items_marcas = new ArrayList<HashMap<String, String>>();
        items_item = new ArrayList<HashMap<String, String>>();

        // Integracion de objetos / spinner
        Integer [] financiacion = {1,2,4,6,12,15,24};
        ArrayAdapter <Integer> adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, financiacion);
        sp_financiacion.setAdapter(adapter);

        // Integracion de objetos / spinner
        Integer [] cantidad_dias = {1,3,7,15,30};
        ArrayAdapter <Integer> adapter_dias = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, cantidad_dias);
        sp_cant_dias.setAdapter(adapter_dias);

        // Integracion de objetos / spinner ciudad
        items_marcas = new ArrayList<HashMap<String, String>>();

        // Consulta SQL para usuario y contraseña
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname,null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        // Consultamos los datos de los usuarios
        Cursor ciudad = db.rawQuery
                ("select id_ciudad, descripcion from ciudades order by descripcion", null);

        // ciclo while por cada fila
        while (ciudad.moveToNext()){
            // Creamos un diccionario con los valores
            HashMap<String, String> dict = new HashMap<String, String>();
            dict.put("Id", ciudad.getString(0));
            dict.put("Descripcion", ciudad.getString(1));
            items_marcas.add(dict); // Agregamos a la matrix

            // Agregamos los datos en la vista
            SimpleAdapter adapter_ciudad = new SimpleAdapter(this, items_marcas, android.R.layout.simple_spinner_item, new String[] {"Descripcion"}, new int[] {android.R.id.text1});
            sp_ciudad.setAdapter(adapter_ciudad);
        }
        db.close();

        // Al momento de dar click sobre uno de los valores, se autocompletara los datos para la el item seleccionado
        sp_ciudad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> mymap = (HashMap<String, String>) parent.getItemAtPosition(position);
                et_codigo_ciudad.setText(mymap.get("Codigo"));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Integracion de objetos / spinner item
        items_item = new ArrayList<HashMap<String, String>>();
        // Consulta SQL para usuario y contraseña
        String dbname_item = getResources().getString(R.string.dbname).toString();
        SQLControl admin_item = new SQLControl(this, dbname,null, 1);
        SQLiteDatabase db_item = admin_item.getReadableDatabase();

        // Consultamos los datos de los usuarios
        Cursor item = db_item.rawQuery
                ("select item_cod, item_descr from item order by item_cod desc", null);
        // ciclo while por cada fila
        while (item.moveToNext()){
            // Creamos un diccionario con los valores
            HashMap<String, String> dict = new HashMap<String, String>();
            dict.put("Codigo", item.getString(0));
            dict.put("Nombre", item.getString(1));
            items_item.add(dict); // Agregamos a la matrix

            // Agregamos los datos en la vista
            SimpleAdapter adapter_item = new SimpleAdapter(this, items_item, android.R.layout.simple_spinner_item, new String[] {"Nombre"}, new int[] {android.R.id.text1});
            sp_item.setAdapter(adapter_item);
        }
        db.close();

        // Al momento de dar click sobre uno de los valores, se autocompletara los datos para la el item seleccionado
        sp_item.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> mymap = (HashMap<String, String>) parent.getItemAtPosition(position);
                et_codigo_item.setText(mymap.get("Codigo"));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        // Preparamos el calendario para la entrada
        DatePickerDialog.OnDateSetListener date_entrada = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendario_entrada.set(Calendar.YEAR, year);
                calendario_entrada.set(Calendar.MONTH, monthOfYear);
                calendario_entrada.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SetCalendarioEntrada();
            }

        };

        btn_calendario_entrada.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(mercaderias.this, date_entrada, calendario_entrada
                        .get(Calendar.YEAR), calendario_entrada.get(Calendar.MONTH),
                        calendario_entrada.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // Preparamos el calendario para la salida
        DatePickerDialog.OnDateSetListener date_salida = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendario_salida.set(Calendar.YEAR, year);
                calendario_salida.set(Calendar.MONTH, monthOfYear);
                calendario_salida.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SetCalendarioSalida();
            }

        };

        btn_calendario_salida.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(mercaderias.this, date_salida, calendario_salida
                        .get(Calendar.YEAR), calendario_salida.get(Calendar.MONTH),
                        calendario_salida.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        // Mostramos la lista
        //PaqueteListar();
    }

    private void SetCalendarioSalida() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        et_salida.setText(sdf.format(calendario_salida.getTime()));
        Date d_entrada = calendario_entrada.getTime();
    }

    private void SetCalendarioEntrada() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        et_entrada.setText(sdf.format(calendario_entrada.getTime()));
        Date d_salida = calendario_salida.getTime();
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

    public void ClickPaquete(View view){
        recreate();
    }

    public void ClickItem(View view){
        // Cambiamos al activity seleccionado
        MainActivity.redirectActivity(this, item.class);
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

    // Metodo registrar
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
        String paquete_codigo_ciudad_str = et_codigo_ciudad.getText().toString();
        String paquete_codigo_item_str = et_codigo_item.getText().toString();
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
}