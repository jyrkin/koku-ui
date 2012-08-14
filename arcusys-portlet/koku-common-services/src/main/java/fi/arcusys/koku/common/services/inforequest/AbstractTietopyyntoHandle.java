package fi.arcusys.koku.common.services.inforequest;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import fi.arcusys.koku.common.services.AbstractHandle;
import fi.arcusys.koku.common.services.inforequest.model.KokuInformationAccessType;
import fi.arcusys.koku.common.services.inforequest.model.KokuInformationRequestStatus;
import fi.arcusys.koku.common.util.MessageUtil;

public class AbstractTietopyyntoHandle extends AbstractHandle {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractTietopyyntoHandle.class);

	public AbstractTietopyyntoHandle(MessageSource messageSource) {
		super(messageSource);
	}

	protected String getLocalizedInformationRequestSummary(KokuInformationRequestStatus type) {
		if (getMessageSource() == null) {
			LOG.warn(MESSAGE_SOURCE_MISSING);
			return type.toString().toLowerCase();
		}
		Locale locale = MessageUtil.getLocale();

		try {
			switch(type) {
			case VALID:
				return getMessageSource().getMessage("InformationRequestStatus.VALID", null, locale);
			case DECLINED:
				return getMessageSource().getMessage("InformationRequestStatus.DECLINED", null, locale);
			case EXPIRED:
				return getMessageSource().getMessage("InformationRequestStatus.EXPIRED", null, locale);
			case OPEN:
				return getMessageSource().getMessage("InformationRequestStatus.OPEN", null, locale);
			default:
				return getMessageSource().getMessage("unknown", null, locale);
			}
		} catch (NoSuchMessageException nsme) {
			LOG.warn(MESSAGE_SOURCE_MISSING);
			return type.toString().toLowerCase();
		}
	}


	protected String getLocalizedInfoAccessType(KokuInformationAccessType type) {
		if (getMessageSource() == null) {
			LOG.warn(MESSAGE_SOURCE_MISSING);
			return type.toString().toLowerCase();
		}
		Locale locale = MessageUtil.getLocale();

		try {
			switch(type) {
			case MANUAL:
				return getMessageSource().getMessage("InformationAccessType.PORTAL", null, locale);
			case PORTAL:
				return getMessageSource().getMessage("InformationAccessType.MANUAL", null, locale);
			default:
				return getMessageSource().getMessage("unknown", null, locale);
			}
		} catch (NoSuchMessageException nsme) {
			LOG.warn(MESSAGE_SOURCE_MISSING);
			return type.toString().toLowerCase();
		}
	}

}
