package fi.arcusys.koku.common.services;

import org.springframework.context.MessageSource;

public abstract class AbstractHandle {
	
	private final MessageSource messageSource;
	
	protected static final String MESSAGE_SOURCE_MISSING 	= "Coulnd't find localized message MessageSource missing. Localization doesn't work properly";
	protected static final String SPLIT_REGEX 				= "\\|";

	
	public AbstractHandle(MessageSource messageSource) {
		if (messageSource == null) {
			throw new IllegalArgumentException("MessageSource can't be null!");
		}
		this.messageSource = messageSource;
	}
	
	protected final MessageSource getMessageSource() {
		return messageSource;
	}

}
