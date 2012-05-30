package fi.arcusys.koku.common.util;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

/**
 * Dummy messageSource
 * 
 * 
 * @author Toni Turunen
 *
 */
public class DummyMessageSource implements MessageSource {
	
	private static final Logger LOG = Logger.getLogger(DummyMessageSource.class);
	private static final String NAG = "Bug in code. You shouldn't see me! Use proper messageSource object!";
	
	@Override
	public String getMessage(String code, Object[] args, String defaultMessage,
			Locale locale) {
		LOG.warn(NAG);
		return code;
	}
	
	@Override
	public String getMessage(String code, Object[] args, Locale locale)
			throws NoSuchMessageException {
		LOG.warn(NAG);
		return code;
	}
	
	@Override
	public String getMessage(MessageSourceResolvable resolvable, Locale locale)
			throws NoSuchMessageException {
		LOG.warn(NAG);
		throw new NoSuchMessageException("Not supported");
	}
}
