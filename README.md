# TestUno

Test 1 [Micro Server]
Usando el lenguaje Java implemente un micro servidor de CGI que permita
recibir peticiones WEB usando como cliente un navegador común basado en protocolo
TCP/IP.
La prueba consta de dos componentes esenciales, el microserver y una clase que haga
las veces de CGI, el cual debe recibir y retornar información a su cliente, que para el
caso será un navegador. Piense que no tiene a la mano, ni un tomcat, ni un apache
con php ni nada de eso.
Las peticiones desde el navegador deben ser peticiones Ajax get y post.
Tenga en cuenta los múltiples hilos que debe soportar, ya que puede tener
concurrencia.
Para java puede usar la librería httpcore-4.1.jar de apache que le apoya en el manejo
del protocolo http para no hacerlo desde cero, complementado con el manejo de
ServerSocket.
En la sustentación debe demostrar cómo se hizo la prueba para soportar múltiples
peticiones concurrentes.
