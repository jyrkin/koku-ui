package fi.arcusys.koku.web;


import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import fi.arcusys.koku.common.proxy.AttachmentDownload;


@Controller("attachmentFileController")
@RequestMapping(value = "VIEW")
public class AttachmentController {

	@ResourceMapping(value = "attachment")
	public void actionPageView(
			@RequestParam(value = "path") String path,
			ResourceRequest request,
			ResourceResponse response) {
		new AttachmentDownload(request, response, path);
	}
}
