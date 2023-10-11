package at.htlklu.spring.controller;

import at.htlklu.spring.model.*;
import at.htlklu.spring.repository.*;

import at.htlklu.spring.api.LogUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

//localhost:8082/mvc/teachers
//localhost:8082/mvc/teachers/1/departments
//localhost:8082/mvc/teachers/1/schoolClasses


@Controller
@RequestMapping(value ="mvc/departments")
public class DepartmentController
{
	//region Properties
	private static Logger logger = LogManager.getLogger(DepartmentController.class);
	private static final String CLASS_NAME = "DepartmentController";
	public static final String FORM_NAME_SINGLE = "TeacherSingle";
	public static final String FORM_NAME_LIST = "DepartmentList";

	@Autowired
	DepartmentRepository departmentRepository;
	//endregion


	// localhost:8082/mvc/teachers
	@GetMapping("")
	public ModelAndView show()
	{
		logger.info(LogUtils.info(CLASS_NAME, "show"));

		ModelAndView mv = new ModelAndView();

		mv.setViewName(DepartmentController.FORM_NAME_LIST);

		List<Department> departments = departmentRepository.findAll();
		mv.addObject("departments", departments); //link zur HTML

	    return mv;
	}

}
