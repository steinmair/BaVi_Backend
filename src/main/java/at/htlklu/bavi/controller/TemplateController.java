package at.htlklu.bavi.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value ="/mvc/templates")
public class TemplateController
{
	//region Properties
	private static Logger logger = LogManager.getLogger(TemplateController.class);
	private static final String CLASS_NAME = "TemplateController";
	public static final String FORM_NAME_SINGLE = "TemplateSingle";
	public static final String FORM_NAME_LIST = "TemplateList";
	//endregion
}
