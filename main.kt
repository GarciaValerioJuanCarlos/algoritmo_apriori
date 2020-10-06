//Librerías
import java.nio.charset.Charset          //Para manejo de caracteres
import java.util.*                       //Para manejo de arreglos


////Para almacenar los datos
var datos = arrayListOf<String>()	//Aquí, guardaremos cada fila de datos
var totalDeTransacciones = 0	//Saber la cantidad de transacciones
var letras = Arrays.asList("a","b","c","d","e","f","g")	//Arreglo para almacenar las cadenas que pertenecen a las transacciones
var L = ArrayList<String>()		//Para el evento A
var L2 = ArrayList<String>()	//Para el evento B
var C = ArrayList<Double>()		//Para la cobertura
var P = ArrayList<Double>()		//Para la cobertura
var umbral = 0.2	//Para almacenar la cobertura
//Variable de tipo string para almacenar cada resultado de forma entendible
var reporte ="|----------------------------------------------------|\n"+
        "|                ------Reporte------                 |\n"+
        "|----------------------------------------------------|\n"

fun main(args : Array<String>){
    datos.add("a,d,f,g")	//Agregamos un elemento a nuestro arreglo
    datos.add("b,e")
    datos.add("c,e")
    datos.add("b,c,e,f")
    datos.add("a,b,g")
    datos.add("c,e,f,g")
    datos.add("a,b")
    totalDeTransacciones=datos.count()	//Conseguimos el total de transacciones
    algoritmoPriori()	//Llamamos a nuestro algoritmo apriori
}
//Funcion para sacar L
fun algoritmoPriori(){
    //Con el umbral, vamos a pasar a crear L[n] y C[n]
    //Para hacerlo, vamos a tener que hacer L[1] de forma manual, al igual que C[1]
    letras.sort()	//Ordenamos el arreglo de los elementos
    var letrasA = letras	//Usado para recorrer cada elemento y formar L
    
    //numerador de presicion
    var numPre =0.0
    //denominador de presicion
    var denPre = 0.0
    //numerador de cobertura
    var numCob=0.0
    //denominador de cobertura
    var denCob =totalDeTransacciones
    reporte=reporte+"\t----Iteracion 1\n"
    
    //For usado para crear las reglas de asociacion
    for(i in 0..letrasA.count()-1){
        //For usado para crear las reglas de asociacion
        for(j in 0..letrasA.count()-1){
            //Evitamos que haya repeticiones en la regla
            if (i!=j){
                //Buscamos las reglas 1 a 1
                for (k in 0..totalDeTransacciones-1){
                    //Conseguimos el numerador para Cobertura y precision
                    if (datos.get(k).contains(letrasA.get(i)) && datos.get(k).contains(letrasA.get(j))){
                        numCob+=1
                        numPre+=1
                    }
                    //Conseguimos el denominador para Precision
                    if(datos.get(k).contains(letrasA.get(i))){
                        denPre+=1
                    }
                }
                //Aca vemos si entra dentro de nuestro umbral
                //Cobertura
                if(numCob/denCob >= umbral){
                    //Si su cobertura es mayor o igual al umbral
                    //Procedemos a agregalo a L, L2, C y P
                    //Y agregamos al reporte
                    L.add(letrasA.get(i))
                    L2.add(letrasA.get(j))
                    C.add(numCob/denCob)
                    P.add(numPre/denPre)
                    reporte=reporte+"\t"+letrasA.get(i)+" -> "+letrasA.get(j)+" Cobertura: "+numCob/denCob+" Precision: "+numPre/denPre+"\n"
                }
                //Reestablecemos los numeradores y denominadores
                numCob=0.0
                numPre=0.0
                denPre=0.0
                //--------------------------------------------------------
            }
        }
    }
    //--------------------------------------------------------
    //     Creacion de las posibles reglas de asociacion
    //--------------------------------------------------------
    //Ya que creamos nuestro primer L y L2, vamos a comenzar a crear nuestras combinaciones aumentadas
    var iteracion = 0
    var listafinal = L.count()
    var inicio=0
    var anterior=""
    var elementosActuales = L.count()-1
    do {
        reporte=reporte+"\t----Iteracion "+(iteracion+2)+"\n\t"
        //Vamos a hacer las combinaciones
        //Vamos a ir recorriendo L, uno por uno para hacer las combinaciones donde tengamos A-> B,C Ó A,B->C
        //Esto es después de crear las L sucesivas
        //Recorremos la lista de generada por L()
        for(i in inicio..elementosActuales){
            //Ahora, vamos a hacer la regla de correspondencia
            //Este if sirve para que no repitamos las letras
            if(anterior!=L.get(i)){
                anterior = L.get(i)
                var vectA = L.get(i).split(",")
                //-----------------------------------------------------
                // Primero, vamos a buscar los elementos repetidos en L
                //-----------------------------------------------------
                for (j in inicio..elementosActuales) {
                    //Condicional para no repetir la misma comparación
                    if (i != j) {
                        //-----------------------------------------------------
                        // Si hay alguna secuencia repetida, vamos a realizar lo siguiente
                        // Vamos a concatenar L y L2, vamos a separarlos en un vector
                        // a ese vector lo ordenaremos de forma albatetica
                        // despues, vamos a crear una nueva cadena, desechando elementos repetidos
                        // para eso, usaremos una variable para almacenar la letra que ha sido agregada
                        //-----------------------------------------------------
                        if (vectA.sorted() == L.get(j).split(",").sorted()) {
                            //vamos a combinar su L2
                            var combinar = L2.get(i)+","+L2.get(j)
                            var separar = combinar.split(",").sorted()
                            var letra=""
                            var nuevaCadena=""
                            var ulti=separar.count()-1
                            for(k in 0..ulti){
                                if (letra!=separar.get(k)){
                                    letra=separar.get(k)
                                    if(k==separar.count() - 1){
                                        nuevaCadena+=separar.get(k)
                                    }else {
                                        nuevaCadena=nuevaCadena+separar.get(k)+","
                                    }
                                }else {
                                    if (k == ulti) {
                                        nuevaCadena = nuevaCadena.substring(0, nuevaCadena.count() - 1)
                                    }
                                }
                            }
                            //Recorremos L para buscar que la regla de asociacion no se repita
                            //para eso, tendremos dos banderas, una para verificar que la regla de asociacion
                            //a - b no se repita y otra para verificar que la regla b - a no se repite
                            var ban1=false
                            var ban2=false
                            for (k in inicio..L.count()-1){
                                if(L.get(k) == nuevaCadena && L2.get(k) == L.get(j) ){
                                    ban1=true
                                }
                                if(L.get(k) == L.get(j) && L2.get(k) == nuevaCadena ){
                                    ban2=true
                                }

                            }
                            // Creamos variable para la cobertura y precision
                            // y procedemos a verificar si las reglas creadas no se repiten
                            // si no se repiten,vamos a verificar que esten dentro del umbral establecido
                            // si estan dentro del umbral, calculamos su precicion y las agregamos a L,L2,C,P y al reporte
                            var cobertura= 0.0
                            var precision= 0.0
                            if(!ban1) {
                                cobertura=getCobertura(nuevaCadena,L.get(j))
                                if(cobertura>=umbral) {
                                    precision=getPrecision(nuevaCadena,L.get(j))
                                    L.add(nuevaCadena)
                                    L2.add(L.get(j))
                                    C.add(cobertura)
                                    P.add(precision)
                                    reporte=reporte+nuevaCadena+" -> "+L.get(j)+" Cobertura: "+cobertura+" Precision: "+precision+"\n\t"
                                }
                            }
                            if(!ban2) {
                                cobertura=getCobertura(L.get(j),nuevaCadena)
                                if(cobertura>=umbral) {
                                    precision=getPrecision(L.get(j),nuevaCadena)
                                    L2.add(nuevaCadena)
                                    L.add(L.get(j))
                                    C.add(cobertura)
                                    P.add(precision)
                                    reporte=reporte+L.get(j)+" -> "+nuevaCadena+" Cobertura: "+cobertura+" Precision: "+precision+"\n\t"
                                }
                            }
                        }else {
                            //
                            //  Ahora, vamos por los elementos que no se repiten
                            //  Para esto, vamos a tener que revisar si comparten alguna letra
                            // Si el L2[x] del L[x], es igual al L[z] y el L2[z] es diferente a L[x]
                            // Si el L2[x] y L[z] son iguales, ver si existe las reglas
                            //  L[x] -> L2[z]
                            //  L2[z] -> L[x]
                            //  Para eso, buscamos que L[x] != L2[z] y que L2[x] == L[z]
                            if (L2.get(i) == L.get(j) && L2.get(j) != L.get(i)) {
                                //Buscamos que letra no son iguales
                                var nuevoCadena=L.get(i)+","+L2.get(j)
                                var nuevaCadena2=L.get(j)+","+L2.get(j)
                                var separar = nuevoCadena.split(",").sorted()
                                var separar2 = nuevaCadena2.split(",").sorted()
                                var letra=""
                                nuevoCadena=""
                                var ulti=separar.count() - 1
                                //Creación de una cadena para L2
                                for (k in 0..ulti) {
                                    if (letra!=separar.get(k)){
                                        letra=separar.get(k)
                                        if(k==ulti){
                                            nuevoCadena += separar.get(k)
                                        }else {
                                            nuevoCadena = nuevoCadena + separar.get(k) + ","
                                        }
                                    }else {
                                        if (k == ulti) {
                                            nuevoCadena = nuevoCadena.substring(0, nuevoCadena.count() - 1)
                                        }
                                    }
                                }
                                //Creación de una cadena para L2
                                ulti=separar2.count()-1
                                nuevaCadena2=""
                                for (k in 0..ulti){
                                    if(!L.get(i).contains(separar2.get(k))){
                                        if (nuevaCadena2==""){
                                            nuevaCadena2+=separar2.get(k)
                                        }else{
                                            nuevaCadena2=nuevaCadena2+","+separar2.get(k)
                                        }
                                    }
                                }
                                //Recorremos L para buscar reglas de asociacion iguales
                                var ban1=false
                                var ban2=false
                                var ban3=false
                                for (k in inicio..L.count()-1){
                                    if(L.get(k) == nuevoCadena && L2.get(k) == L.get(j) ){
                                        ban1=true
                                    }
                                    if(L.get(k) == L.get(i) && L2.get(k) == nuevaCadena2 ){
                                        ban2=true
                                    }
                                    if(L.get(k) == nuevaCadena2 && L2.get(k) == L.get(i) ){
                                        ban3=true
                                    }
                                }
                                //Variable para guardar la cobertura
                                var cobertura= 0.0
                                var precision= 0.0
                                //Si la regla de asociacion no son iguales a las existentes en L-L2
                                //procedemos a ver si su cobertura está dentro del umbral, si es
                                //así, calculamos su precision y agregamos a L,L2,C,P y reporte
                                if(!ban1) {
                                    cobertura=getCobertura(nuevoCadena,L.get(j))
                                    if(cobertura>=umbral) {
                                        precision=getPrecision(nuevoCadena,L.get(j))
                                        L.add(nuevoCadena)
                                        L2.add(L.get(j))
                                        C.add(cobertura)
                                        P.add(precision)
                                        reporte=reporte+nuevoCadena+" -> "+L.get(j)+" Cobertura: "+cobertura+" Precision: "+precision+"\n\t"
                                    }
                                }
                                if(!ban2){
                                    cobertura=getCobertura(L.get(i),nuevaCadena2)
                                    if(cobertura>=umbral) {
                                        precision=getPrecision(L.get(i),nuevaCadena2)
                                        L.add(L.get(i))
                                        L2.add(nuevaCadena2)
                                        C.add(cobertura)
                                        P.add(precision)
                                        reporte=reporte+L.get(i)+" -> "+nuevaCadena2+" Cobertura: "+cobertura+" Precision: "+precision+"\n\t"
                                    }
                                }
                                if(!ban3){
                                    cobertura=getCobertura(nuevaCadena2,L.get(i))
                                    if(cobertura>=umbral) {
                                        precision=getPrecision(nuevaCadena2,L.get(i))
                                        L.add(nuevaCadena2)
                                        L2.add(L.get(i))
                                        C.add(cobertura)
                                        P.add(precision)
                                        reporte=reporte+nuevaCadena2+" -> "+L.get(i)+" Cobertura: "+cobertura+" Precision: "+precision+"\n\t"
                                    }
                                }
                                //
                            }
                            //
                        }//Fin del else
                    }//Fin de la comparación para que no se repita
                }//Fin del for que sirve para buscar en los elementos actuales
            }//Para no pasar por las mismas letras
        }//Fin del for principal para recorrer la lista
        //Aca aumentamos el numero de iteración y comprobamos
        //si ya podemos salir, debido a que no tenemos más elementos nuevos
        iteracion+=1
        inicio = listafinal
        listafinal= L.count()
        elementosActuales=L.count()-1
        //println("---Inicio: "+inicio+" final: "+listafinal)
        if(listafinal == L.count()){
            break
        }

    }while (iteracion < 15)
    //Imprimimos el reporte
    println(reporte)
}
//Funcion para analizar la cobertura
fun getCobertura(cadenaL : String, cadenaL2:String) : Double{
    //Revisamos cada elemento de L y L2 uno por uno, para ver si está en
    //en la transaccion
    var numeradorC = 0.0    //Numerador de la cobertura
    //Creamos banderas para comprobar que L y L2 están completas
    //en la transaccion
    var estaCompletoL = true
    var estaCompletoL2 = true
    //primer for sirve para analizar toda la lista de transacciones
    var total=datos.count()-1
    for(i in 0..total){
        //------------------------------------------------------------|
        //Obtenemos el numerador de la cobertura
        //para ello, vamos a tener que revisar que cada elemento de L y L2,
        //está en la transaccion
        //------------------------------------------------------------|
        cadenaL.split(",").forEach {
            if(!datos.get(i).contains(it)){
                estaCompletoL=false
            }
        }
        cadenaL2.split(",").forEach {
            if(!datos.get(i).contains(it)){
                estaCompletoL2=false
            }
        }
        //Si L y L2 están completos en la transaccion, aumentamos el numerador
        if(estaCompletoL && estaCompletoL2)
            numeradorC++
        //Reestablecemos nuestras banderas
        estaCompletoL=true
        estaCompletoL2=true
    }
    //regresamos la cobertura
    return (numeradorC/(total+1))
}
//Funcion para analizar la precision
fun getPrecision(cadenaL : String, cadenaL2:String) : Double{
    //Revisamos elemento por elemento en cada transaccion
    var numeradorP  = 0.0    //Numerador de la precision
    var denominadorP= 0.0    //Denominador de la precision
    //Creamos banderas para comprobar que la regla de asociacion este completa
    // y asi obtener el numerador y denominador, para calcular la precision
    var estaCompletoL = true
    var estaCompletoL2 = true
    //primer for sirve para analizar toda la lista de registro
    var total=datos.count()-1
    for(i in 0..total){
        //------------------------------------------------------------|
        //Obtenemos el numerador y denominador de la precisión |
        //Vamos a verificar que cada elemento en L y L2 esté en la transaccion
        //si no está toda, se cambia la bandera indicando que no
        //------------------------------------------------------------|
        cadenaL.split(",").forEach {
            if(!datos.get(i).contains(it)){
                estaCompletoL=false
            }
        }
        cadenaL2.split(",").forEach {
            if(!datos.get(i).contains(it)){
                estaCompletoL2=false
            }
        }
        //Si ambas están en la transaccion, aumentamos el numerador
        if(estaCompletoL && estaCompletoL2)
            numeradorP++
        //si solo está L, aumentamos su denominador
        if(estaCompletoL)
            denominadorP++

        estaCompletoL=true
        estaCompletoL2=true
    }
    //Devolvemos la precision
    return (numeradorP/denominadorP).toDouble()
}
