package com.igot.cb.controller;

import com.igot.cb.dto.PaginationRequestDto;
import com.igot.cb.dto.ServiceLocatorDto;
import com.igot.cb.entity.ServiceLocatorEntity;
import com.igot.cb.service.ServiceLocatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("serviceregistry")
@Slf4j
public class ServiceLocatorController {

    @Autowired
    private ServiceLocatorService serviceLocatorService;


    @PostMapping("/config/create")
    public ServiceLocatorEntity createServiceConfig(@RequestBody ServiceLocatorEntity serviceLocatorEntity) {
        return serviceLocatorService.createOrUpdateServiceConfig(serviceLocatorEntity);
    }

    @PostMapping("/config/update")
    public ServiceLocatorEntity updateServiceConfig(@RequestBody ServiceLocatorEntity serviceLocatorEntity) {
        return serviceLocatorService.createOrUpdateServiceConfig(serviceLocatorEntity);
    }


    @DeleteMapping("/config/delete/{id}")
    public String deleteServiceConfig(@PathVariable String id) {
        serviceLocatorService.deleteServiceConfig(id);
        return "Data deleted successfully with id " + id;
    }

    @PostMapping("/config/search")
    public List<ServiceLocatorEntity> searchServiceConfig(@RequestBody ServiceLocatorDto serviceLocatorDto) {
        return serviceLocatorService.searchServiceConfig(serviceLocatorDto);
    }

    @PostMapping("/config/fetch")
    public ResponseEntity<?> getAllServiceConfig(@RequestBody PaginationRequestDto dto){
        return ResponseEntity.ok(serviceLocatorService.getAllServiceConfig(dto));
    }

    @GetMapping("/config/read/{id}")
    public ResponseEntity<?> readServiceConfig(@PathVariable String id) {
        return ResponseEntity.ok(serviceLocatorService.readServiceConfig(id));

    }

}
