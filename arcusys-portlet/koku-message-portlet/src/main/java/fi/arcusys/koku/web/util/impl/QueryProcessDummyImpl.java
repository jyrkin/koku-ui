package fi.arcusys.koku.web.util.impl;

import net.sf.json.JSONObject;

import org.springframework.context.MessageSource;

import fi.arcusys.koku.common.exceptions.KokuCommonException;
import fi.arcusys.koku.common.services.facades.Page;

public class QueryProcessDummyImpl extends AbstractQueryProcess {
	
	public QueryProcessDummyImpl(MessageSource messages) {
		super(messages);
	}

	@Override
	protected JSONObject getJsonTasks(String taskType, Page page,
			String keyword, String field, String userUid)
			throws KokuCommonException {
		return createFailedResult("No implementation", null);
	}
}
