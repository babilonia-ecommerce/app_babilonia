package com.example.app_babilonia;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_babilonia.database.SQLControl;

import java.util.ArrayList;
import java.util.HashMap;

public class list_presupuesto_compras extends AppCompatActivity {

    // declaramos los componentes a utilizar
    private ListView lv_pedido_compras;

    ArrayList<HashMap<String, String>> items; // Diccionario Array para valores de usuarios


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_pedido_compras);
        // Integracion de objetos / lista

        lv_pedido_compras = (ListView)findViewById(R.id.pedido_compras_lista);


        // Mostrar Lista
        PedidoComprasListar();
    }

    // Metodo para listar ciudades
    public void PedidoComprasListar() {

        items = new ArrayList<HashMap<String, String>>();

        String dbname = getResources().getString(R.string.dbname).toString();
        SQLControl admin = new SQLControl(this, dbname, null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        // Consultamos los datos
        Cursor rows = db.rawQuery("select pc.id_pedido_compra, e.nombres | ', ' | e.apellidos as empleado, p.razon_social, pc.fecha, pc.estado, (pcd.precio_compra * pcd.cantidad) as total " +
                " from pedido_compras pc " +
                " left join pedido_compras_detalle pcd on pcd.id_pedido_compra = pc.id_pedido_compra " +
                " left join proveedores p on p.id_proveedor = pc.id_proveedor " +
                " left join empleados e on e.id_empleado = pc.id_empleado ", null);

        if (rows.getCount() > 0) {

            while (rows.moveToNext()) {

                // Creamos un diccionario con los valores
                HashMap<String, String> dict = new HashMap<String, String>();
                dict.put("id", rows.getString(0));
                dict.put("empleado", rows.getString(1));
                dict.put("proveedor", rows.getString(2));
                dict.put("fecha", rows.getString(3));
                dict.put("estado", rows.getString(4));
                dict.put("total", rows.getString(5));

                items.add(dict); // Agregamos a la matrix

                // Agregamos los datos en la vista
                SimpleAdapter adapter = new SimpleAdapter(this, items, R.layout.list_pedido_compras, new String[] {"id","empleado","proveedor","estado","total"}, new int[]{R.id.tv_id_pedido_pc, R.id.tv_empleado_nombre, R.id.tv_proveedor_razon_social, R.id.tv_estado, R.id.tv_total});

                lv_pedido_compras.setAdapter(adapter);
            }
            db.close();
        } else {
            Toast.makeText(this, "No se encuentran resultados", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, pedido_compras.class);
            startActivity(intent);
        }

        /* Al momento de dar click sobre uno de los valores, enviaremos el codigo al activity detalle
        lv_items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> mymap = (HashMap<String, String>) parent.getItemAtPosition(position);
                String coidgo_paquete = mymap.get("Codigo_Paquete");
                Intent intent = new Intent(list_pedido_compras.this, pedido_compras_detalle.class);
                intent.putExtra("codigo", coidgo_paquete);
                startActivity(intent);
            }
        });*/
    }
}