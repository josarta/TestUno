package testuno.principal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.BindException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import org.apache.http.ConnectionClosedException;
import org.apache.http.ExceptionLogger;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.impl.nio.bootstrap.HttpServer;
import org.apache.http.impl.nio.bootstrap.ServerBootstrap;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.protocol.BasicAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.nio.protocol.HttpAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestHandler;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.protocol.HttpContext;

import testuno.clases.ProcesoHttp;

/**
 * Elemental example for executing multiple POST requests sequentially.
 */
public class ElementalHttpGet {

	public static final boolean DEBUG_MODE = System.getProperty("java.vm.info", "").contains("sharing");

	public static void main(final String[] args) throws Exception {

		System.out.println(new File("Files").getAbsolutePath());
		// Llamamos al método que
		// devuelve la ruta absoluta

//        if (args.length < 1) {
//            System.err.println("Please specify document root directory");
//            System.exit(1);
//        }
//        Document root directory

//		Mac
		final File docRoot = new File(new File("Files").getAbsolutePath()+"/");

//		Linux		
//      docRoot archivo raiz del servido web		
//		final File docRoot = new File("/home/josarta/Escritorio/Test-Uno/");

//      puerto por defecto para el servidor 8080		
		int port = 8080;

//      valido si cuando se inicio el servidor se envio un argumento en este caso el numero del puerto.		
		if (args.length < 1) {
			System.err.println("Ingrese un directorio raiz - ");
			System.exit(1);
		} else {
			port = Integer.parseInt(args[0]);
		}

		SSLContext sslContext = null;
		final IOReactorConfig config = IOReactorConfig.custom().setSoTimeout(15000).setTcpNoDelay(true).build();

		// se crea el servidor
		final HttpServer server = ServerBootstrap.bootstrap().setListenerPort(port).setServerInfo("Test/1.1")
				.setIOReactorConfig(config).setSslContext(sslContext).setExceptionLogger(new StdErrorExceptionLogger())
				.registerHandler("*", new HttpFileHandler(docRoot)).create();

		server.start();

		System.out.println("Serving " + docRoot + " on " + server.getEndpoint().getAddress()
				+ (sslContext == null ? "" : " with " + sslContext.getProvider() + " " + sslContext.getProtocol()));

		server.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				server.shutdown(5, TimeUnit.SECONDS);
			}
		});

	}

	static class StdErrorExceptionLogger implements ExceptionLogger {

		@Override
		public void log(final Exception ex) {
			if (ex instanceof SocketTimeoutException) {
				System.err.println("Connection timed out");
			} else if (ex instanceof ConnectionClosedException) {
				System.err.println(ex.getMessage());
			} else if (ex instanceof BindException) {
				System.err.println(ex.getMessage());
			} else if (ex instanceof IOReactorException) {
				System.err.println(ex.getMessage());
			} else if (ex instanceof FileNotFoundException) {
				System.err.println(ex.getMessage());
			} else {
				ex.printStackTrace();

			}
		}

	}

	static class HttpFileHandler implements HttpAsyncRequestHandler<HttpRequest> {

		private final File docRoot;

		public HttpFileHandler(final File docRoot) {
			super();
			this.docRoot = docRoot;
		}

		public HttpAsyncRequestConsumer<HttpRequest> processRequest(final HttpRequest request,
				final HttpContext context) {
			// Buffer request content in memory for simplicity
			return new BasicAsyncRequestConsumer();
		}

		public void handle(final HttpRequest request, final HttpAsyncExchange httpexchange, final HttpContext context)
				throws HttpException, IOException {
			final HttpResponse response = httpexchange.getResponse();

			Runnable serverInstance = new ProcesoHttp(request, response, context, httpexchange, docRoot);

			Thread serverThread = new Thread(serverInstance);
			serverThread.start();

			System.out.println("Duracion = " + ((ProcesoHttp) serverInstance).getRunningTime());
			// handleInternal(request, response, context);

		}

	}

}