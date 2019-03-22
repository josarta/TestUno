package testuno.clases;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NFileEntity;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.nio.protocol.BasicAsyncResponseProducer;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

public class ProcesoHttp implements Runnable {

	public static final boolean DEBUG_MODE = System.getProperty("java.vm.info", "").contains("sharing");
	private HttpRequest request;
	private HttpResponse response;
	private HttpContext context;
	private HttpAsyncExchange httpexchange;
	private File docRoot;

	private long t0;
	private long t1;

	public ProcesoHttp(final HttpRequest request, final HttpResponse response, final HttpContext context,
			final HttpAsyncExchange httpexchange, final File docRoot) {
		super();
		this.request = request;
		this.response = response;
		this.context = context;
		this.httpexchange = httpexchange;
		this.docRoot = docRoot;

	}

	@Override
	public void run() {
		 t0 = System.currentTimeMillis();
		try {
			this.handleInternal();
			httpexchange.submitResponse(new BasicAsyncResponseProducer(response));
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
		      t1 = System.currentTimeMillis();
	    }

	}

	private void handleInternal() throws HttpException, IOException {

		final String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
		if (!method.equals("GET") && !method.equals("HEAD") && !method.equals("POST")) {
			throw new MethodNotSupportedException(method + " method not supported");
		}

		String targetPlano = request.getRequestLine().getUri();
		int queryIndex = targetPlano.indexOf('?');

		final String target = queryIndex == -1 ? targetPlano : targetPlano.substring(0, queryIndex);

		switch (target) {
		case "/iniciarsesion":
			try {

				String nombreUsuario = "";
				String clave = "";
				Gson usuGson = new Gson();

				if (method.equals("POST")) {

					if (DEBUG_MODE) {
						System.out.println(""); // empty line before each request
						System.out.println(request.getRequestLine());
						System.out.println("-------- HEADERS --------");
						for (Header header : request.getAllHeaders()) {
							System.out.println(header.getName() + " : " + header.getValue());
						}
						System.out.println("--------");
					}

					HttpEntity entityT = null;
					if (request instanceof HttpEntityEnclosingRequest)
						entityT = ((HttpEntityEnclosingRequest) request).getEntity();

					// For some reason, just putting the incoming entity into
					// the response will not work. We have to buffer the message.
					byte[] data;
					if (entityT == null) {
						data = new byte[0];
					} else {
						data = EntityUtils.toByteArray(entityT);
					}
					System.out.println(new String(data));
					Gson logGson = new Gson();
					InicioSesion inSe = logGson.fromJson(new String(data), InicioSesion.class);
					nombreUsuario = inSe.getNombreUsuario();
					clave = inSe.getClave();

				} else {
					List<NameValuePair> parameters = URLEncodedUtils
							.parse(new URI(request.getRequestLine().getUri()).getQuery(), StandardCharsets.UTF_8);
					if (parameters.isEmpty() || (parameters.size() < 1 || parameters.size() > 3)) {

						Mensaje menLog = new Mensaje("401", "SC_SERVICE_UNAVAILABLE");
						HttpEntity entity = new NStringEntity(usuGson.toJson(menLog), ContentType.APPLICATION_JSON);
						response.setStatusCode(HttpStatus.SC_OK);
						response.setEntity(entity);
						System.out.println("Json " + usuGson.toJson(menLog));
						System.out.println("Enlace correcto sin parametros o parametros incompletos");
						break;
					} else {

						for (NameValuePair nameValuePair : parameters) {
							if (nameValuePair.getName().equals("nombreUsuario"))
								nombreUsuario = nameValuePair.getValue();
							if (nameValuePair.getName().equals("clave"))
								clave = nameValuePair.getValue();
						}

					}

				}

				if (Usuario.validarUsuario(nombreUsuario, clave)) {
					Usuario usuLog = new Usuario("Cedula", (long) 1234, "Jose Luis", "Sarta Alvarez",
							"josarta@misena.edu.co");
					HttpEntity entity = new NStringEntity(usuGson.toJson(usuLog), ContentType.APPLICATION_JSON);
					response.setStatusCode(HttpStatus.SC_OK);
					response.setEntity(entity);
					System.out.println("Json " + usuGson.toJson(usuLog));

				} else {
					Mensaje menLog = new Mensaje("405", "SC_METHOD_NOT_ALLOWED ");
					HttpEntity entity = new NStringEntity(usuGson.toJson(menLog), ContentType.APPLICATION_JSON);
					response.setStatusCode(HttpStatus.SC_OK);
					response.setEntity(entity);
					System.out.println("Json " + usuGson.toJson(menLog));
				}

			} catch (Exception e) {
				// TODO: handle exception
			}

			break;

		default:
			final File file = new File(this.docRoot, URLDecoder.decode(target, "UTF-8"));

			if (!file.exists()) {
				response.setStatusCode(HttpStatus.SC_NOT_FOUND);
				this.redireccion(response, "/404.html");

			} else if (!file.canRead() || file.isDirectory()) {
				response.setStatusCode(HttpStatus.SC_FORBIDDEN);
				this.redireccion(response, "/bloqueado.html");
			} else {

				response.setStatusCode(HttpStatus.SC_OK);
				final NFileEntity body = new NFileEntity(file, ContentType.create("text/html"));
				response.setEntity(body);

			}

			break;
		}

	}

	private void redireccion(final HttpResponse response, final String pagRedirecionamiento) {
		// TODO Auto-generated method stub
		try {
			final File fileBloqueo = new File(this.docRoot, URLDecoder.decode(pagRedirecionamiento, "UTF-8"));
			final NFileEntity body = new NFileEntity(fileBloqueo, ContentType.create("text/html"));
			response.setEntity(body);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Problemas con el redireccionamiento 404 " + e.getMessage());
		}
	}

	public  long getRunningTime() {
		return t1 - t0;
	}
}
