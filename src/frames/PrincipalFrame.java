/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frames;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.StringTokenizer;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import model.Estado;
import model.Produccion;
import model.Reconocedor;
import model.Transicion;

/**
 *
 * @author alejandro.gallegoc
 */
public class PrincipalFrame extends javax.swing.JFrame {
   
    private DefaultTableModel modelTable;
    private final String[] vDefectos = {"1.", "<>", "->", ""};
    private String [] seleccion = {""};
    private int numPn = 1;
    private Stack pila;
    
    private List<Produccion> list_producciones;
    private List<String> list_simbolosEntrada;
    private List<String> list_simbolosPila;
    private List<Transicion> list_transiciones;
    
    private final JFileChooser fileChooser; // Selector de archivos
    
    /**
     * Creates new form PrincipalFrame
     */
    public PrincipalFrame() {
        initComponents();
        
        this.setResizable(false);
        this.setTitle("Ingreso de Gramáticas S Q LL1");
        
        String relleno[] ={"P/n", "Lado Izquierdo", " ", "Lado Derecho"};//definimos el encabezado de la gramática
        String valoreIniciales[][] = {{"1.", "<>", "->", ""}};// definimos lo que por defecto se colocará al agregar una producción
        
        this.modelTable = new DefaultTableModel(valoreIniciales, relleno);// creamos el modelo con el encabezado y la plantilla de la primera producción
        this.tableGramatica.setModel(this.modelTable);//actualizamos la parte gráfica de la tabla de la gramática
        this.tableGramatica.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);//definimos que el tamaño de las colunmas no cambie automaticamente
        this.tableGramatica.setCellSelectionEnabled(true);//definimos que las celdas sean seleccionables
        this.tableGramatica.getColumnModel().getColumn(0).setPreferredWidth(40);//editamos el tamaño de la columna del numero de produccion
        this.tableGramatica.getColumnModel().getColumn(1).setPreferredWidth(100);//editamos el tamaño de la columna del lado izquierdo
        this.tableGramatica.getColumnModel().getColumn(2).setPreferredWidth(30);//editamos el tamaño de la columna del simbolo "->"
        this.tableGramatica.getColumnModel().getColumn(3).setPreferredWidth(300);//editamos el tamaño de la columna del lado derecho 
        
        reiniciarValores();
        
        this.fileChooser = new JFileChooser(); //Inicializamos el selector de archivos
        this.fileChooser.setDialogTitle("Seleccione el archivo TXT de la matriz");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos de texto (*.txt)", "txt");
        this.fileChooser.setFileFilter(filter);
    }
    
    /**
     * Agregra una producción a la Tabla de la Gramática
     * @param ladoIzq
     * @param ladoDer 
     */
    private void agregarProduccion(String ladoIzq, String ladoDer){
        this.numPn = this.numPn + 1; //actualizamos la cantdad de instrucciones
        String valoresPn[] = {"","","",""}; 
        valoresPn[0] = String.valueOf(this.numPn)+".";//actualizamos la pantilla que se agrega por defecto como produccion
        valoresPn[1] = ladoIzq;
        valoresPn[2] = this.vDefectos[2];
        valoresPn[3] = ladoDer;
        this.modelTable.addRow(valoresPn);//añadimos una columna a la tabla
        this.tableGramatica.setModel(this.modelTable);//actualizamos la vista
    }
    
    /**
     * Vauelve a los valores por defecto de la Gramtatica
     */
    private void reiniciarGramatica(){
        this.modelTable.setRowCount(0); //eliminamos todas las filas de la gramatica
        this.numPn = 0;//reiniciamos el numero de producciones
        this.list_producciones = new ArrayList<>(); //Reiniciamos la lista de Producciones
        this.seleccion = new String[0];//asi mismo los conjunto de seleccion de cada produccion
        this.tableGramatica.setModel(this.modelTable);//actualizamos el modelo
    }
    
    /**
     * Agrega el símbolo de secuencia nula al lado derecho de una producción
     */
    private void agregarSecuenciaNula(){
        int fila = this.tableGramatica.getSelectedRow();
        int col = this.tableGramatica.getSelectedColumn();
        if (fila < 0 || col < 0 || col != 3){
            mostrarMensaje("Debes seleccionar el Lado Derecho de una Producción para agregar el símbolo de Secuencia Nula", "Error al agregar símbolo", JOptionPane.ERROR_MESSAGE);
            return;
        }
        this.modelTable.setValueAt(Reconocedor.SECUENCIA_NULA, fila, 3);
        this.tableGramatica.setModel(this.modelTable);//actualizamos el modelo
    }
    
    private void mostrarMensaje(String mensaje, String titulo, int icono){
        JOptionPane.showMessageDialog(this, mensaje, titulo, icono);
    }
    /**
     * Reiniciamos todos los valores que se utilizamos en el Reconocedor
     */
    private void reiniciarValores(){     
        this.list_simbolosEntrada = new ArrayList<>();
        this.list_simbolosPila = new ArrayList<>();
        this.list_transiciones = new ArrayList<>();
    }
    
    /**
     * Valida que no existan campos (ladoIzq o ladoDer) sin completar
     * @return true si se escribio algo
     */
    private boolean comprobarG(){
        list_producciones = new ArrayList<>();
        Produccion p;
        Object valor;//definimos la varible que va a contener el lado izquierdo y luego el derecho de cada produccion
        int i;//definimos una variable para recorrer las producciones
        for(i=0; i<this.numPn; i++){//recorremos las producciones
            valor = this.modelTable.getValueAt(i, 1);//obtenemos el valor del lado izquierdo de cada produccion
            p = new Produccion();
            if(valor.equals("") || valor.toString().replace(" ", "").equals("")){//controlamos de que el usuario si haya ingresado algo
                mostrarMensaje("Debe completar el Lado Izquierdo de la Producción " + String.valueOf(i+1), "Error, Gramática incompleta...", JOptionPane.ERROR_MESSAGE);
                return false;//y decimos que no se ha digitado completamente la gramatica
            }else{
                p.setLadoIzq(valor.toString().replace(" ", ""));//quitamos los espacios en blanco que se hayan digitado
                this.modelTable.setValueAt(valor.toString().replace(" ", ""), i, 1);//actualizamos el valor en la gramatica
            }
            valor = this.modelTable.getValueAt(i, 3); //obtenemos el lado derecho de cada produccion
            if(valor.equals("") || valor.toString().replace(" ", "").equals("")){//controlamos de que si se haya digitado algo
                mostrarMensaje("Debe completar el Lado Derecho de la Producción " + String.valueOf(i+1), "Error, Gramática incompleta...", JOptionPane.ERROR_MESSAGE);
                return false;//y decimos que no se ha digitado completamente la gramatica
            }else{
                p.setLadoDer(valor.toString().replace(" ", ""));//quitamos los espacios en blanco que se hayan digitado
                this.modelTable.setValueAt(valor.toString().replace(" ", ""), i, 3);//actualizamos el valor en la gramatica
            }
            p.crearSimbolos();
            this.list_producciones.add(p);
        }
        this.tableGramatica.setModel(this.modelTable);//actualizamos el modelo de la gramatica
        return true;//decimos que si se han completado los campos de la gramatica
    }
    private boolean validarIzq(String prod){
        this.pila = new Stack();
        int longitud = prod.length(), i;
        if(longitud != 3){ //controlamos de que se haya digitado algo en el lado izquierdo, es decir de que exita por lo menos "<",">"
            return false;
        }
        String ele = prod.substring(0, 1);
        if(!ele.equals("<")){ //controlamos de que el lado izquierdo empiece por "<"
            return false;
        }
        this.pila.push(ele);
        for(i=1; i<longitud; i++){//recorremos el lado izquierdo
            ele = prod.substring(i, i+1);//elemento por elemento
            if(ele.equals(">") ){//controlamos de que ">" se único
                if(this.pila.empty()){
                    return false;
                }
                this.pila.pop();
                //System.out.println("Lego al final11111");
            }else if(this.pila.empty() || ele.equals("<")){//controlamos de que "<" se único
                return false;
            }
        }
        return this.pila.empty();
    }

    /**
     * Comprueba la sintaxis de la produccion
     * @param prod representa del ladoDerecho
     * @return true si es correcto
     */
    private boolean validarDer(String prod){
        this.pila = new Stack();
        int longitud = prod.length(), i;
        boolean siguiente = false;
        String ele = prod.substring(0, 1);
        /*if(ele.equals("<") || ele.equals(">")){//controlamos de que el lado derecho no empiece por "<" o ">"
            return false;
        }*/
        for(i=0; i<longitud; i++){//recorremos el lado derecho
            ele = prod.substring(i, i+1);//elemento por elemento
            switch(ele){
                case ">"://controlamos de que ">"
                    if(this.pila.empty()){ //venga después de un "<"
                        return false;
                    }else if(siguiente){//pero no inmediatamente
                        return false;
                    }else{
                        this.pila.pop();
                    }
                break;
                case "<"://controlamos de que "<"
                    if(!this.pila.empty()){//no venga depués de un "<"
                        return false;
                    }else{
                        this.pila.push(ele);
                    }
                    siguiente = true;//controlamos de que el siguiente inmediato o sea ">"
                break;
                default:
                    siguiente = false;//como ya ha llegado un terminal desactivamos el siguiente inmediato
            }
        }
        if(!this.pila.empty()){
            return false;
        }
        
        return true;
    }
    
    /**
     * Comprueba que la Gramtaica no le falten producciones por llenar
     * y que se hayan escrito bien, a demas elimina las producciones que
     * sean exactamente iguales
     * @return 
     */
    private boolean validarGramatica(){
        if(comprobarG() && esValidaG()){//comprobamos de la gramatica tenga e todos sus campos algo digitado y que se haya digitado correctamente
            eliminarPnesIguales();//eliminamos las producciones iguales
            return true;
        }
        return false;
    }
    
    /**
     * Valida que la sintaxis con que se llenó la Gramatica sea correcta
     * @return true si es correcta
     */
    private boolean esValidaG(){
        int i; 
        for(i=0; i<this.numPn; i++){
            if(!validarIzq(this.list_producciones.get(i).getLadoIzq())){//comprobamos que el lado izquierdo
                mostrarMensaje("Error en el Lado Izquierdo de la Producción " + String.valueOf(i+1) + "\nRecuerde que cada '<' debe cerrar con su respectivo '>' y dentro de éstos sólo debe ir un caracter", "Error de sintaxis", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if(!validarDer(this.list_producciones.get(i).getLadoDer())){//y el derecho sean validos
                mostrarMensaje("Error en el Lado Derecho de la Producción " + String.valueOf(i+1) + "\nRecuerde que cada '<' debe cerrar con su respectivo '>' y dentro de éstos sólo debe ir un caracter", "Error de sintaxis", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }
    
    /**
     * Elimina las producciones que sean exactamente iguales
     */
    private void eliminarPnesIguales(){
        int i, j;
        String eleIzq1, eleDer1, eleIzq2, eleDer2;
        for(i=0; i<this.numPn; i++){//recorremos cada una de las producciones
            eleIzq1 = this.list_producciones.get(i).getLadoIzq();
            eleDer1 = this.list_producciones.get(i).getLadoIzq();
            for(j=i+1; j<this.numPn; j++){//comparandolas con todas las sisuientes
                eleIzq2 = this.list_producciones.get(j).getLadoIzq();
                eleDer2 = this.list_producciones.get(j).getLadoDer();
                if(eleIzq1.equals(eleIzq2) && eleDer1.equals(eleDer2)){//para ver si son iguales las producciones
                    this.modelTable.removeRow(j);//y eliminarlas de la gramatica
                    this.list_producciones.remove(j);//y de la lista de Producciones
                    i--;
                    j--;
                    this.numPn--;//reducimos el numero de las producciones
                }
            }  
        }
        for(i=0; i<this.numPn; i++){
            this.modelTable.setValueAt(String.valueOf(i+1)+".", i, 0);//actualizamos los numero de las producciones
        }
        this.tableGramatica.setModel(this.modelTable);
    }
    
    /**
     * Crea la lista simbolos de entrada
     */
    private void actualizarSimbolosEntrada(){
        for (Produccion produccion : list_producciones) {
            for (String terminal : produccion.getTerminales()) {
                if (!existeSimbolo(terminal, list_simbolosEntrada) && !Reconocedor.SECUENCIA_NULA.equals(terminal)){
                    list_simbolosEntrada.add(terminal);
                }
            }
        }
    }
    
    /**
     * Comprueba que el simbolo exista en la respectiva lista
     * @param simbolo
     * @param lista
     * @return true si el simbolo existe
     */
    private boolean existeSimbolo(String simbolo, List<String> lista){
        return lista.stream().anyMatch((s) -> (simbolo.equals(s)));
    }
    
    /**
     * Crea la lista de simbolos en la pila
     */
    private void actualizarSimbolosPila(){
        list_producciones.stream().filter((p) -> (!existeSimbolo(p.getLadoIzq(), list_simbolosPila))).forEachOrdered((p) -> {
            list_simbolosPila.add(p.getLadoIzq());
        });
        for (Produccion produccion : list_producciones) {
            for (String noTerminal : produccion.getNoTerminales()) {
                if (!existeSimbolo(noTerminal, list_simbolosPila)){
                    list_simbolosPila.add(noTerminal);
                }
            }
            int i = 0;
            if (produccion.isIniciaConTerminal()){
                i = 1;
            }
            List<String> terminales = produccion.getTerminales();
            for (; i<terminales.size();i++) {
                if (!existeSimbolo(terminales.get(i), list_simbolosPila)){
                    list_simbolosPila.add(terminales.get(i));
                }
            }
        }
        //Terminal que pertenezca a Alfa o Beta
    }
    
    /**
     * Muestra los datos de la Gramatica en la consola
     */
    private void mostrarGramatica(){
        for (Produccion p : this.list_producciones) {
            System.out.println("P: " + p.getLadoIzq() + " I: " + p.getLadoDer() +  " T: " + p.isIniciaConTerminal());
        }
    }
    
    /**
     * Crea la lista de símbolos de la Transiciones
     */
    private void actualizarTransiciones(){
        Transicion transicion = new Transicion();
        //Seteamos los datos a la Transición
        transicion.setPosicionTabla(""+1+2);
        transicion.setOperacionEntrada("Avance");
        transicion.setOperacionPila("Apile('s')");
        transicion.setSimboloEntrada("s");
        transicion.setSimboloPila("d");
        //this.list_transiciones.add(transicion);
    }
    
    /**
     * Bucas los nodos no terminales que sean anulables
     */
    private void encuentreNoTerminalesAnulables(){
        List<String> anulables = new ArrayList<>();
        for (Produccion p : this.list_producciones) {
            for (String noTerm : p.getNoTerminales()) {
                if (!anulables.contains(noTerm)){
                    if(esAnulable(noTerm)){
                        anulables.add(noTerm);
                    }else{
                        
                    }
                }
            }
        }
    }
    
    /**
     * Valida si un nodo no Terminal es anulable o no
     * @param noTerminal
     * @return true si es anulable
     */
    private boolean esAnulable(String noTerminal){
        for (Produccion p : this.list_producciones) {
            if(Reconocedor.SECUENCIA_NULA.equals(p.getLadoDer()) && noTerminal.equals(p.getLadoIzq())){
                return true;
            }
            if(p.getTerminales() != null && !p.getTerminales().isEmpty()){
                return false;
            }
            
            return p.getNoTerminales().stream().allMatch((s) -> (esAnulable(s)));
        }
        return false;
    }
    
    /**
     * Crea el Reconocedor y va a la tabla de Transiciones
     */
    private void crearReconocedor() {   
        actualizarSimbolosEntrada();
        actualizarSimbolosPila();
        String confInicial = tableGramatica.getValueAt(0, 1).toString().toUpperCase();// + Reconocedor.PILA_VACIA;
        actualizarTransiciones();
        encuentreNoTerminalesAnulables();
        
        Estado e = new Estado(this.list_simbolosEntrada, this.list_simbolosPila, "S0");
    
        e.setDimension(this.getSize());
        //e.setTransiciones(new ArrayList<>());
        e.setTable();
        e.setConfiguracionInical(confInicial);
        e.setInicial(true);
       
        
        Reconocedor reconocedor = Reconocedor.getInstance();
        reconocedor.setEstado(e);
        reconocedor.setProducciones(this.list_producciones);
        reconocedor.setTransiciones(this.list_transiciones);
        reconocedor.calcularTransicionesPosibles();
        TransicionesFrame tf = new TransicionesFrame();
        tf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        tf.setReconocedor(reconocedor);
        tf.setDimension(this.getSize());
        tf.initUI();
        //tf.setModalExclusionType(Dialog.ModalExclusionType.);
        tf.setVisible(true);
        //this.setEnabled(false);
        //e.imprimirTransicionesPosibles();
    }
    
    /**
     * Cuando se quiera abrir el archivo (boton Importar)
     */
    public void abrirArchivo() {
        //Invocamos el selector de archivos
        int res = this.fileChooser.showOpenDialog(this);
        File file = null;
        //Comprobamos el resultado del selector
        if (res == JFileChooser.APPROVE_OPTION) {
            file = this.fileChooser.getSelectedFile();
            reiniciarGramatica();
        } else {
            return;
        }

        //Verificamos que esté permitido el acceso al archivo seleccionado
        if (file.canRead()) {
            try {
                String linea;
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                //Leeemos el archivo línea a línea
                StringTokenizer st;
                String s = "";
                String token1 = "";
                String token2 = "";
                int numLinea = 0;
                //mostrarMensaje("Error de formato", "El archivo TXT seleccionado no cumple con el formato deseado", JOptionPane.ERROR_MESSAGE);
                while ((linea = br.readLine()) != null) {
                    s = s + linea;
                    numLinea++;
                    st = new StringTokenizer(linea, "=");
                    if (st.countTokens() != 2){
                        mostrarMensaje("El archivo TXT seleccionado no cumple con el formato deseado\n"
                                + "-->> El error puede que esté relacionado con la línea " + numLinea, "Error de formato", JOptionPane.ERROR_MESSAGE);
                        reiniciarGramatica();
                        agregarProduccion(this.vDefectos[1], this.vDefectos[3]);
                        return;
                    }else {
                        token1 = st.nextToken();
                        token2 = st.nextToken();
                        if ("|".equals(token2)){
                            token2 = Reconocedor.SECUENCIA_NULA;
                        }
                        agregarProduccion(token1, token2);
                    }
                    System.out.println(linea);
                }
                validarGramatica();
                fr.close();
                br.close();

            } catch (FileNotFoundException ex) {
                mostrarMensaje("No se ha encontrado el archivo", "Error", JOptionPane.ERROR_MESSAGE);
                reiniciarGramatica();
                agregarProduccion(this.vDefectos[1], this.vDefectos[3]);
            } catch (IOException ex) {
                mostrarMensaje("Ha ocurrido un error en la lectura del archivo", "Error", JOptionPane.ERROR_MESSAGE);
                reiniciarGramatica();
                agregarProduccion(this.vDefectos[1], this.vDefectos[3]);
            } catch (NoSuchElementException ex){
                mostrarMensaje("El archivo TXT seleccionado no cumple con el formato deseado", "Error de formato", JOptionPane.ERROR_MESSAGE);
                reiniciarGramatica();
                agregarProduccion(this.vDefectos[1], this.vDefectos[3]);
            }
        } else {
            mostrarMensaje("El archivo no pudo ser leído", "Error", JOptionPane.ERROR_MESSAGE);
            reiniciarGramatica();
            agregarProduccion(this.vDefectos[1], this.vDefectos[3]);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn_importar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableGramatica = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                if(colIndex == 0 || colIndex == 2){
                    return false;
                }else {
                    return true;
                }
            }
        };
        btn_agregarPn = new javax.swing.JButton();
        btn_reconocedor = new javax.swing.JButton();
        btn_reiniciar = new javax.swing.JButton();
        btn_ayuda = new javax.swing.JButton();
        btn_formato = new javax.swing.JButton();
        btn_nula = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btn_importar.setText("Importar TXT");
        btn_importar.setToolTipText("Importe su Gramática desde un archivo de texto");
        btn_importar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_importarActionPerformed(evt);
            }
        });
        getContentPane().add(btn_importar, new org.netbeans.lib.awtextra.AbsoluteConstraints(545, 40, 120, -1));

        jScrollPane1.setPreferredSize(new java.awt.Dimension(450, 400));

        tableGramatica.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tableGramatica.setToolTipText("Ingrese todas las Producciones de su Gramática");
        tableGramatica.setShowHorizontalLines(false);
        tableGramatica.setShowVerticalLines(false);
        jScrollPane1.setViewportView(tableGramatica);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 40, 480, 250));

        btn_agregarPn.setText("Agregar Producción");
        btn_agregarPn.setToolTipText("Agregue una Producción a su Gramática");
        btn_agregarPn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_agregarPnActionPerformed(evt);
            }
        });
        getContentPane().add(btn_agregarPn, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 310, 160, -1));

        btn_reconocedor.setText("Transiciones");
        btn_reconocedor.setToolTipText("Cree el Reconocedor de la Gramática ingresada");
        btn_reconocedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_reconocedorActionPerformed(evt);
            }
        });
        getContentPane().add(btn_reconocedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 310, 110, -1));

        btn_reiniciar.setText("Reiniciar");
        btn_reiniciar.setToolTipText("Reinicia todos los valores ica ingresadade la Gramát");
        btn_reiniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_reiniciarActionPerformed(evt);
            }
        });
        getContentPane().add(btn_reiniciar, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 310, 100, -1));

        btn_ayuda.setText("?");
        btn_ayuda.setToolTipText("Ayuda");
        btn_ayuda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ayudaActionPerformed(evt);
            }
        });
        getContentPane().add(btn_ayuda, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 40, -1));

        btn_formato.setText("Formato");
        btn_formato.setToolTipText("Vea el formato que se utiliza para importar una Gramática");
        btn_formato.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_formatoActionPerformed(evt);
            }
        });
        getContentPane().add(btn_formato, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 70, -1, -1));

        btn_nula.setText("λ");
        btn_nula.setToolTipText("Inserte el símbolo de Secuencia nula a una Producción");
        btn_nula.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_nulaActionPerformed(evt);
            }
        });
        getContentPane().add(btn_nula, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 100, -1, -1));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 0, 0));
        jLabel2.setText("Si digita A<b> el Reconocedor lo interpretará como a<B>");
        jLabel2.setToolTipText("Los Terminales y no Terminales serán convertidos a minúscula y mayúscula respectivamente");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 20, -1, -1));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fondo.jpg"))); // NOI18N
        jLabel1.setText("Recuer que si va a definir un nodo no terminal debe colocar el símbolo  '<' seguido de un único caracter y luego terminar con '>'. ");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 670, 330));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_agregarPnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_agregarPnActionPerformed
        // TODO add your handling code here:
        agregarProduccion(this.vDefectos[1], this.vDefectos[3]);//agregamos una produccion
    }//GEN-LAST:event_btn_agregarPnActionPerformed

    private void btn_reconocedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_reconocedorActionPerformed
        // TODO add your handling code here:
        if (validarGramatica()){
            //contruirSeleccion();//armamos los conjuntos de seleccion de cada produccion
            reiniciarValores();
            crearReconocedor();//se arma el reconocedor
        }
    }//GEN-LAST:event_btn_reconocedorActionPerformed

    private void btn_reiniciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_reiniciarActionPerformed
        // TODO add your handling code here:
        reiniciarGramatica();
        agregarProduccion(this.vDefectos[1], this.vDefectos[3]);
    }//GEN-LAST:event_btn_reiniciarActionPerformed

    private void btn_ayudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ayudaActionPerformed
        mostrarMensaje(""
                + "-> Cada Producción puede tantos nodos terminales como desee \n"
                + "-> Si va a añadir el símbolo de Secuencia nula debe seccionar el Lado Derecho de la respectiva Producción\n"
                + "-> Con el botón '" + Reconocedor.SECUENCIA_NULA + "' puede añadir la Secuencia nula a una Producción\n"  
                + "-> Si va a definir un nodo no terminal debe colocar el símbolo '<' "
                + "seguido de un único caracter y luego terminar con el símbolo '>'\n"
                + "-> Los Terminales y no Terminales serán convertidos a minúscula y mayúscula respectivamente\n"
                + "\tPor ejemplo,si digita A<b> el Reconocedor lo interpretará como a<B>"
                , "Tenga en cuenta", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btn_ayudaActionPerformed

    private void btn_importarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_importarActionPerformed
        abrirArchivo();
    }//GEN-LAST:event_btn_importarActionPerformed

    private void btn_formatoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_formatoActionPerformed
        mostrarMensaje("Para importar su Gramática exitosamente tenga en cuenta: \n\n" 
                + "-> Cada Producción debe tener el sigo '=' que representa al símbolo 'se define como'\n"
                + "-> Ninguna Producción debe contener espacios en blanco\n"
                + "-> Ninguna Producción debe estar numerada\n"
                + "-> Cada línea de archivo de texto represanta una Producción\n"
                + "-> Si un caracter reprsenta un nodo no Terminal, entonces debe estar rodeado de los símbolos '<' y '>'\n"
                + "\nPor ejemplo:\n"
                + "<A>=a<C>\n" 
                + "<A>=b<B>c\n" 
                + "<B>=a<A>\n" 
                + "<B>=b<B>\n" 
                + "<C>=a<C>a\n" 
                + "<C>=b<B>cc\n" 
                + "<C>=c",
                "Formato de archivo TXT", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btn_formatoActionPerformed

    private void btn_nulaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_nulaActionPerformed
        agregarSecuenciaNula();
    }//GEN-LAST:event_btn_nulaActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PrincipalFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PrincipalFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PrincipalFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PrincipalFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PrincipalFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_agregarPn;
    private javax.swing.JButton btn_ayuda;
    private javax.swing.JButton btn_formato;
    private javax.swing.JButton btn_importar;
    private javax.swing.JButton btn_nula;
    private javax.swing.JButton btn_reconocedor;
    private javax.swing.JButton btn_reiniciar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tableGramatica;
    // End of variables declaration//GEN-END:variables
}
