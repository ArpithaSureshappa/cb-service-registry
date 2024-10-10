package com.igot.cb.service;

import com.igot.cb.dto.ServiceRequestDto;
import com.igot.cb.entity.IntegrationModel;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Service
public interface IntegrationModelService {

     Object getDetailsFromExternalService(IntegrationModel integrationModel) throws IOException;

     Object getRequestPayloadByConfigId(ServiceRequestDto requestDto,String id) throws IOException;
}
