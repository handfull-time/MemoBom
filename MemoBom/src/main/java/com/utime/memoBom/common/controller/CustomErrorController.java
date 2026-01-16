package com.utime.memoBom.common.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.utime.memoBom.common.util.AppUtils;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
class CustomErrorController implements ErrorController {
	
	private class _ErrorInfo{
		String exception;
		String exceptionType;
        String message;
        String requestUri;
		Integer status;
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("_ErrorInfo [\n");
			if (exception != null)
				builder.append("exception=").append(exception).append(",\n");
			if (exceptionType != null)
				builder.append("exceptionType=").append(exceptionType).append(",\n");
			if (message != null)
				builder.append("message=").append(message).append(",\n");
			if (requestUri != null)
				builder.append("requestUri=").append(requestUri).append(",\n");
			if (status != null)
				builder.append("status=").append(status);
			builder.append("\n]");
			return builder.toString();
		}
	}
	
	@RequestMapping("error")
    public ModelAndView handleError(HttpServletRequest request, HttpServletResponse response, Exception e ){
		
		final Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
		final Object exceptionType = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION_TYPE);
        final Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        final Object requestUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
		final Integer status = (Integer)request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
           
		final _ErrorInfo info = new _ErrorInfo();
		info.exception = (exception == null)? null:exception.toString();
		info.exceptionType = (exceptionType == null)? null:exceptionType.toString();
		info.message = AppUtils.isEmpty( message )? exception.getClass().getName():message.toString();
		info.requestUri = (requestUri == null)? null:requestUri.toString();
		info.status = (status == null)? HttpStatus.INTERNAL_SERVER_ERROR.value():status;

        // JSON 응답을 할지 여부 확인
        boolean jsonResponse = false;
       	if( info.requestUri != null ){
       		jsonResponse = info.requestUri.indexOf(".json") > 0;
       	} 

       	final String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);
       	if( ! jsonResponse && acceptHeader != null ) {
        	jsonResponse = acceptHeader.contains(MediaType.APPLICATION_JSON_VALUE);
        }
		
		response.setStatus( status );
		
		final ModelAndView result;
		
        if (jsonResponse) {

    		response.setContentType("application/json; charset=UTF-8");

    		final  Map<String, Object>  json = new  HashMap<>();
        	json.put("code", "Error");
        	json.put("status", info.status);
        	json.put("message", info.message);
        	json.put("requestUri", info.requestUri);
        	json.put("exception", info.exception);
        	json.put("exceptionType", info.exceptionType);
    		
        	result = new ModelAndView( new MappingJackson2JsonView() );
        	result.addAllObjects(json);

        } else {
    		response.setContentType("text/html; charset=UTF-8");
    		
    		result = new ModelAndView("Common/Error");
    		ModelMap modelMap = result.getModelMap();
    		modelMap.addAttribute("statusCode", info.status);
    		modelMap.addAttribute("statusMessage", info.message);
    		modelMap.addAttribute("requestUri", info.requestUri);
        }
        
        e.printStackTrace();
        
        return result;
    }
	
	@RequestMapping("Error/AccessDenied.html")
	public String AccessDenied(ModelMap model, @RequestParam("url") String address) {
		model.addAttribute("reqAddress", address);
		return "Common/Denied";
	}
	
}
