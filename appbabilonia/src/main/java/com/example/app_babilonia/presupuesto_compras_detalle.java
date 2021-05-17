package com.example.app_babilonia;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_babilonia.database.SQLControl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class presupuesto_compras_detalle extends AppCompatActivity {

    private static final String TAG = "paquete_detalle";

    private EditText et_nombre, et_salida, et_entrada, et_cant_persona, et_ciudad_precio, et_codigo_ciudad, et_codigo_item;
    private Spinner sp_ciudad, sp_item, sp_ciuad_cant_dia, sp_financiacion;
    private ImageButton btn_calendario_entrada,btn_calendario_salida, btn_ciudad_registrar;
    private ListView lv_paquete_ciudad, lv_paquete_item;
    private TextView tv_item_total, tv_ciudad_total;
    private String paquete_codigo;

    // calendarios
    final Calendar calendario_salida = Calendar.getInstance();
    final Calendar calendario_entrada = Calendar.getInstance();
    
    ArrayList<HashMap<String, String>> items_ciudad; // Diccionario Array para valores ciudad
    ArrayList<HashMap<String, String>> items_item; // Diccionario Array para valores item

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presupuesto_compras_detalle);

        // Integracion de componentes
        et_nombre = (EditText) findViewById(R.id.paquete_txt_nombre);
        et_salida = (EditText) findViewById(R.id.paquete_txt_salida);
        et_entrada = (EditText) findViewById(R.id.paquete_txt_entrada);
        et_cant_persona = (EditText) findViewById(R.id.paquete_txt_cantidad_persona);
        et_ciudad_precio = (EditText) findViewById(R.id.paquete_txt_ciudad_precio);
        et_codigo_ciudad = (EditText) findViewById(R.id.paquete_txt_codigo_ciudad);
        et_codigo_item = (EditText) findViewById(R.id.paquete_txt_codigo_item);
        sp_ciudad = (Spinner)findViewById(R.id.paquete_sp_ciudad);
        sp_ciuad_cant_dia = (Spinner)findViewById(R.id.paquete_sp_cant_dias);
        sp_financiacion = (Spinner)findViewById(R.id.paquete_sp_financiacion);
        sp_item = (Spinner)findViewById(R.id.paquete_sp_item);
        btn_calendario_entrada = (ImageButton)findViewById(R.id.paquete_btn_calendario_entrada);
        btn_calendario_salida = (ImageButton)findViewById(R.id.paquete_btn_calendario_salida);
        btn_ciudad_registrar = (ImageButton)findViewById(R.id.paquete_btn_ciudad_registrar);
        lv_paquete_ciudad = (ListView)findViewById(R.id.paquete_ciudad_lista);
        lv_paquete_item = (ListView)findViewById(R.id.paquete_item_lista);
        tv_ciudad_total = (TextView)findViewById(R.id.txt_paquete_ciudad_total);
        tv_item_total = (TextView)findViewById(R.id.txt_paquete_item_total);

        // Obtenemos el codigo del activity anterior
        paquete_codigo = getIntent().getStringExtra("codigo");

        // Consulta SQL para completar campos
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname,null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        // Consultamos el paquete mediante el codigo
        Cursor row = db.rawQuery("select paq_cod, paq_descr, paq_fecha_salida, paq_fecha_llegada, paq_cant_persona, paq_fin_cuota, paq_precio_ciudad, paq_precio_item from paquete where paq_cod =" + paquete_codigo, null);
        if(row.moveToFirst()){
            // Completamos los campos tipo texto
            et_nombre.setText(row.getString(1));
            et_salida.setText(row.getString(2));
            et_entrada.setText(row.getString(3));
            et_cant_persona.setText(row.getString(4));
            tv_ciudad_total.setText(row.getString(6));
            tv_item_total.setText(row.getString(7));

            // Integracion de objetos / spinner
            List<Integer> financiacion = new ArrayList<>();
            financiacion.add(0, Integer.valueOf(row.getString(5))); // completamos el spinner
            financiacion.add(1);
            financiacion.add(2);
            financiacion.add(3);
            financiacion.add(4);
            financiacion.add(6);
            financiacion.add(12);
            financiacion.add(15);
            financiacion.add(24);

            ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, financiacion);
            sp_financiacion.setAdapter(adapter);

        }else {
            Toast.makeText(this, "Ha ocurrido un error. Contactar con el administrador", Toast.LENGTH_SHORT).show();
        }

        // Integracion de objetos / spinner
        Integer [] cantidad_dias = {1,3,7,15,30};
        ArrayAdapter <Integer> adapter_dias = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, cantidad_dias);
        sp_ciuad_cant_dia.setAdapter(adapter_dias);

        // Integracion de objetos / spinner ciudad
        items_ciudad = new ArrayList<HashMap<String, String>>();
        // Consultamos los datos de la ciudad
        Cursor ciudad = db.rawQuery
                ("select ciu_cod, ciu_descr from ciudad order by ciu_cod desc", null);
        // ciclo while por cada fila
        while (ciudad.moveToNext()){
            // Creamos un diccionario con los valores
            HashMap<String, String> dict = new HashMap<String, String>();
            dict.put("Codigo", ciudad.getString(0));
            dict.put("Nombre", ciudad.getString(1));
            items_ciudad.add(dict); // Agregamos a la matrix

            // Agregamos los datos en la vista
            SimpleAdapter adapter_ciudad = new SimpleAdapter(this, items_ciudad, android.R.layout.simple_spinner_item, new String[] {"Nombre"}, new int[] {android.R.id.text1});
            sp_ciudad.setAdapter(adapter_ciudad);
        }

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
        SQLControl admin_item = new SQLControl(this, dbname_item,null, 1);
        SQLiteDatabase db_item = admin_item.getReadableDatabase();

        // Consultamos los datos de los items
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
                DatePickerDialog date = new DatePickerDialog(presupuesto_compras_detalle.this, date_entrada,
                        calendario_entrada.get(Calendar.YEAR),
                        calendario_entrada.get(Calendar.MONTH),
                        calendario_entrada.get(Calendar.DAY_OF_MONTH));
                date.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); //
                date.show();
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
                DatePickerDialog date = new DatePickerDialog(presupuesto_compras_detalle.this, date_salida,
                        calendario_salida.get(Calendar.YEAR),
                        calendario_salida.get(Calendar.MONTH),
                        calendario_salida.get(Calendar.DAY_OF_MONTH));
                date.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); //
                date.show();
            }
        });

        // Listamos las ciudades e items registrados
        CiudadListar();
        ItemListar();

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

    public void PaqueteCancelar(View view){
        Intent intent = new Intent(this, pedido_compras.class);
        startActivity(intent);
    }

    public void PaqueteEliminar(View view){
        // declaramos los valores
        String paq_cod_str = paquete_codigo;

        // Iniciamos la db
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname,null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        // Creamos una alerta de dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar"); // Titulo
        builder.setMessage("¿Eliminar el siguiente registro?"); // Mensaje de cierre
        // En caso de que seleccione 'Si'
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!paq_cod_str.isEmpty()){
                    int eliminar = db.delete("paquete", "paq_cod=" + paq_cod_str, null);
                    int eliminar_detalle_1 = db.delete("paquete_item", "paq_cod=" + paq_cod_str, null);
                    int eliminar_detalle_2 = db.delete("paquete_ciudad", "paq_cod=" + paq_cod_str, null);
                    db.close();

                    if(eliminar >= 1){
                        Toast.makeText(presupuesto_compras_detalle.this, "Registro Eliminado", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(presupuesto_compras_detalle.this, pedido_compras.class);
                        startActivity(intent);

                    }else{ // En caso de que el id no exista
                        Toast.makeText(presupuesto_compras_detalle.this, "¡El registro indicado no existe!", Toast.LENGTH_SHORT).show();
                    }
                }else{ // en caso de que no complete el campo indicado
                    Toast.makeText(presupuesto_compras_detalle.this, "¡Debe indicar un codigo!", Toast.LENGTH_SHORT).show();
                }
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

    public void PaqueteEditar(View view){
        // Abrimos la db
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname,null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        // Obtenemos los valores
        String paq_descr_str = et_nombre.getText().toString();
        String paq_cant_persona = et_cant_persona.getText().toString();
        String paq_fin_cuota_str = sp_financiacion.getSelectedItem().toString();
        String paq_fecha_salida = et_salida.getText().toString();
        String paq_fecha_entrada = et_entrada.getText().toString();

        // obtenemos el valor de la cantidad de días segun las ciudades
        String  paquete_paqciu_cant_dia_str = "";
        Cursor ciudad_dias = db.rawQuery("select coalesce(sum(paqciu_cant_dia),0) from paquete_ciudad where paq_cod =" + paquete_codigo, null);
        if(ciudad_dias.moveToFirst()){
            paquete_paqciu_cant_dia_str = ciudad_dias.getString(0);
        }

        // Validamos que se completen los datos
        if(!paq_descr_str.isEmpty() && !paq_cant_persona.isEmpty()){

            // Obtenemos los datos tipo text y lo pasamos a Date para hacer el calculo de fecha
            DateFormat format = new SimpleDateFormat("dd/MM/yy", Locale.US);
            Date fecha_entrada  = null;
            try {
                fecha_entrada = format.parse(paq_fecha_entrada);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date fecha_salida = null;
            try {
                fecha_salida = format.parse(paq_fecha_salida);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Si la fecha de salida es menor a la fecha de entrada
            if (fecha_salida.compareTo(fecha_entrada) < 0){
                // Verificamos si la suma de los días en la ciudad se encuentra en el rango de llegada
                // Primero sumamos los dias seleccionados a la fecha de salida
                Calendar salida_dias = Calendar.getInstance();
                salida_dias.setTime(fecha_salida);
                salida_dias.add(Calendar.DATE, Integer.valueOf(paquete_paqciu_cant_dia_str));

                // Si la suma de dias es menor a la fecha de entrada
                if (salida_dias.getTime().compareTo(fecha_entrada) < 0){

                    // Validamos si la descripcion pertenece a id correspondiente
                    Cursor descripcion = db.rawQuery("select paq_cod from paquete where paq_cod = " + paquete_codigo + " and paq_descr = '" + paq_descr_str + "'", null);
                    if(descripcion.moveToFirst()){
                        ContentValues datos = new ContentValues();
                        datos.put("paq_descr", paq_descr_str);
                        datos.put("paq_fecha_salida", paq_fecha_salida);
                        datos.put("paq_fecha_llegada", paq_fecha_entrada);
                        datos.put("paq_cant_persona", paq_cant_persona);
                        datos.put("paq_fin_cuota", paq_fin_cuota_str);

                        // Realizamos el update en la base de datos
                        int editar = db.update("paquete", datos, "paq_cod=" + paquete_codigo, null);
                        db.close();

                        if(editar >= 1){
                            Toast.makeText(this, "Registro Actualizado", Toast.LENGTH_SHORT).show();

                        }else{ // En caso de que el id no exista
                            Toast.makeText(this, "¡El registro indicado no existe!", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        // Validamos que la descripcion no se encuentre registrada
                        Cursor registrado = db.rawQuery("select paq_cod from paquete where paq_descr = '" + paq_descr_str + "'", null);
                        if(!registrado.moveToFirst()){
                            ContentValues datos = new ContentValues();
                            datos.put("paq_descr", paq_descr_str);
                            datos.put("paq_fecha_salida", paq_fecha_salida);
                            datos.put("paq_fecha_llegada", paq_fecha_entrada);
                            datos.put("paq_cant_persona", paq_cant_persona);
                            datos.put("paq_fin_cuota", paq_fin_cuota_str);

                            // Realizamos el update en la base de datos
                            int editar = db.update("paquete", datos, "paq_cod=" + paquete_codigo, null);
                            db.close();

                            if(editar >= 1){
                                Toast.makeText(this, "Registro Actualizado", Toast.LENGTH_SHORT).show();

                            }else{ // En caso de que el id no exista
                                Toast.makeText(this, "¡El registro indicado no existe!", Toast.LENGTH_SHORT).show();
                            }

                        }else {
                            Toast.makeText(this, "Paquete existente en el sistema", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {
                    Toast.makeText(this, "La cantidad de días en la ciudad supera la fecha de entrada", Toast.LENGTH_SHORT).show();
                }

            }else {
                Toast.makeText(this, "La Fecha de Entrada es menor a la Fecha de Salida", Toast.LENGTH_LONG).show();
            }
        }else{ // caso contrario, lanza el siguiente mensaje
            Toast.makeText(this, "Dede completar los campos.", Toast.LENGTH_SHORT).show();
        }
    }

    public void CiudadRegistrar(View view){
        String ciu_cod_str = et_codigo_ciudad.getText().toString();
        String paq_cod_str = paquete_codigo;
        String paqciu_cant_dia_str = sp_ciuad_cant_dia.getSelectedItem().toString();
        String paqciu_precio_str = et_ciudad_precio.getText().toString();

        if (!paqciu_precio_str.isEmpty()){
            // Abromos la db
            String dbname = getResources().getString(R.string.dbname).toString();
            SQLControl admin = new SQLControl(this, dbname,null, 1);
            SQLiteDatabase db = admin.getReadableDatabase();

            // verificamos si la ciudad aun no se encuentra registrada
            Cursor ciudad_registrado = db.rawQuery("select ciu_cod as cod from paquete_ciudad where ciu_cod =" + ciu_cod_str + " and paq_cod = " + paquete_codigo, null);
            if(!ciudad_registrado.moveToFirst()){

                // Verificamos si los días seleccionados se encuentran en el rango de fecha establecido entre la salida y entrada
                Cursor ciudad_dia = db.rawQuery("select coalesce(sum(paqciu_cant_dia), 0) from paquete_ciudad where paq_cod=" + paquete_codigo, null);
                if(ciudad_dia.moveToFirst()){
                    // Sumamos la cantidad de dias seleccionado
                    Integer ciudad_dia_total = Integer.valueOf(ciudad_dia.getString(0)) + Integer.valueOf(paqciu_cant_dia_str);
                    // agregamos el total de días a la fecha de salida
                    // obtenemos la fecha de salida
                    Cursor salida = db.rawQuery("select paq_fecha_salida from paquete where paq_cod=" + paquete_codigo, null);
                    if(salida.moveToFirst()){
                        DateFormat format = new SimpleDateFormat("dd/MM/yy", Locale.US); // establecemos el formato
                        Date fecha_salida = null;
                        try {
                            fecha_salida = format.parse(salida.getString(0)); // guardamos el valor de la fecha
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        // Sumamos los días a la fecha de salida para obtener el total en fecha
                        Calendar fecha_final = Calendar.getInstance();
                        fecha_final.setTime(fecha_salida);
                        fecha_final.add(Calendar.DATE, ciudad_dia_total);
                        //Log.i(TAG, "fecha_final = " + fecha_final.getTime().toString());

                        // obtenemos la fecha de entrada actual
                        Cursor entrada = db.rawQuery("select paq_fecha_llegada from paquete where paq_cod=" + paquete_codigo, null);
                        if(entrada.moveToFirst()){
                            Date fecha_entrada = null;
                            try {
                                fecha_entrada = format.parse(entrada.getString(0)); // guardamos el valor de la fecha
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            // Comparamos la fecha de entrada actual y la fecha final
                            // Si la fecha de final es menor a la fecha de entrada
                            if (fecha_final.getTime().compareTo(fecha_entrada) < 0){
                                // Insertamos el paquete ciudad
                                ContentValues paquete_ciudad = new ContentValues();
                                paquete_ciudad.put("paq_cod", paq_cod_str);
                                paquete_ciudad.put("ciu_cod", ciu_cod_str);
                                paquete_ciudad.put("paqciu_cant_dia", paqciu_cant_dia_str);
                                paquete_ciudad.put("paqciu_precio", paqciu_precio_str);

                                db.insert("paquete_ciudad", null, paquete_ciudad);

                                // Procedemos a actualizar la tabla
                                // Consultamos la suma del precio de las ciudades
                                Cursor nuevo_precio = db.rawQuery("select coalesce(sum(paqciu_precio),0) as ciudad_total_precio from paquete_ciudad where paq_cod=" + paquete_codigo, null);
                                if (nuevo_precio.moveToFirst()) {
                                    ContentValues datos = new ContentValues();
                                    datos.put("paq_precio_ciudad", nuevo_precio.getString(0));
                                    int editar = db.update("paquete", datos, "paq_cod=" + paquete_codigo, null);
                                    if (editar >= 1) {
                                        Toast.makeText(presupuesto_compras_detalle.this, "Registro Actualizado", Toast.LENGTH_SHORT).show();
                                    } else { // En caso de que el id no exista
                                        Toast.makeText(presupuesto_compras_detalle.this, "¡El registro indicado no existe!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                db.close();

                                et_ciudad_precio.setText("");
                                // Listamos las ciudades
                                CiudadListar();

                                Toast.makeText(this, "Registro Completado", Toast.LENGTH_SHORT).show();
                            }else{ // caso contrario, retornamos
                                Toast.makeText(this, "La cantidad de días en la ciudad supera la fecha de entrada", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                /**

                 **/
            }else {
                Toast.makeText(this, "La ciudad seleccionada ya se encuentra registrada", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "¡Debe completar los campos!", Toast.LENGTH_SHORT).show();
        }
    }
    public void CiudadListar(){
        // Consulta SQL para usuario y contraseña
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname,null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        // lista de ciudades registradas
        items_ciudad = new ArrayList<HashMap<String, String>>();
        Cursor ciudad_registrada = db.rawQuery("select a.paq_cod, a.ciu_cod, b.ciu_descr,  a.paqciu_cant_dia, a.paqciu_precio from paquete_ciudad  a, ciudad b where a.ciu_cod = b.ciu_cod and a.paq_cod =" + paquete_codigo, null);
        while (ciudad_registrada.moveToNext()){
            // Creamos un diccionario con los valores
            HashMap<String, String> dict = new HashMap<String, String> ();
            dict.put("Codigo_Paquete", ciudad_registrada.getString(0));
            dict.put("Codigo_Ciudad", ciudad_registrada.getString(1));
            dict.put("Ciudad_Nombre", ciudad_registrada.getString(2));
            dict.put("Ciudad_dias", ciudad_registrada.getString(3));
            dict.put("Ciudad_Precio", ciudad_registrada.getString(4));
            items_ciudad.add(dict); // agregamos a la matrix

            // Agregamos los datos a la vista
            SimpleAdapter adapter = new SimpleAdapter(this, items_ciudad, R.layout.list_paquete_ciudad, new String[] {"Codigo_Ciudad", "Ciudad_Nombre", "Ciudad_dias", "Ciudad_Precio"}, new int[] {R.id.tv_id, R.id.tv_ciudad_nombre, R.id.tv_ciudad_dias, R.id.tv_ciudad_precio});
            lv_paquete_ciudad.setAdapter(adapter);
        }

        // Actualizar el total de ciudad
        Cursor ciudad_total = db.rawQuery("select paq_precio_ciudad from paquete where paq_cod =" + paquete_codigo, null);
        if(ciudad_total.moveToFirst()){
            tv_ciudad_total.setText(ciudad_total.getString(0));
        }

        // Al momento de dar click sobre uno de los valores, se eliminara
        lv_paquete_ciudad.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> mymap = (HashMap<String, String>) parent.getItemAtPosition(position);
                String ciudad_codigo = mymap.get("Codigo_Ciudad");
                // Procedemos a eliminar
                int eliminar = db.delete("paquete_ciudad", "ciu_cod=" + ciudad_codigo + " and paq_cod=" + paquete_codigo, null);

                if(eliminar >= 1){
                    // Procedemos a actualizar la tabla
                    // Consultamos la suma del precio de las ciudades
                    Cursor nuevo_precio = db.rawQuery("select coalesce(sum(paqciu_precio),0) as ciudad_total_precio from paquete_ciudad where paq_cod=" + paquete_codigo, null);
                    if (nuevo_precio.moveToFirst()){
                        ContentValues datos = new ContentValues();
                        datos.put("paq_precio_ciudad", nuevo_precio.getString(0));
                        int editar = db.update("paquete", datos, "paq_cod=" + paquete_codigo, null);
                        if(editar >= 1){
                            Toast.makeText(presupuesto_compras_detalle.this, "Registro Actualizado", Toast.LENGTH_SHORT).show();
                        }else{ // En caso de que el id no exista
                            Toast.makeText(presupuesto_compras_detalle.this, "¡El registro indicado no existe!", Toast.LENGTH_SHORT).show();
                        }

                        Toast.makeText(presupuesto_compras_detalle.this, "Registro Eliminado", Toast.LENGTH_SHORT).show();
                        CiudadListar();

                    }else{
                        Toast.makeText(presupuesto_compras_detalle.this, "Ha ocurrido un error. Contante con el administrador", Toast.LENGTH_SHORT).show();
                    }
                }else{ // En caso de que el id no exista
                    Toast.makeText(presupuesto_compras_detalle.this, "¡El registro indicado no existe!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void ItemRegistrar(View view){
        // Consulta SQL para usuario y contraseña
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname,null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        String item_cod_str = et_codigo_item.getText().toString();
        String item_precio_str = "";
        // Consultamos el precio del Item para agregar a la columna paqitem_total
        Cursor item_precio = db.rawQuery("select item_precio from item where item_cod = " + item_cod_str, null);
        if(item_precio.moveToFirst()){
           item_precio_str = item_precio.getString(0);
        }else {
            Toast.makeText(this, "Ha ocurrido un error. Contactar con el administrador", Toast.LENGTH_SHORT).show();
        }

        // validamos que el item no se encuentre registrado
        Cursor item_registrado = db.rawQuery("select item_cod from paquete_item where item_cod =" + item_cod_str + " and paq_cod=" + paquete_codigo, null);
        if(!item_registrado.moveToFirst()){
            // Insertamos los valores
            ContentValues paquete_item = new ContentValues();
            paquete_item.put("paq_cod", paquete_codigo);
            paquete_item.put("item_cod", item_cod_str);
            paquete_item.put("paqitem_total", item_precio_str);

            db.insert("paquete_item", null, paquete_item);

            // Procedemos a actualizar la tabla
            // Consultamos la suma del precio de los items
            Cursor nuevo_precio = db.rawQuery("select coalesce(sum(paqitem_total),0) as item_total_precio from paquete_item where paq_cod=" + paquete_codigo, null);
            if (nuevo_precio.moveToFirst()) {
                ContentValues datos = new ContentValues();
                datos.put("paq_precio_item", nuevo_precio.getString(0));
                int editar = db.update("paquete", datos, "paq_cod=" + paquete_codigo, null);
                if (editar >= 1) {
                    Toast.makeText(presupuesto_compras_detalle.this, "Registro Actualizado", Toast.LENGTH_SHORT).show();
                } else { // En caso de que el id no exista
                    Toast.makeText(presupuesto_compras_detalle.this, "¡El registro indicado no existe!", Toast.LENGTH_SHORT).show();
                }
            }

            // Listamos los items registrados
            ItemListar();

            Toast.makeText(this, "Registro Completado", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "El item seleccionado ya se encuentra registrado", Toast.LENGTH_SHORT).show();
        }

    }
    public void ItemListar(){
        // Consulta SQL para usuario y contraseña
        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname,null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        // lista de item registrados
        items_item = new ArrayList<HashMap<String, String>>();
        Cursor item_registrado = db.rawQuery("select a.paq_cod, a.item_cod, b.item_descr, a.paqitem_total from paquete_item  a, item b where a.item_cod = b.item_cod and a.paq_cod =" + paquete_codigo, null);
        while (item_registrado.moveToNext()){
            // Creamos un diccionario con los valores
            HashMap<String, String> dict = new HashMap<String, String> ();
            dict.put("Codigo_Paquete", item_registrado.getString(0));
            dict.put("Codigo_Item", item_registrado.getString(1));
            dict.put("Item_Nombre", item_registrado.getString(2));
            dict.put("Item_Precio", item_registrado.getString(3));
            items_item.add(dict); // agregamos a la matrix

            // Agregamos los datos a la vista
            SimpleAdapter adapter = new SimpleAdapter(this, items_item, R.layout.list_paquete_item, new String[] {"Codigo_Item", "Item_Nombre", "Item_Precio"}, new int[] {R.id.tv_id, R.id.tv_item_nombre, R.id.tv_item_precio});
            lv_paquete_item.setAdapter(adapter);
        }

        // Actualizar el total de item
        Cursor item_total = db.rawQuery("select paq_precio_item from paquete where paq_cod =" + paquete_codigo, null);
        if(item_total.moveToFirst()){
            tv_item_total.setText(item_total.getString(0));
        }

        // Al momento de dar click sobre uno de los valores, se eliminara
        lv_paquete_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> mymap = (HashMap<String, String>) parent.getItemAtPosition(position);
                String item_codigo = mymap.get("Codigo_Item");
                int eliminar = db.delete("paquete_item", "item_cod=" + item_codigo + " and paq_cod=" + paquete_codigo, null);

                if(eliminar >= 1){
                    // Procedemos a actualizar la tabla
                    // Consultamos la suma del precio de los items
                    Cursor nuevo_precio = db.rawQuery("select coalesce(sum(paqitem_total),0) as item_total_precio from paquete_item where paq_cod=" + paquete_codigo, null);
                    if (nuevo_precio.moveToFirst()) {
                        ContentValues datos = new ContentValues();
                        datos.put("paq_precio_item", nuevo_precio.getString(0));
                        int editar = db.update("paquete", datos, "paq_cod=" + paquete_codigo, null);
                        if (editar >= 1) {
                            Toast.makeText(presupuesto_compras_detalle.this, "Registro Actualizado", Toast.LENGTH_SHORT).show();
                        } else { // En caso de que el id no exista
                            Toast.makeText(presupuesto_compras_detalle.this, "¡El registro indicado no existe!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    Toast.makeText(presupuesto_compras_detalle.this, "Registro Eliminado", Toast.LENGTH_SHORT).show();
                    ItemListar();
                }else{ // En caso de que el id no exista
                    Toast.makeText(presupuesto_compras_detalle.this, "¡El registro indicado no existe!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}