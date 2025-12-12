package es.onebox.common.config.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Clase que carga el contexto de Spring en {@link AppContext}
 * @author MMolinero
 */
public class ApplicationContextProvider implements ApplicationContextAware 
{
	public void setApplicationContext(ApplicationContext ctx) throws BeansException 
	{
		AppContext.setApplicationContext(ctx);
	}
}