$(document).ready(function () {

    var t = $('#mytable').DataTable(
        {
            "language":{
                "sProcessing":     "Procesando...",
                "sLengthMenu":     "Mostrar _MENU_ registros",
                "sZeroRecords":    "No se encontraron resultados",
                "sEmptyTable":     "Ningún dato disponible en esta tabla",
                "sInfo":           "Mostrando registros del _START_ al _END_ de un total de _TOTAL_ registros",
                "sInfoEmpty":      "Mostrando registros del 0 al 0 de un total de 0 registros",
                "sInfoFiltered":   "(filtrado de un total de _MAX_ registros)",
                "sInfoPostFix":    "",
                "sSearch":         "Buscar:",
                "sUrl":            "",
                "sInfoThousands":  ",",
                "sLoadingRecords": "Cargando...",
                "oPaginate": {
                    "sFirst":    "Primero",
                    "sLast":     "Último",
                    "sNext":     "Siguiente",
                    "sPrevious": "Anterior"
                },
                "oAria": {
                    "sSortAscending":  ": Activar para ordenar la columna de manera ascendente",
                    "sSortDescending": ": Activar para ordenar la columna de manera descendente"
                }
            }

        });

    $("#formularioRepetir").bind("submit", function () {
        var btnEnviarRp = $("#btnEnviarRp");
        btnEnviarRp.attr("disabled", "disabled");
        t.clear().draw();
        $("#rowIniciarSesion").hide();
        $("#rowTabla").show("");
        // Capturamnos el boton de envío
        for (var i = 1; i <= 200; ++i)
            doSetTimeout(i);
        // Nos permite cancelar el envio del formulario
        btnEnviarRp.removeAttr("disabled");
        // Nos permite cancelar el envio del formulario
        return false;
    });


    function doSetTimeout(i) {
        // Funcion a repetir
        setTimeout(function () { serviceInicioSession(i); }, 1000);
    }

    function serviceInicioSession(i) {
        //  console.log(this);
        var varNombres = new Array("josarta", "josarta-1", "josarta-2", "josarta-3", "josarta-4", "josarta-5"); //Defines los nombres
        var varClaves = new Array("1234", "11234", "21234", "31234", "41234", "51234"); //Defines unas claves


        var search = {}
        search["nombreUsuario"] = varNombres[Math.floor((Math.random()) * 6)];
        search["clave"] = varClaves[Math.floor((Math.random()) * 6)];;

        $.ajax({
            type: "POST",
            url: "iniciarsesion",
            dataType: 'text',
            contentType: 'application/json',
            cache: false,
            timeout: 100000,
            data: JSON.stringify(search),
            success: function (data) {
                /*
                * Se ejecuta cuando termina la petición y esta ha sido
                * correcta
                * */
               
                llenar(data);
            },
            error: function (e) {
                /*
                * Se ejecuta si la peticón ha sido erronea
                * */

            }
        });

        function llenar(data) {

            t.row.add( [
                i,
                search["nombreUsuario"] +" - " + search["clave"] ,
                data
            ] ).draw( false );
        }

    }

    $("#formulario").bind("submit", function () {
        // Capturamnos el boton de envío
        var btnEnviar = $("#btnEnviar");
        var btnEtiqueta = $("#etiquetaBoton")
        // array
        var search = {}
        search["nombreUsuario"] = $("#nombreUsuario").val();
        search["clave"] = $("#clave").val();
        $("#nombreUsuario").val("");
        $("#clave").val("");

        // oculta el div de datos usuario

        $("#cardUsuario").hide("");

        //  console.log(this);
        $.ajax({
            type: $(this).attr("method"),
            url: $(this).attr("action"),
            dataType: 'text',
            contentType: 'application/json',
            cache: false,
            timeout: 100000,
            data: JSON.stringify(search),
            beforeSend: function () {
                /*
                * Esta función se ejecuta durante el envió de la petición al
                * servidor.
                * */
                // btnEnviar.text("Enviando"); Para button 

                btnEtiqueta.text("Enviando"); // Para input de tipo button
                btnEnviar.attr("disabled", "disabled");
            },
            complete: function (data) {
                /*
                * Se ejecuta al termino de la petición
                * */

                btnEtiqueta.text("Enviar");
                btnEnviar.removeAttr("disabled");
            },
            success: function (data) {
                /*
                * Se ejecuta cuando termina la petición y esta ha sido
                * correcta
                * */
                var j = '[' + data + ']';
                var tipoMensaje = false;

                var obj = JSON.parse(j, function (key, value) {
                    if (key == "tipoDocumento")
                        tipoMensaje = true;
                    return value;

                });

                if (tipoMensaje) {

                    $("#tipoDocumentoMs").val(obj[0].tipoDocumento);
                    $("#numeroDocumentoMs").val(obj[0].numeroDocumento);
                    $("#nombreMs").val(obj[0].nombres);
                    $("#apellidosMs").val(obj[0].apellidos);
                    $("#emailMs").val(obj[0].correo);
                    $("#cardUsuario").show("");

                } else {
                    Materialize.toast('¡ Ups Aviso: No se encontro ningun usuario', 4000, 'red');
                }


            },
            error: function (e) {
                /*
                * Se ejecuta si la peticón ha sido erronea
                * */
                alert("Problemas al tratar de enviar el formulario");
                console.log("ERROR : ", e);

            }
        });
        // Nos permite cancelar el envio del formulario
        return false;
    });
});