package com.igot.cb.service;

import com.igot.cb.dto.PaginatedResponse;
import com.igot.cb.dto.PaginationRequestDto;
import com.igot.cb.dto.ServiceLocatorDto;
import com.igot.cb.entity.ServiceLocatorEntity;
import java.util.List;

public interface ServiceLocatorService {

  ServiceLocatorEntity createOrUpdateServiceConfig(ServiceLocatorEntity batchService);

  String deleteServiceConfig(String id);

  List<ServiceLocatorEntity> searchServiceConfig(ServiceLocatorDto serviceLocatorDto);

  PaginatedResponse getAllServiceConfig(PaginationRequestDto dto);

  ServiceLocatorEntity readServiceConfig(String id);
}
