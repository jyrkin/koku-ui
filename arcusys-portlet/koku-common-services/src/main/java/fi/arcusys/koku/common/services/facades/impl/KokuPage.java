package fi.arcusys.koku.common.services.facades.impl;

import fi.arcusys.koku.common.services.facades.Page;
import static fi.arcusys.koku.common.util.Constants.PAGE_NUMBER;

public class KokuPage implements Page {

	private final Integer first;
	private final Integer last;
	
	public KokuPage(final int page) {
		if (page > 0) {
			this.first = (page-1) * PAGE_NUMBER + 1;
		} else {
			this.first = 1;
		}
		this.last = first + PAGE_NUMBER;
	}
	
	@Override
	public Integer getFirst() {
		return first;
	}

	@Override
	public Integer getLast() {
		return last;
	}
}
