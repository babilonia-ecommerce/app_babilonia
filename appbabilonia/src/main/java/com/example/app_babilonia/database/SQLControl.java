package com.example.app_babilonia.database;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLControl extends SQLiteOpenHelper{

    public SQLControl(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creamos las tablas utilizadas para el proyecto

            // Referenciales
        db.execSQL("create table usuarios (id_usuario int primary key, descripcion text, contrasena text, id_rol text, estado text)");
        db.execSQL("create table roles (id_rol int primary key, descripcion text)");
        db.execSQL("create table ciudades (id_ciudad int primary key, descripcion text)");
        db.execSQL("create table marcas (id_marca int primary key, descripcion text)");
        db.execSQL("create table mercaderias (id_mercaderia int primary key, id_marca int, descripcion text, stock text, precio_compra text, precio_venta text)");
        db.execSQL("create table empleados(id_empleado int primary key, id_ciudad int, id_usuario int, nombres text, apellidos text, cedula_identidad int, fecha_nac text, sexo text, estado_civil text, nacionalidad text, telefono int, direccion text, email text, legajo text, estado text, fecha_incorporacion text)");
        db.execSQL("create table proveedores (id_proveedor int primary key, id_ciudad int, razon_social text, ruc text, direccion text, telefono text, email text, tipo_persona text)");

            //Movimientos
        db.execSQL("create table pedido_compras (id_pedido_compra int primary key, id_proveedor int, id_empleado int, fecha text, estado text, observacion text)");
        db.execSQL("create table pedido_compras_detalle (id_pedido_compra int primary key, id_mercaderia int, cantidad int, precio_compra int)");

//_________________________________________________________________CARGA DE DATOS POR DEFECTO_________________________________________________________________
        // Usuarios por defecto
        db.execSQL("insert into usuarios values (1,'admin','admin','1','activo')"); // usuario administrador principal
        db.execSQL("insert into usuarios values (2,'damian','123','2','activo')"); // usuario no administrador
        db.execSQL("insert into usuarios values (3,'lou','lou','2','activo')"); // usuario no administrador

        //Empleados por defecto
        db.execSQL("insert into empleados values (1, 1, 1, 'George', 'Century', 1234567, '01/01/1990', 'Masculino', 'Soltero', 'Paraguaya', '0981234567', 'Avda. Eusebio Ayala 123', 'admin@hotmail.com', '14', 'activo', '01/01/2010' )");
        db.execSQL("insert into empleados values (2, 2, 2, 'Damian', 'Centurion', 6658802, '07/02/1996', 'Masculino', 'Soltero', 'Paraguaya', '0971111350', 'Chamacoco 9999 c/Policarpo', 'damian-centurion@hotmail.com', '154', 'activo', '03/07/2015' )");

        //Proveedores por defecto
        db.execSQL("insert into proveedores values (1, 1, 'Luisito' ,'80005485-4','Avda. Eusebio Ayala 1540','021548584', 'info@luisito.com.py', 'Juridica')");
        db.execSQL("insert into proveedores values (2, 2, 'Copipunto' ,'80004587-4','Avda. Eusebio Ayala 1540','021548584', 'info@copi.com.py', 'Fisica')");
        db.execSQL("insert into proveedores values (3, 3, 'Punto Exacto' ,'80001250-4','Avda. Eusebio Ayala 1540','021548584', 'info@punto.com.py', 'Juridica')");
        db.execSQL("insert into proveedores values (4, 1, 'Stock' ,'800004543-4','Avda. Eusebio Ayala 1540','021548584', 'info@stock.com.py', 'Fisica')");
        db.execSQL("insert into proveedores values (5, 2, 'Fastrax' ,'80001124-4','Avda. Eusebio Ayala 1540','021548584', 'info@fastrax.com.py', 'Juridica')");
        db.execSQL("insert into proveedores values (6, 3, 'Tigo' ,'80007796-4','Avda. Eusebio Ayala 1540','021548584', 'info@tigo.com.py', 'Fisica')");
        db.execSQL("insert into proveedores values (7, 1, 'Lomilitos' ,'80003967-4','Avda. Eusebio Ayala 1540','021548584', 'info@lomi.com.py', 'Juridica')");

        // Roles por defecto
        db.execSQL("insert into roles values (1,'Administrador')");
        db.execSQL("insert into roles values (2,'Encargado de Compras')");
        db.execSQL("insert into roles values (3,'Jefe de Compras')");

        // Ciudades por defecto
        db.execSQL("insert into ciudades values (1,'Asuncion')");
        db.execSQL("insert into ciudades values (2,'Coronel Oviedo')");
        db.execSQL("insert into ciudades values (3,'Encarnacion')");

        // Marcas por defecto
        db.execSQL("insert into marcas values (1,'Samsung')");
        db.execSQL("insert into marcas values (2,'Xiaomi')");
        db.execSQL("insert into marcas values (3,'Huawei')");

        // Mercaderias por defecto
        db.execSQL("insert into mercaderias values (1, 1, 'Galaxy Note 10', 15, 150000, 250000)");
        db.execSQL("insert into mercaderias values (2, 2, 'Mi 9T PRO Full HD', 200, 1250000, 1450000)");
        db.execSQL("insert into mercaderias values (3, 3, 'P40 Ultra Mega Intra PRO', 15, 4900000, 5400000)");

        // Pedido de Compras / Detalle por defecto
        db.execSQL("insert into pedido_compras values (1, 1, 1, '01/01/2020', 'Pendiente', 'ninguna')");
        db.execSQL("insert into pedido_compras_detalle values (1, 1, 5, 50000)");
//        db.execSQL("insert into pedido_compras_detalle values (1, 2, 10, 100000)");
        db.execSQL("insert into pedido_compras values (2, 2, 2, '02/03/2020', 'Cancelado', 'ninguna')");
        db.execSQL("insert into pedido_compras_detalle values (2, 2, 5, 50000)");


        //__________________________________________________TABLAS DE DB REEMPLAZADA__________________________________________________
        db.execSQL("create table item (item_cod int primary key, item_descr text, item_precio real)");
        db.execSQL("create table ciudad (ciu_cod int primary key, ciu_descr text)");
        db.execSQL("create table paquete (paq_cod int primary key, paq_descr text, paq_precio_ciudad real, paq_precio_item real, paq_fecha_salida text, paq_fecha_llegada text, paq_cant_persona int, paq_fin_cuota int)");
        db.execSQL("create table paquete_ciudad (paq_cod int, ciu_cod int, paqciu_cant_dia int, paqciu_precio real, foreign key (paq_cod) references paquete(paq_cod), foreign key (ciu_cod) references ciudad(ciu_cod), primary key (paq_cod, ciu_cod))");
        db.execSQL("create table paquete_item (paq_cod int, item_cod int, paqitem_total real, foreign key (paq_cod) references paquete(paq_cod), foreign key (item_cod) references item(item_cod), primary key (paq_cod, item_cod))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
