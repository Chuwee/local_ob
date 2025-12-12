package es.onebox.common.config.context;

import org.springframework.context.ApplicationContext;

/**
 * Contiene el contexto de Spring para que sea accesible desde toda la aplicacion
 * @author MMolinero
 */
public class AppContext {

	private static ApplicationContext ctx;

	private AppContext() {}

	public static void setApplicationContext(ApplicationContext applicationContext)
	{
		ctx = applicationContext;
	}

	public static ApplicationContext getApplicationContext() 
	{
		return ctx;    
	}

}
