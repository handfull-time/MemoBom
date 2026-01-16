package com.utime.memoBom.common.interceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.utime.memoBom.common.util.AppUtils;
import com.utime.memoBom.common.util.LimitStringBuilder;
import com.utime.memoBom.user.vo.UserVo;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;


/**
 * LoggerInterceptor.java 클래스를 대체할 목적으로 만들었으나 Fillter가 생각처럼 동작하지 않아 포기함. 나중에 다시 해야지. 
 * @author utime
 *
 */
@Slf4j
@Aspect
@Component
class LoggingAspect {

	private final String KEY_USER_AGENT = HttpHeaders.USER_AGENT;

	private final String line1 = "┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";
	private final String line2 = "┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";
	private final String equals = "=";
	public static final String front = "┃";
	
	private final String RequestValue = front + "Request \t: ";
	private final String ResponseValue = front + "Response \t: ";
	private final String RemoteUri = front + "Remote URI \t: ";
	private final String Method = front + "Method Type \t: ";
	private final String PackagePath = front + "Package Path \t: ";
	private final String MethodName = front + "Method Name \t: ";
	private final String RemoteAddress = front + "Remote IP \t: ";
	private final String ContentType = front + "Content Type \t: ";
	private final String UserNoHeader = front + "User Number \t: ";
	private final String UserAgent = front + "User Agent \t: ";
	private final String RequestValues = front + "Request Values \n";
	private final String ValueFront = "\t";
	private final String Cookie = front + "Header Cookie\n";
	private final String ExecutionTime = front + "ExecutionTime \t: ";
	private final String ViewName = front + "View \t\t: ";
	private final String RequestBody = front + "Request Body \n";
	private final String FilterKey = "org.springframework.validation";
	
	public static final String lineSepretor = System.lineSeparator();
	
	private final ObjectWriter objWirter;

	public LoggingAspect() {
    	final ObjectMapper objMapper = new ObjectMapper();
    	objMapper.registerModule(new JavaTimeModule()); // Java 8 날짜/시간 타입 지원 추가
    	objWirter = objMapper.writerWithDefaultPrettyPrinter();
	}

	public static void printClassHierarchy(Object obj) {
        Class<?> currentClass = obj.getClass();
        List<Class<?>> classHierarchy = new ArrayList<>();
        
        while (currentClass != null) {
            classHierarchy.add(currentClass);
            currentClass = currentClass.getSuperclass();
        }
        
        StringBuffer sb = new StringBuffer(lineSepretor);
        for (Class<?> clazz : classHierarchy) {
        	sb.append(clazz.getName()).append(lineSepretor);
        }
        log.info(sb.toString());
    }
	
	private void requestLog(final ServletRequestAttributes attributes, String startValue, String packageName, String methodName, UserVo user, List<Object> objList ) {

		final HttpServletRequest req = attributes.getRequest();
		String contentType = req.getContentType();
		if( contentType == null ) {
			contentType = "";
		}
		contentType = contentType.toLowerCase();
		
		final LimitStringBuilder paramStrBuffer = new LimitStringBuilder(2048);
		final String method = req.getMethod();
		
		paramStrBuffer.append(line1).append(lineSepretor);
		paramStrBuffer.append(RequestValue + startValue).append(lineSepretor);
		paramStrBuffer.append(PackagePath + packageName).append(lineSepretor);
		paramStrBuffer.append(MethodName + methodName).append(lineSepretor);
		paramStrBuffer.append(RemoteUri + req.getRequestURI()).append(lineSepretor);
		paramStrBuffer.append(Method + method).append(lineSepretor);
		paramStrBuffer.append(ContentType + contentType ).append(lineSepretor);
		paramStrBuffer.append(UserAgent + req.getHeader(KEY_USER_AGENT) ).append(lineSepretor);
		paramStrBuffer.append(RemoteAddress + AppUtils.getRemoteAddress(req) ).append(lineSepretor);
		if( user != null ) {
			paramStrBuffer.append(UserNoHeader + user.getUserNo() ).append(lineSepretor);
		}
		
		{
			final Cookie[] cookies = req.getCookies();
			if( cookies != null && cookies.length > 0 ){
				paramStrBuffer.append(Cookie);
				for( Cookie item : cookies ) {
					paramStrBuffer.append("\t" + item.getName() + "=" + item.getValue() + "\t" + item.getMaxAge() ).append(lineSepretor);
				} 
			}
		}

		if( req.getParameterMap().size() > 0 ){
			paramStrBuffer.append(RequestValues);
			req.getParameterMap().forEach((key, value) -> {
				
				if (key.equals("pw") || key.equals("authHint") || key.indexOf("{")==0 || key.indexOf("[")==0) {
				}else {
					paramStrBuffer.append(ValueFront);
					paramStrBuffer.append(key).append(equals);
					if( value != null ) {
						paramStrBuffer.append(String.join(",", value));
					}
					paramStrBuffer.append(lineSepretor);
				}
	        });
		}

        if( objList.size() > 0 ) {
        	paramStrBuffer.append(RequestBody);
        	for( Object obj : objList ) {
            	paramStrBuffer.append( obj.toString() ).append(lineSepretor); 
        	}
        }

		paramStrBuffer.append(line2);
		log.info(paramStrBuffer.toString());
		paramStrBuffer.clear();
	}
	
	private void responseLog(MethodSignature signature, Object proceed, Model model, String startValue, long executionTime, String packageName, String methodName) {
		final LimitStringBuilder paramStrBuffer = new LimitStringBuilder( 1024*10 );
		paramStrBuffer.append(line1).append(lineSepretor);
		paramStrBuffer.append(ResponseValue + startValue ).append(lineSepretor);
		paramStrBuffer.append(PackagePath + packageName).append(lineSepretor);
		paramStrBuffer.append(MethodName + methodName).append(lineSepretor);
		paramStrBuffer.append(ExecutionTime + String.format("%,d", executionTime) + "ms").append(lineSepretor);

        final Method methodObj = signature.getMethod();
        if (methodObj.isAnnotationPresent(ResponseBody.class) || methodObj.getDeclaringClass().isAnnotationPresent(RestController.class)) {
			paramStrBuffer.append(ValueFront);
			try {
				paramStrBuffer.append( objWirter.writeValueAsString(proceed) ).append(lineSepretor);
			} catch (Exception e) {
				paramStrBuffer.append( "Json Convert Error : " + e.getMessage() ).append(lineSepretor);
				paramStrBuffer.append( proceed ).append(lineSepretor);
			}
        	
        } else if (model != null ) {
            
            paramStrBuffer.append(ViewName + (String)proceed ).append(lineSepretor);
            final Map<String, Object> modelMap = model.asMap();
			final java.util.Set<String> keys = modelMap.keySet();
			for( String key : keys ){
				if( key.indexOf(FilterKey) > -1 )
					continue;

				paramStrBuffer.append(ValueFront);
				paramStrBuffer.append(key);
				paramStrBuffer.append(equals);
				
				final Object obj = modelMap.get(key);
				if( obj == null ) {
					paramStrBuffer.append("Is null.");
				}else {
					paramStrBuffer.append(obj.toString());
				}
				paramStrBuffer.append(lineSepretor);
			}

        } else {
            // 기타 응답
        	try {
				paramStrBuffer.append( objWirter.writeValueAsString(proceed) ).append(lineSepretor);
			} catch (Exception e) {
				paramStrBuffer.append( "Json Convert Error : " + e.getMessage() ).append(lineSepretor);
				paramStrBuffer.append( proceed ).append(lineSepretor);
			}
        }
        
		paramStrBuffer.append(line2);
		log.info(paramStrBuffer.toString());
		paramStrBuffer.clear();
	}
	
//	/**
//	 * 호출된 변수 중 변수 이름에 해당 되는 값 추출
//	 * @param list Object 목록
//	 * @param params 값을 얻을 변수 이름
//	 * @return
//	 */
//	private Properties getObjFromParameter(List<Object> list, String[] params) {
//		
//		final Properties result = new Properties();
//        
//        if( params.length < 1 || list.size() < 1 ) {
//        	return result;
//        }
//
//        for (Object obj : list) {
//            final Class<?> clazz = obj.getClass();
//
//            for (String param : params) {
//                Object value = null;
//
//                try {
//                    // 변수에서 획득
//                    final Field field = clazz.getDeclaredField(param);
//                    field.setAccessible(true);
//                    value = field.get(obj);
//                } catch (Exception ignored) {
//                    try {
//                        // 변수가 없으면 get 함수에서~
//                    	final String methodName = "get" + Character.toUpperCase(param.charAt(0)) + param.substring(1);
//                    	final Method method = clazz.getMethod(methodName);
//                        value = method.invoke(obj);
//                    } catch (Exception ignoreField) {
//                    }
//                }
//
//                // 저장: key = param, value = 마지막으로 찾은 값 (덮어씀)
//                if (value != null) {
//                    result.put(param, value.toString());
//                }
//            }
//        }
//
//        return result;
//    }
	
	/**
	 * Request 로그에 포함 되지 않아도 되는 Object들
	 */
	private final Set<Class<?>> excludedClasses = Set.of(
		    Model.class,
		    UserVo.class,
		    ModelMap.class,
		    HttpSession.class,
		    HttpServletRequest.class,
		    HttpServletResponse.class
		);
	
	/**
     * Around : 대상 “메서드” 실행 전, 후 또는 예외 발생 시에 Advice를 실행합니다.
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.utime.memoBom.*.controller.*.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {

    	final ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    	if( attributes == null ) {
    		return joinPoint.proceed();
    	}

		final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		final String packageName = signature.getDeclaringTypeName();
		// 에러 페이지는 로그에 추가 하지 않는다. 에러페이지 내부에서 로그를 출력 한다.
		if( packageName.indexOf("CustomErrorController") > 0 ) {
			return joinPoint.proceed();
		}
		
		// 요청 파라미터 처리
		final List<Object> objList = new ArrayList<>();
		UserVo user = null;
		Model model = null;
		final Object[] args = joinPoint.getArgs();
		for (Object arg : args) {
	        if (arg == null ) {
	        	continue;
	        }
	        
	        if (model == null && arg instanceof Model) {
	            model = (Model) arg;
	            continue;
	        }
	        
	        final Class<?> classValue = arg.getClass();
	        
	        if( user == null && classValue.equals(UserVo.class)) {
	        	user = (UserVo)arg;
	        	continue;
	        }
	        
	        if( !excludedClasses.contains(classValue)) {
	        	objList.add( arg );
	        }
	    }
		
		final String methodName = signature.getName();
		
    	final String startValue = UUID.randomUUID().toString();
		this.requestLog(attributes, startValue, packageName, methodName, user, objList );
		
		long executionTime;
		final Object proceed;
    	long start = System.currentTimeMillis();
        try {
            proceed = joinPoint.proceed();
            executionTime = System.currentTimeMillis() - start;
        } catch (Throwable e) {
        	executionTime = System.currentTimeMillis() - start;
        	log.error("", e);

            throw e;
        }
        
       	this.responseLog(signature, proceed, model, startValue, executionTime, packageName, methodName);
        
        return proceed;
    }
	
}
