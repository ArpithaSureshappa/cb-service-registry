package com.igot.cb.validator;

import com.igot.cb.entity.ServiceLocatorEntity;
import com.igot.cb.exceptions.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ServiceLocatorValidator {


    public void validate(ServiceLocatorEntity entity) {
        log.info("ServiceLocatorValidator::validate");
        if(entity == null){
           throw new CustomException("SERVICE_LOCATOR_CONFIG","Service locator config is mandatory", HttpStatus.BAD_REQUEST);
        }
        if(StringUtils.isBlank(entity.getUrl())){
            throw new CustomException("URL","Url is mandatory",HttpStatus.BAD_REQUEST);
        }
        if(StringUtils.isBlank(entity.getServiceCode())){
            throw new CustomException("SERVICE_CODE","Service code is missing",HttpStatus.BAD_REQUEST);
        }
        if(StringUtils.isBlank(entity.getServiceName())){
            throw new CustomException("SERVICE_NAME","Service name is missing",HttpStatus.BAD_REQUEST);
        }
        if(StringUtils.isBlank(entity.getOperationType())){
            throw new CustomException("OPERATION_TYPE","Operation type is missing",HttpStatus.BAD_REQUEST);
        }
        if(entity.getRequestMethod() == null
                || !EnumUtils.isValidEnum(ServiceLocatorEntity.RequestMethod.class,entity.getRequestMethod().name())){
            throw new CustomException("REQUEST_METHOD","Request method is mandatory",HttpStatus.BAD_REQUEST);
        }

    }
}
