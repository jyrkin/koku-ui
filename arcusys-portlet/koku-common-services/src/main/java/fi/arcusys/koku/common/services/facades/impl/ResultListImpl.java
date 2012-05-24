package fi.arcusys.koku.common.services.facades.impl;

import static fi.arcusys.koku.common.util.Constants.PAGE_NUMBER;

import java.util.ArrayList;
import java.util.List;

import fi.arcusys.koku.common.services.facades.Page;
import fi.arcusys.koku.common.services.facades.ResultList;

public class ResultListImpl<T> implements ResultList<T> {
	
	private final List<T> tasks;
	private final int tasksTotal;
	private final Page page;
	
	public ResultListImpl(List<T> tasks, int tasksTotal, Page page) {
		if (tasks == null) {
			this.tasks = new ArrayList<T>();
		} else {
			this.tasks = tasks;			
		}
		this.tasksTotal = tasksTotal;
		this.page = page;
	}

	@Override
	public List<T> getResults() {
		return tasks;
	}

	@Override
	public Integer getTotalTasks() {
		return tasksTotal;
	}

	@Override
	public Integer getTotalPages() {
		return (tasksTotal == 0) ? 1 : (int) Math.ceil((double)tasksTotal/PAGE_NUMBER);
	}
	
	@Override
	public Integer getPageLength() {
		return page.getLast();
	}

	@Override
	public Integer getCurrentPage() {
		return (int) Math.ceil(((1+page.getFirst()) / PAGE_NUMBER));
	}
}
 